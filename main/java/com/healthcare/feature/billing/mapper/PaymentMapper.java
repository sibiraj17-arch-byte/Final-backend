package com.healthcare.feature.billing.mapper;

import com.healthcare.entity.Payment;
import com.healthcare.enums.PaymentStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface PaymentMapper {
    Optional<Payment> findById(@Param("id") Long id);

    Optional<Payment> findByAppointmentId(@Param("appointmentId") Long appointmentId);

    Optional<Payment> findByRazorpayOrderId(@Param("razorpayOrderId") String razorpayOrderId);

    List<Payment> findByPatientId(@Param("patientId") Long patientId);

    List<Payment> findByStatus(@Param("status") PaymentStatus status);

    List<Payment> findAll();

    boolean existsByAppointmentId(@Param("appointmentId") Long appointmentId);

    Double sumCompletedPaymentsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    void insertPayment(Payment payment);

    void updatePayment(Payment payment);

    default Payment save(Payment payment) {
        if (payment.getId() == null) {
            insertPayment(payment);
        } else {
            updatePayment(payment);
        }
        return findById(payment.getId()).orElse(payment);
    }
}
