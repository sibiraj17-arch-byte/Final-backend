package com.healthcare.feature.reporting.dto;

import java.util.ArrayList;
import java.util.List;

public class RevenueReportResponseDTO {
    private String scope;
    private String filterType;
    private String filterLabel;
    private long totalPayments;
    private long completedPayments;
    private double totalRevenue;
    private double averageRevenuePerPatient;
    private List<RevenueReportItemDTO> payments = new ArrayList<>();
    private List<RevenueByPatientDTO> patientBreakdown = new ArrayList<>();
    private List<RevenueByDoctorDTO> doctorBreakdown = new ArrayList<>();

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterLabel() {
        return filterLabel;
    }

    public void setFilterLabel(String filterLabel) {
        this.filterLabel = filterLabel;
    }

    public long getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(long totalPayments) {
        this.totalPayments = totalPayments;
    }

    public long getCompletedPayments() {
        return completedPayments;
    }

    public void setCompletedPayments(long completedPayments) {
        this.completedPayments = completedPayments;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getAverageRevenuePerPatient() {
        return averageRevenuePerPatient;
    }

    public void setAverageRevenuePerPatient(double averageRevenuePerPatient) {
        this.averageRevenuePerPatient = averageRevenuePerPatient;
    }

    public List<RevenueReportItemDTO> getPayments() {
        return payments;
    }

    public void setPayments(List<RevenueReportItemDTO> payments) {
        this.payments = payments;
    }

    public List<RevenueByPatientDTO> getPatientBreakdown() {
        return patientBreakdown;
    }

    public void setPatientBreakdown(List<RevenueByPatientDTO> patientBreakdown) {
        this.patientBreakdown = patientBreakdown;
    }

    public List<RevenueByDoctorDTO> getDoctorBreakdown() {
        return doctorBreakdown;
    }

    public void setDoctorBreakdown(List<RevenueByDoctorDTO> doctorBreakdown) {
        this.doctorBreakdown = doctorBreakdown;
    }
}
