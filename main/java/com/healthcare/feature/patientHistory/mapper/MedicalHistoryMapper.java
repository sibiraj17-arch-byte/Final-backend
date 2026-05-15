package com.healthcare.feature.patientHistory.mapper;

import com.healthcare.entity.MedicalHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MedicalHistoryMapper {
    Optional<MedicalHistory> findById(@Param("id") Long id);

    List<MedicalHistory> findAll();

    List<MedicalHistory> findByPatientId(@Param("patientId") Long patientId);

    List<MedicalHistory> findByPatientIdAndCreatedAtBetween(@Param("patientId") Long patientId,
                                                            @Param("startDate") LocalDateTime startDate,
                                                            @Param("endDate") LocalDateTime endDate);

    boolean existsByIdAndPatientId(@Param("id") Long id, @Param("patientId") Long patientId);

    List<MedicalHistory> findByPatientIdAndConditionContainingIgnoreCase(@Param("patientId") Long patientId,
                                                                         @Param("keyword") String keyword);

    void insertMedicalHistory(MedicalHistory medicalHistory);

    void updateMedicalHistory(MedicalHistory medicalHistory);

    void deleteById(@Param("id") Long id);

    default MedicalHistory save(MedicalHistory medicalHistory) {
        if (medicalHistory.getId() == null) {
            insertMedicalHistory(medicalHistory);
        } else {
            updateMedicalHistory(medicalHistory);
        }
        return findById(medicalHistory.getId()).orElse(medicalHistory);
    }
}
