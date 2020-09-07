package kz.nicnbk.service.impl.tripmemo;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.dto.tripmemo.TripMemoDto;
import kz.nicnbk.service.impl.m2s2.ExportMemoListServiceImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportTripMemoListServiceImpl implements BaseService {

    private static final Logger logger = LoggerFactory.getLogger(ExportMemoListServiceImpl.class);

    @Autowired
    private EmployeeService EmployeeService;

    public ByteArrayInputStream excelExport(List<TripMemoDto> list) {
        String[] columns = {"Type", "Name", "Organization", "Location",
                "Date from", "Date to", "Author", "Updated", "Attendees", "Description"};

        try {
            Workbook workbook = new XSSFWorkbook();
            CreationHelper creationHelper = workbook.getCreationHelper();

            Sheet sheet = workbook.createSheet("TripMemos");

            Font headerFont = workbook.createFont();
            headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.DARK_BLUE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            populateHeaderRow(columns, headerCellStyle, headerRow);

            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));

            int rowNum = 1;
            for (TripMemoDto tripMemo : list) {
                Row row = sheet.createRow(rowNum++);
                populateCells(dateCellStyle, tripMemo, row);
            }

            for (int i = 0; i < columns.length - 1; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            logger.error("IOException occurred");
            return null;
        }
    }

    private void populateHeaderRow(String[] columns, CellStyle headerCellStyle, Row headerRow) {
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
    }

    private void populateCells(CellStyle dateCellStyle, TripMemoDto tripMemo, Row row) {
        Cell memoTypeCell = row.createCell(0);
        populateMemoTypeCell(tripMemo, memoTypeCell);

        row.createCell(1).setCellValue(tripMemo.getName());
        row.createCell(2).setCellValue(tripMemo.getOrganization());
        row.createCell(3).setCellValue(tripMemo.getLocation());

        Cell meetingDateStartCell = row.createCell(4);
        meetingDateStartCell.setCellValue(tripMemo.getMeetingDateStart());
        meetingDateStartCell.setCellStyle(dateCellStyle);

        Cell meetingDateEndCell = row.createCell(5);
        meetingDateEndCell.setCellValue(tripMemo.getMeetingDateEnd());
        meetingDateEndCell.setCellStyle(dateCellStyle);

        Cell authorCell = row.createCell(6);
        String author = tripMemo.getAuthor();
        String owner = tripMemo.getOwner();
        if (author == null || author.isEmpty()) {
            authorCell.setCellValue(owner);
        } else {
            authorCell.setCellValue(author);
        }
        Cell meetingUpdatedDateCell = row.createCell(7);
        if (tripMemo.getUpdateDate() == null) {
            meetingUpdatedDateCell.setCellValue("");
        } else {
            meetingUpdatedDateCell.setCellValue(tripMemo.getUpdateDate());
            meetingUpdatedDateCell.setCellStyle(dateCellStyle);
        }

        String attendees = tripMemo.getAttendees().toString();
        attendees = attendees.substring(1, attendees.length() - 1);
        row.createCell(8).setCellValue(attendees);
        row.createCell(9).setCellValue(tripMemo.getDescription());
    }

    private void populateMemoTypeCell(TripMemoDto tripMemo, Cell memoTypeCell) {
        if (tripMemo.getTripType() == null || tripMemo.getTripType().isEmpty()) {
            memoTypeCell.setCellValue("");
        } else if (tripMemo.getTripType().equals("CONFERENCE")) {
            memoTypeCell.setCellValue("CONFERENCE");
        } else if (tripMemo.getTripType().equals("TRAINING")) {
            memoTypeCell.setCellValue("TRAINING");
        } else {
            memoTypeCell.setCellValue("");
        }
    }
}
