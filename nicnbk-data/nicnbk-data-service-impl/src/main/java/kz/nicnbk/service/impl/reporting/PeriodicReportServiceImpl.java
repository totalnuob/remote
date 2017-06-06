package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.util.ExcelUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.reporting.PeriodReportRepository;
import kz.nicnbk.repo.api.reporting.PeriodicReportFilesRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.PeriodicReportFiles;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.converter.reporting.PeriodReportConverter;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.exception.ExcelFileParseException;
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
                    fileDto.setType(entity.getFile().getType().getCode());
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
    public FileUploadResultDto parseFile(String fileType, FilesDto filesDto) {
        if(fileType.equals(FileTypeLookup.NB_REP_T1.getCode())){
            return parseScheduleInvestments(filesDto);
        }else if(fileType.equals(FileTypeLookup.NB_REP_T2.getCode())){
            return parseStatementAssetsLiabilities(filesDto);
        }else if(fileType.equals(FileTypeLookup.NB_REP_T3.getCode())){
            return parseStatementCashFlows(filesDto);
        }else if(fileType.equals(FileTypeLookup.NB_REP_T4.getCode())){
            return parseStatementChanges(filesDto);
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
            return fileUploadResultDto;
        }
    }


    /* Schedule of Investments ******************************************************/

    private FileUploadResultDto parseScheduleInvestments(FilesDto filesDto){

        ScheduleInvestmentsDto scheduleInvestmentsDtoTrancheA = new ScheduleInvestmentsDto();
        ScheduleInvestmentsDto scheduleInvestmentsDtoTrancheB = new ScheduleInvestmentsDto();
        try {
            InputStream inputFile = new ByteArrayInputStream(filesDto.getBytes());
            XSSFWorkbook workbook = new XSSFWorkbook(inputFile);

            // Sheet 1 - Tranche A
            XSSFSheet sheet = workbook.getSheetAt(0);
            FileUploadResultDto fileUploadResultDto = parseScheduleInvestmentsSheet(sheet, scheduleInvestmentsDtoTrancheA);
            if(fileUploadResultDto.getStatus() == StatusResultType.FAIL){
                return fileUploadResultDto;
            }

            // Sheet 2 - Tranche B
            XSSFSheet sheet2 = workbook.getSheetAt(1);
            fileUploadResultDto = parseScheduleInvestmentsSheet(sheet2, scheduleInvestmentsDtoTrancheB);

            // TODO: remove temp
            scheduleInvestmentsDtoTrancheA.print();
            scheduleInvestmentsDtoTrancheB.print();

            inputFile.close();

            return fileUploadResultDto;

        }catch (ExcelFileParseException e) {
            //logger.error("");
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            //logger.error("");
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error when processing 'Schedule of Investments' file", "");
        }

    }

    private void checkScheduleInvestmentsFileHeader(int rowNum, Row row){
        if (rowNum == 0) {/* ROW = 0 */
            // check file header
            Cell cell = row.getCell(0);
            if (!cell.getStringCellValue().equals("Tarragon Master Fund LP")) {
                //logger.error("");
                throw new ExcelFileParseException("File header check failed: row=1, cell=1");
            }
        } else if (rowNum == 1) {/* ROW = 1 */
            // check file header
            Cell cell = row.getCell(0);
            if (!cell.getStringCellValue().contains("Schedule of Investments - Tranche ")) {
                //logger.error("");
                throw new ExcelFileParseException("File header check failed: row=2, cell=1");
            }
        } else if (rowNum == 2) {/* ROW = 2 */
            // report date check
            try {
                Cell cell = row.getCell(0);
                Date reportDate = cell.getDateCellValue();
                if(reportDate != null ){

                    // TODO: check report date

                    return;
                }
            }catch (Exception ex){
                //do nothing, will be logged and thrown
            }
            //logger.error("");
            throw new ExcelFileParseException("File header check failed: row=3, cell=1 - report date");

        } else if (rowNum == 3) {/* ROW = 3 */
            // skip, empty row
        }
    }

    private void checkScheduleInvestmentsTableHeader(Row row){
        Cell cell = row.getCell(1);
        if (cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())) {
            if (!cell.getStringCellValue().equals("Investment")) {
                //logger.error("");
                throw new ExcelFileParseException("File header check failed: row=5, cell=2 - 'Investment' expected");
            }
        }else{
            throw new ExcelFileParseException("File header check failed: row=5, cell=2 - 'Investment' expected");
        }
        cell = row.getCell(2);
        if (cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())) {
            if (!cell.getStringCellValue().equals("Capital Commitments")) {
                //logger.error("");
                throw new ExcelFileParseException("File header check failed: row=5, cell=3 - 'Capital Commitments' expected");
            }
        }else{
            throw new ExcelFileParseException("File header check failed: row=5, cell=3 - 'Capital Commitments' expected");
        }
        cell = row.getCell(3);
        if (cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())) {
            if (!cell.getStringCellValue().equals("Net Cost")) {
                //logger.error("");
                throw new ExcelFileParseException("File header check failed: row=5, cell=4 - 'Net Cost' expected");
            }
        }else{
            throw new ExcelFileParseException("File header check failed: row=5, cell=4 - 'Net Cost' expected");
        }
        cell = row.getCell(4);
        if (cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())) {
            if (!cell.getStringCellValue().equals("Fair Value")) {
                //logger.error("");
                throw new ExcelFileParseException("File header mismatch: row=5, cell=5 - 'Fair Value' expected");
            }
        }else{
            throw new ExcelFileParseException("File header mismatch: row=5, cell=5 - 'Fair Value' expected");
        }
    }

    private String getCellCurrency(Cell cell, String currency) throws ExcelFileParseException{
        if(cell.getCellStyle().getDataFormatString().contains("€")){
            if(currency != null && !currency.equals("Euro")){
                throw new ExcelFileParseException("Currency mismatch: row=" + (cell.getRowIndex() + 1)+ ", cell=3");
            }
            return "Euro";
        }else if(cell.getCellStyle().getDataFormatString().contains("£")){
            if(currency != null && !currency.equals("GBP")){
                throw new ExcelFileParseException("Currency mismatch: row=" + (cell.getRowIndex() + 1)+ ", cell=3");
            }
            return "GBP";
        }else if(cell.getCellStyle().getDataFormatString().contains("$")){
            return "USD";
        }
        return currency;
    }

    private FileUploadResultDto parseScheduleInvestmentsSheet(XSSFSheet sheet, ScheduleInvestmentsDto scheduleInvestmentsDto)
            throws ExcelFileParseException{

        if(sheet == null){
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Excel sheet is null", "");
        }

        Iterator<Row> rowIterator = sheet.iterator();
        List<FundInvestmentDto> fundInvestments = new ArrayList<>();
        List<CoInvestmentDto> coInvestments = new ArrayList<>();

        InvestmentType investmentType = null;
        String investmentName = null;
        String strategy = null;
        String currency = null;

        int rowNum = 0;
        while (rowIterator.hasNext()) { // each row
            Row row = rowIterator.next();

            if (rowNum < 4) {/* ROW 0-3 */
                checkScheduleInvestmentsFileHeader(rowNum, row);
            } else if (rowNum == 4) { /* ROW == 4*/
                // check table header
                checkScheduleInvestmentsTableHeader(row);
            } else {
                // Final total if two investment types
                Cell lastCell = row.getCell(1);
                if(!ExcelUtils.isEmptyCell(lastCell) && lastCell.getCellType() == Cell.CELL_TYPE_STRING){
                    String value = lastCell.getStringCellValue();
                    if(StringUtils.isNotEmpty(value) && value.trim().equals("Total Private Equity Partnerships and Co-Investments")){
                        Cell cell2 = row.getCell(2);
                        Cell cell3 = row.getCell(3);
                        Cell cell4 = row.getCell(4);

                        Double capitalCommitments = ExcelUtils.getDoubleValueFromCell(cell2);
                        Double netCost = ExcelUtils.getDoubleValueFromCell(cell3);
                        Double fairValue = ExcelUtils.getDoubleValueFromCell(cell4);

                        // set currency
                        currency = getCellCurrency(cell2, null) != null ? getCellCurrency(cell2, null) : null;

                        CommonInvestmentDto commonInvestmentDto = new CommonInvestmentDto(investmentName, capitalCommitments,
                                netCost, fairValue, currency);

                        scheduleInvestmentsDto.setTotal(commonInvestmentDto);

                        rowNum++;
                        continue;
                    }
                }

                // Investment type
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    investmentType = cell.getStringCellValue().equals("Co-Investments") ? InvestmentType.CO_INVESTMENT :
                            InvestmentType.FUND_INVESTMENT;
                }
                if(investmentType == null){
                    investmentType = InvestmentType.FUND_INVESTMENT;
                }

                if(investmentType == InvestmentType.FUND_INVESTMENT){
                    /* Investment type FUND INVESTMENTS */
                    cell = row.getCell(1);
                    if(cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
                        Cell cell2 = row.getCell(2);
                        Cell cell3 = row.getCell(3);
                        Cell cell4 = row.getCell(4);

                        if(ExcelUtils.isEmptyCell(cell2) && ExcelUtils.isEmptyCell(cell3) && ExcelUtils.isEmptyCell(cell4)){
                            // empty cells
                            // Strategy
                            strategy = cell.getStringCellValue();
                            if(StringUtils.isNotEmpty(strategy)){
                                if(strategy.trim().endsWith("- USD") || strategy.contains("-USD")){
                                    currency = "USD";
                                }else if(strategy.trim().endsWith("- Euro") || strategy.contains("-Euro")){
                                    currency = "Euro";
                                }else if(strategy.trim().endsWith("- GBP") || strategy.contains("-GBP")){
                                    currency = "GBP";
                                }
                            }
                        }else if(!ExcelUtils.isEmptyCell(cell) && cell.getCellType() == Cell.CELL_TYPE_STRING){
                            investmentName = cell.getStringCellValue();

                            // check total
                            if(investmentName != null && investmentName.contains("Total")){
                                if(strategy != null && !investmentName.contains(strategy)){
                                    //strategy = null;
                                    strategy = investmentName.split("Total").length > 0 ? investmentName.split("Total")[1].trim() : null;
                                }
                            }

                            // currency
                            currency = getCellCurrency(cell2, currency) != null ? getCellCurrency(cell2, currency) : currency;

                            Double capitalCommitments = ExcelUtils.getDoubleValueFromCell(cell2); //cell2.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell2.getNumericCellValue() : null;
                            Double netCost = ExcelUtils.getDoubleValueFromCell(cell3); //cell3.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell3.getNumericCellValue() : null;
                            Double fairValue = ExcelUtils.getDoubleValueFromCell(cell4); //cell4.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell4.getNumericCellValue() : null;

                            FundInvestmentDto fundInvestmentDto = new FundInvestmentDto(investmentName, capitalCommitments, netCost, fairValue,
                                    currency, strategy);
                            fundInvestments.add(fundInvestmentDto);

                            if(StringUtils.isNotEmpty(investmentName) && investmentName.contains("Total")){
                                if(strategy != null && investmentName.contains(strategy)){
                                    strategy = null;
                                    currency = null;
                                }
                            }
                            // clear investment name
                            investmentName = null;
                        }
                    }
                }else if(investmentType == InvestmentType.CO_INVESTMENT){
                     /* Investment type CO-INVESTMENTS */
                    cell = row.getCell(1);
                    if(cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
                        Cell cell2 = row.getCell(2);
                        Cell cell3 = row.getCell(3);
                        Cell cell4 = row.getCell(4);
                        if(ExcelUtils.isEmptyCell(cell2) && ExcelUtils.isEmptyCell(cell3) && ExcelUtils.isEmptyCell(cell4)){
                            // empty cell
                            // investment name
                            investmentName = cell.getStringCellValue();
                        }else {

                            //if(cell2.getCellType() == Cell.CELL_TYPE_NUMERIC)

                            // description
                            String description = cell.getStringCellValue();

                            // currency
                            currency = getCellCurrency(cell2, currency) != null ? getCellCurrency(cell2, currency) : currency;

                            Double capitalCommitments = ExcelUtils.getDoubleValueFromCell(cell2); //cell2.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell2.getNumericCellValue() : null;
                            Double netCost = ExcelUtils.getDoubleValueFromCell(cell3); //cell3.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell3.getNumericCellValue() : null;
                            Double fairValue = ExcelUtils.getDoubleValueFromCell(cell4); //cell4.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell4.getNumericCellValue() : null;

                            CoInvestmentDto coInvestmentDto = new CoInvestmentDto(investmentName, description,
                                    capitalCommitments, netCost, fairValue, currency);
                            coInvestments.add(coInvestmentDto);

                            // clear description
                            //description = null;

                            if(StringUtils.isNotEmpty(description) && description.contains("Total")){
                                if(investmentName != null && description.contains(investmentName)){
                                    investmentName = null;
                                    currency = null;
                                }
                            }

                        }
                    }
                }else{
                    // should not be here
                    //logger.error("");
                    throw new IllegalStateException("Investment type should not be null");
                }
            }

            rowNum++;
        }

        scheduleInvestmentsDto.setFundInvestments(fundInvestments);
        scheduleInvestmentsDto.setCoInvestments(coInvestments);

        // TODO: return result
        return new FileUploadResultDto();
    }



    /* Statement of Assets, Liabilities, Partners Capital ****************************/

    private FileUploadResultDto parseStatementAssetsLiabilities(FilesDto filesDto)
    {

        StatementAssetsLiabilitiesDto statementAssetsLiabilitiesDtoTrancheA = new StatementAssetsLiabilitiesDto();
        StatementAssetsLiabilitiesDto statementAssetsLiabilitiesDtoTrancheB = new StatementAssetsLiabilitiesDto();
        try {
            InputStream inputFile = new ByteArrayInputStream(filesDto.getBytes());
            XSSFWorkbook workbook = new XSSFWorkbook(inputFile);

            // Sheet 1 - Tranche A
            XSSFSheet sheet = workbook.getSheetAt(0);
            FileUploadResultDto fileUploadResultDto = parseStatementAssetsLiabilitiesSheet(sheet, statementAssetsLiabilitiesDtoTrancheA);
            if(fileUploadResultDto.getStatus() == StatusResultType.FAIL){
                return fileUploadResultDto;
            }

            // Sheet 2 - Tranche B
            XSSFSheet sheet2 = workbook.getSheetAt(1);
            fileUploadResultDto = parseStatementAssetsLiabilitiesSheet(sheet2, statementAssetsLiabilitiesDtoTrancheB);

            // TODO: remove temp
            statementAssetsLiabilitiesDtoTrancheA.print();
            statementAssetsLiabilitiesDtoTrancheB.print();

            inputFile.close();

            return fileUploadResultDto;

        }catch (ExcelFileParseException e) {
            //logger.error("");
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            //logger.error("");
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error when processing 'Statement of Assets, Liabilities and Partners Capital' file' file", "");
        }
    }

    private ConsolidatedStatementType getStatementType(Cell cell){
        if(cell.getStringCellValue().equalsIgnoreCase("Assets")){
            return ConsolidatedStatementType.ASSETS;
        }else if(cell.getStringCellValue().equalsIgnoreCase("Liabilities")){
            return ConsolidatedStatementType.LIABILITIES;
//        }else if(cell.getStringCellValue().equalsIgnoreCase("Partners' capital")){
//            return ConsolidatedStatementType.PARTNERS_CAPITAL;
        }else if(cell.getStringCellValue().equalsIgnoreCase("Income:")){
            return ConsolidatedStatementType.INCOME;
        }else if(cell.getStringCellValue().equalsIgnoreCase("Expenses:")){
            return ConsolidatedStatementType.EXPENSES;
//        }else if(cell.getStringCellValue().contains("Net investment")){
//            return ConsolidatedStatementType.NET_INVESTMENT;
//        }else if(cell.getStringCellValue().equalsIgnoreCase("Net increase in partners' capital") ||
//                cell.getStringCellValue().equalsIgnoreCase("Net decrease in partner's capital")){
//            return ConsolidatedStatementType.NET_PARTNERS_CAPITAL;
        }
        return null;
    }

    private FileUploadResultDto parseStatementAssetsLiabilitiesSheet(XSSFSheet sheet, StatementAssetsLiabilitiesDto statementDto){
        if(sheet == null){
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Excel sheet is null", "");
        }
        boolean isTrancheA = sheet.getSheetName().equals("Tranche A");
        boolean isTrancheB = sheet.getSheetName().equals("Tranche B");
        if(!isTrancheA && !isTrancheB){
            throw new ExcelFileParseException("Missing sheets or sheet name illegal");
        }

        Iterator<Row> rowIterator = sheet.iterator();
        List<ConsolidatedStatementRecordDto> balance = new ArrayList<>();
        List<ConsolidatedStatementRecordDto> operations = new ArrayList<>();
        int rowNum = 0;

        ConsolidatedStatementType statementType = null;
        boolean isBalance = false;
        boolean isOperations = false;
        while (rowIterator.hasNext()) { // each row
            Row row = rowIterator.next();
            if(rowNum == 0){
                // skip
                rowNum++;
                continue;
            }else if(rowNum == 1){
                if(isTrancheA){
                    Cell cell = row.getCell(2);
                    if (!ExcelUtils.isCellStringValueEqual(cell, "Tarragon Master Fund LP")) {
                        //logger.error("");
                        throw new ExcelFileParseException("Table header check failed (Tranche A): row=2, cell=3");
                    }
                }else if(isTrancheB){
                    if(!ExcelUtils.isCellStringValueEqual(row.getCell(2), "Tarragon")){
                        //logger.error("");
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=2, cell=3");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(8), "Consolidation")){
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=2, cell=9");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(10), "Tranche B")){
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=2, cell=11");
                    }
                }
            }else if(rowNum == 2){
                if(isTrancheA){
                    boolean tableHeaders = ExcelUtils.isCellStringValueEqual(row.getCell(4), "Tarragon GP") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(6), "NICK Master") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(12), "Consolidation") &&
                            ExcelUtils.isCellStringValueEqual(row.getCell(14), "Tranche A");
                    if(!tableHeaders){
                        throw new ExcelFileParseException("Table header check failed (Tranche A) : row=3");
                    }
                }else if(isTrancheB){
                    if(!ExcelUtils.isCellStringValueEqual(row.getCell(2), "Master Fund LP")){
                        //logger.error("");
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=3, cell=3");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(4), "Tarragon LP")){
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=3, cell=5");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(6), "Total")){
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=3, cell=7");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(8), "Adjustments")){
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=3, cell=9");
                    }else if(!ExcelUtils.isCellStringValueEqual(row.getCell(10), "Consolidated")){
                        throw new ExcelFileParseException("Table header check failed (Tranche B): row=3, cell=11");
                    }
                }
            }else if(rowNum == 3){
                if(isTrancheA){
                    boolean tableHeaders = row.getCell(2).getStringCellValue().equals("Total*") &&
                            row.getCell(4).getStringCellValue().equals("LLC's Share") &&
                            row.getCell(6).getStringCellValue().equals("Fund Ltd.'s Share") &&
                            row.getCell(8).getStringCellValue().equals("Tarragon LP") &&
                            row.getCell(10).getStringCellValue().equals("Total") &&
                            row.getCell(12).getStringCellValue().equals("Adjustments") &&
                            row.getCell(14).getStringCellValue().equals("Consolidated");
                    if(!tableHeaders){
                        throw new ExcelFileParseException("Table header check failed (Tranche A) - row=4");
                    }
                }else if(isTrancheB){
                    // Not part of a header, no specific actions
                }
            }else{ /* Rows 4,... */
                Cell cell = row.getCell(0);
                if(ExcelUtils.isNotEmptyCell(cell) && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
                    if(cell.getStringCellValue().trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partners' Capital") ||
                            cell.getStringCellValue().trim().equalsIgnoreCase("Consolidated Statement of Assets, Liabilities and Partner's Capital")){
                        isBalance = true;
                        isOperations = false;
                    }else if(cell.getStringCellValue().trim().equalsIgnoreCase("Consolidated Statement of Operations")){
                        isOperations = true;
                        isBalance = false;
                    }

                    if(ExcelUtils.isEmptyCell(row.getCell(2)) && ExcelUtils.isEmptyCell(row.getCell(4)) &&
                            ExcelUtils.isEmptyCell(row.getCell(6)) && ExcelUtils.isEmptyCell(row.getCell(8)) &&
                            ExcelUtils.isEmptyCell(row.getCell(10)) && ExcelUtils.isEmptyCell(row.getCell(12)) &&
                            ExcelUtils.isEmptyCell(row.getCell(14))){

                        // statement type
                        ConsolidatedStatementType newType = getStatementType(cell);
                        statementType = newType != null ? newType : statementType;

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
                        }else if(isTrancheB){
                            values[1] = null;
                            values[2] = null;
                            values[3] = ExcelUtils.getDoubleValueFromCell(row.getCell(4));
                            values[4] = ExcelUtils.getDoubleValueFromCell(row.getCell(6));
                            values[5] = ExcelUtils.getDoubleValueFromCell(row.getCell(8));
                            values[6] = ExcelUtils.getDoubleValueFromCell(row.getCell(10));
                        }

                        ConsolidatedStatementRecordDto recordDto = new ConsolidatedStatementRecordDto();
                        recordDto.setName(name);
                        recordDto.setType(statementType);
                        recordDto.setValues(values);
                        if(statementType == null){
                            if(isBalance){
                                recordDto.setType(ConsolidatedStatementType.BALANCE_OTHER);
                            }else if(isOperations){
                                recordDto.setType(ConsolidatedStatementType.OPERATIONS_OTHER);
                            }
                        }

                        if(recordDto.isBalance()){
                            balance.add(recordDto);
                        }else if(recordDto.isOperations()){
                            operations.add(recordDto);
                        }else{
                            // logger.error("");
                            throw new ExcelFileParseException("Unresolved statement type: row=" + (rowNum + 1));
                        }

                        if(name.equalsIgnoreCase("Total assets") || name.equalsIgnoreCase("Total liabilities") ||
                                name.equalsIgnoreCase("Total income") || name.equalsIgnoreCase("Total expenses")){
                            statementType = null;
                        }
                    }

                }

            }


            rowNum++;
        }

        statementDto.setBalance(balance);
        statementDto.setOperations(operations);

        // TODO: return result
        return new FileUploadResultDto();

    }


    /* Statement of Cash Flows ********************************************************/
    private FileUploadResultDto parseStatementCashFlows(FilesDto filesDto){
        return null;
    }



    private FileUploadResultDto parseStatementChanges(FilesDto filesDto){
        return null;
    }

    private FileUploadResultDto parseBSTrancheA(FilesDto filesDto){
        return null;
    }
    private FileUploadResultDto parseIMDRTrancheA(FilesDto filesDto){
        return null;
    }
    private FileUploadResultDto parsePARTrancheA(FilesDto filesDto){
        return null;
    }
    private FileUploadResultDto parseISTrancheA(FilesDto filesDto){
        return null;
    }

    private FileUploadResultDto parseBSTrancheB(FilesDto filesDto){
        return null;
    }
    private FileUploadResultDto parseIMDRTrancheB(FilesDto filesDto){
        return null;
    }
    private FileUploadResultDto parsePARTrancheB(FilesDto filesDto){
        return null;
    }
    private FileUploadResultDto parseISTrancheB(FilesDto filesDto){
        return null;
    }

}
