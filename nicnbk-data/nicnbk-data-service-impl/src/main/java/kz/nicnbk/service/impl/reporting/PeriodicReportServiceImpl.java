package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.util.ArrayUtils;
import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.lookup.StrategyRepository;
import kz.nicnbk.repo.api.reporting.PeriodReportRepository;
import kz.nicnbk.repo.api.reporting.PeriodicReportFilesRepository;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.PeriodicReportFiles;
import kz.nicnbk.repo.model.reporting.privateequity.*;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.reporting.PeriodicDataService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.privateequity.*;
import kz.nicnbk.service.converter.reporting.PeriodReportConverter;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.*;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by magzumov on 20.04.2017.
 */
@Service
public class PeriodicReportServiceImpl implements PeriodicReportService {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicReportServiceImpl.class);

    @Autowired
    private PeriodReportRepository periodReportRepository;

    @Autowired
    private PeriodReportConverter periodReportConverter;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PeriodicReportFilesRepository periodicReportFilesRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private PEScheduleInvestmentService scheduleInvestmentService;

    @Autowired
    private PEStatementBalanceService statementBalanceService;

    @Autowired
    private PEStatementOperatinsService statementOperatinsService;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private PEStatementCashflowsService statementCashflowsService;

    @Autowired
    private PEStatementChangesService statementChangesService;

    @Autowired
    private PeriodicDataService periodicDataService;


    @Override
    public Long save(PeriodicReportDto dto, String updater) {

        try {
            PeriodicReport entity = this.periodReportConverter.assemble(dto);
            if (dto.getId() == null) { // CREATE
                Employee employee = this.employeeRepository.findByUsername(dto.getCreator());
                // set creator
                entity.setCreator(employee);
            } else { // UPDATE
                // set creator
                Employee employee = this.periodReportRepository.findOne(dto.getId()).getCreator();
                entity.setCreator(employee);
                // set creation date
                Date creationDate = periodReportRepository.findOne(dto.getId()).getCreationDate();
                entity.setCreationDate(creationDate);
                // set update date
                entity.setUpdateDate(new Date());
                // set updater
                Employee updatedby = this.employeeRepository.findByUsername(updater);
                entity.setUpdater(updatedby);
            }

            Long id = periodReportRepository.save(entity).getId();
            logger.info(dto.getId() == null ? "Periodic report created: " + id + ", by " + entity.getCreator().getUsername() :
                    "Periodic report updated: " + id + ", by " + updater);
            return id;
        } catch (Exception ex){
            logger.error("Error saving periodic report: " + (dto != null && dto.getId() != null ? dto.getId() : "new") ,ex);
            return null;
        }
    }

    @Override
    public boolean deleteFile(Long fileId) {
        try {
            this.periodicReportFilesRepository.deleteByFileId(fileId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting period report file record for file id = " + fileId );
        }
        return false;
    }

    @Override
    public PeriodicReportDto get(Long id) {
        try {
            PeriodicReport entity = this.periodReportRepository.findOne(id);
            PeriodicReportDto dto = this.periodReportConverter.disassemble(entity);
            return dto;
        } catch(Exception ex){
            logger.error("Error loading periodic report: " + id, ex);
        }
        return null;
    }

    @Override
    public List<PeriodicReportDto> getAll() {
        try {
            List<PeriodicReportDto> dtoList = new ArrayList<>();
            Iterator<PeriodicReport> iterator = this.periodReportRepository.findAll(new Sort(Sort.Direction.DESC, "reportDate", "id")).iterator();
            while (iterator.hasNext()) {
                PeriodicReport entity = iterator.next();
                PeriodicReportDto dto = this.periodReportConverter.disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Error loading all period reports", ex);
        }
        return null;
    }

    @Override
    public List<FilesDto> getPeriodicReportFiles(Long reportId) {
        try {
            List<PeriodicReportFiles> entities = this.periodicReportFilesRepository.getEntitiesByReportId(reportId);
            List<FilesDto> files = new ArrayList<>();
            if (entities != null) {
                for (PeriodicReportFiles entity : entities) {
                    FilesDto fileDto = new FilesDto();
                    fileDto.setType(entity.getFile().getType() != null ? entity.getFile().getType().getCode() : null);
                    fileDto.setId(entity.getFile().getId());
                    fileDto.setFileName(entity.getFile().getFileName());
                    files.add(fileDto);
                }
            }
            return files;
        }catch(Exception ex){
            logger.error("Error getting periodic report files: report=" + reportId, ex);
        }
        return null;
    }

    @Override
    public FilesDto saveInputFile(Long reportId, FilesDto filesDto) {
        try {
            if (filesDto != null) {
                if (filesDto.getId() == null) {

                    Long fileId = fileService.save(filesDto, this.fileService.getCatalogByFileCode(filesDto.getType()));

                    logger.info("Saved periodic report file (File system): report=" + reportId + ", file=" + fileId);
                    PeriodicReportFiles periodicReportFiles = new PeriodicReportFiles(reportId, fileId);
                    periodicReportFilesRepository.save(periodicReportFiles);
                    logger.info("Saved periodic report file info (Database): report=" + reportId + ", file=" + fileId);

                    FilesDto newFileDto = new FilesDto();
                    newFileDto.setId(fileId);
                    newFileDto.setFileName(filesDto.getFileName());
                    return newFileDto;
                }
            }
            return null;
        }catch (Exception ex){
            logger.error("Error saving periodic report file: reportId=" + reportId, ex);
        }
        return null;
    }

    @Override
    public FileUploadResultDto parseFile(String fileType, FilesDto filesDto, Long reportId) {
        if(fileType.equals(FileTypeLookup.NB_REP_T1.getCode())){
            return parseScheduleInvestments(filesDto, reportId);
        }else if(fileType.equals(FileTypeLookup.NB_REP_T2.getCode())){
            return parseStatementAssetsLiabilities(filesDto, reportId);
        }else if(fileType.equals(FileTypeLookup.NB_REP_T3.getCode())){
            return parseStatementCashFlows(filesDto, reportId);
        }else if(fileType.equals(FileTypeLookup.NB_REP_T4.getCode())){
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
        }else{
            FileUploadResultDto fileUploadResultDto = new FileUploadResultDto();
            // log
            logger.error("File type did not match[" + fileType + "] for file '" + filesDto.getFileName() + "'");
            fileUploadResultDto.setStatus(StatusResultType.FAIL);
            fileUploadResultDto.setMessageEn("Report type mismatch");
            return fileUploadResultDto;
        }
    }

    @Override
    public ConsolidatedReportRecordHolderDto getScheduleInvestments(Long reportId) {
        ConsolidatedReportRecordHolderDto results = this.scheduleInvestmentService.get(reportId);
        return results;
    }

    @Override
    public ConsolidatedReportRecordHolderDto getStatementBalanceOperations(Long reportId) {
        ConsolidatedReportRecordHolderDto balanceResults = this.statementBalanceService.get(reportId);

        ConsolidatedReportRecordHolderDto operationsResults = this.statementOperatinsService.get(reportId);

        balanceResults.merge(operationsResults);

        return balanceResults;
    }

    @Override
    public ConsolidatedReportRecordHolderDto getStatementCashflows(Long reportId){
        ConsolidatedReportRecordHolderDto statementCashflows = this.statementCashflowsService.get(reportId);
        return statementCashflows;
    }

    @Override
    public ConsolidatedReportRecordHolderDto getStatementChanges(Long reportId){
        ConsolidatedReportRecordHolderDto statementChanges = this.statementChangesService.get(reportId);
        return statementChanges;
    }

    // TODO: @Transactional????

    /* Schedule of Investments ******************************************************/

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

            /* CHECK SUMS/TOTALS ********************************************************************************/
            List<ConsolidatedReportRecordDto> updatedRecordsTrancheA = checkTotalSumsGeneric(sheet1Records, 3, getScheduleInvestmentsTotalRecordName(sheet1Records), 1);
            List<ConsolidatedReportRecordDto> updatedRecordsTrancheB = checkTotalSumsGeneric(sheet2Records, 3, getScheduleInvestmentsTotalRecordName(sheet2Records), 2);

            /* CHECK ENTITIES AND ASSEMBLE **********************************************************************/
            // sheet 1 - Tranche A
            List<ReportingPEScheduleInvestment> entities1 = this.scheduleInvestmentService.assembleList(updatedRecordsTrancheA, 1, reportId); // TODO: tranche type constant !!!
            // sheet 2 - Tranche B
            List<ReportingPEScheduleInvestment> entities2 = this.scheduleInvestmentService.assembleList(updatedRecordsTrancheB, 2, reportId); // TODO: tranche type constant !!!

            /* SAVE TO DB **************************************************************************************/
            boolean saved = this.scheduleInvestmentService.save(entities1);
            if(saved) {
                saved = this.scheduleInvestmentService.save(entities2);
            }

            if(saved){
                logger.info("Successfully parsed 'Schedule of Investments' file");
                return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - Schedule of Investments", "");
            }else{
                logger.error("Error saving 'Schedule of Investments' file parsed data into database");
                return new FileUploadResultDto(StatusResultType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Schedule of Investments' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("Error parsing 'Schedule of Investments' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error when processing 'Schedule of Investments' file", "");
        }
    }

    private String getScheduleInvestmentsTotalRecordName(List<ConsolidatedReportRecordDto> records){
        String nameTotal1 = "Private equity partnerships";
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
                    }

                    if(name != null && currentName != null && !name.equalsIgnoreCase(currentName)){
                        return "Total Private equity partnerships and co-investments";
                    }else if(name == null && currentName != null){
                        name = currentName;
                    }
                }
            }
        }
        return null;
    }

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

                // Check Headers: Fund Investments & Co-Investments
                if(ExcelUtils.isNotEmptyCell(row.getCell(0)) && row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING &&
                        StringUtils.isNotEmpty(row.getCell(0).getStringCellValue())){

                    if(ExcelUtils.isEmptyCell(row.getCell(2)) && ExcelUtils.isEmptyCell(row.getCell(3)) &&
                            ExcelUtils.isEmptyCell(row.getCell(4))){

                        // classifications
                        for(int i = 0; i < classifications.length; i++){
                            if(classifications[i] == null){
                                classifications[i] = row.getCell(0).getStringCellValue();
                                break;
                            }
                        }

                    }

                }

                Cell cell = row.getCell(1);
                if(ExcelUtils.isNotEmptyCell(cell) && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
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
                        values[0] = ExcelUtils.getDoubleValueFromCell(row.getCell(2));
                        values[1] = ExcelUtils.getDoubleValueFromCell(row.getCell(3));
                        values[2] = ExcelUtils.getDoubleValueFromCell(row.getCell(4));

                        ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto(5, 3);
                        recordDto.setName(name);
                        recordDto.setValues(values);
                        // classifications
                        recordDto.setClassifications(Arrays.copyOf((String[]) classifications, 5));
                        // currency
                        recordDto.setCurrency(ExcelUtils.getCellCurrency(row.getCell(2)));

                        recordDto.setFormula(row.getCell(2).getCellType() == Cell.CELL_TYPE_FORMULA);
                        records.add(recordDto);

                        boolean reset = false;
                        for(int i = 0; i < classifications.length; i++){
                            if(reset){
                                classifications[i] = null;
                            }else if(classifications[i] != null && name.equalsIgnoreCase("total " + classifications[i])){
                                classifications[i] = null;
                                reset = true;
                            }
                        }
                    }
                }
            }

            rowNum++;
        }
        return records;

    }

    private void checkScheduleInvestmentsFileHeader(int rowNum, Row row){
        if (rowNum == 0) {/* ROW = 0 */
            // check file header
            Cell cell = row.getCell(0);
            if (ExcelUtils.isNotEmptyCell(cell) && !cell.getStringCellValue().equals("Tarragon Master Fund LP")) {
                logger.error("File header check failed for 'Schedule of Investments' file: row=1, cell=1. Expected: 'Tarragon Master Fund LP'");
                throw new ExcelFileParseException("File header check failed for 'Schedule of Investments' file: row=1, cell=1. Expected: 'Tarragon Master Fund LP'");
            }
        } else if (rowNum == 1) {/* ROW = 1 */
            // check file header
            Cell cell = row.getCell(0);
            if (ExcelUtils.isNotEmptyCell(cell) && !cell.getStringCellValue().startsWith("Schedule of Investments - Tranche ")) {
                logger.error("File header check failed for 'Schedule of Investments' file: row=2, cell=1. Expected: 'Schedule of Investments - Tranche A(or B)'");
                throw new ExcelFileParseException("File header check failed for 'Schedule of Investments' file: row=2, cell=1. Expected: 'Schedule of Investments - Tranche A(or B)'");
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
            logger.error("File header check failed for 'Schedule of Investments' file: row=3, cell=1. Report date: " + reportDate);
            throw new ExcelFileParseException("File header check failed: row=3, cell=1. Report date: " + reportDate);

        } else if (rowNum == 3) {/* ROW = 3 */
            // skip, empty row
        }
    }

    private void checkScheduleInvestmentsTableHeader(Row row){
        Cell cell = row.getCell(1);
        if (ExcelUtils.isNotEmptyCell(cell) && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())) {
            if (!cell.getStringCellValue().equals("Investment")) {
                logger.error("File header check failed for 'Schedule of Investments' file: row=5, cell=2. Expected: 'Investment'");
                throw new ExcelFileParseException("File header check failed: row=5, cell=2. Expected: 'Investment'");
            }
        }else{
            throw new ExcelFileParseException("File header check failed: row=5, cell=2. Expected: 'Investment'");
        }
        cell = row.getCell(2);
        if (ExcelUtils.isNotEmptyCell(cell) && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())) {
            if (!cell.getStringCellValue().equals("Capital Commitments")) {
                logger.error("File header check failed for 'Schedule of Investments' file: row=5, cell=2. Expected: 'Capital Commitments'");
                throw new ExcelFileParseException("File header check failed: row=5, cell=2. Expected: 'Capital Commitments'");
            }
        }else{
            logger.error("File header check failed for 'Schedule of Investments' file: row=5, cell=2. Expected: 'Capital Commitments'");
            throw new ExcelFileParseException("File header check failed: row=5, cell=2. Expected: 'Capital Commitments'");
        }
        cell = row.getCell(3);
        if (ExcelUtils.isNotEmptyCell(cell) && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())) {
            if (!cell.getStringCellValue().equals("Net Cost")) {
                logger.error("File header check failed for 'Schedule of Investments' file: row=5, cell=2. Expected: 'Net Cost'");
                throw new ExcelFileParseException("File header check failed: row=5, cell=2. Expected: 'Net Cost'");
            }
        }else{
            throw new ExcelFileParseException("File header check failed: row=5, cell=2. Expected: 'Net Cost'");
        }
        cell = row.getCell(4);
        if (cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())) {
            if (!cell.getStringCellValue().equals("Fair Value")) {
                logger.error("File header check failed for 'Schedule of Investments' file: row=5, cell=2. Expected: 'Fair Value'");
                throw new ExcelFileParseException("File header check failed: row=5, cell=2. Expected: 'Fair Value'");
            }
        }else{
            logger.error("File header check failed for 'Schedule of Investments' file: row=5, cell=2. Expected: 'Fair Value'");
            throw new ExcelFileParseException("File header check failed: row=5, cell=2. Expected: 'Fair Value'");
        }
    }

    /* Statement of Assets, Liabilities, Partners Capital ****************************/

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
                        recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partner's Capital")){
                    recordDto.getClassifications()[0] = null;
                    balanceRecordsSheet1.add(recordDto);
                }else if(recordDto.getClassifications() != null &&
                        recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Operations")){
                    recordDto.getClassifications()[0] = null;
                    operationsRecordsSheet1.add(recordDto);
                }else{
                    logger.error("[Tranche A]Record '" + recordDto.getName() + "' is missing type header: " +
                            "'Consolidated Statement of Assets, Liabilities and Partner's Capital' or 'Consolidated Statement of Operations'");
                    throw new ExcelFileParseException("[Tranche A]Record '" + recordDto.getName() + "' is missing type header: " +
                            "'Consolidated Statement of Assets, Liabilities and Partner's Capital' or 'Consolidated Statement of Operations'");
                }
            }

            // Sheet 2 - Tranche B
            rowIterator = getRowIterator(filesDto, 1);
            sheet2Records = parseStatementAssetsLiabilitiesSheetRaw(rowIterator, false);

            List<ConsolidatedReportRecordDto> balanceRecordsSheet2 = new ArrayList<>();
            List<ConsolidatedReportRecordDto> operationsRecordsSheet2 = new ArrayList<>();
            for(ConsolidatedReportRecordDto recordDto: sheet2Records){
                if(recordDto.getClassifications() != null &&
                        recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partner's Capital")){
                    recordDto.getClassifications()[0] = null;
                    balanceRecordsSheet2.add(recordDto);
                }else if(recordDto.getClassifications() != null &&
                        recordDto.getClassifications()[0].trim().equalsIgnoreCase("Consolidated Statement of Operations")){
                    recordDto.getClassifications()[0] = null;
                    operationsRecordsSheet2.add(recordDto);
                }else{
                    logger.error("[Tranche B]Record '" + recordDto.getName() + "' is missing type header: " +
                            "'Consolidated Statement of Assets, Liabilities and Partner's Capital' or 'Consolidated Statement of Operations'");
                    throw new ExcelFileParseException("[Tranche B]Record '" + recordDto.getName() + "' is missing type header: " +
                            "'Consolidated Statement of Assets, Liabilities and Partner's Capital' or 'Consolidated Statement of Operations'");
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

            /* CHECK SUMS/TOTALS ********************************************************************************/

            // Balance (Assets, Liabilities, Partners Capital)
            List<ConsolidatedReportRecordDto> updatedBalanceRecordsTrancheA = checkTotalSumsGeneric(balanceRecordsSheet1, 7, null, 1);
            List<ConsolidatedReportRecordDto> updatedBalanceRecordsTrancheB = checkTotalSumsGeneric(balanceRecordsSheet2, 7, null, 2);

            List<ConsolidatedReportRecordDto> checkedRecordsA = checkTotalSumsStatementAssetsLiabilities(updatedBalanceRecordsTrancheA);
            List<ConsolidatedReportRecordDto> checkedRecordsB = checkTotalSumsStatementAssetsLiabilities(updatedBalanceRecordsTrancheB);

            // Operations
            List<ConsolidatedReportRecordDto> updatedOperationsRecordsTrancheA = checkTotalSumsGeneric(operationsRecordsSheet1, 7, "Net increase (decrease) in partner's capital resulting from operations", 1);
            List<ConsolidatedReportRecordDto> updatedOperationsRecordsTrancheB = checkTotalSumsGeneric(operationsRecordsSheet2, 7, "Net increase (decrease) in partner's capital resulting from operations", 2);

            //checkTotalSumsStatementAssetsLiabilities(updatedBalanceRecordsTrancheA);
            //checkTotalSumsStatementAssetsLiabilities(updatedBalanceRecordsTrancheB);

            /* CHECK ENTITIES AND ASSEMBLE **********************************************************************/

            // BALANCE
            // sheet 1 - Tranche A
            List<ReportingPEStatementBalance> entities1 = this.statementBalanceService.assembleList(checkedRecordsA, 1, reportId); // TODO: tranche type constant !!!
            // sheet 2 - Tranche B
            List<ReportingPEStatementBalance> entities2 = this.statementBalanceService.assembleList(checkedRecordsB, 2, reportId); // TODO: tranche type constant !!!

            // TODO: OPERATIONS
            // sheet 1 - Tranche A
            List<ReportingPEStatementOperations> operationsEntities1 = this.statementOperatinsService.assembleList(updatedOperationsRecordsTrancheA, 1, reportId); // TODO: tranche type constant !!!
            // sheet 2 - Tranche B
            List<ReportingPEStatementOperations> operationsEntities2 = this.statementOperatinsService.assembleList(updatedOperationsRecordsTrancheB, 2, reportId); // TODO: tranche type constant !!!


            /* SAVE TO DB **************************************************************************************/
            // BALANCE
            boolean savedBalance = this.statementBalanceService.save(entities1);
            if(savedBalance) {
                savedBalance = this.statementBalanceService.save(entities2);
            }
            if(!savedBalance){
                // TODO: rollback? or transactional?

                logger.error("Error saving 'Schedule of Investments' file parsed data into database (statement of balance)");
                return new FileUploadResultDto(StatusResultType.FAIL, "", "Error saving to database (statement of balance)", "");
            }

            // OPERATIONS
            boolean savedOperations = this.statementOperatinsService.save(operationsEntities1);
            if(savedOperations) {
                savedOperations = this.statementOperatinsService.save(operationsEntities2);
            }

            if(savedOperations){
                logger.info("Successfully parsed 'Statement of Assets, Liabilities and Partners Capital' file");
                return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - Statement of Assets, Liabilities and Partners Capital", "");
            }else{
                // TODO: rollback? or transactional?

                logger.error("Error saving 'Schedule of Investments' file parsed data into database (statement of operations)");
                return new FileUploadResultDto(StatusResultType.FAIL, "", "Error saving to database (statement of operations)", "");
            }
        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Statement of Assets, Liabilities and Partners Capital' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Statement of Assets, Liabilities and Partners Capital' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error processing 'Statement of Assets, Liabilities and Partners Capital' file'", "");
        }
    }

    @Deprecated
    private ConsolidatedStatementType getStatementType(Cell cell){
        if(cell.getStringCellValue().equalsIgnoreCase("Assets")){
            return ConsolidatedStatementType.ASSETS;
        }else if(cell.getStringCellValue().equalsIgnoreCase("Liabilities")){
            return ConsolidatedStatementType.LIABILITIES;
//        }else if(cell.getStringCellValue().equalsIgnoreCase("Partner's capital")){
//            return ConsolidatedStatementType.PARTNERS_CAPITAL;
        }else if(cell.getStringCellValue().equalsIgnoreCase("Income:")){
            return ConsolidatedStatementType.INCOME;
        }else if(cell.getStringCellValue().equalsIgnoreCase("Expenses:")){
            return ConsolidatedStatementType.EXPENSES;
//        }else if(cell.getStringCellValue().contains("Net investment")){
//            return ConsolidatedStatementType.NET_INVESTMENT;
//        }else if(cell.getStringCellValue().equalsIgnoreCase("Net increase in partner's capital") ||
//                cell.getStringCellValue().equalsIgnoreCase("Net decrease in partner's capital")){
//            return ConsolidatedStatementType.NET_PARTNERS_CAPITAL;
        }
        return null;
    }

    private List<ConsolidatedReportRecordDto> parseStatementAssetsLiabilitiesSheetRaw(Iterator<Row> rowIterator, boolean isTrancheA){

        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        int rowNum = 0;
        String[] classifications = new String[5]; // TODO: size? dynamic?
        while (rowIterator.hasNext()) { // each row
            Row row = rowIterator.next();

            if(isTrancheA && rowNum <= 5){
                if(rowNum == 1){
                    Cell cell = row.getCell(2);
                    if (!ExcelUtils.isCellStringValueEqual(cell, "Tarragon Master Fund LP")) {
                        logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file: row=2, cell=3. Expected: 'Tarragon Master Fund LP'");
                        throw new ExcelFileParseException("Table header check failed (Tranche A): row=2, cell=3. Expected: 'Tarragon Master Fund LP'");
                    }
                }else if(rowNum == 2){
                    if(!ExcelUtils.isCellStringValueEqual(row.getCell(14), "NICK Master")){
                        logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file: row=3, cell=15. Expected: 'NICK Master'");
                        throw new ExcelFileParseException("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file: row=3, cell=15. Expected: 'NICK Master'");
                    }
                }else if (rowNum == 3){
                    if (!ExcelUtils.isCellStringValueEqual(row.getCell(10), "NICK Master")) {
                        logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file: row=4, cell=11. Expected: 'NICK Master'");
                        throw new ExcelFileParseException("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file: row=4, cell=11. Expected: 'NICK Master'");
                    }
                    if (!ExcelUtils.isCellStringValueEqual(row.getCell(14), "Fund Ltd.'s Share")) {
                        logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file: row=4, cell=15. Expected: 'Fund Ltd.'s Share'");
                        throw new ExcelFileParseException("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file: row=4, cell=11. Expected: 'Fund Ltd.'s Share'");
                    }
                }else if (rowNum == 4){
                    boolean tableHeaders = ExcelUtils.isCellStringValueEqual(row.getCell(4), "Tarragon GP") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(6), "NICK Master") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(10), "Fund Ltd.'s") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(12), "Consolidation") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(14), "of Tranche A");
                    if (!tableHeaders) {
                        logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file: row=5.");
                        throw new ExcelFileParseException("Table header check failed (Tranche A) : row=5.");
                    }
                }else if (rowNum == 5){
                    boolean tableHeaders = ExcelUtils.isCellStringValueEqual(row.getCell(2),"Total*") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(4), "LLC's Share") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(6), "Fund Ltd.'s Share") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(8), "Tarragon LP") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(10), "Share of Total") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(12), "Adjustments") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(14), "Consolidated");
                    if(!tableHeaders){
                        logger.error("Table header check failed (Tranche A) for 'Statement of Assets, Liabilities and Partners Capital' file: row=6.");
                        throw new ExcelFileParseException("Table header check failed (Tranche A): row=6.");
                    }
                }
            }else if(!isTrancheA && rowNum <= 2){
                if(rowNum == 1){
                    if(!ExcelUtils.isCellStringValueEqual(row.getCell(2), "Tarragon")){
                        logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file: row=2, cell=3. Expected: 'Tarragon'");
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=2, cell=3. Expected: 'Tarragon'");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(8), "Consolidation")){
                        logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file: row=2, cell=9. Expected: 'Consolidation'");
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=2, cell=9. Expected: 'Consolidation'");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(10), "Tranche B")){
                        logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file: row=2, cell=11. Expected: 'Tranche B'");
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=2, cell=11. Expected: 'Tranche B'");
                    }
                }else if(rowNum == 2){
                    if(!ExcelUtils.isCellStringValueEqual(row.getCell(2), "Master Fund LP")){
                        logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file: row=3, cell=3. Expected 'Master Fund LP'");
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=3, cell=3. Expected 'Master Fund LP'");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(4), "Tarragon LP")){
                        logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file: row=3, cell=5. Expected 'Tarragon LP'");
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=3, cell=5. Expected 'Tarragon LP'");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(6), "Total")){
                        logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file: row=3, cell=7. Expected 'Total'");
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=3, cell=7. Expected 'Total'");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(8), "Adjustments")){
                        logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file: row=3, cell=9. Expected 'Adjustments'");
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=3, cell=9. Expected 'Adjustments'");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(10), "Consolidated")){
                        logger.error("Table header check failed (Tranche B) for 'Statement of Assets, Liabilities and Partners Capital' file: row=3, cell=11. Expected 'Consolidated'");
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=3, cell=11. Expected 'Consolidated'");
                    }
                }
            }else{ /* Rows with data, not headers */
                Cell cell = row.getCell(0);
                if(ExcelUtils.isNotEmptyCell(cell) && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
                    if(cell.getStringCellValue().trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partner's Capital")){

                    }else if(cell.getStringCellValue().trim().equalsIgnoreCase("Consolidated Statement of Operations")){
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

                    } else{
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

                        // totalsum
                        recordDto.setFormula(row.getCell(2).getCellType() == Cell.CELL_TYPE_FORMULA);
                        records.add(recordDto);

                        boolean reset = false;
                        for(int i = 0; i < classifications.length; i++){
                            if(reset){
                                classifications[i] = null;
                            }else if(classifications[i] != null &&
                                    (name.equalsIgnoreCase("total " + classifications[i]) ||
                                    (name + ":").equalsIgnoreCase("total " + classifications[i]) ||
                                    (name).equalsIgnoreCase("total " + classifications[i] + ":") ||
                                    name.equalsIgnoreCase("net " + classifications[i]) ||
                                    (name + ":").equalsIgnoreCase("net " + classifications[i])) ||
                                    (name).equalsIgnoreCase("net " + classifications[i] + ":")){
                                classifications[i] = null;
                                reset = true;
                            }
                        }


                    }

                }

            }
            rowNum++;
        }
        return records;

    }

    private List<ConsolidatedReportRecordDto> checkTotalSumsStatementAssetsLiabilities(List<ConsolidatedReportRecordDto> records){

        List<ConsolidatedReportRecordDto> resultList = new ArrayList<>();
        Double[] assetsTotal = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        Double[] liabilitiesTotal = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        Double[] partnersCapitalTotal = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        for(ConsolidatedReportRecordDto recordDto: records){
            if(recordDto.getName().equalsIgnoreCase("Total liabilities and partner's capital")){
                // check liabiliies and partners capital total sum
                for(int i = 0; i < liabilitiesTotal.length; i++){
                    double liabilitiesValue = liabilitiesTotal[i]  != null ? liabilitiesTotal[i].doubleValue(): 0.0;
                    double partnersCapitalValue = partnersCapitalTotal[i]  != null ? partnersCapitalTotal[i].doubleValue(): 0.0;
                    double recordValue = recordDto.getValues() != null  && recordDto.getValues()[i] != null ? recordDto.getValues()[i].doubleValue() : 0.0;
                    if((liabilitiesValue + partnersCapitalValue) != recordValue){
                        throw new ExcelFileParseException("Error checking Total liabilities and partner's capital - for values #" + (i + 1) +
                                ": expected=" + (liabilitiesValue + partnersCapitalValue) + ", found=" + recordValue);
                    }
                }
            }else {
                if(recordDto.getName().startsWith("Total ") && recordDto.hasClassification(recordDto.getName().substring(5).trim())){
                    // is total value

                }else if(recordDto.hasClassification("ASSETS")){ // TODO: final strings
                    ArrayUtils.addArrayValues(assetsTotal, recordDto.getValues());
                }else if(recordDto.hasClassification("LIABILITIES")){// TODO: final strings
                    ArrayUtils.addArrayValues(liabilitiesTotal, recordDto.getValues());
                }else if(recordDto.hasClassification("PARTNER'S CAPITAL") ||
                        recordDto.getName().trim().equalsIgnoreCase("PARTNER'S CAPITAL")){ // TODO: final strings
                    ArrayUtils.addArrayValues(partnersCapitalTotal, recordDto.getValues());
                }

                resultList.add(recordDto);
            }
        }

        // Check:
        // ASSETS = LIABILITIES + PARTNERS CAPITAL
        for(int i = 0; i < assetsTotal.length; i++){
            Double assetsValue = assetsTotal[i]  != null ? assetsTotal[i].doubleValue(): 0.0;
            Double liabilitiesValue = liabilitiesTotal[i]  != null ? liabilitiesTotal[i].doubleValue(): 0.0;
            Double partnersCapitalValue = partnersCapitalTotal[i]  != null ? partnersCapitalTotal[i].doubleValue(): 0.0;
            if(assetsValue != (liabilitiesValue + partnersCapitalValue)){
                throw new ExcelFileParseException("Error checking total sums for values #" + (i + 1) +
                        ": assets=" + assetsValue + ", liabilities and partner's capital =" + (liabilitiesValue + partnersCapitalValue));
            }
        }

        return records;
        //return resultList;
    }


    /* Statement of Cash Flows ********************************************************/
    private FileUploadResultDto parseStatementCashFlows(FilesDto filesDto, Long reportId){

        try {

            /* PARSE EXCEL (RAW) *******************************************************************************/
            Iterator<Row> rowIterator = getRowIterator(filesDto, 0);
            List<ConsolidatedReportRecordDto> records = parseStatementCashFlowsRaw(rowIterator);
            //printRecords(records);

            /* NORMALIZE TEXT FIELDS ********************************************************************************/
            normalizeTextFields(records);

            /* CHECK SUMS/TOTALS ********************************************************************************/
            List<ConsolidatedReportRecordDto> updatedRecords = checkTotalSumsGeneric(records, 3, "Net increase (decrease) in cash and cash equivalents", 0);
            List<ConsolidatedReportRecordDto> checkedRecords = checkSumsStatementCashflows(updatedRecords);


            /* CHECK ENTITIES AND ASSEMBLE **********************************************************************/
            List<ReportingPEStatementCashflows> entities = this.statementCashflowsService.assembleList(checkedRecords, reportId); // TODO: tranche type constant !!!

            /* SAVE TO DB **************************************************************************************/
            boolean saved = this.statementCashflowsService.save(entities);

            if(saved){
                logger.info("Successfully parsed 'Statement of Cashflows' file");
                return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - Statement of Cashflows", "");
            }else{
                logger.error("Error saving 'Statement of Cashflows' file parsed data into database");
                return new FileUploadResultDto(StatusResultType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Statement of Cashflows' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Statement of Cashflows' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error processing 'Statement of Cashflows' file'", "");
        }
    }

    private List<ConsolidatedReportRecordDto> checkSumsStatementCashflows(List<ConsolidatedReportRecordDto> records){
        List<ConsolidatedReportRecordDto> resultList = new ArrayList<>();
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

                    ArrayUtils.addArrayValues(totals, record.getValues());
                    resultList.add(record);
                }
            }

        }
        return resultList;
    }

    private List<ConsolidatedReportRecordDto> parseStatementCashFlowsRaw(Iterator<Row> rowIterator){
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        int rowNum = 0;
        String[] classifications = new String[5]; // TODO: size?
        Integer[] indentations = new Integer[5]; // TODO: size?
        while (rowIterator.hasNext()) { // each row
            Row row = rowIterator.next();
            if(rowNum == 0){
                Cell cell = row.getCell(0);
                if (!ExcelUtils.isCellStringValueEqual(cell, "Tarragon LP")) {
                    logger.error("File header check failed for 'Statement of Cashflows' file: row=1, cell=1. Expected 'Tarragon LP'");
                    throw new ExcelFileParseException("File header check failed : row=1, cell=1. Expected 'Tarragon LP'");
                }
            }else if(rowNum == 1){
                Cell cell = row.getCell(0);
                if (!ExcelUtils.isCellStringValueEqual(cell, "Consolidated Statement of Cash Flows for NICK Master Fund Ltd.")) {
                    logger.error("File header check failed for 'Statement of Cashflows' file: row=2, cell=1. Expected: 'Consolidated Statement of Cash Flows for NICK Master Fund Ltd.'");
                    throw new ExcelFileParseException("File header check failed : row=2, cell=1. " +
                            "Expected: 'Consolidated Statement of Cash Flows for NICK Master Fund Ltd.'");
                }
            }else if(rowNum == 2){

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

                        recordDto.setFormula(row.getCell(2).getCellType() == Cell.CELL_TYPE_FORMULA);

                        recordDto.setValues(values);

                        // classifications
                        recordDto.setClassifications(Arrays.copyOf((String[]) classifications, 10));
                        for (int i = 0; i < indentations.length; i++) {
                            if (indentations[i] == null || indentations[i] >= indentation) {
                                if(!recordDto.getName().equalsIgnoreCase("Net " + classifications[i])){
                                    recordDto.clearClassification(i);
                                }
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

    /* Statement of Changes in Partner's Capital ********************************************************/
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
                return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - Statement of Changes in Partners Capital", "");
            }else{
                logger.error("Error saving 'Statement of Changes in Partners Capital' file parsed data into database");
                return new FileUploadResultDto(StatusResultType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Statement of Changes in Partners Capital' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Statement of Changes in Partners Capital' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error processing 'Statement of Changes in Partners Capital' file'", "");
        }
    }

    private List<ConsolidatedReportRecordDto> checkSumsStatementChanges(List<ConsolidatedReportRecordDto> records){
        List<ConsolidatedReportRecordDto> recordsWithoutTotals = new ArrayList<>();
        Double[] totalSum = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        if(records != null){
            for(ConsolidatedReportRecordDto record: records){
                // Check 'Net Investment income (loss)'
                Double sum = ArrayUtils.sumArray(record.getValues(), 3, 11);
                sum = sum != null ? sum : 0.0;
                double expectedTotal = (record.getValues()[12] != null ? record.getValues()[12].doubleValue() : 0.0);
                if(sum.doubleValue() != expectedTotal){
                    logger.error("Error checking TOTAL for record '" + record.getName() + "' : expected " + expectedTotal + ", found " + sum.doubleValue());
                    throw new ExcelFileParseException("Error checking TOTAL for record '" + record.getName() + "' : expected " + expectedTotal + ", found " + sum.doubleValue());
                }
                // Check 'Ending Capital Balance before potential carried interest'
                Double calculatedEndingCapitalBalanceBefore = ArrayUtils.sumArray(record.getValues(), 0, 11);
                calculatedEndingCapitalBalanceBefore = calculatedEndingCapitalBalanceBefore != null ? calculatedEndingCapitalBalanceBefore : 0.0;
                Double gains = ArrayUtils.sumArray(record.getValues(), 13, 14);
                gains = gains != null ? gains : 0.0;
                calculatedEndingCapitalBalanceBefore = calculatedEndingCapitalBalanceBefore.doubleValue() + gains.doubleValue();
                double expectedEndingCapitalBalanceBefore = (record.getValues()[15] != null ? record.getValues()[15].doubleValue() : 0.0);
                if(calculatedEndingCapitalBalanceBefore.doubleValue() != expectedEndingCapitalBalanceBefore){
                    logger.error("Error checking 'Ending Capital Balance Before potential carried interest' for record '" + record.getName() +
                            "' : expected " + expectedEndingCapitalBalanceBefore + ", found " + calculatedEndingCapitalBalanceBefore.doubleValue());
                    throw new ExcelFileParseException("Error checking 'Ending Capital Balance Before potential carried interest' for record '" + record.getName() +
                            "' : expected " + expectedEndingCapitalBalanceBefore + ", found " + calculatedEndingCapitalBalanceBefore.doubleValue());
                }

                // Check 'Ending Capital Balance after potential carried interest'
                Double potentialInterests = ArrayUtils.sumArray(record.getValues(), 16, 17);
                potentialInterests = potentialInterests != null ? potentialInterests : 0.0;
                Double calculatedEndingCapitalBalanceAfter = calculatedEndingCapitalBalanceBefore.doubleValue() + potentialInterests.doubleValue();
                double expectedEndingCapitalBalanceAfter = (record.getValues()[18] != null ? record.getValues()[18].doubleValue() : 0.0);
                if(calculatedEndingCapitalBalanceAfter.doubleValue() != expectedEndingCapitalBalanceAfter){
                    logger.error("Error checking 'Ending Capital Balance After potential carried interest' for record '" + record.getName() +
                            "' : expected " + expectedEndingCapitalBalanceAfter + ", found " + calculatedEndingCapitalBalanceAfter.doubleValue());
                    throw new ExcelFileParseException("Error checking 'Ending Capital Balance After potential carried interest' for record '" + record.getName() +
                            "' : expected " + expectedEndingCapitalBalanceAfter + ", found " + calculatedEndingCapitalBalanceAfter.doubleValue());
                }

                if(record.getName().equalsIgnoreCase("Total")){
                    // Check total
                    if(record.getValues() != null){
                        for(int i = 0; i < record.getValues().length; i++){
                            Double value = record.getValues()[i] != null ? record.getValues()[i] : 0.0;
                            if(value.doubleValue() != totalSum[i].doubleValue()){
                                logger.error("Error checking totals for record '" + record.getName() +
                                        "' for values #" + (i + 1) + ": expected " + totalSum[i] + ", found " + record.getValues()[i]);
                                throw new ExcelFileParseException("Error checking totals for record '" + record.getName() +
                                        "' for values #" + (i + 1) + ": expected " + totalSum[i] + ", found " + record.getValues()[i]);
                            }
                        }
                    }
                }else{
                    ArrayUtils.addArrayValues(totalSum, record.getValues());
                    recordsWithoutTotals.add(record);
                }
            }
        }

        return recordsWithoutTotals;
    }

    private List<ConsolidatedReportRecordDto> parseStatementChangesRaw(Iterator<Row> rowIterator){
        int[] indicesWithValues = {2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,18,20,22,24};
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        int rowNum = 0;
        while (rowIterator.hasNext()) { // each row
            Row row = rowIterator.next();

            if(rowNum == 0){
                Cell cell = row.getCell(0);
                if (!ExcelUtils.isCellStringValueEqual(cell, "Tarragon LP")) {
                    logger.error("File header check failed for 'Statement of Changes in Partners Capital' file: row=1, cell=1. Expected 'Tarragon LP'");
                    throw new ExcelFileParseException("File header check failed : row=1, cell=1. Expected 'Tarragon LP'");
                }
            }else if(rowNum == 1){
                Cell cell = row.getCell(0);
                if (!ExcelUtils.isCellStringValueEqual(cell, "Consolidated Statement of Changes in Partner’s Capital for NICK Master Fund Ltd.")) {
                    logger.error("File header check failed for 'Statement of Changes in Partners Capital' file: row=2, cell=1. Expected 'Consolidated Statement of Changes in Partner’s Capital for NICK Master Fund Ltd.'");
                    throw new ExcelFileParseException("File header check failed : row=2, cell=1. Expected 'Consolidated Statement of Changes in Partner’s Capital for NICK Master Fund Ltd.'");
                }
            }else if(rowNum == 2){
                // TODO: check date?
            }else if(rowNum == 4){
                checkStatementChangesTableHeader(row, indicesWithValues);
            }else if(rowNum > 4){
                if(ExcelUtils.isNotEmptyCell(row.getCell(0))){
                    ConsolidatedReportRecordDto recordDto = new ConsolidatedReportRecordDto();
                    String name = row.getCell(0).getStringCellValue();
                    Double[] values = {ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[0])),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[1])), ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[2])),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[3])), ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[4])),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[5])), ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[6])),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[7])), ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[8])),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[9])), ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[10])),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[11])), ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[12])),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[13])), ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[14])),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[15])), ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[16])),
                            ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[17])), ExcelUtils.getDoubleValueFromCell(row.getCell(indicesWithValues[18]))};
                    recordDto.setName(name);

                    recordDto.setFormula(row.getCell(indicesWithValues[0]).getCellType() == Cell.CELL_TYPE_FORMULA);

                    recordDto.setValues(values);
                    records.add(recordDto);
                }else{
                    // empty cell, skip
                }
            }
            rowNum++;
        }
        //printRecords(records);
        return records;
    }

    private void checkStatementChangesTableHeader(Row row, int[] indices){
        Cell cell = row.getCell(indices[0]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Beginning Capital Balance Before Potential Carried Interest")) {
            logger.error("File header check failed for 'Statement of Changes in Partners Capital' file: cell=3. Expected 'Beginning Capital Balance Before Potential Carried Interest'");
            throw new ExcelFileParseException("File header check failed: cell=3. Expected 'Beginning Capital Balance Before Potential Carried Interest'");
        }
        cell = row.getCell(indices[1]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Capital Contributions")) {
            logger.error("File header check failed for 'Statement of Changes in Partners Capital' file: cell=4. Expected 'Capital Contributions'");
            throw new ExcelFileParseException("File header check failed: cell=4. Expected 'Capital Contributions'");
        }
        cell = row.getCell(indices[2]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Distributions")) {
            logger.error("File header check failed for 'Statement of Changes in Partners Capital' file: cell=5. Expected 'Distributions'");
            throw new ExcelFileParseException("File header check failed: cell=5. Expected 'Distributions'");
        }
        cell = row.getCell(indices[3]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Dividend and Interest Income, Net of Related Taxes")) {
            logger.error("File header check failed for 'Statement of Changes in Partners Capital' file: cell=6. Expected 'Dividend and Interest Income, Net of Related Taxes'");
            throw new ExcelFileParseException("File header check failed: cell=6. Expected 'Dividend and Interest Income, Net of Related Taxes'");
        }
        cell = row.getCell(indices[4]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Other Income")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=7. Expected 'Other Income'");
            throw new ExcelFileParseException("File header check failed: cell=7. Expected 'Other Income'");
        }
        cell = row.getCell(indices[5]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Management Fee")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=8. Expected 'Management Fee'");
            throw new ExcelFileParseException("File header check failed: cell=8. Expected 'Management Fee'");
        }
        cell = row.getCell(indices[6]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Administration Fees")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=9. Expected 'Administration Fees'");
            throw new ExcelFileParseException("File header check failed: cell=9. Expected 'Administration Fees'");
        }
        cell = row.getCell(indices[7]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Audit and tax fees")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=10. Expected 'Audit and tax fees'");
            throw new ExcelFileParseException("File header check failed: cell=10. Expected 'Audit and tax fees'");
        }
        cell = row.getCell(indices[8]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Organizational Costs")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=11. Expected 'Organizational Costs'");
            throw new ExcelFileParseException("File header check failed: cell=11. Expected 'Organizational Costs'");
        }
        cell = row.getCell(indices[9]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Interest Expense - PEP's")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=12. Expected 'Interest Expense - PEP's'");
            throw new ExcelFileParseException("File header check failed: cell=12. Expected 'Interest Expense - PEP's'");
        }
        cell = row.getCell(indices[10]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("License and Filing Fees")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=13. Expected 'License and Filing Fees'");
            throw new ExcelFileParseException("File header check failed: cell=13. Expected 'License and Filing Fees'");
        }
        cell = row.getCell(indices[11]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Other Expenses")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=14. Expected 'Other Expenses'");
            throw new ExcelFileParseException("File header check failed: cell=14. Expected 'Other Expenses'");
        }
        cell = row.getCell(indices[12]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Total")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=15. Expected 'Total'");
            throw new ExcelFileParseException("File header check failed: cell=15. Expected 'Total'");
        }
        cell = row.getCell(indices[13]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Realized Gain (Loss)")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=16. Expected 'Realized Gain (Loss)'");
            throw new ExcelFileParseException("File header check failed: cell=16. Expected 'Realized Gain (Loss)'");
        }
        cell = row.getCell(indices[14]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Unrealized Gain (Loss)")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=17. Expected 'Unrealized Gain (Loss)'");
            throw new ExcelFileParseException("File header check failed: cell=17. Expected 'Unrealized Gain (Loss)'");
        }
        cell = row.getCell(indices[15]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Ending Capital Balance before potential carried interest")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=19. Expected 'Ending Capital Balance before potential carried interest'");
            throw new ExcelFileParseException("File header check failed: cell=19. Expected 'Ending Capital Balance before potential carried interest'");
        }
        cell = row.getCell(indices[16]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Prior year - Potential carried interest allocated to general partner")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=21. Expected 'Prior year - Potential carried interest allocated to general partner'");
            throw new ExcelFileParseException("File header check failed: cell=21. Expected 'Prior year - Potential carried interest allocated to general partner'");
        }
        cell = row.getCell(indices[17]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Current year - Potential carried interest allocated to general partner")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=23. Expected 'Current year - Potential carried interest allocated to general partner'");
            throw new ExcelFileParseException("File header check failed: cell=23. Expected 'Current year - Potential carried interest allocated to general partner'");
        }
        cell = row.getCell(indices[18]);
        if (!cell.getStringCellValue().trim().equalsIgnoreCase("Ending Capital Balance after potential carried interest")) {
            logger.error("File header check  failed for 'Statement of Changes in Partners Capital' file: cell=25. Expected 'Ending Capital Balance after potential carried interest'");
            throw new ExcelFileParseException("File header check failed: cell=25. Expected 'Ending Capital Balance after potential carried interest'");
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
            return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - BS Singularity", "");

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Balance Sheet Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Balance Sheet Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error processing 'Balance Sheet Singularity' file'", "");
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
            return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - IMDR Singularity", "");

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'IMDR Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'IMDR Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error processing 'IMDR Singularity' file'", "");
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
            return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - PAR Singularity", "");

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'PAR Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'PAR Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error processing 'PAR Singularity' file'", "");
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
            return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - IS Singularity", "");

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Income Statement Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Income Statement Singularity' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error processing 'Income Statement Singularity' file'", "");
        }
    }

    private FileUploadResultDto parseISTrancheA(FilesDto filesDto){
        return parseIS(filesDto);
    }

    private FileUploadResultDto parseISTrancheB(FilesDto filesDto){
        return parseIS(filesDto);
    }


    /* COMMON *******************************************************************************************/

    private Iterator<Row> getRowIterator(FilesDto filesDto, int sheetNumber){
        InputStream inputFile = new ByteArrayInputStream(filesDto.getBytes());
        String extension = filesDto.getFileName().substring(filesDto.getFileName().lastIndexOf(".") + 1,
                filesDto.getFileName().length());
        try {
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

    private List<ConsolidatedReportRecordDto> checkTotalSumsGeneric(List<ConsolidatedReportRecordDto> fullList, int size,
                                                                    String totalRecordName, int tranche){

        // TODO: REFACTOR, HARD TO MAINTAIN


        String trancheName = tranche == 1 ? "[Tranche A]" : tranche == 2 ? "[Tranche B]" : "";

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

                // Cut off ':' in name
                if(StringUtils.isNotEmpty(recordDto.getName()) && recordDto.getName().endsWith(":")){
                    recordDto.setName(recordDto.getName().substring(0, recordDto.getName().length() - 1));
                }

                if(StringUtils.isNotEmpty(totalRecordName) && totalRecordName.equalsIgnoreCase(recordDto.getName())) {
                    // total sum
                    totalRecordDto = recordDto;
                    if(StringUtils.isNotEmpty(totalRecordName)){
                        double[] totalValues = sums.get(totalRecordName);
                        if (totalValues != null) {
                            for (int i = 0; i < totalValues.length; i++) {
                                if (totalRecordDto.getValues() != null && totalRecordDto.getValues()[i] != null) {
                                    if (totalValues[i] != totalRecordDto.getValues()[i]) {
                                        // total sum mismatch
                                        logger.error(trancheName + "Error checking totals for record '" + totalRecordDto.getName() +
                                                "' for values #" + (i + 1) + ": expected " + totalValues[i] + ", found " + totalRecordDto.getValues()[i]);
                                        throw new ExcelFileParseException(trancheName + "Error checking totals for record '" + totalRecordDto.getName() +
                                                "' for values #" + (i + 1) + ": expected " + totalValues[i] + ", found " + totalRecordDto.getValues()[i]);
                                    }
                                }
                            }
                        }

                    }
                }else if(recordDto.getClassifications() != null){

                    boolean isTotal = recordDto.getName().startsWith("Total") && recordDto.getName().length() > 5 &&
                            recordDto.hasClassification(recordDto.getName().substring(5).trim().toLowerCase());

                    boolean isNet = recordDto.getName().startsWith("Net") && recordDto.getName().length() > 3 &&
                            recordDto.hasClassification(recordDto.getName().substring(3).trim().toLowerCase());

                    if(!isTotal && !isNet && (recordDto.getName().startsWith("Net") || recordDto.getName().startsWith("Total")) && !recordDto.getName().equalsIgnoreCase(totalRecordName)){
                        // Possible NET/TOTAL value

                        String strategyName = recordDto.getName().startsWith("Net") ? recordDto.getName().substring(3).trim() :
                                recordDto.getName().startsWith("Total") ? recordDto.getName().substring(5).trim() : null;

                        // strategy name cut off CURRENCY
//                        if(strategyName.trim().endsWith(" - USD") || strategyName.trim().endsWith(" - GBP")){
//                            strategyName = strategyName.substring(0, strategyName.length() - 6);
//                        }else if(strategyName.trim().endsWith(" - Euro")){
//                            strategyName = strategyName.substring(0, strategyName.length() - 7);
//                        }

                        boolean isStrategy = strategyName != null && this.strategyRepository.findByNameEnAndGroupType(strategyName, Strategy.TYPE_PRIVATE_EQUITY) != null;

                        if(recordDto.isFormula() || isStrategy) {
                            logger.error(trancheName + "Record '" + recordDto.getName() + "' appears to be NET/TOTAL value (e.g. contains formula, contains strategy name etc.), " +
                                    "but it does not match any classification/header.");
                            throw new ExcelFileParseException(trancheName + "Record '" + recordDto.getName() + "' appears to be NET/TOTAL value (e.g. contains formula, contains strategy name etc.), " +
                                    "but it does not match any classification/header.");
                        }
                    }


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
                                    "' values list size mismatch: expected "  + size + " , found "  + recordDto.getValues().length);
                            throw new ExcelFileParseException(trancheName + "Error checking totals for record '" + recordDto.getName() +
                                    "' values list size mismatch: expected "  + size + " , found "  + recordDto.getValues().length);
                        }

                        if(isTotal || isNet){
                            // Check total sum value
                            int nameIndex = isTotal ? 5 : 3;
                            String key = recordDto.getName().substring(nameIndex).trim().toLowerCase();
                            if(key.endsWith(":")){
                                key = key.substring(0, key.length() - 1);
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
                                isTotalValue = true;
                            }
                        }else if(sums.containsKey(classification)){
                            // update total values
                            double[] values = sums.get(classification);
                            if(values != null){
                                for(int i = 0; i < values.length; i++){
                                    values[i] += recordDto.getValues()[i] != null ? recordDto.getValues()[i] : 0.0;
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
                                if (totalSum != null) {
                                    totalSum[i] += recordDto.getValues()[i] != null ? recordDto.getValues()[i] : 0.0;
                                }
                            }
                        }
                    }
                }
            }

            // Check level 1 classification total/net value
            Iterator<String> iterator = sums.keySet().iterator();
            while(iterator.hasNext()){
                String classification = iterator.next();
                if(sums.get(classification) != null && classificationsLevel.get(classification) == 1){
                    // Level 1 classification, NET/TOTAL value missing
                    logger.error(trancheName + "Total value for classification '" + classification + "' is missing: has to match 'Net/Total " + classification + "'");
                    throw new ExcelFileParseException(trancheName + "Total value for classification '" + classification + "' is missing: has to match 'Net/Total " + classification + "'");
                }
            }


            // Check total record
            if(StringUtils.isNotEmpty(totalRecordName) && totalRecordDto == null) {
                logger.error(trancheName + "Total record '" + totalRecordName + "' is missing.");
                throw new ExcelFileParseException(trancheName + "Total record '" + totalRecordName + "' is missing.");
            }
        }
        return fullList;
    }

    /**
     * Remove trimming spaces, replace new line characters with spaces.
     * @param records
     */
    private void normalizeTextFields(List<ConsolidatedReportRecordDto> records){
        if(records != null){
            for(ConsolidatedReportRecordDto record: records){
                if(StringUtils.isNotEmpty(record.getName())){
                    record.setName(record.getName().trim().replace("\\n", " "));
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
}
