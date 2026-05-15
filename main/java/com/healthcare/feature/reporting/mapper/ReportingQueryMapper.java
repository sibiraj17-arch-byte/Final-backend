package com.healthcare.feature.reporting.mapper;

import com.healthcare.feature.reporting.dto.AppointmentReportItemDTO;
import com.healthcare.feature.reporting.dto.RevenueByDoctorDTO;
import com.healthcare.feature.reporting.dto.RevenueByPatientDTO;
import com.healthcare.feature.reporting.dto.RevenueReportItemDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportingQueryMapper {

	List<AppointmentReportItemDTO> findAppointmentsForReport(@Param("role") String role,
			@Param("doctorId") Long doctorId, @Param("patientId") Long patientId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	List<RevenueReportItemDTO> findPaymentsForReport(@Param("role") String role, @Param("doctorId") Long doctorId,
			@Param("patientId") Long patientId, @Param("startDateTime") LocalDateTime startDateTime,
			@Param("endDateTime") LocalDateTime endDateTime);

	List<RevenueByPatientDTO> findRevenueByPatientForReport(@Param("role") String role,
			@Param("doctorId") Long doctorId, @Param("patientId") Long patientId,
			@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

	List<RevenueByDoctorDTO> findRevenueByDoctorForReport(@Param("role") String role, @Param("doctorId") Long doctorId,
			@Param("patientId") Long patientId, @Param("startDateTime") LocalDateTime startDateTime,
			@Param("endDateTime") LocalDateTime endDateTime);
}
