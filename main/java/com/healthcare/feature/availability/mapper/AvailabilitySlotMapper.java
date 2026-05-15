package com.healthcare.feature.availability.mapper;

import com.healthcare.entity.AvailabilitySlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface AvailabilitySlotMapper {
    Optional<AvailabilitySlot> findById(@Param("id") Long id);

    List<AvailabilitySlot> findByDoctorIdAndSlotDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    List<AvailabilitySlot> findByDoctorIdAndSlotDateAndIsAvailableTrue(@Param("doctorId") Long doctorId,
                                                                       @Param("date") LocalDate date);

    Optional<AvailabilitySlot> findByDoctorIdAndSlotDateAndStartTimeAndIsAvailableTrue(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime);

    Optional<AvailabilitySlot> findByDoctorIdAndSlotDateAndStartTime(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime);

    boolean existsByDoctorIdAndSlotDateAndStartTime(@Param("doctorId") Long doctorId,
                                                     @Param("date") LocalDate date,
                                                     @Param("startTime") LocalTime startTime);

    List<AvailabilitySlot> findByDoctorIdAndSlotDateBetweenOrderBySlotDateAscStartTimeAsc(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    void insertAvailabilitySlot(AvailabilitySlot availabilitySlot);

    void updateAvailabilitySlot(AvailabilitySlot availabilitySlot);

    void deleteByDoctorIdAndSlotDateAndIsAvailableTrue(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    default AvailabilitySlot save(AvailabilitySlot availabilitySlot) {
        if (availabilitySlot.getId() == null) {
            insertAvailabilitySlot(availabilitySlot);
        } else {
            updateAvailabilitySlot(availabilitySlot);
        }
        return findById(availabilitySlot.getId()).orElse(availabilitySlot);
    }
}
