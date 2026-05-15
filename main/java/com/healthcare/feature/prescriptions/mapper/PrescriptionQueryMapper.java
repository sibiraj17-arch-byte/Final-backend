package com.healthcare.feature.prescriptions.mapper;

import com.healthcare.entity.Prescription;
import com.healthcare.entity.PrescriptionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PrescriptionQueryMapper {

    Optional<Prescription> findById(@Param("id") Long id);

    Optional<Prescription> findByAppointmentId(@Param("appointmentId") Long appointmentId);

    List<Prescription> findByPatientId(@Param("patientId") Long patientId);

    List<Prescription> findByDoctorId(@Param("doctorId") Long doctorId);

    List<Prescription> findAll();

    void insertPrescription(Prescription prescription);

    void updatePrescription(Prescription prescription);

    void deleteItemsByPrescriptionId(@Param("prescriptionId") Long prescriptionId);

    void insertPrescriptionItem(@Param("prescriptionId") Long prescriptionId,
                                @Param("item") PrescriptionItem item);

    default Prescription save(Prescription prescription) {
        if (prescription.getId() == null) {
            insertPrescription(prescription);
        } else {
            updatePrescription(prescription);
            deleteItemsByPrescriptionId(prescription.getId());
        } 

        if (prescription.getItems() != null) {
            for (PrescriptionItem item : prescription.getItems()) {
                insertPrescriptionItem(prescription.getId(), item);
            }
        }

        return findById(prescription.getId()).orElse(prescription);
    }
}
