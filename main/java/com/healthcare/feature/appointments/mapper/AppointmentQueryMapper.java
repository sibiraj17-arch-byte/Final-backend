package com.healthcare.feature.appointments.mapper;

import com.healthcare.entity.Appointment;
import com.healthcare.enums.AppointmentStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface AppointmentQueryMapper {

    Optional<Appointment> findById(@Param("id") Long id);

    Optional<Appointment> findByCode(@Param("code") String code);

    List<Appointment> findAll();

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientIdAndStatus(@Param("patientId") Long patientId,
                                               @Param("status") AppointmentStatus status);

    List<Appointment> findByDoctorIdAndStatus(@Param("doctorId") Long doctorId,
                                              @Param("status") AppointmentStatus status);

    List<Appointment> findByPatientIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscStartTimeAsc(
            @Param("patientId") Long patientId,
            @Param("today") LocalDate today
    );

    List<Appointment> findByDoctorIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAscStartTimeAsc(
            @Param("doctorId") Long doctorId,
            @Param("today") LocalDate today
    );

    List<Appointment> findByDoctorIdAndAppointmentDate(@Param("doctorId") Long doctorId,
                                                       @Param("date") LocalDate date);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAscStartTimeAsc(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<Appointment> findByDoctorIdAndAppointmentDateBetweenOrderByAppointmentDateAscStartTimeAsc(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<Appointment> findByDoctorIdAndAppointmentDateOrderByStartTimeAsc(@Param("doctorId") Long doctorId,
                                                                           @Param("today") LocalDate today);

    List<Appointment> findByAppointmentDateBetween(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    List<Appointment> findByPatientIdAndAppointmentDateBetween(@Param("patientId") Long patientId,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate);

    List<Appointment> findByDoctorIdAndAppointmentDateBetween(@Param("doctorId") Long doctorId,
                                                              @Param("startDate") LocalDate startDate,
                                                              @Param("endDate") LocalDate endDate);

    boolean existsByDoctorIdAndAppointmentDateAndStartTimeAndStatusNot(@Param("doctorId") Long doctorId,
                                                                        @Param("date") LocalDate date,
                                                                        @Param("startTime") LocalTime startTime,
                                                                        @Param("status") AppointmentStatus status);

    long countByStatus(@Param("status") AppointmentStatus status);

    long countByAppointmentDate(@Param("today") LocalDate today);

    void insertAppointment(Appointment appointment);

    void updateAppointment(Appointment appointment);

    default Appointment save(Appointment appointment) {
        if (appointment.getId() == null) {
            insertAppointment(appointment);
        } else {
            updateAppointment(appointment);
        }
        return findById(appointment.getId()).orElse(appointment);
    }
}
