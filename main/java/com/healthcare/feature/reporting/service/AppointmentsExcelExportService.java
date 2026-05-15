package com.healthcare.feature.reporting.service;

import com.healthcare.feature.reporting.dto.AppointmentReportItemDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentsExcelExportService {

    public byte[] export(String scope, String filterType, String filterLabel, List<AppointmentReportItemDTO> appointments) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Appointments Report");
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);

            int rowIndex = 0;
            rowIndex = writeMetadataRow(sheet, rowIndex, "Scope", scope, titleStyle);
            rowIndex = writeMetadataRow(sheet, rowIndex, "Filter Type", filterType, titleStyle);
            rowIndex = writeMetadataRow(sheet, rowIndex, "Filter Label", filterLabel, titleStyle);
            rowIndex = writeMetadataRow(sheet, rowIndex, "Total Appointments", String.valueOf(appointments.size()), titleStyle);
            rowIndex++;

            Row headerRow = sheet.createRow(rowIndex++);
            String[] headers = {
                    "Appointment ID", "Appointment Code", "Patient ID", "Patient Name",
                    "Doctor ID", "Doctor Name", "Specialization", "Appointment Date",
                    "Start Time", "End Time", "Status", "Consultation Fee"
            };
            for (int columnIndex = 0; columnIndex < headers.length; columnIndex++) {
                Cell cell = headerRow.createCell(columnIndex);
                cell.setCellValue(headers[columnIndex]);
                cell.setCellStyle(headerStyle);
            }

            for (AppointmentReportItemDTO appointment : appointments) {
                Row row = sheet.createRow(rowIndex++);
                int columnIndex = 0;
                row.createCell(columnIndex++).setCellValue(getLongValue(appointment.getAppointmentId()));
                row.createCell(columnIndex++).setCellValue(getStringValue(appointment.getAppointmentCode()));
                row.createCell(columnIndex++).setCellValue(getLongValue(appointment.getPatientId()));
                row.createCell(columnIndex++).setCellValue(getStringValue(appointment.getPatientName()));
                row.createCell(columnIndex++).setCellValue(getLongValue(appointment.getDoctorId()));
                row.createCell(columnIndex++).setCellValue(getStringValue(appointment.getDoctorName()));
                row.createCell(columnIndex++).setCellValue(getStringValue(appointment.getSpecialization()));
                row.createCell(columnIndex++).setCellValue(getStringValue(formatLocalDate(appointment.getAppointmentDate())));
                row.createCell(columnIndex++).setCellValue(getStringValue(formatLocalTime(appointment.getStartTime())));
                row.createCell(columnIndex++).setCellValue(getStringValue(formatLocalTime(appointment.getEndTime())));
                row.createCell(columnIndex++).setCellValue(getStringValue(appointment.getStatus()));
                row.createCell(columnIndex).setCellValue(getDoubleValue(appointment.getConsultationFee()));
            }

            for (int columnIndex = 0; columnIndex < headers.length; columnIndex++) {
                sheet.autoSizeColumn(columnIndex);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to generate appointments Excel report.", ex);
        }
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private int writeMetadataRow(Sheet sheet, int rowIndex, String label, String value, CellStyle labelStyle) {
        Row row = sheet.createRow(rowIndex);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);
        row.createCell(1).setCellValue(getStringValue(value));
        return rowIndex + 1;
    }

    private String getStringValue(String value) {
        return value == null ? "" : value;
    }

    private long getLongValue(Long value) {
        return value == null ? 0L : value;
    }

    private double getDoubleValue(Double value) {
        return value == null ? 0D : value;
    }

    private String formatLocalDate(LocalDate value) {
        return value == null ? "" : value.toString();
    }

    private String formatLocalTime(LocalTime value) {
        return value == null ? "" : value.toString();
    }
}
