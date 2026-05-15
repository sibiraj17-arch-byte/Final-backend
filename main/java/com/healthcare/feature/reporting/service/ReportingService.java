package com.healthcare.feature.reporting.service;

import com.healthcare.entity.Doctor;
import com.healthcare.entity.Patient;
import com.healthcare.entity.User;
import com.healthcare.enums.PaymentStatus;
import com.healthcare.enums.UserRole;
import com.healthcare.exception.ResourceNotFoundException;
import com.healthcare.exception.UnauthorizedException;
import com.healthcare.feature.auth.mapper.UserMapper;
import com.healthcare.feature.doctors.mapper.DoctorMapper;
import com.healthcare.feature.patients.mapper.PatientMapper;
import com.healthcare.feature.reporting.dto.AppointmentReportItemDTO;
import com.healthcare.feature.reporting.dto.AppointmentsReportResponseDTO;
import com.healthcare.feature.reporting.dto.ReportFilterRequestDTO;
import com.healthcare.feature.reporting.dto.RevenueByDoctorDTO;
import com.healthcare.feature.reporting.dto.RevenueByPatientDTO;
import com.healthcare.feature.reporting.dto.RevenueReportItemDTO;
import com.healthcare.feature.reporting.dto.RevenueReportResponseDTO;
import com.healthcare.feature.reporting.mapper.ReportingQueryMapper;
import com.healthcare.security.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class ReportingService {

    private final ReportingQueryMapper reportingQueryMapper;
    private final PatientMapper patientRepository;
    private final DoctorMapper doctorRepository;
    private final UserMapper userRepository;
    private final AppointmentsExcelExportService appointmentsExcelExportService;

    public ReportingService(
            ReportingQueryMapper reportingQueryMapper,
            PatientMapper patientRepository,
            DoctorMapper doctorRepository,
            UserMapper userRepository,
            AppointmentsExcelExportService appointmentsExcelExportService
    ) {
        this.reportingQueryMapper = reportingQueryMapper;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.appointmentsExcelExportService = appointmentsExcelExportService;
    }

    public AppointmentsReportResponseDTO getAppointmentsReport(ReportFilterRequestDTO request) {
        User currentUser = getCurrentUser();
        DateWindow dateWindow = buildDateWindow(request);
        ScopeContext scope = resolveScope(currentUser);
        List<AppointmentReportItemDTO> resultItems = findAppointmentsForReport(currentUser, scope, dateWindow);

        AppointmentsReportResponseDTO response = new AppointmentsReportResponseDTO();
        response.setScope(currentUser.getRole().name());
        response.setFilterType(normalizeFilterType(request));
        response.setFilterLabel(dateWindow.label);
        response.setAppointments(resultItems);
        response.setTotalAppointments(resultItems.size());
        return response;
    }

    public byte[] exportAppointmentsReportExcel(ReportFilterRequestDTO request) {
        User currentUser = getCurrentUser();
        DateWindow dateWindow = buildDateWindow(request);
        ScopeContext scope = resolveScope(currentUser);
        List<AppointmentReportItemDTO> appointments = findAppointmentsForReport(currentUser, scope, dateWindow);
        return appointmentsExcelExportService.export(
                currentUser.getRole().name(),
                normalizeFilterType(request),
                dateWindow.label,
                appointments
        );
    }

    public String buildAppointmentsExportFileName(ReportFilterRequestDTO request) {
        DateWindow dateWindow = buildDateWindow(request);
        String safeLabel = dateWindow.label == null
                ? "report"
                : dateWindow.label.toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");

        if (safeLabel.isBlank()) {
            safeLabel = "report";
        }

        return "appointments-report-" + safeLabel + ".xlsx";
    }

    public RevenueReportResponseDTO getRevenueReport(ReportFilterRequestDTO request) {
        User currentUser = getCurrentUser();
        DateWindow dateWindow = buildDateWindow(request);
        ScopeContext scope = resolveScope(currentUser);
        LocalDateTime startDateTime = dateWindow.startDate.atStartOfDay();
        LocalDateTime endDateTime = dateWindow.endDate.atTime(LocalTime.MAX);

        List<RevenueReportItemDTO> paymentItems = reportingQueryMapper.findPaymentsForReport(
                currentUser.getRole().name(),
                scope.doctorId(),
                scope.patientId(),
                startDateTime,
                endDateTime
        );
        List<RevenueByPatientDTO> patientBreakdown = reportingQueryMapper.findRevenueByPatientForReport(
                currentUser.getRole().name(),
                scope.doctorId(),
                scope.patientId(),
                startDateTime,
                endDateTime
        );
        List<RevenueByDoctorDTO> doctorBreakdown = reportingQueryMapper.findRevenueByDoctorForReport(
                currentUser.getRole().name(),
                scope.doctorId(),
                scope.patientId(),
                startDateTime,
                endDateTime
        );
        long completedPayments = paymentItems.stream()
                .filter(item -> PaymentStatus.COMPLETED.name().equalsIgnoreCase(item.getPaymentStatus()))
                .count();

        RevenueReportResponseDTO response = new RevenueReportResponseDTO();
        response.setScope(currentUser.getRole().name());
        response.setFilterType(normalizeFilterType(request));
        response.setFilterLabel(dateWindow.label);
        response.setPayments(paymentItems);
        response.setTotalPayments(paymentItems.size());
        response.setCompletedPayments((int) completedPayments);
        response.setTotalRevenue(sumPatientRevenue(patientBreakdown));
        response.setPatientBreakdown(patientBreakdown);
        response.setDoctorBreakdown(doctorBreakdown);
        response.setAverageRevenuePerPatient(calculateAveragePerPatient(patientBreakdown));
        return response;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal principal)) {
            throw new UnauthorizedException("Not authenticated");
        }
        return userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getUserId()));
    }

    private ScopeContext resolveScope(User currentUser) {
        if (currentUser.getRole() == UserRole.ADMIN) {
            return new ScopeContext(null, null);
        }
        if (currentUser.getRole() == UserRole.DOCTOR) {
            Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", currentUser.getId()));
            return new ScopeContext(doctor.getId(), null);
        }

        Patient patient = patientRepository.findCanonicalByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "userId", currentUser.getId()));
        return new ScopeContext(null, patient.getId());
    }

    private double sumPatientRevenue(List<RevenueByPatientDTO> patientBreakdown) {
        double total = 0;
        for (RevenueByPatientDTO item : patientBreakdown) {
            total += item.getTotalRevenue();
        }
        return total;
    }

    private double calculateAveragePerPatient(List<RevenueByPatientDTO> patientBreakdown) {
        if (patientBreakdown.isEmpty()) {
            return 0;
        }

        double total = 0;
        for (RevenueByPatientDTO item : patientBreakdown) {
            total += item.getTotalRevenue();
        }
        return total / patientBreakdown.size();
    }

    private List<AppointmentReportItemDTO> findAppointmentsForReport(User currentUser, ScopeContext scope, DateWindow dateWindow) {
        return reportingQueryMapper.findAppointmentsForReport(
                currentUser.getRole().name(),
                scope.doctorId(),
                scope.patientId(),
                dateWindow.startDate,
                dateWindow.endDate
        );
    }

    private String normalizeFilterType(ReportFilterRequestDTO request) {
        String filterType = request != null ? request.getFilterType() : null;
        return filterType == null || filterType.isBlank() ? "dateRange" : filterType;
    }

    private DateWindow buildDateWindow(ReportFilterRequestDTO request) {
        String filterType = normalizeFilterType(request);
        if ("date".equalsIgnoreCase(filterType)) {
            LocalDate selectedDate = parseDateValue(request.getDate(), "date", LocalDate.now());
            return new DateWindow(selectedDate, selectedDate, selectedDate.toString());
        }

        if ("week".equalsIgnoreCase(filterType)) {
            String weekValue = request.getWeek();
            LocalDate startOfWeek = parseWeek(weekValue);
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            return new DateWindow(startOfWeek, endOfWeek, weekValue);
        }

        if ("month".equalsIgnoreCase(filterType)) {
            YearMonth selectedMonth = parseMonth(request.getMonth());
            return new DateWindow(selectedMonth.atDay(1), selectedMonth.atEndOfMonth(), formatMonthLabel(selectedMonth));
        }

        if ("year".equalsIgnoreCase(filterType)) {
            Year selectedYear = parseYear(request.getYear());
            return new DateWindow(selectedYear.atDay(1), selectedYear.atMonth(12).atEndOfMonth(), String.valueOf(selectedYear.getValue()));
        }

        LocalDate startDate = parseDateValue(request.getStartDate(), "startDate", LocalDate.now().minusDays(30));
        LocalDate endDate = parseDateValue(request.getEndDate(), "endDate", LocalDate.now());
        if (endDate.isBefore(startDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }
        return new DateWindow(startDate, endDate, startDate + " to " + endDate);
    }

    private LocalDate parseWeek(String weekValue) {
        if (weekValue == null || weekValue.isBlank()) {
            return LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        }
        try {
            return LocalDate.parse(weekValue + "-1", DateTimeFormatter.ISO_WEEK_DATE);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Week must be in YYYY-Www format.");
        }
    }

    private YearMonth parseMonth(String monthValue) {
        if (monthValue == null || monthValue.isBlank()) {
            return YearMonth.now();
        }
        if (monthValue.contains("-")) {
            return YearMonth.parse(monthValue);
        }

        String normalized = monthValue.trim();
        try {
            int monthNumber = java.time.Month.valueOf(normalized.toUpperCase(Locale.ENGLISH)).getValue();
            return YearMonth.of(LocalDate.now().getYear(), monthNumber);
        } catch (IllegalArgumentException ignored) {
            try {
                java.time.Month parsedMonth = java.time.Month.from(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH).parse(normalized));
                return YearMonth.of(LocalDate.now().getYear(), parsedMonth);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Month must be in YYYY-MM or month name format.");
            }
        }
    }

    private Year parseYear(String yearValue) {
        if (yearValue == null || yearValue.isBlank()) {
            return Year.now();
        }

        try {
            return Year.parse(yearValue.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Year must be in YYYY format.");
        }
    }

    private LocalDate parseDateValue(String value, String fieldName, LocalDate defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid date in YYYY-MM-DD format.");
        }
    }

    private String formatMonthLabel(YearMonth month) {
        return month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + month.getYear();
    }

    private static class DateWindow {
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final String label;

        private DateWindow(LocalDate startDate, LocalDate endDate, String label) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.label = label;
        }
    }

    private record ScopeContext(Long doctorId, Long patientId) {
    }
}
