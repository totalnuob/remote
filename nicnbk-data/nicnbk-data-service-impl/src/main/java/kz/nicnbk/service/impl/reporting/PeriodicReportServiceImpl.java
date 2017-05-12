package kz.nicnbk.service.impl.reporting;

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
import kz.nicnbk.service.dto.reporting.InvestmentType;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;
import kz.nicnbk.service.dto.reporting.ScheduleInvestmentsDto;
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
            return parseStatementLiabilities(filesDto);
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

    private FileUploadResultDto parseScheduleInvestments(FilesDto filesDto){

        List<ScheduleInvestmentsDto> scheduleInvestmentsDtoList = new ArrayList<>();
        try
        {
            InputStream inputFile = new ByteArrayInputStream(filesDto.getBytes());
            XSSFWorkbook workbook = new XSSFWorkbook(inputFile);

            // Sheet 1 - Tranche A
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            InvestmentType investmentType = null;
            String investmentName = null;
            String strategy = null;
            String substrategy = null;

            int rowNum = 0;
            while (rowIterator.hasNext()) { // each row
                Row row = rowIterator.next();

                if(rowNum == 0){/* ROW = 0 */
                    // check file header
                    Cell cell = row.getCell(0);
                    if (!cell.getStringCellValue().equals("Tarragon Master Fund LP")) {
                        throw new IllegalArgumentException("File header mismatch: row=1, cell=1");
                    }
                }else if(rowNum == 1){/* ROW = 1 */
                    // check file header
                    Cell cell = row.getCell(0);
                    if (!cell.getStringCellValue().equals("Schedule of Investments - Tranche A")){
                        throw new IllegalArgumentException("File header mismatch: row=2, cell=1");
                    }
                }else if(rowNum == 2){/* ROW = 2 */
                    // TODO: report date check
                }else if(rowNum == 3){/* ROW = 3 */
                    // skip
                    rowNum++;
                    continue;
                }else if(rowNum == 4){ /* ROW == 4*/
                    // check table header
                    Cell cell = row.getCell(1);
                    if(cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
                        if(!cell.getStringCellValue().equals("Investment")){
                            throw new IllegalArgumentException("File header mismatch: row=5, cell=2");
                        }
                    }
                    cell = row.getCell(2);
                    if(cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
                        if(!cell.getStringCellValue().equals("Capital Commitments")){
                            throw new IllegalArgumentException("File header mismatch: row=5, cell=3");
                        }
                    }
                    cell = row.getCell(3);
                    if(cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
                        if(!cell.getStringCellValue().equals("Net Cost")){
                            throw new IllegalArgumentException("File header mismatch: row=5, cell=4");
                        }
                    }
                    cell = row.getCell(4);
                    if(cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
                        if(!cell.getStringCellValue().equals("Fair Value")){
                            throw new IllegalArgumentException("File header mismatch: row=5, cell=5");
                        }
                    }
                }else {

                    // Investment type
                    Cell cell = row.getCell(0);
                    if(cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
                        investmentType = cell.getStringCellValue().equals("Co-Investments") ? InvestmentType.CO_INVESTMENT :
                                InvestmentType.FUND_INVESTMENT;
                    }

                    if(investmentType == InvestmentType.FUND_INVESTMENT){
                        /* Investment type = FUND INVESTMENTS */
                        cell = row.getCell(1);
                        if(cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
                            Cell cell2 = row.getCell(2);
                            Cell cell3 = row.getCell(3);
                            Cell cell4 = row.getCell(4);
                            if(isEmptyCell(row.getCell(2)) && isEmptyCell(cell3) && isEmptyCell(cell4)){
                                // empty cell
                                // Strategy
                                strategy = cell.getStringCellValue();
                            }else {
                                // if(cell2.getCellType() == Cell.CELL_TYPE_NUMERIC)
                                investmentName = cell.getStringCellValue();
                                // TODO: currency
                                String currency = null;

                                Double capitalCommitments = getDoubleFromCell(cell2); //cell2.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell2.getNumericCellValue() : null;
                                Double netCost = getDoubleFromCell(cell3); //cell3.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell3.getNumericCellValue() : null;
                                Double fairValue = getDoubleFromCell(cell4); //cell4.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell4.getNumericCellValue() : null;
                                if(StringUtils.isNotEmpty(strategy) && strategy.contains("/")){
                                    substrategy = strategy.split("/")[1];
                                    strategy = strategy.split("/")[0];
                                }

                                // check if total
                                boolean totalSubstrategy = false;
                                boolean totalStrategy = false;
                                if(StringUtils.isNotEmpty(investmentName) && investmentName.contains("Total")){
                                    if(StringUtils.isNotEmpty(strategy) && investmentName.contains(strategy)){
                                        if(StringUtils.isNotEmpty(substrategy) && investmentName.contains(substrategy)){
                                            totalSubstrategy = true;
                                        }else{
                                            totalStrategy = true;
                                        }
                                        investmentName = null;
                                    }
                                }


                                ScheduleInvestmentsDto scheduleInvestmentsDto = new ScheduleInvestmentsDto(investmentName,
                                        currency, capitalCommitments, netCost, fairValue, null, strategy, substrategy,
                                        investmentType, totalSubstrategy, totalStrategy, null);

                                scheduleInvestmentsDtoList.add(scheduleInvestmentsDto);

                                if(totalSubstrategy){
                                    substrategy = null;
                                }
                                if(totalStrategy){
                                    strategy = null;
                                }

                            }
                        }
                    }else if(investmentType == InvestmentType.CO_INVESTMENT){
                         /* Investment type = CO-INVESTMENTS */
                        cell = row.getCell(1);
                        if(cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
                            Cell cell2 = row.getCell(2);
                            Cell cell3 = row.getCell(3);
                            Cell cell4 = row.getCell(4);
                            if(isEmptyCell(row.getCell(2)) && isEmptyCell(cell3) && isEmptyCell(cell4)){
                                // empty cell
                                // investment name
                                investmentName = cell.getStringCellValue();
                            }else if(cell2.getCellType() == Cell.CELL_TYPE_NUMERIC){
                                // description
                                String description = cell.getStringCellValue();

                                // TODO: currency
                                String currency = null;

                                Double capitalCommitments = getDoubleFromCell(cell2); //cell2.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell2.getNumericCellValue() : null;
                                Double netCost = getDoubleFromCell(cell3); //cell3.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell3.getNumericCellValue() : null;
                                Double fairValue = getDoubleFromCell(cell4); //cell4.getCellType() == Cell.CELL_TYPE_NUMERIC ? cell4.getNumericCellValue() : null;

                                // check if total
                                boolean totalInvestment = false;
                                if(StringUtils.isNotEmpty(description) && description.equals("Total " + investmentName)){
                                    description = null;
                                    totalInvestment = true;
                                }else{

                                }

                                ScheduleInvestmentsDto scheduleInvestmentsDto = new ScheduleInvestmentsDto(investmentName,
                                        currency, capitalCommitments, netCost, fairValue, description, null, null,
                                        investmentType, null, null, totalInvestment);

                                scheduleInvestmentsDtoList.add(scheduleInvestmentsDto);
                            }
                        }
                    }
                }


//                Iterator<Cell> cellIterator = row.cellIterator();
//                int cellNum = 0;
//                while (cellIterator.hasNext()) { // each column
//                    Cell cell = cellIterator.next();
//
//                    /* CHECK HEADER */
//                    boolean headerChecked = checkScheduleInvestmentsHeader(rowNum, cellNum, cell);
//                    if(!headerChecked){
//                        FileUploadResultDto fileUploadResultDto = new FileUploadResultDto();
//                        fileUploadResultDto.setStatus(StatusResultType.FAIL);
//                        fileUploadResultDto.setMessageEn("File contents check - header check failed");
//
//                        return fileUploadResultDto;
//                    }
//
//                    /* CHECK TABLE HEADER */
//                    boolean tableHeaderChecked = checkScheduleInvestmentsTableHeader(rowNum, cellNum, cell);
//                    if(!tableHeaderChecked){
//                        FileUploadResultDto fileUploadResultDto = new FileUploadResultDto();
//                        fileUploadResultDto.setStatus(StatusResultType.FAIL);
//                        fileUploadResultDto.setMessageEn("File contents check - table header check failed");
//
//                        return fileUploadResultDto;
//                    }
//                    if(rowNum <= 4){
//                        continue;
//                    }
//
//                    // investment type
//                    if(cellNum == 0){
//                        if(cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
//                            investmentType = cell.getStringCellValue().equals("Fund Investments") ? InvestmentType.FUND_INVESTMENT :
//                                    cell.getStringCellValue().equals("Co-Investments") ? InvestmentType.CO_INVESTMENT : null;
//                        }
//                    }
//
//                    if(cellNum == 1){
//                        // strategy
//                        if(cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isNotEmpty(cell.getStringCellValue())){
//
//                        }
//                    }
//
//
//                    //Check the cell type and format accordingly
//                    switch (cell.getCellType()) {
//                        case Cell.CELL_TYPE_NUMERIC:
//                            System.out.print(cell.getNumericCellValue() + "t");
//                            break;
//                        case Cell.CELL_TYPE_STRING:
//                            System.out.print(cell.getStringCellValue() + "t");
//                            break;
//                        default:
//                            // TODO: not-numeric and non-string
//                            break;
//                    }
//                }
//                System.out.println("");

                rowNum++;
            }
            inputFile.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        for(ScheduleInvestmentsDto dto: scheduleInvestmentsDtoList){
            dto.print();
        }
        return new FileUploadResultDto();
    }

    // TODO: refactor to EXCEL util class

    private boolean isEmptyCell(Cell cell){
        if(cell == null){
            return true;
        }else if(cell.getCellType() == Cell.CELL_TYPE_BLANK ||
                (cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isEmpty(cell.getStringCellValue()))){
            return true;
        }
        return false;
    }

    private Double getDoubleFromCell(Cell cell){
        if(cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            return cell.getNumericCellValue();
        }else if(cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING){
            try{
                Double value = Double.parseDouble(cell.getStringCellValue());
                return value;
            }catch (Exception ex){
                System.out.println("Double parse failed: " + cell.getStringCellValue());
            }
        }
        return null;
    }

    private boolean checkScheduleInvestmentsHeader(int rowNum, int cellNum, Cell cell){
        try {
            if (rowNum == 0 && cellNum == 0) {
                if (!cell.getStringCellValue().equals("Tarragon Master Fund LP")) {
                    throw new IllegalArgumentException();
                }
            }
            if (rowNum == 1 && cellNum == 0) {
                if (!cell.getStringCellValue().equals("Schedule of Investments - Tranche A")) {
                    throw new IllegalArgumentException();
                }
            }
            if (rowNum == 2 && cellNum == 0) {
                Date headerDate = cell.getDateCellValue();
                // TODO: check date
                System.out.println(headerDate);
            }
        }catch(Exception ex){
            return false;
        }
        return true;
    }

    private boolean checkScheduleInvestmentsTableHeader(int rowNum, int cellNum, Cell cell){
        try {
            if (rowNum == 4) {
                if (cellNum == 1) {
                    if(!cell.getStringCellValue().equals("Investment")){
                        throw new IllegalArgumentException();
                    }
                }else if (cellNum == 2) {
                    if(!cell.getStringCellValue().equals("Capital Commitments")){
                        throw new IllegalArgumentException();
                    }
                }else if (cellNum == 3) {
                    if(!cell.getStringCellValue().equals("Net Cost")){
                        throw new IllegalArgumentException();
                    }
                }else if (cellNum == 4) {
                    if(!cell.getStringCellValue().equals("Fair Value")){
                        throw new IllegalArgumentException();
                    }
                }
            }
        }catch (Exception ex){
            return false;
        }
        return true;
    }

    private FileUploadResultDto parseStatementLiabilities(FilesDto filesDto){
        return null;
    }

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
