package com.healthcare.feature.reporting.dto;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsReportResponseDTO {
    private String scope;
    private String filterType;
    private String filterLabel;
    private long totalAppointments;
    private List<AppointmentReportItemDTO> appointments = new ArrayList<>();

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

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public List<AppointmentReportItemDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentReportItemDTO> appointments) {
        this.appointments = appointments;
    }
}
