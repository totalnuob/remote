package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.repo.api.reporting.ReserveCalculationRepository;
import kz.nicnbk.repo.model.lookup.reporting.CapitalCallExportTypeLookup;
import kz.nicnbk.repo.model.reporting.ReserveCalculation;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.privateequity.ReserveCalculationService;
import kz.nicnbk.service.converter.reporting.ReserveCalculationConverter;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportType;
import kz.nicnbk.service.dto.reporting.ReserveCalculationDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by magzumov on 30.11.2017.
 */

@Service
public class ReserveCalculationServiceImpl implements ReserveCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(ReserveCalculationServiceImpl.class);

    /* Root folder on the server */
    @Value("${filestorage.root.directory}")
    private String rootDirectory;

    @Autowired
    private PeriodicReportService periodicReportService;

    @Autowired
    ReserveCalculationRepository reserveCalculationRepository;

    @Autowired
    private CurrencyRatesService currencyRatesService;

    @Autowired
    ReserveCalculationConverter reserveCalculationConverter;

    @Override
    public List<ReserveCalculationDto> getAllReserveCalculations() {
        List<ReserveCalculationDto> records = new ArrayList<>();
        try {

            Date mostRecentFinalReportDate = null;
            List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
            if(periodicReportDtos != null){
                for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                    if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                        if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                            mostRecentFinalReportDate = periodicReportDto.getReportDate();
                        }
                    }
                }
            }

            Iterator<ReserveCalculation> entitiesIterator = this.reserveCalculationRepository.findAll(new Sort(Sort.Direction.DESC, "date", "id")).iterator();
            if (entitiesIterator != null) {
                while (entitiesIterator.hasNext()) {
                    ReserveCalculation entity = entitiesIterator.next();
                    ReserveCalculationDto dto = this.reserveCalculationConverter.disassemble(entity);

                    // set currency rate
                    Date nextDay = DateUtils.getNextDay(dto.getDate());
                    CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
                    if (currencyRatesDto == null || currencyRatesDto.getValue() == null) {
                        // TODO: error message
                        logger.error("No currency rate for date '" + nextDay + "', currency='USD'");
                        //return null;
                    } else {
                        dto.setCurrencyRate(currencyRatesDto.getValue());
                    }

                    // set amount kzt
                    if (dto.getAmount() != null && currencyRatesDto != null && currencyRatesDto.getValue() != null) {
                        dto.setAmountKZT(new BigDecimal(currencyRatesDto.getValue().doubleValue()).multiply(new BigDecimal(dto.getAmount())).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    }

                    if(mostRecentFinalReportDate == null || dto.getDate() == null){
                        dto.setCanDelete(true);
                    }else { //mostRecentFinalReportDate != null && dto.getDate() != null
                        dto.setCanDelete(dto.getDate().compareTo(mostRecentFinalReportDate) <= 0 ? false : true);
                    }

                    records.add(dto);
                }
            }
        } catch (Exception ex) {
            logger.error("Error loading reserve calculations", ex);
            return null;
        }
        return records;
    }


    /**
     * Returns reserve calculation records for the specified month.
     * E.g. if date is 31.08.2018, then returns records with date
     * from 01.08.2018 to 31.08.2018.
     *
     * @param date - date
     * @return - reserve calculation records
     */
    //@Override
    private List<ReserveCalculationDto> getReserveCalculationsForMonth(String code, Date date) {

        Date mostRecentFinalReportDate = null;
        List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
        if(periodicReportDtos != null){
            for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                    if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                        mostRecentFinalReportDate = periodicReportDto.getReportDate();
                    }
                }
            }
        }

        Date fromDate = DateUtils.getFirstDayOfCurrentMonth(date);
        Date toDate = DateUtils.getFirstDayOfNextMonth(date);
        List<ReserveCalculation> entities = this.reserveCalculationRepository.getEntitiesByExpenseTypeBetweenDates(
                code, fromDate, toDate);

        List<ReserveCalculationDto> records = this.reserveCalculationConverter.disassembleList(entities);

        for (ReserveCalculationDto record : records) {
            // set currency rate
            Date nextDay = DateUtils.getNextDay(record.getDate());
            CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
            if (currencyRatesDto == null || currencyRatesDto.getValue() == null) {
                // TODO: error message
                logger.error("No currency rate for date '" + nextDay + "', currency='USD'");
                //return null;
            } else {
                record.setCurrencyRate(currencyRatesDto.getValue());
            }

            if(mostRecentFinalReportDate == null || record.getDate() == null){
                record.setCanDelete(true);
            }else { //mostRecentFinalReportDate != null && dto.getDate() != null
                record.setCanDelete(record.getDate().compareTo(mostRecentFinalReportDate) <= 0 ? false : true);
            }

            // set amount kzt
            if (record.getAmount() != null && currencyRatesDto != null && currencyRatesDto.getValue() != null) {
                record.setAmountKZT(MathUtils.multiply(currencyRatesDto.getValue().doubleValue(), record.getAmount()));
            }
        }

        return records;
    }

    @Override
    public Double getReserveCalculationSumKZTForMonth(String code, Date date) {
        List<ReserveCalculationDto> records = getReserveCalculationsForMonth(code, date);
        Double sum = 0.0;
        if (records != null) {
            for (ReserveCalculationDto record : records) {
                if (record.getCurrencyRate() != null) {
                    sum = MathUtils.add(sum, record.getAmountKZT());
                } else {
                    String errorMessage = "Reserve Calculations sum: one of records has no currency rate. Date '" + DateUtils.getDateFormatted(record.getDate()) + "'";
                    logger.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
            }
        }
        return sum;
    }

    @Override
    public boolean save(List<ReserveCalculationDto> records) {
        try {
            if (records != null) {
                List<ReserveCalculation> entities = this.reserveCalculationConverter.assembleList(records);
                this.reserveCalculationRepository.save(entities);
            }
        } catch (Exception ex) {
            logger.error("Error saving reserve calculation records", ex);
            return false;
        }

        return true;
    }

    @Override
    public List<ReserveCalculationDto> getReserveCalculationsByExpenseType(String code) {
        Date mostRecentFinalReportDate = null;
        List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
        if(periodicReportDtos != null){
            for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                    if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                        mostRecentFinalReportDate = periodicReportDto.getReportDate();
                    }
                }
            }
        }

        List<ReserveCalculationDto> records = new ArrayList<>();
        List<ReserveCalculation> entities = this.reserveCalculationRepository.getEntitiesByExpenseType(code);
        if (entities != null) {
            for (ReserveCalculation entity : entities) {
                ReserveCalculationDto dto = this.reserveCalculationConverter.disassemble(entity);

                // set currency rate
                Date nextDay = DateUtils.getNextDay(dto.getDate());
                CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
                if (currencyRatesDto == null || currencyRatesDto.getValue() == null) {
                    // TODO: error message
                    logger.error("No currency rate for date '" + DateUtils.getDateFormatted(nextDay) + "', currency='USD'");
                    throw new IllegalStateException("No currency rate for date '" + DateUtils.getDateFormatted(nextDay) + "', currency='USD'");
                }
                dto.setCurrencyRate(currencyRatesDto.getValue());

                // set amount kzt
                if (dto.getAmount() != null) {
                    dto.setAmountKZT(new BigDecimal(currencyRatesDto.getValue().doubleValue()).multiply(new BigDecimal(dto.getAmount())).setScale(2, RoundingMode.HALF_UP).doubleValue());
                }

                if(mostRecentFinalReportDate == null || dto.getDate() == null){
                    dto.setCanDelete(true);
                }else { //mostRecentFinalReportDate != null && dto.getDate() != null
                    dto.setCanDelete(dto.getDate().compareTo(mostRecentFinalReportDate) <= 0 ? false : true);
                }

                records.add(dto);
            }
        }
        return records;
    }

    @Override
    public InputStream getExportFileStream(Long recordId, String type) {
        if (recordId == null) {
            logger.error("Capital call export: record id not specified");
            return null;
        }
        if (type == null) {
            logger.error("Capital call export: type not specified");
            return null;
        }

        if (type.equalsIgnoreCase(CapitalCallExportTypeLookup.TO_OPERATIONS.getCode())) {
            return getOperationsExportInputStream(recordId);
        } else if (type.equalsIgnoreCase(CapitalCallExportTypeLookup.TO_SPV.getCode())) {
            return getSPVExportInputStream(recordId);
        } else if (type.equalsIgnoreCase(CapitalCallExportTypeLookup.ORDER.getCode())) {
            return getOrderExportInputStream(recordId);
        }
        logger.error("Capital call export: type not matched '" + type + "'");
        return null;
    }

    @Override
    public boolean deleteReserveCalculationRecord(Long recordId) {
        try {
            ReserveCalculation entity = this.reserveCalculationRepository.findOne(recordId);
            if(entity == null){
                logger.error("Reserve calculation - No record found to delete: record id " + recordId);
                return false;
            }

            // Get date of most recent report with status SUBMITTED
            Date mostRecentFinalReportDate = null;
            List<PeriodicReportDto> periodicReportDtos = this.periodicReportService.getAllPeriodicReports();
            if(periodicReportDtos != null){
                for(PeriodicReportDto periodicReportDto: periodicReportDtos){
                    if(periodicReportDto.getStatus() != null && periodicReportDto.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                        if (mostRecentFinalReportDate == null || mostRecentFinalReportDate.compareTo(periodicReportDto.getReportDate()) < 0) {
                            mostRecentFinalReportDate = periodicReportDto.getReportDate();
                        }
                    }
                }
            }

            // cannot delete record with date earlier than most recent SUBMITTED report date
            if(mostRecentFinalReportDate != null && entity.getDate().compareTo(mostRecentFinalReportDate) <= 0){
                logger.error("Reserve Calculation delete record failed for record id " + recordId +
                        ": finalized report exists with date '" + DateUtils.getDateFormatted(mostRecentFinalReportDate) +
                        "', record date '" + DateUtils.getDateFormatted(entity.getDate()) + "'");
                return false;
            }

            this.reserveCalculationRepository.delete(entity);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting Reserve Calculation Record: record id " + recordId, ex);
        }
        return false;
    }

    private ReserveCalculationDto getReserveCalculationRecordById(Long recordId){
        return this.reserveCalculationConverter.disassemble(this.reserveCalculationRepository.findOne(recordId));
    }

    private InputStream getOperationsExportInputStream(Long recordId){

        ReserveCalculationDto record = getReserveCalculationRecordById(recordId);

        Resource resource = new ClassPathResource("export_template/capital_call/CC_TA_OA_TEMPLATE.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'CC_TA_OA_TEMPLATE.xlsx'");
            return null;
            //e.printStackTrace();
        }

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            boolean endOfTable = false;
            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Original Investment Approval Date:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(3), "<dd.MM.yyyy>")){
                    row.getCell(3).setCellValue(DateUtils.getDateFormatted(record.getDate()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Value Date:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(3), "<date_text>")){
                    row.getCell(3).setCellValue(DateUtils.getDateEnglishTextualDate(record.getDate()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Amount:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(3), "<amount>")){
                    // TODO: amount formatting
                    row.getCell(3).setCellValue(String.format(Locale.ENGLISH, "$%,.2f", record.getAmount()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Akylzhan Baimagambetov-Director") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(3), "<dd.MM.yyyy>")){
                    row.getCell(3).setCellValue(DateUtils.getDateFormatted(record.getDate()));
                }
            }
            //
            File tmpDir = new File(this.rootDirectory + "/tmp");

            String[]entries = tmpDir.list();
            if(entries != null) {
                for (String s : entries) {
                    File currentFile = new File(tmpDir.getPath(), s);
                    currentFile.delete();
                }
            }

            // write to new
            String filePath = this.rootDirectory + "/tmp/CC_TA_OA_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        } catch (IOException e) {
            logger.error("IO Exception when exporting Capital Call TA OA", e);
        }

        return null;
    }

    private InputStream getSPVExportInputStream(Long recordId){
        ReserveCalculationDto record = getReserveCalculationRecordById(recordId);

        Resource resource = new ClassPathResource("export_template/capital_call/CC_OA_SPV_TEMPLATE.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'CC_OA_SPV_TEMPLATE.xlsx'");
            return null;
            //e.printStackTrace();
        }

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            boolean endOfTable = false;
            String beneficiary = record.getRecipient().getCode().startsWith("SING") ? "Singularity, Ltd." :
                    record.getRecipient().getCode().startsWith("TARR") ? "Tarragon LP" : "";
            String bankCode = record.getRecipient().getCode().startsWith("TARR") ? "CTZIUS33" :
                    record.getRecipient().getCode().startsWith("SING") ? "021 000 018" : "";
            String bankDetails = record.getRecipient().getCode().startsWith("TARR") ? "Citizens Bank, N.A., One Citizens Drive Riverside, RI 02915-3000" :
                    record.getRecipient().getCode().startsWith("SING") ? "Bank of New York Mellon, 1 Wall Street, New York" : "";
            String accountNumber = record.getRecipient().getCode().startsWith("TARR") ? "4010297920" :
                    record.getRecipient().getCode().startsWith("SING_B") ? "8901300101" :
                            record.getRecipient().getCode().startsWith("SING_A") ? "8901274828" : "";
            String reference = record.getRecipient().getCode().startsWith("TARR") ? "NICK Master Fund Ltd." :
                    record.getRecipient().getCode().startsWith("SING") ? "Grosvenor Capital Management, L.P." : "";

            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Original Investment Approval Date:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<dd.MM.yyyy>")){
                    row.getCell(4).setCellValue(DateUtils.getDateFormatted(record.getDate()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Use of Funds (New, Add-on, Mgmt Fee, etc.):") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<use_of_funds>")) {
                    row.getCell(4).setCellValue("Funds transfer from NICK Operating to " + beneficiary);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Value Date:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<date_text_with_time>")){
                    Date date = record.getValueDate() != null ? record.getValueDate() : record.getDate();
                    row.getCell(4).setCellValue(DateUtils.getDateEnglishTextualDate(date) + " before 4 pm London time");
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Amount:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<amount>")){
                    // TODO: amount formatting
                    row.getCell(4).setCellValue(String.format(Locale.ENGLISH, "$%,.2f", record.getAmount()));
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Bank Code (SWIFT or ABA):") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<bank_code>")){
                    row.getCell(4).setCellValue(bankCode);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Bank Name and Address:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<bank_details>")){
                    row.getCell(4).setCellValue(bankDetails);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Account number or CHIPS UID:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<account_number>")){
                    row.getCell(4).setCellValue(accountNumber);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Beneficiary's Name:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<beneficiary>")){
                    row.getCell(4).setCellValue(beneficiary);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Reference:") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<reference>")){
                    row.getCell(4).setCellValue(reference);
                }else if(ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(2), "Akylzhan Baimagambetov-Director") &&
                        ExcelUtils.isCellStringValueEqualMatchCase(row.getCell(4), "<dd.MM.yyyy>")){
                    row.getCell(4).setCellValue(DateUtils.getDateFormatted(record.getDate()));
                }
            }
            //
            File tmpDir = new File(this.rootDirectory + "/tmp");

            String[]entries = tmpDir.list();
            if(entries != null) {
                for (String s : entries) {
                    File currentFile = new File(tmpDir.getPath(), s);
                    currentFile.delete();
                }
            }

            // write to new
            String filePath = this.rootDirectory + "/tmp/CC_TA_OA_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        } catch (IOException e) {
            logger.error("IO Exception when exporting Capital Call TA OA", e);
        }

        return null;
    }

    private InputStream getOrderExportInputStream(Long recordId){

        ReserveCalculationDto record = getReserveCalculationRecordById(recordId);

        Resource resource = new ClassPathResource("export_template/capital_call/CC_ORDER_TEMPLATE.docx");
        InputStream wordFileToRead = null;
        try {
            wordFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'CC_ORDER_TEMPLATE.docx'");
            return null;
            //e.printStackTrace();
        }

        try {
            XWPFDocument document = new XWPFDocument(wordFileToRead);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            if(paragraphs != null){
                for(XWPFParagraph paragraph: paragraphs){
                    List<XWPFRun> runs = paragraph.getRuns();
                    if (runs != null) {
                        for (XWPFRun r : runs) {
                            String text = r.getText(0);
                            if (text != null && text.contains("INPUTDATE")) {
                                text = text.replace("INPUTDATE", DateUtils.getDateEnglishTextualDate(record.getDate()));
                                r.setText(text, 0);
                            }else if (text != null && text.contains("INPUTVALUEDATE")) {
                                text = text.replace("INPUTVALUEDATE", DateUtils.getDateEnglishTextualDate(record.getDate()));
                                r.setText(text, 0);
                            }else if (text != null && text.contains("INPUTENTITY")) {

                                String name = record.getRecipient().getCode().startsWith("TARR") ? "Tarragon LP (Class B)" :
                                        record.getRecipient().getCode().startsWith("SING") ? "Singularity Ltd. (Class A)" :
                                        record.getRecipient().getNameEn();

                                text = text.replace("INPUTENTITY", name);
                                r.setText(text, 0);
                            }else if (text != null && text.contains("INPUTAMOUNT")) {

                                text = text.replace("INPUTAMOUNT", String.format(Locale.ENGLISH, "$%,.2f", record.getAmount()));
                                r.setText(text, 0);
                            }
                        }
                    }
                }

                File tmpDir = new File(this.rootDirectory + "/tmp");

                String[]entries = tmpDir.list();
                if(entries != null) {
                    for (String s : entries) {
                        File currentFile = new File(tmpDir.getPath(), s);
                        currentFile.delete();
                    }
                }

                // write to new
                String filePath = this.rootDirectory + "/tmp/CC_ORDER_" + new Date().getTime() + ".docx";
                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    document.write(outputStream);
                }

                InputStream inputStream = new FileInputStream(filePath);
                return inputStream;
            }

        } catch (IOException e) {
            logger.error("IO Exception when exporting Capital Call Order", e);
            //e.printStackTrace();
        }

        return null;
    }
}
