package kz.nicnbk.service.impl.m2s2;

import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.m2s2.*;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.m2s2.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Set;

@Service
public class ExportMemoListServiceImpl implements ExportMemoListService {

    private static final Logger logger = LoggerFactory.getLogger(ExportMemoListServiceImpl.class);

    @Autowired
    private GeneralMeetingMemoService generalMemoService;
    @Autowired
    private PEMeetingMemoService PEmemoService;
    @Autowired
    private HFMeetingMemoService HFmemoService;
    @Autowired
    private REMeetingMemoService REmemoService;
    @Autowired
    private INFRMeetingMemoService INFRmemoService;
    @Autowired
    private EmployeeService EmployeeService;

    public ByteArrayInputStream excelExport(List<MeetingMemoDto> list) {
        String[] columns = {"Type", "Meeting/Call", "Firm", "Fund",
                "Meeting Date", "Meeting Time", "Author", "Updated", "UpdatedBy",
                "Meeting Location", "Purpose", "Attendees", "Summary"};

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
            populateHeaderRow(columns, headerCellStyle, headerRow);

            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));

            int rowNum = 1;
            for (MeetingMemoDto memo:list) {
                Row row = sheet.createRow(rowNum++);
                populateCells(dateCellStyle, memo, row);
            }

            for (int i = 0; i < columns.length - 3; i++) {
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

    private void populateCells(CellStyle dateCellStyle, MeetingMemoDto memo, Row row) {
        Cell memoTypeCell = row.createCell(0);
        populateMemoTypeCell(memo, memoTypeCell);

        row.createCell(1).setCellValue(memo.getMeetingType());
        row.createCell(2).setCellValue(memo.getFirmName());
        row.createCell(3).setCellValue(memo.getFundName());
        Cell meetingDateCell = row.createCell(4);
        meetingDateCell.setCellValue(memo.getMeetingDate());
        meetingDateCell.setCellStyle(dateCellStyle);

        Cell meetingTimeCell = row.createCell(5);
        if (memo.getMeetingTime() == null) {
            meetingTimeCell.setCellValue("");
        } else {
            meetingTimeCell.setCellValue(memo.getMeetingTime());
        }

        row.createCell(6).setCellValue(memo.getOwner());
        Cell meetingUpdatedDateCell = row.createCell(7);
        if (memo.getUpdateDate() == null) {
            meetingUpdatedDateCell.setCellValue("");
        } else {
            meetingUpdatedDateCell.setCellValue(memo.getUpdateDate());
            meetingUpdatedDateCell.setCellStyle(dateCellStyle);
        }
        row.createCell(8).setCellValue(memo.getUpdater());

        Cell meetingLocationCell = row.createCell(9);
        if (memo.getMeetingLocation() == null) {
            meetingLocationCell.setCellValue("");
        } else {
            meetingLocationCell.setCellValue(memo.getMeetingLocation());
        }

        Cell purposeCell = row.createCell(10);
        if (memo.getPurpose() == null) {
            purposeCell.setCellValue("");
        } else {
            purposeCell.setCellValue(memo.getPurpose());
        }

        Cell attendeesCell = row.createCell(11);
        attendeesCell.setCellValue(getAttendees(memo.getMemoType(), memo.getId()));

        Cell summaryCell = row.createCell(12);
        summaryCell.setCellValue(getSummary(memo.getMemoType(), memo.getId()));
    }

    private void populateMemoTypeCell(MeetingMemoDto memo, Cell memoTypeCell) {
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
    }

    private String getAttendees(int type, Long id) {
        String result = "";
        switch (type) {
            case MeetingMemo.GENERAL_DISCRIMINATOR:
                GeneralMeetingMemoDto generalMemo = generalMemoService.get(id);
                Set<EmployeeDto> generalEmployeeSet = generalMemo.getAttendeesNIC();
                result = generateAttendeesString(result, generalEmployeeSet, generalMemo.getAttendeesNICOther(), generalMemo.getAttendeesOther());
                return result;
            case MeetingMemo.PE_DISCRIMINATOR:
                PrivateEquityMeetingMemoDto PEMemo = PEmemoService.get(id);
                Set<EmployeeDto> PEemployeeSet = PEMemo.getAttendeesNIC();
                result = generateAttendeesString(result, PEemployeeSet, PEMemo.getAttendeesNICOther(), PEMemo.getAttendeesOther());
                return result;
            case MeetingMemo.HF_DISCRIMINATOR:
                HedgeFundsMeetingMemoDto HFMemo = HFmemoService.get(id);
                Set<EmployeeDto> HFemployeeSet = HFMemo.getAttendeesNIC();
                result = generateAttendeesString(result, HFemployeeSet, HFMemo.getAttendeesNICOther(), HFMemo.getAttendeesOther());
                return result;
            case MeetingMemo.RE_DISCRIMINATOR:
                RealEstateMeetingMemoDto REMemo = REmemoService.get(id);
                Set<EmployeeDto> REemployeeSet = REMemo.getAttendeesNIC();
                result = generateAttendeesString(result, REemployeeSet, REMemo.getAttendeesNICOther(), REMemo.getAttendeesOther());
                return result;
            case MeetingMemo.INFR_DISCRIMINATOR:
                InfrastructureMeetingMemoDto infrMemo = INFRmemoService.get(id);
                Set<EmployeeDto> infrEmployeeSet = infrMemo.getAttendeesNIC();
                result = generateAttendeesString(result, infrEmployeeSet, infrMemo.getAttendeesNICOther(), infrMemo.getAttendeesOther());
                return result;
            default:
                return result;
        }
    }

    private String generateAttendeesString(String result, Set<EmployeeDto> employeeSet, String attendeesNICOther, String attendeesOther) {
        if (!employeeSet.isEmpty()) {
            result = result + "NIC Attendees: \n";
            for (EmployeeDto employee : employeeSet) {
                result = concatNICAttendees(result, employee);
            }
        }
        if (attendeesNICOther != null) {
            result = result + "Other NIC Attendees: \n" + attendeesNICOther + "\n";
        }
        if (attendeesOther != null) {
            result = result + "Other Party Attendees: \n" + attendeesOther + "\n";
        }
        return result;
    }

    private String concatNICAttendees(String result, EmployeeDto employee) {
        result = result.concat(EmployeeService.getEmployeeById(employee.getId()).toString() + "\n");
        return result;
    }

    private String getSummary(int type, Long id) {
        String result = "";
        switch (type) {
            case MeetingMemo.GENERAL_DISCRIMINATOR:
                GeneralMeetingMemoDto generalMemo = generalMemoService.get(id);
                result = generateSummary(result, generalMemo.getTopic1(), "Topic 1: ", generalMemo.getTopic2(),
                        "Topic 2: ", generalMemo.getTopic3(), "Topic 3: ");
                return result;
            case MeetingMemo.PE_DISCRIMINATOR:
                PrivateEquityMeetingMemoDto PEMemo = PEmemoService.get(id);
                if (PEMemo.getMemoSummary() != null) {
                    result = result + "Memo Summary: " + PEMemo.getMemoSummary() + "\n";
                }
                return result;
            case MeetingMemo.HF_DISCRIMINATOR:
                HedgeFundsMeetingMemoDto HFMemo = HFmemoService.get(id);
                if (HFMemo.getOtherNotes() != null) {
                    result = result + "Topics Discussed: " + HFMemo.getOtherNotes() + "\n";
                }
                return result;
            case MeetingMemo.RE_DISCRIMINATOR:
                RealEstateMeetingMemoDto REMemo = REmemoService.get(id);
                result = generateSummary(result, REMemo.getTeamNotes(), "GP and Team: ", REMemo.getTrackRecordNotes(),
                        "Track Record: ", REMemo.getStrategyNotes(), "Strategy: ");
                if (REMemo.getOtherNotes() != null) {
                    result = result + "Other Info: " + REMemo.getOtherNotes() + "\n";
                }
                return result;
            case MeetingMemo.INFR_DISCRIMINATOR:
                InfrastructureMeetingMemoDto infrMemo = INFRmemoService.get(id);
                result = generateSummary(result, infrMemo.getTeamNotes(), "GP and Team: ", infrMemo.getTrackRecordNotes(),
                        "Track Record: ", infrMemo.getStrategyNotes(), "Strategy: ");
                if (infrMemo.getOtherNotes() != null) {
                    result = result + "Other Info: " + infrMemo.getOtherNotes() + "\n";
                }
                return result;
            default:
                return result;
        }
    }

    private String generateSummary(String result, String summary1, String s, String summary2, String s2, String summary3, String s3) {
        if (summary1 != null) {
            result = result + s + summary1 + "\n";
        }
        if (summary2 != null) {
            result = result + s2 + summary2 + "\n";
        }
        if (summary3 != null) {
            result = result + s3 + summary3 + "\n";
        }
        return result;
    }

}