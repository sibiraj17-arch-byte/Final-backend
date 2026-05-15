package com.healthcare.feature.reviews.mapper;

import com.healthcare.entity.Review;
import com.healthcare.feature.reviews.dto.RatingStatsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReviewMapper {
    Optional<Review> findById(@Param("id") Long id);

    Optional<Review> findByAppointmentId(@Param("appointmentId") Long appointmentId);

    List<Review> findByDoctorId(@Param("doctorId") Long doctorId);

    List<Review> findByPatientId(@Param("patientId") Long patientId);

    boolean existsByAppointmentId(@Param("appointmentId") Long appointmentId);

    Double getAverageRating(@Param("profId") Long profId);

    long countByDoctorId(@Param("doctorId") Long doctorId);

    long count();

    RatingStatsDTO getRatingStats(@Param("profId") Long profId);

    void insertReview(Review review);

    void updateReview(Review review);

    default Review save(Review review) {
        if (review.getId() == null) {
            insertReview(review);
        } else {
            updateReview(review);
        }
        return findById(review.getId()).orElse(review);
    }
}
