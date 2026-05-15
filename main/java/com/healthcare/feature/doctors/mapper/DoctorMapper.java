package com.healthcare.feature.doctors.mapper;

import com.healthcare.entity.Doctor;
import com.healthcare.enums.AvailabilityStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DoctorMapper {
    Optional<Doctor> findById(@Param("id") Long id);

    Optional<Doctor> findByUserId(@Param("userId") Long userId);

    Optional<Doctor> findByUserMobileNumber(@Param("mobileNumber") String mobileNumber);

    List<Doctor> findBySpecializationId(@Param("specializationId") Long specializationId);

    List<Doctor> findByAvailabilityStatus(@Param("status") AvailabilityStatus status);

    List<Doctor> findByIsVerifiedTrue();

    List<Doctor> findByAvailabilityStatusAndIsVerifiedTrue(@Param("availabilityStatus") AvailabilityStatus availabilityStatus);

    List<Doctor> findByAvailabilityStatusAndIsVerifiedTrueAndSpecializationId(
            @Param("availabilityStatus") AvailabilityStatus availabilityStatus,
            @Param("specializationId") Long specializationId);

    List<Doctor> findByUserNameContainingIgnoreCase(@Param("name") String name);

    List<Doctor> findAll();

    boolean existsById(@Param("id") Long id);

    void insertDoctor(Doctor doctor);

    void updateDoctor(Doctor doctor);

    void deleteById(@Param("id") Long id);

    default Doctor save(Doctor doctor) {
        if (doctor.getId() == null) {
            insertDoctor(doctor);
        } else {
            updateDoctor(doctor);
        }
        return findById(doctor.getId()).orElse(doctor);
    }

    default void delete(Doctor doctor) {
        if (doctor != null && doctor.getId() != null) {
            deleteById(doctor.getId());
        }
    }
}
