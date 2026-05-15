package com.healthcare.feature.reporting.dto;

public class RevenueByPatientDTO {
    private Long patientId;
    private String patientName;
    private double totalRevenue;
    private long paymentCount;
    private double averageRevenue;

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(long paymentCount) {
        this.paymentCount = paymentCount;
    }

    public double getAverageRevenue() {
        return averageRevenue;
    }

    public void setAverageRevenue(double averageRevenue) {
        this.averageRevenue = averageRevenue;
    }
}
