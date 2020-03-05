package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.service.api.m2s2.ExportMemoListService;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class ExportMemoListServiceImpl implements ExportMemoListService {

    private static final Logger logger = LoggerFactory.getLogger(ExportMemoListServiceImpl.class);

    public ByteArrayInputStream excelExport(List<MeetingMemoDto> list) {
        String[] columns = {"Type", "Meeting/Call", "Firm", "Fund",
                "Meeting Date", "Author", "Updated", "UpdatedBy"};

        try {
            Workbook workbook = new XSSFWorkbook();
            CreationHelper creationHelper = workbook.getCreationHelper();

            Sheet sheet = workbook.createSheet("Memos");

            Font headerFont = workbook.createFont();
            headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.DARK_BLUE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));

            int rowNum = 1;

            for (MeetingMemoDto memo:list) {
                Row row = sheet.createRow(rowNum++);

                Cell memoTypeCell = row.createCell(0);
                if (memo.getMemoType() == 1) {
                    memoTypeCell.setCellValue("General");
                } else if (memo.getMemoType() == 2) {
                    memoTypeCell.setCellValue("PE");
                } else if (memo.getMemoType() == 3) {
                    memoTypeCell.setCellValue("HF");
                } else if (memo.getMemoType() == 4) {
                    memoTypeCell.setCellValue("RE");
                } else if (memo.getMemoType() == 5) {
                    memoTypeCell.setCellValue("INFR");
                } else {
                    memoTypeCell.setCellValue("");
                }
                row.createCell(1).setCellValue(memo.getMeetingType());
                row.createCell(2).setCellValue(memo.getFirmName());
                row.createCell(3).setCellValue(memo.getFundName());
                Cell meetingDateCell = row.createCell(4);
                meetingDateCell.setCellValue(memo.getMeetingDate());
                meetingDateCell.setCellStyle(dateCellStyle);
                row.createCell(5).setCellValue(memo.getOwner());
                Cell meetingUpdatedDateCell = row.createCell(6);
                if (memo.getUpdateDate() == null) {
                    meetingUpdatedDateCell.setCellValue("");
                } else {
                    meetingUpdatedDateCell.setCellValue(memo.getUpdateDate());
                    meetingUpdatedDateCell.setCellStyle(dateCellStyle);
                }
                row.createCell(7).setCellValue(memo.getUpdater());

            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            logger.error("IOException occured");
            return null;
        }
    }

}