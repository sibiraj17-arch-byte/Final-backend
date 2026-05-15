package com.healthcare.feature.myAppointments.mapper;

import com.healthcare.entity.Appointment;
import com.healthcare.enums.AppointmentStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface MyAppointmentsQueryMapper {

    Optional<Appointment> findById(@Param("id") Long id);

    List<Appointment> findByPatientId(@Param("patientId") Long patientId);

    List<Appointment> findByPatientIdAndStatus(@Param("patientId") Long patientId,
                                               @Param("status") AppointmentStatus status);

    List<Appointment> findByPatientIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscStartTimeAsc(
            @Param("patientId") Long patientId,
            @Param("today") LocalDate today
    );
}
