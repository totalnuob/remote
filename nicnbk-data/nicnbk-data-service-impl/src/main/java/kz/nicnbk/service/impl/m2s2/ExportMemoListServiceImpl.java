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

            for (int i = 0; i < columns.length - 3; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));

            int rowNum = 1;

            for (MeetingMemoDto memo:list) {
                Row row = sheet.createRow(rowNum++);
                populateCells(dateCellStyle, memo, row);
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

    private void populateCells(CellStyle dateCellStyle, MeetingMemoDto memo, Row row) {
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

    private String getAttendees(int type, Long id) {
        String result = "";
        switch (type) {
            case MeetingMemo.GENERAL_DISCRIMINATOR:
                GeneralMeetingMemoDto generalMemo = generalMemoService.get(id);
                Set<EmployeeDto> generalEmployeeSet = generalMemo.getAttendeesNIC();
                if (!generalEmployeeSet.isEmpty()) {
                    result = result + "NIC Attendees: \n";
                    for (EmployeeDto employee : generalEmployeeSet) {
                        result = result + EmployeeService.getEmployeeById(employee.getId()).toString() + "\n";
                    }
                }
                if (generalMemo.getAttendeesNICOther() != null) {
                    result = result + "Other NIC Attendees: \n" + generalMemo.getAttendeesNICOther() + "\n";
                }
                if (generalMemo.getAttendeesOther() != null) {
                    result = result + "Other Party Attendees: \n" + generalMemo.getAttendeesOther() + "\n";
                }
                return result;
            case MeetingMemo.PE_DISCRIMINATOR:
                PrivateEquityMeetingMemoDto PEMemo = PEmemoService.get(id);
                Set<EmployeeDto> PEemployeeSet = PEMemo.getAttendeesNIC();
                if (!PEemployeeSet.isEmpty()) {
                    result = result + "NIC Attendees: \n";
                    for (EmployeeDto employee : PEemployeeSet) {
                        result = result + EmployeeService.getEmployeeById(employee.getId()).toString() + "\n";
                    }
                }
                if (PEMemo.getAttendeesNICOther() != null) {
                    result = result + "Other NIC Attendees: \n" + PEMemo.getAttendeesNICOther() + "\n";
                }
                if (PEMemo.getAttendeesOther() != null) {
                    result = result + "Other Party Attendees: \n" + PEMemo.getAttendeesOther() + "\n";
                }
                return result;
            case MeetingMemo.HF_DISCRIMINATOR:
                HedgeFundsMeetingMemoDto HFMemo = HFmemoService.get(id);
                Set<EmployeeDto> HFemployeeSet = HFMemo.getAttendeesNIC();
                if (!HFemployeeSet.isEmpty()) {
                    result = result + "NIC Attendees: \n";
                    for (EmployeeDto employee : HFemployeeSet) {
                        result = result + EmployeeService.getEmployeeById(employee.getId()).toString() + "\n";
                    }
                }
                if (HFMemo.getAttendeesNICOther() != null) {
                    result = result + "Other NIC Attendees: \n" + HFMemo.getAttendeesNICOther() + "\n";
                }
                if (HFMemo.getAttendeesOther() != null) {
                    result = result + "Other Party Attendees: \n" + HFMemo.getAttendeesOther() + "\n";
                }
                return result;
            case MeetingMemo.RE_DISCRIMINATOR:
                RealEstateMeetingMemoDto REMemo = REmemoService.get(id);
                Set<EmployeeDto> REemployeeSet = REMemo.getAttendeesNIC();
                if (!REemployeeSet.isEmpty()) {
                    result = result + "NIC Attendees: \n";
                    for (EmployeeDto employee : REemployeeSet) {
                        result = result + EmployeeService.getEmployeeById(employee.getId()).toString() + "\n";
                    }
                }
                if (REMemo.getAttendeesNICOther() != null) {
                    result = result + "Other NIC Attendees: \n" + REMemo.getAttendeesNICOther() + "\n";
                }
                if (REMemo.getAttendeesOther() != null) {
                    result = result + "Other Party Attendees: \n" + REMemo.getAttendeesOther() + "\n";
                }
                return result;
            case MeetingMemo.INFR_DISCRIMINATOR:
                InfrastructureMeetingMemoDto infrMemo = INFRmemoService.get(id);
                Set<EmployeeDto> infrEmployeeSet = infrMemo.getAttendeesNIC();
                if (!infrEmployeeSet.isEmpty()) {
                    result = result + "NIC Attendees: \n";
                    for (EmployeeDto employee : infrEmployeeSet) {
                        result = result + EmployeeService.getEmployeeById(employee.getId()).toString() + "\n";
                    }
                }
                if (infrMemo.getAttendeesNICOther() != null) {
                    result = result + "Other NIC Attendees: \n" + infrMemo.getAttendeesNICOther() + "\n";
                }
                if (infrMemo.getAttendeesOther() != null) {
                    result = result + "Other Party Attendees: \n" + infrMemo.getAttendeesOther() + "\n";
                }
                return result;
            default:
                return result;
        }
    }

    private String getSummary(int type, Long id) {
        String result = "";
        switch (type) {
            case MeetingMemo.GENERAL_DISCRIMINATOR:
                GeneralMeetingMemoDto generalMemo = generalMemoService.get(id);
                if (generalMemo.getTopic1() != null) {
                    result = result + "Topic 1: " + generalMemo.getTopic1() + "\n";
                }
                if (generalMemo.getTopic2() != null) {
                    result = result + "Topic 2: " + generalMemo.getTopic2() + "\n";
                }
                if (generalMemo.getTopic3() != null) {
                    result = result + "Topic 3: " + generalMemo.getTopic3() + "\n";
                }
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
                if (REMemo.getTeamNotes() != null) {
                    result = result + "GP and Team: " + REMemo.getTeamNotes() + "\n";
                }
                if (REMemo.getTrackRecordNotes() != null) {
                    result = result + "Track Record: " + REMemo.getTrackRecordNotes() + "\n";
                }
                if (REMemo.getStrategyNotes() != null) {
                    result = result + "Strategy: " + REMemo.getStrategyNotes() + "\n";
                }
                if (REMemo.getOtherNotes() != null) {
                    result = result + "Other Info: " + REMemo.getOtherNotes() + "\n";
                }
                return result;
            case MeetingMemo.INFR_DISCRIMINATOR:
                InfrastructureMeetingMemoDto infrMemo = INFRmemoService.get(id);
                if (infrMemo.getTeamNotes() != null) {
                    result = result + "GP and Team: " + infrMemo.getTeamNotes() + "\n";
                }
                if (infrMemo.getTrackRecordNotes() != null) {
                    result = result + "Track Record: " + infrMemo.getTrackRecordNotes() + "\n";
                }
                if (infrMemo.getStrategyNotes() != null) {
                    result = result + "Strategy: " + infrMemo.getStrategyNotes() + "\n";
                }
                if (infrMemo.getOtherNotes() != null) {
                    result = result + "Other Info: " + infrMemo.getOtherNotes() + "\n";
                }
                return result;
            default:
                return result;
        }
    }

}