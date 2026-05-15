package com.healthcare.feature.reporting.controller;

import com.healthcare.feature.reporting.dto.AppointmentsReportResponseDTO;
import com.healthcare.feature.reporting.dto.ReportFilterRequestDTO;
import com.healthcare.feature.reporting.dto.RevenueReportResponseDTO;
import com.healthcare.feature.reporting.service.ReportingService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentsReportResponseDTO> getAppointmentsReport(@RequestBody ReportFilterRequestDTO request) {
        return ResponseEntity.ok(reportingService.getAppointmentsReport(request));
    }

    @PostMapping("/appointments/export")
    public ResponseEntity<ByteArrayResource> exportAppointmentsReport(@RequestBody ReportFilterRequestDTO request) {
        String fileName = reportingService.buildAppointmentsExportFileName(request);
        byte[] fileBytes = reportingService.exportAppointmentsReportExcel(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(fileBytes.length)
                .body(new ByteArrayResource(fileBytes));
    }

    @PostMapping("/revenue")
    public ResponseEntity<RevenueReportResponseDTO> getRevenueReport(@RequestBody ReportFilterRequestDTO request) {
        return ResponseEntity.ok(reportingService.getRevenueReport(request));
    }
}
