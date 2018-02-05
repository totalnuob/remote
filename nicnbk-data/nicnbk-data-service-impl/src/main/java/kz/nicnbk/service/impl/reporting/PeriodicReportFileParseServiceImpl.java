package kz.nicnbk.service.impl.reporting;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import kz.nicnbk.common.service.util.*;
import kz.nicnbk.repo.api.lookup.StrategyRepository;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFGeneralLedgerBalance;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFNOAL;
import kz.nicnbk.repo.model.reporting.privateequity.*;
import kz.nicnbk.service.api.reporting.PeriodicDataService;
import kz.nicnbk.service.api.reporting.PeriodicReportFileParseService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFNOALService;
import kz.nicnbk.service.api.reporting.privateequity.*;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.ConsolidatedReportRecordDto;
import kz.nicnbk.service.dto.reporting.PeriodicDataDto;
import kz.nicnbk.service.dto.reporting.SingularityGeneralLedgerBalanceRecordDto;
import kz.nicnbk.service.dto.reporting.SingularityNOALRecordDto;
import kz.nicnbk.service.dto.reporting.exception.ExcelFileParseException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by magzumov on 18.01.2018.
 */
@Service
public class PeriodicReportFileParseServiceImpl implements PeriodicReportFileParseService {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicReportFileParseServiceImpl.class);

    @Autowired
    private PEScheduleInvestmentService scheduleInvestmentService;

    @Autowired
    private PEStatementBalanceService statementBalanceService;

    @Autowired
    private PEStatementOperationsService statementOperationsService;

    @Autowired
    private PEStatementChangesService statementChangesService;

    @Autowired
    private PEStatementCashflowsService statementCashflowsService;

    @Autowired
    private PeriodicDataService periodicDataService;

    @Autowired
    private HFGeneralLedgerBalanceService generalLedgerBalanceService;

    @Autowired
    private HFNOALService hfNOALService;


    // TODO: use service (LookupService, StrategyService etc) ?
    @Autowired
    private StrategyRepository strategyRepository;

    /**
     * Parse specified file and save parsed data to database.
     * Parse file by calling corresponding function (by file type).
     * Return the result of parsing.
     *
     * @param fileType - file type
     * @param filesDto - file data
     * @param reportId - report id
     * @return - file upload result
     */
    @Override
    public FileUploadResultDto parseFile(String fileType, FilesDto filesDto, Long reportId) {
        if(fileType.equals(FileTypeLookup.NB_REP_TARR_SCHED_INVEST.getCode())){
            return parseScheduleInvestments(filesDto, reportId);
        }else if(fileType.equals(FileTypeLookup.NB_REP_TARR_STMT_BALANCE_OPERATIONS.getCode())){
            return parseStatementAssetsLiabilities(filesDto, reportId);
        }else if(fileType.equals(FileTypeLookup.NB_REP_TARR_STMT_CASHFLOW.getCode())){
            return parseStatementCashFlows(filesDto, reportId);
        }else if(fileType.equals(FileTypeLookup.NB_REP_TARR_STMT_CHANGES.getCode())){
            return parseStatementChanges(filesDto, reportId);
        }else if(fileType.equals(FileTypeLookup.NB_REP_S1A.getCode())){
            return parseBSTrancheA(filesDto);
        }else if(fileType.equals(FileTypeLookup.NB_REP_S2A.getCode())){
            return parseIMDRTrancheA(filesDto);
        }else if(fileType.equals(FileTypeLookup.NB_REP_S3A.getCode())){
            return parsePARTrancheA(filesDto);
        }else if(fileType.equals(FileTypeLookup.NB_REP_S4A.getCode())){
            return parseISTrancheA(filesDto);
        }else if(fileType.equals(FileTypeLookup.NB_REP_S1B.getCode())){
            return parseBSTrancheB(filesDto);
        }else if(fileType.equals(FileTypeLookup.NB_REP_S2B.getCode())){
            return parseIMDRTrancheB(filesDto);
        }else if(fileType.equals(FileTypeLookup.NB_REP_S3B.getCode())){
            return parsePARTrancheB(filesDto);
        }else if(fileType.equals(FileTypeLookup.NB_REP_S4B.getCode())){
            return parseISTrancheB(filesDto);
        }else if(fileType.equals(FileTypeLookup.NB_REP_SINGULAR_GENERAL_LEDGER.getCode())){
            return parseSingularGeneralLedger(filesDto, reportId);
        }else if(fileType.equals(FileTypeLookup.NB_REP_SN_TRANCHE_A.getCode())){
            return parseSingularNOAL(filesDto, reportId, 1);
        }else if(fileType.equals(FileTypeLookup.NB_REP_SN_TRANCHE_B.getCode())){
            return parseSingularNOAL(filesDto, reportId, 2);
        }else{
            // log
            logger.error("File type did not match[" + fileType + "] for file '" + filesDto.getFileName() + "'");
            FileUploadResultDto fileUploadResultDto = new FileUploadResultDto(ResponseStatusType.FAIL, null, "Report type mismatch", null);
            return fileUploadResultDto;
        }
    }

    /**
     * Parse schedule of investment excel file for specified report id and save parsed data to database.
     *
     * @param filesDto - schedule of investments file
     * @param reportId - report id
     * @return - file parse result
     * @throws ExcelFileParseException
     */
    private FileUploadResultDto parseScheduleInvestments(FilesDto filesDto, Long reportId) throws ExcelFileParseException{

        List<ConsolidatedReportRecordDto> sheet1Records = new ArrayList<>();
        List<ConsolidatedReportRecordDto> sheet2Records = new ArrayList<>();
        try {

            /* PARSE EXCEL (RAW) *******************************************************************************/
            // Sheet 1 - Tranche A
            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);
            sheet1Records = parseScheduleInvestmentsSheetRaw(rowIterator);

            // Sheet 2 - Tranche B
            rowIterator = getRowIterator(filesDto, 1);
            sheet2Records = parseScheduleInvestmentsSheetRaw(rowIterator);

            //printRecords(sheet1Records);
            //System.out.println("---------------------------------------------------------------------------\n");
            //printRecords(sheet2Records);

            /* NORMALIZE TEXT FIELDS ********************************************************************************/
            normalizeTextFields(sheet1Records);
            normalizeTextFields(sheet2Records);

            /* CHECK FORMAT *****************************************************************************************/

            checkHeaderClosingTotalSumFormat(sheet1Records, getScheduleInvestmentsTotalRecordName(sheet1Records, "[Tranche A] "), "[Tranche A] ");
            checkHeaderClosingTotalSumFormat(sheet2Records, getScheduleInvestmentsTotalRecordName(sheet2Records, "[Tranche B] "), "[Tranche B] ");


            /* CHECK SUMS/TOTALS ********************************************************************************/
            checkTotalSumsGeneric(sheet1Records, 3, getScheduleInvestmentsTotalRecordName(sheet1Records, "[Tranche A] "), 1);
            checkTotalSumsGeneric(sheet2Records, 3, getScheduleInvestmentsTotalRecordName(sheet2Records, "[Tranche B] "), 2);

            /* CHECK ENTITIES AND ASSEMBLE **********************************************************************/
            // sheet 1 - Tranche A
            List<ReportingPEScheduleInvestment> entities1 = this.scheduleInvestmentService.assembleList(sheet1Records, 1, reportId); // TODO: tranche type constant !!!
            // sheet 2 - Tranche B
            List<ReportingPEScheduleInvestment> entities2 = this.scheduleInvestmentService.assembleList(sheet2Records, 2, reportId); // TODO: tranche type constant !!!

            /* SAVE TO DB **************************************************************************************/
            boolean saved = this.scheduleInvestmentService.save(entities1);
            if(saved) {
                saved = this.scheduleInvestmentService.save(entities2);
            }

            if(saved){
                logger.info("Successfully parsed 'Schedule of Investments' file");
                return new FileUploadResultDto(ResponseStatusType.SUCCESS, "", "Successfully processed the file - Schedule of Investments", "");
            }else{
                logger.error("Error saving 'Schedule of Investments' file parsed data into database");
                return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Schedule of Investments' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Schedule of Investments' file with error. Stack trace: \n" + ExceptionUtils.getStackTrace(e));
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error when processing 'Schedule of Investments' file", "");
        }
    }

    /**
     * Return the name of total record for schedule of investments, depending on the given records list.
     * Return either
     *  1) Total Fund Investments, or
     *  2) Total co-investments, or
     *  3) Total Fund Investments and co-investments
     *
     * @param records - records
     * @return - correct total record name
     */
    private String getScheduleInvestmentsTotalRecordName(List<ConsolidatedReportRecordDto> records, String trancheName){
        String nameTotal1 = "Fund Investments";
        String nameTotal2 = "co-investments";
        if(records != null){
            String name = null;
            for(ConsolidatedReportRecordDto record: records){
                if(record.getClassifications() != null && record.getClassifications()[0] != null){
                    String currentName = null;
                    if(record.getClassifications()[0].equalsIgnoreCase(nameTotal1)){
                        currentName = nameTotal1;
                    }else if(record.getClassifications()[0].equalsIgnoreCase(nameTotal2)){
                        currentName = nameTotal2;
                    }else if(!record.getName().equalsIgnoreCase("Total " + nameTotal1 + " and " + nameTotal2)){
                        logger.error(trancheName + "Investment types could not be determined: expected '" + nameTotal1 + "' or '" + nameTotal2 + "'");
                        throw new ExcelFileParseException(trancheName + "Investment types could not be determined: expected '" + nameTotal1 + "' or '" + nameTotal2 + "'");
                    }

                    if(name != null && currentName != null && !name.equalsIgnoreCase(currentName)){
                        return "Total " + nameTotal1 + " and " + nameTotal2;
                    }else if(name == null && currentName != null){
                        name = currentName;
                    }
                }
            }
            if(StringUtils.isNotEmpty(name)){
                return "Total " + name;
            }
        }
        logger.error(trancheName + "Total record name could not be determined: check for possible missing headers ('" + nameTotal1 + "', '" + nameTotal2 + "', etc.");
        throw new ExcelFileParseException(trancheName + "Total record name could not be determined: check for possible missing headers ('" + nameTotal1 + "', '" + nameTotal2 + "', etc.");
    }

    /**
     * Iterate over rows of excel file (specified by row iterator) and return a list of corresponding DTOs.
     * Each DTO represents a data row in excel file.
     *
     * @param rowIterator - excel file row iterator
     * @return - list of DTOs
     */
    private List<ConsolidatedReportRecordDto> parseScheduleInvestmentsSheetRaw(Iterator<Row> rowIterator){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        int rowNum = 0;
        String[] classifications = new String[5];
        while (rowIterator.hasNext()) { // each row
            Row row = rowIterator.next();
            if (rowNum < 4) {/* ROW 0-3 */
                // check file header
                checkScheduleInvestmentsFileHeader(rowNum, row);
            } else if (rowNum == 4) { /* ROW == 4*/
                // check table header
                checkScheduleInvestmentsTableHeader(row);
            } else{ /* Rows 4,... */
                // Check Main Headers: Fund Investments & Co-Investments
                if(ExcelUtils.getStringValueFromCell(row.getCell(0)) != null){
                    if(ExcelUtils.isEmptyCellRange(row, 2, 4)){ // TODO: (1, 3)
                        // set classifications
                        for(int i = 0; i < classifications.length; i++){
                            if(classifications[i] == null){
                                classifications[i] = row.getCell(0).getStringCellValue().trim();
                                break;
                            }
                        }
                    }else{
                        // do nothing
                    }
                }
                Cell cell = row.getCell(1);
                if(ExcelUtils.getStringValueFromCell(cell) != null){
                    if(ExcelUtils.isEmptyCellRange(row, 2, 4)){
                        // classifications
                        for(int i = 0; i < classifications.length; i++){
                            if(classifications[i] == null){
                                classifications[i] = cell.getStringCellValue().trim();
                                break;
                            }
                        }
                    } else{
                        // values
                        String name = cell.getStringCellValue().trim();
                        Double[] values = new Double[3];
                        for(int i = 2; i <= 4; i++) {
                            if (row.getCell(i) != null && row.getCell(i).getCellType() == Cell.CELL_TYPE_STRING &&
                                    StringUtils.isNotEmpty(row.getCell(i).getStringCellValue()) && !row.getCell(i).getStringCellValue().trim().equalsIgnoreCase("-")) {
                                logger.error("Expected numeric value for record '" + name + "', found  text value '" + row.getCell(i).getStringCellValue() + "'");
                                throw new ExcelFileParseException("Expected numeric value for record '" + name + "', found text value '" + row.getCell(i).getStringCellValue() + "'");
                            }
                        }
                        values[0] = ExcelUtils.getDoubleValueFromCell(row.getCell(2));
                        values[1] = ExcelUtils.getDoubleValueFromCell(row.getCell(3));
                        values[2] = ExcelUtils.getDoubleValueFromCell(row.getCell(4));

                        ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(classifications.length, 3);
                        recordDto.setName(name);
                        recordDto.setValues(values);
                        // classifications
                        recordDto.setClassifications(Arrays.copyOf((String[]) classifications, classifications.length));
                        // currency
                        recordDto.setCurrency(ExcelUtils.getCellCurrency(row.getCell(2)));

                        recordDto.setWithSumFormula(isSumFormulaCell(row.getCell(2)) || isSumFormulaCell(row.getCell(3)) || isSumFormulaCell(row.getCell(4)));

                        // check if total classification
                        boolean reset = false;
                        for(int i = 0; i < classifications.length; i++){
                            if(reset){
                                classifications[i] = null;
                            }else if(classifications[i] != null &&
                                    (name.equalsIgnoreCase("Total " + classifications[i]) || name.equalsIgnoreCase("Net " + classifications[i]))){
                                classifications[i] = null;
                                reset = true;
                                recordDto.setTotalSum(true);
                            }else if(name.equalsIgnoreCase("Total Fund Investments and Co-Investments")){
                                // TODO: refactor total value
                                recordDto.setTotalSum(true);
                            }
                        }
                        records.add(recordDto);
                    }
                }
            }

            rowNum++;
        }
        return records;

    }

    /**
     * Check schedule of investments file header row-by-row.
     * Throw exception if check fails.
     *
     * @param rowNum - row number
     * @param row - row
     */
    private void checkScheduleInvestmentsFileHeader(int rowNum, Row row){
        if (rowNum == 0) {/* ROW = 0 */
            // check file header
            Cell cell = row.getCell(0);
            if (ExcelUtils.isNotEmptyCell(cell) && cell.getCellType() == Cell.CELL_TYPE_STRING &&  !cell.getStringCellValue().equalsIgnoreCase("Tarragon Master Fund LP")) {
                logger.error("File header check failed for 'Schedule of Investments' file. Expected 'Tarragon Master Fund LP', " +
                        "found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
                throw new ExcelFileParseException("File header check failed for 'Schedule of Investments' file. Expected 'Tarragon Master Fund LP', " +
                        "found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
            }
        } else if (rowNum == 1) {/* ROW = 1 */
            // check file header
            Cell cell = row.getCell(0);
            if (ExcelUtils.isNotEmptyCell(cell) && cell.getCellType() == Cell.CELL_TYPE_STRING && !cell.getStringCellValue().startsWith("Schedule of Investments - Tranche ")) {
                logger.error("File header check failed for 'Schedule of Investments' file. Expected 'Schedule of Investments - Tranche A(or B)', " +
                        "found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
                throw new ExcelFileParseException("File header check failed for 'Schedule of Investments' file. Expected 'Schedule of Investments - Tranche A(or B)', " +
                        "found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
            }
        } else if (rowNum == 2) {/* ROW = 2 */
            // report date check
            Date reportDate = null;
            try {
                Cell cell = row.getCell(0);
                reportDate = cell.getDateCellValue();
                if(reportDate != null ){

                    // TODO: check report date
                    return;
                }
            }catch (Exception ex){
                //do nothing, will be logged and thrown
            }
            logger.error("File header check failed for 'Schedule of Investments' file. Report date '" + reportDate + "' could not be parsed. ");
            throw new ExcelFileParseException("File header check failed for 'Schedule of Investments' file. Report date '" + reportDate + "' could not be parsed. ");

        } else if (rowNum == 3) {/* ROW = 3 */
            // skip, empty row
        }
    }

    /**
     * Check schedule of investments file table header row-by-row.
     * Throw exception if check fails.
     *
     * @param row - row
     */
    private void checkScheduleInvestmentsTableHeader(Row row){
        Cell cell = row.getCell(1);
        if (ExcelUtils.isEmptyCell(cell) || cell.getCellType() != Cell.CELL_TYPE_STRING || StringUtils.isEmpty(cell.getStringCellValue())
                || !cell.getStringCellValue().equalsIgnoreCase("Investment")) {
            logger.error("Table header check failed for 'Schedule of Investments' file. Expected: 'Investment', found '" +
                    ExcelUtils.getStringValueFromCell(cell) + "'");
            throw new ExcelFileParseException("Table header check failed for 'Schedule of Investments' file. Expected: 'Investment', found '" +
                    ExcelUtils.getStringValueFromCell(cell) + "'");
        }
        cell = row.getCell(2);
        if (ExcelUtils.isEmptyCell(cell) || cell.getCellType() != Cell.CELL_TYPE_STRING || StringUtils.isEmpty(cell.getStringCellValue())
                || !cell.getStringCellValue().equals("Capital Commitments")) {
            logger.error("Table header check failed for 'Schedule of Investments' file. Expected: 'Capital Commitments', " +
                    "found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
            throw new ExcelFileParseException("Table header check failed for 'Schedule of Investments' file. Expected: 'Capital Commitments', " +
                    "found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
        }
        cell = row.getCell(3);
        if (ExcelUtils.isEmptyCell(cell) || cell.getCellType() != Cell.CELL_TYPE_STRING || StringUtils.isEmpty(cell.getStringCellValue())
                || !cell.getStringCellValue().equals("Net Cost")) {
            logger.error("Table header check failed for 'Schedule of Investments' file. Expected: 'Net Cost', found '" +
                    ExcelUtils.getStringValueFromCell(cell) + "'");
            throw new ExcelFileParseException("Table header check failed for 'Schedule of Investments' file. Expected: 'Net Cost', found '" +
                    ExcelUtils.getStringValueFromCell(cell) + "'");
        }

        cell = row.getCell(4);
        if (ExcelUtils.isEmptyCell(cell) || cell.getCellType() != Cell.CELL_TYPE_STRING  || StringUtils.isEmpty(cell.getStringCellValue())
                || !cell.getStringCellValue().equals("Fair Value")) {
            logger.error("Table header check failed for 'Schedule of Investments' file. Expected: 'Fair Value'");
            throw new ExcelFileParseException("Table header check failed for 'Schedule of Investments' file. Expected: 'Fair Value'");
        }
    }

    /**
     * Parse statement of assets, liabilities, and partners capital excel file for specified report id and
     * save parsed data to database.
     *
     * @param filesDto - statement of assets, liabilities, and partners capital file
     * @param reportId - report id
     * @return - file parse result
     * @throws ExcelFileParseException
     */
    private FileUploadResultDto parseStatementAssetsLiabilities(FilesDto filesDto, Long reportId)
    {

        List<ConsolidatedReportRecordDto> sheet1Records = new ArrayList<>();
        List<ConsolidatedReportRecordDto> sheet2Records = new ArrayList<>();
        try {
            /* PARSE EXCEL (RAW) *******************************************************************************/
            // Sheet 1 - Tranche A
            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);
            sheet1Records = parseStatementAssetsLiabilitiesSheetRaw(rowIterator, true);

            List<ConsolidatedReportRecordDto> balanceRecordsSheet1 = new ArrayList<>();
            List<ConsolidatedReportRecordDto> operationsRecordsSheet1 = new ArrayList<>();
            for(ConsolidatedReportRecordDto recordDto: sheet1Records){
                if(recordDto.getClassifications() != null &&
                        (recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partners' Capital") ||
                                recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partner's Capital")) ||
                        recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partners Capital")){
                    recordDto.getClassifications()[0] = null;
                    balanceRecordsSheet1.add(recordDto);
                }else if(recordDto.getClassifications() != null &&
                        recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Operations")){
                    recordDto.getClassifications()[0] = null;
                    operationsRecordsSheet1.add(recordDto);
                }else{
                    logger.error("[Tranche A] Record '" + recordDto.getName() + "' is missing type header: " +
                            "'Consolidated Statement of Assets, Liabilities and Partners Capital' or 'Consolidated Statement of Operations'.");
                    throw new ExcelFileParseException("[Tranche A] Record '" + recordDto.getName() + "' is missing type header: " +
                            "'Consolidated Statement of Assets, Liabilities and Partners Capital' or 'Consolidated Statement of Operations'.");
                }
            }

            // Sheet 2 - Tranche B
            rowIterator = getRowIterator(filesDto, 1);
            sheet2Records = parseStatementAssetsLiabilitiesSheetRaw(rowIterator, false);

            List<ConsolidatedReportRecordDto> balanceRecordsSheet2 = new ArrayList<>();
            List<ConsolidatedReportRecordDto> operationsRecordsSheet2 = new ArrayList<>();
            for(ConsolidatedReportRecordDto recordDto: sheet2Records){
                if(recordDto.getClassifications() != null &&
                        (recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partners' Capital") ||
                                recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partner's Capital") ||
                                recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partners Capital"))){
                    recordDto.getClassifications()[0] = null;
                    balanceRecordsSheet2.add(recordDto);
                }else if(recordDto.getClassifications() != null &&
                        recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Operations")){
                    recordDto.getClassifications()[0] = null;
                    operationsRecordsSheet2.add(recordDto);
                }else{
                    logger.error("[Tranche B]Record '" + recordDto.getName() + "' is missing type header: " +
                            "'Consolidated Statement of Assets, Liabilities and Partners Capital' or 'Consolidated Statement of Operations'.");
                    throw new ExcelFileParseException("[Tranche B]Record '" + recordDto.getName() + "' is missing type header: " +
                            "'Consolidated Statement of Assets, Liabilities and Partners Capital' or 'Consolidated Statement of Operations'.");
                }
            }

            //printRecords(sheet1Records);
            //System.out.println("---------------------------------------------------------------------------\n");
            //printRecords(sheet2Records);

            /* NORMALIZE TEXT FIELDS ********************************************************************************/
            normalizeTextFields(balanceRecordsSheet1);
            normalizeTextFields(balanceRecordsSheet2);
            normalizeTextFields(operationsRecordsSheet1);
            normalizeTextFields(operationsRecordsSheet2);

            /**/
            checkHeaderClosingTotalSumFormat(balanceRecordsSheet1, null, "[Tranche A] ");
            checkHeaderClosingTotalSumFormat(balanceRecordsSheet2, null, "[Tranche B] ");
            checkHeaderClosingTotalSumFormat(operationsRecordsSheet1, null, "[Tranche A] ");
            checkHeaderClosingTotalSumFormat(operationsRecordsSheet2, null, "[Tranche B] ");


            /* CHECK SUMS/TOTALS ********************************************************************************/

            // Balance (Assets, Liabilities, Partners Capital)
            checkTotalSumsGeneric(balanceRecordsSheet1, 7, null, 1); // TODO: Total record name ???
            checkTotalSumsGeneric(balanceRecordsSheet2, 7, null, 2);

            checkTotalSumsStatementAssetsLiabilities(balanceRecordsSheet1, "[Tranche A]");
            checkTotalSumsStatementAssetsLiabilities(balanceRecordsSheet2, "[Tranche B]");

            // Operations
            checkTotalSumsGeneric(operationsRecordsSheet1, 7, getStatementOperationsTotalRecordName(operationsRecordsSheet1), 1);
            checkTotalSumsGeneric(operationsRecordsSheet2, 7, getStatementOperationsTotalRecordName(operationsRecordsSheet2), 2);

            //checkTotalSumsStatementAssetsLiabilities(updatedBalanceRecordsTrancheA);
            //checkTotalSumsStatementAssetsLiabilities(updatedBalanceRecordsTrancheB);

            /* CHECK ENTITIES AND ASSEMBLE **********************************************************************/
            // BALANCE
            // sheet 1 - Tranche A
            List<ReportingPEStatementBalance> entities1 = this.statementBalanceService.assembleList(balanceRecordsSheet1, 1, reportId); // TODO: tranche type constant !!!
            // sheet 2 - Tranche B
            List<ReportingPEStatementBalance> entities2 = this.statementBalanceService.assembleList(balanceRecordsSheet2, 2, reportId); // TODO: tranche type constant !!!

            // OPERATIONS
            // sheet 1 - Tranche A
            List<ReportingPEStatementOperations> operationsEntities1 = this.statementOperationsService.assembleList(operationsRecordsSheet1, 1, reportId); // TODO: tranche type constant !!!
            // sheet 2 - Tranche B
            List<ReportingPEStatementOperations> operationsEntities2 = this.statementOperationsService.assembleList(operationsRecordsSheet2, 2, reportId); // TODO: tranche type constant !!!

            /* SAVE TO DB **************************************************************************************/
            // BALANCE
            boolean savedBalance = this.statementBalanceService.save(entities1);
            if(savedBalance) {
                savedBalance = this.statementBalanceService.save(entities2);
            }
            if(!savedBalance){
                // TODO: rollback? or transactional?

                logger.error("Error saving 'Schedule of Investments' file data to database (statement of balance)");
                return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error saving 'Schedule of Investments' file data to database (statement of balance)", "");
            }

            // OPERATIONS
            boolean savedOperations = this.statementOperationsService.save(operationsEntities1);
            if(savedOperations) {
                savedOperations = this.statementOperationsService.save(operationsEntities2);
            }

            if(savedOperations){
                logger.info("Successfully parsed 'Statement of Assets, Liabilities and Partners Capital' file");
                return new FileUploadResultDto(ResponseStatusType.SUCCESS, "", "Successfully processed the file - Statement of Assets, Liabilities and Partners Capital", "");
            }else{
                // TODO: rollback? or transactional?

                logger.error("Error saving 'Schedule of Investments' file data to database (statement of operations)");
                return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error saving 'Schedule of Investments' file data to database (statement of operations)", "");
            }
        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Statement of Assets, Liabilities and Partners Capital' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Statement of Assets, Liabilities and Partners Capital' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error processing 'Statement of Assets, Liabilities and Partners Capital' file'", "");
        }
    }

    private String getStatementOperationsTotalRecordName(List<ConsolidatedReportRecordDto> records){
        String totalRecordName = "Net increase (decrease) in partners' capital resulting from operations";
        if(records != null){
            for(ConsolidatedReportRecordDto record: records){
                if(isStatementOperationsTotalRecordName(record.getName())){
                    // total record
                    return record.getName().trim();
                }
            }
        }
        return totalRecordName;
    }

    private boolean isStatementOperationsTotalRecordName(String name){
        return StringUtils.isNotEmpty(name) &&
                (name.equalsIgnoreCase("Net increase (decrease) in partners' capital resulting from operations") ||
                        name.equalsIgnoreCase("Net increase (decrease) in partner's capital resulting from operations") ||
                        name.equalsIgnoreCase("Net increase (decrease) in partners capital resulting from operations"));
    }

    /**
     * Iterate over rows of Statement of assets, liabilities, and partners capital excel file (specified by row iterator)
     * and return a list of corresponding DTOs. Each DTO represents a data row in excel file.
     *
     * @param rowIterator - excel file row iterator
     * @return - list of DTOs
     */
    private List<ConsolidatedReportRecordDto> parseStatementAssetsLiabilitiesSheetRaw(Iterator<Row> rowIterator, boolean isTrancheA){

        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        int rowNum = 0;
        String[] classifications = new String[5]; // TODO: size? dynamic?
        while (rowIterator.hasNext()) { // each row
            Row row = rowIterator.next();
            if(isTrancheA && rowNum <= 5){
                checkStatementAssetsLiabilitiesTrancheATableHeader(row, rowNum);
            }else if(!isTrancheA && rowNum <= 2){
                checkStatementAssetsLiabilitiesTrancheBTableHeader(row, rowNum);
            }else{ /* Rows with data, not headers */
                Cell cell = row.getCell(0);
                if(ExcelUtils.getStringValueFromCell(cell) != null){
                    if(cell.getStringCellValue().trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partners' Capital") ||
                            cell.getStringCellValue().trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partner's Capital") ||
                            cell.getStringCellValue().trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partners Capital") ||
                            cell.getStringCellValue().trim().equalsIgnoreCase("Consolidated Statement of Operations")){
                        // TODO: ?
                        classifications = new String[5];
                    }
                    if(ExcelUtils.isEmptyCell(row.getCell(2)) && ExcelUtils.isEmptyCell(row.getCell(4)) &&
                            ExcelUtils.isEmptyCell(row.getCell(6)) && ExcelUtils.isEmptyCell(row.getCell(8)) &&
                            ExcelUtils.isEmptyCell(row.getCell(10)) && ExcelUtils.isEmptyCell(row.getCell(12)) &&
                            ExcelUtils.isEmptyCell(row.getCell(14))){
                        // classifications
                        for(int i = 0; i < classifications.length; i++){
                            if(classifications[i] == null){
                                String classification = cell.getStringCellValue();
                                if(StringUtils.isNotEmpty(classification) && classification.charAt(classification.length() - 1) == ':'){
                                    classification = classification.substring(0, classification.length() - 1);
                                }
                                classifications[i] = classification;
                                break;
                            }
                        }

                    }else{
                        // values
                        String name = cell.getStringCellValue();
                        Double[] values = new Double[7];
                        values[0] = ExcelUtils.getDoubleValueFromCell(row.getCell(2));
                        if(isTrancheA){
                            values[1] = ExcelUtils.getDoubleValueFromCell(row.getCell(4));
                            values[2] = ExcelUtils.getDoubleValueFromCell(row.getCell(6));
                            values[3] = ExcelUtils.getDoubleValueFromCell(row.getCell(8));
                            values[4] = ExcelUtils.getDoubleValueFromCell(row.getCell(10));
                            values[5] = ExcelUtils.getDoubleValueFromCell(row.getCell(12));
                            values[6] = ExcelUtils.getDoubleValueFromCell(row.getCell(14));
                        }else {
                            values[1] = null;
                            values[2] = null;
                            values[3] = ExcelUtils.getDoubleValueFromCell(row.getCell(4));
                            values[4] = ExcelUtils.getDoubleValueFromCell(row.getCell(6));
                            values[5] = ExcelUtils.getDoubleValueFromCell(row.getCell(8));
                            values[6] = ExcelUtils.getDoubleValueFromCell(row.getCell(10));
                        }

                        ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto();
                        recordDto.setName(name);
                        recordDto.setValues(values);
                        // classifications
                        recordDto.setClassifications(Arrays.copyOf((String[]) classifications, 5));
                        // total sum
                        recordDto.setWithSumFormula(isSumFormulaCell(row.getCell(2)));

                        boolean reset = false;
                        for(int i = 0; i < classifications.length; i++){
                            if(reset){
                                classifications[i] = null;
                            }else if(classifications[i] != null &&
                                    (name.equalsIgnoreCase("total " + classifications[i]) ||
                                            (name + ":").equalsIgnoreCase("total " + classifications[i]) ||
                                            (name).equalsIgnoreCase("total " + classifications[i] + ":") ||
                                            name.equalsIgnoreCase("net " + classifications[i]) ||
                                            (name + ":").equalsIgnoreCase("net " + classifications[i]) ||
                                            (name).equalsIgnoreCase("net " + classifications[i] + ":") ||
                                            ((name.startsWith("Total") || name.startsWith("Net")) && name.equalsIgnoreCase(classifications[i])) ||
                                            isStatementOperationsTotalRecordName(name))){
                                classifications[i] = null;
                                reset = true;
                                recordDto.setTotalSum(true);
                            }
                        }
                        records.add(recordDto);
                    }

                }else if(ExcelUtils.isNotEmptyCell(row.getCell(2)) || ExcelUtils.isNotEmptyCell(row.getCell(4)) ||
                        ExcelUtils.isNotEmptyCell(row.getCell(6)) || ExcelUtils.isNotEmptyCell(row.getCell(8)) ||
                        ExcelUtils.isNotEmptyCell(row.getCell(10)) || ExcelUtils.isNotEmptyCell(row.getCell(12)) |
                        ExcelUtils.isNotEmptyCell(row.getCell(14))){
                    logger.error("Expected text value for record name, found : '" + ExcelUtils.getTextValueFromAnyCell(cell) + "'");
                    throw new ExcelFileParseException("Expected text value for record name, found : '" + ExcelUtils.getTextValueFromAnyCell(cell) + "'");
                }

            }
            rowNum++;
        }
        return records;

    }

    /**
     * Check statement of assets, liabilities, and partners capital file table header row-by-row (tranche A).
     * Throw exception if check fails.
     *
     * @param row - row
     */
    private void checkStatementAssetsLiabilitiesTrancheATableHeader(Row row, int rowNum){
        if(rowNum == 1){
            Cell cell = row.getCell(2);
            if (!ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "Tarragon Master Fund LP")) {
                logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'Tarragon Master Fund LP', found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'Tarragon Master Fund LP', found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
            }
        }else if(rowNum == 2){
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(14), "NICK Master")){
                logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'NICK Master', found '" + ExcelUtils.getStringValueFromCell(row.getCell(14)) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'NICK Master', found '" + ExcelUtils.getStringValueFromCell(row.getCell(14)) + "'");
            }
        }else if (rowNum == 3){
            if (!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10), "NICK Master")) {
                logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'NICK Master', found '"  + ExcelUtils.getStringValueFromCell(row.getCell(10)) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'NICK Master', found '"  + ExcelUtils.getStringValueFromCell(row.getCell(10)) + "'");
            }
            if (!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(14), "Fund Ltd.'s Share")) {
                logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'Fund Ltd.'s Share', foudn '" + ExcelUtils.getStringValueFromCell(row.getCell(14)) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'Fund Ltd.'s Share', foudn '" + ExcelUtils.getStringValueFromCell(row.getCell(14)) + "'");
            }
        }else if (rowNum == 4){
            boolean tableHeaders = ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(4), "Tarragon GP") &&
                    ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(6), "NICK Master") &&
                    ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10), "Fund Ltd.'s") &&
                    ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(12), "Consolidation") &&
                    ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(14), "of Tranche A");
            if (!tableHeaders) {
                logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Tarragon GP', 'NICK Master', 'Fund Ltd.'s', 'Consolidation', 'of Tranche A'");
                throw new ExcelFileParseException("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Tarragon GP', 'NICK Master', 'Fund Ltd.'s', 'Consolidation', 'of Tranche A'");
            }
        }else if (rowNum == 5){
            boolean tableHeaders = ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2),"Total*") &&
                    ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(4), "LLC's Share") &&
                    ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(6), "Fund Ltd.'s Share") &&
                    ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8), "Tarragon LP") &&
                    ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10), "Share of Total") &&
                    ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(12), "Adjustments") &&
                    ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(14), "Consolidated");
            if(!tableHeaders){
                logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Total*', 'LLC's Share', 'Fund Ltd.'s Share', 'Tarragon LP', 'Share of Total', 'Adjustments', 'Consolidated'.");
                throw new ExcelFileParseException("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Total*', 'LLC's Share', 'Fund Ltd.'s Share', 'Tarragon LP', 'Share of Total', 'Adjustments', 'Consolidated'.");
            }
        }
    }

    /**
     * Check statement of assets, liabilities, and partners capital file table header row-by-row (tranche B).
     * Throw exception if check fails.
     *
     * @param row - row
     */
    private void checkStatementAssetsLiabilitiesTrancheBTableHeader(Row row, int rowNum){
        if(rowNum == 1){
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), "Tarragon")){
                logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'Tarragon', found '" + ExcelUtils.getStringValueFromCell(row.getCell(2)) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'Tarragon', found '" + ExcelUtils.getStringValueFromCell(row.getCell(2)) + "'");
            }else if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8), "Consolidation")){
                logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'Consolidation', found '" + ExcelUtils.getStringValueFromCell(row.getCell(8)) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'Consolidation', found '" + ExcelUtils.getStringValueFromCell(row.getCell(8)) + "'");
            }else if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10), "Tranche B")){
                logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'Tranche B', found '" + ExcelUtils.getStringValueFromCell(row.getCell(10)) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected: 'Tranche B', found '" + ExcelUtils.getStringValueFromCell(row.getCell(10)) + "'");
            }
        }else if(rowNum == 2){
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), "Master Fund LP")){
                logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Master Fund LP', found '" + ExcelUtils.getStringValueFromCell(row.getCell(2)) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Master Fund LP', found '" + ExcelUtils.getStringValueFromCell(row.getCell(2)) + "'");
            }else if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(4), "Tarragon LP")){
                logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Tarragon LP', found '" + ExcelUtils.getStringValueFromCell(row.getCell(4)) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Tarragon LP', found '" + ExcelUtils.getStringValueFromCell(row.getCell(4)) + "'");
            }else if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(6), "Total")){
                logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Total', found '" + ExcelUtils.getStringValueFromCell(row.getCell(6)) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Total', found '" + ExcelUtils.getStringValueFromCell(row.getCell(6)) + "'");
            }else if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8), "Adjustments")){
                logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Adjustments', found '" + ExcelUtils.getStringValueFromCell(row.getCell(8)) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Adjustments', found '" + ExcelUtils.getStringValueFromCell(row.getCell(8)) + "'");
            }else if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10), "Consolidated")){
                logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Consolidated', found '" + ExcelUtils.getStringValueFromCell(row.getCell(10)) + "'");
                throw new ExcelFileParseException("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file. " +
                        "Expected 'Consolidated', found '" + ExcelUtils.getStringValueFromCell(row.getCell(10)) + "'");
            }
        }
    }

    /**
     * Check statement of assets, liabilities, and partners capital excel file total sums, e.g. total assets
     * has to equal to total liabilities and partners capital.
     * Throw exception if check fails.
     *
     * @param records - records
     */
    private void checkTotalSumsStatementAssetsLiabilities(List<ConsolidatedReportRecordDto> records, String trancheName){
        Double[] assetsTotal = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        Double[] liabilitiesAndPartnersCapitalTotal = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        Double[] totals = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        for(ConsolidatedReportRecordDto recordDto: records){
            if(recordDto.getName().startsWith("Total ") && recordDto.hasClassification(recordDto.getName().substring(5).trim())){
                // is total value
            }else if(recordDto.hasClassification("ASSETS") || recordDto.getName().equalsIgnoreCase("ASSETS")){ // TODO: final strings
                ArrayUtils.addArrayValues(assetsTotal, recordDto.getValues());
                ArrayUtils.addArrayValues(totals, recordDto.getValues());
            }else if(recordDto.hasClassification("LIABILITIES") || recordDto.getName().equalsIgnoreCase("LIABILITIES")){// TODO: final strings
                ArrayUtils.addArrayValues(liabilitiesAndPartnersCapitalTotal, recordDto.getValues());
                ArrayUtils.addArrayValues(totals, recordDto.getValues());
            }else if(isPartnersCapital(recordDto)){
                ArrayUtils.addArrayValues(liabilitiesAndPartnersCapitalTotal, recordDto.getValues());
                ArrayUtils.addArrayValues(totals, recordDto.getValues());
            }else if(isLiabilitiesAndPartnersCapital(recordDto)){
                ArrayUtils.addArrayValues(liabilitiesAndPartnersCapitalTotal, recordDto.getValues());
                ArrayUtils.addArrayValues(totals, recordDto.getValues());
            }else{
                logger.error(trancheName + "Could not determine type for record '" + recordDto.getName() + "'. Must be one of the following: " +
                        "Assets, Liabilities, Partners' Capital, Liabilities and Partners' Capital");
                throw new ExcelFileParseException(trancheName + "Could not determine type for record '" + recordDto.getName() + "'. Must be one of the following: " +
                        "Assets, Liabilities, Partners' Capital, Liabilities and Partners' Capital");
            }
        }

        // Check:
        // ASSETS = LIABILITIES + PARTNERS CAPITAL
        for(int i = 0; i < assetsTotal.length; i++){
            if(assetsTotal[i].doubleValue() != liabilitiesAndPartnersCapitalTotal[i].doubleValue() ){
                logger.error(trancheName + "Error checking total sums for values #" + (i + 1) +
                        ": assets=" + assetsTotal[i].doubleValue() + ", liabilities and partners' capital =" + liabilitiesAndPartnersCapitalTotal[i].doubleValue());
                throw new ExcelFileParseException(trancheName + "Error checking total sums for values #" + (i + 1) +
                        ": assets=" + assetsTotal[i].doubleValue() + ", liabilities and partners' capital =" + liabilitiesAndPartnersCapitalTotal[i].doubleValue());
            }
        }
    }

    // TODO: move to PEStatementBalanceServiceImpl
    private boolean isPartnersCapital(ConsolidatedReportRecordDto recordDto){
        return recordDto.hasClassification("PARTNERS' CAPITAL") ||
                recordDto.hasClassification("PARTNER'S CAPITAL") ||
                recordDto.hasClassification("PARTNERS CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("PARTNERS' CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("PARTNER'S CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("PARTNERS CAPITAL");
    }
    private boolean isLiabilitiesAndPartnersCapital(ConsolidatedReportRecordDto recordDto){
        return recordDto.hasClassification("LIABILITIES AND PARTNERS' CAPITAL") ||
                recordDto.hasClassification("LIABILITIES AND PARTNER'S CAPITAL") ||
                recordDto.hasClassification("LIABILITIES AND PARTNERS CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("LIABILITIES AND PARTNERS' CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("LIABILITIES AND PARTNER'S CAPITAL") ||
                recordDto.getName().equalsIgnoreCase("LIABILITIES AND PARTNERS CAPITAL");
    }

    private FileUploadResultDto parseStatementCashFlows(FilesDto filesDto, Long reportId){
        try {
            /* PARSE EXCEL (RAW) *******************************************************************************/
            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);
            List<ConsolidatedReportRecordDto> records = parseStatementCashFlowsRaw(rowIterator);
            //printRecords(records);

            /* NORMALIZE TEXT FIELDS ********************************************************************************/
            normalizeTextFields(records);

            /* CHECK FORMAT *****************************************************************************************/
            for(ConsolidatedReportRecordDto recordDto: records) {
                if (!isStatementCashFlowsSpecialName(recordDto.getName()) && !recordDto.hasNonEmptyClassification()) {
                    logger.error("Record '" + recordDto.getName() + "' does not have any matching classification/header. Check for required indentations.");
                    throw new ExcelFileParseException("Record '" + recordDto.getName() + "' does not have any matching classification/header. Check for required indentations.");
                }
            }

            /* CHECK SUMS/TOTALS ***********************************************************************************/
            checkTotalSumsGeneric(records, 3, "Net increase (decrease) in cash and cash equivalents", 0);
            checkSumsStatementCashFlows(records);


            /* CHECK ENTITIES AND ASSEMBLE *************************************************************************/
            List<ReportingPEStatementCashflows> entities = this.statementCashflowsService.assembleList(records, reportId); // TODO: tranche type constant !!!

            /* SAVE TO DB ******************************************************************************************/
            boolean saved = this.statementCashflowsService.save(entities);

            if(saved){
                logger.info("Successfully parsed 'Statement of Cashflows' file");
                return new FileUploadResultDto(ResponseStatusType.SUCCESS, "", "Successfully processed the file - Statement of Cashflows", "");
            }else{
                logger.error("Error saving 'Statement of Cashflows' file parsed data into database");
                return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Statement of Cash flows' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Statement of Cash flows' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error processing 'Statement of Cashflows' file'", "");
        }
    }

    private List<ConsolidatedReportRecordDto> parseStatementCashFlowsRaw(Iterator<Row> rowIterator){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        int rowNum = 0;
        String[] classifications = new String[5]; // TODO: size?
        Integer[] indentations = new Integer[5]; // TODO: size?
        while (rowIterator.hasNext()) { // each row
            Row row = rowIterator.next();
            if(rowNum <= 2){
                checkStatementCashFlowsFileHeader(row, rowNum);
            }else{
                if(ExcelUtils.isNotEmptyCell(row.getCell(0))){
                    String name = row.getCell(0).getStringCellValue();
                    Double trancheAValue = ExcelUtils.getDoubleValueFromCell(row.getCell(2));
                    Double trancheBValue = ExcelUtils.getDoubleValueFromCell(row.getCell(4));
                    Double totalValue = ExcelUtils.getDoubleValueFromCell(row.getCell(6));

                    int indentation = row.getCell(0).getCellStyle().getIndention();
                    if(trancheAValue != null || trancheBValue != null || totalValue != null){
                        // values
                        ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(10, 3);
                        recordDto.setName(name);
                        Double[] values = {trancheAValue, trancheBValue, totalValue};
                        recordDto.setWithSumFormula(isSumFormulaCell(row.getCell(2)));
                        recordDto.setValues(values);
                        // classifications
                        recordDto.setClassifications(Arrays.copyOf((String[]) classifications, 10));

                        boolean isTotalClassificationRecord = false;
                        for (int i = 0; i < indentations.length; i++) {
                            if(isTotalClassificationRecord || indentations[i] == null || indentations[i] >= indentation){
                                recordDto.clearClassification(i);
                            }else if(recordDto.getName().equalsIgnoreCase("Net " + classifications[i])) {
                                isTotalClassificationRecord = true;
                            }
                        }
                        records.add(recordDto);
                    }else{
                        // classifications
                        boolean reset = false;
                        for(int i = 0; i < classifications.length; i++){
                            if(reset){
                                classifications[i] = null;
                                indentations[i] = null;
                            }else if(classifications[i] == null || indentations[i] == indentation){
                                if(StringUtils.isNotEmpty(name) && name.charAt(name.length() - 1) == ':'){
                                    classifications[i] = name.substring(0, name.length() - 1);
                                }else {
                                    classifications[i] = name;
                                }

                                indentations[i] = indentation;
                                reset = true;
                            }
                        }
                    }
                }else{
                    // empty cell, skip
                }
            }
            rowNum++;
        }
        return records;
    }

    /**
     * Check statement of cash flows file header row-by-row.
     * Throw exception if check fails.
     *
     * @param rowNum - row number
     * @param row - row
     */
    private void checkStatementCashFlowsFileHeader(Row row, int rowNum){
        if(rowNum == 0){
            Cell cell = row.getCell(0);
            if (!ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "Tarragon LP")) {
                logger.error("File header check failed for 'Statement of Cashflows' file. Expected 'Tarragon LP', " +
                        "found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
                throw new ExcelFileParseException("File header check failed for 'Statement of Cashflows' file. Expected 'Tarragon LP', " +
                        "found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
            }
        }else if(rowNum == 1){
            Cell cell = row.getCell(0);
            if (!ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "Consolidated Statement of Cash Flows for NICK Master Fund Ltd.")) {
                logger.error("File header check failed for 'Statement of Cashflows' file: row=2, cell=1. " +
                        "Expected: 'Consolidated Statement of Cash Flows for NICK Master Fund Ltd.', found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
                throw new ExcelFileParseException("File header check failed for 'Statement of Cashflows' file: row=2, cell=1. " +
                        "Expected: 'Consolidated Statement of Cash Flows for NICK Master Fund Ltd.', found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
            }
        }else if(rowNum == 2){
            // skip, empty row
        }
    }

    /**
     * Return true if the given name is a special for statement of cash flows,
     * e.g. total sum record name, does not have headers, etc.
     *
     * @param name - name
     * @return - true/false
     */
    private boolean isStatementCashFlowsSpecialName(String name){
        return name.equalsIgnoreCase("Cash and cash equivalents - beginning of period") ||
                name.equalsIgnoreCase("Cash and cash equivalents - end of period") ||
                name.equalsIgnoreCase("Net increase (decrease) in cash and cash equivalents");
    }

    /**
     * Check statement of cash flows total sums, e.g. 'Cash and cash equivalents - end of period' value, etc.
     *
     * @param records - cash flows
     */
    private void checkSumsStatementCashFlows(List<ConsolidatedReportRecordDto> records){
        //List<ConsolidatedReportRecordDto> resultList = new ArrayList<>();
        Double[] totals = new Double[]{0.0, 0.0, 0.0};
        if(records != null){
            Double[] previousPeriod = null;
            for(ConsolidatedReportRecordDto record: records){
                if(record.getName().equalsIgnoreCase("Cash and cash equivalents - beginning of period")){
                    previousPeriod = record.getValues();

                    PeriodicDataDto periodicDataDtoTrancheA = this.periodicDataService.getCashflowBeginningPeriod(1);
                    PeriodicDataDto periodicDataDtoTrancheB = this.periodicDataService.getCashflowBeginningPeriod(2);

                    if(periodicDataDtoTrancheA == null || periodicDataDtoTrancheB == null){
                        throw new ExcelFileParseException("Missing beginning of period data for '" + record.getName() + "'");
                    }

                    // check tranche A
                    if(periodicDataDtoTrancheA.getValue() != null && record.getValues() != null && record.getValues()[0] != null &&
                            periodicDataDtoTrancheA.getValue().doubleValue() != record.getValues()[0].doubleValue()){
                        logger.error("Error checking beginning of period tranche A for '" + record.getName() +
                                "': expected " + periodicDataDtoTrancheA.getValue().doubleValue() + ", found " + record.getValues()[0].doubleValue());
                        throw new ExcelFileParseException("Error checking beginning of period tranche A for '" + record.getName() +
                                "': expected " + periodicDataDtoTrancheA.getValue().doubleValue() + ", found " + record.getValues()[0].doubleValue());

                    }
                    // check tranche B
                    if(periodicDataDtoTrancheB.getValue() != null && record.getValues() != null && record.getValues()[1] != null &&
                            periodicDataDtoTrancheB.getValue().doubleValue() != record.getValues()[1].doubleValue()){
                        logger.error("Error checking beginning of period tranche B for '" + record.getName() +
                                "': expected " + periodicDataDtoTrancheB.getValue().doubleValue() + ", found " + record.getValues()[1].doubleValue());
                        throw new ExcelFileParseException("Error checking beginning of period tranche B for '" + record.getName() +
                                "': expected " + periodicDataDtoTrancheB.getValue().doubleValue() + ", found " + record.getValues()[1].doubleValue());

                    }

                    //resultList.add(record);
                }else if(record.getName().equalsIgnoreCase("Cash and cash equivalents - end of period")){
                    if(previousPeriod == null){
                        logger.error("Error checking totals for record '" + record.getName() +
                                "': missing beginning of period data");
                        throw new ExcelFileParseException("Error checking totals for record '" + record.getName() +
                                "': missing beginning of period data");
                    }else if((totals[0] + previousPeriod[0]) != record.getValues()[0]){
                        logger.error("Error checking totals for record '" + record.getName() +
                                "' for values #1: expected " + (totals[0] + previousPeriod[0]) + ", found " + record.getValues()[0]);
                        throw new ExcelFileParseException("Error checking totals for record '" + record.getName() +
                                "' for values #1: expected " + (totals[0] + previousPeriod[0]) + ", found " + record.getValues()[0]);
                    }else if((totals[1] + previousPeriod[1]) != record.getValues()[1]){
                        logger.error("Error checking totals for record '" + record.getName() +
                                "' for values #1: expected " + (totals[1] + previousPeriod[1]) + ", found " + record.getValues()[1]);
                        throw new ExcelFileParseException("Error checking totals for record '" + record.getName() +
                                "' for values #1: expected " + (totals[1] + previousPeriod[1]) + ", found " + record.getValues()[1]);
                    }

                    //resultList.add(record);
                }else if(record.getName().equalsIgnoreCase("Net increase (decrease) in cash and cash equivalents")){

                    // check totals
                    if(NumberUtils.isNotEqualValues(totals[0], record.getValues()[0])){
                        logger.error("Error checking totals for record '" + record.getName() +
                                "' for values #1: expected " + totals[0] + ", found " + record.getValues()[0]);
                        throw new ExcelFileParseException("Error checking totals for record '" + record.getName() +
                                "' for values #1: expected " + totals[0] + ", found " + record.getValues()[0]);
                    }else if(NumberUtils.isNotEqualValues(totals[1], record.getValues()[1])){
                        logger.error("Error checking totals for record '" + record.getName() +
                                "' for values #2: expected " + totals[1] + ", found " + record.getValues()[1]);
                        throw new ExcelFileParseException("Error checking totals for record '" + record.getName() +
                                "' for values #2: expected " + totals[1] + ", found " + record.getValues()[1]);
                    }
                    //resultList.add(record);
                }else{

                    // check total = trancheA + trancheB
                    if(record.getValues() != null && record.getValues()[0] != null && record.getValues()[1] != null && record.getValues()[2] != null){
                        if((record.getValues()[0].doubleValue() + record.getValues()[1].doubleValue()) != record.getValues()[2].doubleValue()){
                            logger.error("Error checking totals for record '" + record.getName() + "': total column value is " +
                                    record.getValues()[2].doubleValue() + ", expected total is " + (record.getValues()[0].doubleValue() + record.getValues()[1].doubleValue()));
                            throw new ExcelFileParseException("Error checking totals for record '" + record.getName() + "': total column value is " +
                                    record.getValues()[2].doubleValue() + ", expected total is " + (record.getValues()[0].doubleValue() + record.getValues()[1].doubleValue()));
                        }
                    }

                    if(!record.getName().startsWith("Net ") || !record.hasClassification(record.getName().substring(3).trim())){
                        ArrayUtils.addArrayValues(totals, record.getValues());
                    }
                    //resultList.add(record);
                }
            }

        }

        //return records;
        //return resultList;
    }

    private FileUploadResultDto parseStatementChanges(FilesDto filesDto, Long reportId){
        try {

            /* PARSE EXCEL (RAW) *******************************************************************************/
            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);
            List<ConsolidatedReportRecordDto> records = parseStatementChangesRaw(rowIterator);
            //printRecords(records);

            /* NORMALIZE TEXT FIELDS ********************************************************************************/
            normalizeTextFields(records);

            /* CHECK SUMS/TOTALS ********************************************************************************/
            //List<ConsolidatedReportRecordDto> updatedRecords = checkTotalSumsGeneric(records, 3, null);
            List<ConsolidatedReportRecordDto> updatedRecords = checkSumsStatementChanges(records);

            /* CHECK ENTITIES AND ASSEMBLE **********************************************************************/
            List<ReportingPEStatementChanges> entities = this.statementChangesService.assembleList(updatedRecords, reportId); // TODO: tranche type constant !!!

            /* SAVE TO DB **************************************************************************************/
            boolean saved = this.statementChangesService.save(entities);

            if(saved){
                logger.info("Successfully parsed 'Statement of Changes in Partners Capital' file");
                return new FileUploadResultDto(ResponseStatusType.SUCCESS, "", "Successfully processed the file - Statement of Changes in Partners Capital", "");
            }else{
                logger.error("Error saving 'Statement of Changes in Partners Capital' file parsed data into database");
                return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Statement of Changes in Partners Capital' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Statement of Changes in Partners Capital' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error processing 'Statement of Changes in Partners Capital' file'", "");
        }
    }

    private void checkStatementChangesFileHeaders(Row row, int rowNum){
        if(rowNum == 0){
            Cell cell = row.getCell(0);
            if (!ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "Tarragon LP")) {
                logger.error("File header check failed for 'Statement of Changes in Partners Capital' file. " +
                        "Expected 'Tarragon LP', found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
                throw new ExcelFileParseException("File header check failed for 'Statement of Changes in Partners Capital' file. " +
                        "Expected 'Tarragon LP', found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
            }
        }else if(rowNum == 1){
            Cell cell = row.getCell(0);
            String header =  "Consolidated Statement of Changes in Partner’s Capital for NICK Master Fund Ltd.";
            if (!ExcelUtils.isCellStringValueEqualIgnoreCase(cell, header)) {
                logger.error("File header check failed for 'Statement of Changes in Partners Capital' file. " +
                        "Expected '" + header + "', found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
                throw new ExcelFileParseException("File header check failed for 'Statement of Changes in Partners Capital' file. " +
                        "Expected '" + header + "', found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
            }
        }else if(rowNum == 2){
            // TODO: check date?

        }
        else if(rowNum == 3){
            // empty rows are skipped
            //checkStatementChangesTableHeader(row, indicesWithValues);
            Cell cell = row.getCell(5);
            if (!ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "Net Investment Income (Loss)")) {
                //logger.error("Table header check failed for 'Statement of Changes in Partners Capital' file. " +
                //        "Expected 'Net Investment Income (Loss)', found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
                //throw new ExcelFileParseException("Table header check failed for 'Statement of Changes in Partners Capital' file. " +
                //        "Expected 'Net Investment Income (Loss)', found '" + ExcelUtils.getStringValueFromCell(cell) + "'");
            }
        }
    }

    private List<ConsolidatedReportRecordDto> parseStatementChangesRaw(Iterator<Row> rowIterator){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        int rowNum = 0;
        List<Integer> valueIndices = new ArrayList<>();

        String requiredHeaderVersion1 = "Unrealized Gain (Loss), Net of Related Taxes";
        String requiredHeaderVersion2 = "Unrealized Gain (Loss)";
        boolean requiredHeaderAdded = false;

        while (rowIterator.hasNext()) { // each row
            Row row = rowIterator.next();
            if(rowNum < 4){
                checkStatementChangesFileHeaders(row, rowNum);
            }else if(rowNum == 4){
                int cellNum = 0;
                boolean rowIteratable = true;
                int consecEmptyCellCount = 0;
                while(rowIteratable){
                    Cell cell = row.getCell(cellNum);
                    if(ExcelUtils.getStringValueFromCell(cell) != null){
                        ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(0, 3);
                        recordDto.setName(cell.getStringCellValue());
                        valueIndices.add(cellNum);
                        records.add(recordDto);
                        if(recordDto.getName().equalsIgnoreCase(requiredHeaderVersion1) || recordDto.getName().equalsIgnoreCase(requiredHeaderVersion2)){
                            requiredHeaderAdded = true;
                        }

                        consecEmptyCellCount = 0;
                    }else{
                        consecEmptyCellCount++;
                    }

                    if(consecEmptyCellCount >= 4){ // possible empty columns, but no more than consecutive 4, then end
                        rowIteratable = false;
                    }

                    cellNum++;
                }
            }else if(rowNum > 4){

                // TODO: currency ?? !!!

                Cell cell = row.getCell(0);
                if(ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "tranche A")){
                    setStatementChangesValues(row, 0, valueIndices, records);
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "tranche B")){
                    setStatementChangesValues(row, 1, valueIndices, records);
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "total")){
                    setStatementChangesValues(row, 2, valueIndices, records);
                }else{
                    //
                }
            }

            rowNum++;
        }
        if(!requiredHeaderAdded){
            logger.error("Missing required value: '" + requiredHeaderVersion1 + "' or '" + requiredHeaderVersion2 + "'");
            throw new ExcelFileParseException("Missing required value: '" + requiredHeaderVersion1 + "' or '" + requiredHeaderVersion2 + "'");
        }

        //printRecords(records);
        return records;
    }

    private void setStatementChangesValues(Row row, int valueIndex, List<Integer> cellIndices, List<ConsolidatedReportRecordDto> records){
        int i = 0;
        for(Integer cellIndex: cellIndices){
            Cell valueCell = row.getCell(cellIndex);
            if(valueCell != null && ExcelUtils.getDoubleValueFromCell(valueCell) != null){
                ConsolidatedReportRecordDto recordDto = records.get(i);
                recordDto.getValues()[valueIndex] = valueCell.getNumericCellValue();
                i++;

            }else{
                // TODO: error
            }
        }
    }

    private List<ConsolidatedReportRecordDto> checkSumsStatementChanges(List<ConsolidatedReportRecordDto> records){
        if(records != null){
            for(ConsolidatedReportRecordDto record: records){
                double sum = record.getValues()[0] != null ? record.getValues()[0].doubleValue() : 0.0;
                sum = record.getValues()[1] != null ? sum + record.getValues()[1].doubleValue() : sum;
                double totalValue = record.getValues()[2] != null ? record.getValues()[2].doubleValue() : 0.0;
                if(sum != totalValue){
                    logger.error("Error checking TOTAL for record '" + record.getName() + "' : expected " + sum + ", found " + totalValue);
                    logger.error("Error checking TOTAL for record '" + record.getName() + "' : expected " + sum + ", found " + totalValue);
                }
            }
        }
        return records;
    }

    private FileUploadResultDto parseSingularGeneralLedger(FilesDto filesDto, Long reportId){
        try {

            /* PARSE EXCEL (RAW) *******************************************************************************/
            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);
            List<SingularityGeneralLedgerBalanceRecordDto> records = parseSingularGeneralLedgerRaw(rowIterator);
            //printRecords(records);

            /* CHECK ENTITIES AND ASSEMBLE **********************************************************************/
            List<ReportingHFGeneralLedgerBalance> entities = this.generalLedgerBalanceService.assembleList(records, reportId);

            /* SAVE TO DB **************************************************************************************/
            boolean saved = this.generalLedgerBalanceService.save(entities);

            if(saved){
                logger.info("Successfully parsed 'Singular General Ledger Balance' file");
                return new FileUploadResultDto(ResponseStatusType.SUCCESS, "", "Successfully processed the file - Singular General Ledger Balance", "");
            }else{
                logger.error("Error saving 'Singular General Ledger Balance' file parsed data into database");
                return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Singular General Ledger Balance' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Singular General Ledger Balance' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error processing 'Singular General Ledger Balance' file'", "");
        }
    }

    private List<SingularityGeneralLedgerBalanceRecordDto> parseSingularGeneralLedgerRaw(Iterator<Row> rowIterator){
        List<SingularityGeneralLedgerBalanceRecordDto> records = new ArrayList<>();
        boolean tableHeaderChecked = false;
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
            if(tableHeaderChecked){
                SingularityGeneralLedgerBalanceRecordDto record = new SingularityGeneralLedgerBalanceRecordDto();
                /* Acronym */
                if(ExcelUtils.getStringValueFromCell(row.getCell(0)) != null){
                    record.setAcronym(row.getCell(0).getStringCellValue());
                }else{
                    logger.error("Error parsing 'Singularity General Ledger Balance' file: could not set 'acronym' (cell #1)");
                    throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: could not set 'acronym' (cell #1)");
                }
                /* Balance date */
                if(ExcelUtils.isNotEmptyCell(row.getCell(1)) && row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC){
                    Date balanceDate = row.getCell(1).getDateCellValue();
                    if(balanceDate == null){
                        logger.error("Error parsing 'Singularity General Ledger Balance' file: balance date is invalid - '" + row.getCell(1).getNumericCellValue() + "'");
                        throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: balance date is invalid - '" + row.getCell(1).getNumericCellValue() + "'");
                    }else{
                        record.setBalanceDate(balanceDate);
                    }
                }else{
                    logger.error("Error parsing 'Singularity General Ledger Balance' file: 'balanceDate' is missing or invalid.");
                    throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: 'balanceDate' is missing or invalid.");
                }

                /* Financial statement category */
                if(ExcelUtils.getStringValueFromCell(row.getCell(2)) != null){
                    record.setFinancialStatementCategory(row.getCell(2).getStringCellValue());
                }else{
                    logger.error("Error parsing 'Singularity General Ledger Balance' file: 'financial statement category' is missing or invalid");
                    throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: 'financial statement category' is missing or invalid");
                }

                /* GL Account  */
                if(ExcelUtils.getStringValueFromCell(row.getCell(3)) != null){
                    record.setGLAccount(row.getCell(3).getStringCellValue());
                }else{
                    logger.error("Error parsing 'Singularity General Ledger Balance' file: 'GL Account' is missing or invalid");
                    throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: 'GL Account' is missing or invalid");
                }

                /* Financial statement category description */
                if(ExcelUtils.getStringValueFromCell(row.getCell(4)) != null){
                    record.setFinancialStatementCategoryDescription(row.getCell(4).getStringCellValue());
                }else{
                    logger.error("Error parsing 'Singularity General Ledger Balance' file: 'financial statement category description' is missing or invalid");
                    throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: 'financial statement category description' is missing or invalid");
                }

                /* Chart of Accounts Description */
                if(ExcelUtils.getStringValueFromCell(row.getCell(5)) != null){
                    record.setChartAccountsDescription(row.getCell(5).getStringCellValue());
                }else{
                    logger.error("Error parsing 'Singularity General Ledger Balance' file: 'Chart of accounts description' is missing or invalid");
                    throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: 'Chart of accounts description' is missing or invalid");
                }

                /* Chart of Accounts Long Description */
                if(ExcelUtils.getStringValueFromCell(row.getCell(6)) != null){
                    record.setChartAccountsLongDescription(row.getCell(6).getStringCellValue());
                }else{
                    logger.error("Error parsing 'Singularity General Ledger Balance' file: 'Chart of accounts long description' is missing or invalid");
                    throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: 'Chart of accounts long description' is missing or invalid");
                }

                /* Short name */
                if(ExcelUtils.getStringValueFromCell(row.getCell(7)) != null){
                    record.setShortName(row.getCell(7).getStringCellValue());
                }

                /* GL Account Balance */
                if(ExcelUtils.isNotEmptyCell(row.getCell(12)) && row.getCell(12).getCellType() == Cell.CELL_TYPE_NUMERIC){
                    record.setGLAccountBalance(row.getCell(12).getNumericCellValue());
                }else{
                    logger.error("Error parsing 'Singularity General Ledger Balance' file: 'GL Account Balance' is missing or invalid");
                    throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: 'GL Account Balance' is missing or invalid");
                }

                /* Seg Val CCY */
                if(ExcelUtils.getStringValueFromCell(row.getCell(13)) != null){
                    record.setSegValCCY(row.getCell(13).getStringCellValue());
                }else{
                    logger.error("Error parsing 'Singularity General Ledger Balance' file: 'Seg Val CCY' is missing or invalid");
                    throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: 'Seg Val CCY' is missing or invalid");
                }

                /* Fund CCY */
                if(ExcelUtils.getStringValueFromCell(row.getCell(14)) != null){
                    record.setFundCCY(row.getCell(14).getStringCellValue());
                }else{
                    logger.error("Error parsing 'Singularity General Ledger Balance' file: 'Fund CCY' is missing or invalid");
                    throw new ExcelFileParseException("Error parsing 'Singularity General Ledger Balance' file: 'Fund CCY' is missing or invalid");
                }
                records.add(record);
            }else{
                tableHeaderChecked = singularityGeneralLedgerBalanceTableHeaderCheck(row);
            }

        }
        if(!tableHeaderChecked){
            logger.error("Table header check failed for 'Singularity General Ledger Balance'. " +
                    "Expected: Acronym, Balance Date, Financial Statement Category, GL Account, Financial Statement Category Description, " +
                    "Chart of Accounts Description, Chart of Accounts Long Description, Seg Val1, Seg Val2, Seg Val3, Seg Val4, GL Account Balance, Seg Val CCY, Fund CCY");
            throw new ExcelFileParseException("Table header check failed for 'Singularity General Ledger Balance'. " +
                    "Expected: Acronym, Balance Date, Financial Statement Category, GL Account, Financial Statement Category Description, " +
                    "Chart of Accounts Description, Chart of Accounts Long Description, Seg Val1, Seg Val2, Seg Val3, Seg Val4, GL Account Balance, Seg Val CCY, Fund CCY");
        }
        return records;
    }

    private boolean singularityGeneralLedgerBalanceTableHeaderCheck(Row row){
        return ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), "Acronym") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), "Balance Date") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), "Financial Statement Category") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(3), "GL Account") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(4), "Financial Statement Category Description") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(5), "Chart of Accounts Description") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(6), "Chart of Accounts Long Description") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(7), "Short name") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8), "Seg Val1") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(9), "Seg Val2") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10), "Seg Val3") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(11), "Seg Val4") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(12), "GL Account Balance") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(13), "Seg Val CCY") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(14), "Fund CCY");
    }

    private FileUploadResultDto parseSingularNOAL(FilesDto filesDto, Long reportId, int tranche){
        try {

            /* PARSE EXCEL (RAW) *******************************************************************************/
            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);
            List<SingularityNOALRecordDto> records = parseSingularNOALRaw(rowIterator);
            //printRecords(records);

            /* CHECK ENTITIES AND ASSEMBLE **********************************************************************/

            String trancheName = tranche == 1 ? "[Tranche A] " : tranche == 2 ? "[Tranche B] " : "";
            checkNOALTotalSums(records, trancheName);

            List<ReportingHFNOAL> entities = this.hfNOALService.assembleList(records, reportId, tranche);

            /* SAVE TO DB **************************************************************************************/
            boolean saved = this.hfNOALService.save(entities);

            if(saved){
                logger.info("Successfully parsed 'Singular NOAL' file");
                return new FileUploadResultDto(ResponseStatusType.SUCCESS, "", "Successfully processed the file - Singular NOAL", "");
            }else{
                logger.error("Error saving 'Singular NOAL' file parsed data into database");
                return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Singular NOAL' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Singular NOAL' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error processing 'Singular NOAL' file'", "");
        }
    }

    private List<SingularityNOALRecordDto> parseSingularNOALRaw(Iterator<Row> rowIterator){
        List<SingularityNOALRecordDto> records = new ArrayList<>();

        boolean tableHeaderChecked = false;
        String accountNumber = null;
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
            if(tableHeaderChecked){
                if(singularityNOALTableHeaderCheck(row)){
                    // skip repeating table headers
                    continue;
                }else if(ExcelUtils.getStringValueFromCell(row.getCell(0)) != null){

                    // TODO: account ??

                    // TODO: REPORT TOTAL ???!!!
                    if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), "REPORT TOTAL") ){
                        SingularityNOALRecordDto record = new SingularityNOALRecordDto();
                        record.setTransaction(row.getCell(0).getStringCellValue());
                        if(ExcelUtils.getDoubleValueFromCell(row.getCell(17)) != null) {
                            record.setFunctionalAmount(ExcelUtils.getDoubleValueFromCell(row.getCell(17)));
                        }
                        if(ExcelUtils.getStringValueFromCell(row.getCell(20)) != null) {
                            record.setFunctionalAmountCCY(row.getCell(20).getStringCellValue());
                        }
                        records.add(record);
                        break;
                    }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), "1500-XXXX-XXX-USD")){
                        // Subscriptions
                        accountNumber = "1500-XXXX-XXX-USD";

                    }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), "1550-XXXX-XXX-USD")){
                        // Redemptions
                        accountNumber = "1550-XXXX-XXX-USD";

                    }

                }else if(isSingularityNOALRecordNotEmpty(row)){
                    SingularityNOALRecordDto record = new SingularityNOALRecordDto();
                    /* Date */
                    if(ExcelUtils.isNotEmptyCell(row.getCell(1)) && row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC){
                        Date date = row.getCell(1).getDateCellValue();
                        if(date == null){
                            logger.error("Error parsing 'Singularity NOAL' file: date is invalid '" + ExcelUtils.getTextValueFromAnyCell(row.getCell(1)) + "'");
                            throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: date is invalid '" + ExcelUtils.getTextValueFromAnyCell(row.getCell(1)) + "'");
                        }else{
                            record.setDate(date);
                        }
                    }else if(ExcelUtils.getStringValueFromCell(row.getCell(1)) != null){
                        String dateText = row.getCell(1).getStringCellValue();
                        if(DateUtils.getDate(dateText) == null){
                            logger.error("Error parsing 'Singularity NOAL' file: error parsing date - '" + dateText + "'");
                            throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: error parsing date - '" + dateText + "'");
                        }else{
                            record.setDate(DateUtils.getDate(dateText));
                        }
                    }else{
                        logger.error("Error parsing 'Singularity NOAL' file: 'date' is missing for record #" + (records.size() + 1));
                        throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: 'date' is missing for record #" + (records.size() + 1));
                    }

                    /* Transaction */
                    if(ExcelUtils.getStringValueFromCell(row.getCell(4)) != null){
                        record.setTransaction(row.getCell(4).getStringCellValue());
                    }else{
                        logger.error("Error parsing 'Singularity NOAL' file: 'transaction' is missing or invalid");
                        throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: 'transaction' is missing or invalid");
                    }

                    /* Investor Account/Portfolio fund  */
                    if(ExcelUtils.getStringValueFromCell(row.getCell(7)) != null){
                        record.setName(row.getCell(7).getStringCellValue());
                    }else{
//                        logger.error("Error parsing 'Singularity NOAL' file: 'Investor Account/Portfolio fund' is missing or invalid");
//                        throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: 'Investor Account/Portfolio fund' is missing or invalid");
                    }

                    /* Effective Date */
                    if(ExcelUtils.isNotEmptyCell(row.getCell(9)) && row.getCell(9).getCellType() == Cell.CELL_TYPE_NUMERIC){
                        Date date = row.getCell(9).getDateCellValue();
                        if(date == null){
                            //logger.error("Error parsing 'Singularity NOAL' file: error parsing effective date");
                            //throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: error parsing effective date");
                        }else if(ExcelUtils.isNotEmptyCell(row.getCell(9)) && row.getCell(9).getCellType() == Cell.CELL_TYPE_STRING){
                            String dateText = row.getCell(9).getStringCellValue();
                            if(DateUtils.getDate(dateText) == null){
                                //logger.error("Error parsing 'Singularity NOAL' file: error parsing effective date - " + dateText);
                                //throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: error parsing effective date - " + dateText);
                            }else{
                                record.setEffectiveDate(DateUtils.getDate(dateText));
                            }
                        }else{
                            record.setEffectiveDate(date);
                        }
                    }else{
                        //logger.error("Error parsing 'Singularity NOAL' file: 'effective date' is missing or invalid");
                        //throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: 'effective date' is missing or invalid");
                    }

                    /* Transaction Amount*/
                    if(ExcelUtils.isNotEmptyCell(row.getCell(11)) && row.getCell(11).getCellType() == Cell.CELL_TYPE_NUMERIC){
                        record.setTransactionAmount(row.getCell(11).getNumericCellValue());
                    }else{
                        //logger.error("Error parsing 'Singularity NOAL' file: 'Transaction Amount' is missing or invalid");
                        //throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: 'Transaction Amount' is missing or invalid");
                    }

                    /* Transaction Amount CCY  */
                    if(ExcelUtils.getStringValueFromCell(row.getCell(14)) != null){
                        record.setTransactionAmountCCY(row.getCell(14).getStringCellValue());
                    }else{
                        //logger.error("Error parsing 'Singularity NOAL' file: 'Transaction Amount CCY' is missing");
                        //throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: 'Transaction Amount CCY' is missing");
                    }

                    /* Functional Amount*/
                    if(ExcelUtils.isNotEmptyCell(row.getCell(17)) && row.getCell(17).getCellType() == Cell.CELL_TYPE_NUMERIC){
                        record.setFunctionalAmount(row.getCell(17).getNumericCellValue());
                    }else{
                        //logger.error("Error parsing 'Singularity NOAL' file: 'Functional Amount' is missing");
                        //throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: 'Functional Amount' is missing");
                    }

                    /* Functional Amount CCY  */
                    if(ExcelUtils.getStringValueFromCell(row.getCell(20)) != null){
                        record.setFunctionalAmountCCY(row.getCell(20).getStringCellValue());
                    }else{
                        //logger.error("Error parsing 'Singularity NOAL' file: 'Functional Amount CCY' is missing");
                        //throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: 'Functional Amount CCY' is missing");
                    }
                    record.setAccountNumber(accountNumber);
                    records.add(record);
                }
            }else{
                tableHeaderChecked = singularityNOALTableHeaderCheck(row);
            }

        }
        if(!tableHeaderChecked){
            logger.error("Table header check failed for 'Singularity NOAL' file. Expected: Date, Transaction, Investor Account / Portfolio Fund, Effective Date, Amount, CCY, Amount, CCY");
            throw new ExcelFileParseException("Table header check failed for 'Singularity NOAL' file. Expected: Date, Transaction, Investor Account / Portfolio Fund, Effective Date, Amount, CCY, Amount, CCY");
        }
        return records;
    }

    private void checkNOALTotalSums(List<SingularityNOALRecordDto> records, String trancheName){
        Double functionalSum = 0.0;
        Double endingBalanceSum = 0.0;
        if(records != null){
            for(SingularityNOALRecordDto record: records){
                if(record.getTransaction() == null){
                    logger.error(trancheName + "NOAL 'Transaction' value is missing");
                    throw new ExcelFileParseException(trancheName + "NOAL 'Transaction' value is missing");
                } else if(record.getTransaction().equalsIgnoreCase("Ending Balance") || record.getTransaction().equalsIgnoreCase("Ending")){
                    if(endingBalanceSum.doubleValue() != NumberUtils.getDouble(record.getFunctionalAmount())){
                        logger.error(trancheName + "NOAL Ending Balance does not match for '" + record.getName() +
                                "': found " + NumberUtils.getDouble(record.getFunctionalAmount()) + ", expected " + endingBalanceSum);
                        throw new ExcelFileParseException(trancheName + "NOAL Ending Balance does not match for '" + record.getName() +
                                "': found " + NumberUtils.getDouble(record.getFunctionalAmount()) + ", expected " + endingBalanceSum);
                    }
                    endingBalanceSum = 0.0;
                }else if(record.getTransaction().equalsIgnoreCase("REPORT TOTAL")){
                    if(functionalSum.doubleValue() != NumberUtils.getDouble(record.getFunctionalAmount())){
                        logger.error(trancheName + "NOAL 'Report Total' does not match: found " +
                                NumberUtils.getDouble(record.getFunctionalAmount()) + ", expected " + functionalSum);
                        throw new ExcelFileParseException(trancheName + "NOAL 'Report Total' does not match: found " +
                                NumberUtils.getDouble(record.getFunctionalAmount()) + ", expected " + functionalSum);
                    }
                    functionalSum = 0.0;
                }else{
                    endingBalanceSum = MathUtils.add(endingBalanceSum, record.getFunctionalAmount());
                    functionalSum = MathUtils.add(functionalSum, record.getFunctionalAmount());
                }
            }
        }
    }

    private boolean singularityNOALTableHeaderCheck(Row row){
        return ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), "Date") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(4), "Transaction") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(7), "Investor Account / Portfolio Fund") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8), "Effective Date") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(11), "Amount") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(14), "CCY") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(17), "Amount") &&
                ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(20), "CCY");

        // TODO: Transaction and Functional headers
        // (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(12), "Transaction") &&
        //ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(18), "Functional")
    }

    private boolean isSingularityNOALRecordNotEmpty(Row row){
        return ExcelUtils.isNotEmptyCell(row.getCell(1)) || ExcelUtils.isNotEmptyCell(row.getCell(4)) ||
                ExcelUtils.isNotEmptyCell(row.getCell(7)) || ExcelUtils.isNotEmptyCell(row.getCell(8)) ||
                ExcelUtils.isNotEmptyCell(row.getCell(11)) || ExcelUtils.isNotEmptyCell(row.getCell(14)) ||
                ExcelUtils.isNotEmptyCell(row.getCell(17)) || ExcelUtils.isNotEmptyCell(row.getCell(20));
    }


    private Iterator<Row> getRowIterator(FilesDto filesDto, int sheetNumber){
        InputStream inputFile = null;
        try {
            inputFile = new ByteArrayInputStream(filesDto.getBytes());
            String extension = filesDto.getFileName().substring(filesDto.getFileName().lastIndexOf(".") + 1,
                    filesDto.getFileName().length());
            if (extension.equalsIgnoreCase("xls")) {
                HSSFWorkbook workbook = new HSSFWorkbook(inputFile);
                HSSFSheet sheet = workbook.getSheetAt(sheetNumber);
                return sheet.iterator();
            } else if (extension.equalsIgnoreCase("xlsx")) {
                XSSFWorkbook workbook = new XSSFWorkbook(inputFile);
                XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
                return sheet.iterator();
            } else {
                // log error
                throw new ExcelFileParseException("Invalid file extension: " + filesDto.getFileName());
            }
        }catch (IOException ex){
            // TODO: log error
        }finally {
            try {
                inputFile.close();
            } catch (IOException e) {
                //e.printStackTrace();
                // TODO: log error
            }
        }
        return null;
    }

    private void printRecords(List<ConsolidatedReportRecordDto> records){
        if(records != null || records.isEmpty()) {
            for (ConsolidatedReportRecordDto record : records) {
                System.out.println(record.getName() + " - " + Arrays.toString(record.getClassifications()) + " - " + Arrays.toString(record.getValues()));
            }
        }else{
            System.out.println("Empty or null list.");
        }
    }

    private void checkTotalSumsGeneric(List<ConsolidatedReportRecordDto> fullList, int size,
                                       String totalRecordName, int tranche){

        // TODO: REFACTOR, HARD TO MAINTAIN

        String trancheName = tranche == 1 ? "[Tranche A] " : tranche == 2 ? "[Tranche B] " : "";

        //List<ConsolidatedReportRecordDto> recordsWithoutTotals = new ArrayList<>();
        if(fullList != null){
            Map<String, double[]> sums = new HashMap<>();
            Map<String, Integer> classificationsLevel = new HashMap<>();
            if(StringUtils.isNotEmpty(totalRecordName)){
                sums.put(totalRecordName, new double[size]);
                classificationsLevel.put(totalRecordName, 0);
            }
            ConsolidatedReportRecordDto totalRecordDto = null;
            for(ConsolidatedReportRecordDto recordDto: fullList){
                if(StringUtils.isNotEmpty(totalRecordName) && totalRecordName.equalsIgnoreCase(recordDto.getName())) {
                    // Total record sum check
                    totalRecordDto = recordDto;
                    double[] totalValues = sums.get(totalRecordName);
                    if (totalValues != null) {
                        for (int i = 0; i < totalValues.length; i++) {
                            if (totalRecordDto.getValues() != null && totalRecordDto.getValues()[i] != null) {
                                if (totalValues[i] != totalRecordDto.getValues()[i].doubleValue()) {
                                    // total sum mismatch
                                    logger.error(trancheName + "Error checking totals for record '" + totalRecordDto.getName() +
                                            "' for values #" + (i + 1) + ": expected " + totalValues[i] + ", found " + totalRecordDto.getValues()[i]);
                                    throw new ExcelFileParseException(trancheName + "Error checking totals for record '" + totalRecordDto.getName() +
                                            "' for values #" + (i + 1) + ": expected " + totalValues[i] + ", found " + totalRecordDto.getValues()[i]);
                                }
                            }
                        }
                    }else{
                        // TODO: ????
                    }
                    recordDto.setClassificationRequired(false);
                }else if(recordDto.getClassifications() != null){
                    // check possible total/net values
                    checkPossibleTotalValue(recordDto, totalRecordName, trancheName);

                    boolean isTotalValue = false;
                    int level = 0;
                    for(int j = 0; j < recordDto.getClassifications().length; j++){
                        String classification = recordDto.getClassifications()[j];
                        if(StringUtils.isEmpty(classification)){
                            continue;
                        }
                        classification = classification.toLowerCase();
                        level++;

                        // check values size
                        if(size != recordDto.getValues().length){
                            logger.error(trancheName + "Error checking totals for record '" + recordDto.getName() +
                                    "', values list size mismatch: expected "  + size + " , found "  + recordDto.getValues().length);
                            throw new ExcelFileParseException(trancheName + "Error checking totals for record '" + recordDto.getName() +
                                    "', values list size mismatch: expected "  + size + " , found "  + recordDto.getValues().length);
                        }

                        if(isTotal(recordDto) || isNet(recordDto)){
                            // Check total sum value
                            String key = recordDto.getName().trim().toLowerCase();
                            if(!sums.containsKey(key)){
                                int nameIndex = isTotal(recordDto) ? 5 : 3;
                                key = recordDto.getName().substring(nameIndex).trim().toLowerCase();
                                if(key.endsWith(":")){
                                    key = key.substring(0, key.length() - 1);
                                }
                            }
                            if(StringUtils.isNotEmpty(key) && classification.equalsIgnoreCase(key) && sums.containsKey(key)){
                                // check sums
                                double[] values = sums.get(classification);
                                if(values != null){
                                    for(int i = 0; i < values.length; i++){
                                        if(recordDto.getValues() != null && recordDto.getValues()[i] != null){
                                            if(values[i] != recordDto.getValues()[i]){
                                                // total sum mismatch
                                                logger.error(trancheName + "Error checking totals for record '" + recordDto.getName() +
                                                        "' for values #" + (i + 1) + ": expected " + values[i] + ", found " + recordDto.getValues()[i]);
                                                throw new ExcelFileParseException(trancheName + "Error checking totals for record '" + recordDto.getName() +
                                                        "' for values #" + (i + 1) + ": expected " + values[i] + ", found " + recordDto.getValues()[i]);
                                            }
                                        }
                                    }
                                    // sums ok
                                    sums.put(key, null);
                                }else{
                                    // values is null, total already handled
                                    logger.error(trancheName + "Total value for record '" + recordDto.getName() + "' of classification '" +
                                            classification + "' already handled.");
                                    throw new ExcelFileParseException(trancheName + "Total value for record '" + recordDto.getName() + "' of classification '" +
                                            classification + "' already handled.");
                                }
                            }
                            isTotalValue = true;
                        }else if(sums.containsKey(classification)){
                            // update total values
                            double[] values = sums.get(classification);
                            if(values != null){
                                for(int i = 0; i < values.length; i++){
                                    values[i] = MathUtils.add(values[i], recordDto.getValues()[i] != null ? recordDto.getValues()[i] : 0.0);
                                }
                            }else{
                                // values is null, total already handled
                                logger.error(trancheName + "Total value for record '" + recordDto.getName() + "' of classification '" +
                                        classification + "' already handled.");
                                throw new ExcelFileParseException(trancheName + "Total value for record '" + recordDto.getName() + "' of classification '" +
                                        classification + "' already handled.");
                            }
                        }else{
                            // add totals entry
                            double[] values = new double[size];
                            for(int i = 0; i < values.length; i++){
                                values[i] = recordDto.getValues()[i] != null ? recordDto.getValues()[i] : 0.0;
                            }
                            sums.put(classification, values);
                            classificationsLevel.put(classification, level);
                        }
                    }
                    if(!isTotalValue) {
                        //recordsWithoutTotals.add(recordDto);

                        double[] totalSum = sums.get(totalRecordName);
                        if(totalSum != null) {
                            for (int i = 0; i < size; i++) {
                                totalSum[i] = MathUtils.add(totalSum[i], recordDto.getValues()[i] != null ? recordDto.getValues()[i] : 0.0);
                            }
                        }
                    }
                }else{
                    // TODO: ???
                }
            }


            // TODO: ????????
            // Check level 1 classification total/net value
//            Iterator<String> iterator = sums.keySet().iterator();
//            while(iterator.hasNext()){
//                String classification = iterator.next();
//                if(sums.get(classification) != null && classificationsLevel.get(classification) == 1){
//                    // Level 1 classification, NET/TOTAL value missing
//                    logger.error(trancheName + "Total value for classification '" +
//                            classification + "' is missing: has to match 'Net/Total " + classification + "'");
//                    throw new ExcelFileParseException(trancheName + "Total value for classification '"
//                            + classification + "' is missing: has to match 'Net/Total " + classification + "'");
//                }
//            }


            // Check total record
            if(StringUtils.isNotEmpty(totalRecordName) && totalRecordDto == null) {
                logger.error(trancheName + "Total record '" + totalRecordName + "' is missing.");
                throw new ExcelFileParseException(trancheName + "Total record '" + totalRecordName + "' is missing.");
            }
        }
    }

    private boolean isTotal(ConsolidatedReportRecordDto recordDto){
        return recordDto != null && recordDto.getName().startsWith("Total") && recordDto.getName().length() > 5 &&
                (recordDto.hasClassification(recordDto.getName().substring(5).trim()) ||
                        recordDto.hasClassification(recordDto.getName().trim()));
    }

    private boolean isNet(ConsolidatedReportRecordDto recordDto){
        return recordDto != null && recordDto.getName().startsWith("Net") && recordDto.getName().length() > 3 &&
                (recordDto.hasClassification(recordDto.getName().substring(3).trim()) ||
                        recordDto.hasClassification(recordDto.getName().trim()));
    }


    private void checkPossibleTotalValue(ConsolidatedReportRecordDto recordDto, String totalRecordName, String trancheName){

        if(!isTotal(recordDto) && !isNet(recordDto) && !recordDto.getName().equalsIgnoreCase(totalRecordName)
                && (recordDto.getName().startsWith("Net") || recordDto.getName().startsWith("Total"))){
            // Possible NET/TOTAL value

            String strategyName = recordDto.getName().startsWith("Net") ? recordDto.getName().substring(3).trim() :
                    recordDto.getName().startsWith("Total") ? recordDto.getName().substring(5).trim() : null;

            // strategy name cut off CURRENCY
//                        if(strategyName.trim().endsWith(" - USD") || strategyName.trim().endsWith(" - GBP")){
//                            strategyName = strategyName.substring(0, strategyName.length() - 6);
//                        }else if(strategyName.trim().endsWith(" - Euro")){
//                            strategyName = strategyName.substring(0, strategyName.length() - 7);
//                        }

            if(recordDto.isWithSumFormula()) {
                logger.error(trancheName + "Record '" + recordDto.getName() +
                        "' starts with 'Total/Net' and contains formula, but it does not match any classification/header.");
                throw new ExcelFileParseException(trancheName + "Record '" + recordDto.getName() +
                        "' starts with 'Total/Net' and contains formula, but it does not match any classification/header.");
            }

            boolean isStrategy = strategyName != null && this.strategyRepository.findByNameEnAndGroupType(strategyName, Strategy.TYPE_PRIVATE_EQUITY) != null;
            if(isStrategy) {
                logger.error(trancheName + "Record '" + recordDto.getName() +
                        "' starts with 'Total/Net' and contains strategy name but it does not match any classification/header.");
                throw new ExcelFileParseException(trancheName + "Record '" + recordDto.getName() +
                        "' starts with 'Total/Net' and contains strategy name but it does not match any classification/header.");
            }
        }
    }

    /**
     * Remove trimming spaces, replace new line characters with spaces, and cut off ';' on the following DTO fields:
     * - ConsolidatedReportRecordDto.name
     * - ConsolidatedReportRecordDto.currency
     * - ConsolidatedReportRecordDto.classifications[i]
     *
     * @param records - list of DTOs with string attributes
     */
    private void normalizeTextFields(List<ConsolidatedReportRecordDto> records){
        if(records != null){
            for(ConsolidatedReportRecordDto record: records){
                if(StringUtils.isNotEmpty(record.getName())){
                    record.setName(record.getName().trim().replace("\\n", " "));
                    // Cut off ':' in name
                    if(StringUtils.isNotEmpty(record.getName()) && record.getName().endsWith(":")){
                        record.setName(record.getName().substring(0, record.getName().length() - 1));
                    }
                }

                if(StringUtils.isNotEmpty(record.getCurrency())){
                    record.setCurrency(record.getCurrency().trim().replace("\\n", " "));
                }

                for(int i = 0; record.getClassifications() != null && i < record.getClassifications().length; i++){
                    if(StringUtils.isNotEmpty(record.getClassifications()[i])){
                        String classification = record.getClassifications()[i].trim().replace("\n", " ");
                        record.getClassifications()[i] = classification;
                    }
                }
            }
        }

    }

    private boolean isSumFormulaCell(Cell cell){
        return cell != null && cell.getCellType() == Cell.CELL_TYPE_FORMULA && cell.getCellFormula().contains("SUM");
    }

    /**
     * Check format of records:
     * 1) all headers must have corresponding total values
     * 2) total value record (if provided) exists, total sum not checked
     *
     * Throw exception if no match.
     *
     * @param records - list of records to check
     */
    private void checkHeaderClosingTotalSumFormat(List<ConsolidatedReportRecordDto> records, String totalRecordName, String trancheName){
        if(records != null){
            Stack<String> stack = new StringStack();
            boolean totalRecordNameCheck = false;
            for(ConsolidatedReportRecordDto record: records){
                if(record.getClassifications() != null) {
                    if(StringUtils.isNotEmpty(record.getName()) && StringUtils.isNotEmpty(totalRecordName) &&
                            record.getName().equalsIgnoreCase(totalRecordName)){
                        totalRecordNameCheck = true;
                        if (!stack.isEmpty() && (record.getName().startsWith("Total") && stack.peek().equalsIgnoreCase(record.getName().substring(5).trim()) ||
                                (record.getName().startsWith("Net") && stack.peek().equalsIgnoreCase(record.getName().substring(3).trim())) ||
                                (record.getName().trim().equalsIgnoreCase(stack.peek())))) {
                            // is total
                            stack.pop();
                        }

                    }else if (StringUtils.isNotEmpty(record.getName()) && !stack.isEmpty() &&
                            (record.getName().startsWith("Total") && stack.peek().equalsIgnoreCase(record.getName().substring(5).trim()) ||
                                    (record.getName().startsWith("Net") && stack.peek().equalsIgnoreCase(record.getName().substring(3).trim())) ||
                                    (record.getName().trim().equalsIgnoreCase(stack.peek())))) {
                        // is total
                        stack.pop();
                    }else {
                        for (String classification : record.getClassifications()) {
                            if (StringUtils.isNotEmpty(classification) && !stack.contains(classification)) {
                                stack.push(classification.trim());
                            }
                        }
                    }
                }
            }
            if(totalRecordName != null && !totalRecordNameCheck){
                logger.error(trancheName + "Total record not found, expected '" + totalRecordName + "'");
                throw new ExcelFileParseException(trancheName + "Total record not found, expected '" + totalRecordName + "'");
            }
            if(!stack.isEmpty()){
                String header = stack.pop();
                logger.error(trancheName + "No matching total value found for '" + header + "'");
                throw new ExcelFileParseException(trancheName + "No matching total value found for '" + header + "'");
            }
        }
    }


    /* BS Singularity Tranche A *************************************************************************/

    private FileUploadResultDto parseBS(FilesDto filesDto){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        try {
            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);
            int rowNum = 0;
            String[] classifications = new String[2];
            while (rowIterator.hasNext()) { // each row
                Row row = rowIterator.next();

                Iterator<Cell> cellIterator = row.cellIterator();
                ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(2, 1);
                String name = null;
                while(cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    if(ExcelUtils.isNotEmptyCell(cell) && cell.getCellType() == Cell.CELL_TYPE_STRING){
                        if(cell.getStringCellValue().equalsIgnoreCase("Assets")){
                            classifications[0] = "Assets";
                        }else if(cell.getStringCellValue().equalsIgnoreCase("Liabilities")){
                            classifications[0] = "Liabilities";
                        }else if(cell.getStringCellValue().equalsIgnoreCase("Equity")){
                            classifications[0] = "Equity";
                        }else {
                            name = cell.getStringCellValue();
                        }
                    }else if(ExcelUtils.isNotEmptyCell(cell) && cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                        Double[] values = {cell.getNumericCellValue()};
                        recordDto.setValues(values);
                        recordDto.setName(name);
                    }
                }

                if(recordDto.getValues() != null && recordDto.getValues()[0] != null){
                    recordDto.setClassifications(Arrays.copyOf((String[])classifications, 2));
                    records.add(recordDto);
                }else if(name != null){
                    classifications[1] = name;
                }

                if(name != null && name.equalsIgnoreCase("Total " + classifications[0])){
                    classifications = new String[2];
                }
                if(name != null && name.equalsIgnoreCase("Total " + classifications[1])){
                    String classification = classifications[0];
                    classifications = new String[2];
                    classifications[0] = classification;
                }

                if(name != null && name.equalsIgnoreCase("Notes and Disclosures")){
                    break;
                }

                rowNum++;
            }

            printRecords(records);
            logger.info("Successfully parsed 'Balance Sheet Singularity' file");
            return new FileUploadResultDto(ResponseStatusType.SUCCESS, "", "Successfully processed the file - BS Singularity", "");

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Balance Sheet Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Balance Sheet Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error processing 'Balance Sheet Singularity' file'", "");
        }
    }

    private FileUploadResultDto parseBSTrancheA(FilesDto filesDto){
        return parseBS(filesDto);
    }

    private FileUploadResultDto parseBSTrancheB(FilesDto filesDto){
        return parseBS(filesDto);
    }

    /* IMDR Singularity Tranche A ***********************************************************************/
    private FileUploadResultDto parseIMDR(FilesDto filesDto){

        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        try {
            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);
            int rowNum = 0;
            String[] classifications = new String[2];
            while (rowIterator.hasNext()) { // each row
                Row row = rowIterator.next();
                if(rowNum < 10){
                    // file and table headers
                    checkIMDRTableHeader(row, rowNum);
                }else{
                    Cell cell = row.getCell(0);
                    if(ExcelUtils.isNotEmptyCell(cell)){
                        String name = cell.getStringCellValue();

                        if(ExcelUtils.isEmptyCellRange(row, 4, 16) && ExcelUtils.isEmptyCell(row.getCell(18))){
                            if(classifications[0] == null){
                                classifications[0] = name;
                            }else{
                                classifications[1] = name;
                            }
                        }else{
                            ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto();
                            recordDto.setName(name);
                            recordDto.setClassifications(Arrays.copyOf((String[])classifications, 5));
                            for(int i = 4; i <= 18; i++){
                                if(i == 17){
                                    continue;
                                }
                                recordDto.addValue(ExcelUtils.getDoubleValueFromCell(row.getCell(i)));
                            }
                            records.add(recordDto);

                            if(name.equalsIgnoreCase("Total " + recordDto.getLastClassification())){
                                classifications = new String[2];
                            }
                        }

                        // Check end of file (footnote)
                        if(name.equalsIgnoreCase("NET ASSET VALUE")){
                            break;
                        }

                    }else{
                        if(ExcelUtils.isNotEmptyCellRange(row, 4, 16) || ExcelUtils.isNotEmptyCell(row.getCell(18))){
                            ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto();
                            //recordDto.setName(name);
                            recordDto.setClassifications(Arrays.copyOf((String[])classifications, 5));
                            for(int i = 4; i <= 18; i++){
                                if(i == 17){
                                    continue;
                                }
                                recordDto.addValue(ExcelUtils.getDoubleValueFromCell(row.getCell(i)));
                            }
                            records.add(recordDto);

                            // clear last classification
                            classifications[1] = null;
                        }
                    }
                }

                rowNum++;
            }
            printRecords(records);
            logger.info("Successfully parsed 'IMDR Singularity' file");
            return new FileUploadResultDto(ResponseStatusType.SUCCESS, "", "Successfully processed the file - IMDR Singularity", "");

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'IMDR Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'IMDR Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error processing 'IMDR Singularity' file'", "");
        }
    }

    private FileUploadResultDto parseIMDRTrancheA(FilesDto filesDto){
        return parseIMDR(filesDto);
    }

    private FileUploadResultDto parseIMDRTrancheB(FilesDto filesDto){
        return parseIMDR(filesDto);
    }

    private void checkIMDRTableHeader(Row row, int rowNum){
        if(rowNum == 7){
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(5), "Month to Date")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=8, cell=6. Expected 'Month to Date'");
                throw new ExcelFileParseException("File header check failed: row=8, cell=6. Expected 'Month to Date'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(13), "Year to Date")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=8, cell=14. Expected 'Year to Date'");
                throw new ExcelFileParseException("File header check failed: row=8, cell=14. Expected 'Year to Date'");
            }
        }else if(rowNum == 8){
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(4), "Balance")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=5. Expected 'Balance'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=5. Expected 'Balance'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(5), "Subscriptions")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=6. Expected 'Subscriptions'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=6. Expected 'Subscriptions'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(6), "Opening")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=7. Expected 'Opening'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=7. Expected 'Opening'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(7), "USD")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=8. Expected 'USD'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=8. Expected 'USD'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(9), "Contrib.")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=10. Expected 'Contrib.'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=10. Expected 'Contrib.'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10), "Balance")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=11. Expected 'Balance'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=11. Expected 'Balance'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(11), "Subscriptions")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=12. Expected 'Subscriptions'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=12. Expected 'Subscriptions'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(12), "USD")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=13. Expected 'USD'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=13. Expected 'USD'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(13), "Ending")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=14. Expected 'Ending'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=14. Expected 'Ending'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(14), "% of")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=15. Expected '% of'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=15. Expected '% of'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(15), "% of")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=16. Expected '% of'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=16. Expected '% of'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(18), "Contrib.")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=19. Expected 'Contrib.'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=19. Expected 'Contrib.'");
            }
        }else if(rowNum == 9){
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), "Portfolio Fund Name")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=3. Expected 'Portfolio Fund Name'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=3. Expected 'Portfolio Fund Name'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(4), "Forward")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=5. Expected 'Forward'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=5. Expected 'Forward'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(5), "(Redemptions)")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=6. Expected '(Redemptions)'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=6. Expected '(Redemptions)'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(6), "Balance")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=7. Expected 'Balance'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=7. Expected 'Balance'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(7), "Gain (Loss)")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=8. Expected 'Gain (Loss)'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=8. Expected 'Gain (Loss)'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8), "ROR")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=9. Expected 'ROR'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=9. Expected 'ROR'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(9), "to Ret.")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=10. Expected 'to Ret.'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=10. Expected 'to Ret.'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10), "Forward")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=11. Expected 'Forward'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=11. Expected 'Forward'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(11), "(Redemptions)")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=12. Expected '(Redemptions)'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=12. Expected '(Redemptions)'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(12), "Gain (Loss)")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=13. Expected 'Gain (Loss)'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=13. Expected 'Gain (Loss)'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(13), "Balance")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=14. Expected 'Balance'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=14. Expected 'Balance'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(14), "Inv.")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=15. Expected 'Inv.'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=15. Expected 'Inv.'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(15), "NAV")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=16. Expected 'NAV'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=16. Expected 'NAV'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(16), "ROR")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=17. Expected 'ROR'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=17. Expected 'ROR'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(18), "to Ret.")){
                logger.error("File header check failed for 'IMDR Singularity' file: row=9, cell=19. Expected 'to Ret.'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=19. Expected 'to Ret.'");
            }
        }

    }

    /* PAR Singularity Tranche A ************************************************************************/

    private FileUploadResultDto parsePAR(FilesDto filesDto){

        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        try {

            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);
            int rowNum = 0;
            String[] classifications = new String[2];
            while (rowIterator.hasNext()) { // each row
                Row row = rowIterator.next();
                if(rowNum < 11){
                    // file and table headers
                    checkPARTableHeader(row, rowNum);
                }else{
                    Cell cell = row.getCell(0);
                    if(ExcelUtils.isNotEmptyCell(cell)){
                        String name = cell.getStringCellValue();
                        if(ExcelUtils.isEmptyCellRange(row, 6, 10) && ExcelUtils.isEmptyCellRange(row, 13, 18)){
                            if(classifications[0] == null){
                                classifications[0] = name;
                            }else{
                                classifications[1] = name;
                            }
                        }else{
                            ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto();
                            recordDto.setName(name);
                            recordDto.setClassifications(Arrays.copyOf((String[])classifications, 5));
                            for(int i = 6; i <= 18; i++){
                                if(i == 11 || i == 12){
                                    continue;
                                }
                                recordDto.addValue(ExcelUtils.getDoubleValueFromCell(row.getCell(i)));
                            }
                            records.add(recordDto);

                            if(name.equalsIgnoreCase("Total " + recordDto.getLastClassification()) ||
                                    name.equalsIgnoreCase("Total - " + recordDto.getLastClassification()) ){
                                if(classifications[1] != null && classifications[1].equalsIgnoreCase(recordDto.getLastClassification())){
                                    String classification = classifications[0];
                                    classifications = new String[2];
                                    classifications[0] = classification;
                                }else if(classifications[0] != null && classifications[0].equalsIgnoreCase(recordDto.getLastClassification())){
                                    classifications = new String[2];
                                }
                            }
                        }

                        // Check end of file (footnote)
                        if(name.equalsIgnoreCase("NET ASSET VALUE")){
                            break;
                        }

                    }else{
                    }
                }

                rowNum++;
            }
            printRecords(records);
            logger.info("Successfully parsed 'PAR Singularity' file");
            return new FileUploadResultDto(ResponseStatusType.SUCCESS, "", "Successfully processed the file - PAR Singularity", "");

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'PAR Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'PAR Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error processing 'PAR Singularity' file'", "");
        }
    }

    private FileUploadResultDto parsePARTrancheA(FilesDto filesDto){
        return parsePAR(filesDto);
    }

    private FileUploadResultDto parsePARTrancheB(FilesDto filesDto){
        return parsePAR(filesDto);
    }

    private void checkPARTableHeader(Row row, int rowNum){
        if(rowNum == 7){
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(7), "as Percentage")){
                logger.error("File header check failed for 'PAR Singularity' file: row=8, cell=8. Expected 'as Percentage'");
                throw new ExcelFileParseException("File header check failed: row=8, cell=8. Expected 'as Percentage'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8), "as a")){
                logger.error("File header check failed for 'PAR Singularity' file: row=8, cell=9. Expected 'as a'");
                throw new ExcelFileParseException("File header check failed: row=8, cell=9. Expected 'as a'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(9), "as a")){
                logger.error("File header check failed for 'PAR Singularity' file: row=8, cell=10. Expected 'as a'");
                throw new ExcelFileParseException("File header check failed: row=8, cell=10. Expected 'as a'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10), "as a")){
                logger.error("File header check failed for 'PAR Singularity' file: row=8, cell=11. Expected 'as a'");
                throw new ExcelFileParseException("File header check failed: row=8, cell=11. Expected 'as a'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(15), "as Percentage")){
                logger.error("File header check failed for 'PAR Singularity' file: row=8, cell=16. Expected 'as Percentage'");
                throw new ExcelFileParseException("File header check failed: row=8, cell=16. Expected 'as Percentage'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(16), "as a")){
                logger.error("File header check failed for 'PAR Singularity' file: row=8, cell=17. Expected 'as a'");
                throw new ExcelFileParseException("File header check failed: row=8, cell=17. Expected 'as a'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(17), "as a")){
                logger.error("File header check failed for 'PAR Singularity' file: row=8, cell=18. Expected 'as a'");
                throw new ExcelFileParseException("File header check failed: row=8, cell=18. Expected 'as a'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(18), "as a")){
                logger.error("File header check failed for 'PAR Singularity' file: row=8, cell=19. Expected 'as a'");
                throw new ExcelFileParseException("File header check failed: row=8, cell=19. Expected 'as a'");
            }

        }else if(rowNum == 8){
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(6), "Ending")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=7. Expected 'Ending'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=7. Expected 'Ending'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(7), "of Substrategy/")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=8. Expected 'of Substrategy/'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=8. Expected 'of Substrategy/'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8), "Percentage")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=9. Expected 'Percentage'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=9. Expected 'Percentage'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(9), "Percentage")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=10. Expected 'Percentage'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=10. Expected 'Percentage'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10), "Percentage")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=11. Expected 'Percentage'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=11. Expected 'Percentage'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(12), "Subscriptions")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=13. Expected 'Subscriptions'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=13. Expected 'Subscriptions'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(14), "Allocated")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=15. Expected 'Allocated'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=15. Expected 'Allocated'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(15), "of Substrategy/")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=16. Expected 'of Substrategy/'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=16. Expected 'of Substrategy/'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(16), "Percentage")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=17. Expected 'Percentage'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=17. Expected 'Percentage'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(17), "Percentage")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=18. Expected 'Percentage'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=18. Expected 'Percentage'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(18), "Percentage")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=19. Expected 'Percentage'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=19. Expected 'Percentage'");
            }

        }else if(rowNum == 9){
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(6), "Balance")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=7. Expected 'Balance'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=7. Expected 'Balance'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(7), "Region")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=8. Expected 'Region'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=8. Expected 'Region'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(8), "of")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=9. Expected 'of'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=9. Expected 'of'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(9), "of")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=10. Expected 'of'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=10. Expected 'of'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(10), "of")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=11. Expected 'of'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=11. Expected 'of'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(12), "(Redemptions)")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=13. Expected '(Redemptions)'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=13. Expected '(Redemptions)'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(14), "Balance")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=15. Expected 'Balance'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=15. Expected 'Balance'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(15), "Region")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=16. Expected 'Region'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=16. Expected 'Region'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(16), "of")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=17. Expected 'of'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=17. Expected 'of'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(17), "of")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=18. Expected 'of'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=18. Expected 'of'");
            }
            if(!ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(18), "of")){
                logger.error("File header check failed for 'PAR Singularity' file: row=9, cell=19. Expected 'of'");
                throw new ExcelFileParseException("File header check failed: row=9, cell=19. Expected 'of'");
            }

        }

    }

    /* IS Singularity Tranche A *************************************************************************/
    private FileUploadResultDto parseIS(FilesDto filesDto){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        try {

            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);
            int rowNum = 0;
            String[] classifications = new String[2];
            while (rowIterator.hasNext()) { // each row
                Row row = rowIterator.next();
                if(rowNum == 7){
                    if(ExcelUtils.isEmptyCell(row.getCell(9)) || !ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(9), "PTD Postings")){
                        logger.error("File header check failed for 'Income Statement Singularity' file: row=9, cell=10. Expected 'PTD Postings'");
                        throw new ExcelFileParseException("File header check failed: row=9, cell=10. Expected 'PTD Postings'");
                    }
                    if(ExcelUtils.isEmptyCell(row.getCell(15)) || !ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(15), "YTD Postings")){
                        logger.error("File header check failed for 'Income Statement Singularity' file: row=9, cell=13. Expected 'YTD Postings'");
                        throw new ExcelFileParseException("File header check failed: row=9, cell=13. Expected 'YTD Postings'");
                    }
                }else if(rowNum > 7){
                    if(ExcelUtils.isNotEmptyCell(row.getCell(1))){
                        if(row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING){
                            classifications[0] = row.getCell(1).getStringCellValue();
                        }
                    }else if(ExcelUtils.isNotEmptyCell(row.getCell(2))){
                        if(row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING){
                            classifications[1] = row.getCell(2).getStringCellValue();
                        }
                    }else if(ExcelUtils.isNotEmptyCell(row.getCell(3)) || ExcelUtils.isNotEmptyCell(row.getCell(4))){
                        Cell cell = ExcelUtils.isNotEmptyCell(row.getCell(3)) ? row.getCell(3) : row.getCell(4);
                        if(cell.getCellType() == Cell.CELL_TYPE_STRING){
                            ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto();
                            recordDto.setName(cell.getStringCellValue());

                            int ptdIndex = ExcelUtils.isNotEmptyCell(row.getCell(3)) ? 8 : 8;
                            int ytdIndex = ExcelUtils.isNotEmptyCell(row.getCell(3)) ? 14 : 13;
                            if(ExcelUtils.isNotEmptyCell(row.getCell(ptdIndex)) && row.getCell(ptdIndex).getCellType() == Cell.CELL_TYPE_NUMERIC){
                                recordDto.addValue(row.getCell(ptdIndex).getNumericCellValue());
                            }
                            if(ExcelUtils.isNotEmptyCell(row.getCell(ytdIndex)) && row.getCell(ytdIndex).getCellType() == Cell.CELL_TYPE_NUMERIC){
                                recordDto.addValue(row.getCell(ytdIndex).getNumericCellValue());
                            }

                            if(recordDto.getValues() != null && (recordDto.getValues()[0] != null || recordDto.getValues()[1] != null)){
                                recordDto.setClassifications(Arrays.copyOf((String[])classifications, 5));
                                records.add(recordDto);
                            }else{
                                if(classifications[0] == null){
                                    classifications[0] = recordDto.getName();
                                }else{
                                    classifications[1] = recordDto.getName();
                                }
                            }

                            if(recordDto.getName() != null && recordDto.getName().equalsIgnoreCase("Total " + recordDto.getLastClassification())){
                                if(classifications[1] != null && classifications[1].equalsIgnoreCase(recordDto.getLastClassification())){
                                    String classification = classifications[0];
                                    classifications = new String[2];
                                    classifications[0] = classification;
                                }else if(classifications[0] != null && classifications[0].equalsIgnoreCase(recordDto.getLastClassification())){
                                    classifications = new String[2];
                                }
                            }

                            if(recordDto.getName() != null && recordDto.getName().equalsIgnoreCase("Net Income ( Loss ) from Operation")){
                                break;
                            }
                        }
                    }

                }

                rowNum++;
            }
            printRecords(records);
            logger.info("Successfully parsed 'Income Statement Singularity' file");
            return new FileUploadResultDto(ResponseStatusType.SUCCESS, "", "Successfully processed the file - IS Singularity", "");

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Income Statement Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Income Statement Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(ResponseStatusType.FAIL, "", "Error processing 'Income Statement Singularity' file'", "");
        }
    }

    private FileUploadResultDto parseISTrancheA(FilesDto filesDto){
        return parseIS(filesDto);
    }

    private FileUploadResultDto parseISTrancheB(FilesDto filesDto){
        return parseIS(filesDto);
    }
}