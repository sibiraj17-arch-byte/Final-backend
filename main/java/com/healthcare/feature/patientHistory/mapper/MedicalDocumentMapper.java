package com.healthcare.feature.patientHistory.mapper;

import com.healthcare.entity.MedicalDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MedicalDocumentMapper {
    Optional<MedicalDocument> findById(@Param("id") Long id);

    List<MedicalDocument> findByMedicalHistoryPatientId(@Param("patientId") Long patientId);

    List<MedicalDocument> findByMedicalHistoryId(@Param("medicalHistoryId") Long medicalHistoryId);

    void insertMedicalDocument(MedicalDocument medicalDocument);

    void updateMedicalDocument(MedicalDocument medicalDocument);

    default MedicalDocument save(MedicalDocument medicalDocument) {
        if (medicalDocument.getId() == null) {
            insertMedicalDocument(medicalDocument);
        } else {
            updateMedicalDocument(medicalDocument);
        }
        return findById(medicalDocument.getId()).orElse(medicalDocument);
    }
}
