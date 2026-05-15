package com.healthcare.feature.patients.mapper;

import com.healthcare.entity.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PatientMapper {
    Optional<Patient> findById(@Param("id") Long id);

    Optional<Patient> findByUserId(@Param("userId") Long userId);

    Optional<Patient> findByUserMobileNumber(@Param("mobileNumber") String mobileNumber);

    List<Patient> findAllByUserIdOrderByIdAsc(@Param("userId") Long userId);

    List<Patient> findAll();

    boolean existsById(@Param("id") Long id);

    void insertPatient(Patient patient);

    void updatePatient(Patient patient);

    default Optional<Patient> findCanonicalByUserId(Long userId) {
        List<Patient> matches = findAllByUserIdOrderByIdAsc(userId);
        return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
    }

    default Patient save(Patient patient) {
        if (patient.getId() == null) {
            insertPatient(patient);
        } else {
            updatePatient(patient);
        }
        return findById(patient.getId()).orElse(patient);
    }
}
