package com.healthcare.feature.patientHistory.service;

import com.healthcare.entity.*;
import com.healthcare.exception.ResourceNotFoundException;
import com.healthcare.feature.patientHistory.dto.*;
import com.healthcare.feature.patientHistory.mapper.MedicalHistoryMapper;
import com.healthcare.feature.patientHistory.mapper.MedicalDocumentMapper;
import com.healthcare.feature.patients.mapper.PatientMapper;
import com.healthcare.feature.appointments.mapper.AppointmentQueryMapper;
import com.healthcare.feature.prescriptions.mapper.PrescriptionQueryMapper;
import com.healthcare.feature.medicalReports.mapper.MedicalReportMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PatientHistoryService {

    private final MedicalHistoryMapper medicalHistoryRepository;
    private final MedicalDocumentMapper medicalDocumentRepository;
    private final PatientMapper patientRepository;
    private final AppointmentQueryMapper appointmentRepository;
    private final PrescriptionQueryMapper prescriptionRepository;
    private final MedicalReportMapper medicalReportRepository;

    public PatientHistoryService(MedicalHistoryMapper medicalHistoryRepository,
                                  MedicalDocumentMapper medicalDocumentRepository,
                                  PatientMapper patientRepository,
                                  AppointmentQueryMapper appointmentRepository,
                                  PrescriptionQueryMapper prescriptionRepository,
                                  MedicalReportMapper medicalReportRepository) {
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.medicalDocumentRepository = medicalDocumentRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.medicalReportRepository = medicalReportRepository;
    }

    public List<MedicalHistoryResponseDTO> getByPatient(Long patientId) {
        validatePatientExists(patientId);
        return medicalHistoryRepository.findByPatientId(patientId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<MedicalHistoryResponseDTO> getByPatientAndDateRange(Long patientId, LocalDateTime start, LocalDateTime end) {
        validatePatientExists(patientId);
        validateDateRange(start, end);

        return medicalHistoryRepository.findByPatientIdAndCreatedAtBetween(patientId, start, end).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public MedicalSummaryDTO getSummary(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        MedicalSummaryDTO summary = new MedicalSummaryDTO();
        summary.setPatientId(patient.getId());
        summary.setPatientName(patient.getUser().getName());

        List<MedicalHistory> histories = medicalHistoryRepository.findByPatientId(patientId);
        summary.setTotalConditions(histories.size());
        summary.setActiveConditions(histories.stream().filter(h -> "Active".equalsIgnoreCase(h.getStatus())).count());
        summary.setConditions(histories.stream().map(this::toDTO).collect(Collectors.toList()));

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        summary.setTotalAppointments(appointments.size());
        summary.setCompletedAppointments(appointments.stream()
                .filter(a -> a.getStatus() == com.healthcare.enums.AppointmentStatus.COMPLETED).count());

        summary.setTotalPrescriptions(prescriptionRepository.findByPatientId(patientId).size());
        summary.setTotalReports(medicalReportRepository.findByPatientId(patientId).size());

        return summary;
    }

    @Transactional
    public MedicalHistoryResponseDTO create(Long patientId, MedicalHistoryRequestDTO request) {
        Patient patient = getPatientOrThrow(patientId);

        MedicalHistory history = new MedicalHistory();
        history.setPatient(patient);
        history.setCondition(request.getCondition());
        history.setDiagnosis(request.getDiagnosis());
        history.setDiagnosisDate(request.getDiagnosisDate());
        history.setStatus(request.getStatus());
        history.setNotes(request.getNotes());
        history.setTreatment(request.getTreatment());

        return toDTO(medicalHistoryRepository.save(history));
    }

    @Transactional
    public MedicalHistoryResponseDTO update(Long id, MedicalHistoryRequestDTO request) {
        MedicalHistory history = medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalHistory", "id", id));

        history.setCondition(request.getCondition());
        history.setDiagnosis(request.getDiagnosis());
        history.setDiagnosisDate(request.getDiagnosisDate());
        history.setStatus(request.getStatus());
        history.setNotes(request.getNotes());
        history.setTreatment(request.getTreatment());

        return toDTO(medicalHistoryRepository.save(history));
    }

    @Transactional
    public void delete(Long id) {
        medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalHistory", "id", id));
        medicalHistoryRepository.deleteById(id);
    }

    @Transactional
    public MedicalDocument uploadDocument(Long patientId, Long historyId, DocumentUploadDTO request) {
        validatePatientExists(patientId);

        MedicalHistory history;
        if (historyId != null) {
            history = medicalHistoryRepository.findById(historyId)
                    .orElseThrow(() -> new ResourceNotFoundException("MedicalHistory", "id", historyId));
            if (!patientId.equals(history.getPatient().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical history does not belong to the patient");
            }
        } else {
            List<MedicalHistory> histories = medicalHistoryRepository.findByPatientId(patientId);
            if (histories.isEmpty()) {
                throw new ResourceNotFoundException("MedicalHistory", "patientId", patientId);
            }
            history = histories.get(0);
        }

        MedicalDocument doc = new MedicalDocument();
        doc.setMedicalHistory(history);
        doc.setDocumentName(request.getDocumentName());
        doc.setDocumentType(request.getDocumentType());
        doc.setFileUrl(request.getFileUrl());
        doc.setDescription(request.getDescription());

        return medicalDocumentRepository.save(doc);
    }

    public List<MedicalDocument> getDocuments(Long patientId) {
        validatePatientExists(patientId);
        return medicalDocumentRepository.findByMedicalHistoryPatientId(patientId);
    }

    public String downloadDocument(Long documentId) {
        MedicalDocument doc = medicalDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalDocument", "id", documentId));
        return "Download URL: " + (doc.getFileUrl() != null ? doc.getFileUrl() : "/documents/" + documentId);
    }

    private MedicalHistoryResponseDTO toDTO(MedicalHistory mh) {
        MedicalHistoryResponseDTO dto = new MedicalHistoryResponseDTO();
        dto.setId(mh.getId());
        dto.setPatientId(mh.getPatient().getId());
        dto.setCondition(mh.getCondition());
        dto.setDiagnosis(mh.getDiagnosis());
        dto.setDiagnosisDate(mh.getDiagnosisDate());
        dto.setStatus(mh.getStatus());
        dto.setNotes(mh.getNotes());
        dto.setTreatment(mh.getTreatment());
        dto.setCreatedAt(mh.getCreatedAt());
        return dto;
    }

    private Patient getPatientOrThrow(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));
    }

    private void validatePatientExists(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }
    }

    private void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date and end date are required");
        }
        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before or equal to end date");
        }
    }
}
