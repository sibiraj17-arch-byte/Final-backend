package com.healthcare.feature.medicalReports.mapper;

import com.healthcare.entity.MedicalReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MedicalReportMapper {
    Optional<MedicalReport> findById(@Param("id") Long id);

    List<MedicalReport> findByPatientId(@Param("patientId") Long patientId);

    List<MedicalReport> findByPatientIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            @Param("patientId") Long patientId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    void insertMedicalReport(MedicalReport medicalReport);

    void updateMedicalReport(MedicalReport medicalReport);

    default MedicalReport save(MedicalReport medicalReport) {
        if (medicalReport.getId() == null) {
            insertMedicalReport(medicalReport);
        } else {
            updateMedicalReport(medicalReport);
        }
        return findById(medicalReport.getId()).orElse(medicalReport);
    }
}
