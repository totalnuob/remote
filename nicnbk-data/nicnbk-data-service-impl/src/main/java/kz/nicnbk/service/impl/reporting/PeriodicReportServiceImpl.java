package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.util.*;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.reporting.*;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.CurrencyLookup;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.lookup.reporting.InvestmentTypeLookup;
import kz.nicnbk.repo.model.lookup.reporting.PETrancheTypeLookup;
import kz.nicnbk.repo.model.lookup.reporting.TerraNICChartAccountsLookup;
import kz.nicnbk.repo.model.reporting.*;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.reporting.PeriodicDataService;
import kz.nicnbk.service.api.reporting.PeriodicReportNICKMFService;
import kz.nicnbk.service.api.reporting.PeriodicReportPrevYearInputService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFITDService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFNOALService;
import kz.nicnbk.service.api.reporting.hedgefunds.PeriodicReportHFService;
import kz.nicnbk.service.api.reporting.privateequity.*;
import kz.nicnbk.service.api.reporting.realestate.PeriodicReportREService;
import kz.nicnbk.service.converter.reporting.*;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.PeriodicReportType;
import kz.nicnbk.service.dto.reporting.privateequity.TarragonGeneratedGeneralLedgerFormDto;
import kz.nicnbk.service.dto.reporting.privateequity.TarragonStatementBalanceOperationsHolderDto;
import kz.nicnbk.service.dto.reporting.realestate.*;
import kz.nicnbk.service.impl.reporting.lookup.NICChartAccountsLookup;
import kz.nicnbk.service.impl.reporting.lookup.PeriodicDataTypeLookup;
import kz.nicnbk.service.impl.reporting.lookup.ReserveCalculationsExpenseTypeLookup;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Period;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Service Impl class for Periodic Reports.
 *
 * Created by magzumov on 20.04.2017.
 */
@Service
public class PeriodicReportServiceImpl implements PeriodicReportService {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicReportServiceImpl.class);

    /* Root folder on the server */
    @Value("${filestorage.root.directory}")
    private String rootDirectory;

    public static final int PERIODIC_DATA_DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private PeriodReportRepository periodReportRepository;

    @Autowired
    private PeriodicReportConverter periodicReportConverter;

    @Autowired
    private ConsolidatedKZTForm7Converter consolidatedKZTForm7Converter;

    @Autowired
    private ConsolidatedKZTForm8Converter consolidatedKZTForm8Converter;

    @Autowired
    private ConsolidatedKZTForm10Converter consolidatedKZTForm10Converter;

    @Autowired
    private ConsolidatedKZTForm13Converter consolidatedKZTForm13Converter;

    @Autowired
    private ConsolidatedKZTForm14Converter consolidatedKZTForm14Converter;

    @Autowired
    private ConsolidatedKZTForm19Converter consolidatedKZTForm19Converter;

    @Autowired
    private ConsolidatedKZTForm22Converter consolidatedKZTForm22Converter;

    @Autowired
    private ConsolidatedKZTForm2Converter consolidatedKZTForm2Converter;

    @Autowired
    private ConsolidatedKZTForm1Converter consolidatedKZTForm1Converter;

    @Autowired
    private ConsolidatedKZTForm6Converter consolidatedKZTForm6Converter;

    @Autowired
    private ConsolidatedKZTForm3Converter consolidatedKZTForm3Converter;

    @Autowired
    private ConsolidatedUSDFormBalanceConverter consolidatedUSDFormBalanceConverter;

    @Autowired
    private ConsolidatedUSDFormIncomeExpenseConverter consolidatedUSDFormIncomeExpenseConverter;

    @Autowired
    private ConsolidatedUSDFormTotalIncomeConverter consolidatedUSDFormTotalIncomeConverter;

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
    private PEStatementOperationsService statementOperationsService;

    @Autowired
    private PEStatementCashflowsService statementCashflowsService;

    @Autowired
    private PEStatementChangesService statementChangesService;

    @Autowired
    private PeriodicDataService periodicDataService;

    @Autowired
    private HFGeneralLedgerBalanceService generalLedgerBalanceService;

    @Autowired
    private HFNOALService hfNOALService;

    @Autowired
    private HFITDService hfITDService;

    @Autowired
    private PeriodicReportREService realEstateService;

    @Autowired
    private PeriodicReportNICKMFService periodicReportNICKMFService;

    @Autowired
    private CurrencyRatesService currencyRatesService;

    @Autowired
    private ConsolidatedReportKZTForm7Repository consolidatedReportKZTForm7Repository;

    @Autowired
    private ConsolidatedReportKZTForm8Repository consolidatedReportKZTForm8Repository;

    @Autowired
    private ConsolidatedReportKZTForm10Repository consolidatedReportKZTForm10Repository;

    @Autowired
    private ConsolidatedReportKZTForm13Repository consolidatedReportKZTForm13Repository;

    @Autowired
    private ConsolidatedReportKZTForm14Repository consolidatedReportKZTForm14Repository;

    @Autowired
    private ConsolidatedReportKZTForm19Repository consolidatedReportKZTForm19Repository;

    @Autowired
    private ConsolidatedReportKZTForm22Repository consolidatedReportKZTForm22Repository;

    @Autowired
    private ConsolidatedReportKZTForm6Repository consolidatedReportKZTForm6Repository;

    @Autowired
    private ConsolidatedReportKZTForm3Repository consolidatedReportKZTForm3Repository;

    @Autowired
    private ConsolidatedReportUSDFormBalanceRepository consolidatedReportUSDFormBalanceRepository;

    @Autowired
    private ConsolidatedReportUSDFormIncomeExpenseRepository consolidatedReportUSDFormIncomeExpenseRepository;

    @Autowired
    private ConsolidatedReportUSDFormTotalIncomeRepository consolidatedReportUSDFormTotalIncomeRepository;

    @Autowired
    private ConsolidatedReportKZTForm2Repository consolidatedReportKZTForm2Repository;

    @Autowired
    private ConsolidatedReportKZTForm1Repository consolidatedReportKZTForm1Repository;

    @Autowired
    private ReserveCalculationService reserveCalculationService;

    @Autowired
    private PeriodicReportPrevYearInputService prevYearInputService;

    @Autowired
    private PeriodicReportPEService periodicReportPEService;

    @Autowired
    private PeriodicReportHFService periodicReportHFService;

    @Autowired
    private PeriodicReportFundRenameInfoRepository fundRenameInfoRepository;


    /* PERIODIC REPORT ****************************************************************************************************************/

    /**
     * Assemble entity from DTO data, save to database and return EntitySaveResponseDto.
     * Set entity updater to specified username.
     *
     * @param dto - dto
     * @param updater - username of updater
     * @return - EntitySaveResponseDto
     */
    @Override
    public EntitySaveResponseDto savePeriodicReport(PeriodicReportDto dto, String updater) {
        try {
            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
            PeriodicReport entity = this.periodicReportConverter.assemble(dto);
            if (dto.getId() == null) { // CREATE
                // check report date
                if(this.findReportByReportDate(entity.getReportDate()) != null){
                    String errorMessage = "Failed to create report, report date already exists: report date = " + DateUtils.getDateFormatted(entity.getReportDate()) + " [user " + updater + "]";
                    logger.error(errorMessage);
                    saveResponseDto.setErrorMessageEn(errorMessage);
                    return saveResponseDto;
                }

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

            PeriodicReport reportEntity = periodReportRepository.save(entity);
            logger.info(dto.getId() == null ? "Periodic report created: " + reportEntity.getId() + ", by " + entity.getCreator().getUsername() :
                    "Periodic report updated: " + reportEntity.getId() + ", by " + updater);
            saveResponseDto.setSuccessMessageEn("Periodic report successfully saved");
            saveResponseDto.setEntityId(reportEntity.getId());
            saveResponseDto.setCreationDate(reportEntity.getCreationDate());
            return saveResponseDto;
        } catch (Exception ex){
            logger.error("Error saving periodic report: " + (dto != null && dto.getId() != null ? ("report id ") + dto.getId() : "new report")  + " [user " + updater + "]" ,ex);
            return null;
        }
    }

    /**
     * Delete file association from database.
     *
     * @param fileId - file id
     * @return = true/false
     */
    @Override
    public boolean deletePeriodicReportFileAssociationById(Long fileId) {
        try {
            this.periodicReportFilesRepository.deleteByFileId(fileId);
            return true;
        }catch (Exception ex){
            logger.error("Error deleting periodic report file record for file id = " + fileId );
        }
        return false;
    }

    /**
     * Return periodic report dto identified by id.
     *
     * @param id - report id
     * @return - report dto
     */
    @Override
    public PeriodicReportDto getPeriodicReport(Long id) {
        try {
            PeriodicReport entity = this.periodReportRepository.findOne(id);
            PeriodicReportDto dto = this.periodicReportConverter.disassemble(entity);
            return dto;
        } catch(Exception ex){
            logger.error("Error loading periodic report: " + id, ex);
        }
        return null;
    }

    /**
     * Returns list of all periodic report dtos.
     *
     * @return - list of dto
     */
    @Override
    public List<PeriodicReportDto> getAllPeriodicReports() {

        try {
            List<PeriodicReportDto> dtoList = new ArrayList<>();
            Iterator<PeriodicReport> iterator = this.periodReportRepository.findAll(new Sort(Sort.Direction.DESC, "reportDate", "id")).iterator();
            while (iterator.hasNext()) {
                PeriodicReport entity = iterator.next();
                PeriodicReportDto dto = this.periodicReportConverter.disassemble(entity);
                dtoList.add(dto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Error loading all periodic reports", ex);
        }
        return null;
    }

    /**
     * Return a list of periodic report files for specified report (by id).
     *
     * @param reportId - id of the report
     * @return - list of DTOs
     */
    @Override
    public PeriodicReportInputFilesHolder getPeriodicReportInputFiles(Long reportId) {
        try {
            PeriodicReportInputFilesHolder holder = new PeriodicReportInputFilesHolder();
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
                if(entities.size() > 0) {
                    holder.setFiles(files);
                }
            }
            holder.setReport(getPeriodicReport(reportId));
            return holder;
        }catch(Exception ex){
            logger.error("Error getting periodic report files: report=" + reportId, ex);
        }
        return null;
    }

    /**
     * Return periodic report file for specified report (by id) and file type.
     *
     * @param reportId - id of the report
     * @param type - file type
     * @return - file DTO
     */
    @Override
    public FilesDto getPeriodicReportFileByIdAndType(Long reportId, String type){
        try {
            PeriodicReportFiles entity = this.periodicReportFilesRepository.getEntityByReportIdAndFileType(reportId, type);
            if (entity != null) {
                FilesDto fileDto = new FilesDto();
                fileDto.setType(entity.getFile().getType() != null ? entity.getFile().getType().getCode() : null);
                fileDto.setId(entity.getFile().getId());
                fileDto.setFileName(entity.getFile().getFileName());
                return fileDto;
            }
        }catch(Exception ex){
            logger.error("Error getting periodic report files: report=" + reportId + ", type=" + type, ex);
        }
        return null;
    }

    @Override
    public PeriodicReportDto findReportByReportDate(Date date){
        PeriodicReport report = this.periodReportRepository.findByReportDate(date);
        if(report != null){
            return this.periodicReportConverter.disassemble(report);
        }
        return null;
    }

    @Override
    public PeriodicReportDto findMostRecentReport() {
        List<PeriodicReportDto> allReports = getAllPeriodicReports();
        if(allReports != null && !allReports.isEmpty()){
            PeriodicReportDto mostRecentReport = null;
            for(PeriodicReportDto report: allReports){
                if(mostRecentReport == null || mostRecentReport.getReportDate().before(report.getReportDate())){
                    mostRecentReport = report;
                }
            }
            return mostRecentReport;
        }
        return null;
    }

    @Override
    public boolean safeDeleteFile(Long fileId, String username) {
        try{
            PeriodicReportFiles periodicReportFiles = this.periodicReportFilesRepository.getEntityByFileId(fileId);

            if (periodicReportFiles != null) {
                PeriodicReport periodicReport = periodicReportFiles.getPeriodicReport();
                // check status
                if(periodicReport == null || (periodicReport.getStatus() != null &&
                        periodicReport.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode()))){
                    //log error
                    logger.error("Could not delete file for report with status 'SUBMITTED': report id " + periodicReport.getId() + ", file id " + fileId +
                            (StringUtils.isNotEmpty(username) ? " [user " + username + "]" : ""));
                    return false;
                }

                boolean deleted = fileService.safeDelete(fileId);
                if(!deleted){
                    logger.error("Failed to delete(safe) reporting input file: reportId=" + periodicReport.getId() + ", file id=" + fileId);
                }else{
                    // delete table data
                    deleted = deleteParsedFileData(periodicReport.getId(), periodicReportFiles.getFile().getType().getCode());
                    if(deleted) {
                        logger.info("Deleted(safe) reporting input file: reportId=" + periodicReport.getId() + ", file id=" + fileId);
                        return true;
                    }else{
                        // recover from error
                        boolean reverted = fileService.revertSafeDeleteFile(fileId);
                        if(!reverted){
                            logger.error("Could not revert safe deletion of file: file id " +fileId + ". Parsed data was not deleted from DB, need to safe delete the file");
                        }
                        logger.error("Error deleting file: file id=" + fileId + ", report id=" + periodicReport.getId());
                    }
                }
                return false;
            }else{
                logger.error("Failed to delete(safe) reporting input file - no file found : file id=" + fileId);
            }
        }catch (Exception ex){
            logger.error("Failed to delete(safe) reporting input file wtih exceptin: file id=" + fileId, ex);
        }
        return false;
    }

    /**
     * Deletes parsed data by report id and file type.
     *
     * @param reportId - report id
     * @param fileType - file type
     * @return - true/false
     */
    private boolean deleteParsedFileData(Long reportId, String fileType){
        if (fileType.equalsIgnoreCase(FileTypeLookup.NB_REP_TARR_SCHED_INVEST.getCode())){
            return this.scheduleInvestmentService.deleteByReportId(reportId);
        }else if (fileType.equalsIgnoreCase(FileTypeLookup.NB_REP_TARR_SOI_REPORT.getCode())){
            return this.scheduleInvestmentService.deleteByReportId(reportId);
        }else if(fileType.equalsIgnoreCase(FileTypeLookup.NB_REP_TARR_STMT_BALANCE_OPERATIONS.getCode())){
            boolean deleted = this.statementBalanceService.deleteByReportId(reportId);
            if(deleted) {
                return this.statementOperationsService.deleteByReportId(reportId);
            }else {
                // TODO: recover from failure
                return false;
            }
        }else if(fileType.equalsIgnoreCase(FileTypeLookup.NB_REP_TARR_STMT_CASHFLOW.getCode())){
            return this.statementCashflowsService.deleteByReportId(reportId);
        }else if(fileType.equalsIgnoreCase(FileTypeLookup.NB_REP_TARR_STMT_CHANGES.getCode())){
            return this.statementChangesService.deleteByReportId(reportId);
        }else if(fileType.equalsIgnoreCase(FileTypeLookup.NB_REP_SINGULAR_GENERAL_LEDGER.getCode())){
            return this.generalLedgerBalanceService.deleteByReportId(reportId);
        }else if (fileType.equalsIgnoreCase(FileTypeLookup.NB_REP_SN_TRANCHE_A.getCode())) {
            return this.hfNOALService.deleteByReportId(reportId, 1);
        }else if(fileType.equalsIgnoreCase(FileTypeLookup.NB_REP_SN_TRANCHE_B.getCode())) {
            return this.hfNOALService.deleteByReportId(reportId, 2);
        }else if(fileType.equalsIgnoreCase(FileTypeLookup.NB_REP_SN_ITD.getCode())) {
            return this.hfITDService.deleteByReportId(reportId);
        }else if(fileType.equalsIgnoreCase(FileTypeLookup.NB_REP_TERRA_COMBINED.getCode())) {
            return this.realEstateService.deleteByReportId(reportId);
        }else if(fileType.equalsIgnoreCase(FileTypeLookup.NB_REP_TERRA_GENERAL_LEDGER.getCode())) {
            return this.realEstateService.deleteGeneralLedgerByReportId(reportId);
        }

        return false;
    }

    /**
     * Saves input file without parsing. Parsing is done elsewhere.
     * 1) saves to file system
     * 2) writes record to DB
     *
     * @param reportId - report id
     * @param filesDto - file
     * @return - saved file info
     */
    @Override
    public FilesDto saveInputFile(Long reportId, FilesDto filesDto) {
        try {
            if (filesDto != null) {
                if (filesDto.getId() == null) {
                    if(StringUtils.isEmpty(filesDto.getType())){
                        logger.error("Error uploading file: file type not specified or invalid: '" + filesDto.getType());
                        return null;
                    }
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

    /**
     * Saves all the generated reports to DB and marks report as 'SUBMITTED'.
     *
     * @param reportId - report id
     * @return - true/false
     */

    @Transactional
    @Override
    public boolean markReportAsFinal(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);

        if(report.getStatus() != null && report.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            return true;
        }
        // TODO: which report can be made final

        // GENERATE ALL REPORTS ***********************************************************************************

        // USD 1
        ListResponseDto balanceResponseDto = generateConsolidatedBalanceUSDForm(reportId);
        if(balanceResponseDto.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating USD Form 1 report. " + balanceResponseDto.getMessage().getNameEn());
        }

        // USD 2
        List<ConsolidatedBalanceFormRecordDto> incomeExpenseRecords = generateConsolidatedIncomeExpenseUSDForm(reportId);

        // USD 3
        List<ConsolidatedBalanceFormRecordDto> totalIncomeRecords = generateConsolidatedTotalIncomeUSDForm(reportId);

        // KZT 1
        ListResponseDto responseDtoKZTForm1 = generateConsolidatedBalanceKZTForm1(reportId);
        if(responseDtoKZTForm1.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating KZT Form 1 report. " + responseDtoKZTForm1.getMessage().getNameEn());
        }

        // KZT 2
        ListResponseDto KZTForm2ResponseDto = generateConsolidatedIncomeExpenseKZTForm2(reportId);
        if(KZTForm2ResponseDto.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating KZT Form 2 report. " + KZTForm2ResponseDto.getMessage().getNameEn());
        }

        // KZT 3
        ListResponseDto KZTForm3ResponseDto = generateConsolidatedTotalIncomeKZTForm3(reportId);
        if(KZTForm3ResponseDto.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating KZT Form 3 report. " + KZTForm3ResponseDto.getMessage().getNameEn());
        }

        // KZT 6
        ListResponseDto KZTForm6ResponseDto = generateConsolidatedBalanceKZTForm6(reportId);
        if(KZTForm6ResponseDto.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating KZT Form 6 report. " + KZTForm6ResponseDto.getMessage().getNameEn());
        }

        // KZT 7
        ListResponseDto KZTForm7ResponseDto = generateConsolidatedBalanceKZTForm7(reportId);
        if(KZTForm7ResponseDto.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating KZT Form 7 report. " + KZTForm7ResponseDto.getMessage().getNameEn());
        }

        // KZT 8
        ListResponseDto KZTForm8ResponseDto = generateConsolidatedBalanceKZTForm8(reportId);
        if(KZTForm8ResponseDto.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating KZT Form 8 report. " + KZTForm8ResponseDto.getMessage().getNameEn());
        }

        // KZT 10
        ListResponseDto KZTForm10ResponseDto = generateConsolidatedBalanceKZTForm10(reportId);
        if(KZTForm10ResponseDto.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating KZT Form 10 report. " + KZTForm10ResponseDto.getMessage().getNameEn());
        }

        // KZT 13
        ListResponseDto KZTForm13ResponseDto = generateConsolidatedBalanceKZTForm13(reportId);
        if(KZTForm13ResponseDto.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating KZT Form 13 report. " + KZTForm13ResponseDto.getMessage().getNameEn());
        }

        // KZT 14
        ListResponseDto KZTForm14ResponseDto = generateConsolidatedBalanceKZTForm14(reportId);
        if(KZTForm14ResponseDto.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating KZT Form 14 report. " + KZTForm14ResponseDto.getMessage().getNameEn());
        }

        // KZT 19
        ListResponseDto KZTFOrm19ResponseDto = generateConsolidatedBalanceKZTForm19(reportId);
        if(KZTFOrm19ResponseDto.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating KZT Form 19 report. " + KZTFOrm19ResponseDto.getMessage().getNameEn());
        }

        // KZT 22
        ListResponseDto KZTFOrm22ResponseDto = generateConsolidatedBalanceKZTForm22(reportId);
        if(KZTFOrm22ResponseDto.getStatus() == ResponseStatusType.FAIL){
            throw new IllegalStateException("Error generating KZT Form 22 report. " + KZTFOrm22ResponseDto.getMessage().getNameEn());
        }


        // SAVE REPORTS ********************************************************************************************
        try {
            List<ConsolidatedBalanceFormRecordDto> balanceRecords = balanceResponseDto.getRecords();
            boolean saved = saveConsolidatedUSDFormBalance(balanceRecords, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving USD FORM 1 records");
                throw new IllegalStateException("Failed to mark report FINAL: error saving USD FORM 1 records");
                //return false;
            }

            saved = saveConsolidatedUSDFormIncomeExpense(incomeExpenseRecords, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving USD FORM 2 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving USD FORM 2 records");
            }

            saved = saveConsolidatedUSDFormTotalIncome(totalIncomeRecords, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving USD FORM 3 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving USD FORM 3 records");
            }

            // save kzt form 1

            List<ConsolidatedBalanceFormRecordDto> form1Records = responseDtoKZTForm1.getRecords();
            saved = saveConsolidatedKZTForm1(form1Records, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving KZT FORM 1 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving KZT FORM 1 records");
            }

            // save kzt form 2

            List<ConsolidatedBalanceFormRecordDto> form2Records = KZTForm2ResponseDto.getRecords();
            saved = saveConsolidatedKZTForm2(form2Records, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving KZT FORM 2 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving KZT FORM 2 records");
            }

            // save kzt form 3

            List<ConsolidatedBalanceFormRecordDto> form3Records = KZTForm3ResponseDto.getRecords();
            saved = saveConsolidatedKZTForm3(form3Records, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving KZT FORM 3 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving KZT FORM 3 records");
            }

            // save from 6

            List<ConsolidatedKZTForm6RecordDto> form6Records = KZTForm6ResponseDto.getRecords();
            saved = saveConsolidatedKZTForm6(form6Records, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving KZT FORM 6 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving KZT FORM 6 records");
            }

            // save from 7
            List<ConsolidatedKZTForm7RecordDto> form7Records = KZTForm7ResponseDto.getRecords();
            saved = saveConsolidatedKZTForm7(form7Records, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving KZT FORM 7 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving KZT FORM 7 records");
            }

            // save from 8
            List<ConsolidatedKZTForm8RecordDto> form8Records = KZTForm8ResponseDto.getRecords();
            saved = saveConsolidatedKZTForm8(form8Records, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving KZT FORM 8 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving KZT FORM 8 records");
            }

            List<ConsolidatedKZTForm10RecordDto> form10Records = KZTForm10ResponseDto.getRecords();
            saved = saveConsolidatedKZTForm10(form10Records, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving KZT FORM 10 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving KZT FORM 10 records");
            }


            List<ConsolidatedKZTForm13RecordDto> form13Records = KZTForm13ResponseDto.getRecords();
            saved = saveConsolidatedKZTForm13(form13Records, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving KZT FORM 13 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving KZT FORM 13 records");
            }


            List<ConsolidatedKZTForm14RecordDto> form14Records = KZTForm14ResponseDto.getRecords();
            saved = saveConsolidatedKZTForm14(form14Records, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving KZT FORM 14 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving KZT FORM 14 records");
            }


            List<ConsolidatedKZTForm19RecordDto> form19Records = KZTFOrm19ResponseDto.getRecords();
            saved = saveConsolidatedKZTForm19(form19Records, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving KZT FORM 19 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving KZT FORM 19 records");

            }

            List<ConsolidatedKZTForm22RecordDto> form22Records = KZTFOrm22ResponseDto.getRecords();
            saved = saveConsolidatedKZTForm22(form22Records, reportId);
            if (!saved) {
                logger.error("Failed to mark report FINAL: error saving KZT FORM 22 records");
                //return false;
                throw new IllegalStateException("Failed to mark report FINAL: error saving KZT FORM 22 records");
            }
        }catch (IllegalStateException ex){
            //clearSavedReportsTables(reportId);
            throw ex;
            //return false;
        }

        ReportStatus status = new ReportStatus();
        status.setId(3);
        report.setStatus(status);
        this.periodReportRepository.save(report);
        return true;
    }

    private void clearSavedReportsTables(Long reportId){
        deleteConsolidatedUSDFormBalance(reportId);
        deleteConsolidatedUSDFormIncomeExpense(reportId);
        deleteConsolidatedUSDFormTotalIncome(reportId);

        deleteConsolidatedKZTForm1(reportId);
        deleteConsolidatedKZTForm2(reportId);
        deleteConsolidatedKZTForm3(reportId);
        deleteConsolidatedKZTForm6(reportId);
        deleteConsolidatedKZTForm7(reportId);
        deleteConsolidatedKZTForm8(reportId);
        deleteConsolidatedKZTForm10(reportId);
        deleteConsolidatedKZTForm13(reportId);
        deleteConsolidatedKZTForm14(reportId);
        deleteConsolidatedKZTForm19(reportId);
        deleteConsolidatedKZTForm22(reportId);
    }

    @Override
    public ReportingFundRenameInfoDto getFundRenameInfo(Long reportId){
        ReportingFundRenameInfoDto fundRenameInfoDto = new ReportingFundRenameInfoDto();
        List<PeriodicReportFundRenameInfo> entities = this.fundRenameInfoRepository.getEntitiesReportId(reportId);
        if(entities != null && !entities.isEmpty()){
            for(PeriodicReportFundRenameInfo entity: entities){
                fundRenameInfoDto.addFundRenamePair(entity.getCurrentFundName(), entity.getPreviousFundName(), entity.getType(), entity.isUsePreviousFundName());
            }
        }

        return fundRenameInfoDto;
    }

    private Map<String, ReportingFundRenamePairDto> getFundRenameInfoMap(Long reportId){
        ReportingFundRenameInfoDto fundRenameInfo = getFundRenameInfo(reportId);
        Map<String, ReportingFundRenamePairDto> fundRenames = new HashedMap();
        if(fundRenameInfo != null && !fundRenameInfo.isEmpty()){
            for(ReportingFundRenamePairDto pair: fundRenameInfo.getFundRenames()){
                fundRenames.put(pair.getPreviousFundName(), pair);
            }
        }
        return fundRenames;
    }

    @Override
    public boolean saveFundRenameInfo(ReportingFundRenameInfoDto info){
        try {
            if (info != null && info.getFundRenames() != null && info.getReport().getId() != null) {
                // delete all
                this.fundRenameInfoRepository.deleteByReportId(info.getReport().getId());
                // save all
                List<PeriodicReportFundRenameInfo> entities = new ArrayList<>();
                for(ReportingFundRenamePairDto pair: info.getFundRenames()){
                    entities.add(new PeriodicReportFundRenameInfo(info.getReport().getId(), pair.getCurrentFundName(), pair.getPreviousFundName(), pair.getType(), pair.isUsePreviousFundName()));
                }
                this.fundRenameInfoRepository.save(entities);
                return true;
            }
        }catch (Exception ex){
         logger.error("Error saving Reporting Fund Rename Info: report id  " + (info.getReport().getId() != null ? info.getReport().getId() : null), ex);
        }
        return false;
    }

//    @Override
//    public ReportingFundNameListHolderDto getFundNameList(Long reportId){
//        ReportingFundNameListHolderDto fundNamesHoder = new ReportingFundNameListHolderDto();
//        Set<String> currentPEFundNames = new HashSet<String>();
//        Set<String> currentHFFundNames = new HashSet<String>();
//        Set<String> currentREFundNames = new HashSet<String>();
//        Set<String> previousPEFundNames = new HashSet<String>();
//        Set<String> previousHFFundNames = new HashSet<String>();
//        Set<String> previousREFundNames = new HashSet<String>();
//
//        // CURRENT
//        // PRIVATE EQUITY
//        List<ScheduleInvestmentsDto> scheduleInvestmentsDtos = this.scheduleInvestmentService.getScheduleInvestments(reportId);
//        if(scheduleInvestmentsDtos != null && !scheduleInvestmentsDtos.isEmpty()){
//            for(ScheduleInvestmentsDto investmentsDto: scheduleInvestmentsDtos){
//                if(investmentsDto.getType() != null && investmentsDto.getType().getCode() != null &&
//                        /*investmentsDto.getType().getCode().equalsIgnoreCase(InvestmentTypeLookup.FUND_INVESTMENT.getCode()) &&*/
//                        (investmentsDto.getTotalSum() == null || !investmentsDto.getTotalSum().booleanValue())) {
//                    currentPEFundNames.add(investmentsDto.getName());
//                }
//            }
//            fundNamesHoder.setCurrentPEFundNameList(currentPEFundNames.toArray(new String[currentPEFundNames.size()]));
//        }
//
//        // HEDGE FUNDS
//        // General ledger
////        ListResponseDto responseDto = this.periodicReportHFService.getSingularGeneratedForm(reportId);
////        if(responseDto.getRecords() != null){
////            for(GeneratedGeneralLedgerFormDto glRecord: (List<GeneratedGeneralLedgerFormDto>)responseDto.getRecords()){
////                if(glRecord.getShortName() != null){
////                    currentHFFundNames.add(glRecord.getShortName());
////                }
////            }
////        }
//
//        ConsolidatedReportRecordHolderDto generalLedgerHolder = this.generalLedgerBalanceService.getWithExcludedRecords(reportId);
//        if(generalLedgerHolder != null && generalLedgerHolder.getGeneralLedgerBalanceList() != null && !generalLedgerHolder.getGeneralLedgerBalanceList().isEmpty()){
//            for(SingularityGeneralLedgerBalanceRecordDto glRecord: generalLedgerHolder.getGeneralLedgerBalanceList()){
//                if(glRecord.getShortName() != null){
//                    currentHFFundNames.add(glRecord.getShortName());
//                }
//            }
//        }
//        // Noal
//        ConsolidatedReportRecordHolderDto noalHolderA = this.hfNOALService.get(reportId, 1);
//        if(noalHolderA != null && noalHolderA.getNoalTrancheAList() != null){
//            for(SingularityNOALRecordDto noalRecord: noalHolderA.getNoalTrancheAList()){
//                if(StringUtils.isNotEmpty(noalRecord.getName())){
//                    currentHFFundNames.add(noalRecord.getName());
//                }
//            }
//
//        }
//        ConsolidatedReportRecordHolderDto noalHolderB = this.hfNOALService.get(reportId, 2);
//        if(noalHolderB != null && noalHolderB.getNoalTrancheBList() != null){
//            for(SingularityNOALRecordDto noalRecord: noalHolderB.getNoalTrancheBList()){
//                if(StringUtils.isNotEmpty(noalRecord.getName())){
//                    currentHFFundNames.add(noalRecord.getName());
//                }
//            }
//        }
//
//        fundNamesHoder.setCurrentHFFundNameList((String[]) currentHFFundNames.toArray(new String[currentHFFundNames.size()]));
//
//        // Real Estate
////        TerraCombinedDataHolderDto terraData = this.realEstateService.getTerraCombinedParsedData(reportId);
////        if(terraData != null && terraData.getSecuritiesCostRecords() != null && !terraData.getSecuritiesCostRecords().isEmpty()){
////            for(TerraSecuritiesCostRecordDto securitiesDto: terraData.getSecuritiesCostRecords()){
////                if((securitiesDto.getTotalSum() == null || !securitiesDto.getTotalSum().booleanValue()) && StringUtils.isNotEmpty(securitiesDto.getName())){
////                    currentREFundNames.add(securitiesDto.getName());
////                }
////            }
////        }
//        ListResponseDto terraGLResponse = this.realEstateService.getTerraGeneralLedgerFormData(reportId);
//        if(terraGLResponse != null) {
//            List<TerraGeneratedGeneralLedgerFormDto> records = terraGLResponse.getRecords();
//            if(records != null) {
//                for (TerraGeneratedGeneralLedgerFormDto terraRecord: records) {
//                    if(terraRecord.getShortName() != null){
//                        currentREFundNames.add(terraRecord.getShortName());
//                    }
//                }
//            }
//        }
//
//        fundNamesHoder.setCurrentREFundNameList((String[]) currentREFundNames.toArray(new String[currentREFundNames.size()]));
//
//        // PREVIOUS
//        PeriodicReportDto currentReport = getPeriodicReport(reportId);
//        Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
//        PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
//
//        // PRIVATE EQUITY
//
//        scheduleInvestmentsDtos = this.scheduleInvestmentService.getScheduleInvestments(previousReport.getId());
//        if(scheduleInvestmentsDtos != null && !scheduleInvestmentsDtos.isEmpty()){
//            for(ScheduleInvestmentsDto investmentsDto: scheduleInvestmentsDtos){
//                if(investmentsDto.getType() != null && investmentsDto.getType().getCode() != null &&
//                        /*investmentsDto.getType().getCode().equalsIgnoreCase(InvestmentTypeLookup.FUND_INVESTMENT.getCode()) &&*/
//                        (investmentsDto.getTotalSum() == null || !investmentsDto.getTotalSum().booleanValue())) {
//                    previousPEFundNames.add(investmentsDto.getName());
//                }
//            }
//            fundNamesHoder.setPreviousPEFundNameList(previousPEFundNames.toArray(new String[previousPEFundNames.size()]));
//        }
//
//        // HEDGE FUNDS
//        // General ledger
//        generalLedgerHolder = this.generalLedgerBalanceService.getWithExcludedRecords(previousReport.getId());
//        if(generalLedgerHolder != null && generalLedgerHolder.getGeneralLedgerBalanceList() != null && !generalLedgerHolder.getGeneralLedgerBalanceList().isEmpty()){
//            for(SingularityGeneralLedgerBalanceRecordDto glRecord: generalLedgerHolder.getGeneralLedgerBalanceList()){
//                if(glRecord.getShortName() != null){
//                    previousHFFundNames.add(glRecord.getShortName());
//                }
//            }
//        }
//        // Noal
//        noalHolderA = this.hfNOALService.get(previousReport.getId(), 1);
//        if(noalHolderA != null && noalHolderA.getNoalTrancheAList() != null){
//            for(SingularityNOALRecordDto noalRecord: noalHolderA.getNoalTrancheAList()){
//                if(StringUtils.isNotEmpty(noalRecord.getName())){
//                    previousHFFundNames.add(noalRecord.getName());
//                }
//            }
//
//        }
//        noalHolderB = this.hfNOALService.get(previousReport.getId(), 2);
//        if(noalHolderB != null && noalHolderB.getNoalTrancheBList() != null){
//            for(SingularityNOALRecordDto noalRecord: noalHolderB.getNoalTrancheBList()){
//                if(StringUtils.isNotEmpty(noalRecord.getName())){
//                    previousHFFundNames.add(noalRecord.getName());
//                }
//            }
//        }
//
//        fundNamesHoder.setPreviousHFFundNameList((String[]) previousHFFundNames.toArray(new String[previousHFFundNames.size()]));
//
//        // Real Estate
////        terraData = this.realEstateService.getTerraCombinedParsedData(previousReport.getId());
////        if(terraData != null && terraData.getSecuritiesCostRecords() != null && !terraData.getSecuritiesCostRecords().isEmpty()){
////            for(TerraSecuritiesCostRecordDto securitiesDto: terraData.getSecuritiesCostRecords()){
////                if((securitiesDto.getTotalSum() == null || !securitiesDto.getTotalSum().booleanValue()) && StringUtils.isNotEmpty(securitiesDto.getName())){
////                    previousREFundNames.add(securitiesDto.getName());
////                }
////            }
////        }
//        ListResponseDto terraPreviousGLResponse = this.realEstateService.getTerraGeneralLedgerFormData(previousReport.getId());
//        if(terraPreviousGLResponse != null) {
//            List<TerraGeneratedGeneralLedgerFormDto> records = terraPreviousGLResponse.getRecords();
//            if(records != null) {
//                for (TerraGeneratedGeneralLedgerFormDto terraRecord: records) {
//                    if(terraRecord.getShortName() != null){
//                        previousREFundNames.add(terraRecord.getShortName());
//                    }
//                }
//            }
//        }
//
//        fundNamesHoder.setPreviousREFundNameList((String[]) previousREFundNames.toArray(new String[previousREFundNames.size()]));
//
//        if(fundNamesHoder != null && fundNamesHoder.getCurrentPEFundNameList() != null) {
//            Arrays.sort(fundNamesHoder.getCurrentPEFundNameList());
//        }
//        if(fundNamesHoder != null && fundNamesHoder.getCurrentHFFundNameList() != null) {
//            Arrays.sort(fundNamesHoder.getCurrentHFFundNameList());
//        }
//        if(fundNamesHoder != null && fundNamesHoder.getCurrentREFundNameList() != null) {
//            Arrays.sort(fundNamesHoder.getCurrentREFundNameList());
//        }
//        if(fundNamesHoder != null && fundNamesHoder.getPreviousPEFundNameList() != null) {
//            Arrays.sort(fundNamesHoder.getPreviousPEFundNameList());
//        }
//        if(fundNamesHoder != null && fundNamesHoder.getPreviousHFFundNameList() != null) {
//            Arrays.sort(fundNamesHoder.getPreviousHFFundNameList());
//        }
//        if(fundNamesHoder != null && fundNamesHoder.getPreviousREFundNameList() != null) {
//            Arrays.sort(fundNamesHoder.getPreviousREFundNameList());
//        }
//        return fundNamesHoder;
//    }

    @Override
    public ReportingFundNameListHolderDto getFundNameList(Long reportId){
        ReportingFundNameListHolderDto fundNamesHolder = new ReportingFundNameListHolderDto();
        Set<String> currentPEFundNames = new HashSet<String>();
        Set<String> currentHFFundNames = new HashSet<String>();
        Set<String> currentREFundNames = new HashSet<String>();
        Set<String> previousPEFundNames = new HashSet<String>();
        Set<String> previousHFFundNames = new HashSet<String>();
        Set<String> previousREFundNames = new HashSet<String>();

        // CURRENT
        // PRIVATE EQUITY
        List<ScheduleInvestmentsDto> scheduleInvestmentsDtos = this.scheduleInvestmentService.getScheduleInvestments(reportId);
        if(scheduleInvestmentsDtos != null && !scheduleInvestmentsDtos.isEmpty()){
            for(ScheduleInvestmentsDto investmentsDto: scheduleInvestmentsDtos){
                if(investmentsDto.getType() != null && investmentsDto.getType().getCode() != null &&
                        /*investmentsDto.getType().getCode().equalsIgnoreCase(InvestmentTypeLookup.FUND_INVESTMENT.getCode()) &&*/
                        (investmentsDto.getTotalSum() == null || !investmentsDto.getTotalSum().booleanValue())) {
                    currentPEFundNames.add(investmentsDto.getName());
                }
            }
            fundNamesHolder.setCurrentPEFundNameList(currentPEFundNames.toArray(new String[currentPEFundNames.size()]));
        }

        // HEDGE FUNDS
        ConsolidatedReportRecordHolderDto generalLedgerHolder = this.generalLedgerBalanceService.getWithExcludedRecords(reportId);
        if(generalLedgerHolder != null && generalLedgerHolder.getGeneralLedgerBalanceList() != null && !generalLedgerHolder.getGeneralLedgerBalanceList().isEmpty()){
            for(SingularityGeneralLedgerBalanceRecordDto glRecord: generalLedgerHolder.getGeneralLedgerBalanceList()){
                if(glRecord.getShortName() != null){
                    currentHFFundNames.add(glRecord.getShortName());
                }
            }
        }
        // Noal
        ConsolidatedReportRecordHolderDto noalHolderA = this.hfNOALService.get(reportId, 1);
        if(noalHolderA != null && noalHolderA.getNoalTrancheAList() != null){
            for(SingularityNOALRecordDto noalRecord: noalHolderA.getNoalTrancheAList()){
                if(StringUtils.isNotEmpty(noalRecord.getName())){
                    currentHFFundNames.add(noalRecord.getName());
                }
            }

        }
        ConsolidatedReportRecordHolderDto noalHolderB = this.hfNOALService.get(reportId, 2);
        if(noalHolderB != null && noalHolderB.getNoalTrancheBList() != null){
            for(SingularityNOALRecordDto noalRecord: noalHolderB.getNoalTrancheBList()){
                if(StringUtils.isNotEmpty(noalRecord.getName())){
                    currentHFFundNames.add(noalRecord.getName());
                }
            }
        }

        fundNamesHolder.setCurrentHFFundNameList((String[]) currentHFFundNames.toArray(new String[currentHFFundNames.size()]));

        // REAL ESTATE
        ListResponseDto terraGLResponse = this.realEstateService.getTerraGeneralLedgerFormData(reportId);
        if(terraGLResponse != null) {
            List<TerraGeneratedGeneralLedgerFormDto> records = terraGLResponse.getRecords();
            if(records != null) {
                for (TerraGeneratedGeneralLedgerFormDto terraRecord: records) {
                    if(terraRecord.getShortName() != null){
                        currentREFundNames.add(terraRecord.getShortName());
                    }
                }
            }
        }

        fundNamesHolder.setCurrentREFundNameList((String[]) currentREFundNames.toArray(new String[currentREFundNames.size()]));

        // PREVIOUS
        PeriodicReportDto currentReport = getPeriodicReport(reportId);
        Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
        PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
        ListResponseDto prevResponseDto = generateConsolidatedBalanceKZTForm7(previousReport.getId());
        List<ConsolidatedKZTForm7RecordDto> prevRecords = prevResponseDto.getRecords();
        if(prevRecords != null){
            for(ConsolidatedKZTForm7RecordDto record: prevRecords){
                if(record.getLineNumber() != null && record.getLineNumber() == 3 && record.getAccountNumber() != null &&
                        record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010)){
                    if(record.getName().startsWith(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)){
                        previousHFFundNames.add(record.getEntityName());
                    }else if(record.getName().startsWith(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)){
                        previousPEFundNames.add(record.getEntityName());
                    }else if(record.getName().startsWith(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT)){
                        previousREFundNames.add(record.getEntityName());
                    }
                }
            }
        }
        fundNamesHolder.setPreviousPEFundNameList(previousPEFundNames.toArray(new String[previousPEFundNames.size()]));
        fundNamesHolder.setPreviousHFFundNameList((String[]) previousHFFundNames.toArray(new String[previousHFFundNames.size()]));
        fundNamesHolder.setPreviousREFundNameList((String[]) previousREFundNames.toArray(new String[previousREFundNames.size()]));

        // SORTING
        if(fundNamesHolder.getCurrentPEFundNameList() != null) {
            Arrays.sort(fundNamesHolder.getCurrentPEFundNameList());
        }
        if(fundNamesHolder.getCurrentHFFundNameList() != null) {
            Arrays.sort(fundNamesHolder.getCurrentHFFundNameList());
        }
        if(fundNamesHolder.getCurrentREFundNameList() != null) {
            Arrays.sort(fundNamesHolder.getCurrentREFundNameList());
        }
        if(fundNamesHolder.getPreviousPEFundNameList() != null) {
            Arrays.sort(fundNamesHolder.getPreviousPEFundNameList());
        }
        if(fundNamesHolder.getPreviousHFFundNameList() != null) {
            Arrays.sort(fundNamesHolder.getPreviousHFFundNameList());
        }
        if(fundNamesHolder.getPreviousREFundNameList() != null) {
            Arrays.sort(fundNamesHolder.getPreviousREFundNameList());
        }
        return fundNamesHolder;
    }

//    @Override
//    public EntitySaveResponseDto saveInterestRate(Long reportId, String interestRate, String updater){
//        EntitySaveResponseDto entitySaveResponseDto = new EntitySaveResponseDto();
//        PeriodicReportDto reportDto = getPeriodicReport(reportId);
//        if(reportDto != null) {
//            entitySaveResponseDto = savePeriodicReport(reportDto, updater);
//            return entitySaveResponseDto;
//        }
//        entitySaveResponseDto.setErrorMessageEn("Error updating interest reate for report with id '" + reportId + "'");
//        return entitySaveResponseDto;
//    }

    /* GENERATE REPORTS ***********************************************************************************************/

    //USD Reports
    @Override
    public ListResponseDto generateConsolidatedBalanceUSDForm(Long reportId){
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(currentReport.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedBalanceFormRecordDto> records =  getConsolidatedBalanceUSDFormSaved(reportId);
            responseDto.setRecords(records);
            return responseDto;
        }else{
            ListResponseDto responseDtoCurrent = generateConsolidatedBalanceUSDFormCurrent(reportId);
            if(responseDtoCurrent.getStatus() == ResponseStatusType.FAIL){
                return responseDtoCurrent;
            }
            List<ConsolidatedBalanceFormRecordDto> currentPeriodRecords = responseDtoCurrent.getRecords();

            int header1Index = 0; // Инвестиции к возврату
            int header2Index = 0; // Предварительная подписка
            int header3_1Index = 0; // Инвестиции в фонд недвижимости
            int header3_2Index = 0; // Инвестиции в фонд частного капитала
            int header3_3Index = 0; // Инвестиции в хедж-фонд
            for(int i = 0; i < currentPeriodRecords.size(); i++) {
                ConsolidatedBalanceFormRecordDto currentRecord = currentPeriodRecords.get(i);
                if (currentRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_INVESTMENTS_TO_RETURN) && currentRecord.getAccountNumber() == null) {
                    header1Index = i + 1;
                } else if (currentRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PRE_SUBSCRIPTION) && currentRecord.getAccountNumber() == null) {
                    header2Index = i + 1;
                } else if (header3_1Index == 0 && currentRecord.getName().startsWith(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT)) {
                    header3_1Index = i;
                } else if (header3_2Index == 0 && currentRecord.getName().startsWith(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)) {
                    header3_2Index = i;
                } else if (header3_3Index == 0 && currentRecord.getName().startsWith(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)) {
                    header3_3Index = i;
                }
            }

            // Set previous month account balance
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if(previousReport != null && previousReport.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
                List<ConsolidatedBalanceFormRecordDto> previousPeriodRecords = getConsolidatedBalanceUSDFormSaved(previousReport.getId());
                if(previousPeriodRecords != null){
                    ReportingFundRenameInfoDto fundRenameInfo = getFundRenameInfo(reportId);
                    if(previousPeriodRecords != null && !fundRenameInfo.isEmpty()){
                        for(ConsolidatedBalanceFormRecordDto dto: previousPeriodRecords){
                            if(dto.getLineNumber() == 16 && dto.getAccountNumber() != null && dto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010)){
                                for(ReportingFundRenamePairDto pairDto: fundRenameInfo.getFundRenames()){
                                    if(dto.getName().contains(pairDto.getPreviousFundName()) && !pairDto.isUsePreviousFundName()){
                                        // TODO: Check type
                                        String newName = dto.getName().replace(pairDto.getPreviousFundName(), pairDto.getCurrentFundName());
                                        dto.setName(newName);
                                    }else if(dto.getName().contains(pairDto.getCurrentFundName()) && pairDto.isUsePreviousFundName()){
                                        // TODO: Check type
                                        String newName = dto.getName().replace(pairDto.getCurrentFundName(), pairDto.getPreviousFundName());
                                        dto.setName(newName);
                                    }
                                }
                            }
                        }
                    }

                    List<ConsolidatedBalanceFormRecordDto> toAdd = new ArrayList<>();
                    List<Integer> toAddIndex = new ArrayList<>();
                    for(ConsolidatedBalanceFormRecordDto previousRecord: previousPeriodRecords){
                        for(int i = 0; i < currentPeriodRecords.size(); i++){
                            ConsolidatedBalanceFormRecordDto currentRecord = currentPeriodRecords.get(i);

                            if(currentRecord.getName().equalsIgnoreCase(previousRecord.getName()) && currentRecord.getLineNumber() != null &&
                                    previousRecord.getLineNumber() != null && currentRecord.getLineNumber() == previousRecord.getLineNumber()){
                                currentRecord.setPreviousAccountBalance(previousRecord.getCurrentAccountBalance());
                                break;
                            }

                            // next line number, means record was not found, possibly  need to add
                            if(previousRecord.getLineNumber() + 1 == (currentRecord.getLineNumber())){
                                if(previousRecord.getName().startsWith(PeriodicReportConstants.RU_INVESTMENTS_TO_RETURN) && previousRecord.getAccountNumber() != null){
                                    toAddIndex.add(header1Index);
                                }else if(previousRecord.getName().startsWith(PeriodicReportConstants.RU_PRE_SUBSCRIPTION) && previousRecord.getAccountNumber() != null){
                                    toAddIndex.add(header2Index);
                                }else if(previousRecord.getName().startsWith(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT) && previousRecord.getAccountNumber() != null){
                                    toAddIndex.add(header3_1Index);
                                }else if(previousRecord.getName().startsWith(PeriodicReportConstants.RU_PE_FUND_INVESTMENT) && previousRecord.getAccountNumber() != null){
                                    toAddIndex.add(header3_2Index);
                                }else if(previousRecord.getName().startsWith(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT) && previousRecord.getAccountNumber() != null) {
                                    toAddIndex.add(header3_3Index);
                                }else {
                                    toAddIndex.add(i);
                                }
                                toAdd.add(previousRecord);
                                break;
                            }

                        }
                    }
                    int added = 0;
                    for(int i = 0; i < toAdd.size(); i++) {
                        ConsolidatedBalanceFormRecordDto recordToAdd = toAdd.get(i);
                        if(recordToAdd.getCurrentAccountBalance() != null && recordToAdd.getCurrentAccountBalance() != 0) {
                            recordToAdd.setPreviousAccountBalance(recordToAdd.getCurrentAccountBalance());
                            recordToAdd.setCurrentAccountBalance(null);
                            currentPeriodRecords.add(toAddIndex.get(i) + added, recordToAdd);
                            added++;
                        }
                    }

                }
            }else{
                logger.error("ConsolidatedBalanceUSDForm: No previous month report or report status is not 'SUBMITTED', report id " + reportId);
            }

            // Rename other entity names for NB PORTAL
//            for(ConsolidatedBalanceFormRecordDto record: currentPeriodRecords){
//                if(record.getOtherEntityName() != null){
//                    if(record.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE)){
//                        record.setOtherEntityName(record.getOtherEntityName().replace(PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE, PeriodicReportConstants.SINGULARITY_LOWER_CASE));
//                    }else if(record.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)){
//                        record.setOtherEntityName(record.getOtherEntityName().replace(PeriodicReportConstants.SINGULAR_CAPITAL_CASE, PeriodicReportConstants.SINGULARITY_LOWER_CASE));
//                    }else if(record.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_B_CAPITAL_CASE)){
//                        record.setOtherEntityName(record.getOtherEntityName().replace(PeriodicReportConstants.TARRAGON_B_CAPITAL_CASE, PeriodicReportConstants.TARRAGON_LOWER_CASE));
//                    }else if(record.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)){
//                        record.setOtherEntityName(record.getOtherEntityName().replace(PeriodicReportConstants.TARRAGON_CAPITAL_CASE, PeriodicReportConstants.TARRAGON_LOWER_CASE));
//                    }else if(record.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)){
//                        record.setOtherEntityName(record.getOtherEntityName().replace(PeriodicReportConstants.TERRA_CAPITAL_CASE, PeriodicReportConstants.TERRA_LOWER_CASE));
//                    }
//                }
//            }

            responseDto.setStatus(ResponseStatusType.SUCCESS);
            responseDto.setRecords(currentPeriodRecords);
            return responseDto;
        }
    }

    private ListResponseDto generateConsolidatedBalanceUSDFormCurrent(Long reportId) {
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        Map<Integer, Double> sums = new HashedMap();
        for(int i = 1; i <= 54; i++){
            sums.put(i, 0.0);
        }
        // Add line number headers
        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedBalanceUSDFormLineHeaders();

        // Add NICK MF records
        NICKMFReportingDataHolderDto nickmfReportingDataHolderDto = this.periodicReportNICKMFService.getNICKMFReportingData(reportId);
        if(nickmfReportingDataHolderDto != null && nickmfReportingDataHolderDto.getRecords() != null){
            for(NICKMFReportingDataDto nickMFRecord: nickmfReportingDataHolderDto.getRecords()){
                if(nickMFRecord.getNbChartOfAccountsCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010) &&
                        !nickMFRecord.getNicChartOfAccountsName().equalsIgnoreCase(PeriodicReportConstants.RU_5440_010_a)){
                    // include only 'Резерв на переоценку  финансовых инвестиций, имеющихся в наличии для продажи'
                    continue;
                }else if(nickMFRecord.getNbChartOfAccountsCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5520_010)){
                    // exclude '5520.010' records
                    continue;
                }
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setAccountNumber(nickMFRecord.getNbChartOfAccountsCode());
                recordDto.setName(nickMFRecord.getNicChartOfAccountsName());
                int lineNumber = getConsolidatedBalanceUSDFormLineNumberByAccountNumber(nickMFRecord.getNbChartOfAccountsCode());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    continue;
                }
                //recordDto.setOtherEntityName();
                recordDto.setCurrentAccountBalance(lineNumber == 32 || lineNumber == 47 || lineNumber == 51 ||
                        (recordDto.getAccountNumber() != null && (recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3013_010) ||
                                recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3383_010)))
                        ? MathUtils.subtract(0.0, nickMFRecord.getAccountBalance()) : nickMFRecord.getAccountBalance());

                //recordDto.setPreviousAccountBalance();
                records.add(recordDto);

                if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                    Double sum = sums.get(recordDto.getLineNumber());
                    sum = sum != null ? sum : 0.0;
                    sum = MathUtils.add(sum, recordDto.getCurrentAccountBalance());
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add Singular records
        ListResponseDto singularGeneratedFormResponse = this.periodicReportHFService.getSingularGeneratedForm(reportId);
        if(singularGeneratedFormResponse.getStatus() != null &&
                singularGeneratedFormResponse.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
            singularGeneratedFormResponse.setRecords(null);
            return singularGeneratedFormResponse;
        }
        List<GeneratedGeneralLedgerFormDto> singularRecords = singularGeneratedFormResponse.getRecords();
        if(singularRecords != null){
            for(GeneratedGeneralLedgerFormDto singularRecord: singularRecords){
                if(singularRecord.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010) &&
                        !singularRecord.getNicAccountName().equalsIgnoreCase(PeriodicReportConstants.RU_5440_010_a)){
                    // include only 'Резерв на переоценку  финансовых инвестиций, имеющихся в наличии для продажи'
                    continue;
                }else if(singularRecord.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5520_010)){
                    // exclude '5520.010' records
                    continue;
                }
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setAccountNumber(singularRecord.getNbAccountNumber());
                recordDto.setName(singularRecord.getNicAccountName());
                int lineNumber = getConsolidatedBalanceUSDFormLineNumberByAccountNumber(singularRecord.getNbAccountNumber());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    continue;
                }
                if(lineNumber == 4 || lineNumber == 9){
                    recordDto.setOtherEntityName(singularRecord.getAcronym());
                }
                recordDto.setOtherEntityName(singularRecord.getAcronym());
                if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060) &&
                        recordDto.getName().equalsIgnoreCase(PeriodicReportConstants.RU_3053_060)){
                    recordDto.setOtherEntityName(PeriodicReportConstants.SINGULARITY_LOWER_CASE + " - " + singularRecord.getSubscriptionRedemptionEntity());
                }
                recordDto.setCurrentAccountBalance(lineNumber == 32 || lineNumber == 47  || lineNumber == 51 ||
                        (recordDto.getAccountNumber() != null && (recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3013_010) ||
                                recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3383_010) ||
                                recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060)))
                        ? MathUtils.subtract(0.0, singularRecord.getGLAccountBalance()) : singularRecord.getGLAccountBalance());
                //recordDto.setPreviousAccountBalance();
                records.add(recordDto);
                if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                    Double sum = sums.get(recordDto.getLineNumber());
                    sum = sum != null ? sum : 0.0;
                    sum = MathUtils.add(sum, recordDto.getCurrentAccountBalance());
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add Tarragon records
        List<GeneratedGeneralLedgerFormDto> tarragonRecords = this.periodicReportPEService.getTarragonGeneratedFormWithoutExcluded(reportId).getRecords();
        if(tarragonRecords != null){
            for(GeneratedGeneralLedgerFormDto tarragonRecord: tarragonRecords){
                if(tarragonRecord.getNbAccountNumber() != null && tarragonRecord.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010) &&
                        !tarragonRecord.getNicAccountName().equalsIgnoreCase(PeriodicReportConstants.RU_5440_010_a)){
                    // include only 'Резерв на переоценку  финансовых инвестиций, имеющихся в наличии для продажи'
                    continue;
                }else if(tarragonRecord.getNbAccountNumber() != null && tarragonRecord.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5520_010)){
                    // exclude '5520.010' records
                    continue;
                }
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setAccountNumber(tarragonRecord.getNbAccountNumber());
                recordDto.setName(tarragonRecord.getNicAccountName());
                int lineNumber = getConsolidatedBalanceUSDFormLineNumberByAccountNumber(tarragonRecord.getNbAccountNumber());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    continue;
                }
                if(lineNumber == 4 || lineNumber == 9){
                    recordDto.setOtherEntityName(tarragonRecord.getAcronym());
                }
                recordDto.setOtherEntityName(tarragonRecord.getAcronym());
                if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060) &&
                        recordDto.getName().equalsIgnoreCase(PeriodicReportConstants.RU_3053_060)){
                    recordDto.setOtherEntityName(PeriodicReportConstants.TARRAGON_LOWER_CASE + " - " + tarragonRecord.getChartAccountsLongDescription());
                }
                recordDto.setCurrentAccountBalance(lineNumber == 32 || lineNumber == 47  || lineNumber == 51 || lineNumber == 41 ||
                        (recordDto.getAccountNumber() != null && (recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3013_010) ||
                                recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3383_010)) ||
                                (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060)))
                        ? MathUtils.subtract(0.0, tarragonRecord.getGLAccountBalance()) : tarragonRecord.getGLAccountBalance());
                //recordDto.setPreviousAccountBalance();
                records.add(recordDto);

                if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                    Double sum = sums.get(recordDto.getLineNumber());
                    sum = sum != null ? sum : 0.0;
                    sum = MathUtils.add(sum, recordDto.getCurrentAccountBalance());
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add Terra records
        //ListResponseDto terraResponseDto = this.realEstateService.getTerraGeneratedFormWithoutExcluded(reportId);
        ListResponseDto terraResponseDto = this.realEstateService.getTerraGeneralLedgerFormDataWithoutExcluded(reportId);

        if(terraResponseDto.getStatus() != null &&
                terraResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
            terraResponseDto.setRecords(null);
            return terraResponseDto;
        }
        List<TerraGeneratedGeneralLedgerFormDto> terraRecords = terraResponseDto.getRecords();
        if(terraRecords != null){
            for(TerraGeneratedGeneralLedgerFormDto terraRecord: terraRecords){
                if(terraRecord.getNbAccountNumber() != null && terraRecord.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010) &&
                        !terraRecord.getNicAccountName().equalsIgnoreCase(PeriodicReportConstants.RU_5440_010_a)){
                    // include only 'Резерв на переоценку  финансовых инвестиций, имеющихся в наличии для продажи'
                    continue;
                }else if(terraRecord.getNbAccountNumber() != null && terraRecord.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5520_010)){
                    // exclude '5520.010' records
                    continue;
                }
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setAccountNumber(terraRecord.getNbAccountNumber());
                recordDto.setName(terraRecord.getNicAccountName());
                int lineNumber = getConsolidatedBalanceUSDFormLineNumberByAccountNumber(terraRecord.getNbAccountNumber());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    continue;
                }
                if(lineNumber == 4 || lineNumber == 9){
                    recordDto.setOtherEntityName(terraRecord.getAcronym());
                }
                recordDto.setOtherEntityName(terraRecord.getAcronym());
                if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060) &&
                        recordDto.getName().equalsIgnoreCase(PeriodicReportConstants.RU_3053_060)){
                    recordDto.setOtherEntityName(PeriodicReportConstants.TERRA_LOWER_CASE + " - " + terraRecord.getShortName());
                }
                recordDto.setCurrentAccountBalance(lineNumber == 32 || lineNumber == 47  || lineNumber == 51 || lineNumber == 41 ||
                        (recordDto.getAccountNumber() != null && (recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3013_010) ||
                                recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3383_010)))
                        ? MathUtils.subtract(0.0, terraRecord.getGLAccountBalance()) : terraRecord.getGLAccountBalance());
                //recordDto.setPreviousAccountBalance();
                records.add(recordDto);

                if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                    Double sum = sums.get(recordDto.getLineNumber());
                    sum = sum != null ? sum : 0.0;
                    sum = MathUtils.add(sum, recordDto.getCurrentAccountBalance());
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add previous year input
        List<PreviousYearInputDataDto> previousYearRecords = this.prevYearInputService.getPreviousYearInputData(reportId);
        if(previousYearRecords != null){
            for(PreviousYearInputDataDto previousRecord: previousYearRecords){
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setName(previousRecord.getChartOfAccounts().getNameRu());
                recordDto.setCurrentAccountBalance(previousRecord.getAccountBalance());
                recordDto.setAccountNumber(previousRecord.getChartOfAccounts().getNBChartOfAccounts().getCode());
                int lineNumber = getConsolidatedBalanceUSDFormLineNumberByAccountNumber(previousRecord.getChartOfAccounts().getNBChartOfAccounts().getCode());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    continue;
                }
                records.add(recordDto);

                if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                    Double sum = sums.get(recordDto.getLineNumber());
                    sum = sum != null ? sum : 0.0;
                    sum = MathUtils.add(sum, recordDto.getCurrentAccountBalance());
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        List<ConsolidatedBalanceFormRecordDto> incomeExpenseRecords = generateConsolidatedIncomeExpenseUSDForm(reportId);
        if(incomeExpenseRecords != null && !incomeExpenseRecords.isEmpty()){
            for(int i = incomeExpenseRecords.size() - 1; i < incomeExpenseRecords.size(); i--){
                ConsolidatedBalanceFormRecordDto inRecordDto = incomeExpenseRecords.get(i);
                if(inRecordDto.getLineNumber() == 21){
                    ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                    recordDto.setAccountNumber(PeriodicReportConstants.ACC_NUM_5510_010);
                    recordDto.setName(PeriodicReportConstants.RU_5510_010);
                    recordDto.setLineNumber(52);
                    recordDto.setCurrentAccountBalance(inRecordDto.getCurrentAccountBalance());
                    records.add(recordDto);

                    if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                        Double sum = sums.get(recordDto.getLineNumber());
                        sum = sum != null ? sum : 0.0;
                        sum = MathUtils.add(sum, recordDto.getCurrentAccountBalance());
                        sums.put(recordDto.getLineNumber(), sum);
                    }
                    break;
                }
            }
        }

        // Sort
        Collections.sort(records);

        // Add sums
        for(ConsolidatedBalanceFormRecordDto record: records){
            if(StringUtils.isEmpty(record.getAccountNumber()) && record.getLineNumber() != null){
                Double sum = sums.get(record.getLineNumber());
                record.setCurrentAccountBalance(sum);
            }else{
            }
        }

        // Join "duplicate" records into one, exclude records without line number
        List<ConsolidatedBalanceFormRecordDto> recordsNoDuplicates = new ArrayList<>();
        ConsolidatedBalanceFormRecordDto previousRecord = null;
        for(ConsolidatedBalanceFormRecordDto record: records){
            if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_3053_060)){
                // 3053.060 - Прочие краткосрочные финансовые обязательства
                // Do not join duplicates
            }else if(previousRecord != null && previousRecord.getAccountNumber() != null && previousRecord.getName() != null){
                if(record.getAccountNumber() != null && record.getName() != null &&
                        previousRecord.getAccountNumber().equalsIgnoreCase(record.getAccountNumber()) &&
                        previousRecord.getName().equalsIgnoreCase(record.getName())){
                    Double sum = previousRecord.getCurrentAccountBalance() != null && record.getCurrentAccountBalance() != null ?
                            MathUtils.add(previousRecord.getCurrentAccountBalance(), record.getCurrentAccountBalance()) :
                            previousRecord.getCurrentAccountBalance() != null ? previousRecord.getCurrentAccountBalance() : record.getCurrentAccountBalance();
                    previousRecord.setCurrentAccountBalance(sum);
                    continue;
                }
            }
            if(record.getLineNumber() != null) {
                recordsNoDuplicates.add(record);
                previousRecord = record;
            }
        }
        // Set line number sums
        setConsolidatedBalanceUSDFormHeaderSumsAndClearOtherEntityName(recordsNoDuplicates, sums);

        // set additional headers
        recordsNoDuplicates = setConsolidatedBalanceUSDFormAdditionalHeaders(recordsNoDuplicates);

        // TODO: Check : Assets = Liabilities + Capital

        //return records;
        responseDto.setRecords(recordsNoDuplicates);
        return responseDto;
    }

    @Override
    public List<ConsolidatedBalanceFormRecordDto> generateConsolidatedIncomeExpenseUSDForm(Long reportId){
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(currentReport.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            return getConsolidatedIncomeExpenseUSDFormSaved(reportId);
        }else{
            List<ConsolidatedBalanceFormRecordDto> currentPeriodRecords = generateConsolidatedIncomeExpenseUSDFormCurrent(reportId);

            // Set previous month account balance
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if(previousReport != null && previousReport.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
                List<ConsolidatedBalanceFormRecordDto> previousPeriodRecords = getConsolidatedIncomeExpenseUSDFormSaved(previousReport.getId());
                if(previousPeriodRecords != null){

                    ReportingFundRenameInfoDto fundRenameInfo = getFundRenameInfo(reportId);
                    if(previousPeriodRecords != null && !fundRenameInfo.isEmpty()){
                        for(ConsolidatedBalanceFormRecordDto dto: previousPeriodRecords){
                            if(dto.getLineNumber() == 8 && dto.getAccountNumber() != null && dto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030)){
                                for(ReportingFundRenamePairDto pairDto: fundRenameInfo.getFundRenames()){
                                    if(dto.getName().contains(pairDto.getPreviousFundName()) && !pairDto.isUsePreviousFundName()){
                                        // TODO: Check type
                                        String newName = dto.getName().replace(pairDto.getPreviousFundName(), pairDto.getCurrentFundName());
                                        dto.setName(newName);
                                    }else if(dto.getName().contains(pairDto.getCurrentFundName()) && pairDto.isUsePreviousFundName()){
                                        // TODO: Check type
                                        String newName = dto.getName().replace(pairDto.getCurrentFundName(), pairDto.getPreviousFundName());
                                        dto.setName(newName);
                                    }
                                }
                            }
                        }
                    }

                    List<ConsolidatedBalanceFormRecordDto> toAdd = new ArrayList<>();
                    List<Integer> toAddIndex = new ArrayList<>();
                    for(ConsolidatedBalanceFormRecordDto previousRecord: previousPeriodRecords){
                        int header1Index = 0; // Доходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи
                        int header2Index = 0; // Расходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи
                        for(int i = 0; i < currentPeriodRecords.size(); i++){
                            ConsolidatedBalanceFormRecordDto currentRecord = currentPeriodRecords.get(i);
                            if(currentRecord.getName().equalsIgnoreCase(PeriodicReportConstants.INCOME_FAIR_VALUE_CHANGES) &&
                                    currentRecord.getAccountNumber() == null){
                                header1Index = i;
                            }else if(currentRecord.getName().equalsIgnoreCase(PeriodicReportConstants.EXPENSE_FAIR_VALUE_CHANGES) &&
                                    currentRecord.getAccountNumber() == null){
                                header2Index = i;
                            }

                            if(previousRecord.getLineNumber() + 1 == (currentRecord.getLineNumber())){
                                // next line number
                                if(previousRecord.getAccountNumber() != null && previousRecord.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030)){
                                    toAddIndex.add(header1Index + 1);
                                }else if(previousRecord.getAccountNumber() != null && previousRecord.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030)){
                                    toAddIndex.add(header2Index + 1);
                                }else {
                                    toAddIndex.add(i);
                                }
                                toAdd.add(previousRecord);
                                break;
                            }

                            if(isMatchingRecords(currentRecord, previousRecord)){
                                if(!DateUtils.isJanuary(currentReport.getReportDate())){
                                    currentRecord.setPreviousAccountBalance(previousRecord.getCurrentAccountBalance());
                                }
                                break;
                            }
                        }
                    }
                    int added = 0;
                    for(int i = 0; i < toAdd.size(); i++) {
                        ConsolidatedBalanceFormRecordDto recordToAdd = toAdd.get(i);
                        if(recordToAdd.getCurrentAccountBalance() != null && recordToAdd.getCurrentAccountBalance() != 0.0) {
                            if(!DateUtils.isJanuary(currentReport.getReportDate())) {
                                recordToAdd.setPreviousAccountBalance(recordToAdd.getCurrentAccountBalance());
                            }else{
                                recordToAdd.setPreviousAccountBalance(null);
                            }
                            recordToAdd.setCurrentAccountBalance(null);
                            currentPeriodRecords.add(toAddIndex.get(i) + added, recordToAdd);
                            added++;
                        }
                    }

                }
            }else{
                logger.error("ConsolidatedIncomeExpenseUSDForm: No previous month report or report status is not 'SUBMITTED', report id " + reportId);
            }

            // Rename other entity names for NB PORTAL
            for(ConsolidatedBalanceFormRecordDto record: currentPeriodRecords){
                if(record.getOtherEntityName() != null){
                    if(record.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE)){
                        record.setOtherEntityName(record.getOtherEntityName().replace(PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE, PeriodicReportConstants.SINGULARITY_LOWER_CASE));
                    }else if(record.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)){
                        record.setOtherEntityName(record.getOtherEntityName().replace(PeriodicReportConstants.SINGULAR_CAPITAL_CASE, PeriodicReportConstants.SINGULARITY_LOWER_CASE));
                    }else if(record.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_B_CAPITAL_CASE)){
                        record.setOtherEntityName(record.getOtherEntityName().replace(PeriodicReportConstants.TARRAGON_B_CAPITAL_CASE, PeriodicReportConstants.TARRAGON_LOWER_CASE));
                    }else if(record.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)){
                        record.setOtherEntityName(record.getOtherEntityName().replace(PeriodicReportConstants.TARRAGON_CAPITAL_CASE, PeriodicReportConstants.TARRAGON_LOWER_CASE));
                    }else if(record.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)){
                        record.setOtherEntityName(record.getOtherEntityName().replace(PeriodicReportConstants.TERRA_CAPITAL_CASE, PeriodicReportConstants.TERRA_LOWER_CASE));
                    }
                }
            }

            return currentPeriodRecords;
        }


    }

    public List<ConsolidatedBalanceFormRecordDto> generateConsolidatedIncomeExpenseUSDFormCurrent(Long reportId) {
        Map<Integer, Double> sums = new HashedMap();
        for(int i = 1; i <= 21; i++){
            sums.put(i, 0.0);
        }
        // Add line number headers
        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedIncomeExpenseUSDFormLineHeaders();

        // Add NICK MF records
        NICKMFReportingDataHolderDto nickmfReportingDataHolderDto = this.periodicReportNICKMFService.getNICKMFReportingData(reportId);
        if(nickmfReportingDataHolderDto != null && nickmfReportingDataHolderDto.getRecords() != null){
            for(NICKMFReportingDataDto nickMFRecord: nickmfReportingDataHolderDto.getRecords()){
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setAccountNumber(nickMFRecord.getNbChartOfAccountsCode());
                recordDto.setName(nickMFRecord.getNicChartOfAccountsName());
                int lineNumber = getConsolidatedIncomeExpenseUSDFormLineNumberByAccountNumber(nickMFRecord.getNbChartOfAccountsCode());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    continue;
                }
                //recordDto.setOtherEntityName();
                recordDto.setCurrentAccountBalance(lineNumber == 6 || lineNumber == 7  || lineNumber == 8 || lineNumber == 11 || lineNumber == 13 || lineNumber == 16
                        ? MathUtils.subtract(0.0, nickMFRecord.getAccountBalance()) : nickMFRecord.getAccountBalance());
                //recordDto.setPreviousAccountBalance();
                records.add(recordDto);
                if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                    Double sum = sums.get(recordDto.getLineNumber());
                    sum = sum != null ? sum : 0.0;
                    sum = MathUtils.add(sum, recordDto.getCurrentAccountBalance());
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add Singular records
        ListResponseDto singularGeneratedFormResponse = this.periodicReportHFService.getSingularGeneratedForm(reportId);
        List<GeneratedGeneralLedgerFormDto> singularRecords = singularGeneratedFormResponse.getRecords();
        if(singularRecords != null){
            for(GeneratedGeneralLedgerFormDto singularRecord: singularRecords){
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setAccountNumber(singularRecord.getNbAccountNumber());
                recordDto.setName(singularRecord.getNicAccountName());
                int lineNumber = getConsolidatedIncomeExpenseUSDFormLineNumberByAccountNumber(singularRecord.getNbAccountNumber());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    continue;
                }
                recordDto.setOtherEntityName(singularRecord.getAcronym());
                recordDto.setCurrentAccountBalance(lineNumber == 6 || lineNumber == 7  || lineNumber == 8 || lineNumber == 11 || lineNumber == 13 || lineNumber == 16
                        ? MathUtils.subtract(0.0, singularRecord.getGLAccountBalance()) : singularRecord.getGLAccountBalance());
                //recordDto.setPreviousAccountBalance();
                records.add(recordDto);
                if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                    Double sum = sums.get(recordDto.getLineNumber());
                    sum = sum != null ? sum : 0.0;
                    sum = MathUtils.add(sum, recordDto.getCurrentAccountBalance());
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add Tarragon records
        List<GeneratedGeneralLedgerFormDto> tarragonRecords = this.periodicReportPEService.getTarragonGeneratedFormWithoutExcluded(reportId).getRecords();
        if(tarragonRecords != null){
            for(GeneratedGeneralLedgerFormDto tarragonRecord: tarragonRecords){
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setAccountNumber(tarragonRecord.getNbAccountNumber());
                recordDto.setName(tarragonRecord.getNicAccountName());
                int lineNumber = getConsolidatedIncomeExpenseUSDFormLineNumberByAccountNumber(tarragonRecord.getNbAccountNumber());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    continue;
                }
                recordDto.setOtherEntityName(tarragonRecord.getAcronym());
                recordDto.setCurrentAccountBalance(lineNumber == 6 || lineNumber == 7  || lineNumber == 8 || lineNumber == 11 || lineNumber == 13 || lineNumber == 16
                        ? MathUtils.subtract(0.0, tarragonRecord.getGLAccountBalance()) : tarragonRecord.getGLAccountBalance());
                //recordDto.setPreviousAccountBalance();
                records.add(recordDto);
                if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                    Double sum = sums.get(recordDto.getLineNumber());
                    sum = sum != null ? sum : 0.0;
                    sum = MathUtils.add(sum, recordDto.getCurrentAccountBalance());
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add Terra records
        List<TerraGeneratedGeneralLedgerFormDto> terraRecords = this.realEstateService.getTerraGeneralLedgerFormDataWithoutExcluded(reportId).getRecords();
        if(terraRecords != null){
            for(TerraGeneratedGeneralLedgerFormDto terraRecord: terraRecords){
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setAccountNumber(terraRecord.getNbAccountNumber());
                recordDto.setName(terraRecord.getNicAccountName());
                int lineNumber = getConsolidatedIncomeExpenseUSDFormLineNumberByAccountNumber(terraRecord.getNbAccountNumber());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    continue;
                }
                recordDto.setOtherEntityName(terraRecord.getAcronym());
                recordDto.setCurrentAccountBalance(lineNumber == 6 || lineNumber == 7  || lineNumber == 8 || lineNumber == 11 || lineNumber == 13 || lineNumber == 16
                        ? MathUtils.subtract(0.0, terraRecord.getGLAccountBalance()) : terraRecord.getGLAccountBalance());
                //recordDto.setPreviousAccountBalance();
                records.add(recordDto);
                if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                    Double sum = sums.get(recordDto.getLineNumber());
                    sum = sum != null ? sum : 0.0;
                    sum = MathUtils.add(sum, recordDto.getCurrentAccountBalance());
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }


        // Sort
        Collections.sort(records);

        // Add sums
        for(ConsolidatedBalanceFormRecordDto record: records){
            if(StringUtils.isEmpty(record.getAccountNumber()) && record.getLineNumber() != null){
                Double sum = sums.get(record.getLineNumber());
                record.setCurrentAccountBalance(sum);
            }else{
            }
        }

        // Join "duplicate" records into one, exclude records without line number
        List<ConsolidatedBalanceFormRecordDto> recordsNoDuplicates = new ArrayList<>();
        ConsolidatedBalanceFormRecordDto previousRecord = null;
        for(ConsolidatedBalanceFormRecordDto record: records){
            if(previousRecord != null && previousRecord.getAccountNumber() != null && previousRecord.getName() != null){
                if(record.getAccountNumber() != null && record.getName() != null &&
                        previousRecord.getAccountNumber().equalsIgnoreCase(record.getAccountNumber()) &&
                        previousRecord.getName().equalsIgnoreCase(record.getName())){
                    Double sum = previousRecord.getCurrentAccountBalance() != null && record.getCurrentAccountBalance() != null ?
                            previousRecord.getCurrentAccountBalance() + record.getCurrentAccountBalance() :
                            previousRecord.getCurrentAccountBalance() != null ? previousRecord.getCurrentAccountBalance() : record.getCurrentAccountBalance();
                    previousRecord.setCurrentAccountBalance(sum);
                    continue;
                }
            }

            if(record.getLineNumber() != null) {
                recordsNoDuplicates.add(record);
                previousRecord = record;
            }
        }

        // Set line number sums
        setConsolidatedIncomeExpenseUSDFormHeaderSumsAndClearOtherEntityName(recordsNoDuplicates, sums);

        // set additional headers
        recordsNoDuplicates = setConsolidatedIncomeExpenseUSDFormAdditionalHeadersAndClearLineNumbers(recordsNoDuplicates);

        // TODO: Check : Assets = Liabilities + Capital

        //return records;
        return recordsNoDuplicates;
    }

    @Override
    public List<ConsolidatedBalanceFormRecordDto> generateConsolidatedTotalIncomeUSDForm(Long reportId){
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(currentReport.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            return getConsolidatedTotalIncomeUSDFormSaved(reportId);
        }else {

            List<ConsolidatedBalanceFormRecordDto> currentPeriodRecords = generateConsolidatedTotalIncomeUSDFormCurrent(reportId);

            // Set previous month account balance
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if (previousReport != null && previousReport.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())) {
                List<ConsolidatedBalanceFormRecordDto> previousPeriodRecords = getConsolidatedTotalIncomeUSDFormSaved(previousReport.getId());
                if (previousPeriodRecords != null) {
                    for (ConsolidatedBalanceFormRecordDto previousRecord : previousPeriodRecords) {
                        for (ConsolidatedBalanceFormRecordDto currentRecord : currentPeriodRecords) {
                            if (currentRecord.getName().equalsIgnoreCase(previousRecord.getName()) &&
                                    currentRecord.getLineNumber() != null && previousRecord.getLineNumber() != null &&
                                    previousRecord.getLineNumber() == currentRecord.getLineNumber()) {
                                if(!DateUtils.isJanuary(currentReport.getReportDate())){
                                    currentRecord.setPreviousAccountBalance(previousRecord.getCurrentAccountBalance());
                                }else{
                                    currentRecord.setPreviousAccountBalance(null);
                                }
                                break;
                            }
                        }
                    }
                }
            } else {
                logger.error("ConsolidatedTotalIncomeUSDForm: No previous month report or report status is not 'SUBMITTED', report id " + reportId);
            }

            return currentPeriodRecords;
        }
    }

    public List<ConsolidatedBalanceFormRecordDto> generateConsolidatedTotalIncomeUSDFormCurrent(Long reportId) {
        Map<Integer, Double> sums = new HashedMap();
        for(int i = 1; i <= 6; i++){
            sums.put(i, 0.0);
        }
        // Add line number headers
        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedTotalIncomeUSDFormLineHeaders();

        // Get consolidated income expense
        Double header_1_Balance = null;
        List<ConsolidatedBalanceFormRecordDto> incomeExpenseFormRecords = generateConsolidatedIncomeExpenseUSDForm(reportId);
        if(incomeExpenseFormRecords != null && !incomeExpenseFormRecords.isEmpty()){
            for(ConsolidatedBalanceFormRecordDto inRecordDto: incomeExpenseFormRecords){
                if(inRecordDto.getLineNumber() != null && inRecordDto.getLineNumber() == 21){
                    header_1_Balance = inRecordDto.getCurrentAccountBalance();
                    break;
                }
            }
        }

        // Set values from other reports
        for(ConsolidatedBalanceFormRecordDto record: records){
            if(record.getLineNumber() != null){
                if(record.getLineNumber() == 1){
                    record.setCurrentAccountBalance(header_1_Balance);
                }
                if(record.getCurrentAccountBalance() != null) {
                    Double sum = sums.get(record.getLineNumber()) != null ? sums.get(record.getLineNumber()) : 0.0;
                    sums.put(record.getLineNumber(), MathUtils.add(sum, record.getCurrentAccountBalance()));
                }
            }
        }

        // Sort
        Collections.sort(records);

        // Set total sums
        for(ConsolidatedBalanceFormRecordDto record: records){
            if(record.getLineNumber() != null){
                if(record.getLineNumber() == 7){
                    record.setCurrentAccountBalance(MathUtils.add(sums.get(4), sums.get(5), sums.get(6)));
                }else if(record.getLineNumber() == 11){
                    record.setCurrentAccountBalance(MathUtils.add(sums.get(9), sums.get(10)));
                }else if(record.getLineNumber() == 12){
                    record.setCurrentAccountBalance(MathUtils.add(sums.get(7), sums.get(11)));
                }else if(record.getLineNumber() == 13){
                    record.setCurrentAccountBalance(MathUtils.add(sums.get(1), sums.get(12)));
                }
            }
        }

        return records;
    }

    // KZT Reports
    @Override
    public ListResponseDto generateConsolidatedBalanceKZTForm1(Long reportId) {
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        if(currentReport.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedBalanceKZTForm1Saved(reportId);
            responseDto.setRecords(records);
            return responseDto;
        }else{

            ListResponseDto responseDtoKZTForm1 = generateConsolidatedBalanceKZTForm1Current(reportId);
            if(responseDtoKZTForm1.getStatus() == ResponseStatusType.FAIL){
                //return responseDtoKZTForm1;
                responseDto.setStatus(responseDtoKZTForm1.getStatus());
                responseDto.setMessage(responseDtoKZTForm1.getMessage());
            }
            List<ConsolidatedBalanceFormRecordDto> currentRecords = responseDtoKZTForm1.getRecords();

            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);

            List<ConsolidatedBalanceFormRecordDto> toAddRecords = new ArrayList<>();
            List<Integer> toAddIndices = new ArrayList<>();
            if (previousReport != null) {
                List<ConsolidatedBalanceFormRecordDto> previousRecords = getConsolidatedBalanceKZTForm1Saved(previousReport.getId());
                if(previousRecords != null){
                    for(ConsolidatedBalanceFormRecordDto previousRecord: previousRecords){
                        for(int i = 0; currentRecords != null && i < currentRecords.size(); i++){
                            ConsolidatedBalanceFormRecordDto currentRecord = currentRecords.get(i);
                            if(previousRecord.getAccountNumber() == null && previousRecord.getLineNumber() != null &&
                                    (previousRecord.getLineNumber() == 1 /*|| previousRecord.getLineNumber() == 14*/)){
                                previousRecord.setCurrentAccountBalance(null);
                                previousRecord.setPreviousAccountBalance(null);
                            }
                            if(isMatchingRecords(currentRecord, previousRecord)){
                                currentRecord.setPreviousAccountBalance(previousRecord.getCurrentAccountBalance());
                                break;
                            }
                            //next line number, means record was not found, possibly  need to add
                            if(previousRecord.getLineNumber() + 1 == (currentRecord.getLineNumber())){
                                toAddIndices.add(i);
                                toAddRecords.add(previousRecord);
                                break;
                            }
                        }
                    }

                    int added = 0;
                    for(int i = 0; i < toAddRecords.size(); i++) {
                        ConsolidatedBalanceFormRecordDto recordToAdd = toAddRecords.get(i);
                        if(recordToAdd.getCurrentAccountBalance() != null && recordToAdd.getCurrentAccountBalance() != 0) {
                            recordToAdd.setPreviousAccountBalance(recordToAdd.getCurrentAccountBalance());
                            recordToAdd.setCurrentAccountBalance(null);
                            currentRecords.add(toAddIndices.get(i) + added, recordToAdd);
                            added++;
                        }
                    }
                }
            }

            responseDto.setRecords(currentRecords);
            return responseDto;
        }
    }

    private ListResponseDto generateConsolidatedBalanceKZTForm1Current(Long reportId) { ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        Date rateDate = DateUtils.getFirstDayOfNextMonth(currentReport.getReportDate());
        // Find exchange rate
        CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, CurrencyLookup.USD.getCode());
        if(endCurrencyRatesDto == null || endCurrencyRatesDto.getValue() == null){
            logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
            // error message
            responseDto.setErrorMessageEn("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
            return responseDto;
        }

        Date averageRateDate = DateUtils.getLastDayOfCurrentMonth(currentReport.getReportDate());
        Double averageRate = null;
        try {
            averageRate = this.currencyRatesService.getAverageRateForAllMonthsBeforeDateAndCurrency(averageRateDate, CurrencyLookup.USD.getCode(), 2);
            if (averageRate == null) {
                logger.error("No average currency rate found for date '" + DateUtils.getDateFormatted(averageRateDate));
                responseDto.setErrorMessageEn("No average currency rate found for date '" + DateUtils.getDateFormatted(averageRateDate) + "'");
                return responseDto;
            }
        }catch (IllegalStateException ex){
            responseDto.setErrorMessageEn(ex.getMessage());
            return responseDto;
        }

//        Map<Integer, Double> sums = new HashedMap();
//        for(int i = 1; i <= 52; i++){
//            sums.put(i, 0.0);
//        }
        // Add line number headers
        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedBalanceKZTForm1LineHeaders();

        ListResponseDto USDRecordsResponseDto = generateConsolidatedBalanceUSDForm(reportId);
        if(USDRecordsResponseDto.getStatus() == ResponseStatusType.FAIL){
            return USDRecordsResponseDto;
        }
        List<ConsolidatedBalanceFormRecordDto> recordsUSD = USDRecordsResponseDto.getRecords();

        String record1033_010Name = PeriodicReportConstants.RU_1033_010;
        String record1033_010AccountNumber = PeriodicReportConstants.ACC_NUM_1033_010;
        Double record1033_010 = 0.0;
        Double record3053_060 = 0.0;

        String record5440_010Name = PeriodicReportConstants.RU_5440_010_a;
        String record5440_010ReportName = PeriodicReportConstants.RU_5440_010_b;
        String record5440_010AccountNumber = PeriodicReportConstants.ACC_NUM_5440_010;
        Double record5440_010 = 0.0;
        Double lineNumber41USDReportSum = 0.0;
        if(recordsUSD != null){
            for(ConsolidatedBalanceFormRecordDto record: recordsUSD){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record1033_010AccountNumber) &&
                        record.getName().equalsIgnoreCase(record1033_010Name)){
                    record1033_010 = MathUtils.add(record1033_010, MathUtils.multiply(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue()));
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record5440_010AccountNumber) &&
                        record.getName().equalsIgnoreCase(record5440_010Name)){
                    // Average rate
                    record5440_010 = MathUtils.add(record5440_010, MathUtils.multiply(record.getCurrentAccountBalance(), averageRate));
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 41){
                    lineNumber41USDReportSum = MathUtils.multiply(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060) &&
                        record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_3053_060)){
                    record3053_060 = MathUtils.add(record3053_060, MathUtils.multiply(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue()));
                }
            }
        }

        String record1283_020Name = PeriodicReportConstants.RU_1283_020;
        String record1283_020AccountNumber = PeriodicReportConstants.ACC_NUM_1283_020;
        Double record1283_020 = 0.0;
        /* Report KZT 8 ***********************************************************************************************/
        ListResponseDto KZTForm8ResponseDto = generateConsolidatedBalanceKZTForm8(reportId);
        if(KZTForm8ResponseDto.getStatus() == ResponseStatusType.FAIL){
            //responseDto.setErrorMessageEn("Error generating KZT Form 8. " + KZTForm8ResponseDto.getMessage().getNameEn());
            //return responseDto;
            responseDto.appendErrorMessageEn("Error generating KZT Form 8. " + KZTForm8ResponseDto.getMessage().getNameEn());
        }
        List<ConsolidatedKZTForm8RecordDto> form8Records = KZTForm8ResponseDto.getRecords();
        if(form8Records != null){
            for(ConsolidatedKZTForm8RecordDto record: form8Records){
                if(record.getAccountNumber() == null && record.getLineNumber() == 6 && record.getDebtEndPeriod() != null){
                    record1283_020 = record.getDebtEndPeriod();
                }
            }
        }

        /* ************************************************************************************************************/

        String record1623_010Name = PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD;
        String record1623_010AccountNumber = PeriodicReportConstants.ACC_NUM_1623_010;
        Double record1623_010 = 0.0;

        String record2923_010Name = PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD;
        String record2923_010AccountNumber = PeriodicReportConstants.ACC_NUM_2923_010;
        Double record2923_010 = 0.0;

        /* Report KZT 10 **********************************************************************************************/
        ListResponseDto KZTForm10ResponseDto = generateConsolidatedBalanceKZTForm10(reportId);
        if(KZTForm10ResponseDto.getStatus() == ResponseStatusType.FAIL){
            responseDto.setErrorMessageEn("Error generating KZT Form 10. " + KZTForm10ResponseDto.getMessage().getNameEn());
            return responseDto;
        }
        List<ConsolidatedKZTForm10RecordDto> form10Records = KZTForm10ResponseDto.getRecords();
        if(form10Records != null){
            for(ConsolidatedKZTForm10RecordDto record: form10Records){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record1623_010AccountNumber) && record.getEndPeriodBalance() != null){
                    record1623_010 = MathUtils.add(record1623_010, record.getEndPeriodBalance());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record2923_010AccountNumber) && record.getEndPeriodBalance() != null){
                    record2923_010 = MathUtils.add(record2923_010, record.getEndPeriodBalance());
                }
            }
        }
        /* ************************************************************************************************************/

        String record2033_010Name = PeriodicReportConstants.RU_2033_010;
        String record2033_010AccountNumber = PeriodicReportConstants.ACC_NUM_2033_010;
        Double record2033_010 = 0.0;
        Double record1123_010 = 0.0;

        String record2033_040Name = PeriodicReportConstants.RU_2033_040;
        String record2033_040AccountNumber = PeriodicReportConstants.ACC_NUM_2033_040;
        Double record2033_040 = 0.0;
        Double record1123_020 = 0.0;

        String record2033_050Name = PeriodicReportConstants.RU_2033_050;
        String record2033_050AccountNumber = PeriodicReportConstants.ACC_NUM_2033_050;
        Double record2033_050 = 0.0;
        Double record1123_030 = 0.0;

        Double record1183_040 = 0.0;

        /* Report KZT 7 **********************************************************************************************/
        ListResponseDto KZTForm7ResponseDto = generateConsolidatedBalanceKZTForm7(reportId);
        if(KZTForm7ResponseDto.getStatus() == ResponseStatusType.FAIL){
            responseDto.setErrorMessageEn("Error when generating KZT Form 7. " + KZTForm7ResponseDto.getMessage().getNameEn());
            return responseDto;
        }
        List<ConsolidatedKZTForm7RecordDto> form7Records = KZTForm7ResponseDto.getRecords();
        if(form7Records != null){
            for(ConsolidatedKZTForm7RecordDto record: form7Records){
//                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record2033_010AccountNumber)){
//                    record2033_010 = MathUtils.add(record2033_010, record.getDebtEndPeriod() != null ? record.getDebtEndPeriod() : 0);
//                    if(record.getFairValueAdjustmentsEndPeriod() != null){
//                        record2033_040 = MathUtils.add(record2033_040, record.getFairValueAdjustmentsEndPeriod().doubleValue() >= 0 ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0);
//                        record2033_050 = MathUtils.add(record2033_050, record.getFairValueAdjustmentsEndPeriod().doubleValue() < 0 ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0);
//                    }
//                }
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010)){
                    record1123_010 = MathUtils.add(record1123_010, record.getDebtEndPeriod() != null ? record.getDebtEndPeriod() : 0);
                    if(record.getFairValueAdjustmentsEndPeriod() != null){
//                        record2033_040 = MathUtils.add(record2033_040, record.getFairValueAdjustmentsEndPeriod().doubleValue() >= 0 ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0);
//                        record2033_050 = MathUtils.add(record2033_050, record.getFairValueAdjustmentsEndPeriod().doubleValue() < 0 ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0);

                        record1123_020 = MathUtils.add(record1123_020, record.getFairValueAdjustmentsEndPeriod().doubleValue() >= 0 ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0);
                        record1123_030 = MathUtils.add(record1123_030, record.getFairValueAdjustmentsEndPeriod().doubleValue() < 0 ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0);
                    }
                    if(record.getInterestEndPeriod() != null){
                        record1183_040 = MathUtils.add(record1183_040, record.getInterestEndPeriod());
                    }
                }
            }
        }
        /* ************************************************************************************************************/

        String record3013_010Name = PeriodicReportConstants.RU_3013_010;
        String record3013_010AccountNumber = PeriodicReportConstants.ACC_NUM_3013_010;
        Double record3013_010 = 0.0;

        String record3063_010Name = PeriodicReportConstants.RU_3063_010;
        String record3063_010AccountNumber = PeriodicReportConstants.ACC_NUM_3063_010;
        Double record3063_010 = 0.0;

        /* Report KZT 13 ***********************************************************************************************/
        ListResponseDto KZTForm13ResponseDto = generateConsolidatedBalanceKZTForm13(reportId);
        if(KZTForm13ResponseDto.getStatus() == ResponseStatusType.FAIL){
            responseDto.setErrorMessageEn("Error generating KZT Form 13. " + KZTForm13ResponseDto.getMessage().getNameEn());
            return responseDto;
        }
        List<ConsolidatedKZTForm13RecordDto> form13Records = KZTForm13ResponseDto.getRecords();
        if(form13Records != null){
            for(ConsolidatedKZTForm13RecordDto record: form13Records){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3013_010AccountNumber) && record.getDebtEndPeriod() != null){
                    record3013_010 = MathUtils.add(record3013_010, record.getDebtEndPeriod());
                }
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3013_010AccountNumber) && record.getInterestEndPeriod() != null){
                    record3063_010 = MathUtils.add(record3063_010, record.getInterestEndPeriod());
                }
            }
        }
        /* ************************************************************************************************************/


        /* Report KZT 14 ***********************************************************************************************/
        String record3393_020Name = PeriodicReportConstants.RU_3393_020;
        String record3393_020AccountNumber = PeriodicReportConstants.ACC_NUM_3393_020;
        Double record3393_020 = 0.0;
        ListResponseDto KZTForm14ResponseDto  = generateConsolidatedBalanceKZTForm14(reportId);
        if(KZTForm14ResponseDto.getStatus() == ResponseStatusType.FAIL){
            responseDto.setErrorMessageEn("Error generating KZT Form 14. " + KZTForm14ResponseDto.getMessage().getNameEn());
            return responseDto;
        }
        List<ConsolidatedKZTForm14RecordDto> form14Records = KZTForm14ResponseDto.getRecords();
        if(form14Records != null){
            for(ConsolidatedKZTForm14RecordDto record: form14Records){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3393_020AccountNumber)){
                    record3393_020 = MathUtils.add(record3393_020, record.getDebtEndPeriod() != null ? record.getDebtEndPeriod().doubleValue() : 0);
                }
            }
        }
        /* ************************************************************************************************************/

        String record5440_010_previousYearName = PeriodicReportConstants.RU_5440_010__LAST_YEAR;
        String record5520_010Name = PeriodicReportConstants.RU_5520_010;
        String record5520_010AccountNumber = PeriodicReportConstants.ACC_NUM_5520_010;
        Double record5520_010 = 0.0;
        Double record5450_010Corrections = 0.0;
        List<PreviousYearInputDataDto> previousYearInputData = this.prevYearInputService.getPreviousYearInputData(reportId);
        if(previousYearInputData != null){
            for(PreviousYearInputDataDto inputData: previousYearInputData){
                if(inputData.getChartOfAccounts() != null && inputData.getChartOfAccounts().getCode().startsWith(PeriodicReportConstants.ACC_NUM_5440_010) &&
                        inputData.getChartOfAccounts().getNameRu().equalsIgnoreCase(record5440_010_previousYearName)){
                    record5440_010 = MathUtils.add(record5440_010, inputData.getAccountBalanceKZT());
                }else if(inputData.getChartOfAccounts() != null && inputData.getChartOfAccounts().getCode().startsWith(record5520_010AccountNumber) &&
                        inputData.getChartOfAccounts().getNameRu().equalsIgnoreCase(record5520_010Name)){
                    record5520_010 = MathUtils.add(record5520_010, inputData.getAccountBalanceKZT() != null ? inputData.getAccountBalanceKZT().doubleValue() : 0);
                }else if(inputData.getChartOfAccounts() != null && inputData.getChartOfAccounts().getCode().startsWith(PeriodicReportConstants.ACC_NUM_5450_010_CODE_ADJUSTMENT) &&
                        inputData.getChartOfAccounts().getNameRu().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5450_010_CODE_ADJUSTMENT_RU)){
                    // 5450.010 adjustment
                    record5450_010Corrections = MathUtils.add(record5450_010Corrections, inputData.getAccountBalanceKZT());
                }
            }
        }

        String record5021_010AccountNumber = PeriodicReportConstants.ACC_NUM_5021_010;
        String record5022_010AccountNumber = PeriodicReportConstants.ACC_NUM_5022_010;

        String record5021_010Name = PeriodicReportConstants.COMMON_SHARES;
        String record5022_010Name = PeriodicReportConstants.COMMON_SHARES;

        Double record5022_010 = 18765.0;

        // TODO: Refactor string literal
        Double record5021_010 = 0.0;
        List<ReserveCalculationDto> reserveCalculations = null;
        try {
            reserveCalculations = this.reserveCalculationService.getReserveCalculationsByExpenseTypeBeforeDate(ReserveCalculationsExpenseTypeLookup.ADD.getCode(), currentReport.getReportDate());
            List<ReserveCalculationDto> reserveCalculationsNICKMFExp =
                    this.reserveCalculationService.getReserveCalculationsByExpenseTypeBeforeDate(ReserveCalculationsExpenseTypeLookup.NICK_MF_EXPENSES.getCode(), currentReport.getReportDate());
            if(reserveCalculationsNICKMFExp != null){
                reserveCalculations.addAll(reserveCalculationsNICKMFExp);
            }
        }catch (IllegalStateException ex){
            responseDto.setErrorMessageEn("Reserve Calculation failed. " + ex.getMessage());
            return responseDto;
        }

        if(reserveCalculations != null){
            Double sum = 0.0;
            for(ReserveCalculationDto reserveCalculationDto: reserveCalculations){
                if(reserveCalculationDto.getCurrencyRate() == null){
                    String errorMessage = "One of the reserve calculation records has no currency rate: date '" + DateUtils.getDateFormatted(reserveCalculationDto.getDate()) + "'";
                    logger.error(errorMessage);
                    responseDto.setErrorMessageEn(errorMessage);
                    return responseDto;
                }
                if(reserveCalculationDto.getDate().compareTo(currentReport.getReportDate()) <= 0) {
                    sum = MathUtils.add(sum, reserveCalculationDto.getAmountKZT());

                }
            }
            record5021_010 = MathUtils.subtract(sum, record5022_010);
        }

        String record5450_010AccountNumber = PeriodicReportConstants.ACC_NUM_5450_010;
        String record5450_010Name = PeriodicReportConstants.RU_5450_010;

        Double record5450_010 = 0.0;
        try{
            record5450_010 = MathUtils.add(record5450_010Corrections, getCurrencyReserveCalculationKZTForm1(currentReport.getId(), currentReport.getReportDate()));

        }catch (IllegalStateException ex){
            responseDto.setErrorMessageEn(ex.getMessage());
            return responseDto;
        }

        Map<Integer, Double> sums = new HashedMap();
        for(int i = 1; i <= 54; i++){
            sums.put(i, 0.0);
        }

        String record5510_010AccountNumber = PeriodicReportConstants.ACC_NUM_5510_010;
        String record5510_010Name = PeriodicReportConstants.RU_5510_010;
        Double record5510_010 = 0.0;

        ListResponseDto KZTForm2ResponseDto  = generateConsolidatedIncomeExpenseKZTForm2(reportId);
        if(KZTForm2ResponseDto.getStatus() == ResponseStatusType.FAIL){
            responseDto.setErrorMessageEn("Error generating KZT Form 2 report. " + KZTForm2ResponseDto.getMessage().getNameEn());
            return responseDto;
        }
        List<ConsolidatedBalanceFormRecordDto> form2Records = KZTForm2ResponseDto.getRecords();
        for(int i = form2Records.size() - 1; i >= 0; i--){
            if(form2Records.get(i).getAccountNumber() == null && form2Records.get(i).getLineNumber() != null && form2Records.get(i).getLineNumber() == 21 &&
                    form2Records.get(i).getCurrentAccountBalance() != null){
                record5510_010 = form2Records.get(i).getCurrentAccountBalance();
                break;
            }
        }

        for(ConsolidatedBalanceFormRecordDto record: records){
            if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record1033_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record1033_010Name)){
                record.setCurrentAccountBalance(record1033_010);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record1283_020AccountNumber) &&
                    record.getName().equalsIgnoreCase(record1283_020Name)){
                record.setCurrentAccountBalance(record1283_020);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record1623_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record1623_010Name)){
                record.setCurrentAccountBalance(record1623_010);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_1123_010)){
                record.setCurrentAccountBalance(record1123_010);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_020) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_1123_020)){
                record.setCurrentAccountBalance(record1123_020);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_030) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_1123_030)){
                record.setCurrentAccountBalance(record1123_030);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1183_040) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_1183_040)){
                record.setCurrentAccountBalance(record1183_040);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record2033_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record2033_010Name)){
                record.setCurrentAccountBalance(record2033_010);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record2033_040AccountNumber) &&
                    record.getName().equalsIgnoreCase(record2033_040Name)){
                record.setCurrentAccountBalance(record2033_040);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record2033_050AccountNumber) &&
                    record.getName().equalsIgnoreCase(record2033_050Name)){
                record.setCurrentAccountBalance(record2033_050);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record2923_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record2923_010Name)){
                record.setCurrentAccountBalance(record2923_010);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3013_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record3013_010Name)){
                record.setCurrentAccountBalance(record3013_010);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3063_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record3063_010Name)){
                record.setCurrentAccountBalance(record3063_010);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3393_020AccountNumber) &&
                    record.getName().equalsIgnoreCase(record3393_020Name)){
                record.setCurrentAccountBalance(record3393_020);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3393_020AccountNumber) &&
                    record.getName().equalsIgnoreCase(record3393_020Name)){
                record.setCurrentAccountBalance(record3393_020);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record5440_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record5440_010ReportName)){
                record.setCurrentAccountBalance(record5440_010);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record5520_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record5520_010Name)){
                record.setCurrentAccountBalance(record5520_010);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record5021_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record5021_010Name)){
                record.setCurrentAccountBalance(record5021_010);
                record.setOtherEntityName("НБРК");
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record5022_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record5022_010Name)){
                record.setCurrentAccountBalance(record5022_010);
                record.setOtherEntityName("НИК");
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record5450_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record5450_010Name)){
                record.setCurrentAccountBalance(record5450_010);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record5510_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record5510_010Name)){
                record.setCurrentAccountBalance(record5510_010);
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_3053_060)) {
                record.setCurrentAccountBalance(record3053_060);
            }else{
                continue;
            }

            if(record.getLineNumber() != null){
                if (sums.get(record.getLineNumber()) == null) {
                    sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
                } else {
                    Double value = sums.get(record.getLineNumber());
                    sums.put(record.getLineNumber(), MathUtils.add(value,record.getCurrentAccountBalance()));
                }
            }
        }

        int lineNumber38Index = 0;
        Double lineNumber36Value = null;
        for(int i = 0; i < records.size(); i++){
            ConsolidatedBalanceFormRecordDto record = records.get(i);
            if(record.getLineNumber() != null && record.getLineNumber() == 14 && record.getAccountNumber() == null){
                Double value = getSumFromMap(sums, 2, 13);
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }else if(record.getLineNumber() != null && record.getLineNumber() == 27 && record.getAccountNumber() == null){
                Double value = getSumFromMap(sums, 16, 26);
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }else if(record.getLineNumber() != null && record.getLineNumber() == 28 && record.getAccountNumber() == null){
                Double value = MathUtils.add(sums.get(14), sums.get(27)).doubleValue();
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }else if(record.getLineNumber() != null && record.getLineNumber() == 37 && record.getAccountNumber() == null){
                Double value = getSumFromMap(sums, 30, 36);
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }else if(record.getLineNumber() != null && record.getLineNumber() == 38 && record.getAccountNumber() == null){
                lineNumber38Index = i;
            }else if(record.getLineNumber() != null && record.getLineNumber() == 41){
                record.setCurrentAccountBalance(lineNumber41USDReportSum);
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }else if(record.getLineNumber() != null && record.getLineNumber() == 45 && record.getAccountNumber() == null){
                Double value = getSumFromMap(sums, 39, 44);
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
                lineNumber36Value = record.getCurrentAccountBalance();
            }else if(record.getLineNumber() != null && record.getLineNumber() == 53 && record.getAccountNumber() == null){
                Double value = getSumFromMap(sums, 47, 52);
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }else if(record.getLineNumber() != null && record.getLineNumber() == 54 && record.getAccountNumber() == null){
                Double value = MathUtils.add(MathUtils.add(sums.get(37), sums.get(45)), sums.get(53)).doubleValue();
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }else if(record.getLineNumber() != null && sums.get(record.getLineNumber()) != null && record.getCurrentAccountBalance() == null &&
                    record.getAccountNumber() == null){
                record.setCurrentAccountBalance(sums.get(record.getLineNumber()).doubleValue());
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }
        }

        Double totalSumCheck = 0.0;
        int correctionRecordIndex = 0;
        int correctionRecordHeader49Index = 0;
        int correctionRecordHeader51Index = 0;
        int correctionRecordHeader52Index = 0;
        Double value = 0.0;
        Double valueHeader51 = 0.0;
        Double valueHeader53 = 0.0;
        Double valueHeader54 = 0.0;
        for(int i = 0; i < records.size(); i++){
            ConsolidatedBalanceFormRecordDto record = records.get(i);
            if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 28){
                totalSumCheck = MathUtils.add(totalSumCheck, record.getCurrentAccountBalance());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null &&
                    (record.getLineNumber() == 37 || record.getLineNumber() == 45 || record.getLineNumber() == 47 || record.getLineNumber() == 52)){
                totalSumCheck = MathUtils.subtract(totalSumCheck, record.getCurrentAccountBalance());
            }else if(record.getLineNumber() != null && record.getLineNumber() == 51 && record.getAccountNumber()!= null &&
                    record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010)){
                totalSumCheck = MathUtils.subtract(totalSumCheck, record.getCurrentAccountBalance());
            }else if(record.getLineNumber() != null && record.getLineNumber() == 51 && record.getAccountNumber()!= null &&
                    record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5450_010)){
                correctionRecordIndex = i;
                value = record.getCurrentAccountBalance();
            }else if(record.getLineNumber() != null && record.getLineNumber() == 51 && record.getAccountNumber() == null){
                correctionRecordHeader49Index = i;
                valueHeader51 = record.getCurrentAccountBalance();
            }else if(record.getLineNumber() != null && record.getLineNumber() == 53 && record.getAccountNumber() == null){
                correctionRecordHeader51Index = i;
                valueHeader53 = record.getCurrentAccountBalance();
            }else if(record.getLineNumber() != null && record.getLineNumber() == 54 && record.getAccountNumber() == null){
                correctionRecordHeader52Index = i;
                valueHeader54 = record.getCurrentAccountBalance();
            }
        }

        Double difference = MathUtils.subtract(value, totalSumCheck);
        if(difference > -1 && difference < 1 && correctionRecordIndex > 0){
            records.get(correctionRecordIndex).setCurrentAccountBalance(totalSumCheck);
            records.get(correctionRecordHeader49Index).setCurrentAccountBalance(MathUtils.subtract(valueHeader51, difference));
            records.get(correctionRecordHeader51Index).setCurrentAccountBalance(MathUtils.subtract(valueHeader53, difference));
            records.get(correctionRecordHeader52Index).setCurrentAccountBalance(MathUtils.subtract(valueHeader54, difference));
        }else if(difference > 1 || difference < -1){
            String errorMessage = "Record '5450.010' (код строки #51) value = " + String.format(Locale.ENGLISH, "%,.2f", value) + ", #28 - #37 - #45 - #47 - #52 - '5440.010'(код строки #51) = " +
                    String.format(Locale.ENGLISH, "%,.2f", totalSumCheck) +
                    ". Difference = " + String.format(Locale.ENGLISH, "%,.2f", difference);
            logger.error(errorMessage);

            responseDto.setErrorMessageEn(errorMessage);
        }

        responseDto.setRecords(records);
        return responseDto;
    }

    private Double getCurrencyReserveCalculationKZTForm1(Long reportId, Date reportDate){

        Double total = 0.0;

        Date nextDay = DateUtils.getNextDay(reportDate);
        CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, CurrencyLookup.USD.getCode());
        Double avgCurrencyRateForReportDate = null;
        try{
           avgCurrencyRateForReportDate = this.currencyRatesService.getAverageRateForAllMonthsBeforeDateAndCurrency(reportDate, CurrencyLookup.USD.getCode(), 2);
            if (avgCurrencyRateForReportDate == null) {
                logger.error("No average currency rate found for date '" + DateUtils.getDateFormatted(DateUtils.getNextDay(reportDate)));
               throw new IllegalStateException("No average currency rate found for date '" + DateUtils.getDateFormatted(DateUtils.getNextDay(reportDate)));
            }
        }catch (IllegalStateException ex) {
            throw ex;
        }

        List<ReserveCalculationDto> reserveCalculations = null;
        try{
            reserveCalculations = this.reserveCalculationService.getReserveCalculationsByExpenseTypeBeforeDate(ReserveCalculationsExpenseTypeLookup.ADD.getCode(), reportDate);
            List<ReserveCalculationDto> reserveCalculationsNICKMFExp =
                    this.reserveCalculationService.getReserveCalculationsByExpenseTypeBeforeDate(ReserveCalculationsExpenseTypeLookup.NICK_MF_EXPENSES.getCode(), reportDate);
            if(reserveCalculationsNICKMFExp != null){
                reserveCalculations.addAll(reserveCalculationsNICKMFExp);
            }
        }catch (IllegalStateException ex){
            throw ex;
        }
        if(reserveCalculations != null){
            Double sumKZTInitial = 0.0;
            Double sumKZTOnReportDate = 0.0;
            for(ReserveCalculationDto reserveCalculationDto: reserveCalculations){
                if(reserveCalculationDto.getDate().compareTo(reportDate) <= 0) {
                    sumKZTInitial = MathUtils.add(sumKZTInitial, reserveCalculationDto.getAmountKZT());
                    Double calculatedAmount = reserveCalculationDto.getAmount() != null ?
                            MathUtils.multiply(10, reserveCalculationDto.getAmount(), currencyRatesDto.getValue()) : 0.0;

                    sumKZTOnReportDate = MathUtils.add(sumKZTOnReportDate, calculatedAmount);
                }
            }
            total = MathUtils.subtract(MathUtils.add(total, sumKZTOnReportDate), sumKZTInitial);
        }

        int currentYear = DateUtils.getYear(reportDate);
        for(int year = 2015; year < currentYear; year++){
            PeriodicDataDto netProfitDto = this.periodicDataService.get(DateUtils.getDate("31.12." + year), PeriodicDataTypeLookup.NET_PROFIT.getCode());
            if(netProfitDto == null){
                logger.error("'" + PeriodicDataTypeLookup.NET_PROFIT.getName() + "' not found for date '31.12." + year + "'");
                throw  new IllegalStateException("'" + PeriodicDataTypeLookup.NET_PROFIT.getName() + "' not found for date '31.12." + year + "'");
            }
            PeriodicDataDto reserveRevalutionDto = this.periodicDataService.get(DateUtils.getDate("31.12." + year), PeriodicDataTypeLookup.RESERVE_REVALUATION.getCode());
            if(reserveRevalutionDto == null){
                logger.error("'" + PeriodicDataTypeLookup.RESERVE_REVALUATION.getName() + "' not found for date '31.12." + year + "'");
                throw  new IllegalStateException("'" + PeriodicDataTypeLookup.RESERVE_REVALUATION.getName() + "' not found for date '31.12." + year + "'");
            }

            // TODO: ?
            int scale = year == 2015 ? 4 : 2;
            Double avgCurrencyRate = null;
            try {
                avgCurrencyRate = this.currencyRatesService.getAverageYearRateForFixedDateAndCurrency(DateUtils.getDate("31.12." + year), CurrencyLookup.USD.getCode());
                //avgCurrencyRate = this.currencyRatesService.getAverageRateForAllMonthsBeforeDateAndCurrency(DateUtils.getDate("31.12." + year), CurrencyLookup.USD.getCode(), scale);
                if(avgCurrencyRate == null){
                    logger.error("No average currency rate found for date '" + DateUtils.getDate("31.12." + year) + "'");
                    throw  new IllegalStateException("No average currency rate found for date '" + DateUtils.getDate("31.12." + year) + "'");
                }
            }catch (IllegalStateException ex){
                throw ex;
            }

            if(netProfitDto != null){
                total = MathUtils.add(total, MathUtils.multiply(10, netProfitDto.getValue(), currencyRatesDto.getValue()));
                total = MathUtils.subtract(total, MathUtils.multiply(10, netProfitDto.getValue(), avgCurrencyRate));
            }
            if(reserveRevalutionDto != null){
                total = MathUtils.add(total, MathUtils.multiply(10, reserveRevalutionDto.getValue(), currencyRatesDto.getValue()));
                total = MathUtils.subtract(total, MathUtils.multiply(10, reserveRevalutionDto.getValue(), avgCurrencyRate));
            }

            if(currentYear == year + 1){
                // TODO: if restated NET PROFIT and RESERVE, then skip
//                List<PreviousYearInputDataDto> previousYearInputDataDtos = this.prevYearInputService.getPreviousYearInputData(reportId);
//                if(previousYearInputDataDtos != null){
//                    for(PreviousYearInputDataDto previousYearInputDataDto: previousYearInputDataDtos){
//                        if(previousYearInputDataDto.getChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.RU_KZT_6_REPORT_LINE_NUMBER_2_8_CODE) &&
//                                previousYearInputDataDto.getAccountBalance() != null){
//                            total = total.add(new BigDecimal(previousYearInputDataDto.getAccountBalance()).multiply(new BigDecimal(currencyRatesDto.getValue())));
//                            total = total.subtract(new BigDecimal(previousYearInputDataDto.getAccountBalance()).multiply(new BigDecimal(avgCurrencyRate)));
//                        }else if(previousYearInputDataDto.getChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.RU_KZT_6_REPORT_LINE_NUMBER_2_7_CODE) &&
//                                previousYearInputDataDto.getAccountBalance() != null){
//                            total = total.add(new BigDecimal(previousYearInputDataDto.getAccountBalance()).multiply(new BigDecimal(currencyRatesDto.getValue())));
//                            total = total.subtract(new BigDecimal(previousYearInputDataDto.getAccountBalance()).multiply(new BigDecimal(avgCurrencyRate)));
//                        }
//                    }
//                }
            }
        }

        List<ConsolidatedBalanceFormRecordDto> incomeExpenseRecords = generateConsolidatedIncomeExpenseUSDForm(reportId);
        for(int i = incomeExpenseRecords.size() - 1; i >= 0; i--){
            if(incomeExpenseRecords.get(i).getLineNumber() != null && incomeExpenseRecords.get(i).getLineNumber() == 21 && incomeExpenseRecords.get(i).getCurrentAccountBalance() != null){
                total = MathUtils.add(total, MathUtils.multiply(10, incomeExpenseRecords.get(i).getCurrentAccountBalance(), currencyRatesDto.getValue()));
                total = MathUtils.subtract(total, MathUtils.multiply(10, incomeExpenseRecords.get(i).getCurrentAccountBalance(), avgCurrencyRateForReportDate));
                break;
            }
        }

        ListResponseDto balanceUSDResponseDto = generateConsolidatedBalanceUSDForm(reportId);
        if(balanceUSDResponseDto.getStatus() == ResponseStatusType.FAIL){
            logger.error("USD report generation failed. " + balanceUSDResponseDto.getMessage().getNameEn());
            throw new IllegalStateException("USD report generation failed. " + balanceUSDResponseDto.getMessage().getNameEn());
        }
        List<ConsolidatedBalanceFormRecordDto> balanceRecords = balanceUSDResponseDto.getRecords();
        for(int i = balanceRecords.size() - 1; i >= 0; i--){
            if(balanceRecords.get(i).getCurrentAccountBalance() != null && balanceRecords.get(i).getLineNumber() != null && balanceRecords.get(i).getLineNumber() == 49 &&
                    balanceRecords.get(i).getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010) &&
                    balanceRecords.get(i).getName().equalsIgnoreCase(PeriodicReportConstants.RU_5440_010_a)){
                total = MathUtils.add(total, MathUtils.multiply(10, balanceRecords.get(i).getCurrentAccountBalance(), currencyRatesDto.getValue()));
                total = MathUtils.subtract(total, MathUtils.multiply(10, balanceRecords.get(i).getCurrentAccountBalance(), avgCurrencyRateForReportDate));
                break;
            }
        }

        return total;
    }

    @Override
    public ListResponseDto generateConsolidatedIncomeExpenseKZTForm2(Long reportId) {
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedBalanceKZTForm2Saved(reportId);
            responseDto.setRecords(records);
            return responseDto;
        }else{
            List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedIncomeExpenseKZTForm2LineHeaders();
            Double record6113_030 = 0.0;
            Double record6280_010 = 0.0;
            Double record7470_010 = 0.0;
            Double record6150_010 = 0.0;
            Double record7330_010 = 0.0;
            Double record6150_030 = 0.0;
            Double record7330_030 = 0.0;
            Double record6283_080 = 0.0;
            Double record7313_010 = 0.0;
            Double record7473_080 = 0.0;
            ListResponseDto KZTForm19ResponseDto = generateConsolidatedBalanceKZTForm19(reportId);
            if(KZTForm19ResponseDto.getStatus() == ResponseStatusType.FAIL){
                //responseDto.setErrorMessageEn("Error generating KZT Form 19 report. " + KZTForm19ResponseDto.getMessage().getNameEn());
                //return responseDto;
                responseDto.appendErrorMessageEn("Error generating KZT Form 19 report. " + KZTForm19ResponseDto.getMessage().getNameEn());
            }

            List<ConsolidatedKZTForm19RecordDto> form19Records = KZTForm19ResponseDto.getRecords();
            if(form19Records != null){
                for(ConsolidatedKZTForm19RecordDto record: form19Records){
                    if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6113_030)){
                        record6113_030 = MathUtils.add(record6113_030, record.getCurrentAccountBalance());
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6280_010)){
                        record6280_010 = MathUtils.add(record6280_010, record.getCurrentAccountBalance());
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7470_010)){
                        record7470_010 = MathUtils.add(record7470_010, record.getCurrentAccountBalance());
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_010)){
                        record6150_010 = MathUtils.add(record6150_010, record.getCurrentAccountBalance());
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_010)){
                        record7330_010 = MathUtils.add(record7330_010, record.getCurrentAccountBalance());
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030)){
                        record6150_030 = MathUtils.add(record6150_030, record.getCurrentAccountBalance());
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030)){
                        record7330_030 = MathUtils.add(record7330_030, record.getCurrentAccountBalance());
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010)){
                        record7313_010 = MathUtils.add(record7313_010, record.getCurrentAccountBalance());
                    }
                }
            }

            ListResponseDto KZTForm22ResponseDto = generateConsolidatedBalanceKZTForm22(reportId);
            if(KZTForm22ResponseDto.getStatus() == ResponseStatusType.FAIL){
                //responseDto.setErrorMessageEn("Error generating KZT Form 22 report. " + KZTForm22ResponseDto.getMessage().getNameEn());
                //return responseDto;
                responseDto.appendErrorMessageEn("Error generating KZT Form 22 report. " + KZTForm22ResponseDto.getMessage().getNameEn());
            }
            List<ConsolidatedKZTForm22RecordDto> form22Records = KZTForm22ResponseDto.getRecords();
            if(form22Records != null){
                for(ConsolidatedKZTForm22RecordDto record: form22Records) {
                    if (record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6283_080)) {
                        record6283_080 = MathUtils.add(record6283_080, record.getCurrentAccountBalance());
                    } else if (record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7473_080)) {
                        record7473_080 = MathUtils.add(record7473_080, record.getCurrentAccountBalance());
                    }
                }
            }

//            double sum = MathUtils.add(record6113_030, record6280_010, record7470_010, record6150_010, record7330_010,
//                    record6150_030, record7330_030, record6283_080, record7313_010, record7473_080).doubleValue();
            Map<Integer, Double> sums = new HashMap<>();
            for(ConsolidatedBalanceFormRecordDto record: records){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6113_030) &&
                        record.getLineNumber() != null && record.getLineNumber() == 5){
                    record.setCurrentAccountBalance(record6113_030.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6280_010) &&
                        record.getLineNumber() != null && record.getLineNumber() == 7){
                    record.setCurrentAccountBalance(record6280_010.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7470_010) &&
                        record.getLineNumber() != null && record.getLineNumber() == 7){
                    record.setCurrentAccountBalance(record7470_010.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_010) &&
                        record.getLineNumber() != null && record.getLineNumber() == 8){
                    record.setCurrentAccountBalance(record6150_010.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_010) &&
                        record.getLineNumber() != null && record.getLineNumber() == 8){
                    record.setCurrentAccountBalance(record7330_010.doubleValue());
//                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030) &&
//                        record.getLineNumber() != null && record.getLineNumber() == 9){
//                    record.setCurrentAccountBalance(record6150_030.doubleValue());
//                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030) &&
//                        record.getLineNumber() != null && record.getLineNumber() == 9){
//                    record.setCurrentAccountBalance(record7330_030.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6283_080) &&
                        record.getLineNumber() != null && record.getLineNumber() == 11){
                    record.setCurrentAccountBalance(record6283_080.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010) &&
                        record.getLineNumber() != null && record.getLineNumber() == 13){
                    record.setCurrentAccountBalance(record7313_010.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7473_080) &&
                        record.getLineNumber() != null && record.getLineNumber() == 16){
                    record.setCurrentAccountBalance(record7473_080.doubleValue());
//                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 9){
//                    record.setCurrentAccountBalance(MathUtils.add(record6150_030, record7330_030).doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 5){
                    record.setCurrentAccountBalance(record6113_030.doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 7){
                    record.setCurrentAccountBalance(MathUtils.add(record6280_010, record7470_010));
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 8){
                    record.setCurrentAccountBalance(MathUtils.add(record6150_010, record7330_010));
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 11){
                    record.setCurrentAccountBalance(record6283_080.doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 13){
                    record.setCurrentAccountBalance(record7313_010.doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 16){
                    record.setCurrentAccountBalance(record7473_080.doubleValue());
//                }else if(record.getAccountNumber() == null && record.getLineNumber() != null &&
//                        (record.getLineNumber() == 17 || record.getLineNumber() == 19 || record.getLineNumber() == 21)){
//                    record.setCurrentAccountBalance(sum);
                }
                if(record.getAccountNumber() != null && record.getLineNumber() != null) {
                    Double value = sums.get(record.getLineNumber());
                    sums.put(record.getLineNumber(), MathUtils.add(value, record.getCurrentAccountBalance()));
                }
            }
            // Line number sums
            for(ConsolidatedBalanceFormRecordDto record: records){
                if(record.getAccountNumber() == null && record.getLineNumber() == 3){
                    record.setCurrentAccountBalance(sums.get(3));
                    sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
                }else if(record.getAccountNumber() == null && record.getLineNumber() == 17){
                    record.setCurrentAccountBalance(getSumFromMap(sums, 3, 16));
                    sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
                }else if(record.getAccountNumber() == null && record.getLineNumber() == 19){
                    record.setCurrentAccountBalance(getSumFromMap(sums, 17, 18));
                    sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
                }else if(record.getAccountNumber() == null && record.getLineNumber() == 21){
                    record.setCurrentAccountBalance(getSumFromMap(sums, 19, 20));
                    sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
                }
            }

            List<ConsolidatedBalanceFormRecordDto> toAddRecords = new ArrayList<>();
            List<Integer> toAddIndices = new ArrayList<>();

            Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if (previousReport != null) {
                List<ConsolidatedBalanceFormRecordDto> previousRecords = getConsolidatedBalanceKZTForm2Saved(previousReport.getId());
                if(previousRecords != null){
                    for(ConsolidatedBalanceFormRecordDto previousRecord: previousRecords){
                        for(int i = 0; i < records.size(); i++){
                            ConsolidatedBalanceFormRecordDto currentRecord = records.get(i);
                            if(isMatchingRecords(currentRecord, previousRecord)){
                                if(!DateUtils.isJanuary(report.getReportDate())) {
                                    currentRecord.setPreviousAccountBalance(previousRecord.getCurrentAccountBalance());
                                }else{
                                    currentRecord.setPreviousAccountBalance(null);
                                }
                                break;
                            }

                            //next line number, means record was not found, possibly  need to add
                            if(previousRecord.getLineNumber() + 1 == (currentRecord.getLineNumber())){
                                toAddIndices.add(i);
                                toAddRecords.add(previousRecord);
                                break;
                            }
                        }
                    }

                    int added = 0;
                    for(int i = 0; i < toAddRecords.size(); i++) {
                        ConsolidatedBalanceFormRecordDto recordToAdd = toAddRecords.get(i);
                        if(recordToAdd.getCurrentAccountBalance() != null && recordToAdd.getCurrentAccountBalance() != 0) {
                            if(!DateUtils.isJanuary(report.getReportDate())) {
                                recordToAdd.setPreviousAccountBalance(recordToAdd.getCurrentAccountBalance());
                            }else{
                                recordToAdd.setPreviousAccountBalance(null);
                            }
                            recordToAdd.setCurrentAccountBalance(null);
                            records.add(toAddIndices.get(i) + added, recordToAdd);
                            added++;
                        }
                    }
                }
            }
            List<ConsolidatedBalanceFormRecordDto> newRecords = new ArrayList<>();
            for(ConsolidatedBalanceFormRecordDto record: records){
                if(record.getAccountNumber() != null){
                    if((record.getCurrentAccountBalance() == null || record.getCurrentAccountBalance().doubleValue() == 0.0) &&
                            (record.getPreviousAccountBalance() == null || record.getPreviousAccountBalance().doubleValue() == 0.0)){
                        // skip;
                        continue;
                    }
                }
                newRecords.add(record);
            }

            responseDto.setRecords(newRecords);
            if(responseDto.getStatus() == null) {
                responseDto.setStatus(ResponseStatusType.SUCCESS);
            }
            return responseDto;
        }
    }

    @Override
    public ListResponseDto generateConsolidatedTotalIncomeKZTForm3(Long reportId){
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedBalanceKZTForm3Saved(reportId);
            responseDto.setRecords(records);
            return responseDto;
        }else{
            ListResponseDto KZTForm3CurrentResponseDto = getConsolidatedTotalIncomeKZTForm3Current(reportId);
            if(KZTForm3CurrentResponseDto.getStatus() == ResponseStatusType.FAIL){
                responseDto.appendErrorMessageEn(KZTForm3CurrentResponseDto.getErrorMessageEn());
                //return KZTForm3CurrentResponseDto;
            }
            List<ConsolidatedBalanceFormRecordDto> currentRecords = KZTForm3CurrentResponseDto.getRecords();

            if(currentRecords != null) {
                Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
                PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
                if (previousReport != null) {
                    List<ConsolidatedBalanceFormRecordDto> previousRecords = getConsolidatedBalanceKZTForm3Saved(previousReport.getId());
                    if (previousRecords != null) {
                        Double added = 0.0;
                        for (ConsolidatedBalanceFormRecordDto currentRecord : currentRecords) {
                            for (ConsolidatedBalanceFormRecordDto prevRecord : previousRecords) {
                                if(isMatchingRecords(currentRecord, prevRecord)){
                                    if(!DateUtils.isJanuary(report.getReportDate())) {
                                        currentRecord.setPreviousAccountBalance(prevRecord.getCurrentAccountBalance());
                                    }else{
                                        currentRecord.setPreviousAccountBalance(null);
                                    }
                                    if(currentRecord.getLineNumber() != null && currentRecord.getLineNumber() == 6){
                                        currentRecord.setCurrentAccountBalance(MathUtils.add(currentRecord.getCurrentAccountBalance(), currentRecord.getPreviousAccountBalance()));
                                        added = currentRecord.getPreviousAccountBalance();
                                    }
                                    break;
                                }
                            }
                        }

                        for (ConsolidatedBalanceFormRecordDto currentRecord : currentRecords) {
                            if(currentRecord.getLineNumber() != null && (currentRecord.getLineNumber() == 7 || currentRecord.getLineNumber() == 12 ||
                                    currentRecord.getLineNumber() == 13 )){
                                currentRecord.setCurrentAccountBalance(MathUtils.add(currentRecord.getCurrentAccountBalance(), added));
                            }
                        }
                    }
                }
            }
            responseDto.setRecords(currentRecords);
            if(responseDto.getStatus() == null){
                responseDto.setStatus(ResponseStatusType.SUCCESS);
            }
            return responseDto;
        }
    }


    private ListResponseDto getConsolidatedTotalIncomeKZTForm3Current(Long reportId) {

        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedTotalIncomeKZTForm3LineHeaders();

        Double record1 = 0.0;

        ListResponseDto KZTForm2ResponseDto = generateConsolidatedIncomeExpenseKZTForm2(reportId);
        if(KZTForm2ResponseDto.getStatus() == ResponseStatusType.FAIL){
            //responseDto.setErrorMessageEn("Error generating KZT Form2 report. " + KZTForm2ResponseDto.getMessage().getNameEn());
            //return responseDto;
            responseDto.appendErrorMessageEn("Error generating KZT Form2 report: " + KZTForm2ResponseDto.getErrorMessageEn());
        }
        List<ConsolidatedBalanceFormRecordDto> form2Records =  KZTForm2ResponseDto.getRecords() != null ?
                KZTForm2ResponseDto.getRecords() : new ArrayList<>();
        if(form2Records != null){
            for(int i = form2Records.size() - 1; i >= 0; i--){
                ConsolidatedBalanceFormRecordDto record = form2Records.get(i);
                if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 21){
                    record1 = record.getCurrentAccountBalance();
                }
            }
        }

        Double record4 = 0.0;
        Double record6 = 0.0;

        ListResponseDto responseDtoKZTForm1 = generateConsolidatedBalanceKZTForm1(reportId);
        if(responseDtoKZTForm1.getStatus() == ResponseStatusType.FAIL){
            //responseDto.setErrorMessageEn("Error generating KZT Form1 report. " + responseDtoKZTForm1.getMessage().getNameEn());
            //return responseDto;
            responseDto.appendErrorMessageEn("Error generating KZT Form1 report: " + responseDtoKZTForm1.getErrorMessageEn());
        }
        List<ConsolidatedBalanceFormRecordDto> form1Records = responseDtoKZTForm1.getRecords() != null ?
                responseDtoKZTForm1.getRecords() : new ArrayList<>();
        if(form1Records != null){
            for(int i = form1Records.size() - 1; i >= 0; i--){
                ConsolidatedBalanceFormRecordDto record = form1Records.get(i);
                if(record.getLineNumber() != null && record.getLineNumber() == 51 &&
                        record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010)){
                    List<PreviousYearInputDataDto> previousYearInputData = this.prevYearInputService.getPreviousYearInputData(reportId);
                    if(previousYearInputData != null){
                        for(PreviousYearInputDataDto previousYearInputDataDto: previousYearInputData){
                            if(previousYearInputDataDto.getAccountBalanceKZT() != null && previousYearInputDataDto.getChartOfAccounts() != null &&
                                    previousYearInputDataDto.getChartOfAccounts().getNBChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010)) {
                                record4 = MathUtils.subtract(record.getCurrentAccountBalance(), previousYearInputDataDto.getAccountBalanceKZT());
                            }
                        }
                    }
                }else if(record.getLineNumber() != null && record.getLineNumber() == 51 &&
                        record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5450_010)){
                    record6 = MathUtils.subtract(record.getCurrentAccountBalance(), record.getPreviousAccountBalance());
                }
            }
        }

        // Corrections
        List<PreviousYearInputDataDto> previousYearInputList = this.prevYearInputService.getPreviousYearInputData(reportId);
        if(previousYearInputList != null){
            for(PreviousYearInputDataDto previousYearInputDataDto: previousYearInputList){
                if(previousYearInputDataDto.getChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.RU_KZT_3_REPORT_LINE_NUMBER_5_1_CODE) &&
                        previousYearInputDataDto.getAccountBalanceKZT() != null && previousYearInputDataDto.getAccountBalanceKZT().doubleValue() != 0.0){
                    record6 = MathUtils.add(record6, previousYearInputDataDto.getAccountBalanceKZT());
                }
            }
        }

        Map<Integer, Double> sums = new HashMap<>();
        for(ConsolidatedBalanceFormRecordDto record: records){
            if(record.getLineNumber() != null && record.getLineNumber() == 1){
                record.setCurrentAccountBalance(record1);
            }else if(record.getLineNumber() != null && record.getLineNumber() == 4){
                record.setCurrentAccountBalance(record4);
            }else if(record.getLineNumber() != null && record.getLineNumber() == 6){
                record.setCurrentAccountBalance(record6);
//            }else if(record.getLineNumber() != null && record.getLineNumber() == 7){
//                record.setCurrentAccountBalance(MathUtils.add(record4, record6));
//            }else if(record.getLineNumber() != null && record.getLineNumber() == 12){
//                record.setCurrentAccountBalance(MathUtils.add(record4, record6));
//            }else if(record.getLineNumber() != null && record.getLineNumber() == 13){
//                record.setCurrentAccountBalance(MathUtils.add(record1, MathUtils.add(record4, record6)));
            }
            if(record.getLineNumber() != null) {
                Double value = sums.get(record.getLineNumber());
                sums.put(record.getLineNumber(), MathUtils.add(value, record.getCurrentAccountBalance()));
            }
        }
        for(ConsolidatedBalanceFormRecordDto record: records){
            if(record.getAccountNumber() == null && record.getLineNumber() == 7){
                record.setCurrentAccountBalance(getSumFromMap(sums, 4, 6));
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }else if(record.getAccountNumber() == null && record.getLineNumber() == 11){
                record.setCurrentAccountBalance(getSumFromMap(sums, 9, 10));
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }else if(record.getAccountNumber() == null && record.getLineNumber() == 12){
                record.setCurrentAccountBalance(MathUtils.add(sums.get(7), sums.get(11)));
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }else if(record.getAccountNumber() == null && record.getLineNumber() == 13){
                record.setCurrentAccountBalance(MathUtils.add(sums.get(1), sums.get(12)));
                sums.put(record.getLineNumber(), record.getCurrentAccountBalance());
            }
        }

        responseDto.setRecords(records);
        return responseDto; }

    @Override
    public ListResponseDto generateConsolidatedBalanceKZTForm6(Long reportId) {

        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedKZTForm6RecordDto> records = getConsolidatedBalanceKZTForm6Saved(report.getId());
            responseDto.setRecords(records);
            return responseDto;
        }else{
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            List<ConsolidatedKZTForm6RecordDto> previousRecords = previousReport != null ?
                    getConsolidatedBalanceKZTForm6Saved(previousReport.getId()) : new ArrayList<>();
            // KZT 3 records
            Double formKZT3LineNumber1Difference = 0.0;
            Double formKZT3LineNumber7Difference = 0.0;

            ListResponseDto KZTForm3ResponseDto = generateConsolidatedTotalIncomeKZTForm3(reportId);
            if(KZTForm3ResponseDto.getStatus() == ResponseStatusType.FAIL){
                responseDto.appendErrorMessageEn("Error generating KZT Form3 report: " + KZTForm3ResponseDto.getErrorMessageEn());
            }
            List<ConsolidatedBalanceFormRecordDto> formKZT3Records = KZTForm3ResponseDto.getRecords();
            if(formKZT3Records != null){
                for(ConsolidatedBalanceFormRecordDto form3Record: formKZT3Records){
                    // 1
                    if(form3Record.getLineNumber() != null && form3Record.getLineNumber() == 1){
                        formKZT3LineNumber1Difference = MathUtils.subtract(form3Record.getCurrentAccountBalance(), form3Record.getPreviousAccountBalance());
                    }else if(form3Record.getLineNumber() != null && form3Record.getLineNumber() == 7){
                        formKZT3LineNumber7Difference = MathUtils.subtract(form3Record.getCurrentAccountBalance(), form3Record.getPreviousAccountBalance());
                    }
                }
            }

            // Previous period
            ConsolidatedKZTForm6RecordDto previousPeriodRecord = null;
            for(int i = previousRecords.size() - 1; i >= 0; i--){
                ConsolidatedKZTForm6RecordDto record = previousRecords.get(i);
                if(record.getLineNumber() != null && record.getLineNumber() == 15){
                    previousPeriodRecord = new ConsolidatedKZTForm6RecordDto(record);
                    previousPeriodRecord.setName(null);
                    break;
                }
            }

            Double reservesSum = null;
            try {
                reservesSum = this.reserveCalculationService.getReserveCalculationSumKZTForMonth(ReserveCalculationsExpenseTypeLookup.ADD.getCode(), report.getReportDate());
                Double reservesSumNICKMFExp = this.reserveCalculationService.getReserveCalculationSumKZTForMonth(ReserveCalculationsExpenseTypeLookup.NICK_MF_EXPENSES.getCode(), report.getReportDate());
                reservesSum = MathUtils.add(reservesSum, reservesSumNICKMFExp);
            }catch (IllegalStateException ex){
                responseDto.setErrorMessageEn("Error generating KZT Form3 report. " + ex.getMessage());
                return responseDto;
            }

            Double lineNumber2Column7Value = null;
            Double lineNumber2Column8Value = null;
            List<PreviousYearInputDataDto> previousYearInputList = this.prevYearInputService.getPreviousYearInputData(reportId);
            if(previousYearInputList != null){
                for(PreviousYearInputDataDto previousYearInputDataDto: previousYearInputList){
                    if(previousYearInputDataDto.getChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.RU_KZT_6_REPORT_LINE_NUMBER_2_7_CODE) &&
                            previousYearInputDataDto.getAccountBalanceKZT() != null && previousYearInputDataDto.getAccountBalanceKZT().doubleValue() != 0.0){
                        lineNumber2Column7Value = previousYearInputDataDto.getAccountBalanceKZT();
                    }else if(previousYearInputDataDto.getChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.RU_KZT_6_REPORT_LINE_NUMBER_2_8_CODE) &&
                            previousYearInputDataDto.getAccountBalanceKZT() != null && previousYearInputDataDto.getAccountBalanceKZT().doubleValue() != 0.0){
                        lineNumber2Column8Value = previousYearInputDataDto.getAccountBalanceKZT();
                    }
                }
            }

            ConsolidatedKZTForm6RecordDto sumLineNumber3Record = new ConsolidatedKZTForm6RecordDto();
            ConsolidatedKZTForm6RecordDto sumLineNumber6Record = new ConsolidatedKZTForm6RecordDto();
            ConsolidatedKZTForm6RecordDto sumLineNumber14Record = new ConsolidatedKZTForm6RecordDto();
            List<ConsolidatedKZTForm6RecordDto> records = getConsolidatedBalanceKZTForm6LineHeaders();
            for(ConsolidatedKZTForm6RecordDto record: records) {
                if(record.getLineNumber() != null && record.getLineNumber() == 1 && previousPeriodRecord != null){
                    record.setShareholderEquity(previousPeriodRecord.getShareholderEquity());
                    record.setAdditionalPaidinCapital(previousPeriodRecord.getAdditionalPaidinCapital());
                    record.setRedeemedOwnEquityInstruments(previousPeriodRecord.getRedeemedOwnEquityInstruments());
                    record.setReserveCapital(previousPeriodRecord.getReserveCapital());
                    record.setOtherReserves(previousPeriodRecord.getOtherReserves());
                    record.setRetainedEarnings(previousPeriodRecord.getRetainedEarnings());
                    record.setTotal(previousPeriodRecord.getTotal());

                    sumLineNumber3Record.addValues(record);
                }else if(record.getLineNumber() != null && record.getLineNumber() == 2){

                    record.setOtherReserves(lineNumber2Column7Value);
                    record.setRetainedEarnings(lineNumber2Column8Value);
                    if(record.getOtherReserves() != null || record.getRetainedEarnings() != null){
                        record.setTotal(MathUtils.add(lineNumber2Column7Value, lineNumber2Column8Value));
                    }

                    sumLineNumber3Record.addValues(record);
                }else if(record.getLineNumber() != null && record.getLineNumber() == 3){
                    record.addValues(sumLineNumber3Record);
                }else if(record.getLineNumber() != null && record.getLineNumber() == 4){
                    record.setRetainedEarnings(formKZT3LineNumber1Difference);
                    record.setTotal(formKZT3LineNumber1Difference);

                    sumLineNumber6Record.addValues(record);
                }else if(record.getLineNumber() != null && record.getLineNumber() == 5){
                    record.setOtherReserves(formKZT3LineNumber7Difference);
                    record.setTotal(formKZT3LineNumber7Difference);

                    sumLineNumber6Record.addValues(record);
                }else if(record.getLineNumber() != null && record.getLineNumber() == 6){
                    record.addValues(sumLineNumber6Record);
                }else if(record.getLineNumber() != null && record.getLineNumber() == 8){
                    record.setShareholderEquity(reservesSum);
                    record.setTotal(reservesSum);

                    sumLineNumber14Record.addValues(record);
                }else if(record.getLineNumber() != null && record.getLineNumber() == 14) {
                    record.addValues(sumLineNumber14Record);
                }else if(record.getLineNumber() != null && record.getLineNumber() == 15) {
                    record.addValues(sumLineNumber3Record);
                    record.addValues(sumLineNumber6Record);
                    record.addValues(sumLineNumber14Record);
                }

            }
            responseDto.setRecords(records);
            return responseDto;
        }
    }

    @Override
    public ListResponseDto generateConsolidatedBalanceKZTForm7(Long reportId) {

        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedKZTForm7RecordDto> records = getConsolidatedBalanceKZTForm7Saved(report.getId());
            responseDto.setRecords(records);
            return responseDto;
        }else{
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);

            List<ConsolidatedKZTForm7RecordDto> records = new ArrayList<>();
            Map<String, Integer> existingFundRecordsMap = new HashedMap();

            List<ConsolidatedKZTForm7RecordDto> previousPeriodRecords = previousReport != null ? getConsolidatedBalanceKZTForm7Saved(previousReport.getId()) : new ArrayList<>();
            //ListResponseDto KZTForm7CurrentResponseDto = getConsolidatedBalanceKZTForm7OnlyCurrentPeriod(reportId);
            ListResponseDto KZTForm7CurrentResponseDto2 = getConsolidatedBalanceKZTForm7Current(reportId);
            if(previousPeriodRecords != null && !previousPeriodRecords.isEmpty()){
                ReportingFundRenameInfoDto fundRenameInfo = getFundRenameInfo(reportId);
                if(!fundRenameInfo.isEmpty()){
                    for(ConsolidatedKZTForm7RecordDto dto: previousPeriodRecords){
                        if((dto.getLineNumber() == 3 || dto.getLineNumber() == 9 || dto.getLineNumber() == 10) &&
                                dto.getAccountNumber() != null && dto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010)){
                            for(ReportingFundRenamePairDto pairDto: fundRenameInfo.getFundRenames()){
                                if(dto.getEntityName().contains(pairDto.getPreviousFundName()) && !pairDto.isUsePreviousFundName()){
                                    // TODO: Check type
                                    String newName = dto.getEntityName().replace(pairDto.getPreviousFundName(), pairDto.getCurrentFundName());
                                    dto.setEntityName(newName);
                                }else if(dto.getEntityName().contains(pairDto.getCurrentFundName()) && pairDto.isUsePreviousFundName()){
                                    // TODO: Check type
                                    String newName = dto.getEntityName().replace(pairDto.getCurrentFundName(), pairDto.getPreviousFundName());
                                    dto.setEntityName(newName);
                                }
                            }
                        }
                    }
                }

                int index = 0;
                for(ConsolidatedKZTForm7RecordDto prevRecord: previousPeriodRecords){
                    if(prevRecord.getAccountNumber() != null && prevRecord.getLineNumber() != null &&
                            (prevRecord.getLineNumber() == 9 || prevRecord.getLineNumber() == 10) &&
                            (prevRecord.getDebtEndPeriod() == null || prevRecord.getDebtEndPeriod() == 0) &&
                            (prevRecord.getFairValueAdjustmentsEndPeriod() == null || prevRecord.getFairValueAdjustmentsEndPeriod() == 0) &&
                            (prevRecord.getTotalEndPeriod() == null || prevRecord.getTotalEndPeriod() == 0)){
                        // empty
                        continue;
                    }
                    // start
                    prevRecord.setDebtStartPeriod(prevRecord.getDebtEndPeriod());
                    prevRecord.setFairValueAdjustmentsStartPeriod(prevRecord.getFairValueAdjustmentsEndPeriod());
                    prevRecord.setTotalStartPeriod(prevRecord.getTotalEndPeriod());
                    prevRecord.setInterestStartPeriod(prevRecord.getInterestEndPeriod());
                    // end
                    prevRecord.setDebtEndPeriod(null);
                    prevRecord.setInterestEndPeriod(null);
                    prevRecord.setFairValueAdjustmentsEndPeriod(null);
                    prevRecord.setTotalEndPeriod(null);
                    prevRecord.setBecameZero(false);
                    // turnover
                    prevRecord.setDebtTurnover(MathUtils.subtract(prevRecord.getDebtEndPeriod(), prevRecord.getDebtStartPeriod()));
                    prevRecord.setInterestTurnover(MathUtils.subtract(prevRecord.getInterestEndPeriod(), prevRecord.getInterestStartPeriod()));
                    Double fairValueAdjustmentsTurnover = MathUtils.subtract(prevRecord.getFairValueAdjustmentsEndPeriod(), prevRecord.getFairValueAdjustmentsStartPeriod());
                    prevRecord.setFairValueAdjustmentsTurnoverPositive(fairValueAdjustmentsTurnover.doubleValue() >= 0.0 ? fairValueAdjustmentsTurnover : null);
                    prevRecord.setFairValueAdjustmentsTurnoverNegative(fairValueAdjustmentsTurnover.doubleValue() < 0.0 ? fairValueAdjustmentsTurnover : null);
                    records.add(prevRecord);

                    if(prevRecord.getAccountNumber() != null && (prevRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT) ||
                            prevRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT) ||
                            prevRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT))) {
                        existingFundRecordsMap.put(prevRecord.getEntityName(), index);
                    }
                    index++;
                }
            }else{
                // TODO: handle error ?
                // TODO: No previous KZT Form 7 data ?
                records = getConsolidatedBalanceKZTForm7LineHeaders();
            }

            // set index
            int indexPE = 0;
            int indexHF = 0;
            int indexRE = 0;
            int index3 = 0;
            for(int i = 0; i < records.size(); i++){
                ConsolidatedKZTForm7RecordDto record = records.get(i);
                if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT) && indexPE == 0){
                    indexPE = i;
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT) && indexRE == 0){
                    indexRE = i;
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT) && indexHF == 0){
                    indexHF = i;
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 4){
                    index3 = i;
                }
            }
            if(indexPE == 0){
                indexPE = index3;
            }
            if(indexRE == 0){
                indexRE = index3;
            }
            if(indexHF == 0){
                indexHF = index3;
            }

            if(KZTForm7CurrentResponseDto2.getStatus() == ResponseStatusType.FAIL){
                return KZTForm7CurrentResponseDto2;
            }
            List<ConsolidatedKZTForm7RecordDto> currentPeriodRecords = KZTForm7CurrentResponseDto2.getRecords();

            ConsolidatedKZTForm7RecordDto totalRecord = new ConsolidatedKZTForm7RecordDto();
            int count = 0;
            List<ConsolidatedKZTForm7RecordDto> newRecords = new ArrayList<>();
            for(ConsolidatedKZTForm7RecordDto currentRecord: currentPeriodRecords){
                if(currentRecord.getEntityName() == null){
                    break;
                }

                boolean newRecord = false;
                ConsolidatedKZTForm7RecordDto record = null;
                if(existingFundRecordsMap.get(currentRecord.getEntityName()) != null){
                    int index = existingFundRecordsMap.get(currentRecord.getEntityName());
                    record = records.get(index);
                    record.setDebtEndPeriod(MathUtils.add(record.getDebtEndPeriod(), currentRecord.getDebtEndPeriod()));
                    record.setInterestEndPeriod(MathUtils.add(record.getInterestEndPeriod(), currentRecord.getInterestEndPeriod()));
                    record.setFairValueAdjustmentsEndPeriod(MathUtils.add(record.getFairValueAdjustmentsEndPeriod(), currentRecord.getFairValueAdjustmentsEndPeriod()));
                    record.setTotalEndPeriod(MathUtils.add(record.getTotalEndPeriod(), currentRecord.getTotalEndPeriod()));
                }else{
                    // new record
                    record = currentRecord;
                    newRecord = true;
                }

                record.setDebtTurnover(MathUtils.subtract(record.getDebtEndPeriod(), record.getDebtStartPeriod()));
                record.setInterestTurnover(MathUtils.subtract(record.getInterestEndPeriod(), record.getInterestStartPeriod()));
                Double fairValueAdjustmentTurnover = MathUtils.subtract(record.getFairValueAdjustmentsEndPeriod(), record.getFairValueAdjustmentsStartPeriod());
                if(fairValueAdjustmentTurnover != null){
                    record.setFairValueAdjustmentsTurnoverPositive(fairValueAdjustmentTurnover != null && fairValueAdjustmentTurnover.doubleValue() >= 0 ? fairValueAdjustmentTurnover : null);
                    record.setFairValueAdjustmentsTurnoverNegative(fairValueAdjustmentTurnover != null && fairValueAdjustmentTurnover.doubleValue() < 0 ? fairValueAdjustmentTurnover : null);
                }
                if(newRecord){
                    newRecords.add(record);
                }
            }
            for(ConsolidatedKZTForm7RecordDto record: newRecords){
                int index = record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT) ? indexPE :
                        record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT) ? indexHF :
                                record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT) ? indexRE : 0;
                if(index > 0){
                    if(record.getPurchaseDate() == null) {
                        record.setPurchaseDate(DateUtils.getFirstDayOfCurrentMonth(report.getReportDate()));
                    }
                    records.add(index, record);
                    if(indexHF > index){
                        indexHF++;
                    }
                    if(indexPE > index){
                        indexPE++;
                    }
                    if(indexRE > index){
                        indexRE++;
                    }
                }
            }

            for(ConsolidatedKZTForm7RecordDto record: records){
                if(record.getAccountNumber() != null && record.getLineNumber() == 3){
                    addValuesKZTForm7(totalRecord, record);
                }
            }

            List<ConsolidatedKZTForm7RecordDto> nonEmptyRecords = new ArrayList();
            for(ConsolidatedKZTForm7RecordDto record: records){
                if(record.getAccountNumber() == null && (record.getLineNumber() == 1 || record.getLineNumber() == 3 || record.getLineNumber() == 14)){
                    record.setDebtTurnover(totalRecord.getDebtTurnover());
                    record.setFairValueAdjustmentsTurnoverPositive(totalRecord.getFairValueAdjustmentsTurnoverPositive());
                    record.setFairValueAdjustmentsTurnoverNegative(totalRecord.getFairValueAdjustmentsTurnoverNegative());
                    record.setInterestTurnover(totalRecord.getInterestTurnover());

                    record.setInterestEndPeriod(totalRecord.getInterestEndPeriod());
                    record.setDebtEndPeriod(totalRecord.getDebtEndPeriod());
                    record.setFairValueAdjustmentsEndPeriod(totalRecord.getFairValueAdjustmentsEndPeriod());
                    record.setTotalEndPeriod(totalRecord.getTotalEndPeriod());
                }

                if(record.getAccountNumber() != null && record.getLineNumber() != null && record.getLineNumber() == 3 &&
                        record.isEmpty()){
                    // do not add
                }else{
                    if(StringUtils.isNotEmpty(record.getAccountNumber()) && record.getLineNumber() == 3) {
                        record.setCurrency("USD");
                    }
                    nonEmptyRecords.add(record);
                }
            }
            // TODO: sort
            //Collections.sort(nonEmptyRecords);
            responseDto.setRecords(nonEmptyRecords);
            return responseDto;
        }
    }

/*    private ListResponseDto getConsolidatedBalanceKZTForm7OnlyCurrentPeriod(Long reportId) {

        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        Date rateDate = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
        // Find exchange rate
        CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, CurrencyLookup.USD.getCode());
        if(endCurrencyRatesDto == null){
            logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
            responseDto.setErrorMessageEn("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
            return responseDto;
        }

        List<ConsolidatedKZTForm7RecordDto> recordsPE = new ArrayList<>();
        List<ConsolidatedKZTForm7RecordDto> recordsHF = new ArrayList<>();
        List<ConsolidatedKZTForm7RecordDto> recordsRE = new ArrayList<>();

        Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
        PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);


        // TODO: same fund in both tranches !!

        //ReportingFundRenameInfoDto fundRenameInfo = getFundRenameInfo(reportId);
        Map<String, ReportingFundRenamePairDto> fundRenames = getFundRenameInfoMap(reportId);

        Map<String, Double> fundTurnoverPE = new HashedMap();
        Map<String, Double> fundTurnoverHF = new HashedMap();
        Map<String, Double> fundTurnoverRE = new HashedMap();

        // TARRAGON
        List<ScheduleInvestmentsDto> previousPeriodInvestments = previousReport != null ?
                this.scheduleInvestmentService.getScheduleInvestments(previousReport.getId()) : new ArrayList<>();
        if(previousPeriodInvestments != null) {
            for (ScheduleInvestmentsDto investmentsDto: previousPeriodInvestments) {
                if(investmentsDto.getNetCost() != null && investmentsDto.getNetCost().doubleValue() != 0 &&
                        (investmentsDto.getTotalSum() == null || !investmentsDto.getTotalSum().booleanValue())) {

                    // Check fund renames
                    String newName = investmentsDto.getName();
                    if(fundRenames != null && fundRenames.get(investmentsDto.getName()) != null && fundRenames.get(investmentsDto.getName()).isPrivateEquityFund()){
                        newName = fundRenames.get(investmentsDto.getName()).getCurrentFundName();
                    }
                    if (investmentsDto.isTrancheAType()) {
                        fundTurnoverPE.put(newName, MathUtils.multiply(investmentsDto.getNetCost(), 0.99));
                    } else {
                        fundTurnoverPE.put(newName, investmentsDto.getNetCost());
                    }
                }
            }
        }
        List<ScheduleInvestmentsDto> currentPeriodInvestments = this.scheduleInvestmentService.getScheduleInvestments(report.getId());
        if(currentPeriodInvestments != null) {
            for (ScheduleInvestmentsDto investmentsDto: currentPeriodInvestments) {
                if(investmentsDto.getTotalSum() == null || !investmentsDto.getTotalSum().booleanValue()) {
                    double previousValue = fundTurnoverPE.get(investmentsDto.getName()) != null ? fundTurnoverPE.get(investmentsDto.getName()).doubleValue() : 0;
                    double currentValue = investmentsDto.getNetCost() != null ? investmentsDto.getNetCost().doubleValue() : 0;
                    if (investmentsDto.isTrancheAType()) {
                        currentValue = MathUtils.multiply(currentValue, 0.99);
                    }
                    if(currentValue == 0){
                        // net cost is zero in current schedule of investments
                        fundTurnoverPE.put(investmentsDto.getName(), null);
                    }else {
                        fundTurnoverPE.put(investmentsDto.getName(), MathUtils.subtract(currentValue, previousValue));
                    }
                }
            }
        }

        // TERRA
        // Securities cost (Previous)
//        List<TerraSecuritiesCostRecordDto> prevTerraSecuritiesCostRecords = previousReport != null ?
//                this.realEstateService.getSecuritiesCostRecords(previousReport.getId()) : new ArrayList<>();
//        if(prevTerraSecuritiesCostRecords != null){
//            for(TerraSecuritiesCostRecordDto terraRecord: prevTerraSecuritiesCostRecords){
//                if(terraRecord.getTotalCostFCY() != null && terraRecord.getTotalCostFCY().doubleValue() != 0 &&
//                        (terraRecord.getTotalSum() == null || !terraRecord.getTotalSum().booleanValue())) {
//
//                    String newName = terraRecord.getName();
//                    if(fundRenames != null && fundRenames.get(terraRecord.getName()) != null && fundRenames.get(terraRecord.getName()).isRealEstateFund()){
//                        newName = fundRenames.get(terraRecord.getName()).getCurrentFundName();
//                    }
//                    fundTurnoverRE.put(newName, MathUtils.multiply(terraRecord.getTotalCostFCY(), 0.99));
//
//                }
//            }
//        }

        // Previous GL
        TerraGeneralLedgerDataHolderDto previousTerraGLHolder = previousReport != null ?
                this.realEstateService.getTerraGeneralLedgerDataWithoutExcluded(previousReport.getId()) : new TerraGeneralLedgerDataHolderDto();
        if(previousTerraGLHolder != null){
            for(TerraGeneralLedgerBalanceRecordDto terraRecord: previousTerraGLHolder.getRecords()){
                if(terraRecord.getGlSubclass() != null && terraRecord.getGlSubclass().equalsIgnoreCase("Security")) {

                    String newName = terraRecord.getPortfolioFund();
                    if(fundRenames != null && fundRenames.get(terraRecord.getPortfolioFund()) != null && fundRenames.get(terraRecord.getPortfolioFund()).isRealEstateFund()){
                        newName = fundRenames.get(terraRecord.getPortfolioFund()).getCurrentFundName();
                    }
                    fundTurnoverRE.put(newName, terraRecord.getAccountBalanceNICKMF());

                }
            }
        }


        // Securities cost (Current)
//        List<TerraSecuritiesCostRecordDto> currentTerraSecuritiesCostRecords = this.realEstateService.getSecuritiesCostRecords(report.getId());
//        if(currentTerraSecuritiesCostRecords != null) {
//            for (TerraSecuritiesCostRecordDto terraRecord: currentTerraSecuritiesCostRecords) {
//                if(terraRecord.getTotalSum() == null || !terraRecord.getTotalSum().booleanValue()) {
//                    double previousValue = fundTurnoverRE.get(terraRecord.getName()) != null ? fundTurnoverRE.get(terraRecord.getName()).doubleValue() : 0;
//                    double currentValue = terraRecord.getTotalCostFCY() != null ? terraRecord.getTotalCostFCY().doubleValue() : 0;
//                    currentValue = MathUtils.multiply(currentValue, 0.99);
//                    if(currentValue == 0){
//                        // net cost is zero in current
//                        fundTurnoverRE.put(terraRecord.getName(), null);
//                    }else {
//                        fundTurnoverRE.put(terraRecord.getName(), MathUtils.subtract(currentValue, previousValue));
//                    }
//                }
//            }
//        }

        // Current GL
        Map<String, Double> interestMap = new HashMap<>();
        TerraGeneralLedgerDataHolderDto currentTerraGLHolder = this.realEstateService.getTerraGeneralLedgerDataWithoutExcluded(reportId);
        if(currentTerraGLHolder != null){
            for(TerraGeneralLedgerBalanceRecordDto terraRecord: currentTerraGLHolder.getRecords()){
                if(terraRecord.getGlSubclass() != null && terraRecord.getGlSubclass().equalsIgnoreCase("Security")) {
                    double previousValue = fundTurnoverRE.get(terraRecord.getPortfolioFund()) != null ? fundTurnoverRE.get(terraRecord.getPortfolioFund()).doubleValue() : 0;
                    double currentValue = terraRecord.getAccountBalanceNICKMF() != null ? terraRecord.getAccountBalanceNICKMF().doubleValue() : 0;
                    if(currentValue == 0){
                        // net cost is zero in current
                        fundTurnoverRE.put(terraRecord.getPortfolioFund(), null);
                    }else {
                        fundTurnoverRE.put(terraRecord.getPortfolioFund(), MathUtils.subtract(currentValue, previousValue));
                    }
                }else if(terraRecord.getGlSubclass() != null && terraRecord.getGlSubclass().equalsIgnoreCase("Interest receivable - bonds")){
                    Double value = interestMap.get(terraRecord.getPortfolioFund());
                    interestMap.put(terraRecord.getPortfolioFund().trim(), MathUtils.add(value, terraRecord.getAccountBalanceNICKMF()));
                }
            }
        }

        // Singularity NOAL

        // current NOAL
        List<SingularityNOALRecordDto> currentNoalRecords = new ArrayList<>();
        List<SingularityNOALRecordDto> currentNoalTrancheARecords = this.hfNOALService.get(report.getId(), 1).getNoalTrancheAList();
        if(currentNoalTrancheARecords != null){
            currentNoalRecords.addAll(currentNoalTrancheARecords);
        }

        Date dateLastDayOfCurrentMonth = DateUtils.getLastDayOfCurrentMonth(report.getReportDate());
        for(SingularityNOALRecordDto noalRecordDto: currentNoalRecords){
            if(noalRecordDto.getTransaction() != null && noalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.EN_NOAL_PORTFOLIO_REDEMPTION) &&
                    noalRecordDto.getName() != null && DateUtils.getDateFormatted(noalRecordDto.getDate()).equalsIgnoreCase(DateUtils.getDateFormatted(dateLastDayOfCurrentMonth))){
                double value = fundTurnoverHF.get(noalRecordDto.getName()) != null ? fundTurnoverHF.get(noalRecordDto.getName()).doubleValue() : 0;
                value = MathUtils.subtract(value, noalRecordDto.getTransactionAmount() != null ? noalRecordDto.getTransactionAmount().doubleValue() : 0);
                fundTurnoverHF.put(noalRecordDto.getName(), value);
            }
        }


        // previous NOAL
        List<SingularityNOALRecordDto> previousNoalRecords = new ArrayList<>();
        List<SingularityNOALRecordDto> previousNoalTrancheARecords = previousReport != null ?
                this.hfNOALService.get(previousReport.getId(), 1).getNoalTrancheAList() : new ArrayList<>();
        if(previousNoalTrancheARecords != null){
            for(SingularityNOALRecordDto noalRecord: previousNoalTrancheARecords){
                if(noalRecord.getName() != null && fundRenames != null && fundRenames.get(noalRecord.getName()) != null && fundRenames.get(noalRecord.getName()).isHedgeFund()){
                    noalRecord.setName(fundRenames.get(noalRecord.getName()).getCurrentFundName());
                }
            }
            previousNoalRecords.addAll(previousNoalTrancheARecords);
        }

        Date dateLastDayOfPreviousMonth = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
        for(SingularityNOALRecordDto prevNoalRecordDto: previousNoalRecords){
            if(prevNoalRecordDto.getDate() != null && DateUtils.getDateFormatted(prevNoalRecordDto.getDate()).equalsIgnoreCase(DateUtils.getDateFormatted(dateLastDayOfPreviousMonth))){
                for(SingularityNOALRecordDto currentNoalRecordDto: currentNoalRecords){
                    if(prevNoalRecordDto.getTransaction() != null && prevNoalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.EN_NOAL_PORTFOLIO_REDEMPTION) &&
                            currentNoalRecordDto.getTransaction() != null && currentNoalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.EN_NOAL_PORTFOLIO_REDEMPTION) &&
                            prevNoalRecordDto.getName() != null && prevNoalRecordDto.getName().equalsIgnoreCase(currentNoalRecordDto.getName()) &&
                            DateUtils.getDateFormatted(currentNoalRecordDto.getDate()).equalsIgnoreCase(DateUtils.getDateFormatted(dateLastDayOfPreviousMonth))){
                        double prevValue = prevNoalRecordDto.getTransactionAmount() != null ? prevNoalRecordDto.getTransactionAmount().doubleValue() : 0;
                        double currValue = currentNoalRecordDto.getTransactionAmount() != null ? currentNoalRecordDto.getTransactionAmount().doubleValue() : 0;
                        if(prevValue != currValue){
                            double value = fundTurnoverHF.get(currentNoalRecordDto.getName()) != null ? fundTurnoverHF.get(currentNoalRecordDto.getName()).doubleValue() : 0;
                            value = MathUtils.subtract(MathUtils.add(value, prevValue), currValue);
                            fundTurnoverHF.put(currentNoalRecordDto.getName(), value);
                        }

                    }
                }
            }

        }


        //Tranche B

        // current NOAL
        List<SingularityNOALRecordDto> currentNoalRecordsTrancheB = new ArrayList<>();
        List<SingularityNOALRecordDto> currentNoalTrancheBRecords = this.hfNOALService.get(report.getId(), 2).getNoalTrancheBList();
        if(currentNoalTrancheBRecords != null){
            currentNoalRecordsTrancheB.addAll(currentNoalTrancheBRecords);
        }

        for(SingularityNOALRecordDto noalRecordDto: currentNoalRecordsTrancheB){
            if(noalRecordDto.getTransaction() != null && noalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.EN_NOAL_PORTFOLIO_REDEMPTION) &&
                    noalRecordDto.getName() != null &&DateUtils.getDateFormatted(noalRecordDto.getDate()).equalsIgnoreCase(DateUtils.getDateFormatted(dateLastDayOfCurrentMonth))){
                double value = fundTurnoverHF.get(noalRecordDto.getName()) != null ? fundTurnoverHF.get(noalRecordDto.getName()).doubleValue() : 0;
                value = MathUtils.subtract(value, noalRecordDto.getTransactionAmount() != null ? noalRecordDto.getTransactionAmount().doubleValue() : 0);
                fundTurnoverHF.put(noalRecordDto.getName(), value);
            }
        }

        // previous NOAL
        List<SingularityNOALRecordDto> previousNoalRecordsTrancheB = new ArrayList<>();
        List<SingularityNOALRecordDto> previousNoalTrancheBRecords = previousReport != null ?
                this.hfNOALService.get(previousReport.getId(), 2).getNoalTrancheAList() : new ArrayList<>();
        if(previousNoalTrancheBRecords != null){
            for(SingularityNOALRecordDto noalRecord: previousNoalTrancheBRecords){
                if(noalRecord.getName() != null && fundRenames != null && fundRenames.get(noalRecord.getName()) != null && fundRenames.get(noalRecord.getName()).isHedgeFund()){
                    noalRecord.setName(fundRenames.get(noalRecord.getName()).getCurrentFundName());
                }
            }
            previousNoalRecordsTrancheB.addAll(previousNoalTrancheBRecords);
        }

        //Date date = DateUtils.getLastDayOfCurrentMonth(previousReport.getReportDate());
        for(SingularityNOALRecordDto prevNoalRecordDto: previousNoalRecordsTrancheB){
            if(prevNoalRecordDto.getDate() != null && DateUtils.getDateFormatted(prevNoalRecordDto.getDate()).equalsIgnoreCase(DateUtils.getDateFormatted(dateLastDayOfPreviousMonth))){
                for(SingularityNOALRecordDto currentNoalRecordDto: currentNoalRecordsTrancheB){
                    if(prevNoalRecordDto.getTransaction() != null && prevNoalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.EN_NOAL_PORTFOLIO_REDEMPTION) &&
                            currentNoalRecordDto.getTransaction() != null && currentNoalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.EN_NOAL_PORTFOLIO_REDEMPTION) &&
                            prevNoalRecordDto.getName() != null && prevNoalRecordDto.getName().equalsIgnoreCase(currentNoalRecordDto.getName()) &&
                            DateUtils.getDateFormatted(currentNoalRecordDto.getDate()).equalsIgnoreCase(DateUtils.getDateFormatted(dateLastDayOfPreviousMonth))){
                        double prevValue = prevNoalRecordDto.getTransactionAmount() != null ? prevNoalRecordDto.getTransactionAmount().doubleValue() : 0;
                        double currValue = currentNoalRecordDto.getTransactionAmount() != null ? currentNoalRecordDto.getTransactionAmount().doubleValue() : 0;
                        if(prevValue != currValue){
                            double value = fundTurnoverHF.get(currentNoalRecordDto.getName()) != null ? fundTurnoverHF.get(currentNoalRecordDto.getName()).doubleValue() : 0;
                            value = MathUtils.subtract(MathUtils.add(value, prevValue), currValue);
                            fundTurnoverHF.put(currentNoalRecordDto.getName(), value);
                        }

                    }
                }
            }

        }

        // Singularity - General Ledger

        ConsolidatedReportRecordHolderDto currentDataHolder = this.generalLedgerBalanceService.getWithoutExcludedRecords(reportId);
        if(currentDataHolder != null && currentDataHolder.getGeneralLedgerBalanceList() != null){
            for(SingularityGeneralLedgerBalanceRecordDto record: currentDataHolder.getGeneralLedgerBalanceList()){
                if(record.getFinancialStatementCategoryDescription() != null && record.getFinancialStatementCategoryDescription().equalsIgnoreCase(PeriodicReportConstants.EN_NET_REALIZED_GAIN_LOSS)){
                    //String fundName = record.getChartAccountsLongDescription().substring("Net Realized Gains/Losses from Portfolio Funds".length()).trim();
                    String fundName = record.getShortName();

                    double value = fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0;
                    value = MathUtils.subtract(value, (record.getGLAccountBalance() != null ? record.getGLAccountBalance().doubleValue() : 0));
                    fundTurnoverHF.put(fundName, value);
                }
                if(record.getAdjustedRedemption() != null){
                    String fundName = record.getShortName();
                    double value = fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0;
                    value = MathUtils.subtract(value, (record.getAdjustedRedemption() != null ? record.getAdjustedRedemption().doubleValue() : 0));
                    fundTurnoverHF.put(fundName, value);
                }
            }
        }

        if(!DateUtils.isJanuary(report.getReportDate())){
            ConsolidatedReportRecordHolderDto previousDataHolder = previousReport != null ?
                    this.generalLedgerBalanceService.getWithoutExcludedRecords(previousReport.getId()) : new ConsolidatedReportRecordHolderDto();
            if (previousDataHolder != null && previousDataHolder.getGeneralLedgerBalanceList() != null) {
                for (SingularityGeneralLedgerBalanceRecordDto record : previousDataHolder.getGeneralLedgerBalanceList()) {
                    if (record.getFinancialStatementCategoryDescription() != null && record.getFinancialStatementCategoryDescription().equalsIgnoreCase(PeriodicReportConstants.EN_NET_REALIZED_GAIN_LOSS)) {
//                    String fundName = record.getChartAccountsLongDescription().substring("Net Realized Gains/Losses from Portfolio Funds".length()).trim();
                        String fundName = record.getShortName() != null ? record.getShortName() : "";

                        // fund rename
                        if(fundRenames != null && !fundRenames.isEmpty()){
                            if(fundRenames.get(fundName) != null && fundRenames.get(fundName).isHedgeFund()){
                                fundName = fundRenames.get(fundName).getCurrentFundName();
                            }
                        }

                        double value = fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0;
                        value = MathUtils.add(value, (record.getGLAccountBalance() != null ? record.getGLAccountBalance().doubleValue() : 0));
                        fundTurnoverHF.put(fundName, value);
                    }
                }
            }
        }


        ListResponseDto balanceUSDResponseDto = generateConsolidatedBalanceUSDForm(report.getId());
        if(balanceUSDResponseDto.getStatus() == ResponseStatusType.FAIL){
            return balanceUSDResponseDto;
        }
        List<ConsolidatedBalanceFormRecordDto> USDFormRecords = balanceUSDResponseDto.getRecords();
        if(USDFormRecords != null){
            for(ConsolidatedBalanceFormRecordDto recordUSD: USDFormRecords){
                if(recordUSD.getAccountNumber() != null && recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1283_020)
                        && recordUSD.getOtherEntityName() != null && recordUSD.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)){
                    if (recordUSD.getName().startsWith(PeriodicReportConstants.RU_PRE_SUBSCRIPTION)) {
                        String fundName = recordUSD.getName().substring(PeriodicReportConstants.RU_PRE_SUBSCRIPTION.length()).trim();

                        double value = fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0;
                        value = MathUtils.add(value, recordUSD.getPreviousAccountBalance());

                        fundTurnoverHF.put(fundName, value);
                    }
                //}else if(recordUSD.getAccountNumber() != null && recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2033_010)){
                }else if(recordUSD.getAccountNumber() != null && recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010)){
                    String name = null;
                    if(recordUSD.getOtherEntityName() != null && recordUSD.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)){
                        name = PeriodicReportConstants.RU_PE_FUND_INVESTMENT;
                    }else if(recordUSD.getOtherEntityName() != null && recordUSD.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)){
                        name = PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT;
                    }else if(recordUSD.getOtherEntityName() != null && recordUSD.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)){
                        name = PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT;
                    }else{

                        logger.error("USD Report: Entity name unexpected for '" + recordUSD.getName() + "' : " + recordUSD.getOtherEntityName() + ". Expected starting with : " +
                                PeriodicReportConstants.TARRAGON_CAPITAL_CASE + ", or " + PeriodicReportConstants.TARRAGON_LOWER_CASE + ", or " +
                                PeriodicReportConstants.TERRA_CAPITAL_CASE + ", or " + PeriodicReportConstants.TERRA_LOWER_CASE + ", or " +
                                PeriodicReportConstants.SINGULAR_CAPITAL_CASE + ", or " + PeriodicReportConstants.SINGULAR_LOWER_CASE);
                        responseDto.setErrorMessageEn("USD Report: Entity name unexpected for '" + recordUSD.getName() + "' : " + recordUSD.getOtherEntityName() + ". Expected starting with : " +
                                PeriodicReportConstants.TARRAGON_CAPITAL_CASE + ", or " + PeriodicReportConstants.TARRAGON_LOWER_CASE + ", or " +
                                PeriodicReportConstants.TERRA_CAPITAL_CASE + ", or " + PeriodicReportConstants.TERRA_LOWER_CASE + ", or " +
                                PeriodicReportConstants.SINGULAR_CAPITAL_CASE + ", or " + PeriodicReportConstants.SINGULAR_LOWER_CASE);
                        return responseDto;
                    }
                    int fundNameIndex = name.equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT) ? PeriodicReportConstants.RU_PE_FUND_INVESTMENT.length() :
                            name.equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT) ? PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT.length() :
                                    PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT.length();
                    String fundName = recordUSD.getName().substring(fundNameIndex).trim();

                    ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
                    if(recordUSD.getPreviousAccountBalance() != null && recordUSD.getPreviousAccountBalance().doubleValue() != 0 &&
                            (recordUSD.getCurrentAccountBalance() == null || recordUSD.getCurrentAccountBalance().doubleValue() == 0)){
                        newRecord.setBecameZero(true);
                    }

                    newRecord.setName(name);
                    newRecord.setAccountNumber(recordUSD.getAccountNumber());
                    newRecord.setLineNumber(3);
                    newRecord.setEntityName(fundName);
                    newRecord.setOtherName(recordUSD.getOtherEntityName());
                    newRecord.setCurrency("USD");
                    //newRecord.setPurchaseDate();

                    if(recordUSD.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
                        if(fundTurnoverPE.get(fundName) == null) {
                            // case when schedule investment record current period is zero
                            newRecord.setDebtTurnover(null);
                            newRecord.setBecameZero(true);
                        }else{
                            newRecord.setDebtTurnover(MathUtils.multiply(fundTurnoverPE.get(fundName), endCurrencyRatesDto.getValue()));
                            fundTurnoverPE.put(fundName, null);
                        }
                    }else if(recordUSD.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
                        if(fundTurnoverRE.get(fundName) == null) {
                            // case when schedule investment record current period is zero
                            newRecord.setDebtTurnover(null);
                            newRecord.setBecameZero(true);
                        }else{
                            newRecord.setDebtTurnover(MathUtils.multiply(fundTurnoverRE.get(fundName), endCurrencyRatesDto.getValue()));
                            fundTurnoverRE.put(fundName, null);

                            if(interestMap.get(fundName) != null){
                                newRecord.setInterestEndPeriod(MathUtils.multiply(interestMap.get(fundName), endCurrencyRatesDto.getValue()));
                            }
                        }
                    }else if(recordUSD.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)){
                        newRecord.setDebtTurnover(MathUtils.multiply(fundTurnoverHF.get(fundName), endCurrencyRatesDto.getValue()));
                        fundTurnoverHF.put(fundName, null);
                    }

                    newRecord.setTotalEndPeriod(MathUtils.multiply(recordUSD.getCurrentAccountBalance(), endCurrencyRatesDto.getValue()));
                    if(recordUSD.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)){
                        //newRecord.setTotalEndPeriod(MathUtils.add(newRecord.getTotalEndPeriod(), newRecord.getInterestEndPeriod()));
                    }

                    if(newRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)) {
                        recordsPE.add(newRecord);
                    }else if(newRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)){
                        recordsHF.add( newRecord);
                    }else if(newRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT)){
                        recordsRE.add( newRecord);
                    }
                }
            }
        }

        // Add missing funds
//        Set<String> keySet = fundTurnoverPE.keySet();
//        if(keySet != null){
//            Iterator<String> iterator = keySet.iterator();
//            while(iterator.hasNext()){
//                String fundName = iterator.next();
//                if(fundTurnoverPE.get(fundName) != null) {
//                    String name = PeriodicReportConstants.RU_PE_FUND_INVESTMENT;
//                    ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
//                    newRecord.setName(name);
//                    newRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_2033_010);
//                    newRecord.setLineNumber(9);
//                    newRecord.setEntityName(fundName);
//                    newRecord.setOtherName(PeriodicReportConstants.TARRAGON_CAPITAL_CASE);
//                    //newRecord.setPurchaseDate();
//
//                    newRecord.setTotalEndPeriod(0.0);
//                    newRecord.setDebtTurnover(MathUtils.multiply(fundTurnoverPE.get(fundName), endCurrencyRatesDto.getValue()));
//
//                    recordsPE.add( newRecord);
//                }
//            }
//        }

        Set<String> keySetRealEstate = fundTurnoverRE.keySet();
        if(keySetRealEstate != null){
            Iterator<String> iterator = keySetRealEstate.iterator();
            while(iterator.hasNext()){
                String fundName = iterator.next();
                if(fundTurnoverRE.get(fundName) != null) {
                    String name = PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT;
                    ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
                    newRecord.setName(name);
                    newRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_2033_010);
                    newRecord.setLineNumber(3);
                    newRecord.setEntityName(fundName);
                    newRecord.setOtherName(PeriodicReportConstants.TERRA_LOWER_CASE);
                    //newRecord.setPurchaseDate();

                    newRecord.setTotalEndPeriod(0.0);
                    newRecord.setDebtTurnover(MathUtils.multiply(fundTurnoverRE.get(fundName), endCurrencyRatesDto.getValue()));

                    recordsRE.add(newRecord);
                }
            }
        }
//
//        Set<String> keySet2 = fundTurnoverHF.keySet();
//        if(keySet != null){
//            Iterator<String> iterator = keySet2.iterator();
//            while(iterator.hasNext()){
//                String fundName = iterator.next();
//                if(fundTurnoverHF.get(fundName) != null) {
//                    String name = PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT;
//                    ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
//                    newRecord.setName(name);
//                    newRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_2033_010);
//                    newRecord.setLineNumber(9);
//                    newRecord.setEntityName(fundName);
//                    newRecord.setOtherName(PeriodicReportConstants.SINGULAR_CAPITAL_CASE);
//                    //newRecord.setPurchaseDate();
//
//                    newRecord.setTotalEndPeriod(0.0);
//                    newRecord.setDebtTurnover(MathUtils.multiply(fundTurnoverHF.get(fundName) , endCurrencyRatesDto.getValue()));
//
//                    recordsHF.add(newRecord);
//                }
//            }
//        }

        recordsHF.addAll(recordsPE);
        recordsHF.addAll(recordsRE);

        responseDto.setRecords(recordsHF);
        return responseDto;
    }*/

    private ListResponseDto getConsolidatedBalanceKZTForm7Current(Long reportId){
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        List<ConsolidatedKZTForm7RecordDto> records = new ArrayList<>();

        Date rateDate = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
        // Find exchange rate
        CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, CurrencyLookup.USD.getCode());
        if(endCurrencyRatesDto == null){
            logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
            responseDto.setErrorMessageEn("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
            return responseDto;
        }

        ListResponseDto balanceUSDReponse = generateConsolidatedBalanceUSDForm(reportId);
        if(balanceUSDReponse == null || (balanceUSDReponse.getStatus() != null &&
                balanceUSDReponse.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode()))){
            // usd form errors
            return balanceUSDReponse;
        }
        // Tarragon - SOI Report
        ConsolidatedReportRecordHolderDto soiReportHolder = this.scheduleInvestmentService.getSOIReport(reportId);
        Map<String, Double> tarragonNetCostByFundName = new HashMap<>();
        Map<String, Double> tarragonFairValueByFundName = new HashMap<>();
        if(soiReportHolder != null && soiReportHolder.getScheduleInvestments() != null){
            for(ScheduleInvestmentsDto investment: soiReportHolder.getScheduleInvestments()){
                Double value = MathUtils.multiply(investment.getNetCost(), endCurrencyRatesDto.getValue());
                Double valueFairValue = investment.getEditedFairValue() != null ? investment.getEditedFairValue() : investment.getFairValue();
                if(investment.isTrancheAType()){
                    // Tranche A
                    value = MathUtils.multiply(0.99, value);
                    valueFairValue = MathUtils.multiply(0.99, valueFairValue);
                }
                tarragonNetCostByFundName.put(investment.getInvestment(), MathUtils.add(tarragonNetCostByFundName.get(investment.getInvestment()), value));
                tarragonFairValueByFundName.put(investment.getInvestment(), MathUtils.add(tarragonFairValueByFundName.get(investment.getInvestment()), valueFairValue));
            }
        }

        // Terra - GL
        TerraGeneralLedgerDataHolderDto terraGLHolder = this.realEstateService.getTerraGeneralLedgerDataWithoutExcluded(reportId);
        Map<String, Double> terraNetCostByFundName = new HashMap<>();
        Map<String, Double> terraUnrealizedGainsByFundName = new HashMap<>();
        Map<String, Double> terraInterestByFundName = new HashMap<>();
        if(terraGLHolder != null && terraGLHolder.getRecords() != null){
            for(TerraGeneralLedgerBalanceRecordDto terraRecord: terraGLHolder.getRecords()){
                if(terraRecord.getGlSubclass().equalsIgnoreCase(TerraNICChartAccountsLookup.SECURITY.getNameEn())){
                    Double value = MathUtils.multiply(terraRecord.getAccountBalanceNICKMF(), endCurrencyRatesDto.getValue());
                    terraNetCostByFundName.put(terraRecord.getPortfolioFund(), MathUtils.add(terraNetCostByFundName.get(terraRecord.getPortfolioFund()), value));
                }else if(terraRecord.getGlSubclass() != null && terraRecord.getGlSubclass().equalsIgnoreCase(TerraNICChartAccountsLookup.INTEREST_RECEIVABLE_BONDS.getNameEn())){
                    Double value = terraInterestByFundName.get(terraRecord.getPortfolioFund());
                    terraInterestByFundName.put(terraRecord.getPortfolioFund().trim(), MathUtils.add(value, MathUtils.multiply(terraRecord.getAccountBalanceNICKMF(), endCurrencyRatesDto.getValue())));
                }else if(terraRecord.getGlSubclass() != null && terraRecord.getGlSubclass().equalsIgnoreCase(TerraNICChartAccountsLookup.UNREALIZED_GAIN.getNameEn())){
                    Double value = terraUnrealizedGainsByFundName.get(terraRecord.getPortfolioFund());
                    terraUnrealizedGainsByFundName.put(terraRecord.getPortfolioFund().trim(), MathUtils.add(value, terraRecord.getAccountBalanceNICKMF()));
                }
            }
        }

        // Singularity - ITD
        ConsolidatedReportRecordHolderDto singularityGLHolder = this.hfITDService.getParsedData(reportId);
        Map<String, Double> singularityNetCostByFundName = new HashMap<>();
        if(singularityGLHolder != null){
            List<SingularityITDRecordDto> recordsITD = singularityGLHolder.getRecordsITDTrancheA() != null ? singularityGLHolder.getRecordsITDTrancheA() : new ArrayList<>();
            recordsITD.addAll(singularityGLHolder.getRecordsITDTrancheB() != null ? singularityGLHolder.getRecordsITDTrancheB() : new ArrayList<>());
            for (SingularityITDRecordDto recordITD : recordsITD) {
                if(recordITD.getInvestmentName().equalsIgnoreCase("TOTAL") || recordITD.getInvestmentName().equalsIgnoreCase("TOTALS")){
                    continue;
                }
                int scale = 18;
                Double value = MathUtils.multiply(scale, recordITD.getClosingBalance(),
                        MathUtils.divide(scale, recordITD.getSubscriptions(),
                                MathUtils.add(scale, recordITD.getSubscriptions(), recordITD.getProfitLoss())));
                singularityNetCostByFundName.put(recordITD.getInvestmentName(),
                        MathUtils.add(MathUtils.multiply(value, endCurrencyRatesDto.getValue()), singularityNetCostByFundName.get(recordITD.getInvestmentName())));
            }
        }

        if(balanceUSDReponse.getRecords() != null && !balanceUSDReponse.getRecords().isEmpty()){
            List<ConsolidatedBalanceFormRecordDto> recordsUSD = balanceUSDReponse.getRecords();
            for(ConsolidatedBalanceFormRecordDto recordUSD: recordsUSD){
                if(recordUSD.getLineNumber() == null || recordUSD.getLineNumber().intValue() != 4 ||
                        recordUSD.getAccountNumber() == null || !recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010) ||
                        recordUSD.getCurrentAccountBalance() == null || recordUSD.getCurrentAccountBalance().doubleValue() == 0.0){
                    continue;
                }
                ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
                newRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_1123_010);
                String name = null;
                String fundName = null;
                String entityName = null;
                if(recordUSD.getName().startsWith(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)){
                    // Hedge Funds
                    entityName = PeriodicReportConstants.SINGULARITY_LOWER_CASE;
                    name = PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT;
                    fundName = recordUSD.getName().substring(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT.length()).trim();

                    newRecord.setDebtEndPeriod(singularityNetCostByFundName.get(fundName));
                    singularityNetCostByFundName.put(fundName, null);
                }else if(recordUSD.getName().startsWith(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)){
                    // Private Equity
                    entityName = PeriodicReportConstants.TARRAGON_LOWER_CASE;
                    name = PeriodicReportConstants.RU_PE_FUND_INVESTMENT;
                    fundName = recordUSD.getName().substring(PeriodicReportConstants.RU_PE_FUND_INVESTMENT.length()).trim();

                    newRecord.setDebtEndPeriod(tarragonNetCostByFundName.get(fundName));
                    tarragonNetCostByFundName.put(fundName, null);
                }else if(recordUSD.getName().startsWith(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT)){
                    // Real Estate
                    entityName = PeriodicReportConstants.TERRA_LOWER_CASE;
                    name = PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT;
                    fundName = recordUSD.getName().substring(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT.length()).trim();

                    newRecord.setDebtEndPeriod(terraNetCostByFundName.get(fundName));
                    terraNetCostByFundName.put(fundName, null);
                    newRecord.setInterestEndPeriod(terraInterestByFundName.get(fundName));
                }else{
                    // skip
                    continue;
                }
                newRecord.setName(name);
                newRecord.setOtherName(entityName);
                newRecord.setEntityName(fundName);

                newRecord.setLineNumber(3);

                newRecord.setTotalEndPeriod(MathUtils.add(newRecord.getInterestEndPeriod(), MathUtils.multiply(recordUSD.getCurrentAccountBalance(), endCurrencyRatesDto.getValue())));
                newRecord.setFairValueAdjustmentsEndPeriod(MathUtils.subtract(newRecord.getTotalEndPeriod(), MathUtils.add(newRecord.getDebtEndPeriod(), newRecord.getInterestEndPeriod())));
                records.add(newRecord);
            }
        }

//        singularityNetCostByFundName.forEach((key, value)->{
//            if(value != null){
//                // need to add
//                ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
//                newRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_1123_010);
//                newRecord.setName(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT);
//                newRecord.setEntityName(key);
//                newRecord.setOtherName(PeriodicReportConstants.SINGULARITY_LOWER_CASE);
//                newRecord.setLineNumber(3);
//                newRecord.setDebtEndPeriod(value);
//                newRecord.setFairValueAdjustmentsEndPeriod(MathUtils.subtract(newRecord.getTotalEndPeriod(), newRecord.getDebtEndPeriod()));
//                records.add(newRecord);
//            }
//        });
        tarragonNetCostByFundName.forEach((key, value)->{
            if(value != null && value.doubleValue() != 0.0 &&
                    (tarragonFairValueByFundName.get(key) == null ||  tarragonFairValueByFundName.get(key).doubleValue() == 0.0)){
                // need to add
                ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
                newRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_1123_010);
                newRecord.setName(PeriodicReportConstants.RU_PE_FUND_INVESTMENT);
                newRecord.setEntityName(key);
                newRecord.setOtherName(PeriodicReportConstants.TARRAGON_LOWER_CASE);
                newRecord.setLineNumber(3);
                newRecord.setDebtEndPeriod(value);
                newRecord.setFairValueAdjustmentsEndPeriod(MathUtils.subtract(newRecord.getTotalEndPeriod(), newRecord.getDebtEndPeriod()));
                records.add(newRecord);
            }
        });
        terraNetCostByFundName.forEach((key, value)->{
            if(value != null && value.doubleValue() != 0.0){
                Double unrealizedGain = MathUtils.multiply(terraUnrealizedGainsByFundName.get(key), endCurrencyRatesDto.getValue());
                Double interest = MathUtils.multiply(terraInterestByFundName.get(key), endCurrencyRatesDto.getValue());
                Double fairValue = MathUtils.add(value, unrealizedGain, interest);
                if(fairValue == 0.0){
                    // need to add
                    ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
                    newRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_1123_010);
                    newRecord.setName(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT);
                    newRecord.setEntityName(key);
                    newRecord.setOtherName(PeriodicReportConstants.TERRA_LOWER_CASE);
                    newRecord.setLineNumber(3);
                    newRecord.setDebtEndPeriod(value);
                    newRecord.setInterestEndPeriod(terraInterestByFundName.get(key));
                    newRecord.setTotalEndPeriod(MathUtils.add(newRecord.getTotalEndPeriod(), newRecord.getInterestEndPeriod()));
                    newRecord.setFairValueAdjustmentsEndPeriod(MathUtils.subtract(newRecord.getTotalEndPeriod(), newRecord.getDebtEndPeriod()));
                    records.add(newRecord);
                }
            }
        });

        ReportingFundRenameInfoDto fundRenameInfo = getFundRenameInfo(reportId);
        if(!fundRenameInfo.isEmpty()) {
            for(ConsolidatedKZTForm7RecordDto record: records) {
                for (ReportingFundRenamePairDto pairDto : fundRenameInfo.getFundRenames()) {
                    if (record.getEntityName().contains(pairDto.getPreviousFundName()) && !pairDto.isUsePreviousFundName()) {
                        // TODO: Check type
                        String newName = record.getEntityName().replace(pairDto.getPreviousFundName(), pairDto.getCurrentFundName());
                        record.setEntityName(newName);
                    } else if (record.getEntityName().contains(pairDto.getCurrentFundName()) && pairDto.isUsePreviousFundName()) {
                        // TODO: Check type
                        String newName = record.getEntityName().replace(pairDto.getCurrentFundName(), pairDto.getPreviousFundName());
                        record.setEntityName(newName);
                    }
                }
            }
        }

        responseDto.setRecords(records);
        return responseDto;
    }

    private void addValuesKZTForm7(ConsolidatedKZTForm7RecordDto totalRecord, ConsolidatedKZTForm7RecordDto record){
        double interestStartPeriod = totalRecord.getInterestStartPeriod() != null ? totalRecord.getInterestStartPeriod() : 0.0;
        interestStartPeriod = MathUtils.add(interestStartPeriod, (record.getInterestStartPeriod() != null ? record.getInterestStartPeriod() : 0.0));
        totalRecord.setInterestStartPeriod(interestStartPeriod);


        double debtTurnover = totalRecord.getDebtTurnover() != null ? totalRecord.getDebtTurnover().doubleValue() : 0;
        debtTurnover = MathUtils.add(debtTurnover, (record.getDebtTurnover() != null ? record.getDebtTurnover().doubleValue() : 0));
        totalRecord.setDebtTurnover(debtTurnover);

        double interestTurnover = totalRecord.getInterestTurnover() != null ? totalRecord.getInterestTurnover() : 0.0;
        interestTurnover = MathUtils.add(interestTurnover, (record.getInterestTurnover() != null ? record.getInterestTurnover() : 0.0));
        totalRecord.setInterestTurnover(interestTurnover);

        double totalFairValueAdjustmentsTurnoverPositive = totalRecord.getFairValueAdjustmentsTurnoverPositive() != null ? totalRecord.getFairValueAdjustmentsTurnoverPositive().doubleValue() : 0;
        totalFairValueAdjustmentsTurnoverPositive = MathUtils.add(totalFairValueAdjustmentsTurnoverPositive, (record.getFairValueAdjustmentsTurnoverPositive() != null ? record.getFairValueAdjustmentsTurnoverPositive().doubleValue() : 0));
        totalRecord.setFairValueAdjustmentsTurnoverPositive(totalFairValueAdjustmentsTurnoverPositive);

        double totalFairValueAdjustmentsTurnoverNegative = totalRecord.getFairValueAdjustmentsTurnoverNegative() != null ? totalRecord.getFairValueAdjustmentsTurnoverNegative().doubleValue() : 0;
        totalFairValueAdjustmentsTurnoverNegative = MathUtils.add(totalFairValueAdjustmentsTurnoverNegative, (record.getFairValueAdjustmentsTurnoverNegative() != null ? record.getFairValueAdjustmentsTurnoverNegative().doubleValue() : 0));
        totalRecord.setFairValueAdjustmentsTurnoverNegative(totalFairValueAdjustmentsTurnoverNegative);

        double debtEndPeriod = totalRecord.getDebtEndPeriod() != null ? totalRecord.getDebtEndPeriod().doubleValue() : 0;
        debtEndPeriod = MathUtils.add(debtEndPeriod, (record.getDebtEndPeriod() != null ? record.getDebtEndPeriod().doubleValue() : 0));
        totalRecord.setDebtEndPeriod(debtEndPeriod);

        double interestEndPeriod = totalRecord.getInterestEndPeriod() != null ? totalRecord.getInterestEndPeriod() : 0.0;
        interestEndPeriod = MathUtils.add(interestEndPeriod, (record.getInterestEndPeriod() != null ? record.getInterestEndPeriod() : 0.0));
        totalRecord.setInterestEndPeriod(interestEndPeriod);

        double fairValueAdjustmentsEndPeriod = totalRecord.getFairValueAdjustmentsEndPeriod() != null ? totalRecord.getFairValueAdjustmentsEndPeriod().doubleValue() : 0;
        fairValueAdjustmentsEndPeriod = MathUtils.add(fairValueAdjustmentsEndPeriod, (record.getFairValueAdjustmentsEndPeriod() != null ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0));
        totalRecord.setFairValueAdjustmentsEndPeriod(fairValueAdjustmentsEndPeriod);

        double totalEndPeriod = totalRecord.getTotalEndPeriod() != null ? totalRecord.getTotalEndPeriod().doubleValue() : 0;
        totalEndPeriod = MathUtils.add(totalEndPeriod, (record.getTotalEndPeriod() != null ? record.getTotalEndPeriod().doubleValue() : 0));
        totalRecord.setTotalEndPeriod(totalEndPeriod);
    }

    @Override
    public ListResponseDto generateConsolidatedBalanceKZTForm8(Long reportId) {

        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedKZTForm8RecordDto> records =  getConsolidatedBalanceKZTForm8Saved(report.getId());
            responseDto.setRecords(records);
            return responseDto;
        }else{
            Date rateDate = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
            // Find exchange rate
            CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, CurrencyLookup.USD.getCode());
            if(endCurrencyRatesDto == null){
                logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
                responseDto.setErrorMessageEn("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
                return responseDto;
            }

            Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            List<ConsolidatedKZTForm8RecordDto> previousRecords = previousReport != null ?
                    getConsolidatedBalanceKZTForm8Saved(previousReport.getId()) : new ArrayList<>();

            //Map<String, ReportingFundRenamePairDto> fundRenames = getFundRenameInfoMap(reportId);
            ReportingFundRenameInfoDto fundRenameInfo = getFundRenameInfo(reportId);
            ConsolidatedKZTForm8RecordDto totalRecord = new ConsolidatedKZTForm8RecordDto();
            int index = 0;
            List<ConsolidatedKZTForm8RecordDto> records = new ArrayList<>();
            Map<String, Integer> recordsMap = new HashMap<>();
            if(previousRecords != null && !previousRecords.isEmpty()){
                int maxLineNumber = 0;
                for(ConsolidatedKZTForm8RecordDto dto: previousRecords){
                    if(dto.getLineNumber() != null && dto.getLineNumber() > maxLineNumber){
                        maxLineNumber = dto.getLineNumber();
                    }
                }

                int addedIndex = 0;
                for(int i = 0; i < previousRecords.size(); i++){
                    ConsolidatedKZTForm8RecordDto record = previousRecords.get(i);
                    if(record.getAccountNumber() == null || (record.getDebtEndPeriod() != null && record.getDebtEndPeriod() != 0) ||
                            (record.getEndPeriodBalance() != null && record.getEndPeriodBalance() != 0)){
                        record.setDebtStartPeriod(record.getDebtEndPeriod());
                        record.setDebtEndPeriod(null);
                        record.setDebtDifference(MathUtils.subtract(record.getDebtEndPeriod(), record.getDebtStartPeriod()));
                        record.setStartPeriodBalance(record.getEndPeriodBalance());
                        record.setEndPeriodBalance(null);

                        // Fund rename
                        if(!fundRenameInfo.isEmpty()){
                            for(ReportingFundRenamePairDto pairDto: fundRenameInfo.getFundRenames()){
                                if(record.getName() .contains(pairDto.getPreviousFundName()) && !pairDto.isUsePreviousFundName()){
                                    // TODO: Check type
                                    String newName = record.getName() .replace(pairDto.getPreviousFundName(), pairDto.getCurrentFundName());
                                    record.setName(newName);
                                }else if(record.getName() .contains(pairDto.getCurrentFundName()) && pairDto.isUsePreviousFundName()){
                                    // TODO: Check type
                                    String newName = record.getName() .replace(pairDto.getCurrentFundName(), pairDto.getPreviousFundName());
                                    record.setName(newName);
                                }
                            }
                        }
                        records.add(record);
                        recordsMap.put(record.getName(), addedIndex);
                        addedIndex++;
                    }
                }
            }else{
                records = getConsolidatedBalanceKZTForm8LineHeaders();
            }

            final int record1283_020LineNumber = 6;
            for(int i = 0; i < records.size(); i++){
                ConsolidatedKZTForm8RecordDto record = records.get(i);
                if(record.getLineNumber() == record1283_020LineNumber + 1){
                    index = i;
                    break;
                }
            }

            ListResponseDto balanceUSDResponseDto = generateConsolidatedBalanceUSDForm(reportId);
            if(balanceUSDResponseDto.getStatus() == ResponseStatusType.FAIL){
                responseDto.appendErrorMessageEn("Error generating USD report: " + balanceUSDResponseDto.getErrorMessageEn());
                //return responseDto;
            }
            List<ConsolidatedBalanceFormRecordDto> USDFormRecords = balanceUSDResponseDto.getRecords();


            if(records != null && index > 0){
                for(ConsolidatedBalanceFormRecordDto recordUSD: USDFormRecords){
                    if(recordUSD.getLineNumber() == 9 && recordUSD.getAccountNumber() != null){
                        // check if exists in previous
                        if(recordsMap.get(recordUSD.getName()) != null){
                            int existingRecordIndex = recordsMap.get(recordUSD.getName());
                            ConsolidatedKZTForm8RecordDto existingRecord = records.get(existingRecordIndex);

                            existingRecord.setDebtEndPeriod(MathUtils.multiply(recordUSD.getCurrentAccountBalance(), endCurrencyRatesDto.getValue()));
                            existingRecord.setDebtDifference(MathUtils.subtract(existingRecord.getDebtEndPeriod(), existingRecord.getDebtStartPeriod()));
                            existingRecord.setEndPeriodBalance(existingRecord.getDebtEndPeriod());

                            totalRecord.setDebtEndPeriod(MathUtils.add(totalRecord.getDebtEndPeriod(), existingRecord.getDebtEndPeriod()));
                            totalRecord.setDebtDifference(MathUtils.add(totalRecord.getDebtDifference(), existingRecord.getDebtDifference()));
                            totalRecord.setEndPeriodBalance(MathUtils.add(totalRecord.getEndPeriodBalance(), existingRecord.getEndPeriodBalance()));

                            recordsMap.put(recordUSD.getName(), null);
                        }else {
                            // new record
                            ConsolidatedKZTForm8RecordDto newRecord = new ConsolidatedKZTForm8RecordDto();
                            newRecord.setLineNumber(record1283_020LineNumber);
                            newRecord.setName(recordUSD.getName());
                            if(!fundRenameInfo.isEmpty()){
                                for(ReportingFundRenamePairDto pairDto: fundRenameInfo.getFundRenames()){
                                    if(newRecord.getName() .contains(pairDto.getPreviousFundName()) && !pairDto.isUsePreviousFundName()){
                                        String newName = newRecord.getName() .replace(pairDto.getPreviousFundName(), pairDto.getCurrentFundName());
                                        newRecord.setName(newName);
                                    }else if(newRecord.getName() .contains(pairDto.getCurrentFundName()) && pairDto.isUsePreviousFundName()){
                                        String newName = newRecord.getName() .replace(pairDto.getCurrentFundName(), pairDto.getPreviousFundName());
                                        newRecord.setName(newName);
                                    }
                                }
                            }
                            newRecord.setAccountNumber(recordUSD.getAccountNumber());

                            //newRecord.setDebtStartPeriod(MathUtils.multiply(record.getPreviousAccountBalance() != null ? record.getPreviousAccountBalance() : 0.0, startCurrencyRatesDto.getValue()));
                            newRecord.setDebtEndPeriod(MathUtils.multiply(recordUSD.getCurrentAccountBalance(), endCurrencyRatesDto.getValue()));
                            newRecord.setDebtDifference(MathUtils.subtract(newRecord.getDebtEndPeriod(), newRecord.getDebtStartPeriod()));
                            newRecord.setEndPeriodBalance(newRecord.getDebtEndPeriod());

                            totalRecord.setDebtEndPeriod(MathUtils.add(totalRecord.getDebtEndPeriod(), newRecord.getDebtEndPeriod()));
                            totalRecord.setDebtDifference(MathUtils.add(totalRecord.getDebtDifference(), newRecord.getDebtDifference()));
                            totalRecord.setEndPeriodBalance(MathUtils.add(totalRecord.getEndPeriodBalance(), newRecord.getEndPeriodBalance()));

                            if(recordUSD.getOtherEntityName() != null &&
                                    recordUSD.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
                                newRecord.setAgreementDescription(PeriodicReportConstants.SINGULARITY_AGREEMENT_DESC);
                            }else if(recordUSD.getOtherEntityName() != null &&
                                    recordUSD.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
                                newRecord.setAgreementDescription(PeriodicReportConstants.TARRAGON_AGREEMENT_DESC);
                            }else if(recordUSD.getOtherEntityName() != null &&
                                    recordUSD.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
                                newRecord.setAgreementDescription(PeriodicReportConstants.TERRA_AGREEMENT_DESC);
                            }

                            Date date = null;
                            date = DateUtils.getLastDayOfCurrentMonth(report.getReportDate());
                            newRecord.setDebtStartDate(date);

                            records.add(index, newRecord);
                            index ++;
                        }
                    }
                }

                for(String name: recordsMap.keySet()){
                    if(recordsMap.get(name) != null) {
                        ConsolidatedKZTForm8RecordDto record = records.get(recordsMap.get(name));
                        if(record.getAccountNumber() != null && record.getDebtStartPeriod() != null && record.getDebtEndPeriod() == null) {
                            totalRecord.setDebtDifference(MathUtils.add(totalRecord.getDebtDifference(), record.getDebtDifference()));
                        }
                    }
                }

            }

            // Set total sums
            List<ConsolidatedKZTForm8RecordDto> nonEmptyRecords = new ArrayList<>();
            for(ConsolidatedKZTForm8RecordDto record: records){
                if(record.isEmpty() && !StringUtils.isEmpty(record.getAccountNumber())){
                    // skip empty records
                    continue;
                }
                if(record.getAccountNumber() == null && (record.getLineNumber() == 1 || record.getLineNumber() == 6 || record.getLineNumber() == 13)){
//                    record.setDebtStartPeriod(totalRecord.getDebtStartPeriod());
                    record.setDebtEndPeriod(totalRecord.getDebtEndPeriod());
                    record.setDebtDifference(totalRecord.getDebtDifference());
//                    record.setStartPeriodBalance(totalRecord.getStartPeriodBalance());
                    record.setEndPeriodBalance(totalRecord.getEndPeriodBalance());
                }
                nonEmptyRecords.add(record);
            }

            responseDto.setRecords(nonEmptyRecords);
            return responseDto;
        }
    }

    @Override
    public  ListResponseDto generateConsolidatedBalanceKZTForm10(Long reportId) {

        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedKZTForm10RecordDto> records =  getConsolidatedBalanceKZTForm10Saved(reportId);
            responseDto.setRecords(records);
            return responseDto;
        }else{
            Date rateDate = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
            // Find exchange rate
            CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, CurrencyLookup.USD.getCode());
            if(endCurrencyRatesDto == null){
                logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
                responseDto.setErrorMessageEn("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
                return responseDto;
            }

            List<ConsolidatedKZTForm10RecordDto> records = new ArrayList<>();

            Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            List<ConsolidatedKZTForm10RecordDto> prevRecords = previousReport != null ?
                    getConsolidatedBalanceKZTForm10Saved(previousReport.getId()) : new ArrayList<ConsolidatedKZTForm10RecordDto>();


            if(prevRecords == null ||  prevRecords.isEmpty()){
                prevRecords = getConsolidatedBalanceKZTForm10LineHeaders();
            }
            int maxLineNumber = 0;
            for(ConsolidatedKZTForm10RecordDto dto: prevRecords){
                if(dto.getLineNumber() != null && dto.getLineNumber() > maxLineNumber){
                    maxLineNumber = dto.getLineNumber();
                }
            }

            for(ConsolidatedKZTForm10RecordDto prevRecord: prevRecords){
                if(prevRecord.getAccountNumber() == null || (prevRecord.getEndPeriodAssets() != null && prevRecord.getEndPeriodAssets() != 0)) {
                    prevRecord.setStartPeriodAssets(prevRecord.getEndPeriodAssets());
                    prevRecord.setStartPeriodBalance(prevRecord.getEndPeriodBalance());

                    prevRecord.setTurnoverPurchased(null);
                    prevRecord.setTurnoverOther(null);
                    prevRecord.setEndPeriodAssets(null);
                    prevRecord.setEndPeriodBalance(null);

                    records.add(prevRecord);
                }
            }

            int indexLineToAdd3 = 0;
            int indexLineToAdd8 = 0;
            Map<String,Integer> addedRecordNames = new HashMap<>();
            for(int i = 0; i < records.size(); i++){
                ConsolidatedKZTForm10RecordDto record = records.get(i);
                if(record.getLineNumber() == 4){
                    indexLineToAdd3 = i;
                }else if(record.getLineNumber() == 9){
                    indexLineToAdd8 = i;
                }
            }

            //List<ConsolidatedBalanceFormRecordDto> previousUSDFormRecords = previousReport != null ? getConsolidatedBalanceUSDFormSaved(previousReport.getId()) : null;

            String nameNICKMFMain = NICChartAccountsLookup.ORG_COST_NICKMF.getNameRu();
            String nameNICKMFOther = NICChartAccountsLookup.AMORTIZATION_ORG_COSTS_NICKMF.getNameRu();
            // TODO: Organization costs NICK MF

            String nameSingularityMain = NICChartAccountsLookup.ORG_COSTS_SINGULARITY.getNameRu();
            String nameSingularityOther = NICChartAccountsLookup.AMORTIZATION_ORG_COSTS_SINGULARITY.getNameRu();
            // TODO: Organization costs Singularity

            ListResponseDto balanceUSDResponseDto = generateConsolidatedBalanceUSDForm(report.getId());
            if(balanceUSDResponseDto.getStatus() == ResponseStatusType.FAIL){
                //responseDto.setErrorMessageEn("Error generating USD Balance Form: " + balanceUSDResponseDto.getMessage().getNameEn());
                //return responseDto;
                responseDto.appendErrorMessageEn("Error generating USD Balance Form: " + balanceUSDResponseDto.getMessage().getNameEn());
            }
            List<ConsolidatedBalanceFormRecordDto> USDFormRecords = balanceUSDResponseDto.getStatus() == ResponseStatusType.SUCCESS ?
                    balanceUSDResponseDto.getRecords() : new ArrayList<>();
            Double total1623EndPeriod = 0.0;
            Double total2923EndPeriod = 0.0;

            Double turnover1623PositiveSum = 0.0;
            Double turnover1623NegativeSum = 0.0;
            Double turnover2923PositiveSum = 0.0;
            Double turnover2923NegativeSum = 0.0;

            if(USDFormRecords != null && !USDFormRecords.isEmpty() && (indexLineToAdd3 > 0 || indexLineToAdd8 > 0)){
                for(ConsolidatedBalanceFormRecordDto recordUSD: USDFormRecords){
                    if(recordUSD.getAccountNumber() != null && (recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1623_010) ||
                            recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2923_010))){
                        if(recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1623_010)) {
                            boolean recordExists = false;
                            for(ConsolidatedKZTForm10RecordDto record: records){
                                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1623_010) &&
                                        record.getName().equalsIgnoreCase(recordUSD.getName())){
                                    double endPeriodAssetsValue = MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ?
                                            recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue());
                                    record.setEndPeriodAssets(MathUtils.add(endPeriodAssetsValue, record.getEndPeriodAssets()));

                                    double endPeriodAssets = record.getEndPeriodAssets() != null ? record.getEndPeriodAssets() : 0.0;
                                    double startPeriodAssets = record.getStartPeriodAssets() != null ? record.getStartPeriodAssets() : 0.0;
                                    double turnoverValue = MathUtils.subtract(endPeriodAssets, startPeriodAssets);
                                    record.setTurnoverPurchased(turnoverValue > 0 ? turnoverValue: null);
                                    record.setTurnoverOther( turnoverValue < 0 ? turnoverValue: null);

                                    turnover1623PositiveSum = MathUtils.add(turnover1623PositiveSum, turnoverValue > 0 ? turnoverValue: 0.0);
                                    turnover1623NegativeSum = MathUtils.add(turnover1623NegativeSum, turnoverValue < 0 ? turnoverValue: 0.0);

                                    recordExists = true;
                                    break;
                                }
                            }
                            if(!recordExists){
                                ConsolidatedKZTForm10RecordDto newRecord = new ConsolidatedKZTForm10RecordDto();
                                newRecord.setAccountNumber(recordUSD.getAccountNumber());
                                newRecord.setName(recordUSD.getName());
                                newRecord.setLineNumber(3);

                                newRecord.setEndPeriodAssets(MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));

                                double endPeriodAssets = newRecord.getEndPeriodAssets() != null ? newRecord.getEndPeriodAssets() : 0.0;
                                double startPeriodAssets = newRecord.getStartPeriodAssets() != null ? newRecord.getStartPeriodAssets() : 0.0;
                                double turnoverValue = MathUtils.subtract(endPeriodAssets, startPeriodAssets);
                                newRecord.setTurnoverPurchased(turnoverValue > 0 ? turnoverValue: null);
                                newRecord.setTurnoverOther( turnoverValue < 0 ? turnoverValue: null);

                                turnover1623PositiveSum = MathUtils.add(turnover1623PositiveSum, turnoverValue > 0 ? turnoverValue: 0.0);
                                turnover1623NegativeSum = MathUtils.add(turnover1623NegativeSum, turnoverValue < 0 ? turnoverValue: 0.0);

                                records.add(indexLineToAdd3, newRecord);

                                indexLineToAdd3++;
                                indexLineToAdd8++;
                            }

                            total1623EndPeriod = MathUtils.add(total1623EndPeriod,
                                    MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0, endCurrencyRatesDto.getValue()));
                        }else if(recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2923_010)) {
//                            String name = null;
//                            if(recordUSD.getName().equalsIgnoreCase(nameNICKMFMain) || recordUSD.getName().equalsIgnoreCase(nameNICKMFOther)){
//                                name = nameNICKMFMain;
//                            }else if(recordUSD.getName().equalsIgnoreCase(nameSingularityMain) || recordUSD.getName().equalsIgnoreCase(nameSingularityOther)){
//                                name = nameSingularityMain;
//                            }else{
//
//                                logger.error("Report KZT 10: account number '2923.010' expected name mismatch: " + recordUSD.getName());
//                                responseDto.setErrorMessageEn("USD Balance Report error: account number '2923.010' expected name mismatch  '" + recordUSD.getName() + "'");
//                                return responseDto;
//                            }

                            boolean recordExists = false;
                            for(ConsolidatedKZTForm10RecordDto record: records){
                                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2923_010) /*&&
                                        record.getName().equalsIgnoreCase(name)*/ && record.getName().equalsIgnoreCase(recordUSD.getName())){
                                    double endPeriodAssetsUSDValue = MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue());
                                    record.setEndPeriodAssets(MathUtils.add(endPeriodAssetsUSDValue, record.getEndPeriodAssets()));

                                    double endPeriodAssets = record.getEndPeriodAssets() != null ? record.getEndPeriodAssets() : 0.0;
                                    double startPeriodAssets = record.getStartPeriodAssets() != null ? record.getStartPeriodAssets() : 0.0;
                                    double turnoverValue = MathUtils.subtract(endPeriodAssets, startPeriodAssets);
                                    record.setTurnoverPurchased(turnoverValue > 0 ? turnoverValue: null);
                                    record.setTurnoverOther( turnoverValue < 0 ? turnoverValue: null);

                                    turnover2923PositiveSum = MathUtils.add(turnover2923PositiveSum, turnoverValue > 0 ? turnoverValue: 0.0);
                                    turnover2923NegativeSum = MathUtils.add(turnover2923NegativeSum, turnoverValue < 0 ? turnoverValue: 0.0);
                                    recordExists = true;
                                    break;
                                }
                            }

                            if(!recordExists){
                                ConsolidatedKZTForm10RecordDto newRecord = new ConsolidatedKZTForm10RecordDto();
                                newRecord.setAccountNumber(recordUSD.getAccountNumber());
                                newRecord.setName(recordUSD.getName());
                                newRecord.setLineNumber(8);

                                newRecord.setEndPeriodAssets(MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));

                                double endPeriodAssets = newRecord.getEndPeriodAssets() != null ? newRecord.getEndPeriodAssets() : 0.0;
                                double startPeriodAssets = newRecord.getStartPeriodAssets() != null ? newRecord.getStartPeriodAssets() : 0.0;
                                double turnoverValue = MathUtils.subtract(endPeriodAssets, startPeriodAssets);
                                newRecord.setTurnoverPurchased(turnoverValue > 0 ? turnoverValue: null);
                                newRecord.setTurnoverOther( turnoverValue < 0 ? turnoverValue: null);
                                turnover2923PositiveSum = MathUtils.add(turnover2923PositiveSum, turnoverValue > 0 ? turnoverValue: 0.0);
                                turnover2923NegativeSum = MathUtils.add(turnover2923NegativeSum, turnoverValue < 0 ? turnoverValue: 0.0);

                                records.add(indexLineToAdd8, newRecord);

                                indexLineToAdd3++;
                                indexLineToAdd8++;
                            }

                            total2923EndPeriod = MathUtils.add(total2923EndPeriod, MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0, endCurrencyRatesDto.getValue()));
                        }
                    }
                }
            }

            // Set total sums
            for(ConsolidatedKZTForm10RecordDto record: records){
                if(record.getAccountNumber() == null && (record.getLineNumber() == 1 || record.getLineNumber() == 3)){
                    record.setEndPeriodAssets(total1623EndPeriod);
                    record.setTurnoverPurchased(turnover1623PositiveSum);
                    record.setTurnoverOther(turnover1623NegativeSum);
                }else if(record.getAccountNumber() == null && (record.getLineNumber() == 5 || record.getLineNumber() == 8)){
                    record.setEndPeriodAssets(total2923EndPeriod);
                    record.setTurnoverPurchased(turnover2923PositiveSum);
                    record.setTurnoverOther(turnover2923NegativeSum);
                }else if(record.getAccountNumber() == null && (record.getLineNumber() == 11)){
                    record.setEndPeriodAssets(total1623EndPeriod + total2923EndPeriod);
                    record.setTurnoverPurchased(MathUtils.add(turnover1623PositiveSum, turnover2923PositiveSum));
                    record.setTurnoverOther((MathUtils.add(turnover1623NegativeSum,turnover2923NegativeSum)));
                }else {

                    double endPeriodAssets = record.getEndPeriodAssets() != null ? record.getEndPeriodAssets() : 0.0;
                    double startPeriodAssets = record.getStartPeriodAssets() != null ? record.getStartPeriodAssets() : 0.0;
                    double turnoverValue = MathUtils.subtract(endPeriodAssets, startPeriodAssets);
                    record.setTurnoverPurchased(turnoverValue > 0 ? turnoverValue : null);
                    record.setTurnoverOther(turnoverValue < 0 ? turnoverValue : null);
                }

                record.setStartPeriodBalance(record.getStartPeriodAssets());
                record.setEndPeriodBalance(record.getEndPeriodAssets());
            }

            responseDto.setRecords(records);
            return responseDto;
        }
    }

    @Override
    public  ListResponseDto generateConsolidatedBalanceKZTForm13(Long reportId) {
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedKZTForm13RecordDto> records = getConsolidatedBalanceKZTForm13Saved(reportId);
            responseDto.setRecords(records);
            return responseDto;
        }else{
            Date rateDate = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
            // Find exchange rate
            CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, CurrencyLookup.USD.getCode());
            if(endCurrencyRatesDto == null){
                logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
                responseDto.setErrorMessageEn("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
                return responseDto;
            }

            Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            List<ConsolidatedKZTForm13RecordDto> previousRecords = previousReport != null ?
                    getConsolidatedBalanceKZTForm13Saved(previousReport.getId()) : new ArrayList<ConsolidatedKZTForm13RecordDto>();

            int index = 0;
            int indexLineNumber3 = 0;
            ConsolidatedKZTForm13RecordDto record3013_010 = null;
            boolean previousLineNumber2Exists = false;
            if(previousRecords == null || previousRecords.isEmpty()){
                previousRecords = getConsolidatedBalanceKZTForm13LineHeaders();
            }else{
                // Reset previous period records
                ReportingFundRenameInfoDto fundRenameInfo = getFundRenameInfo(reportId);
                for(int i = 0; i < previousRecords.size(); i++){
                    ConsolidatedKZTForm13RecordDto record = previousRecords.get(i);
//                    if(record.getAccountNumber() == null && record.getLineNumber() == 2){
//                        record.setName("Займы полученные "); // new report form, need space
//                    }
                    record.setDebtStartPeriod(record.getDebtEndPeriod());
                    record.setInterestStartPeriod(record.getInterestEndPeriod());
                    record.setTotalStartPeriod(record.getTotalEndPeriod());

                    record.setDebtEndPeriod(null);
                    record.setInterestEndPeriod(null);
                    record.setTotalEndPeriod(null);

                    //record.setDebtTurnover(null);
                    record.setDebtTurnover(MathUtils.subtract(record.getDebtEndPeriod(), record.getDebtStartPeriod()));
                    record.setInterestTurnover(null);
                    //record.setTotalTurnover(null);

                    if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3013_010) &&
                            record.getName().equalsIgnoreCase(PeriodicReportConstants.BANK_LOANS_RECEIVED)){
                        record3013_010 = record;
                        previousLineNumber2Exists = true;
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060) &&
                            record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_3053_060)){
                    }
                    if(record.getLineNumber() == 3 && index == 0){
                        index = i;
                        indexLineNumber3 = i + 1;
                        //break;
                    }

                    // Fund rename
                    if(fundRenameInfo != null && !fundRenameInfo.isEmpty() && record.getLineNumber() == 3 && record.getAccountNumber() != null &&
                            record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060)){
                        for(ReportingFundRenamePairDto pairDto: fundRenameInfo.getFundRenames()){
                            if(record.getEntityName().contains(pairDto.getPreviousFundName()) && !pairDto.isUsePreviousFundName()){
                                // TODO: Check type
                                String newName = record.getEntityName().replace(pairDto.getPreviousFundName(), pairDto.getCurrentFundName());
                                record.setEntityName(newName);
                            }else if(record.getEntityName().contains(pairDto.getCurrentFundName()) && pairDto.isUsePreviousFundName()){
                                // TODO: Check type
                                String newName = record.getEntityName().replace(pairDto.getCurrentFundName(), pairDto.getPreviousFundName());
                                record.setEntityName(newName);
                            }
                        }
                    }
                }
            }

            // Record 3013.010
            if(record3013_010 == null) {
                record3013_010 = new ConsolidatedKZTForm13RecordDto();
                record3013_010.setAccountNumber(PeriodicReportConstants.ACC_NUM_3013_010);
                record3013_010.setName(PeriodicReportConstants.BANK_LOANS_RECEIVED);
                record3013_010.setLineNumber(2);
                record3013_010.setEntityName(PeriodicReportConstants.BANK_OF_MONREAL);

                int year = DateUtils.getYear(report.getReportDate());
                Date startPeriodDate = DateUtils.getDate("17.09." + year);
                if(report.getReportDate().before(startPeriodDate)){
                    record3013_010.setStartPeriod(DateUtils.getDate("17.09." + (year - 1)));
                    record3013_010.setEndPeriod(DateUtils.getDate("16.09." + (year)));
                }else{
                    record3013_010.setStartPeriod(DateUtils.getDate("17.09." + (year)));
                    record3013_010.setEndPeriod(DateUtils.getDate("16.09." + (year + 1)));
                }

                record3013_010.setInterestPaymentCount(1);
            }

            // Currency
            record3013_010.setCurrency("USD");

            // Interest rate
            record3013_010.setInterestRate(PeriodicReportConstants.DEFAULT_KZT_13_INTEREST_RATE); // default
            List<SingularityGeneralLedgerBalanceRecordDto> singularityAdjustedRecords = this.generalLedgerBalanceService.getAdjustedRecords(reportId);
            if(singularityAdjustedRecords != null && !singularityAdjustedRecords.isEmpty()){
                for(SingularityGeneralLedgerBalanceRecordDto record: singularityAdjustedRecords){
                    if(StringUtils.isNotEmpty(record.getInterestRate())){
                        // Input interest rate
                        record3013_010.setInterestRate(record.getInterestRate());
                    }
                }
            }

            boolean currentLineNumber2Exists = false;
            ListResponseDto balanceUSDResponseDto = generateConsolidatedBalanceUSDForm(report.getId());
            if(balanceUSDResponseDto.getStatus() == ResponseStatusType.FAIL){
                //responseDto.setErrorMessageEn("Error generating USD Balance form. " + balanceUSDResponseDto.getMessage().getNameEn());
                //return responseDto;
                responseDto.appendErrorMessageEn("Error generating USD Balance form: " + balanceUSDResponseDto.getMessage().getNameEn());
            }

            // Current period records from USD report
            List<ConsolidatedBalanceFormRecordDto> currentUSDFormRecords = balanceUSDResponseDto.getRecords();
            //ConsolidatedKZTForm13RecordDto lineNumber3Record = new ConsolidatedKZTForm13RecordDto();
            Double lineNumber3Sum = 0.0;
            if(currentUSDFormRecords != null){
                for(ConsolidatedBalanceFormRecordDto record: currentUSDFormRecords){
                    if(record.getAccountNumber() != null && (record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3013_010)) && record3013_010.getName().equalsIgnoreCase(record.getName())){
//                        double debtEndPeriod = record3013_010.getDebtEndPeriod() != null ? record3013_010.getDebtEndPeriod() : 0;
                        double currentValue = record3013_010.getDebtEndPeriod() != null ? record3013_010.getDebtEndPeriod() : 0;
                        record3013_010.setDebtEndPeriod(MathUtils.add(currentValue, /*debtEndPeriod,*/ MathUtils.multiply(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue())));
                        currentLineNumber2Exists = true;
                    }else if(record.getAccountNumber() != null && (record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3383_010))){
//                        double interestEndPeriod = record3013_010.getInterestEndPeriod() != null ? record3013_010.getInterestEndPeriod() : 0;
                        double currentValue = record3013_010.getInterestEndPeriod() != null ? record3013_010.getInterestEndPeriod() : 0;
                        record3013_010.setInterestEndPeriod(MathUtils.add(currentValue, /*interestEndPeriod,*/ MathUtils.multiply(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue())));
                        currentLineNumber2Exists = true;
                    }else if(record.getAccountNumber() != null && (record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060))){
                        Double amount = MathUtils.multiply(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue());
                        ConsolidatedKZTForm13RecordDto record3053_060 = new ConsolidatedKZTForm13RecordDto();
                        record3053_060.setAccountNumber(record.getAccountNumber());
                        if(record.getName() != null && record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_3053_060)){
                            record3053_060.setName(PeriodicReportConstants.RU_3053_060_FORM_13);
                        }else {
                            record3053_060.setName(record.getName());
                        }
                        record3053_060.setLineNumber(3);
                        record3053_060.setEntityName(record.getOtherEntityName());
                        record3053_060.setStartPeriod(DateUtils.getLastDayOfCurrentMonth(report.getReportDate()));
                        record3053_060.setEndPeriod(DateUtils.getLastDayOfNextMonth(report.getReportDate()));
                        //record3053_060.setInterestRate(PeriodicReportConstants.DEFAULT_KZT_13_INTEREST_RATE);
                        //record3053_060.setInterestPaymentCount(1);
                        record3053_060.setCurrency("USD");
                        record3053_060.setDebtStartPeriod(null);
                        record3053_060.setInterestStartPeriod(null);
                        record3053_060.setTotalStartPeriod(null);
                        record3053_060.setInterestTurnover(null);
                        //record3053_060.setTotalTurnover(null);
                        record3053_060.setDebtEndPeriod(amount);
                        record3053_060.setDebtTurnover(MathUtils.subtract(record3053_060.getDebtEndPeriod(), record3053_060.getDebtStartPeriod()));
                        record3053_060.setInterestEndPeriod(null);
//                        double totalEndPeriod = record3053_060.getDebtEndPeriod() != null ? record3053_060.getDebtEndPeriod() : 0;
//                        totalEndPeriod = MathUtils.add(totalEndPeriod, record3053_060.getInterestEndPeriod() != null ? record3053_060.getInterestEndPeriod() : 0);
                        record3053_060.setTotalEndPeriod(amount);

                        //Check if already exists, update if so, add new if not
                        boolean foundRecord3053_060 = false;
                        for(int i = 0; i < previousRecords.size(); i++) {
                            ConsolidatedKZTForm13RecordDto prevRecord = previousRecords.get(i);
                            if(prevRecord.getName().equalsIgnoreCase(record3053_060.getName()) &&
                                    prevRecord.getLineNumber().intValue() == record3053_060.getLineNumber().intValue() &&
                                    prevRecord.getEntityName() != null && prevRecord.getEntityName().equalsIgnoreCase(record3053_060.getEntityName())){
                                prevRecord.setDebtEndPeriod(amount);
                                prevRecord.setTotalEndPeriod(amount);
                                prevRecord.setDebtTurnover(MathUtils.subtract(prevRecord.getDebtEndPeriod(), prevRecord.getDebtStartPeriod()));
                                prevRecord.setCurrency("USD");
                                prevRecord.setEndPeriod(DateUtils.getLastDayOfNextMonth(report.getReportDate()));
                                foundRecord3053_060 = true;
                            }
                        }
                        if(!foundRecord3053_060){
                            previousRecords.add(indexLineNumber3, record3053_060);
                        }

                        lineNumber3Sum = MathUtils.add(lineNumber3Sum, amount);
                    }

                }
            }

            double totalEndPeriod = record3013_010.getDebtEndPeriod() != null ? record3013_010.getDebtEndPeriod() : 0;
            totalEndPeriod = MathUtils.add(totalEndPeriod, record3013_010.getInterestEndPeriod() != null ? record3013_010.getInterestEndPeriod() : 0);
            record3013_010.setTotalEndPeriod(totalEndPeriod);

            double interestTurnover = record3013_010.getInterestEndPeriod() != null ? record3013_010.getInterestEndPeriod() : 0;
            interestTurnover = MathUtils.subtract(interestTurnover, record3013_010.getInterestStartPeriod() != null ? record3013_010.getInterestStartPeriod() : 0);
            record3013_010.setInterestTurnover(interestTurnover);

            double debtTurnover = record3013_010.getDebtEndPeriod() != null ? record3013_010.getDebtEndPeriod() : 0;
            debtTurnover = MathUtils.subtract(debtTurnover, record3013_010.getDebtStartPeriod() != null ? record3013_010.getDebtStartPeriod() : 0);
            record3013_010.setDebtTurnover(debtTurnover);


            if(!previousLineNumber2Exists && currentLineNumber2Exists){
                previousRecords.add(index, record3013_010);
            }

            // Set total values
            ConsolidatedKZTForm13RecordDto totalRecord = new ConsolidatedKZTForm13RecordDto();
            for(ConsolidatedKZTForm13RecordDto record: previousRecords){
                if(StringUtils.isNotEmpty(record.getAccountNumber())){
                    addValuesKZTForm13(totalRecord, record);
                }
            }

            for(ConsolidatedKZTForm13RecordDto record: previousRecords){
                if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 2){
                    record.setDebtStartPeriod(record3013_010.getDebtStartPeriod());
                    record.setInterestStartPeriod(record3013_010.getInterestStartPeriod());
                    record.setTotalStartPeriod(record3013_010.getTotalStartPeriod());
                    record.setDebtTurnover(record3013_010.getDebtTurnover());
                    record.setInterestTurnover(record3013_010.getInterestTurnover());
                    record.setDebtEndPeriod(record3013_010.getDebtEndPeriod());
                    record.setInterestEndPeriod(record3013_010.getInterestEndPeriod());
                    record.setTotalEndPeriod(record3013_010.getTotalEndPeriod());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 3){
                    record.setDebtEndPeriod(lineNumber3Sum);
                    record.setDebtTurnover(MathUtils.subtract(record.getDebtEndPeriod(), record.getDebtStartPeriod()));
                    double value = MathUtils.add((record.getDebtEndPeriod() != null ? record.getDebtEndPeriod() : 0), record.getInterestEndPeriod() != null ? record.getInterestEndPeriod() : 0);
                    record.setTotalEndPeriod(value);
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && (record.getLineNumber() == 1 || record.getLineNumber() == 7)){
                    //addValuesKZTForm13(record, totalRecord);
                    record.setDebtStartPeriod(totalRecord.getDebtStartPeriod());
                    record.setInterestStartPeriod(totalRecord.getInterestStartPeriod());
                    record.setTotalStartPeriod(totalRecord.getTotalStartPeriod());
                    record.setDebtTurnover(totalRecord.getDebtTurnover());
                    record.setInterestTurnover(totalRecord.getInterestTurnover());
                    record.setDebtEndPeriod(totalRecord.getDebtEndPeriod());
                    record.setInterestEndPeriod(totalRecord.getInterestEndPeriod());
                    record.setTotalEndPeriod(totalRecord.getTotalEndPeriod());
                }
            }

            List<ConsolidatedKZTForm13RecordDto> noEmptyRecords = new ArrayList<>();
            for(ConsolidatedKZTForm13RecordDto record: previousRecords){
                if(!record.isEmptyAmounts() || record.getAccountNumber() == null){
                    noEmptyRecords.add(record);
                }
            }

            responseDto.setRecords(noEmptyRecords);
            return responseDto;
        }
    }

    private void addValuesKZTForm13(ConsolidatedKZTForm13RecordDto totalRecord, ConsolidatedKZTForm13RecordDto record){
        double debtStart = record.getDebtStartPeriod() != null ? record.getDebtStartPeriod() : 0.0;
        totalRecord.setDebtStartPeriod(MathUtils.add(totalRecord.getDebtStartPeriod(), debtStart));

        double interestStart = record.getInterestStartPeriod() != null ? record.getInterestStartPeriod() : 0.0;
        totalRecord.setInterestStartPeriod(MathUtils.add(totalRecord.getInterestStartPeriod(), interestStart));

        double totalStart = record.getTotalStartPeriod() != null ? record.getTotalStartPeriod() : 0.0;
        totalRecord.setTotalStartPeriod(MathUtils.add(totalRecord.getTotalStartPeriod(), totalStart));

        double debtTurnover = record.getDebtTurnover() != null ? record.getDebtTurnover() : 0.0;
        totalRecord.setDebtTurnover(MathUtils.add(totalRecord.getDebtTurnover(), debtTurnover));

        double interestTurnover = record.getInterestTurnover() != null ? record.getInterestTurnover() : 0.0;
        totalRecord.setInterestTurnover(MathUtils.add(totalRecord.getInterestTurnover(), interestTurnover));

        double debtEnd = record.getDebtEndPeriod() != null ? record.getDebtEndPeriod() : 0.0;
        totalRecord.setDebtEndPeriod(MathUtils.add(totalRecord.getDebtEndPeriod(), debtEnd));

        double interestEnd = record.getInterestEndPeriod() != null ? record.getInterestEndPeriod() : 0.0;
        totalRecord.setInterestEndPeriod(MathUtils.add(totalRecord.getInterestEndPeriod(), interestEnd));

        double totalEnd = record.getTotalEndPeriod() != null ? record.getTotalEndPeriod() : 0.0;
        totalRecord.setTotalEndPeriod(MathUtils.add(totalRecord.getTotalEndPeriod(), totalEnd));

    }

    private String getAgreementDescription(String name){
        if(name == null){
            return null;
        }else if(name.toLowerCase().contains(PeriodicReportConstants.TARRAGON_LOWER_CASE.toLowerCase())){
            return PeriodicReportConstants.TARRAGON_AGREEMENT_DESC;
        }else if(name.toLowerCase().contains(PeriodicReportConstants.SINGULARITY_LOWER_CASE.toLowerCase())){
            return PeriodicReportConstants.SINGULARITY_AGREEMENT_DESC;
        }else if(name.toLowerCase().contains(PeriodicReportConstants.TERRA_LOWER_CASE.toLowerCase())){
            return PeriodicReportConstants.TERRA_AGREEMENT_DESC;
        }else if(name.toLowerCase().contains(PeriodicReportConstants.NICKMF_CAPITAL_CASE.toLowerCase())){
            return PeriodicReportConstants.NICK_MF_AGREEMENT_DESC;
        }
        return null;
    }

    @Override
    public  ListResponseDto generateConsolidatedBalanceKZTForm14(Long reportId) {

        // TODO: simplify (total records, etc)

        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedKZTForm14RecordDto> records =  getConsolidatedBalanceKZTForm14Saved(report.getId());
            responseDto.setRecords(records);
            return responseDto;
        }else{
            Date rateDate = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
            // Find exchange rate
            CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, CurrencyLookup.USD.getCode());
            if(endCurrencyRatesDto == null){
                logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
                responseDto.setErrorMessageEn("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate) + "'");
                return responseDto;
            }

            List<ConsolidatedKZTForm14RecordDto> records = null;
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if(previousReport != null) {
                records = getConsolidatedBalanceKZTForm14Saved(previousReport.getId());
            }

            if(records == null || records.isEmpty()){
                records = getConsolidatedBalanceKZTForm14LineHeaders();
            }

            ConsolidatedKZTForm14RecordDto totalRecord = new ConsolidatedKZTForm14RecordDto();
            ConsolidatedKZTForm14RecordDto totalRecordLineNumber5 = new ConsolidatedKZTForm14RecordDto();
            ConsolidatedKZTForm14RecordDto totalRecordLineNumber10 = new ConsolidatedKZTForm14RecordDto();
            int index = 0;
            int indexLineNumber10 = 0;
            for(int i = 0; i < records.size(); i++){
                ConsolidatedKZTForm14RecordDto record = records.get(i);
                record.setDebtStartPeriod(record.getDebtEndPeriod());
                record.setDebtEndPeriod(null);
                record.setDebtDifference(MathUtils.subtract(record.getDebtEndPeriod(), record.getDebtStartPeriod()));

                if(record.getAccountNumber() != null && record.getLineNumber() != null &&
                        ((record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3393_020) && record.getLineNumber() == 5) ||
                        (record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_4173_010) || record.getLineNumber() == 10))) {
                    record.setAgreementDescription(getAgreementDescription(record.getName()));
                    if(record.getDebtStartDate() == null) {
                        record.setDebtStartDate(DateUtils.getLastDayOfCurrentMonth(report.getReportDate()));
                    }
                }

                if(record.getLineNumber() == 6){
                    index = i;
                }else if(record.getLineNumber() == 11){
                    indexLineNumber10 = i;
                }
            }

            List<ConsolidatedKZTForm14RecordDto> toAddRecords = new ArrayList<>();
            List<Integer> toAddIndices = new ArrayList<>();
            ListResponseDto balanceUSDResponseDto = generateConsolidatedBalanceUSDForm(report.getId());
            if(balanceUSDResponseDto.getStatus() == ResponseStatusType.FAIL){
                //responseDto.setErrorMessageEn("Error generating USD Balance form. " + balanceUSDResponseDto.getMessage().getNameEn());
                //return responseDto;
                responseDto.appendErrorMessageEn("Error generating USD Balance form: " + balanceUSDResponseDto.getMessage().getNameEn());
            }
            List<ConsolidatedBalanceFormRecordDto> USDFormRecords = balanceUSDResponseDto != null ? balanceUSDResponseDto.getRecords(): new ArrayList<>();
            if(records != null && index > 0){
                for(ConsolidatedBalanceFormRecordDto recordUSD: USDFormRecords){
                    boolean exists = false;
                    if(recordUSD.getAccountNumber() != null && (recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3393_020) ||
                            recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_4173_010))){
                        for(ConsolidatedKZTForm14RecordDto record: records){
                            if(record.getAccountNumber() != null && recordUSD.getAccountNumber().equalsIgnoreCase(record.getAccountNumber()) &&
                                    record.getName().equalsIgnoreCase(recordUSD.getName())){

                                Double currentValue = record.getDebtEndPeriod() != null ? record.getDebtEndPeriod() : 0.0;
                                Double newValue = MathUtils.multiply(recordUSD.getCurrentAccountBalance(), endCurrencyRatesDto.getValue());
                                record.setDebtEndPeriod(MathUtils.add(currentValue, newValue));

                                if(record.getDebtStartPeriod() != null && record.getDebtEndPeriod() != null) {
                                    if(record.getDebtDifference() != null) {
                                        record.setDebtDifference(MathUtils.add(record.getDebtDifference(), newValue));
                                    }else{
                                        record.setDebtDifference(MathUtils.subtract(record.getDebtEndPeriod(), record.getDebtStartPeriod()));
                                    }
                                }
                                exists = true;
                                break;
                            }else{
                            }
                        }
                        if(!exists){
                            ConsolidatedKZTForm14RecordDto newRecord = new ConsolidatedKZTForm14RecordDto();
                            if(recordUSD.getAccountNumber() != null && recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3393_020)) {
                                newRecord.setLineNumber(5);
                            }else if(recordUSD.getAccountNumber() != null && recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_4173_010)) {
                                newRecord.setLineNumber(10);
                            }
                            newRecord.setName(recordUSD.getName());
                            newRecord.setAccountNumber(recordUSD.getAccountNumber());

                            newRecord.setDebtEndPeriod(MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));
                            newRecord.setDebtDifference(newRecord.getDebtEndPeriod());

                            newRecord.setAgreementDescription(getAgreementDescription(newRecord.getName()));
                            newRecord.setDebtStartDate(DateUtils.getLastDayOfCurrentMonth(report.getReportDate()));

                            //records.add(index, newRecord);
                            toAddRecords.add(newRecord);
                            if(newRecord.getLineNumber() == 5) {
                                toAddIndices.add(index);
                                index ++;
                                indexLineNumber10 ++;
                            }else if(newRecord.getLineNumber() == 10) {
                                toAddIndices.add(indexLineNumber10);
                                indexLineNumber10 ++;

                            }
                        }
                    }
                }

                for(int i = 0; i < toAddRecords.size(); i++){
                    ConsolidatedKZTForm14RecordDto record = toAddRecords.get(i);
                    if(!record.isEmpty()) {
                        records.add(toAddIndices.get(i), record);
                    }
                }
            }

            for(ConsolidatedKZTForm14RecordDto record: records){
                if(record.getAccountNumber() != null && record.getLineNumber() == 5){
                    totalRecordLineNumber5.setDebtDifference(MathUtils.add(totalRecordLineNumber5.getDebtDifference(), record.getDebtDifference()));
                    totalRecordLineNumber5.setDebtEndPeriod(MathUtils.add(totalRecordLineNumber5.getDebtEndPeriod(), record.getDebtEndPeriod()));

                    totalRecord.setDebtDifference(MathUtils.add(totalRecord.getDebtDifference(), record.getDebtDifference()));
                    totalRecord.setDebtEndPeriod(MathUtils.add(totalRecord.getDebtEndPeriod(), record.getDebtEndPeriod()));
                }
                if(record.getAccountNumber() != null && record.getLineNumber() == 10){
                    totalRecordLineNumber10.setDebtDifference(MathUtils.add(totalRecordLineNumber10.getDebtDifference(), record.getDebtDifference()));
                    totalRecordLineNumber10.setDebtEndPeriod(MathUtils.add(totalRecordLineNumber10.getDebtEndPeriod(), record.getDebtEndPeriod()));

                    totalRecord.setDebtDifference(MathUtils.add(totalRecord.getDebtDifference(), record.getDebtDifference()));
                    totalRecord.setDebtEndPeriod(MathUtils.add(totalRecord.getDebtEndPeriod(), record.getDebtEndPeriod()));
                }

            }

            // Set total sums
            List<ConsolidatedKZTForm14RecordDto> recordWithoutEmpty = new ArrayList<>();
            for(ConsolidatedKZTForm14RecordDto record: records){
                if(record.getAccountNumber() == null && record.getLineNumber() == 11){
                    record.setDebtEndPeriod(totalRecord.getDebtEndPeriod());
                    record.setDebtDifference(totalRecord.getDebtDifference());
                }else if(record.getAccountNumber() == null && (record.getLineNumber() == 1 || record.getLineNumber() == 5)){
                    record.setDebtEndPeriod(totalRecordLineNumber5.getDebtEndPeriod());
                    record.setDebtDifference(totalRecordLineNumber5.getDebtDifference());
                }else if(record.getAccountNumber() == null && (record.getLineNumber() == 6 || record.getLineNumber() == 10)){
                    record.setDebtEndPeriod(totalRecordLineNumber10.getDebtEndPeriod());
                    record.setDebtDifference(totalRecordLineNumber10.getDebtDifference());
                }
                if(!record.isEmpty() || (record.getAccountNumber() == null && record.getLineNumber() != null)){
                    recordWithoutEmpty.add(record);
                }
            }

            responseDto.setRecords(recordWithoutEmpty);
            return responseDto;
        }

    }

    @Override
    public  ListResponseDto generateConsolidatedBalanceKZTForm19(Long reportId){
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        if(currentReport.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedKZTForm19RecordDto> records = getConsolidatedBalanceKZTForm19Saved(reportId);
            responseDto.setRecords(records);
            return responseDto;
        }
        else{
            ListResponseDto KZTForm19ResponseDto = generateConsolidatedKZTForm19Current(reportId);
            if(KZTForm19ResponseDto.getStatus() == ResponseStatusType.FAIL){
                return KZTForm19ResponseDto;
            }
            List<ConsolidatedKZTForm19RecordDto> currentRecords = KZTForm19ResponseDto.getRecords();

            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            List<ConsolidatedKZTForm19RecordDto> previousRecords = null;
            if(previousReport != null) {
                previousRecords = previousReport != null ? getConsolidatedBalanceKZTForm19Saved(previousReport.getId()) : new ArrayList<>();
            }

            if(previousRecords != null && !previousRecords.isEmpty()){
                int index = 0;
                for(ConsolidatedKZTForm19RecordDto record: currentRecords){
                    boolean previousRecordFound = false;
                    for(int i = index; i < previousRecords.size(); i++){
                        if(record.getName().equalsIgnoreCase(previousRecords.get(i).getName()) && record.getLineNumber() != null &&
                                previousRecords.get(i).getLineNumber() != null && record.getLineNumber() == previousRecords.get(i).getLineNumber()){
                            if(!DateUtils.isJanuary(currentReport.getReportDate())) {
                                record.setPreviousAccountBalance(previousRecords.get(i).getCurrentAccountBalance());
                            }
                            if((record.getCurrentAccountBalance() != null && record.getCurrentAccountBalance().doubleValue() != 0.0)
                                    || (record.getPreviousAccountBalance() != null && record.getPreviousAccountBalance().doubleValue() != 0.0)) {
                                record.setTurnover(MathUtils.subtract(record.getCurrentAccountBalance(), record.getPreviousAccountBalance()));
                            }
                            previousRecordFound = true;
                            break;
                        }
                    }
                    if(!previousRecordFound){
                        record.setTurnover(record.getCurrentAccountBalance());
                    }
                }
                // records in previous not int current
                List<ConsolidatedKZTForm19RecordDto> previousToAdd = new ArrayList<>();
                for(ConsolidatedKZTForm19RecordDto previousRecord: previousRecords){
                    boolean found = false;
                    for(ConsolidatedKZTForm19RecordDto currentRecord: currentRecords){
                        if(isMatchingRecords(previousRecord, currentRecord)){
                            found = true;
                            break;
                        }
                    }
                    if(!found && previousRecord.getCurrentAccountBalance() != null && previousRecord.getCurrentAccountBalance().doubleValue() != 0.0 &&
                            !DateUtils.isJanuary(currentReport.getReportDate())){
                        previousRecord.setPreviousAccountBalance(previousRecord.getCurrentAccountBalance());
                        previousRecord.setCurrentAccountBalance(null);
                        previousRecord.setTurnover(MathUtils.subtract(previousRecord.getCurrentAccountBalance(), previousRecord.getPreviousAccountBalance()));
                        previousToAdd.add(previousRecord);
                    }
                }

                currentRecords.addAll(previousToAdd);

                // set totals
                Map<Integer, ConsolidatedKZTForm19RecordDto> totalMap = new HashMap<>();
                for(ConsolidatedKZTForm19RecordDto record: currentRecords){
                    if(record.getLineNumber() != null && totalMap.get(record.getLineNumber()) == null){
                        totalMap.put(record.getLineNumber(), new ConsolidatedKZTForm19RecordDto());
                    }
                    if(record.getAccountNumber() != null){
                        ConsolidatedKZTForm19RecordDto totalRecord = totalMap.get(record.getLineNumber());
                        totalRecord.setCurrentAccountBalance(MathUtils.add(totalRecord.getCurrentAccountBalance(), record.getCurrentAccountBalance()));
                        totalRecord.setPreviousAccountBalance(MathUtils.add(totalRecord.getPreviousAccountBalance(), record.getPreviousAccountBalance()));
                        totalRecord.setTurnover(MathUtils.add(totalRecord.getTurnover(), record.getTurnover()));
                    }
                }
                for(ConsolidatedKZTForm19RecordDto record: currentRecords){
                    if(record.getAccountNumber() == null && record.getLineNumber() != null && totalMap.get(record.getLineNumber()) != null){
                        record.setPreviousAccountBalance(totalMap.get(record.getLineNumber()).getPreviousAccountBalance());
                        record.setCurrentAccountBalance(totalMap.get(record.getLineNumber()).getCurrentAccountBalance());
                        record.setTurnover(totalMap.get(record.getLineNumber()).getTurnover());
                    }
                }
                for(ConsolidatedKZTForm19RecordDto record: currentRecords) {
                    if (record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 8) {
                        record.setCurrentAccountBalance(totalMap.get(9).getCurrentAccountBalance());
                        record.setTurnover(totalMap.get(9).getTurnover());
                        record.setPreviousAccountBalance(totalMap.get(9).getPreviousAccountBalance());
                        totalMap.put(8, record);
                    } else if (record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 23) {
                        record.setCurrentAccountBalance(MathUtils.add(totalMap.get(24).getCurrentAccountBalance(), totalMap.get(27).getCurrentAccountBalance()));
                        record.setTurnover(MathUtils.add(totalMap.get(24).getTurnover(), totalMap.get(27).getTurnover()));
                        record.setPreviousAccountBalance(MathUtils.add(totalMap.get(24).getPreviousAccountBalance(), totalMap.get(27).getPreviousAccountBalance()));
                        totalMap.put(23, record);
                    } else if (record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 30) {
                        record.setCurrentAccountBalance(MathUtils.add(totalMap.get(31).getCurrentAccountBalance(), totalMap.get(32).getCurrentAccountBalance()));
                        record.setTurnover(MathUtils.add(totalMap.get(31).getTurnover(), totalMap.get(32).getTurnover()));
                        record.setPreviousAccountBalance(MathUtils.add(totalMap.get(31).getPreviousAccountBalance(), totalMap.get(32).getPreviousAccountBalance()));
                        totalMap.put(30, record);
                    } else if (record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 43) {
                        record.setCurrentAccountBalance(totalMap.get(44).getCurrentAccountBalance());
                        record.setTurnover(totalMap.get(44).getTurnover());
                        record.setPreviousAccountBalance(totalMap.get(44).getPreviousAccountBalance());
                        totalMap.put(43, record);
                    } else if (record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 55) {
                        //1, 8, 18, 23, 30, 33, 38, 43
                        record.setCurrentAccountBalance(MathUtils.add(totalMap.get(1).getCurrentAccountBalance(), totalMap.get(8).getCurrentAccountBalance(),
                                totalMap.get(18).getCurrentAccountBalance(),totalMap.get(23).getCurrentAccountBalance(), totalMap.get(30).getCurrentAccountBalance(),
                                totalMap.get(33).getCurrentAccountBalance(), totalMap.get(38).getCurrentAccountBalance(), totalMap.get(43).getCurrentAccountBalance()));
                        record.setTurnover(MathUtils.add(totalMap.get(1).getTurnover(), totalMap.get(8).getTurnover(),
                                totalMap.get(18).getTurnover(),totalMap.get(23).getTurnover(), totalMap.get(30).getTurnover(),
                                totalMap.get(33).getTurnover(), totalMap.get(38).getTurnover(), totalMap.get(43).getTurnover()));
                        record.setPreviousAccountBalance(MathUtils.add(totalMap.get(1).getPreviousAccountBalance(), totalMap.get(8).getPreviousAccountBalance(),
                                totalMap.get(18).getPreviousAccountBalance(),totalMap.get(23).getPreviousAccountBalance(), totalMap.get(30).getPreviousAccountBalance(),
                                totalMap.get(33).getPreviousAccountBalance(), totalMap.get(38).getPreviousAccountBalance(), totalMap.get(43).getPreviousAccountBalance()));
                        totalMap.put(55, record);
                    }
                }
            }

            Collections.sort(currentRecords);
            responseDto.setRecords(currentRecords);
            return responseDto;
        }

    }

    private ListResponseDto generateConsolidatedKZTForm19Current(Long reportId) {
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        Double averageRate = null;
        try {
            averageRate = this.currencyRatesService.getAverageRateForAllMonthsBeforeDateAndCurrency(report.getReportDate(), CurrencyLookup.USD.getCode(), 2);
            if (averageRate == null) {
                logger.error("No average currency rate found for date '" + DateUtils.getDateFormatted(report.getReportDate()));
                responseDto.setErrorMessageEn("No average currency rate found for date '" + DateUtils.getDateFormatted(report.getReportDate()) + "'");
                return responseDto;
            }
        }catch (IllegalStateException ex){
            responseDto.setErrorMessageEn(ex.getMessage());
            return responseDto;
        }

        Double record6113_030HF = 0.0;
        Double record6113_030PE = 0.0;
        Double record6113_030RE = 0.0;

        Double record6280_010HF = 0.0;
        Double record6280_010PE = 0.0;
        Double record6280_010RE = 0.0;

        Double record7470_010HF = 0.0;
        Double record7470_010PE = 0.0;
        Double record7470_010RE = 0.0;

        Double record6150_010HF = 0.0;
        Double record6150_010PE = 0.0;
        Double record6150_010RE = 0.0;

        Double record6150_030HF = 0.0;
        Double record6150_030PE = 0.0;
        Double record6150_030RE = 0.0;

        Double record7330_010HF = 0.0;
        Double record7330_010PE = 0.0;
        Double record7330_010RE = 0.0;

        Double record7330_030HF = 0.0;
        Double record7330_030PE = 0.0;
        Double record7330_030RE = 0.0;

        Double record7313_010HF = 0.0;
        Double record7313_010PE = 0.0;
        Double record7313_010RE = 0.0;

        List<ConsolidatedBalanceFormRecordDto> incomeExpenseUSDRecords = generateConsolidatedIncomeExpenseUSDForm(reportId);
        for(ConsolidatedBalanceFormRecordDto recordDto: incomeExpenseUSDRecords){
            if(recordDto.getCurrentAccountBalance() != null) {
                if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6113_030)) {
                    if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
                        record6113_030HF = MathUtils.add(record6113_030HF, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
                        record6113_030PE = MathUtils.add(record6113_030PE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
                        record6113_030RE = MathUtils.add(record6113_030RE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }
                }else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6280_010)) {
                    if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
                        record6280_010HF = MathUtils.add(record6280_010HF, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
                        record6280_010PE = MathUtils.add(record6280_010PE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
                        record6280_010RE = MathUtils.add(record6280_010RE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }
                }else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7470_010)) {
                    if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
                        record7470_010HF = MathUtils.add(record7470_010HF, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
                        record7470_010PE = MathUtils.add(record7470_010PE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
                        record7470_010RE = MathUtils.add(record7470_010RE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }
                }else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_010)) {
                    if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
                        record6150_010HF = MathUtils.add(record6150_010HF, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
                        record6150_010PE = MathUtils.add(record6150_010PE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
                        record6150_010RE = MathUtils.add(record6150_010RE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }
                }else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_010)) {
                    if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
                        record7330_010HF = MathUtils.add(record7330_010HF, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
                        record7330_010PE = MathUtils.add(record7330_010PE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
                        record7330_010RE = MathUtils.add(record7330_010RE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }
                }else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030)) {
                    if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
                        record6150_030HF = MathUtils.add(record6150_030HF, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
                        record6150_030PE = MathUtils.add(record6150_030PE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
                        record6150_030RE = MathUtils.add(record6150_030RE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }
                }else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030)) {
                    if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
                        record7330_030HF = MathUtils.add(record7330_030HF, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
                        record7330_030PE = MathUtils.add(record7330_030PE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
                        record7330_030RE = MathUtils.add(record7330_030RE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }
                }else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010)) {
                    if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
                        record7313_010HF = MathUtils.add(record7313_010HF, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                    }
//                    else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
//                        record7313_010PE = MathUtils.add(record7313_010PE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
//                    }else if(recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
//                        record7313_010RE = MathUtils.add(record7313_010RE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
//                    }
                }




//                if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6113_030) &&
//                        recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
//                    record6150_030HF = MathUtils.add(record6150_030HF, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
//                } else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030) &&
//                        recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
//                    record6150_030PE = MathUtils.add(record6150_030PE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
//                } else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030) &&
//                        recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
//                    record6150_030RE = MathUtils.add(record6150_030RE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
//                }else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030) &&
//                        recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE)) {
//                    record7330_030PE = MathUtils.add(record7330_030PE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
//                }else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030) &&
//                        recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.TERRA_CAPITAL_CASE)) {
//                    record7330_030RE = MathUtils.add(record7330_030RE, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
//                } else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030) &&
//                        recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
//                    record7330_030HF = MathUtils.add(record7330_030HF, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
//                } else if (recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010) &&
//                        recordDto.getOtherEntityName() != null && recordDto.getOtherEntityName().toUpperCase().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
//                    record7313_010HF = MathUtils.add(record7313_010HF, MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
//                }
            }
        }

        //double sum = MathUtils.add(record6150_030HF, record6150_030PE, record6150_030RE, record7330_030HF, record7330_030PE, record7330_030RE, record7313_010).doubleValue();
        double sum = MathUtils.add(record6113_030HF, record6113_030PE, record6113_030RE,
                record6280_010HF, record6280_010PE, record6280_010RE,
                record7470_010HF, record7470_010PE, record7470_010RE,
                record6150_010HF, record6150_010PE, record6150_010RE,
                record6150_030HF, record6150_030PE, record6150_030RE,
                record7330_010HF, record7330_010PE, record7330_010RE,
                record7330_030HF, record7330_030PE, record7330_030RE,
                record7313_010HF).doubleValue();

        List<ConsolidatedKZTForm19RecordDto> records = getConsolidatedBalanceKZTForm19LineHeaders();
        for(ConsolidatedKZTForm19RecordDto record: records){

            if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6113_030)){
                if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6113_030HF.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6113_030PE.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6113_030RE.doubleValue());
                }
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6280_010)){
                if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6280_010HF.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6280_010PE.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6280_010RE.doubleValue());
                }
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7470_010)){
                if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record7470_010HF.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record7470_010PE.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record7470_010RE.doubleValue());
                }
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_010)){
                if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6150_010HF.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6150_010PE.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6150_010RE.doubleValue());
                }
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_010)){
                if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record7330_010HF.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record7330_010PE.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record7330_010RE.doubleValue());
                }
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030)){
                if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6150_030HF.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6150_030PE.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record6150_030RE.doubleValue());
                }
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030)){
                if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record7330_030HF.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record7330_030PE.doubleValue());
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT)) {
                    record.setCurrentAccountBalance(record7330_030RE.doubleValue());
                }
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.BANK_LOANS_RECEIVED)){
                record.setCurrentAccountBalance(record7313_010HF.doubleValue());
            } else if(record.getAccountNumber() == null && record.getLineNumber() != null && (record.getLineNumber() == 9 || record.getLineNumber() == 8)){
                record.setCurrentAccountBalance(MathUtils.add(record6113_030HF, record6113_030PE, record6113_030RE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 23){
                record.setCurrentAccountBalance(MathUtils.add(record6280_010HF,record6280_010RE, record6280_010PE,
                        record7470_010HF,record7470_010PE, record7470_010RE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 24){
                record.setCurrentAccountBalance(MathUtils.add(record6280_010HF,record6280_010RE, record6280_010PE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 27){
                record.setCurrentAccountBalance(MathUtils.add(record7470_010HF,record7470_010PE, record7470_010RE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 30){
                record.setCurrentAccountBalance(MathUtils.add(record6150_010HF, record6150_010PE, record6150_010RE,
                        record7330_010HF, record7330_010PE, record7330_010RE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 31){
                record.setCurrentAccountBalance(MathUtils.add(record6150_010HF, record6150_010PE, record6150_010RE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 32){
                record.setCurrentAccountBalance(MathUtils.add(record7330_010HF, record7330_010PE, record7330_010RE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 35){
                record.setCurrentAccountBalance(MathUtils.add(record6150_030HF, record6150_030PE, record6150_030RE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 37){
                record.setCurrentAccountBalance(MathUtils.add(record7330_030HF, record7330_030PE, record7330_030RE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && (record.getLineNumber() == 44 || record.getLineNumber() == 43)){
                record.setCurrentAccountBalance(MathUtils.add(record7313_010HF, record7313_010PE, record7313_010RE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 55){
                record.setCurrentAccountBalance(sum);
            }
        }

        List<ConsolidatedKZTForm19RecordDto> recordsNoEmpty = new ArrayList<>();
        for(ConsolidatedKZTForm19RecordDto record: records){
            if(record.getAccountNumber() == null || (record.getCurrentAccountBalance() != null && record.getCurrentAccountBalance().doubleValue() != 0) ||
                    (record.getPreviousAccountBalance() != null && record.getPreviousAccountBalance().doubleValue() != 0) ||
                    (record.getTurnover() != null && record.getTurnover().doubleValue() != 0)){
                recordsNoEmpty.add(record);
            }
        }

        responseDto.setRecords(recordsNoEmpty);
        return responseDto;
    }

    @Override
    public  ListResponseDto generateConsolidatedBalanceKZTForm22(Long reportId) {
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(currentReport.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
            List<ConsolidatedKZTForm22RecordDto> records = getConsolidatedBalanceKZTForm22Saved(reportId);
            responseDto.setRecords(records);
            return responseDto;
        }else{
            ListResponseDto KZTForm22ResponseDto = generateConsolidatedKZTForm22Current(reportId);
            if(KZTForm22ResponseDto.getStatus() == ResponseStatusType.FAIL){
                return KZTForm22ResponseDto;
            }
            List<ConsolidatedKZTForm22RecordDto> currentRecords = KZTForm22ResponseDto.getRecords();

            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            List<ConsolidatedKZTForm22RecordDto> previousRecords = null;
            if(previousReport != null) {
                previousRecords = getConsolidatedBalanceKZTForm22Saved(previousReport.getId());
            }

            if(previousRecords != null && !previousRecords.isEmpty()){
                int index = 0;
                for(ConsolidatedKZTForm22RecordDto record: currentRecords){
                    boolean previousRecordFound = false;
                    for(int i = index; i < previousRecords.size(); i++){
                        if(record.getName().equalsIgnoreCase(previousRecords.get(i).getName()) && record.getLineNumber() != null &&
                                previousRecords.get(i).getLineNumber() != null && record.getLineNumber() == previousRecords.get(i).getLineNumber()){
                            if(!DateUtils.isJanuary(currentReport.getReportDate())) {
                                record.setPreviousAccountBalance(previousRecords.get(i).getCurrentAccountBalance());
                            }
                            if((record.getCurrentAccountBalance() != null && record.getCurrentAccountBalance().doubleValue() != 0.0) ||
                                    (record.getPreviousAccountBalance() != null && record.getPreviousAccountBalance().doubleValue() != 0.0)) {
                                record.setTurnover(MathUtils.subtract(record.getCurrentAccountBalance(), record.getPreviousAccountBalance()));
                            }
                            previousRecordFound = true;
                            break;
                        }
                    }
                    if(!previousRecordFound){
                        record.setTurnover(record.getCurrentAccountBalance());
                    }
                }

                // records in previous not int current
                List<ConsolidatedKZTForm22RecordDto> previousToAdd = new ArrayList<>();
                for(ConsolidatedKZTForm22RecordDto previousRecord: previousRecords){
                    boolean found = false;
                    for(ConsolidatedKZTForm22RecordDto currentRecord: currentRecords){
                        if(isMatchingRecords(previousRecord, currentRecord)){
                            found = true;
                            break;
                        }
                    }
                    if(!found && previousRecord.getCurrentAccountBalance() != null && previousRecord.getCurrentAccountBalance().doubleValue() != 0.0 &&
                            !DateUtils.isJanuary(currentReport.getReportDate())){
                        previousRecord.setPreviousAccountBalance(previousRecord.getCurrentAccountBalance());
                        previousRecord.setCurrentAccountBalance(null);
                        previousRecord.setTurnover(MathUtils.subtract(previousRecord.getCurrentAccountBalance(), previousRecord.getPreviousAccountBalance()));
                        previousToAdd.add(previousRecord);
                    }
                }

                currentRecords.addAll(previousToAdd);
                Collections.sort(currentRecords);
            }

            responseDto.setRecords(currentRecords);
            return responseDto;
        }
    }

    private ListResponseDto generateConsolidatedKZTForm22Current(Long reportId) {
        ListResponseDto responseDto = new ListResponseDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        Double averageRate = null;
        try{
            averageRate = this.currencyRatesService.getAverageRateForAllMonthsBeforeDateAndCurrency(report.getReportDate(), CurrencyLookup.USD.getCode(), 2);
            if (averageRate == null) {
                logger.error("No average currency rate found for date '" + DateUtils.getDateFormatted(report.getReportDate()));
                responseDto.setErrorMessageEn("No average currency rate found for date '" + DateUtils.getDateFormatted(report.getReportDate()) + "'");
                return responseDto;
            }
        }catch (IllegalStateException ex) {
            responseDto.setErrorMessageEn(ex.getMessage());
            return responseDto;
        }

        int index1 = 0;
        int index2 = 0;
        double record1Sum = 0;
        double record2Sum = 0;
        List<ConsolidatedKZTForm22RecordDto> records = getConsolidatedBalanceKZTForm22LineHeaders();
        for(int i = 0; i < records.size(); i++){
            ConsolidatedKZTForm22RecordDto record = records.get(i);
            if(record.getLineNumber() != null && record.getLineNumber() == 1){
                index1 = i + 1;
            }else if(record.getLineNumber() != null && record.getLineNumber() == 2){
                index2 = i + 1;
            }
        }

        List<ConsolidatedBalanceFormRecordDto> incomeExpenseUSDRecords = generateConsolidatedIncomeExpenseUSDForm(reportId);
        for(ConsolidatedBalanceFormRecordDto recordDto: incomeExpenseUSDRecords){
            if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6283_080)){
                ConsolidatedKZTForm22RecordDto newRecord = new ConsolidatedKZTForm22RecordDto(recordDto.getName(), 1);
                newRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_6283_080);
                newRecord.setCurrentAccountBalance(MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                records.add(index1, newRecord);

                index1++;
                index2++;
                record1Sum = MathUtils.add(record1Sum, newRecord.getCurrentAccountBalance());
            }else if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7473_080)){
                ConsolidatedKZTForm22RecordDto newRecord = new ConsolidatedKZTForm22RecordDto(recordDto.getName(), 2);
                newRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_7473_080);
                newRecord.setCurrentAccountBalance(MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                records.add(index2, newRecord);

                index1++;
                index2++;
                record2Sum = MathUtils.add(record2Sum, (newRecord.getCurrentAccountBalance() != null ? newRecord.getCurrentAccountBalance() : 0));
            }
        }
        List<ConsolidatedKZTForm22RecordDto> nonEmptyRecords = new ArrayList<>();
        for(ConsolidatedKZTForm22RecordDto record: records){
            if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 1){
                record.setCurrentAccountBalance(record1Sum);
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 2){
                record.setCurrentAccountBalance(record2Sum);
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 3){
                record.setCurrentAccountBalance(MathUtils.add(record1Sum, record2Sum));
            }
            if(record.getAccountNumber() != null){
                if((record.getCurrentAccountBalance() == null || record.getCurrentAccountBalance().doubleValue() == 0.0) &&
                        (record.getTurnover() == null || record.getTurnover().doubleValue() == 0.0) &&
                        (record.getPreviousAccountBalance() == null || record.getPreviousAccountBalance().doubleValue() == 0.0)){
                    // skip
                    continue;
                }
            }
            nonEmptyRecords.add(record);
        }


        List<ConsolidatedKZTForm22RecordDto> noDuplicatesRecords = new ArrayList<>();
        for(int i = 0; i < nonEmptyRecords.size(); i++){
            ConsolidatedKZTForm22RecordDto record1 = nonEmptyRecords.get(i);
            if(record1.getAccountNumber() == null){
                noDuplicatesRecords.add(record1);
                continue;
            }else {
                boolean existing = false;
                for (int j = 0; j < nonEmptyRecords.size(); j++) {
                    if(i == j){
                        // same record, skip
                        continue;
                    }
                    ConsolidatedKZTForm22RecordDto record2 = nonEmptyRecords.get(j);
                    if(record1.getAccountNumber().equalsIgnoreCase(record2.getAccountNumber()) &&
                            record1.getName().trim().equalsIgnoreCase(record2.getName().trim()) &&
                            record1.getLineNumber().intValue() == record2.getLineNumber().intValue()){
                        if(i > j){
                            existing = true;
                            break;
                        }
                        // add up
                        record1.setPreviousAccountBalance(MathUtils.add(record1.getPreviousAccountBalance(), record2.getPreviousAccountBalance()));
                        record1.setTurnover(MathUtils.add(record1.getTurnover(), record2.getTurnover()));
                        record1.setCurrentAccountBalance(MathUtils.add(record1.getCurrentAccountBalance(), record2.getCurrentAccountBalance()));
                    }
                }
                if(!existing) {
                    noDuplicatesRecords.add(record1);
                }
            }
        }

        responseDto.setRecords(noDuplicatesRecords);
        return responseDto;
    }

    // Helpers
    private Double getSumFromMap(Map<Integer, Double> sums, int from , int to){
        Double value = null;
        if(sums != null){
            for(int i = from; i <= to; i++){
                value = sums.get(i) != null ? MathUtils.add(value, sums.get(i)) : value;
            }
        }
        return value;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceUSDFormLineHeaders(){
        // TODO: get from DB ?
        List<ConsolidatedBalanceFormRecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные активы", 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("Деньги (1010-1060)", 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("Вклады размещенные (за вычетом резервов на обесценение) (1170, 1180.020, 1180.030, 1290.070)", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости через прибыль или убыток (1120, 1180.040, 1180.070)", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости через прочий совокупный доход (1140, 1180.050, 1180.080)", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по амортизированной стоимости (за вычетом резервов на обесценение) (1130, 1180.060, 1180.090, 1290.030)", 6));
        headers.add(new ConsolidatedBalanceFormRecordDto("Заемные операции (за вычетом резервов на обесценение) (1110, 1180.100, 1290.010)", 7));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные финансовые инвестиции (1150.010, 1150.140, 1150.150, 1180.110, 1180.120, 1290.130)", 8));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочная торговая и прочая дебиторская задолженность (1210, 1250, 1260, 1280.020, 1280.040, 1180.010, 1290.130, 1290.140)", 9));
        headers.add(new ConsolidatedBalanceFormRecordDto("Текущие налоговые активы (1410-1430)", 10));
        headers.add(new ConsolidatedBalanceFormRecordDto("Запасы (1310-1360)", 11));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные активы (1610-1630, 1290.130)", 12));
        headers.add(new ConsolidatedBalanceFormRecordDto("Активы (или выбывающие группы), предназначенные для продажи (1510-1520)", 13));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого краткосрочных активов (сумма строк 2-13)", 14));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные активы", 15));
        headers.add(new ConsolidatedBalanceFormRecordDto("Вклады размещенные (за вычетом резервов на обесценение) (2060, 2070.010, 1290.080)", 16));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости через прочий совокупный доход (2030, 2070.020, 2070.040)", 17));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по амортизированной стоимости (за вычетом резервов на обесценение) (2020, 2070.030, 2070.050, 1290.040)", 18));
        headers.add(new ConsolidatedBalanceFormRecordDto("Заемные операции (за вычетом резервов на обесценение) (2010, 2070.060, 1290.020)", 19));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные финансовые инвестиции (2040.100, 2070.070, 1290.130)", 20));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочная торговая и прочая дебиторская задолженность (2110, 2150, 2160, 2180, 1290.150)", 21));
        headers.add(new ConsolidatedBalanceFormRecordDto("Инвестиции в дочерние организации, ассоциированные организации и совместные организации (2210)", 22));
        headers.add(new ConsolidatedBalanceFormRecordDto("Основные средства (2410-2430)", 23));
        headers.add(new ConsolidatedBalanceFormRecordDto("Нематериальные активы (2730-2750)", 24));
        headers.add(new ConsolidatedBalanceFormRecordDto("Отложенные налоговые активы (2810)", 25));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные активы (2310-2330, 2910-2950, 1290.130)", 26));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого долгосрочных активов (сумма строк 16-26)", 27));
        headers.add(new ConsolidatedBalanceFormRecordDto("Всего активы (сумма строк 14, 27)", 28));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные обязательства", 29));
        headers.add(new ConsolidatedBalanceFormRecordDto("Займы полученные (3010, 3020, 3060.010, 3060.020)", 30));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные финансовые обязательства (3050, 3060.030-3060.050)", 31));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочная торговая и прочая кредиторская задолженность (3310, 3360, 3390.020, 3390.030)", 32));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные резервы (3410-3440)", 33));
        headers.add(new ConsolidatedBalanceFormRecordDto("Текущие налоговые обязательства (3110-3190, 3210-3250)", 34));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные обязательства (3030, 3350, 3510, 3520, 3540)", 35));
        headers.add(new ConsolidatedBalanceFormRecordDto("Обязательства выбывающих групп, предназначенных для продажи (3530)", 36));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого краткосрочных обязательств (сумма строк 30-36)", 37));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные  обязательства", 38));
        headers.add(new ConsolidatedBalanceFormRecordDto("Займы полученные (4010, 4020, 4040.010, 4040.020)", 39));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные финансовые обязательства (4030, 4040.030-4040.040)", 40));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочная торговая и прочая кредиторская задолженность (4110, 4150, 4170)", 41));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные резервы (4210-4240)", 42));
        headers.add(new ConsolidatedBalanceFormRecordDto("Отложенные налоговые обязательства (4310)", 43));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные обязательства (4410-4430, 4180)", 44));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого долгосрочных обязательств (сумма строк 39-44)", 45));
        headers.add(new ConsolidatedBalanceFormRecordDto("Капитал", 46));
        headers.add(new ConsolidatedBalanceFormRecordDto("Уставный капитал (5010-5030)", 47));
        headers.add(new ConsolidatedBalanceFormRecordDto("Дополнительно оплаченный капитал (5310, 5320)", 48));
        headers.add(new ConsolidatedBalanceFormRecordDto("Выкупленные собственные долевые инструменты (5210)", 49));
        headers.add(new ConsolidatedBalanceFormRecordDto("Резервный капитал (5410, 5460)", 50));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие резервы (5420, 5440, 5450, 5470)", 51));
        headers.add(new ConsolidatedBalanceFormRecordDto("Нераспределенная прибыль (непокрытый убыток) (5510, 5520)", 52));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого капитал (сумма строк 47-52)", 53));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.USD_FORM_1_LAST_RECORD, 54));
        return headers;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedIncomeExpenseUSDFormLineHeaders(){

        // TODO: get from DB
        List<ConsolidatedBalanceFormRecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedBalanceFormRecordDto("Выручка (6010-6030)", 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("Себестоимость реализованной продукции и услуг (7010)", 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("Валовая прибыль (убыток) (сумма строк 1, 2)", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы в виде вознаграждения по размещенным вкладам (6110.090-6110.110, 6110.220, 6110.230)", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы в виде вознаграждения по приобретенным ценным бумагам (6110.030-6110.070, 6110.170-6110.200)", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы в виде вознаграждения по заемным операциям (6110.010, 6110.020, 6110.150, 6110.160)", 6));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от купли-продажи ценных бумаг (6280.010-6280.030, 7470.010-7470.030)", 7));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, оцениваемых по справедливой стоимости через прибыль или убыток (6150.010, 7330.010)", 8));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, оцениваемых по справедливой стоимости через прочий совокупный доход (6150.020, 6150.030, 7330.020, 7330.030)", 9));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от переоценки иностранной валюты (6250, 7430)", 10));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие доходы (6210-6240, 6260, 6110.080, 6110.320, 6110.330, 6110.340, 6110.350, 6120, 6130, 6140, 6150.060, 6160.030, 6170, 6280.060-6280.080, 6280.100, 6110.280-6110.310, 6400) ", 11));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по приобретенным ценным бумагам (7310.100-7310.130)", 12));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по заемным операциям и аренде (7310.010-7310.040, 7310.080, 7310.090, 7310.190-7310.220, 7320.010)", 13));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по реализации (7110)", 14));
        headers.add(new ConsolidatedBalanceFormRecordDto("Административные расходы (7210)", 15));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие расходы (7310.050-7310.070, 7310.150-7310.160, 7310.230, 7310.240, 7310.250, 7330.060, 7340.030, 7410, 7420, 7440, 7470.060-7470.090, 7350, 7600)", 16));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) до налогообложения (сумма строк 3-16)", 17));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по подоходному налогу (7710)", 18));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) после налогообложения от продолжающейся деятельности (сумма строк 17, 18)", 19));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) от прекращенной деятельности (6310, 7510)", 20));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.USD_FORM_2_LAST_RECORD, 21));
        return headers;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedIncomeExpenseKZTForm2LineHeaders(){

        List<ConsolidatedBalanceFormRecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedBalanceFormRecordDto("Выручка (6010-6030)", 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("Себестоимость реализованной продукции и услуг (7010)", 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("Валовая прибыль (убыток) (сумма строк 1, 2)", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы в виде вознаграждения по размещенным вкладам (6110.090-6110.110, 6110.220, 6110.230)", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы в виде вознаграждения по приобретенным ценным бумагам (6110.030-6110.070, 6110.170-6110.200)", 5));

        ConsolidatedBalanceFormRecordDto record6113_030 = new ConsolidatedBalanceFormRecordDto("Доходы по вознаграждению по краткосрочным финансовым активам, оцениваемым по справедливой стоимости через прибыль или убыток", 5);
        record6113_030.setAccountNumber(PeriodicReportConstants.ACC_NUM_6113_030);
        headers.add(record6113_030);

        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы в виде вознаграждения по заемным операциям (6110.010, 6110.020, 6110.150, 6110.160)", 6));

        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от купли-продажи ценных бумаг (6280.010-6280.030, 7470.010-7470.030)", 7));

        ConsolidatedBalanceFormRecordDto record6280_010 = new ConsolidatedBalanceFormRecordDto("Доходы от покупки-продажи финансовых активов, оцениваемых по справедливой стоимости через прибыль или убыток", 7);
        record6280_010.setAccountNumber(PeriodicReportConstants.ACC_NUM_6280_010);
        headers.add(record6280_010);

        ConsolidatedBalanceFormRecordDto record7470_010 = new ConsolidatedBalanceFormRecordDto("Расходы от покупки-продажи финансовых активов, оцениваемых по справедливой стоимости через прибыль или убыток", 7);
        record7470_010.setAccountNumber(PeriodicReportConstants.ACC_NUM_7470_010);
        headers.add(record7470_010);

        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, оцениваемых по справедливой стоимости через прибыль или убыток (6150.010, 7330.010)", 8));

        ConsolidatedBalanceFormRecordDto record6150_010 = new ConsolidatedBalanceFormRecordDto("Доходы от изменения справедливой стоимости финансовых активов, оцениваемых по справедливой стоимости через прибыль или убыток", 8);
        record6150_010.setAccountNumber(PeriodicReportConstants.ACC_NUM_6150_010);
        headers.add(record6150_010);

        ConsolidatedBalanceFormRecordDto record7330_010 = new ConsolidatedBalanceFormRecordDto("Расходы от изменения справедливой стоимости финансовых активов, оцениваемых по справедливой стоимости через прибыль или убыток", 8);
        record7330_010.setAccountNumber(PeriodicReportConstants.ACC_NUM_7330_010);
        headers.add(record7330_010);

        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, оцениваемых по справедливой стоимости через прочий совокупный доход (6150.020, 6150.030, 7330.020, 7330.030)", 9));

        ConsolidatedBalanceFormRecordDto record6150_030 = new ConsolidatedBalanceFormRecordDto("Доходы от изменения справедливой стоимости долгосрочных финансовых активов, оцениваемых по справедливой стоимости через прочий совокупный доход", 9);
        record6150_030.setAccountNumber(PeriodicReportConstants.ACC_NUM_6150_030);
        headers.add(record6150_030);

        ConsolidatedBalanceFormRecordDto record7330_030 = new ConsolidatedBalanceFormRecordDto("Расходы от изменения справедливой стоимости долгосрочных финансовых активов, оцениваемых по справедливой стоимости через прочий совокупный доход", 9);
        record7330_030.setAccountNumber(PeriodicReportConstants.ACC_NUM_7330_030);
        headers.add(record7330_030);

        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от переоценки иностранной валюты (6250, 7430)", 10));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие доходы (6210-6240, 6260, 6110.080, 6110.320, 6110.330, 6110.340, 6110.350, 6120, 6130, 6140, 6150.060, 6160.030, 6170, 6280.060-6280.080, 6280.100, 6110.280-6110.310, 6400)", 11));

        ConsolidatedBalanceFormRecordDto record6283_080 = new ConsolidatedBalanceFormRecordDto("Прочие доходы", 11);
        record6283_080.setAccountNumber(PeriodicReportConstants.ACC_NUM_6283_080);
        headers.add(record6283_080);

        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по приобретенным ценным бумагам (7310.100-7310.130)", 12));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по заемным операциям и аренде (7310.010-7310.040, 7310.080, 7310.090, 7310.190-7310.220, 7320.010)", 13));

        ConsolidatedBalanceFormRecordDto record7313_010 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_7313_010, 13);
        record7313_010.setAccountNumber(PeriodicReportConstants.ACC_NUM_7313_010);
        headers.add(record7313_010);

        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по реализации (7110)", 14));
        headers.add(new ConsolidatedBalanceFormRecordDto("Административные расходы (7210)", 15));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие расходы (7310.050-7310.070, 7310.150-7310.160, 7310.230, 7310.240, 7310.250, 7330.060, 7340.030, 7410, 7420, 7440, 7470.060-7470.090, 7350, 7600)", 16));

        ConsolidatedBalanceFormRecordDto record7473_080 = new ConsolidatedBalanceFormRecordDto("Прочие расходы", 16);
        record7473_080.setAccountNumber(PeriodicReportConstants.ACC_NUM_7473_080);
        headers.add(record7473_080);

        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) до налогообложения (сумма строк 3-16)", 17));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по подоходному налогу (7710)", 18));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) после налогообложения от продолжающейся деятельности (сумма строк 17, 18)", 19));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) от прекращенной деятельности (6310, 7510)", 20));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.USD_FORM_2_LAST_RECORD, 21));
        return headers;
    }

    private List<ConsolidatedKZTForm6RecordDto> getConsolidatedBalanceKZTForm6LineHeaders(){
        List<ConsolidatedKZTForm6RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm6RecordDto("Остаток на начало текущего отчетного периода", 1));
        records.add(new ConsolidatedKZTForm6RecordDto("Изменения в учетной политике и ошибки", 2));
        records.add(new ConsolidatedKZTForm6RecordDto("Остаток на начало текущего отчетного периода (скорректированный) (сумма строк 1, 2)", 3));
        records.add(new ConsolidatedKZTForm6RecordDto("Чистая прибыль (убыток) за период", 4));
        records.add(new ConsolidatedKZTForm6RecordDto("Прочий совокупный доход за период", 5));
        records.add(new ConsolidatedKZTForm6RecordDto("Всего совокупного дохода за период (сумма строк 4, 5)", 6));
        records.add(new ConsolidatedKZTForm6RecordDto("Операции с собственниками, отраженные непосредственно в составе капитала", 7));
        records.add(new ConsolidatedKZTForm6RecordDto("Взносы в уставный капитал", 8));
        records.add(new ConsolidatedKZTForm6RecordDto("Взносы в резервный капитал", 9));
        records.add(new ConsolidatedKZTForm6RecordDto("Дополнительные взносы в капитал", 10));
        records.add(new ConsolidatedKZTForm6RecordDto("Выплата дивидендов", 11));
        records.add(new ConsolidatedKZTForm6RecordDto("Прочие операции с собственниками", 12));
        records.add(new ConsolidatedKZTForm6RecordDto("Подоходный налог по операциям с собственниками", 13));
        records.add(new ConsolidatedKZTForm6RecordDto("Операции с собственниками, отраженные непосредственно в составе капитала за период (сумма строк 8-13)", 14));
        records.add(new ConsolidatedKZTForm6RecordDto("Остаток на конец текущего отчетного периода (сумма строк 3, 6, 14)", 15));
        return records;
    }

    private List<ConsolidatedKZTForm10RecordDto> getConsolidatedBalanceKZTForm10LineHeaders(){
        List<ConsolidatedKZTForm10RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm10RecordDto("Прочие краткосрочные активы (сумма строк 2-4)", 1));
        records.add(new ConsolidatedKZTForm10RecordDto("Краткосрочные авансы выданные", 2));
        records.add(new ConsolidatedKZTForm10RecordDto(PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD, 3));
        records.add(new ConsolidatedKZTForm10RecordDto("Прочие краткосрочные активы", 4));
        records.add(new ConsolidatedKZTForm10RecordDto("Прочие долгосрочные активы (сумма строк 6-10 )", 5));
        records.add(new ConsolidatedKZTForm10RecordDto("Инвестиции в недвижимость", 6));
        records.add(new ConsolidatedKZTForm10RecordDto("Долгосрочные авансы выданные", 7));
        records.add(new ConsolidatedKZTForm10RecordDto(PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD, 8));
        records.add(new ConsolidatedKZTForm10RecordDto("Незавершенное строительство", 9));
        records.add(new ConsolidatedKZTForm10RecordDto("Прочие долгосрочные активы", 10));
        records.add(new ConsolidatedKZTForm10RecordDto("ВСЕГО  (сумма строк 1, 5)", 11));

        return records;
    }

    private List<ConsolidatedKZTForm8RecordDto> getConsolidatedBalanceKZTForm8LineHeaders(){
        List<ConsolidatedKZTForm8RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная торговая и прочая дебиторская задолженность (сумма строк 2-6)", 1));
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность покупателей и заказчиков", 2));
//        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность дочерних организаций", 3));
//        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность ассоциированных и совместных организаций", 4));
//        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность филиалов и представительств", 5));
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность работников", 3));
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность по аренде", 4));
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочные авансы выданные", 5));
        records.add(new ConsolidatedKZTForm8RecordDto("Прочая краткосрочная дебиторская задолженность", 6));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная торговая и прочая дебиторская задолженность (сумма строк 8-12)", 7));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная задолженность покупателей и заказчиков", 8));
//        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная дебиторская задолженность дочерних организаций", 12));
//        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная дебиторская задолженность ассоциированных и совместных организаций", 13));
//        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная дебиторская задолженность филиалов и представительств", 14));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная дебиторская задолженность работников", 9));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная дебиторская задолженность по аренде", 10));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочные авансы выданные", 11));
        records.add(new ConsolidatedKZTForm8RecordDto("Прочая долгосрочная дебиторская задолженность", 12));
        records.add(new ConsolidatedKZTForm8RecordDto("Всего (сумма строк 1, 7)", 13));

        return records;
    }

    private List<ConsolidatedKZTForm14RecordDto> getConsolidatedBalanceKZTForm14LineHeaders(){
        List<ConsolidatedKZTForm14RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная торговая и прочая кредиторская задолженность (сумма строк 2-5)", 1));
        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная кредиторская задолженность поставщикам и подрядчикам", 2));
//        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная кредиторская задолженность дочерним организациям", 3));
//        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная кредиторская задолженность ассоциированным и совместным организациям", 4));
//        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная кредиторская задолженность филиалам и представительствам", 5));
        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная задолженность по аренде", 3));
        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочные авансы полученные", 4));
        records.add(new ConsolidatedKZTForm14RecordDto("Прочая краткосрочная кредиторская задолженность", 5));
        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная торговая и прочая кредиторская задолженность (сумма строк 7-10)", 6));
        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная кредиторская задолженность поставщикам и подрядчикам", 7));
//        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная кредиторская задолженность дочерним организациям", 11));
//        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная кредиторская задолженность ассоциированным и совместным организациям", 12));
//        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная кредиторская задолженность филиалам и представительствам", 13));
        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная задолженность по аренде", 8));
        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочные авансы полученные", 9));
        records.add(new ConsolidatedKZTForm14RecordDto("Прочая долгосрочная кредиторская задолженность", 10));
        records.add(new ConsolidatedKZTForm14RecordDto("Всего (сумма строк 1, 6)", 11));

        return records;
    }

    private List<ConsolidatedKZTForm13RecordDto> getConsolidatedBalanceKZTForm13LineHeaders(){
        List<ConsolidatedKZTForm13RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm13RecordDto("Краткосрочные финансовые обязательства (сумма строк 2, 3)", 1));
        records.add(new ConsolidatedKZTForm13RecordDto("Займы полученные ", 2));
        records.add(new ConsolidatedKZTForm13RecordDto("Прочие краткосрочные финансовые обязательства", 3));
        records.add(new ConsolidatedKZTForm13RecordDto("Долгосрочные финансовые обязательства (сумма строк 5, 6)", 4));
        records.add(new ConsolidatedKZTForm13RecordDto("Займы полученные", 5));
        records.add(new ConsolidatedKZTForm13RecordDto("Прочие долгосрочные финансовые обязательства)", 6));
        records.add(new ConsolidatedKZTForm13RecordDto("ВСЕГО (сумма строк 1, 4)", 7));

        return records;
    }

    private List<ConsolidatedKZTForm7RecordDto> getConsolidatedBalanceKZTForm7LineHeaders(){
        List<ConsolidatedKZTForm7RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm7RecordDto("Краткосрочные финансовые инвестиции (сумма строк 2-7)", 1));
        records.add(new ConsolidatedKZTForm7RecordDto("Вклады размещенные, всего, в том числе", 2));
        records.add(new ConsolidatedKZTForm7RecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости через прибыль или убыток, всего, в том числе", 3));
        records.add(new ConsolidatedKZTForm7RecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости через прочий совокупный доход, всего, в том числе", 4));
        records.add(new ConsolidatedKZTForm7RecordDto("Финансовые инвестиции, оцениваемые по амортизированной стоимости, всего, в том числе", 5));
        records.add(new ConsolidatedKZTForm7RecordDto("Заемные операции, всего, в том числе", 6));
        records.add(new ConsolidatedKZTForm7RecordDto("Прочие краткосрочные финансовые инвестиции, всего, в том числе", 7));
        records.add(new ConsolidatedKZTForm7RecordDto("Долгосрочные финансовые инвестиции (сумма строк 9-13)", 8));
        records.add(new ConsolidatedKZTForm7RecordDto("Вклады размещенные, всего, в том числе", 9));
        records.add(new ConsolidatedKZTForm7RecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости через прочий совокупный доход, всего, в том числе", 10));
        records.add(new ConsolidatedKZTForm7RecordDto("Финансовые инвестиции, оцениваемые по амортизированной стоимости, всего, в том числе", 11));
        records.add(new ConsolidatedKZTForm7RecordDto("Заемные операции, всего, в том числе", 12));
        records.add(new ConsolidatedKZTForm7RecordDto("Прочие долгосрочные финансовые инвестиции, всего, в том числе", 13));
        records.add(new ConsolidatedKZTForm7RecordDto("ВСЕГО (сумма строк 1,8)", 14));
        return records;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceKZTForm1LineHeaders(){
// TODO: get from DB
        List<ConsolidatedBalanceFormRecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные активы", 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("Деньги (1010-1060)", 2));

        ConsolidatedBalanceFormRecordDto record2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_1033_010, 2);
        record2.setAccountNumber(PeriodicReportConstants.ACC_NUM_1033_010);
        headers.add(record2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Вклады размещенные (за вычетом резервов на обесценение) (1170, 1180.020, 1180.030, 1290.070)", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости через прибыль или убыток (1120, 1180.040, 1180.070)", 4));

        ConsolidatedBalanceFormRecordDto record4_1 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_1123_010, 4);
        record4_1.setAccountNumber(PeriodicReportConstants.ACC_NUM_1123_010);
        headers.add(record4_1);

        ConsolidatedBalanceFormRecordDto record4_2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_1123_020, 4);
        record4_2.setAccountNumber(PeriodicReportConstants.ACC_NUM_1123_020);
        headers.add(record4_2);

        ConsolidatedBalanceFormRecordDto record4_3 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_1123_030, 4);
        record4_3.setAccountNumber(PeriodicReportConstants.ACC_NUM_1123_030);
        headers.add(record4_3);

        ConsolidatedBalanceFormRecordDto record4_4 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_1183_040, 4);
        record4_4.setAccountNumber(PeriodicReportConstants.ACC_NUM_1183_040);
        headers.add(record4_4);

        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости через прочий совокупный доход (1140, 1180.050, 1180.080)", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по амортизированной стоимости (за вычетом резервов на обесценение) (1130, 1180.060, 1180.090, 1290.030)", 6));
        headers.add(new ConsolidatedBalanceFormRecordDto("Заемные операции (за вычетом резервов на обесценение) (1110, 1180.100, 1290.010)", 7));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные финансовые инвестиции (1150.010, 1150.140, 1150.150, 1180.110, 1180.120, 1290.130)", 8));

        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочная торговая и прочая дебиторская задолженность (1210, 1250, 1260, 1280.020, 1280.040, 1180.010, 1290.130, 1290.140)", 9));
        ConsolidatedBalanceFormRecordDto record8 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_1283_020, 9);
        record8.setAccountNumber(PeriodicReportConstants.ACC_NUM_1283_020);
        headers.add(record8);

        headers.add(new ConsolidatedBalanceFormRecordDto("Текущие налоговые активы (1410-1430)", 10));
        headers.add(new ConsolidatedBalanceFormRecordDto("Запасы (1310-1360)", 11));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные активы (1610-1630, 1290.130)", 12));

        ConsolidatedBalanceFormRecordDto record11 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD, 12);
        record11.setAccountNumber(PeriodicReportConstants.ACC_NUM_1623_010);
        headers.add(record11);

        headers.add(new ConsolidatedBalanceFormRecordDto("Активы (или выбывающие группы), предназначенные для продажи (1510-1520)", 13));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого краткосрочных активов (сумма строк 2-13)", 14));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные активы", 15));
        headers.add(new ConsolidatedBalanceFormRecordDto("Вклады размещенные (за вычетом резервов на обесценение) (2060, 2070.010, 1290.080)", 16));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости через прочий совокупный доход (2030, 2070.020, 2070.040)", 17));

        ConsolidatedBalanceFormRecordDto record16_1 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_2033_010, 17);
        record16_1.setAccountNumber(PeriodicReportConstants.ACC_NUM_2033_010);
        headers.add(record16_1);

        ConsolidatedBalanceFormRecordDto record16_2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_2033_040, 17);
        record16_2.setAccountNumber(PeriodicReportConstants.ACC_NUM_2033_040);
        headers.add(record16_2);

        ConsolidatedBalanceFormRecordDto record16_3 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_2033_050, 17);
        record16_3.setAccountNumber("2033.050");
        headers.add(record16_3);

        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по амортизированной стоимости (за вычетом резервов на обесценение) (2020, 2070.030, 2070.050, 1290.040)", 18));
        headers.add(new ConsolidatedBalanceFormRecordDto("Заемные операции (за вычетом резервов на обесценение) (2010, 2070.060, 1290.020)", 19));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные финансовые инвестиции (2040.100, 2070.070, 1290.130)", 20));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочная торговая и прочая дебиторская задолженность (2110, 2150, 2160, 2180, 1290.150)", 21));
        headers.add(new ConsolidatedBalanceFormRecordDto("Инвестиции в дочерние организации, ассоциированные организации и совместные организации (2210)", 22));
        headers.add(new ConsolidatedBalanceFormRecordDto("Основные средства (2410-2430)", 23));
        headers.add(new ConsolidatedBalanceFormRecordDto("Нематериальные активы (2730-2750)", 24));
        headers.add(new ConsolidatedBalanceFormRecordDto("Отложенные налоговые активы (2810)", 25));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные активы (2310-2330, 2910-2950, 1290.130)", 26));

        ConsolidatedBalanceFormRecordDto record24 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD, 26);
        record24.setAccountNumber(PeriodicReportConstants.ACC_NUM_2923_010);
        headers.add(record24);

        headers.add(new ConsolidatedBalanceFormRecordDto("Итого долгосрочных активов (сумма строк 16-26)", 27));
        headers.add(new ConsolidatedBalanceFormRecordDto("Всего активы (сумма строк 14, 27)", 28));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные обязательства", 29));
        headers.add(new ConsolidatedBalanceFormRecordDto("Займы полученные (3010, 3020, 3060.010, 3060.020)", 30));

        ConsolidatedBalanceFormRecordDto record28_1 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_3013_010, 30);
        record28_1.setAccountNumber(PeriodicReportConstants.ACC_NUM_3013_010);
        headers.add(record28_1);

        ConsolidatedBalanceFormRecordDto record28_2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_3063_010, 30);
        record28_2.setAccountNumber(PeriodicReportConstants.ACC_NUM_3063_010);
        headers.add(record28_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные финансовые обязательства (3050, 3060.030-3060.050)", 31));

        ConsolidatedBalanceFormRecordDto record29 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_3053_060, 31);
        record29.setAccountNumber(PeriodicReportConstants.ACC_NUM_3053_060);
        headers.add(record29);

        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочная торговая и прочая кредиторская задолженность (3310, 3360, 3390.020, 3390.030)", 32));

        ConsolidatedBalanceFormRecordDto record30 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_3393_020, 32);
        record30.setAccountNumber(PeriodicReportConstants.ACC_NUM_3393_020);
        headers.add(record30);


        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные резервы (3410-3440)", 33));
        headers.add(new ConsolidatedBalanceFormRecordDto("Текущие налоговые обязательства (3110-3190, 3210-3250)", 34));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные обязательства (3030, 3350, 3510, 3520, 3540)", 35));
        headers.add(new ConsolidatedBalanceFormRecordDto("Обязательства выбывающих групп, предназначенных для продажи (3530)", 36));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого краткосрочных обязательств (сумма строк 30-36)", 37));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные  обязательства", 38));
        headers.add(new ConsolidatedBalanceFormRecordDto("Займы полученные (4010, 4020, 4040.010, 4040.020)", 39));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные финансовые обязательства (4030, 4040.030-4040.040)", 40));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочная торговая и прочая кредиторская задолженность (4110, 4150, 4170)", 41));

        ConsolidatedBalanceFormRecordDto record39 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_4173_010, 41);
        record39.setAccountNumber(PeriodicReportConstants.ACC_NUM_4173_010);
        headers.add(record39);

        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные резервы (4210-4240)", 42));
        headers.add(new ConsolidatedBalanceFormRecordDto("Отложенные налоговые обязательства (4310)", 43));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные обязательства (4410-4430, 4180)", 44));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого долгосрочных обязательств (сумма строк 39-44)", 45));
        headers.add(new ConsolidatedBalanceFormRecordDto("Капитал", 46));
        headers.add(new ConsolidatedBalanceFormRecordDto("Уставный капитал (5010-5030)", 47));

        ConsolidatedBalanceFormRecordDto record45_1 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.COMMON_SHARES, 47);
        record45_1.setAccountNumber(PeriodicReportConstants.ACC_NUM_5021_010);
        headers.add(record45_1);

        ConsolidatedBalanceFormRecordDto record45_2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.COMMON_SHARES, 47);
        record45_2.setAccountNumber(PeriodicReportConstants.ACC_NUM_5022_010);
        headers.add(record45_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Дополнительно оплаченный капитал (5310, 5320)", 48));
        headers.add(new ConsolidatedBalanceFormRecordDto("Выкупленные собственные долевые инструменты (5210)", 49));
        headers.add(new ConsolidatedBalanceFormRecordDto("Резервный капитал (5410, 5460)", 50));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие резервы (5420, 5440, 5450, 5470)", 51));

        ConsolidatedBalanceFormRecordDto record49_1 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_5440_010_b, 51);
        record49_1.setAccountNumber(PeriodicReportConstants.ACC_NUM_5440_010);
        headers.add(record49_1);

        ConsolidatedBalanceFormRecordDto record49_2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_5450_010, 51);
        record49_2.setAccountNumber(PeriodicReportConstants.ACC_NUM_5450_010);
        headers.add(record49_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Нераспределенная прибыль (непокрытый убыток) (5510, 5520)", 52));


        ConsolidatedBalanceFormRecordDto record50_1 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_5510_010, 52);
        record50_1.setAccountNumber(PeriodicReportConstants.ACC_NUM_5510_010);
        headers.add(record50_1);

        ConsolidatedBalanceFormRecordDto record50_2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_5520_010, 52);
        record50_2.setAccountNumber(PeriodicReportConstants.ACC_NUM_5520_010);
        headers.add(record50_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Итого капитал (сумма строк 47-52)", 53));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.USD_FORM_1_LAST_RECORD, 54));
        return headers;
    }

    private List<ConsolidatedKZTForm19RecordDto> getConsolidatedBalanceKZTForm19LineHeaders(){
        List<ConsolidatedKZTForm19RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы в виде вознаграждения по размещенным вкладам (сумма строк 2-7)", 1));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по вкладам до востребования", 2));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по краткосрочным срочным вкладам", 3));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по долгосрочным срочным вкладам", 4));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по краткосрочным прочим вкладам", 5));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по краткосрочным срочным вкладам", 6));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по долгосрочным срочным вкладам", 7));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы в виде вознаграждения по приобретенным ценным бумагам (сумма строк 9-17)", 8));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по краткосрочным финансовым инвестициям, оцениваемым по справедливой стоимости через прибыль или убыток", 9));

        ConsolidatedKZTForm19RecordDto record6113_030RE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT, 9);
        record6113_030RE.setAccountNumber(PeriodicReportConstants.ACC_NUM_6113_030);
        record6113_030RE.setOtherEntityName(PeriodicReportConstants.TERRA_LOWER_CASE);
        records.add(record6113_030RE);

        ConsolidatedKZTForm19RecordDto record6113_030PE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_PE_FUND_INVESTMENT, 9);
        record6113_030PE.setAccountNumber(PeriodicReportConstants.ACC_NUM_6113_030);
        record6113_030PE.setOtherEntityName(PeriodicReportConstants.TARRAGON_LOWER_CASE);
        records.add(record6113_030PE);

        ConsolidatedKZTForm19RecordDto record6113_030HF = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT, 9);
        record6113_030HF.setAccountNumber(PeriodicReportConstants.ACC_NUM_6113_030);
        record6113_030HF.setOtherEntityName(PeriodicReportConstants.SINGULARITY_LOWER_CASE);
        records.add(record6113_030HF);

        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по краткосрочным финансовым инвестициям, оцениваемым по амортизированной стоимости", 10));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по долгосрочным финансовым инвестициям, оцениваемым по амортизированной стоимости", 11));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по краткосрочным финансовым инвестициям, оцениваемым по справедливой стоимости через прочий совокупный доход", 12));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по долгосрочным финансовым инвестициям, оцениваемым по справедливой стоимости через прочий совокупный доход", 13));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по краткосрочным финансовым инвестициям, оцениваемым по амортизированной стоимости", 14));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по долгосрочным финансовым инвестициям, оцениваемым по амортизированной стоимости", 15));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по краткосрочным финансовым инвестициям, оцениваемым по справедливой стоимости через прочий совокупный доход", 16));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по долгосрочным финансовым инвестициям, оцениваемым по справедливой стоимости через прочий совокупный доход", 17));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы в виде вознаграждения по заемным операциям (сумма строк 19-22)", 18));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждению по краткосрочным заемным операциям", 19));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждению по долгосрочным заемным операциям", 20));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по краткосрочным заемным операциям", 21));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по долгосрочным заемным операциям", 22));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы (расходы) от купли-продажи ценных бумаг (сумма строк 24-29)", 23));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от покупки-продажи финансовых инвестиций, оцениваемых по справедливой стоимости через прибыль или убыток", 24));

        ConsolidatedKZTForm19RecordDto record6280_010RE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT, 24);
        record6280_010RE.setAccountNumber(PeriodicReportConstants.ACC_NUM_6280_010);
        record6280_010RE.setOtherEntityName(PeriodicReportConstants.TERRA_LOWER_CASE);
        records.add(record6280_010RE);

        ConsolidatedKZTForm19RecordDto record6280_010PE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_PE_FUND_INVESTMENT, 24);
        record6280_010PE.setAccountNumber(PeriodicReportConstants.ACC_NUM_6280_010);
        record6280_010PE.setOtherEntityName(PeriodicReportConstants.TARRAGON_LOWER_CASE);
        records.add(record6280_010PE);

        ConsolidatedKZTForm19RecordDto record6280_010HF = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT,  24);
        record6280_010HF.setAccountNumber(PeriodicReportConstants.ACC_NUM_6280_010);
        record6280_010HF.setOtherEntityName(PeriodicReportConstants.SINGULARITY_LOWER_CASE);
        records.add(record6280_010HF);


        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от покупки-продажи краткосрочных финансовых инвестиций, оцениваемых по справедливой стоимости через прочий совокупный доход", 25));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от покупки-продажи долгосрочных финансовых инвестиций, оцениваемых по справедливой стоимости через прочий совокупный доход", 26));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от покупки-продажи финансовых инвестиций, оцениваемых по справедливой стоимости через прибыль или убыток", 27));

        ConsolidatedKZTForm19RecordDto record7470_010RE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT, 27);
        record7470_010RE.setAccountNumber(PeriodicReportConstants.ACC_NUM_7470_010);
        record7470_010RE.setOtherEntityName(PeriodicReportConstants.TERRA_LOWER_CASE);
        records.add(record7470_010RE);

        ConsolidatedKZTForm19RecordDto record7470_010PE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_PE_FUND_INVESTMENT, 27);
        record7470_010PE.setAccountNumber(PeriodicReportConstants.ACC_NUM_7470_010);
        record7470_010PE.setOtherEntityName(PeriodicReportConstants.TARRAGON_LOWER_CASE);
        records.add(record7470_010PE);

        ConsolidatedKZTForm19RecordDto record7470_010HF = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT, 27);
        record7470_010HF.setAccountNumber(PeriodicReportConstants.ACC_NUM_7470_010);
        record7470_010HF.setOtherEntityName(PeriodicReportConstants.SINGULARITY_LOWER_CASE);
        records.add(record7470_010HF);

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от покупки-продажи краткосрочных финансовых инвестиций, оцениваемых по справедливой стоимости через прочий совокупный доход", 28));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от покупки-продажи долгосрочных финансовых инвестиций, оцениваемых по справедливой стоимости через прочий совокупный доход", 29));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, оцениваемых по справедливой стоимости через прибыль или убыток (сумма строк 31,32)", 30));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от изменения справедливой стоимости финансовых инвестиций, оцениваемых по справедливой стоимости через прибыль или убыток", 31));

        ConsolidatedKZTForm19RecordDto record6150_010RE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT, 31);
        record6150_010RE.setAccountNumber(PeriodicReportConstants.ACC_NUM_6150_010);
        record6150_010RE.setOtherEntityName(PeriodicReportConstants.TERRA_LOWER_CASE);
        records.add(record6150_010RE);

        ConsolidatedKZTForm19RecordDto record6150_010PE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_PE_FUND_INVESTMENT, 31);
        record6150_010PE.setAccountNumber(PeriodicReportConstants.ACC_NUM_6150_010);
        record6150_010PE.setOtherEntityName(PeriodicReportConstants.TARRAGON_LOWER_CASE);
        records.add(record6150_010PE);

        ConsolidatedKZTForm19RecordDto record6150_010HF = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT, 31);
        record6150_010HF.setAccountNumber(PeriodicReportConstants.ACC_NUM_6150_010);
        record6150_010HF.setOtherEntityName(PeriodicReportConstants.SINGULARITY_LOWER_CASE);
        records.add(record6150_010HF);

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от изменения справедливой стоимости финансовых инвестиций, оцениваемых по справедливой стоимости через прибыль или убыток", 32));

        ConsolidatedKZTForm19RecordDto record7330_010RE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT, 32);
        record7330_010RE.setAccountNumber(PeriodicReportConstants.ACC_NUM_7330_010);
        record7330_010RE.setOtherEntityName(PeriodicReportConstants.TERRA_LOWER_CASE);
        records.add(record7330_010RE);

        ConsolidatedKZTForm19RecordDto record7330_010PE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_PE_FUND_INVESTMENT, 32);
        record7330_010PE.setAccountNumber(PeriodicReportConstants.ACC_NUM_7330_010);
        record7330_010PE.setOtherEntityName(PeriodicReportConstants.TARRAGON_LOWER_CASE);
        records.add(record7330_010PE);

        ConsolidatedKZTForm19RecordDto record7330_010HF = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT, 32);
        record7330_010HF.setAccountNumber(PeriodicReportConstants.ACC_NUM_7330_010);
        record7330_010HF.setOtherEntityName(PeriodicReportConstants.SINGULARITY_LOWER_CASE);
        records.add(record7330_010HF);

        records.add(new ConsolidatedKZTForm19RecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, оцениваемых по справедливой стоимости через прочий совокупный доход (сумма строк 34-37)", 33));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от изменения справедливой стоимости краткосрочных финансовых инвестиций, оцениваемых по справедливой стоимости через прочий совокупный доход", 34));

        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, оцениваемых по справедливой стоимости через прочий совокупный доход", 35));

        ConsolidatedKZTForm19RecordDto record6150_030HF = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT, 35);
        record6150_030HF.setAccountNumber(PeriodicReportConstants.ACC_NUM_6150_030);
        record6150_030HF.setOtherEntityName(PeriodicReportConstants.SINGULARITY_LOWER_CASE);
        records.add(record6150_030HF);

        ConsolidatedKZTForm19RecordDto record6150_030PE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_PE_FUND_INVESTMENT, 35);
        record6150_030PE.setAccountNumber(PeriodicReportConstants.ACC_NUM_6150_030);
        record6150_030PE.setOtherEntityName(PeriodicReportConstants.TARRAGON_LOWER_CASE);
        records.add(record6150_030PE);

        ConsolidatedKZTForm19RecordDto record6150_030RE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT, 35);
        record6150_030RE.setAccountNumber(PeriodicReportConstants.ACC_NUM_6150_030);
        record6150_030RE.setOtherEntityName(PeriodicReportConstants.TERRA_LOWER_CASE);
        records.add(record6150_030RE);

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от изменения справедливой стоимости краткосрочных финансовых инвестиций, оцениваемых по справедливой стоимости через прочий совокупный доход", 36));

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, оцениваемых по справедливой стоимости через прочий совокупный доход", 37));

        ConsolidatedKZTForm19RecordDto record7330_030HF = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT, 37);
        record7330_030HF.setAccountNumber(PeriodicReportConstants.ACC_NUM_7330_030);
        record7330_030HF.setOtherEntityName(PeriodicReportConstants.SINGULARITY_LOWER_CASE);
        records.add(record7330_030HF);

        ConsolidatedKZTForm19RecordDto record7330_030PE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_PE_FUND_INVESTMENT, 37);
        record7330_030PE.setAccountNumber(PeriodicReportConstants.ACC_NUM_7330_030);
        record7330_030PE.setOtherEntityName(PeriodicReportConstants.TARRAGON_LOWER_CASE);
        records.add(record7330_030PE);

        ConsolidatedKZTForm19RecordDto record7330_030RE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_REAL_ESTATE_FUND_INVESTMENT, 37);
        record7330_030RE.setAccountNumber(PeriodicReportConstants.ACC_NUM_7330_030);
        record7330_030RE.setOtherEntityName(PeriodicReportConstants.TERRA_LOWER_CASE);
        records.add(record7330_030RE);

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы в виде вознаграждения по приобретенным ценным бумагам (сумма строк 39-42) ", 38));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным краткосрочным финансовым инвестициям, оцениваемым по амортизированной стоимости", 39));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным долгосрочным финансовым инвестициям, оцениваемым по амортизированной стоимости", 40));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным краткосрочным финансовым инвестициям, оцениваемым по справедливой стоимости через прочий совокупный доход", 41));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным долгосрочным финансовым инвестициям, оцениваемым по справедливой стоимости через прочий совокупный доход", 42));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы в виде вознаграждения по заемным операциям и аренде (сумма строк 44-54)", 43));

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по вознаграждениям по краткосрочным банковским займам", 44));
        ConsolidatedKZTForm19RecordDto record7313_010 = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.BANK_LOANS_RECEIVED, 44);
        record7313_010.setAccountNumber(PeriodicReportConstants.ACC_NUM_7313_010);
        record7313_010.setOtherEntityName("Bank of Monreal");
        records.add(record7313_010);

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по вознаграждениям по долгосрочным банковским займам", 45));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по вознаграждениям по краткосрочным займам, полученным от юридических лиц за исключением банков второго уровня", 46));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по вознаграждениям по долгосрочным займам, полученным от юридических лиц за исключением банков второго уровня", 47));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по краткосрочным заемным операциям", 48));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по долгосрочным заемным операциям", 49));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации дисконта по краткосрочным банковским займам полученным", 50));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации дисконта по долгосрочным банковским займам полученным", 51));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации дисконта по краткосрочным займам, полученным от юридических лиц за исключением банков второго уровня", 52));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации дисконта по долгосрочным займам, полученным от юридических лиц за исключением банков второго уровня", 53));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы на выплату процентов по аренде", 54));
        records.add(new ConsolidatedKZTForm19RecordDto("Всего (сумма строк 1, 8, 18, 23, 30, 33, 38 и 43)", 55));

        return records;
    }

    private List<ConsolidatedKZTForm22RecordDto> getConsolidatedBalanceKZTForm22LineHeaders(){
        List<ConsolidatedKZTForm22RecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedKZTForm22RecordDto("Прочие доходы", 1));
        headers.add(new ConsolidatedKZTForm22RecordDto("Прочие расходы", 2));
        headers.add(new ConsolidatedKZTForm22RecordDto("Всего (сумма строк 1, 2)", 3));

        return headers;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedTotalIncomeKZTForm3LineHeaders(){
        List<ConsolidatedBalanceFormRecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedBalanceFormRecordDto("Чистая прибыль (убыток)", 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочий совокупный доход за вычетом подоходного налога", 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("Статьи, которые реклассифицированы или могут быть впоследствии реклассифицированы в состав прибыли или убытка:", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Чистое изменение справедливой стоимости финансовых активов, учитываемых по справедливой стоимости через прочий совокупный доход", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("Чистое изменение справедливой стоимости финансовых активов, учитываемых по справедливой стоимости через прочий совокупный доход, перенесенное в состав прибыли или убытка", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto("Пересчет иностранной валюты по зарубежной деятельности", 6));
        headers.add(new ConsolidatedBalanceFormRecordDto("Всего статей, которые реклассифицированы или могут быть впоследствии реклассифицированы в состав прибыли или убытка (сумма строк 4-6)", 7));
        headers.add(new ConsolidatedBalanceFormRecordDto("Статьи, которые не могут быть впоследствии реклассифицированы в состав прибыли или убытка:", 8));
        headers.add(new ConsolidatedBalanceFormRecordDto("Переоценка основных средств", 9));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыли и убытки от инвестиций в долевые финансовые инструменты, учитываемые по справедливой стоимости через прочий совокупный доход", 10));
        headers.add(new ConsolidatedBalanceFormRecordDto("Всего статей, которые не могут быть впоследствии реклассифицированы в состав прибыли или убытка (сумма строк 9, 10)", 11));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочий совокупный доход за вычетом подоходного налога (сумма строк 7, 11)", 12));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.KZT_FORM_3_LAST_RECORD, 13));

//        headers.add(new ConsolidatedBalanceFormRecordDto("Чистая прибыль (убыток)", 1));
//        headers.add(new ConsolidatedBalanceFormRecordDto("Прочий совокупный доход", 2));
//        headers.add(new ConsolidatedBalanceFormRecordDto("Резерв по переоценке финансовых активов, имеющихся в наличии для продажи всего (сумма строк 3.1-3.3)", 3));
//        headers.add(new ConsolidatedBalanceFormRecordDto("- чистое изменение справедливой стоимости", 3, 1));
//        headers.add(new ConsolidatedBalanceFormRecordDto("- чистое изменение справедливой стоимости, перенесенное в состав прибыли или убытка", 3, 2));
//        headers.add(new ConsolidatedBalanceFormRecordDto("- обесценение, перенесенное в состав прибыли или убытка ", 3, 3));
//        headers.add(new ConsolidatedBalanceFormRecordDto("Переоценка основных средств всего (сумма строк 4.1-4.3)", 4));
//        headers.add(new ConsolidatedBalanceFormRecordDto("- изменение стоимости от переоценки", 4, 1));
//        headers.add(new ConsolidatedBalanceFormRecordDto("- перенос переоценки основных средств при амортизации", 4, 2));
//        headers.add(new ConsolidatedBalanceFormRecordDto("- перенос переоценки основных средств при выбытии", 4, 3));
//        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие операции (сумма строк 5.1-5.2)", 5));
//        headers.add(new ConsolidatedBalanceFormRecordDto("- резерв на пересчет иностранной валюты по зарубежной деятельности", 5, 1));
//        headers.add(new ConsolidatedBalanceFormRecordDto("- прочие", 5, 2));
//        headers.add(new ConsolidatedBalanceFormRecordDto("Прочий совокупный доход (сумма строк 3, 4, 5)", 6));
//        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.KZT_FORM_3_LAST_RECORD, 7));

        return headers;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedTotalIncomeUSDFormLineHeaders(){
        // TODO: get from DB
        List<ConsolidatedBalanceFormRecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedBalanceFormRecordDto("Чистая прибыль (убыток)", 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочий совокупный доход за вычетом подоходного налога", 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("Статьи, которые реклассифицированы или могут быть впоследствии реклассифицированы в состав прибыли или убытка:", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Чистое изменение справедливой стоимости финансовых активов, учитываемых по справедливой стоимости через прочий совокупный доход", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("Чистое изменение справедливой стоимости финансовых активов, учитываемых по справедливой стоимости через прочий совокупный доход, перенесенное в состав прибыли или убытка", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto("Пересчет иностранной валюты по зарубежной деятельности", 6));
        headers.add(new ConsolidatedBalanceFormRecordDto("Всего статей, которые реклассифицированы или могут быть впоследствии реклассифицированы в состав прибыли или убытка (сумма строк 4-6)", 7));
        headers.add(new ConsolidatedBalanceFormRecordDto("Статьи, которые не могут быть впоследствии реклассифицированы в состав прибыли или убытка:", 8));
        headers.add(new ConsolidatedBalanceFormRecordDto("Переоценка основных средств", 9));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыли и убытки от инвестиций в долевые финансовые инструменты, учитываемые по справедливой стоимости через прочий совокупный доход", 10));
        headers.add(new ConsolidatedBalanceFormRecordDto("Всего статей, которые не могут быть впоследствии реклассифицированы в состав прибыли или убытка (сумма строк 9, 10)", 11));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочий совокупный доход за вычетом подоходного налога (сумма строк 7, 11)", 12));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.USD_FORM_3_LAST_RECORD, 13));

        return headers;
    }

    private void setConsolidatedBalanceUSDFormHeaderSumsAndClearOtherEntityName(List<ConsolidatedBalanceFormRecordDto> records, Map<Integer, Double> sums){
        if(records != null && sums != null) {
            for (int i = 0; i < records.size(); i++) {
                ConsolidatedBalanceFormRecordDto record = records.get(i);
                if (record.getAccountNumber() == null) {
                    if (record.getLineNumber() == 1) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(2), sums.get(4), sums.get(9), sums.get(12)));
                        sums.put(1, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 14) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(2), sums.get(3), sums.get(4), sums.get(5), sums.get(6),
                                sums.get(7), sums.get(8), sums.get(9), sums.get(10), sums.get(11), sums.get(12), sums.get(13)));
                        sums.put(14, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 15) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(16), sums.get(24)));
                        sums.put(15, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 27) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(16), sums.get(17), sums.get(18), sums.get(19),
                                sums.get(20), sums.get(21), sums.get(22), sums.get(23), sums.get(24), sums.get(26), sums.get(26)));
                        sums.put(27, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 28) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(14), sums.get(27)));
                        sums.put(28, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 37) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(30), sums.get(31), sums.get(32),
                                sums.get(33), sums.get(34), sums.get(35), sums.get(36)));
                        sums.put(37, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 45) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(39), sums.get(40), sums.get(41), sums.get(42), sums.get(43), sums.get(44)));
                        sums.put(45, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 53) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(47), sums.get(48), sums.get(49), sums.get(50), sums.get(51), sums.get(52)));
                        sums.put(53, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 54) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(37), sums.get(45), sums.get(53)));
                        sums.put(54, record.getCurrentAccountBalance());
                    }

                    if(record.getCurrentAccountBalance() == 0.0){
                        record.setCurrentAccountBalance(null);
                    }
                }else{
                    if(record.getLineNumber() != 29 && record.getLineNumber() != 17 && record.getLineNumber() != 4 && record.getLineNumber() != 9){
                        record.setOtherEntityName(null);
                    }
                }
            }
        }
    }

    private void setConsolidatedIncomeExpenseUSDFormHeaderSumsAndClearOtherEntityName(List<ConsolidatedBalanceFormRecordDto> records, Map<Integer, Double> sums){
        if(records != null && sums != null) {
            for (int i = 0; i < records.size(); i++) {
                ConsolidatedBalanceFormRecordDto record = records.get(i);
                if (record.getAccountNumber() == null) {
                    if (record.getLineNumber() == 17) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(3), sums.get(4), sums.get(5), sums.get(6), sums.get(7) +
                                sums.get(8), sums.get(9), sums.get(10), sums.get(11), sums.get(12), sums.get(13), sums.get(14), sums.get(15), sums.get(16)));
                        sums.put(17, record.getCurrentAccountBalance());
                    }else if (record.getLineNumber() == 19) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(17), sums.get(18)));
                        sums.put(19, record.getCurrentAccountBalance());
                    }else if (record.getLineNumber() == 21) {
                        record.setCurrentAccountBalance(MathUtils.add(sums.get(19), sums.get(20)));
                        sums.put(21, record.getCurrentAccountBalance());
                    }

                    if(record.getCurrentAccountBalance() != null && record.getCurrentAccountBalance().doubleValue() == 0.0){
                        record.setCurrentAccountBalance(null);
                    }
                }else{
                    if(record.getLineNumber() != 6 && record.getLineNumber() != 7 && record.getLineNumber() != 8 &&
                            record.getLineNumber() != 9 && record.getLineNumber() != 13){
                        record.setOtherEntityName(null);
                    }
                }
            }
        }
    }

    private List<ConsolidatedBalanceFormRecordDto> setConsolidatedBalanceUSDFormAdditionalHeaders(List<ConsolidatedBalanceFormRecordDto> records){
        List<ConsolidatedBalanceFormRecordDto> newRecords = new ArrayList<>();
        boolean header1283020aAdded = false;
        ConsolidatedBalanceFormRecordDto header1283020aRecord = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_INVESTMENTS_TO_RETURN, 9);
        //header1283020aRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_1283_020);
        ConsolidatedBalanceFormRecordDto header1283020bRecord = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_PRE_SUBSCRIPTION, 9);
        //header1283020bRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_1283_020);
        boolean header1283020bAdded = false;
        if(records != null) {
            for (int i = 0; i < records.size(); i++) {
                ConsolidatedBalanceFormRecordDto record = records.get(i);
                if (record.getLineNumber() == 9 && record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1283_020)) {
                    if (record.getName().startsWith(PeriodicReportConstants.RU_INVESTMENTS_TO_RETURN)) {
                        if (!header1283020aAdded) {
                            header1283020aRecord.setCurrentAccountBalance(record.getCurrentAccountBalance());
                            newRecords.add(header1283020aRecord);
                            header1283020aAdded = true;
                        } else {
                            header1283020aRecord.setCurrentAccountBalance(MathUtils.add(header1283020aRecord.getCurrentAccountBalance(), record.getCurrentAccountBalance()));
                        }
                    }
                    if (record.getName().startsWith(PeriodicReportConstants.RU_PRE_SUBSCRIPTION)) {
                        if (!header1283020bAdded) {
                            header1283020bRecord.setCurrentAccountBalance(record.getCurrentAccountBalance());
                            newRecords.add(header1283020bRecord);
                            header1283020bAdded = true;
                        } else {
                            header1283020bRecord.setCurrentAccountBalance(MathUtils.add(header1283020bRecord.getCurrentAccountBalance(), record.getCurrentAccountBalance())) ;
                        }
                    }
                }

                if(record.getAccountNumber() != null){
                    //record.setLineNumber(null);
                }
                newRecords.add(record);
            }
        }

        // if 1283.020 headers are missing
        if(!header1283020aAdded || !header1283020bAdded){
            int index = 0;
            for(int i = 0; i < newRecords.size(); i++){
                ConsolidatedBalanceFormRecordDto record = newRecords.get(i);
                if(record.getLineNumber() != null && record.getLineNumber() == 10){
                    index = i;
                    break;
                }
            }
            if(index > 0){
                if(!header1283020aAdded){
                    header1283020aRecord.setCurrentAccountBalance(0.0);
                    newRecords.add(index, header1283020aRecord);
                    index++;
                }
                if(!header1283020bAdded){
                    header1283020bRecord.setCurrentAccountBalance(0.0);
                    newRecords.add(index, header1283020bRecord);
                }
            }
        }

        return newRecords;
    }

    private List<ConsolidatedBalanceFormRecordDto> setConsolidatedIncomeExpenseUSDFormAdditionalHeadersAndClearLineNumbers(List<ConsolidatedBalanceFormRecordDto> records){
        List<ConsolidatedBalanceFormRecordDto> newRecords = new ArrayList<>();
        boolean header6150030Added = false;
        boolean header7330030Added = false;
        boolean header7313010Added = false;
        ConsolidatedBalanceFormRecordDto header6150030Record =
                new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.INCOME_FAIR_VALUE_CHANGES, 9);
        ConsolidatedBalanceFormRecordDto header7330030Record =
                new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.EXPENSE_FAIR_VALUE_CHANGES, 9);
        ConsolidatedBalanceFormRecordDto header7313010Record =
                new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_7313_010, 13);
        if(records != null) {
            for (int i = 0; i < records.size(); i++) {
                ConsolidatedBalanceFormRecordDto record = records.get(i);
                if (record.getLineNumber() == 9 && record.getAccountNumber() != null /* && record.getName().startsWith("Реализованные доходы/расходы по инвестициям")*/) {
                    if (record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030)){
                        if (!header6150030Added) {
                            header6150030Record.setCurrentAccountBalance(record.getCurrentAccountBalance());
                            newRecords.add(header6150030Record);
                            header6150030Added = true;
                        } else {
                            header6150030Record.setCurrentAccountBalance(header6150030Record.getCurrentAccountBalance() + record.getCurrentAccountBalance());
                        }
                    }else if (record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030)) {
                        if (!header7330030Added) {
                            header7330030Record.setCurrentAccountBalance(record.getCurrentAccountBalance());
                            newRecords.add(header7330030Record);
                            header7330030Added = true;
                        } else {
                            header7330030Record.setCurrentAccountBalance(header7330030Record.getCurrentAccountBalance() + record.getCurrentAccountBalance());
                        }
                    }
                }else if (record.getLineNumber() == 13 && record.getAccountNumber() != null /* && record.getName().startsWith("Реализованные доходы/расходы по инвестициям")*/) {
                    if (record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010)){
                        if (!header7313010Added) {
                            header7313010Record.setCurrentAccountBalance(record.getCurrentAccountBalance());
                            newRecords.add(header7313010Record);
                            header7313010Added = true;
                        } else {
                            header7313010Record.setCurrentAccountBalance(header7313010Record.getCurrentAccountBalance() + record.getCurrentAccountBalance());
                        }
                    }
                }

                // clear line number for non-headers
                if(record.getAccountNumber() != null){
                    //record.setLineNumber(null);
                }
                newRecords.add(record);
            }
        }

        return newRecords;
    }

    private int getConsolidatedBalanceUSDFormLineNumberByAccountNumber(String accountNumber){

        // TODO: load from DB
        if(accountNumber == null){
            return 0;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1033_010)){
            return 2;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010)){
            return 4;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1183_040)){
            return 4;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1283_020)){
            return 9;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1623_010)) {
            return 12;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2033_010)){
            return 16;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2923_010)){
            return 24;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3013_010) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3383_010)){
            //return 28;
            return 30;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060)){
            return 29;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3393_020)){
            return 32;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_4173_010)){
            return 41;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5021_010) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5022_010)){
            return 47;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010)){
            return 49;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5510_010) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5520_010)){
            return 52;
        }

        return 0;
    }

    private int getConsolidatedIncomeExpenseUSDFormLineNumberByAccountNumber(String accountNumber) {

        // TODO: load from DB

        if(accountNumber == null){
            return 0;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6113_030)){
            return 6;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6280_010) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7470_010)) {
            return 7;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_010) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_010)) {
            return 8;
        } else if (accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_020) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030) ||
                accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_020) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030)) {
            return 9;
        } else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6283_080)){
            return 11;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010)){
            return 13;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7473_080)){
            return 16;
        }

        return 0;
    }

    private Map<Integer, List<ConsolidatedBalanceFormRecordDto>> getConsolidatedBalanceUSDFormMap(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        ListResponseDto balanceUSDResponseDto = generateConsolidatedBalanceUSDForm(reportId);
        if(balanceUSDResponseDto.getStatus() == ResponseStatusType.FAIL){

            // TODO: handle error

        }
        List<ConsolidatedBalanceFormRecordDto> records = balanceUSDResponseDto.getRecords();

        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedBalanceFormRecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedBalanceFormRecordDto> recordsList = new ArrayList<ConsolidatedBalanceFormRecordDto>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated Balance USD - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated Balance USD - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedBalanceFormRecordDto>> getConsolidatedIncomeExpenseUSDFormMap(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        List<ConsolidatedBalanceFormRecordDto> records = generateConsolidatedIncomeExpenseUSDForm(reportId);

        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedBalanceFormRecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedBalanceFormRecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated Income Expense USD - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated Income Expense USD - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedBalanceFormRecordDto>> getConsolidatedBalanceKZTForm1Map(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        ListResponseDto KZTForm1ResponseDto = generateConsolidatedBalanceKZTForm1(reportId);
        if(KZTForm1ResponseDto.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = StringUtils.isNotEmpty(KZTForm1ResponseDto.getMessage().getNameEn()) ? KZTForm1ResponseDto.getMessage().getNameEn() :
                    "Error occurred when generating KZT Form 1 report";
            //throw new IllegalStateException(errorMessage);

            logger.error("KZT FORM 1 export (getMap()): " +  errorMessage);
        }
        List<ConsolidatedBalanceFormRecordDto> records = KZTForm1ResponseDto.getRecords();

        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedBalanceFormRecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedBalanceFormRecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated KZT Form 1 - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated KZT Form 1 - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedBalanceFormRecordDto>> getConsolidatedBalanceKZTForm2Map(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        ListResponseDto KZTForm2ResponseDto = generateConsolidatedIncomeExpenseKZTForm2(reportId);
        if(KZTForm2ResponseDto.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = StringUtils.isNotEmpty(KZTForm2ResponseDto.getMessage().getNameEn()) ? KZTForm2ResponseDto.getMessage().getNameEn() :
                    "Error occurred when generating KZT Form 2 report";
            //throw new IllegalStateException(errorMessage);
            logger.error("KZT FORM 2 export error (getMap()) " + errorMessage);
        }
        List<ConsolidatedBalanceFormRecordDto> records = KZTForm2ResponseDto.getRecords();

        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedBalanceFormRecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedBalanceFormRecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated KZT Form 2 - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated KZT Form 2 - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedBalanceFormRecordDto>> getConsolidatedBalanceKZTForm3Map(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?

        ListResponseDto KZTForm3ResponseDto = generateConsolidatedTotalIncomeKZTForm3(reportId);
        if(KZTForm3ResponseDto.getStatus() == ResponseStatusType.FAIL){
            //throw new IllegalStateException(KZTForm3ResponseDto.getMessage().getNameEn());

            logger.error("KZT FORM 3 export error (getMap()) " + KZTForm3ResponseDto.getMessage().getNameEn());
        }

        List<ConsolidatedBalanceFormRecordDto> records = KZTForm3ResponseDto.getRecords();
        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedBalanceFormRecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedBalanceFormRecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated KZT Form 3 - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated KZT Form 3 - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedKZTForm6RecordDto>> getConsolidatedBalanceKZTForm6Map(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        ListResponseDto KZTForm6ResponseDto = generateConsolidatedBalanceKZTForm6(reportId);
        if(KZTForm6ResponseDto.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = StringUtils.isNotEmpty(KZTForm6ResponseDto.getMessage().getNameEn()) ? KZTForm6ResponseDto.getMessage().getNameEn() :
                    "Error occurred when generating KZT Form 6 report";
            //throw new IllegalStateException(errorMessage);
            logger.error("KZT FORM 6 export error (getMap()) " + errorMessage);
        }
        List<ConsolidatedKZTForm6RecordDto> records = KZTForm6ResponseDto.getRecords();

        Map<Integer, List<ConsolidatedKZTForm6RecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedKZTForm6RecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedKZTForm6RecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated KZT Form 6 - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated KZT Form 6 - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedKZTForm7RecordDto>> getConsolidatedBalanceKZTForm7Map(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        ListResponseDto KZTForm7ResponseDto = generateConsolidatedBalanceKZTForm7(reportId);
        if(KZTForm7ResponseDto.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = StringUtils.isNotEmpty(KZTForm7ResponseDto.getMessage().getNameEn()) ? KZTForm7ResponseDto.getMessage().getNameEn() :
                    "Error occurred when generating KZT Form 7 report";
            //throw new IllegalStateException(errorMessage);
            logger.error("KZT FORM 7 export error (getMap()) " + errorMessage);
        }
        List<ConsolidatedKZTForm7RecordDto> records = KZTForm7ResponseDto.getRecords();

        Map<Integer, List<ConsolidatedKZTForm7RecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedKZTForm7RecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedKZTForm7RecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated KZT Form 7 - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated KZT Form 7 - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedKZTForm8RecordDto>> getConsolidatedBalanceKZTForm8Map(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        ListResponseDto KZTForm8ResponseDto = generateConsolidatedBalanceKZTForm8(reportId);
        if(KZTForm8ResponseDto.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = StringUtils.isNotEmpty(KZTForm8ResponseDto.getMessage().getNameEn()) ? KZTForm8ResponseDto.getMessage().getNameEn() :
                    "Error occurred when generating KZT Form 8 report";
            //throw new IllegalStateException(errorMessage);

            logger.error("KZT FORM 8 export error (getMap()) " + errorMessage);
        }
        List<ConsolidatedKZTForm8RecordDto> records = KZTForm8ResponseDto.getRecords();

        Map<Integer, List<ConsolidatedKZTForm8RecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedKZTForm8RecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedKZTForm8RecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated KZT Form 8 - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated KZT Form 8 - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedKZTForm10RecordDto>> getConsolidatedBalanceKZTForm10Map(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        ListResponseDto KZTForm10ResponseDto = generateConsolidatedBalanceKZTForm10(reportId);
        if(KZTForm10ResponseDto.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = StringUtils.isNotEmpty(KZTForm10ResponseDto.getMessage().getNameEn()) ? KZTForm10ResponseDto.getMessage().getNameEn() :
                    "Error occurred when generating KZT Form 10 report";
            //throw new IllegalStateException(errorMessage);

            logger.error("KZT FORM 10 export error (getMap()) " + errorMessage);
        }
        List<ConsolidatedKZTForm10RecordDto> records = KZTForm10ResponseDto.getRecords();

        Map<Integer, List<ConsolidatedKZTForm10RecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedKZTForm10RecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedKZTForm10RecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated KZT Form 10 - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated KZT Form 10 - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedKZTForm13RecordDto>> getConsolidatedBalanceKZTForm13Map(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        ListResponseDto KZTForm13ResponseDto = generateConsolidatedBalanceKZTForm13(reportId);
        if(KZTForm13ResponseDto.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = StringUtils.isNotEmpty(KZTForm13ResponseDto.getMessage().getNameEn()) ? KZTForm13ResponseDto.getMessage().getNameEn() :
                    "Error occurred when generating KZT Form 13 report";
            //throw new IllegalStateException(errorMessage);

            logger.error("KZT FORM 13 export error (getMap()) " + errorMessage);
        }
        List<ConsolidatedKZTForm13RecordDto> records = KZTForm13ResponseDto.getRecords();

        Map<Integer, List<ConsolidatedKZTForm13RecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedKZTForm13RecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedKZTForm13RecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated KZT Form 13 - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated KZT Form 13 - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedKZTForm14RecordDto>> getConsolidatedBalanceKZTForm14Map(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        ListResponseDto KZTForm14ResponseDto = generateConsolidatedBalanceKZTForm14(reportId);
        if(KZTForm14ResponseDto.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = StringUtils.isNotEmpty(KZTForm14ResponseDto.getMessage().getNameEn()) ? KZTForm14ResponseDto.getMessage().getNameEn() :
                    "Error occurred when generating KZT Form 14 report";
            //throw new IllegalStateException(errorMessage);

            logger.error("KZT FORM 14 export error (getMap()) " + errorMessage);
        }
        List<ConsolidatedKZTForm14RecordDto> records = KZTForm14ResponseDto.getRecords();

        Map<Integer, List<ConsolidatedKZTForm14RecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedKZTForm14RecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedKZTForm14RecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated KZT Form 14 - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated KZT Form 14 - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedKZTForm19RecordDto>> getConsolidatedBalanceKZTForm19Map(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        ListResponseDto KZTForm19ResponseDto = generateConsolidatedBalanceKZTForm19(reportId);
        if(KZTForm19ResponseDto.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = StringUtils.isNotEmpty(KZTForm19ResponseDto.getMessage().getNameEn()) ? KZTForm19ResponseDto.getMessage().getNameEn() :
                    "Error occurred when generating KZT Form 19 report";
            //throw new IllegalStateException(errorMessage);

            logger.error("KZT FORM 19 export error (getMap()) " + errorMessage);
        }
        List<ConsolidatedKZTForm19RecordDto> records = KZTForm19ResponseDto.getRecords();

        Map<Integer, List<ConsolidatedKZTForm19RecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedKZTForm19RecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedKZTForm19RecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated KZT Form 19 - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated KZT Form 19 - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }

    private Map<Integer, List<ConsolidatedKZTForm22RecordDto>> getConsolidatedBalanceKZTForm22Map(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        ListResponseDto KZTForm22ResponseDto = generateConsolidatedBalanceKZTForm22(reportId);
        if(KZTForm22ResponseDto.getStatus() == ResponseStatusType.FAIL){
            String errorMessage = StringUtils.isNotEmpty(KZTForm22ResponseDto.getMessage().getNameEn()) ? KZTForm22ResponseDto.getMessage().getNameEn() :
                    "Error occurred when generating KZT Form 22 report";
            //throw new IllegalStateException(errorMessage);

            logger.error("KZT FORM 22 export error (getMap()) " + errorMessage);
        }
        List<ConsolidatedKZTForm22RecordDto> records = KZTForm22ResponseDto.getRecords();

        Map<Integer, List<ConsolidatedKZTForm22RecordDto>> recordsMap = new HashedMap();
        if(records != null){
            int lineNumber = 0;
            for(ConsolidatedKZTForm22RecordDto record: records){
                if(record.getLineNumber() != null){
                    lineNumber = record.getLineNumber();
                }
                if(lineNumber > 0){
                    if(recordsMap.get(lineNumber) == null){
                        List<ConsolidatedKZTForm22RecordDto> recordsList = new ArrayList<>();
                        recordsList.add(record);
                        recordsMap.put(lineNumber, recordsList);
                    }else{
                        recordsMap.get(lineNumber).add(record);
                    }
                }else{
                    logger.error("Consolidated KZT Form 22 - line number is null for one of the records: report id=" + reportId);
                    throw new IllegalStateException("Consolidated KZT Form 22 - line number is null for one of the records: report id=" + reportId);
                }
            }
        }
        return recordsMap;
    }


    /* SAVE GENERATED REPORTS *****************************************************************************************/

    // USD reports

    private boolean saveConsolidatedUSDFormBalance(List<ConsolidatedBalanceFormRecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportUSDFormBalance> existingEntities = this.consolidatedReportUSDFormBalanceRepository.getEntitiesByReportId(reportId);
                this.consolidatedReportUSDFormBalanceRepository.deleteAllByReportId(reportId);

                List<ConsolidatedReportUSDFormBalance> entities = this.consolidatedUSDFormBalanceConverter.assembleList(records, reportId);
                this.consolidatedReportUSDFormBalanceRepository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedUSDFormIncomeExpense(List<ConsolidatedBalanceFormRecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportUSDFormIncomeExpense> existingEntities = this.consolidatedReportUSDFormIncomeExpenseRepository.getEntitiesByReportId(reportId);
                this.consolidatedReportUSDFormIncomeExpenseRepository.deleteAllByReportId(reportId);

                List<ConsolidatedReportUSDFormIncomeExpense> entities = this.consolidatedUSDFormIncomeExpenseConverter.assembleList(records, reportId);
                this.consolidatedReportUSDFormIncomeExpenseRepository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedUSDFormTotalIncome(List<ConsolidatedBalanceFormRecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportUSDFormTotalIncome> existingEntities = this.consolidatedReportUSDFormTotalIncomeRepository.getEntitiesByReportId(reportId);
                this.consolidatedReportUSDFormTotalIncomeRepository.deleteAllByReportId(reportId);

                List<ConsolidatedReportUSDFormTotalIncome> entities = this.consolidatedUSDFormTotalIncomeConverter.assembleList(records, reportId);
                this.consolidatedReportUSDFormTotalIncomeRepository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private void deleteConsolidatedUSDFormBalance(Long reportId){
        this.consolidatedReportUSDFormBalanceRepository.deleteAllByReportId(reportId);
    }

    private void deleteConsolidatedUSDFormIncomeExpense(Long reportId){
        this.consolidatedReportUSDFormIncomeExpenseRepository.deleteAllByReportId(reportId);
    }

    private void deleteConsolidatedUSDFormTotalIncome(Long reportId){
        this.consolidatedReportUSDFormTotalIncomeRepository.deleteAllByReportId(reportId);
    }

    // KZT reports

    private boolean saveConsolidatedKZTForm1(List<ConsolidatedBalanceFormRecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportKZTForm1> existingEntities = this.consolidatedReportKZTForm1Repository.getEntitiesByReportId(reportId);
                this.consolidatedReportKZTForm1Repository.deleteAllByReportId(reportId);

                List<ConsolidatedReportKZTForm1> entities = this.consolidatedKZTForm1Converter.assembleList(records, reportId);
                this.consolidatedReportKZTForm1Repository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedKZTForm2(List<ConsolidatedBalanceFormRecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportKZTForm2> existingEntities = this.consolidatedReportKZTForm2Repository.getEntitiesByReportId(reportId);
                this.consolidatedReportKZTForm2Repository.deleteAllByReportId(reportId);

                List<ConsolidatedReportKZTForm2> entities = this.consolidatedKZTForm2Converter.assembleList(records, reportId);
                this.consolidatedReportKZTForm2Repository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedKZTForm3(List<ConsolidatedBalanceFormRecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportKZTForm3> existingEntities = this.consolidatedReportKZTForm3Repository.getEntitiesByReportId(reportId);
                this.consolidatedReportKZTForm3Repository.deleteAllByReportId(reportId);

                List<ConsolidatedReportKZTForm3> entities = this.consolidatedKZTForm3Converter.assembleList(records, reportId);
                this.consolidatedReportKZTForm3Repository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedKZTForm6(List<ConsolidatedKZTForm6RecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportKZTForm6> existingEntities = this.consolidatedReportKZTForm6Repository.getEntitiesByReportId(reportId);
                this.consolidatedReportKZTForm7Repository.deleteAllByReportId(reportId);

                List<ConsolidatedReportKZTForm6> entities = this.consolidatedKZTForm6Converter.assembleList(records, reportId);
                this.consolidatedReportKZTForm6Repository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedKZTForm7(List<ConsolidatedKZTForm7RecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportKZTForm7> existingEntities = this.consolidatedReportKZTForm7Repository.getEntitiesByReportId(reportId);
                this.consolidatedReportKZTForm7Repository.deleteAllByReportId(reportId);

                List<ConsolidatedReportKZTForm7> entities = this.consolidatedKZTForm7Converter.assembleList(records, reportId);
                this.consolidatedReportKZTForm7Repository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedKZTForm8(List<ConsolidatedKZTForm8RecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportKZTForm8> existingEntities = this.consolidatedReportKZTForm8Repository.getEntitiesByReportId(reportId);
                this.consolidatedReportKZTForm8Repository.deleteAllByReportId(reportId);

                List<ConsolidatedReportKZTForm8> entities = this.consolidatedKZTForm8Converter.assembleList(records, reportId);
                this.consolidatedReportKZTForm8Repository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedKZTForm10(List<ConsolidatedKZTForm10RecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportKZTForm10> existingEntities = this.consolidatedReportKZTForm10Repository.getEntitiesByReportId(reportId);
                this.consolidatedReportKZTForm10Repository.deleteAllByReportId(reportId);

                List<ConsolidatedReportKZTForm10> entities = this.consolidatedKZTForm10Converter.assembleList(records, reportId);
                this.consolidatedReportKZTForm10Repository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedKZTForm13(List<ConsolidatedKZTForm13RecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportKZTForm13> existingEntities = this.consolidatedReportKZTForm13Repository.getEntitiesByReportId(reportId);
                this.consolidatedReportKZTForm13Repository.deleteAllByReportId(reportId);

                List<ConsolidatedReportKZTForm13> entities = this.consolidatedKZTForm13Converter.assembleList(records, reportId);
                this.consolidatedReportKZTForm13Repository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedKZTForm14(List<ConsolidatedKZTForm14RecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportKZTForm14> existingEntities = this.consolidatedReportKZTForm14Repository.getEntitiesByReportId(reportId);
                this.consolidatedReportKZTForm14Repository.deleteAllByReportId(reportId);

                List<ConsolidatedReportKZTForm14> entities = this.consolidatedKZTForm14Converter.assembleList(records, reportId);
                this.consolidatedReportKZTForm14Repository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedKZTForm19(List<ConsolidatedKZTForm19RecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportKZTForm19> existingEntities = this.consolidatedReportKZTForm19Repository.getEntitiesByReportId(reportId);
                this.consolidatedReportKZTForm19Repository.deleteAllByReportId(reportId);

                List<ConsolidatedReportKZTForm19> entities = this.consolidatedKZTForm19Converter.assembleList(records, reportId);
                this.consolidatedReportKZTForm19Repository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private boolean saveConsolidatedKZTForm22(List<ConsolidatedKZTForm22RecordDto> records, Long reportId){
        if(records != null){
            try {
                //List<ConsolidatedReportKZTForm22> existingEntities = this.consolidatedReportKZTForm22Repository.getEntitiesByReportId(reportId);
                this.consolidatedReportKZTForm22Repository.deleteAllByReportId(reportId);

                List<ConsolidatedReportKZTForm22> entities = this.consolidatedKZTForm22Converter.assembleList(records, reportId);
                this.consolidatedReportKZTForm22Repository.save(entities);
            }catch (Exception ex){

                // TODO: transactional !!

                return false;
            }
        }
        return true;
    }

    private void deleteConsolidatedKZTForm1(Long reportId){
        this.consolidatedReportKZTForm1Repository.deleteAllByReportId(reportId);
    }

    private void deleteConsolidatedKZTForm2(Long reportId){
        this.consolidatedReportKZTForm2Repository.deleteAllByReportId(reportId);
    }

    private void deleteConsolidatedKZTForm3(Long reportId){
        this.consolidatedReportKZTForm3Repository.deleteAllByReportId(reportId);
    }

    private void deleteConsolidatedKZTForm6(Long reportId){
        this.consolidatedReportKZTForm6Repository.deleteAllByReportId(reportId);
    }
    private void deleteConsolidatedKZTForm7(Long reportId){
        this.consolidatedReportKZTForm7Repository.deleteAllByReportId(reportId);
    }
    private void deleteConsolidatedKZTForm8(Long reportId){
        this.consolidatedReportKZTForm8Repository.deleteAllByReportId(reportId);
    }
    private void deleteConsolidatedKZTForm10(Long reportId){
        this.consolidatedReportKZTForm10Repository.deleteAllByReportId(reportId);
    }
    private void deleteConsolidatedKZTForm13(Long reportId){
        this.consolidatedReportKZTForm13Repository.deleteAllByReportId(reportId);
    }
    private void deleteConsolidatedKZTForm14(Long reportId){
        this.consolidatedReportKZTForm14Repository.deleteAllByReportId(reportId);
    }
    private void deleteConsolidatedKZTForm19(Long reportId){
        this.consolidatedReportKZTForm19Repository.deleteAllByReportId(reportId);
    }
    private void deleteConsolidatedKZTForm22(Long reportId){
        this.consolidatedReportKZTForm22Repository.deleteAllByReportId(reportId);
    }

    /* GET SAVED REPORTS FROM DB***************************************************************************************/

    // USD reports
    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceUSDFormSaved(Long reportId){
        List<ConsolidatedReportUSDFormBalance> entities = this.consolidatedReportUSDFormBalanceRepository.getEntitiesByReportId(reportId);
        List<ConsolidatedBalanceFormRecordDto> dtoList = this.consolidatedUSDFormBalanceConverter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedIncomeExpenseUSDFormSaved(Long reportId){
        List<ConsolidatedReportUSDFormIncomeExpense> entities = this.consolidatedReportUSDFormIncomeExpenseRepository.getEntitiesByReportId(reportId);
        List<ConsolidatedBalanceFormRecordDto> dtoList = this.consolidatedUSDFormIncomeExpenseConverter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedTotalIncomeUSDFormSaved(Long reportId){
        List<ConsolidatedReportUSDFormTotalIncome> entities = this.consolidatedReportUSDFormTotalIncomeRepository.getEntitiesByReportId(reportId);
        List<ConsolidatedBalanceFormRecordDto> dtoList = this.consolidatedUSDFormTotalIncomeConverter.disassembleList(entities);

        return dtoList;
    }

    // KZT reports
    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceKZTForm1Saved(Long reportId){
        List<ConsolidatedReportKZTForm1> entities = this.consolidatedReportKZTForm1Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedBalanceFormRecordDto> dtoList = this.consolidatedKZTForm1Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceKZTForm2Saved(Long reportId){
        List<ConsolidatedReportKZTForm2> entities = this.consolidatedReportKZTForm2Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedBalanceFormRecordDto> dtoList = this.consolidatedKZTForm2Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceKZTForm3Saved(Long reportId){
        List<ConsolidatedReportKZTForm3> entities = this.consolidatedReportKZTForm3Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedBalanceFormRecordDto> dtoList = this.consolidatedKZTForm3Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedKZTForm6RecordDto> getConsolidatedBalanceKZTForm6Saved(Long reportId){
        List<ConsolidatedReportKZTForm6> entities = this.consolidatedReportKZTForm6Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedKZTForm6RecordDto> dtoList = this.consolidatedKZTForm6Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedKZTForm7RecordDto> getConsolidatedBalanceKZTForm7Saved(Long reportId){
        List<ConsolidatedReportKZTForm7> entities = this.consolidatedReportKZTForm7Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedKZTForm7RecordDto> dtoList = this.consolidatedKZTForm7Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedKZTForm8RecordDto> getConsolidatedBalanceKZTForm8Saved(Long reportId){
        List<ConsolidatedReportKZTForm8> entities = this.consolidatedReportKZTForm8Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedKZTForm8RecordDto> dtoList = this.consolidatedKZTForm8Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedKZTForm10RecordDto> getConsolidatedBalanceKZTForm10Saved(Long reportId){
        List<ConsolidatedReportKZTForm10> entities = this.consolidatedReportKZTForm10Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedKZTForm10RecordDto> dtoList = this.consolidatedKZTForm10Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedKZTForm13RecordDto> getConsolidatedBalanceKZTForm13Saved(Long reportId){
        List<ConsolidatedReportKZTForm13> entities = this.consolidatedReportKZTForm13Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedKZTForm13RecordDto> dtoList = this.consolidatedKZTForm13Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedKZTForm14RecordDto> getConsolidatedBalanceKZTForm14Saved(Long reportId){
        List<ConsolidatedReportKZTForm14> entities = this.consolidatedReportKZTForm14Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedKZTForm14RecordDto> dtoList = this.consolidatedKZTForm14Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedKZTForm19RecordDto> getConsolidatedBalanceKZTForm19Saved(Long reportId){
        List<ConsolidatedReportKZTForm19> entities = this.consolidatedReportKZTForm19Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedKZTForm19RecordDto> dtoList = this.consolidatedKZTForm19Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedKZTForm22RecordDto> getConsolidatedBalanceKZTForm22Saved(Long reportId){
        List<ConsolidatedReportKZTForm22> entities = this.consolidatedReportKZTForm22Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedKZTForm22RecordDto> dtoList = this.consolidatedKZTForm22Converter.disassembleList(entities);

        return dtoList;
    }

    /* EXPORT ******************************************************************************************************/

    @Override
    public FilesDto getExportFileStream(Long reportId, String type) {
        if(reportId == null){
            logger.error("Periodic Report export: report id not specified");
            return null;
        }
        if(type == null){
            logger.error("Periodic Report export: type not specified");
            return null;
        }

        if(type.equalsIgnoreCase(PeriodicReportConstants.USD_FORM_1)){
            return getConsolidatedBalanceUSDReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.USD_FORM_2)){
            return getConsolidatedIncomeExpenseUSDReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.USD_FORM_3)){
            return getConsolidatedTotalIncomeUSDReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.KZT_FORM_1)){
            return getConsolidatedForm1KZTReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.KZT_FORM_2)){
            return getConsolidatedForm2KZTReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.KZT_FORM_3)){
            return getConsolidatedForm3KZTReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.KZT_FORM_6)){
            return getConsolidatedForm6KZTReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.KZT_FORM_7)){
            return getConsolidatedForm7KZTReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.KZT_FORM_8)){
            return getConsolidatedForm8KZTReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.KZT_FORM_10)){
            return getConsolidatedForm10KZTReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.KZT_FORM_13)){
            return getConsolidatedForm13KZTReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.KZT_FORM_14)){
            return getConsolidatedForm14KZTReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.KZT_FORM_19)){
            return getConsolidatedForm19KZTReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.KZT_FORM_22)){
            return getConsolidatedForm22KZTReportInputStream(reportId);
        }else if(type.equalsIgnoreCase(PeriodicReportConstants.TARRAGON_GENERATED_GL)){
            return getTarragonGeneratedGeneralLedgerInputStream(reportId);
        }

        return null;
    }

    @Override
    public FilesDto getExportAllKZTReportsFileStream(Long reportId){
        FilesDto filesDto = new FilesDto();
        String fileName = null;
        try {

            fileName = this.rootDirectory + "/tmp/" + HashUtils.hashMD5String(new Date().getTime() + "") + ".zip";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fileName));

            ZipEntry e = null;
            byte[] data = null;

            // Form 1
            FilesDto form1 = getConsolidatedForm1KZTReportInputStream(reportId);
            setExportZipContent("ОФП-1.xlsx", out, form1);

            // Form 2
            FilesDto form2 = getConsolidatedForm2KZTReportInputStream(reportId);
            setExportZipContent("ОПиУ-2.xlsx", out, form2);

            // Form 3
            FilesDto form3 = getConsolidatedForm3KZTReportInputStream(reportId);
            setExportZipContent("ОПСД-3.xlsx", out, form3);

            // Form 6
            FilesDto form6 = getConsolidatedForm6KZTReportInputStream(reportId);
            setExportZipContent("ОИК-6.xlsx", out, form6);

            // Form 7
            FilesDto form7 = getConsolidatedForm7KZTReportInputStream(reportId);
            setExportZipContent("РФИ-7.xlsx", out, form7);

            // Form 8
            FilesDto form8 = getConsolidatedForm8KZTReportInputStream(reportId);
            setExportZipContent("РТПДЗ-8.xlsx", out, form8);

            // Form 10
            FilesDto form10 = getConsolidatedForm10KZTReportInputStream(reportId);
            setExportZipContent("РПА-10.xlsx", out, form10);

            // Form 13
            FilesDto form13 = getConsolidatedForm13KZTReportInputStream(reportId);
            setExportZipContent("РФО-13.xlsx", out, form13);

            // Form 14
            FilesDto form14 = getConsolidatedForm14KZTReportInputStream(reportId);
            setExportZipContent("РТПКЗ-14.xlsx", out, form14);

            // Form 19
            FilesDto form19 = getConsolidatedForm19KZTReportInputStream(reportId);
            setExportZipContent("РДХФИ-19.xlsx", out, form19);

            // Form 22
            FilesDto form22 = getConsolidatedForm22KZTReportInputStream(reportId);
            setExportZipContent("РПДХ-22.xlsx", out, form22);

            out.close();

            filesDto.setInputStream(new FileInputStream(fileName));
            filesDto.setFileName(fileName);

            return filesDto;
        } catch (UnsupportedEncodingException e) {
            logger.error("(PeriodicReport) File export (all kzt reports) request failed: unsupported encoding", e);
        } catch (IOException e) {
            logger.error("(PeriodicReport) File export (all kzt reports) request failed: io exception", e);
        } catch (Exception e){
            logger.error("(PeriodicReport) File export (all kzt reports) request failed", e);
        }

        return null;
    }

    private void setExportZipContent(String fileName,ZipOutputStream out, FilesDto filesDto){
        try {
            ZipEntry e = new ZipEntry(fileName);
            out.putNextEntry(e);
            byte[] data = IOUtils.toByteArray(filesDto.getInputStream());
            out.write(data, 0, data.length);
            out.closeEntry();
            filesDto.getInputStream().close();
            new File(filesDto.getFileName()).delete();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

//    private FilesDto getConsolidatedBalanceUSDReportInputStream(Long reportId) {
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + reportId);
//            return null;
//        }
//
//        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = getConsolidatedBalanceUSDFormMap(reportId);
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_USD_1.xlsx");
//        InputStream excelFileToRead = null;
//        try {
//            excelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_USD_1.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//
//        try {
//            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            int rows = sheet.getLastRowNum();
//            //int rowNum = 0;
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            int addedRecords = 0;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    Cell cell = row.getCell(1);
//                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.USD_FORM_1_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING && row.getCell(2).getStringCellValue() != null) {
//                        int lineNumber = Integer.parseInt(row.getCell(2).getStringCellValue());
//                        List<ConsolidatedBalanceFormRecordDto> records = recordsMap.get(lineNumber);
//                        int recordsNum = records != null ? records.size() : 0;
//                        if (recordsNum > 0 && row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING &&
//                                records.get(0).getName().equalsIgnoreCase(row.getCell(1).getStringCellValue())) {
//                            if(records.get(0).getCurrentAccountBalance() != null) {
//                                row.getCell(4).setCellValue(records.get(0).getCurrentAccountBalance());
//                            }
//                            if(records.get(0).getPreviousAccountBalance() != null) {
//                                row.getCell(5).setCellValue(records.get(0).getPreviousAccountBalance());
//                            }
//                        }
//                        if (recordsNum > 1) {
//                            sheet.shiftRows(row.getRowNum() + 1, rows + addedRecords, recordsNum - 1);
//                            //insertRows(rowNum, row, sheet, records);
//                            int index = 1;
//                            for (int i = 1; i < records.size(); i++) {
//                                Row newRow = sheet.createRow(row.getRowNum() + index);
//                                newRow.createCell(0).setCellValue(records.get(i).getAccountNumber());
//                                newRow.createCell(1).setCellValue(records.get(i).getName());
//                                newRow.createCell(2).setCellType(Cell.CELL_TYPE_STRING);
//                                if (records.get(i).getOtherEntityName() != null) {
//                                    newRow.createCell(3).setCellValue(records.get(i).getOtherEntityName());
//                                } else {
//                                    newRow.createCell(3).setCellType(Cell.CELL_TYPE_STRING);
//                                }
//                                if (records.get(i).getCurrentAccountBalance() != null) {
//                                    newRow.createCell(4).setCellValue(records.get(i).getCurrentAccountBalance());
//                                } else {
//                                    newRow.createCell(4).setCellType(Cell.CELL_TYPE_NUMERIC);
//                                }
//                                if (records.get(i).getPreviousAccountBalance() != null) {
//                                    newRow.createCell(5).setCellValue(records.get(i).getPreviousAccountBalance());
//                                } else {
//                                    newRow.createCell(5).setCellType(Cell.CELL_TYPE_NUMERIC);
//                                }
//
//                                // set styles
//                                newRow.getCell(0).setCellStyle(row.getCell(0).getCellStyle());
//                                newRow.getCell(1).setCellStyle(row.getCell(1).getCellStyle());
//                                newRow.getCell(2).setCellStyle(row.getCell(2).getCellStyle());
//                                newRow.getCell(3).setCellStyle(row.getCell(3).getCellStyle());
//                                if (row.getCell(4) != null && row.getCell(4).getCellStyle() != null) {
//                                    newRow.getCell(4).setCellStyle(row.getCell(4).getCellStyle());
//                                }
//                                newRow.getCell(5).setCellStyle(row.getCell(5).getCellStyle());
//                                index++;
//                                addedRecords++;
//                            }
//                        }
//                    }
//
//                    //rowNum++;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.SUBACCOUNT_GROUP_NAME) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }
//            }
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//            // write to new
//            String filePath = tmpDir + "/CONS_BLNC_USD_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        }  catch (IOException e) {
//            logger.error("IO Exception when exporting USD_FORM_1", e);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedBalanceUSDReportInputStream(Long reportId) {
        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
            return null;
        }
        List<ConsolidatedBalanceFormRecordDto> records = null;
        try {
            ListResponseDto responseDto = generateConsolidatedBalanceUSDForm(reportId);
            if (responseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(responseDto.getMessage().getNameEn()) ? responseDto.getMessage().getNameEn() :
                        "Error occurred when generating USD Form 1 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("USD FORM 1 export (get records): " + errorMessage);
            }
            records = responseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_USD_1.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_USD_1.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 8;
            final int columnNum = 6;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedBalanceFormRecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);
                    added++;

                    for(int i = 0; i < columnNum; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getAccountNumber());
                    newRow.getCell(1).setCellValue(record.getName());
                    newRow.getCell(2).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");

                    if (record.getOtherEntityName() != null) {
                        newRow.getCell(3).setCellValue(record.getOtherEntityName());
                    }
                    if (record.getCurrentAccountBalance() != null) {
                        newRow.getCell(4).setCellValue(record.getCurrentAccountBalance());
                    }
                    if (record.getPreviousAccountBalance() != null) {
                        newRow.getCell(5).setCellValue(record.getPreviousAccountBalance());
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }
            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/CONS_BLNC_USD_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        }  catch (IOException e) {
            logger.error("IO Exception when exporting USD_FORM_1", e);
        }

        return null;
    }

//    private FilesDto getConsolidatedIncomeExpenseUSDReportInputStream(Long reportId){
//
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + reportId);
//            return null;
//        }
//
//        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = getConsolidatedIncomeExpenseUSDFormMap(reportId);
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_USD_2.xlsx");
//        InputStream ExcelFileToRead = null;
//        try {
//            ExcelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_USD_2.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//        try {
//            XSSFWorkbook  workbook = new XSSFWorkbook(ExcelFileToRead);
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            int rows = sheet.getLastRowNum();
//            //int rowNum = 0;
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            int addedRecords = 0;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    Cell cell = row.getCell(1);
//                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.USD_FORM_2_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC){
//                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
//                        List<ConsolidatedBalanceFormRecordDto> records = recordsMap.get(lineNumber);
//                        int recordsNum = records != null ? records.size() : 0;
//                        if (recordsNum > 0 && row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING &&
//                                records.get(0).getName().equalsIgnoreCase(row.getCell(1).getStringCellValue())) {
//                            if(records.get(0).getCurrentAccountBalance() != null) {
//                                row.getCell(4).setCellValue(records.get(0).getCurrentAccountBalance());
//                            }
//                            if(records.get(0).getPreviousAccountBalance() != null) {
//                                row.getCell(5).setCellValue(records.get(0).getPreviousAccountBalance());
//                            }
//                        }
//                        if (recordsNum > 1) {
//                            sheet.shiftRows(row.getRowNum() + 1, rows + addedRecords, recordsNum - 1);
//                            //insertRows(rowNum, row, sheet, records);
//                            int index = 1;
//                            for (int i = 1; i < records.size(); i++) {
//                                Row newRow = sheet.createRow(row.getRowNum() + index);
//                                newRow.createCell(0).setCellValue(records.get(i).getAccountNumber());
//                                newRow.createCell(1).setCellValue(records.get(i).getName());
//                                newRow.createCell(2).setCellType(Cell.CELL_TYPE_STRING);
//                                if (records.get(i).getOtherEntityName() != null) {
//                                    newRow.createCell(3).setCellValue(records.get(i).getOtherEntityName());
//                                } else {
//                                    newRow.createCell(3).setCellType(Cell.CELL_TYPE_STRING);
//                                }
//                                if (records.get(i).getCurrentAccountBalance() != null) {
//                                    newRow.createCell(4).setCellValue(records.get(i).getCurrentAccountBalance());
//                                } else {
//                                    newRow.createCell(4).setCellType(Cell.CELL_TYPE_NUMERIC);
//                                }
//                                if (records.get(i).getPreviousAccountBalance() != null) {
//                                    newRow.createCell(5).setCellValue(records.get(i).getPreviousAccountBalance());
//                                } else {
//                                    newRow.createCell(5).setCellType(Cell.CELL_TYPE_NUMERIC);
//                                }
//
//                                // set styles
//                                newRow.getCell(0).setCellStyle(row.getCell(0).getCellStyle());
//                                newRow.getCell(1).setCellStyle(row.getCell(1).getCellStyle());
//                                newRow.getCell(2).setCellStyle(row.getCell(2).getCellStyle());
//                                newRow.getCell(3).setCellStyle(row.getCell(3).getCellStyle());
//
//                                String textValue = ExcelUtils.getTextValueFromAnyCell(newRow.getCell(1));
//                                boolean needColoring = textValue != null &&
//                                        //Доходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи
//                                        (textValue.equalsIgnoreCase(PeriodicReportConstants.INCOME_FAIR_VALUE_CHANGES) ||
//                                                //Расходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи
//                                                textValue.equalsIgnoreCase(PeriodicReportConstants.EXPENSE_FAIR_VALUE_CHANGES) ||
//                                                //Расходы по вознаграждениям по краткосрочным банковским займам
//                                                textValue.equalsIgnoreCase(PeriodicReportConstants.RU_7313_010)
//                                        );
//                                if(needColoring){
//                                    // colored cells
//                                    if (row.getCell(4) != null && row.getCell(4).getCellStyle() != null) {
//                                        newRow.getCell(4).setCellStyle(row.getCell(4).getCellStyle());
//                                    }
//                                    if (row.getCell(5) != null && row.getCell(5).getCellStyle() != null) {
//                                        newRow.getCell(5).setCellStyle(row.getCell(5).getCellStyle());
//                                    }
//                                }else{
//                                    // not colored cells
//                                    if (row.getCell(3) != null && row.getCell(3).getCellStyle() != null) {
//                                        newRow.getCell(4).setCellStyle(row.getCell(3).getCellStyle());
//                                    }
//                                    if (row.getCell(3) != null && row.getCell(3).getCellStyle() != null) {
//                                        newRow.getCell(5).setCellStyle(row.getCell(3).getCellStyle());
//                                    }
//                                }
//
//
//
//                                index++;
//                                addedRecords++;
//                            }
//                        }
//                    }
//
//                    //rowNum++;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.SUBACCOUNT_GROUP_NAME) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }
//            }
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//            // write to new
//            String filePath =  tmpDir + "/INCOME_EXP_USD_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        } catch (IOException e) {
//            logger.error("IO Exception when exporting USD_FORM_2", e);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedIncomeExpenseUSDReportInputStream(Long reportId){

        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
            return null;
        }
        List<ConsolidatedBalanceFormRecordDto> records = null;
        try {
            records = generateConsolidatedIncomeExpenseUSDForm(reportId);
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_USD_2.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_USD_2.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 8;
            final int columnNum = 6;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedBalanceFormRecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);
                    added++;

                    for(int i = 0; i < columnNum; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getAccountNumber());
                    newRow.getCell(1).setCellValue(record.getName());
                    newRow.getCell(2).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");

                    if (record.getOtherEntityName() != null) {
                        newRow.getCell(3).setCellValue(record.getOtherEntityName());
                    }
                    if (record.getCurrentAccountBalance() != null) {
                        newRow.getCell(4).setCellValue(record.getCurrentAccountBalance());
                    }
                    if (record.getPreviousAccountBalance() != null) {
                        newRow.getCell(5).setCellValue(record.getPreviousAccountBalance());
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath =  tmpDir + "/INCOME_EXP_USD_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            logger.error("IO Exception when exporting USD_FORM_2", e);
        }

        return null;
    }

//    private FilesDto getConsolidatedTotalIncomeUSDReportInputStream(Long reportId){
//
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
//            return null;
//        }
//        List<ConsolidatedBalanceFormRecordDto> records = generateConsolidatedTotalIncomeUSDForm(reportId);
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_USD_3.xlsx");
//        InputStream ExcelFileToRead = null;
//        try {
//            ExcelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_USD_3.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//
//        try {
//            XSSFWorkbook  workbook = new XSSFWorkbook(ExcelFileToRead);
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    Cell cell = row.getCell(1);
//                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.USD_FORM_3_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//                    if (row.getCell(0) != null) {
//                        for(ConsolidatedBalanceFormRecordDto record: records){
//                            if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), record.getName()) && record.getCurrentAccountBalance() != null){
//                                row.getCell(2).setCellValue(record.getCurrentAccountBalance());
//                            }
//                            if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), record.getName()) && record.getPreviousAccountBalance() != null){
//                                row.getCell(3).setCellValue(record.getPreviousAccountBalance());
//                            }
//                        }
//                    }
//
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.INDICATORS_NAME) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.LINE_CODE) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.ON_CURRENT_PERIOD_DATE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }
//            }
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//
//            // write to new
//            String filePath = tmpDir + "/CONS_TOTAL_INCOME_USD_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        } catch (IOException e) {
//            logger.error("IO Exception when exporting USD_FORM_3", e);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedTotalIncomeUSDReportInputStream(Long reportId){

        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue(): null));
            return null;
        }
        List<ConsolidatedBalanceFormRecordDto> records = null;
        try {
            records = generateConsolidatedTotalIncomeUSDForm(reportId);
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_USD_3.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_USD_3.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 14;
            final int columnNum = 4;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedBalanceFormRecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);
                    added++;

                    for(int i = 0; i < columnNum; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getName());
                    newRow.getCell(1).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");
                    if (record.getCurrentAccountBalance() != null) {
                        newRow.getCell(2).setCellValue(record.getCurrentAccountBalance());
                    } else {
                        newRow.getCell(2).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if (record.getPreviousAccountBalance() != null) {
                        newRow.getCell(3).setCellValue(record.getPreviousAccountBalance());
                    } else {
                        newRow.getCell(3).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }


            // write to new
            String filePath = tmpDir + "/CONS_TOTAL_INCOME_USD_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            logger.error("IO Exception when exporting USD_FORM_3", e);
        }

        return null;
    }

//    private FilesDto getConsolidatedForm1KZTReportInputStream(Long reportId) {
//
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + reportId);
//            return null;
//        }
//
//        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = null;
//        try{
//            recordsMap = getConsolidatedBalanceKZTForm1Map(reportId);
//        }catch (IllegalStateException ex){
//            throw ex;
//        }
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_1.xlsx");
//        InputStream excelFileToRead = null;
//        try {
//            excelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_1.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//        try {
//            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            int rows = sheet.getLastRowNum();
//            //int rowNum = 0;
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            int addedRecords = 0;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    Cell cell = row.getCell(1);
//                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.USD_FORM_1_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING && row.getCell(2).getStringCellValue() != null) {
//                        int lineNumber = Integer.parseInt(row.getCell(2).getStringCellValue());
//                        List<ConsolidatedBalanceFormRecordDto> records = recordsMap.get(lineNumber);
//                        int recordsNum = records != null ? records.size() : 0;
//                        if (recordsNum > 0 && row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING &&
//                                records.get(0).getName().equalsIgnoreCase(row.getCell(1).getStringCellValue())) {
//                            if(records.get(0).getCurrentAccountBalance() != null) {
//                                row.getCell(4).setCellValue(records.get(0).getCurrentAccountBalance());
//                            }
//                            if(records.get(0).getPreviousAccountBalance() != null) {
//                                row.getCell(5).setCellValue(records.get(0).getPreviousAccountBalance());
//                            }
//                        }
//                        if (recordsNum > 1) {
//                            sheet.shiftRows(row.getRowNum() + 1, rows + addedRecords, recordsNum - 1);
//                            //insertRows(rowNum, row, sheet, records);
//                            int index = 1;
//                            for (int i = 1; i < records.size(); i++) {
//                                Row newRow = sheet.createRow(row.getRowNum() + index);
//                                newRow.createCell(0).setCellValue(records.get(i).getAccountNumber());
//                                newRow.createCell(1).setCellValue(records.get(i).getName());
//                                newRow.createCell(2).setCellType(Cell.CELL_TYPE_STRING);
//                                if (records.get(i).getOtherEntityName() != null) {
//                                    newRow.createCell(3).setCellValue(records.get(i).getOtherEntityName());
//                                } else {
//                                    newRow.createCell(3).setCellType(Cell.CELL_TYPE_STRING);
//                                }
//                                if (records.get(i).getCurrentAccountBalance() != null) {
//                                    newRow.createCell(4).setCellValue(records.get(i).getCurrentAccountBalance());
//                                } else {
//                                    newRow.createCell(4).setCellType(Cell.CELL_TYPE_NUMERIC);
//                                }
//                                if (records.get(i).getPreviousAccountBalance() != null) {
//                                    newRow.createCell(5).setCellValue(records.get(i).getPreviousAccountBalance());
//                                } else {
//                                    newRow.createCell(5).setCellType(Cell.CELL_TYPE_NUMERIC);
//                                }
//
//                                // set styles
//                                newRow.getCell(0).setCellStyle(row.getCell(0).getCellStyle());
//                                newRow.getCell(1).setCellStyle(row.getCell(1).getCellStyle());
//                                newRow.getCell(2).setCellStyle(row.getCell(2).getCellStyle());
//                                newRow.getCell(3).setCellStyle(row.getCell(3).getCellStyle());
//                                if (row.getCell(4) != null && row.getCell(4).getCellStyle() != null) {
//                                    newRow.getCell(4).setCellStyle(row.getCell(4).getCellStyle());
//                                }
//                                newRow.getCell(5).setCellStyle(row.getCell(5).getCellStyle());
//                                index++;
//                                addedRecords++;
//                            }
//                        }
//                    }
//
//                    //rowNum++;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.SUBACCOUNT_GROUP_NAME) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
//                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
//                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
//                }
//            }
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//            // write to new
//            String filePath = tmpDir + "/CONS_KZT_FORM_1_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        } catch (IOException ex) {
//            // TODO: log error
//            //e.printStackTrace();
//            logger.error("IO Exception when exporting KZT_FORM_1", ex);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedForm1KZTReportInputStream(Long reportId) {

        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
            return null;
        }
        List<ConsolidatedBalanceFormRecordDto> records = null;
        try {
            ListResponseDto KZTForm1ResponseDto = generateConsolidatedBalanceKZTForm1(reportId);
            if (KZTForm1ResponseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(KZTForm1ResponseDto.getMessage().getNameEn()) ? KZTForm1ResponseDto.getMessage().getNameEn() :
                        "Error occurred when generating KZT Form 1 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("KZT FORM 1 export (get records): " + errorMessage);
            }
            records = KZTForm1ResponseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_KZT_1.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_1.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 14;
            final int columnNum = 6;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedBalanceFormRecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);
                    added++;

                    for(int i = 0; i < columnNum; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getAccountNumber());
                    newRow.getCell(1).setCellValue(record.getName());
                    newRow.getCell(2).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");

                    if (record.getOtherEntityName() != null) {
                        newRow.getCell(3).setCellValue(record.getOtherEntityName());
                    }
                    if (record.getCurrentAccountBalance() != null) {
                        newRow.getCell(4).setCellValue(record.getCurrentAccountBalance());
                    }
                    if (record.getPreviousAccountBalance() != null) {
                        newRow.getCell(5).setCellValue(record.getPreviousAccountBalance());
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_1_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException ex) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_1", ex);
        }

        return null;
    }

//    private FilesDto getConsolidatedForm2KZTReportInputStream(Long reportId) {
//
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + reportId);
//            return null;
//        }
//        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = null;
//        try{
//            recordsMap = getConsolidatedBalanceKZTForm2Map(reportId);
//        }catch (IllegalStateException ex){
//            throw ex;
//        }
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_2.xlsx");
//        InputStream excelFileToRead = null;
//        try {
//            excelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_2.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//        try {
//            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            int rows = sheet.getLastRowNum();
//            //int rowNum = 0;
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            int addedRecords = 0;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    Cell cell = row.getCell(1);
//                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.USD_FORM_2_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC && row.getCell(2).getNumericCellValue() > 0) {
//                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
//                        List<ConsolidatedBalanceFormRecordDto> records = recordsMap.get(lineNumber);
//                        int recordsNum = records != null ? records.size() : 0;
//                        if (recordsNum > 0 && row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING &&
//                                records.get(0).getName().equalsIgnoreCase(row.getCell(1).getStringCellValue())) {
//                            if(records.get(0).getCurrentAccountBalance() != null) {
//                                row.getCell(4).setCellValue(records.get(0).getCurrentAccountBalance());
//                            }
//                            if(records.get(0).getPreviousAccountBalance() != null) {
//                                row.getCell(5).setCellValue(records.get(0).getPreviousAccountBalance());
//                            }
//                        }
//                        if (recordsNum > 1) {
//                            sheet.shiftRows(row.getRowNum() + 1, rows + addedRecords, recordsNum - 1);
//                            //insertRows(rowNum, row, sheet, records);
//                            int index = 1;
//                            for (int i = 1; i < records.size(); i++) {
//                                Row newRow = sheet.createRow(row.getRowNum() + index);
//                                newRow.createCell(0).setCellValue(records.get(i).getAccountNumber());
//                                newRow.createCell(1).setCellValue(records.get(i).getName());
//                                newRow.createCell(2).setCellType(Cell.CELL_TYPE_STRING);
//                                if (records.get(i).getOtherEntityName() != null) {
//                                    newRow.createCell(3).setCellValue(records.get(i).getOtherEntityName());
//                                } else {
//                                    newRow.createCell(3).setCellType(Cell.CELL_TYPE_STRING);
//                                }
//                                if (records.get(i).getCurrentAccountBalance() != null) {
//                                    newRow.createCell(4).setCellValue(records.get(i).getCurrentAccountBalance());
//                                } else {
//                                    newRow.createCell(4).setCellType(Cell.CELL_TYPE_NUMERIC);
//                                }
//                                if (records.get(i).getPreviousAccountBalance() != null) {
//                                    newRow.createCell(5).setCellValue(records.get(i).getPreviousAccountBalance());
//                                } else {
//                                    newRow.createCell(5).setCellType(Cell.CELL_TYPE_NUMERIC);
//                                }
//
//                                // set styles
//                                newRow.getCell(0).setCellStyle(row.getCell(0).getCellStyle());
//                                newRow.getCell(1).setCellStyle(row.getCell(1).getCellStyle());
//                                newRow.getCell(2).setCellStyle(row.getCell(2).getCellStyle());
//                                newRow.getCell(3).setCellStyle(row.getCell(3).getCellStyle());
//                                if (row.getCell(4) != null && row.getCell(4).getCellStyle() != null) {
//                                    newRow.getCell(4).setCellStyle(row.getCell(4).getCellStyle());
//                                }
//                                newRow.getCell(5).setCellStyle(row.getCell(5).getCellStyle());
//                                index++;
//                                addedRecords++;
//                            }
//                        }
//                    }
//
//                    //rowNum++;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.SUBACCOUNT_GROUP_NAME) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
//                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
//                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
//                }
//            }
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//            // write to new
//            String filePath = tmpDir + "/CONS_KZT_FORM_2_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        }catch (IOException ex) {
//            // TODO: log error
//            //e.printStackTrace();
//            logger.error("IO Exception when exporting KZT_FORM_2", ex);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedForm2KZTReportInputStream(Long reportId) {

        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
            return null;
        }
        List<ConsolidatedBalanceFormRecordDto> records = null;
        try {
            ListResponseDto KZTForm2ResponseDto = generateConsolidatedIncomeExpenseKZTForm2(reportId);
            if (KZTForm2ResponseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(KZTForm2ResponseDto.getMessage().getNameEn()) ? KZTForm2ResponseDto.getMessage().getNameEn() :
                        "Error occurred when generating KZT Form 2 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("KZT FORM 2 export (get records): " + errorMessage);
            }
            records = KZTForm2ResponseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_KZT_2.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_2.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 14;
            final int columnNum = 6;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedBalanceFormRecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);
                    added++;

                    for(int i = 0; i < columnNum; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getAccountNumber());
                    newRow.getCell(1).setCellValue(record.getName());
                    newRow.getCell(2).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");

                    if (record.getOtherEntityName() != null) {
                        newRow.getCell(3).setCellValue(record.getOtherEntityName());
                    }
                    if (record.getCurrentAccountBalance() != null) {
                        newRow.getCell(4).setCellValue(record.getCurrentAccountBalance());
                    }
                    if (record.getPreviousAccountBalance() != null) {
                        newRow.getCell(5).setCellValue(record.getPreviousAccountBalance());
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_2_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        }catch (IOException ex) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_2", ex);
        }

        return null;
    }

//    private FilesDto getConsolidatedForm3KZTReportInputStream(Long reportId) {
//
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + reportId);
//            return null;
//        }
//        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = null;
//        try{
//            recordsMap = getConsolidatedBalanceKZTForm3Map(reportId);
//        }catch (IllegalStateException ex){
//            throw ex;
//        }
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_3.xlsx");
//        InputStream excelFileToRead = null;
//        try {
//            excelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_3.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//        try {
//            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    Cell cell = row.getCell(0);
//                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.KZT_FORM_3_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//                    int lineNumber = 0;
//                    int sublineNumber = 0;
//                    if(row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC){
//                        lineNumber = (int) row.getCell(1).getNumericCellValue();
//                    } else if (row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING && row.getCell(1).getStringCellValue() != null) {
//                        String lineNumberText = row.getCell(1).getStringCellValue();
//
//                        if (lineNumberText.contains(".")) {
//                            lineNumber = Integer.parseInt(lineNumberText.split("\\.")[0]);
//                            sublineNumber = Integer.parseInt(lineNumberText.split("\\.")[1]);
//                        }else{
//                            lineNumber = Integer.parseInt(lineNumberText);
//                        }
//                    }
//
//                    if(lineNumber > 0){
//                        List<ConsolidatedBalanceFormRecordDto> records = recordsMap.get(lineNumber);
//                        for(ConsolidatedBalanceFormRecordDto record: records){
//                            if(row.getCell(0) != null && row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING &&
//                                    record.getName().equalsIgnoreCase(row.getCell(0).getStringCellValue())){
//                                if(record.getSubLineNumber() == null || (sublineNumber > 0 && record.getSubLineNumber() == sublineNumber)){
//                                        if(record.getCurrentAccountBalance() != null) {
//                                            row.getCell(2).setCellValue(record.getCurrentAccountBalance());
//                                        }
//                                        if(record.getPreviousAccountBalance() != null) {
//                                            row.getCell(3).setCellValue(record.getPreviousAccountBalance());
//                                        }
//                                        break;
//                                }
//                            }
//                        }
//                    }
//                    //rowNum++;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.INDICATORS_NAME) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.LINE_CODE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
//                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
//                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
//                }
//            }
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//            // write to new
//            String filePath = tmpDir + "/CONS_KZT_FORM_3_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        }catch (IOException e) {
//
//            // TODO: log error
//            //e.printStackTrace();
//            logger.error("IO Exception when exporting KZT_FORM_3", e);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedForm3KZTReportInputStream(Long reportId) {

        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue(): null));
            return null;
        }
        List<ConsolidatedBalanceFormRecordDto> records = null;
        try {
            ListResponseDto responseDto = generateConsolidatedTotalIncomeKZTForm3(reportId);
            if (responseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(responseDto.getMessage().getNameEn()) ? responseDto.getMessage().getNameEn() :
                        "Error occurred when generating KZT Form 3 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("KZT FORM 3 export (get records): " + errorMessage);
            }
            records = responseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_KZT_3.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_3.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 14;
            final int columnNum = 4;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedBalanceFormRecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);
                    added++;

                    for(int i = 0; i < columnNum; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getName());
                    newRow.getCell(1).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");
                    if (record.getCurrentAccountBalance() != null) {
                        newRow.getCell(2).setCellValue(record.getCurrentAccountBalance());
                    } else {
                        newRow.getCell(2).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if (record.getPreviousAccountBalance() != null) {
                        newRow.getCell(3).setCellValue(record.getPreviousAccountBalance());
                    } else {
                        newRow.getCell(3).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_3_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        }catch (IOException e) {

            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_3", e);
        }

        return null;
    }

//    private FilesDto getConsolidatedForm6KZTReportInputStream(Long reportId) {
//
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + reportId);
//            return null;
//        }
//        Map<Integer, List<ConsolidatedKZTForm6RecordDto>> recordsMap = null;
//        try{
//            recordsMap = getConsolidatedBalanceKZTForm6Map(reportId);
//        }catch (IllegalStateException ex){
//            throw ex;
//        }
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_6.xlsx");
//        InputStream excelFileToRead = null;
//        try {
//            excelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_6.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//        try {
//            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_6_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//                    int lineNumber = 0;
//                    if(row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC){
//                        lineNumber = (int) row.getCell(1).getNumericCellValue();
//                    } else if (row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING && row.getCell(1).getStringCellValue() != null) {
//                        String lineNumberText = row.getCell(1).getStringCellValue();
//                        lineNumber = Integer.parseInt(lineNumberText);
//                    }
//
//                    if(lineNumber > 0){
//                        List<ConsolidatedKZTForm6RecordDto> records = recordsMap.get(lineNumber);
//                        for(ConsolidatedKZTForm6RecordDto record: records){
//                            if(row.getCell(0) != null && row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING &&
//                                    record.getName().equalsIgnoreCase(row.getCell(0).getStringCellValue())){
//                                if(record.getLineNumber() != null && record.getLineNumber() == lineNumber){
//                                    if(record.getShareholderEquity() != null) {
//                                        row.getCell(2).setCellValue(record.getShareholderEquity());
//                                    }
//                                    if(record.getAdditionalPaidinCapital() != null) {
//                                        row.getCell(3).setCellValue(record.getAdditionalPaidinCapital());
//                                    }
//                                    if(record.getRedeemedOwnEquityInstruments() != null) {
//                                        row.getCell(4).setCellValue(record.getRedeemedOwnEquityInstruments());
//                                    }
//                                    if(record.getReserveCapital() != null) {
//                                        row.getCell(5).setCellValue(record.getReserveCapital());
//                                    }
//                                    if(record.getOtherReserves() != null) {
//                                        row.getCell(6).setCellValue(record.getOtherReserves());
//                                    }
//                                    if(record.getRetainedEarnings() != null) {
//                                        row.getCell(7).setCellValue(record.getRetainedEarnings());
//                                    }
//                                    if(record.getTotal() != null) {
//                                        row.getCell(8).setCellValue(record.getTotal());
//                                    }
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    //rowNum++;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.INDICATORS_NAME) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.LINE_CODE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
//                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
//                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
//                }
//            }
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//            // write to new
//            String filePath = tmpDir + "/CONS_KZT_FORM_6_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        }catch (IOException e) {
//
//            // TODO: log error
//            //e.printStackTrace();
//            logger.error("IO Exception when exporting KZT_FORM_6", e);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedForm6KZTReportInputStream(Long reportId) {

        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue(): null));
            return null;
        }
        List<ConsolidatedKZTForm6RecordDto> records = null;
        try {
            ListResponseDto responseDto = generateConsolidatedBalanceKZTForm6(reportId);
            if (responseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(responseDto.getMessage().getNameEn()) ? responseDto.getMessage().getNameEn() :
                        "Error occurred when generating KZT Form 6 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("KZT FORM 6 export (get records): " + errorMessage);
            }
            records = responseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_KZT_6.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_6.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 14;
            final int columnNum = 9;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedKZTForm6RecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);
                    added++;

                    for(int i = 0; i < columnNum; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getName());
                    newRow.getCell(1).setCellValue(record.getLineNumber() + "");
                    if (record.getShareholderEquity() != null) {
                        newRow.getCell(2).setCellValue(record.getShareholderEquity());
                    } else {
                        newRow.getCell(2).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if (record.getAdditionalPaidinCapital() != null) {
                        newRow.getCell(3).setCellValue(record.getAdditionalPaidinCapital());
                    }
                    if (record.getRedeemedOwnEquityInstruments() != null) {
                        newRow.getCell(4).setCellValue(record.getRedeemedOwnEquityInstruments());
                    }
                    if (record.getReserveCapital() != null) {
                        newRow.getCell(5).setCellValue(record.getReserveCapital());
                    }
                    if (record.getOtherReserves() != null) {
                        newRow.getCell(6).setCellValue(record.getOtherReserves());
                    }
                    if (record.getRetainedEarnings() != null) {
                        newRow.getCell(7).setCellValue(record.getRetainedEarnings());
                    }
                    if (record.getTotal() != null) {
                        newRow.getCell(8).setCellValue(record.getTotal());
                    }
                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_6_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        }catch (IOException e) {

            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_6", e);
        }

        return null;
    }

/*    private FilesDto getConsolidatedForm7KZTReportInputStream(Long reportId) {

        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
//        long start = System.currentTimeMillis();
        Map<Integer, List<ConsolidatedKZTForm7RecordDto>> recordsMap = null;
        try{
            recordsMap = getConsolidatedBalanceKZTForm7Map(reportId);
        }catch (IllegalStateException ex){
            throw ex;
        }

//        long end = System.currentTimeMillis();
//        System.out.println((end-start) / 1000 + " seconds - generating report data");

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_7.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_7.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
//            start = System.currentTimeMillis();
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");

//            start = System.currentTimeMillis();

            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            int rows = sheet.getLastRowNum();
            //int rowNum = 0;
            boolean startOfTable = false;
            boolean endOfTable = false;
            int addedRecords = 0;
            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if (startOfTable) {
                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_7_LAST_RECORD)) {
                        endOfTable = true;
                    }
                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
                        List<ConsolidatedKZTForm7RecordDto> records = recordsMap.get(lineNumber);
                        int recordsNum = records != null ? records.size() : 0;
                        Cell nameCell = endOfTable ? row.getCell(0) : row.getCell(1);
                        if(nameCell == null || nameCell.getCellType() != Cell.CELL_TYPE_STRING){
                            continue;
                        }
                        if (recordsNum > 0 && nameCell != null && nameCell.getCellType() == Cell.CELL_TYPE_STRING &&
                                records.get(0).getName().equalsIgnoreCase(nameCell.getStringCellValue())) {
                            // Set cell values
                            if(records.get(0).getEntityName() != null) {
                                row.getCell(3).setCellValue(records.get(0).getEntityName());
                            }
                            if(records.get(0).getOtherName() != null) {
                                row.getCell(4).setCellValue(records.get(0).getOtherName());
                            }
                            if(records.get(0).getPurchaseDate() != null) {
                                row.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
                                row.getCell(5).setCellValue(DateUtils.getDateFormatted(records.get(0).getPurchaseDate()));
                            }
                            if(records.get(0).getCurrency() != null) {
                                row.getCell(9).setCellType(Cell.CELL_TYPE_STRING);
                                row.getCell(9).setCellValue(records.get(0).getCurrency());
                            }
                            if(records.get(0).getDebtStartPeriod() != null) {
                                row.getCell(11).setCellValue(records.get(0).getDebtStartPeriod());
                            }
                            if(records.get(0).getInterestStartPeriod() != null) {
                                row.getCell(14).setCellValue(records.get(0).getInterestStartPeriod());
                            }
                            if(records.get(0).getFairValueAdjustmentsStartPeriod() != null) {
                                row.getCell(16).setCellValue(records.get(0).getFairValueAdjustmentsStartPeriod());
                            }
                            if(records.get(0).getTotalStartPeriod() != null) {
                                row.getCell(18).setCellValue(records.get(0).getTotalStartPeriod());
                            }
                            if(records.get(0).getDebtTurnover() != null) {
                                row.getCell(20).setCellValue(records.get(0).getDebtTurnover());
                            }
                            if(records.get(0).getInterestTurnover() != null) {
                                row.getCell(23).setCellValue(records.get(0).getInterestTurnover());
                            }
                            if(records.get(0).getFairValueAdjustmentsTurnoverPositive() != null) {
                                row.getCell(25).setCellValue(records.get(0).getFairValueAdjustmentsTurnoverPositive());
                            }
                            if(records.get(0).getFairValueAdjustmentsTurnoverNegative() != null) {
                                row.getCell(26).setCellValue(records.get(0).getFairValueAdjustmentsTurnoverNegative());
                            }
                            if(records.get(0).getDebtEndPeriod() != null) {
                                row.getCell(29).setCellValue(records.get(0).getDebtEndPeriod());
                            }
                            if(records.get(0).getInterestEndPeriod() != null) {
                                row.getCell(32).setCellValue(records.get(0).getInterestEndPeriod());
                            }
                            if(records.get(0).getFairValueAdjustmentsEndPeriod() != null) {
                                row.getCell(34).setCellValue(records.get(0).getFairValueAdjustmentsEndPeriod());
                            }
                            if(records.get(0).getTotalEndPeriod() != null) {
                                row.getCell(36).setCellValue(records.get(0).getTotalEndPeriod());
                            }
                        }
                        if (recordsNum > 1) {
                            sheet.shiftRows(row.getRowNum() + 1, rows + addedRecords, recordsNum - 1);
                            //insertRows(rowNum, row, sheet, records);
                            int index = 1;
                            for (int i = 1; i < records.size(); i++) {
                                Row newRow = sheet.createRow(row.getRowNum() + index);
                                newRow.createCell(0).setCellValue(records.get(i).getAccountNumber());
                                newRow.createCell(1).setCellValue(records.get(i).getName());

                                if(records.get(i).getEntityName() != null) {
                                    newRow.createCell(3).setCellValue(records.get(i).getEntityName());
                                }
                                if(records.get(i).getOtherName() != null) {
                                    newRow.createCell(4).setCellValue(records.get(i).getOtherName());
                                }
                                if(records.get(i).getPurchaseDate() != null) {
                                    //newRow.createCell(5).setCellType(Cell.CELL_TYPE_STRING);
                                    newRow.createCell(5).setCellType(Cell.CELL_TYPE_STRING);
                                    newRow.getCell(5).setCellValue(DateUtils.getDateFormatted(records.get(i).getPurchaseDate()));
                                }
                                if(records.get(i).getCurrency() != null) {
                                    newRow.createCell(9).setCellValue(records.get(i).getCurrency());
                                }
                                if(records.get(i).getDebtStartPeriod() != null) {
                                    newRow.createCell(11).setCellValue(records.get(i).getDebtStartPeriod());
                                }
                                if(records.get(i).getInterestStartPeriod() != null) {
                                    newRow.createCell(14).setCellValue(records.get(i).getInterestStartPeriod());
                                }
                                if(records.get(i).getFairValueAdjustmentsStartPeriod() != null) {
                                    newRow.createCell(16).setCellValue(records.get(i).getFairValueAdjustmentsStartPeriod());
                                }
                                if(records.get(i).getTotalStartPeriod() != null) {
                                    newRow.createCell(18).setCellValue(records.get(i).getTotalStartPeriod());
                                }
                                if(records.get(i).getDebtTurnover() != null) {
                                    newRow.createCell(20).setCellValue(records.get(i).getDebtTurnover());
                                }
                                if(records.get(i).getInterestTurnover() != null) {
                                    newRow.createCell(23).setCellValue(records.get(i).getInterestTurnover());
                                }
                                if(records.get(i).getFairValueAdjustmentsTurnoverPositive() != null) {
                                    newRow.createCell(25).setCellValue(records.get(i).getFairValueAdjustmentsTurnoverPositive());
                                }
                                if(records.get(i).getFairValueAdjustmentsTurnoverNegative() != null) {
                                    newRow.createCell(26).setCellValue(records.get(i).getFairValueAdjustmentsTurnoverNegative());
                                }
                                if(records.get(i).getDebtEndPeriod() != null) {
                                    newRow.createCell(29).setCellValue(records.get(i).getDebtEndPeriod());
                                }
                                if(records.get(i).getInterestEndPeriod() != null) {
                                    newRow.createCell(32).setCellValue(records.get(i).getInterestEndPeriod());
                                }
                                if(records.get(i).getFairValueAdjustmentsEndPeriod() != null) {
                                    newRow.createCell(34).setCellValue(records.get(i).getFairValueAdjustmentsEndPeriod());
                                }
                                if(records.get(i).getTotalEndPeriod() != null) {
                                    newRow.createCell(36).setCellValue(records.get(i).getTotalEndPeriod());
                                }

                                // set styles
                                for(int j = 0; j < 37; j++){
                                    if(newRow.getCell(j) == null){
                                        newRow.createCell(j);
                                    }
                                    if(row.getCell(j) != null && row.getCell(j).getCellStyle() != null) {
                                        CellStyle newCellStyle = workbook.createCellStyle();
                                        newCellStyle.cloneStyleFrom(row.getCell(j).getCellStyle());
                                        newRow.getCell(j).setCellStyle(newCellStyle);

                                        XSSFFont font = workbook.createFont();
                                        font.setFontHeightInPoints((short)8);
                                        font.setFontName("Times New Roman");
                                        font.setBold(false);
                                        font.setItalic(false);
                                        newRow.getCell(j).getCellStyle().setFont(font);
                                    }
                                }
                                index++;
                                addedRecords++;
                            }
                        }
                    }

                    //rowNum++;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.FIN_INVESTMENT_TYPE) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
                    startOfTable = true;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - updating workbook instance");

            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

//            start = System.currentTimeMillis();
            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_7_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_7", e);
        }

        return null;
    }*/

    private FilesDto getConsolidatedForm7KZTReportInputStream(Long reportId) {

        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue(): null));
            return null;
        }
//        long start = System.currentTimeMillis();
        List<ConsolidatedKZTForm7RecordDto> records = null;
        try {
            ListResponseDto responseDto = generateConsolidatedBalanceKZTForm7(reportId);
            if (responseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(responseDto.getMessage().getNameEn()) ? responseDto.getMessage().getNameEn() :
                        "Error occurred when generating KZT Form 7 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("KZT FORM 7 export (get records): " + errorMessage);
            }
            records = responseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }
//        long end = System.currentTimeMillis();
//        System.out.println((end-start) / 1000 + " seconds - generating report data");

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_KZT_7.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_7.xlsx'");
            return null;
            //e.printStackTrace();
        }

        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");
//            start = System.currentTimeMillis();
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 16;
            final int columnNum = 37;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedKZTForm7RecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);

                    for(int i = 0; i < columnNum; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getAccountNumber());
                    newRow.getCell(1).setCellValue(record.getName());
                    if (added + 1 == records.size()) { //last element
                        newRow.getCell(0).setCellValue(record.getName());
                        newRow.getCell(1).setCellValue("");
                    }

                    newRow.getCell(2).setCellValue(record.getAccountNumber() == null ? record.getLineNumber() + "": "");
                    if(record.getEntityName() != null) {
                        newRow.getCell(3).setCellValue(record.getEntityName());
                    }else{
                        newRow.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
                    }
                    if(record.getOtherName() != null) {
                        newRow.getCell(4).setCellValue(record.getOtherName());
                    }else{
                        newRow.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
                    }
                    if(record.getPurchaseDate() != null) {
                        newRow.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
                        newRow.getCell(5).setCellValue(DateUtils.getDateFormatted(records.get(0).getPurchaseDate()));
                    }else{
                        newRow.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
                    }
                    if(record.getCurrency() != null) {
                        newRow.getCell(9).setCellType(Cell.CELL_TYPE_STRING);
                        newRow.getCell(9).setCellValue(record.getCurrency());
                    }else{
                        newRow.getCell(9).setCellType(Cell.CELL_TYPE_STRING);
                    }
                    if(record.getDebtStartPeriod() != null) {
                        newRow.getCell(11).setCellValue(record.getDebtStartPeriod());
                    }else{
                        newRow.getCell(11).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if(record.getInterestStartPeriod() != null) {
                        newRow.getCell(14).setCellValue(record.getInterestStartPeriod());
                    }else{
                        newRow.getCell(14).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if(record.getFairValueAdjustmentsStartPeriod() != null) {
                        newRow.getCell(16).setCellValue(record.getFairValueAdjustmentsStartPeriod());
                    }else{
                        newRow.getCell(16).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if(record.getTotalStartPeriod() != null) {
                        newRow.getCell(18).setCellValue(record.getTotalStartPeriod());
                    }else{
                        newRow.getCell(18).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if(record.getDebtTurnover() != null) {
                        newRow.getCell(20).setCellValue(record.getDebtTurnover());
                    }else{
                        newRow.getCell(20).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if(record.getInterestTurnover() != null) {
                        newRow.getCell(23).setCellValue(record.getInterestTurnover());
                    }else{
                        newRow.getCell(23).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if(record.getFairValueAdjustmentsTurnoverPositive() != null) {
                        newRow.getCell(25).setCellValue(record.getFairValueAdjustmentsTurnoverPositive());
                    }
                    else{
                        newRow.getCell(25).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if(record.getFairValueAdjustmentsTurnoverNegative() != null) {
                        newRow.getCell(26).setCellValue(record.getFairValueAdjustmentsTurnoverNegative());
                    }else{
                        newRow.getCell(26).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if(record.getDebtEndPeriod() != null) {
                        newRow.getCell(29).setCellValue(record.getDebtEndPeriod());
                    }else{
                        newRow.getCell(29).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if(record.getInterestEndPeriod() != null) {
                        newRow.getCell(32).setCellValue(record.getInterestEndPeriod());
                    }else{
                        newRow.getCell(31).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if(record.getFairValueAdjustmentsEndPeriod() != null) {
                        newRow.getCell(34).setCellValue(record.getFairValueAdjustmentsEndPeriod());
                    }else{
                        newRow.getCell(34).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }
                    if(record.getTotalEndPeriod() != null) {
                        newRow.getCell(36).setCellValue(record.getTotalEndPeriod());
                    }else{
                        newRow.getCell(36).setCellType(Cell.CELL_TYPE_NUMERIC);
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    added++;

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);

                sheet.addMergedRegion(new CellRangeAddress(templateRowIndex + added - 1, templateRowIndex + added - 1,0, 1));
                sheet.getRow(templateRowIndex + added - 1).getCell(0).getCellStyle().setAlignment(HorizontalAlignment.LEFT);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
//                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                } else
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

//            start = System.currentTimeMillis();
            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_7_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_7", e);
        }

        return null;
    }

//    private FilesDto getConsolidatedForm8KZTReportInputStream(Long reportId) {
//
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + (reportId != null ? reportId.longValue(): null));
//            return null;
//        }
//        long start = System.currentTimeMillis();
//        Map<Integer, List<ConsolidatedKZTForm8RecordDto>> recordsMap = null;
//        try{
//            recordsMap = getConsolidatedBalanceKZTForm8Map(reportId);
//        }catch (IllegalStateException ex){
//            throw ex;
//        }
//
////        long end = System.currentTimeMillis();
////        System.out.println((end-start) / 1000 + " seconds - generating report data");
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_8.xlsx");
//        InputStream excelFileToRead = null;
//        try {
//            excelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_8.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//        try {
////            start = System.currentTimeMillis();
//            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");
//
//            start = System.currentTimeMillis();
//
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            int rows = sheet.getLastRowNum();
//            //int rowNum = 0;
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            int addedRecords = 0;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_8_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
//                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
//                        List<ConsolidatedKZTForm8RecordDto> records = recordsMap.get(lineNumber);
//                        int recordsNum = records != null ? records.size() : 0;
//                        Cell nameCell = endOfTable ? row.getCell(0) : row.getCell(1);
//                        if (recordsNum > 0 && nameCell != null && nameCell.getCellType() == Cell.CELL_TYPE_STRING &&
//                                records.get(0).getName().equalsIgnoreCase(nameCell.getStringCellValue())) {
//                            // Set cell values
//                            if(records.get(0).getDebtStartPeriod() != null) {
//                                row.getCell(3).setCellValue(records.get(0).getDebtStartPeriod());
//                            }
//                            if(records.get(0).getDebtEndPeriod() != null) {
//                                row.getCell(4).setCellValue(records.get(0).getDebtEndPeriod());
//                            }
//                            if(records.get(0).getDebtDifference() != null) {
//                                row.getCell(5).setCellValue(records.get(0).getDebtDifference());
//                            }
//                            if(records.get(0).getAgreementDescription() != null) {
//                                row.getCell(6).setCellValue(records.get(0).getAgreementDescription());
//                            }
//                            if(records.get(0).getDebtStartDate() != null) {
//                                row.getCell(7).setCellValue(DateUtils.getDateFormatted(records.get(0).getDebtStartDate()));
//                            }
//                            if(records.get(0).getStartPeriodBalance() != null) {
//                                row.getCell(16).setCellValue(records.get(0).getStartPeriodBalance());
//                            }
//                            if(records.get(0).getEndPeriodBalance() != null) {
//                                row.getCell(17).setCellValue(records.get(0).getEndPeriodBalance());
//                            }
//
//                        }
//                        if (recordsNum > 1) {
//                            sheet.shiftRows(row.getRowNum() + 1, rows + addedRecords, recordsNum - 1);
//                            //insertRows(rowNum, row, sheet, records);
//                            int index = 1;
//                            for (int i = 1; i < records.size(); i++) {
//                                Row newRow = sheet.createRow(row.getRowNum() + index);
//                                newRow.createCell(0).setCellValue(records.get(i).getAccountNumber());
//                                newRow.createCell(1).setCellValue(records.get(i).getName());
//
//                                if(records.get(i).getDebtStartPeriod() != null) {
//                                    newRow.createCell(3).setCellValue(records.get(i).getDebtStartPeriod());
//                                }
//                                if(records.get(i).getDebtEndPeriod() != null) {
//                                    newRow.createCell(4).setCellValue(records.get(i).getDebtEndPeriod());
//                                }
//                                if(records.get(i).getDebtDifference() != null) {
//                                    newRow.createCell(5).setCellValue(records.get(i).getDebtDifference());
//                                }
//                                if(records.get(i).getAgreementDescription() != null) {
//                                    newRow.createCell(6).setCellValue(records.get(i).getAgreementDescription());
//                                }
//                                if(records.get(i).getDebtStartDate() != null) {
//                                    newRow.createCell(7).setCellValue(DateUtils.getDateFormatted(records.get(i).getDebtStartDate()));
//                                }
//                                if(records.get(i).getStartPeriodBalance() != null) {
//                                    newRow.createCell(16).setCellValue(records.get(i).getStartPeriodBalance());
//                                }
//                                if(records.get(i).getEndPeriodBalance() != null) {
//                                    newRow.createCell(17).setCellValue(records.get(i).getEndPeriodBalance());
//                                }
//
//                                // set styles
//                                for(int j = 0; j < 18; j++){
//                                    if(newRow.getCell(j) == null){
//                                        newRow.createCell(j);
//                                    }
//
//                                    if(row.getCell(j) != null && row.getCell(j).getCellStyle() != null) {
//                                        CellStyle newCellStyle = workbook.createCellStyle();
//                                        newCellStyle.cloneStyleFrom(row.getCell(j).getCellStyle());
//                                        newRow.getCell(j).setCellStyle(newCellStyle);
//
//                                        XSSFFont font = workbook.createFont();
//                                        font.setFontHeightInPoints((short)11);
//                                        font.setFontName("Times New Roman");
//                                        font.setBold(false);
//                                        font.setItalic(false);
//                                        newRow.getCell(j).getCellStyle().setFont(font);
//                                    }
//                                }
//                                index++;
//                                addedRecords++;
//                            }
//                        }
//                    }
//
//                    //rowNum++;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.DEBTOR_NAME) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
//                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
//                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
//                }
//            }
//
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds - updating workbook instance");
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//            start = System.currentTimeMillis();
//            // write to new
//            String filePath = tmpDir + "/CONS_KZT_FORM_8_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        } catch (IOException e) {
//            // TODO: log error
//            //e.printStackTrace();
//            logger.error("IO Exception when exporting KZT_FORM_8", e);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedForm8KZTReportInputStream(Long reportId) {
        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
            return null;
        }
        List<ConsolidatedKZTForm8RecordDto> records = null;
        try {
            ListResponseDto KZTForm1ResponseDto = generateConsolidatedBalanceKZTForm8(reportId);
            if (KZTForm1ResponseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(KZTForm1ResponseDto.getMessage().getNameEn()) ? KZTForm1ResponseDto.getMessage().getNameEn() :
                        "Error occurred when generating KZT Form 8 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("KZT FORM 8 export (get records): " + errorMessage);
            }
            records = KZTForm1ResponseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_KZT_8.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_8.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 16;
            final int columnNum = 18;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedKZTForm8RecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + added + 1);

                    for (int i = 0; i < columnNum; i++) {
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getAccountNumber());
                    newRow.getCell(1).setCellValue(record.getName());
                    if (added + 1 == records.size()) { //last element
                        newRow.getCell(0).setCellValue(record.getName());
                        newRow.getCell(1).setCellValue("");
                    }

                    newRow.getCell(2).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");
                    if (record.getDebtStartPeriod() != null) {
                        newRow.getCell(3).setCellValue(record.getDebtStartPeriod());
                    }
                    if (record.getDebtEndPeriod() != null) {
                        newRow.getCell(4).setCellValue(record.getDebtEndPeriod());
                    }
                    if (record.getDebtDifference() != null) {
                        newRow.getCell(5).setCellValue(record.getDebtDifference());
                    }
                    if (record.getAgreementDescription() != null) {
                        newRow.getCell(6).setCellValue(record.getAgreementDescription());
                    }
                    if (record.getDebtStartDate() != null) {
                        newRow.getCell(7).setCellValue(DateUtils.getDateFormatted(record.getDebtStartDate()));
                    }
                    if (record.getStartPeriodBalance() != null) {
                        newRow.getCell(16).setCellValue(record.getStartPeriodBalance());
                    }
                    if (record.getEndPeriodBalance() != null) {
                        newRow.getCell(17).setCellValue(record.getEndPeriodBalance());
                    }

                    // set styles
                    for (int i = 0; i < columnNum; i++) {
                        if (newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null) {
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    added++;

                    // TODO: bold
                }

                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);

                sheet.addMergedRegion(new CellRangeAddress(templateRowIndex + added - 1, templateRowIndex + added - 1,0, 1));
                sheet.getRow(templateRowIndex + added - 1).getCell(0).getCellStyle().setAlignment(HorizontalAlignment.LEFT);

            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }
            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_8_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_8", e);
        }

        return null;
    }

//    private FilesDto getConsolidatedForm10KZTReportInputStream(Long reportId) {
//
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + reportId);
//            return null;
//        }
//        long start = System.currentTimeMillis();
//        Map<Integer, List<ConsolidatedKZTForm10RecordDto>> recordsMap = null;
//        try{
//            recordsMap = getConsolidatedBalanceKZTForm10Map(reportId);
//        }catch (IllegalStateException ex){
//            throw ex;
//        }
////        long end = System.currentTimeMillis();
////        System.out.println((end-start) / 1000 + " seconds - generating report data");
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_10.xlsx");
//        InputStream excelFileToRead = null;
//        try {
//            excelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_10.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//
//
//        try {
////            start = System.currentTimeMillis();
//            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");
//
////            start = System.currentTimeMillis();
//
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            int rows = sheet.getLastRowNum();
//            //int rowNum = 0;
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            int addedRecords = 0;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_10_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//
//                    if (row.getCell(0) != null && row.getCell(0).getCellType() != Cell.CELL_TYPE_NUMERIC &&
//                            row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
//                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
//                        List<ConsolidatedKZTForm10RecordDto> records = recordsMap.get(lineNumber);
//                        int recordsNum = records != null ? records.size() : 0;
//                        Cell nameCell = endOfTable ? row.getCell(0) : row.getCell(1);
//                        if (recordsNum > 0 && nameCell != null && nameCell.getCellType() == Cell.CELL_TYPE_STRING &&
//                                records.get(0).getName().equalsIgnoreCase(nameCell.getStringCellValue())) {
//                            // Set cell values
//                            if(records.get(0).getStartPeriodAssets() != null) {
//                                row.getCell(4).setCellValue(records.get(0).getStartPeriodAssets());
//                            }
//                            if(records.get(0).getTurnoverOther() != null) {
//                                row.getCell(14).setCellValue(records.get(0).getTurnoverOther());
//                            }
//                            if(records.get(0).getEndPeriodAssets() != null) {
//                                row.getCell(16).setCellValue(records.get(0).getEndPeriodAssets());
//                            }
//                            if(records.get(0).getStartPeriodBalance() != null) {
//                                row.getCell(24).setCellValue(records.get(0).getStartPeriodBalance());
//                            }
//                            if(records.get(0).getEndPeriodBalance() != null) {
//                                row.getCell(25).setCellValue(records.get(0).getEndPeriodBalance());
//                            }
//
//                        }
//                        if (recordsNum > 1) {
//                            sheet.shiftRows(row.getRowNum() + 1, rows + addedRecords, recordsNum - 1);
//                            //insertRows(rowNum, row, sheet, records);
//                            int index = 1;
//                            for (int i = 1; i < records.size(); i++) {
//                                Row newRow = sheet.createRow(row.getRowNum() + index);
//                                newRow.createCell(0).setCellValue(records.get(i).getAccountNumber());
//                                newRow.createCell(1).setCellValue(records.get(i).getName());
//
//                                if(records.get(i).getStartPeriodAssets() != null) {
//                                    newRow.createCell(4).setCellValue(records.get(i).getStartPeriodAssets());
//                                }
//                                if(records.get(i).getTurnoverOther() != null) {
//                                    newRow.createCell(14).setCellValue(records.get(i).getTurnoverOther());
//                                }
//                                if(records.get(i).getEndPeriodAssets() != null) {
//                                    newRow.createCell(16).setCellValue(records.get(i).getEndPeriodAssets());
//                                }
//                                if(records.get(i).getStartPeriodBalance() != null) {
//                                    newRow.createCell(24).setCellValue(records.get(i).getStartPeriodBalance());
//                                }
//                                if(records.get(i).getEndPeriodBalance() != null) {
//                                    newRow.createCell(25).setCellValue(records.get(i).getEndPeriodBalance());
//                                }
//
//                                // set styles
//                                for(int j = 0; j < 26; j++){
//                                    if(newRow.getCell(j) == null){
//                                        newRow.createCell(j);
//                                    }
//                                    if(row.getCell(j) != null && row.getCell(j).getCellStyle() != null) {
//                                        newRow.getCell(j).setCellStyle(row.getCell(j).getCellStyle());
//                                    }
//                                }
//                                index++;
//                                addedRecords++;
//                            }
//                        }
//                    }
//
//                    //rowNum++;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.ASSETS_DEBTOR_NAME) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
//                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
//                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
//                }
//            }
//
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds - updating workbook instance");
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//            start = System.currentTimeMillis();
//            // write to new
//            String filePath = tmpDir + "/CONS_KZT_FORM_10_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        } catch (IOException e) {
//            // TODO: log error
//            //e.printStackTrace();
//            logger.error("IO Exception when exporting KZT_FORM_10", e);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedForm10KZTReportInputStream(Long reportId) {
        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
            return null;
        }
        List<ConsolidatedKZTForm10RecordDto> records = null;
        try {
            ListResponseDto KZTForm1ResponseDto = generateConsolidatedBalanceKZTForm10(reportId);
            if (KZTForm1ResponseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(KZTForm1ResponseDto.getMessage().getNameEn()) ? KZTForm1ResponseDto.getMessage().getNameEn() :
                        "Error occurred when generating KZT Form 10 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("KZT FORM 10 export (get records): " + errorMessage);
            }
            records = KZTForm1ResponseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_KZT_10.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_10.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 17;
            final int columnNum = 26;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedKZTForm10RecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);

                    for(int i = 0; i < columnNum ; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getAccountNumber());
                    newRow.getCell(1).setCellValue(record.getName());
                    if (added + 1 == records.size()) { //last element
                        newRow.getCell(0).setCellValue(record.getName());
                        newRow.getCell(1).setCellValue("");
                    }
                    newRow.getCell(2).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");
                    if(record.getStartPeriodAssets() != null) {
                        newRow.getCell(4).setCellValue(record.getStartPeriodAssets());
                    }
                    if(record.getTurnoverPurchased() != null) {
                        newRow.getCell(6).setCellValue(record.getTurnoverPurchased());
                    }
                    if(record.getTurnoverOther() != null) {
                        newRow.getCell(14).setCellValue(record.getTurnoverOther());
                    }
                    if(record.getEndPeriodAssets() != null) {
                        newRow.getCell(16).setCellValue(record.getEndPeriodAssets());
                    }
                    if(record.getStartPeriodBalance() != null) {
                        newRow.getCell(24).setCellValue(record.getStartPeriodBalance());
                    }
                    if(record.getEndPeriodBalance() != null) {
                        newRow.getCell(25).setCellValue(record.getEndPeriodBalance());
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    added++;

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);

                // merger last row cells 0 and 1
                sheet.addMergedRegion(new CellRangeAddress(templateRowIndex + added - 1, templateRowIndex + added - 1,0, 1));
                sheet.getRow(templateRowIndex + added - 1).getCell(0).getCellStyle().setAlignment(HorizontalAlignment.LEFT);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_10_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_10", e);
        }

        return null;
    }

//    private FilesDto getConsolidatedForm13KZTReportInputStream(Long reportId) {
//
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + reportId);
//            return null;
//        }
//        long start = System.currentTimeMillis();
//        Map<Integer, List<ConsolidatedKZTForm13RecordDto>> recordsMap = null;
//        try{
//            recordsMap = getConsolidatedBalanceKZTForm13Map(reportId);
//        }catch (IllegalStateException ex){
//            throw ex;
//        }
////        long end = System.currentTimeMillis();
////        System.out.println((end-start) / 1000 + " seconds - generating report data");
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_13.xlsx");
//        InputStream excelFileToRead = null;
//        try {
//            excelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_13.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//        try {
////            start = System.currentTimeMillis();
//            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");
//
//            start = System.currentTimeMillis();
//
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            int rows = sheet.getLastRowNum();
//            //int rowNum = 0;
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            int addedRecords = 0;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_13_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//
//                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
//                        if(ExcelUtils.getTextValueFromAnyCell(row.getCell(0)) != null && ExcelUtils.getTextValueFromAnyCell(row.getCell(0)).equalsIgnoreCase("1.0") &&
//                                ExcelUtils.getTextValueFromAnyCell(row.getCell(1)) != null && ExcelUtils.getTextValueFromAnyCell(row.getCell(1)).equalsIgnoreCase("2.0")){
//                            continue;
//                        }
//                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
//                        List<ConsolidatedKZTForm13RecordDto> records = recordsMap.get(lineNumber);
//                        int recordsNum = records != null ? records.size() : 0;
//                        Cell nameCell = endOfTable ? row.getCell(0) : row.getCell(1);
//                        if (recordsNum > 0 && nameCell != null && nameCell.getCellType() == Cell.CELL_TYPE_STRING &&
//                                records.get(0).getName().equalsIgnoreCase(nameCell.getStringCellValue())) {
//                            // Set cell values
//
//                            if(records.get(0).getEntityName() != null) {
//                                row.getCell(3).setCellValue(records.get(0).getEntityName());
//                            }
//                            if(records.get(0).getStartPeriod() != null) {
//                                row.getCell(4).setCellValue(DateUtils.getDateFormatted(records.get(0).getStartPeriod()));
//                            }
//                            if(records.get(0).getEndPeriod() != null) {
//                                row.getCell(5).setCellValue(DateUtils.getDateFormatted(records.get(0).getEndPeriod()));
//                            }
//                            if(records.get(0).getInterestRate() != null) {
//                                row.getCell(6).setCellValue(records.get(0).getInterestRate());
//                            }
//                            if(records.get(0).getInterestPaymentCount() != null) {
//                                row.getCell(7).setCellValue(records.get(0).getInterestPaymentCount());
//                            }
//                            if(records.get(0).getCurrency() != null) {
//                                row.getCell(8).setCellValue(records.get(0).getCurrency());
//                            }
//                            if(records.get(0).getDebtStartPeriod() != null) {
//                                row.getCell(10).setCellValue(records.get(0).getDebtStartPeriod());
//                            }
//                            if(records.get(0).getInterestStartPeriod() != null) {
//                                row.getCell(13).setCellValue(records.get(0).getInterestStartPeriod());
//                            }
//                            if(records.get(0).getTotalStartPeriod() != null) {
//                                row.getCell(16).setCellValue(records.get(0).getTotalStartPeriod());
//                            }
//                            if(records.get(0).getDebtTurnover() != null) {
//                                row.getCell(18).setCellValue(records.get(0).getDebtTurnover());
//                            }
//                            if(records.get(0).getInterestTurnover() != null) {
//                                row.getCell(21).setCellValue(records.get(0).getInterestTurnover());
//                            }
//                            if(records.get(0).getDebtEndPeriod() != null) {
//                                row.getCell(25).setCellValue(records.get(0).getDebtEndPeriod());
//                            }
//                            if(records.get(0).getInterestEndPeriod() != null) {
//                                row.getCell(28).setCellValue(records.get(0).getInterestEndPeriod());
//                            }
//                            if(records.get(0).getTotalEndPeriod() != null) {
//                                row.getCell(31).setCellValue(records.get(0).getTotalEndPeriod());
//                            }
//
//                        }
//                        if (recordsNum > 1) {
//                            sheet.shiftRows(row.getRowNum() + 1, rows + addedRecords, recordsNum - 1);
//                            //insertRows(rowNum, row, sheet, records);
//                            int index = 1;
//                            for (int i = 1; i < records.size(); i++) {
//                                Row newRow = sheet.createRow(row.getRowNum() + index);
//                                newRow.createCell(0).setCellValue(records.get(i).getAccountNumber());
//                                newRow.createCell(1).setCellValue(records.get(i).getName());
//
//                                if(records.get(i).getEntityName() != null) {
//                                    newRow.createCell(3).setCellValue(records.get(i).getEntityName());
//                                }
//                                if(records.get(i).getStartPeriod() != null) {
//                                    newRow.createCell(4).setCellValue(DateUtils.getDateFormatted(records.get(i).getStartPeriod()));
//                                }
//                                if(records.get(i).getEndPeriod() != null) {
//                                    newRow.createCell(5).setCellValue(DateUtils.getDateFormatted(records.get(i).getEndPeriod()));
//                                }
//                                if(records.get(i).getInterestRate() != null) {
//                                    Cell cell = newRow.createCell(6);
//                                    if(records.get(i).getInterestRateAsDouble() != null){
//                                        cell.setCellValue(records.get(i).getInterestRateAsDouble()); // set value as number
//                                        CellStyle style = workbook.createCellStyle();
//                                        style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
//                                        cell.setCellStyle(style);
//                                    }
//                                }
//                                if(records.get(i).getInterestPaymentCount() != null) {
//                                    newRow.createCell(7).setCellValue(records.get(i).getInterestPaymentCount());
//                                }
//                                if(records.get(i).getCurrency() != null) {
//                                    newRow.createCell(8).setCellValue(records.get(i).getCurrency());
//                                }
//                                if(records.get(i).getDebtStartPeriod() != null) {
//                                    newRow.createCell(10).setCellValue(records.get(i).getDebtStartPeriod());
//                                }
//                                if(records.get(i).getInterestStartPeriod() != null) {
//                                    newRow.createCell(13).setCellValue(records.get(i).getInterestStartPeriod());
//                                }
//                                if(records.get(i).getTotalStartPeriod() != null) {
//                                    newRow.createCell(16).setCellValue(records.get(i).getTotalStartPeriod());
//                                }
//                                if(records.get(i).getDebtTurnover() != null) {
//                                    newRow.createCell(18).setCellValue(records.get(i).getDebtTurnover());
//                                }
//                                if(records.get(i).getInterestTurnover() != null) {
//                                    newRow.createCell(21).setCellValue(records.get(i).getInterestTurnover());
//                                }
//                                if(records.get(i).getDebtEndPeriod() != null) {
//                                    newRow.createCell(25).setCellValue(records.get(i).getDebtEndPeriod());
//                                }
//                                if(records.get(i).getInterestEndPeriod() != null) {
//                                    newRow.createCell(28).setCellValue(records.get(i).getInterestEndPeriod());
//                                }
//                                if(records.get(i).getTotalEndPeriod() != null) {
//                                    newRow.createCell(31).setCellValue(records.get(i).getTotalEndPeriod());
//                                }
//
//                                // set styles
//                                for(int j = 0; j < 32; j++){
//                                    if(newRow.getCell(j) == null){
//                                        newRow.createCell(j);
//                                    }
//                                    if(row.getCell(j) != null && row.getCell(j).getCellStyle() != null) {
//                                        newRow.getCell(j).setCellStyle(row.getCell(j).getCellStyle());
//                                    }
//                                }
//                                index++;
//                                addedRecords++;
//                            }
//                        }
//                    }
//
//                    //rowNum++;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.FIN_LIABILITIES_TYPE) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
//                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
//                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
//                }
//            }
//
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds - updating workbook instance");
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//            start = System.currentTimeMillis();
//            // write to new
//            String filePath = tmpDir + "/CONS_KZT_FORM_13_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        }catch (IOException e) {
//            // TODO: log error
//            //e.printStackTrace();
//            logger.error("IO Exception when exporting KZT_FORM_13", e);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedForm13KZTReportInputStream(Long reportId) {
        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
            return null;
        }
        List<ConsolidatedKZTForm13RecordDto> records = null;
        try {
            ListResponseDto KZTForm1ResponseDto = generateConsolidatedBalanceKZTForm13(reportId);
            if (KZTForm1ResponseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(KZTForm1ResponseDto.getMessage().getNameEn()) ? KZTForm1ResponseDto.getMessage().getNameEn() :
                        "Error occurred when generating KZT Form 13 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("KZT FORM 13 export (get records): " + errorMessage);
            }
            records = KZTForm1ResponseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_KZT_13.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_13.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 15;
            final int columnNum = 32;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedKZTForm13RecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);

                    for(int i = 0; i < columnNum ; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getAccountNumber());
                    newRow.getCell(1).setCellValue(record.getName());
                    if (added + 1 == records.size()) { //last element
                        newRow.getCell(0).setCellValue(record.getName());
                        newRow.getCell(1).setCellValue("");
                    }
                    newRow.getCell(2).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");
                    // Set cell values
                    if(record.getEntityName() != null) {
                        newRow.getCell(3).setCellValue(record.getEntityName());
                    }
                    if(record.getStartPeriod() != null) {
                        newRow.getCell(4).setCellValue(DateUtils.getDateFormatted(record.getStartPeriod()));
                    }
                    if(record.getEndPeriod() != null) {
                        newRow.getCell(5).setCellValue(DateUtils.getDateFormatted(record.getEndPeriod()));
                    }
                    if(record.getInterestRate() != null) {
                        newRow.getCell(6).setCellValue(record.getInterestRate());
                    }
                    if(record.getInterestPaymentCount() != null) {
                        newRow.getCell(7).setCellValue(record.getInterestPaymentCount());
                    }
                    if(record.getCurrency() != null) {
                        newRow.getCell(8).setCellValue(record.getCurrency());
                    }
                    if(record.getDebtStartPeriod() != null) {
                        newRow.getCell(10).setCellValue(record.getDebtStartPeriod());
                    }
                    if(record.getInterestStartPeriod() != null) {
                        newRow.getCell(13).setCellValue(record.getInterestStartPeriod());
                    }
                    if(record.getTotalStartPeriod() != null) {
                        newRow.getCell(16).setCellValue(record.getTotalStartPeriod());
                    }
                    if(record.getDebtTurnover() != null) {
                        newRow.getCell(18).setCellValue(record.getDebtTurnover());
                    }
                    if(record.getInterestTurnover() != null) {
                        newRow.getCell(21).setCellValue(record.getInterestTurnover());
                    }
                    if(record.getDebtEndPeriod() != null) {
                        newRow.getCell(25).setCellValue(record.getDebtEndPeriod());
                    }
                    if(record.getInterestEndPeriod() != null) {
                        newRow.getCell(28).setCellValue(record.getInterestEndPeriod());
                    }
                    if(record.getTotalEndPeriod() != null) {
                        newRow.getCell(31).setCellValue(record.getTotalEndPeriod());
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }
                    added++;

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);

                // merger last row cells 0 and 1
                sheet.addMergedRegion(new CellRangeAddress(templateRowIndex + added - 1, templateRowIndex + added - 1,0, 1));
                sheet.getRow(templateRowIndex + added - 1).getCell(0).getCellStyle().setAlignment(HorizontalAlignment.LEFT);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }
            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_13_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        }catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_13", e);
        }

        return null;
    }

    /*private FilesDto getConsolidatedForm14KZTReportInputStream(Long reportId) {

        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        Map<Integer, List<ConsolidatedKZTForm14RecordDto>> recordsMap = null;
        try{
            recordsMap = getConsolidatedBalanceKZTForm14Map(reportId);
        }catch (IllegalStateException ex){
            throw ex;
        }
//        long end = System.currentTimeMillis();
//        System.out.println((end-start) / 1000 + " seconds - generating report data");

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_14.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_14.xlsx'");
            return null;
            //e.printStackTrace();
        }

        try {
//            start = System.currentTimeMillis();
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");

            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            int rows = sheet.getLastRowNum();
            //int rowNum = 0;
            boolean startOfTable = false;
            boolean endOfTable = false;
            int addedRecords = 0;
            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if (startOfTable) {
                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_14_LAST_RECORD)) {
                        endOfTable = true;
                    }

                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
                        List<ConsolidatedKZTForm14RecordDto> records = recordsMap.get(lineNumber);
                        int recordsNum = records != null ? records.size() : 0;
                        Cell nameCell = endOfTable ? row.getCell(0) : row.getCell(1);
                        if (recordsNum > 0 && nameCell != null && nameCell.getCellType() == Cell.CELL_TYPE_STRING &&
                                records.get(0).getName().equalsIgnoreCase(nameCell.getStringCellValue())) {
                            // Set cell values

                            if(records.get(0).getDebtStartPeriod() != null) {
                                row.getCell(3).setCellValue(records.get(0).getDebtStartPeriod());
                            }
                            if(records.get(0).getDebtEndPeriod() != null) {
                                row.getCell(4).setCellValue(records.get(0).getDebtEndPeriod());
                            }
                            if(records.get(0).getDebtDifference() != null) {
                                row.getCell(5).setCellValue(records.get(0).getDebtDifference());
                            }
                            if(records.get(0).getAgreementDescription() != null) {
                                row.getCell(6).setCellValue(records.get(0).getAgreementDescription());
                            }
                            if(records.get(0).getDebtStartDate() != null) {
                                row.getCell(7).setCellValue(DateUtils.getDateFormatted(records.get(0).getDebtStartDate()));
                            }

                        }
                        if (recordsNum > 1) {
                            sheet.shiftRows(row.getRowNum() + 1, rows + addedRecords, recordsNum - 1);
                            //insertRows(rowNum, row, sheet, records);
                            int index = 1;
                            for (int i = 1; i < records.size(); i++) {
                                Row newRow = sheet.createRow(row.getRowNum() + index);
                                newRow.createCell(0).setCellValue(records.get(i).getAccountNumber());
                                newRow.createCell(1).setCellValue(records.get(i).getName());

                                if(records.get(i).getDebtStartPeriod() != null) {
                                    newRow.createCell(3).setCellValue(records.get(i).getDebtStartPeriod());
                                }
                                if(records.get(i).getDebtEndPeriod() != null) {
                                    newRow.createCell(4).setCellValue(records.get(i).getDebtEndPeriod());
                                }
                                if(records.get(i).getDebtDifference() != null) {
                                    newRow.createCell(5).setCellValue(records.get(i).getDebtDifference());
                                }
                                if(records.get(i).getAgreementDescription() != null) {
                                    newRow.createCell(6).setCellValue(records.get(i).getAgreementDescription());
                                }
                                if(records.get(i).getDebtStartDate() != null) {
                                    newRow.createCell(7).setCellValue(DateUtils.getDateFormatted(records.get(i).getDebtStartDate()));
                                }

                                // set styles
                                for(int j = 0; j < 12; j++){
                                    if(newRow.getCell(j) == null){
                                        newRow.createCell(j);
                                    }
                                    if(row.getCell(j) != null && row.getCell(j).getCellStyle() != null) {
                                        newRow.getCell(j).setCellStyle(row.getCell(j).getCellStyle());
                                    }
                                }
                                index++;
                                addedRecords++;
                            }
                        }
                    }

                    //rowNum++;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.CREDITOR_NAME) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
                    startOfTable = true;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - updating workbook instance");

            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_14_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_14", e);
        }

        return null;
    }*/

    private FilesDto getConsolidatedForm14KZTReportInputStream(Long reportId) {
        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
            return null;
        }
        List<ConsolidatedKZTForm14RecordDto> records = null;
        try {
            ListResponseDto KZTForm1ResponseDto = generateConsolidatedBalanceKZTForm14(reportId);
            if (KZTForm1ResponseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(KZTForm1ResponseDto.getMessage().getNameEn()) ? KZTForm1ResponseDto.getMessage().getNameEn() :
                        "Error occurred when generating KZT Form 14 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("KZT FORM 14 export (get records): " + errorMessage);
            }
            records = KZTForm1ResponseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_KZT_14.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_14.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 16;
            final int columnNum = 12;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedKZTForm14RecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);

                    for(int i = 0; i < columnNum ; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getAccountNumber());
                    newRow.getCell(1).setCellValue(record.getName());
                    if (added + 1 == records.size()) { //last element
                        newRow.getCell(0).setCellValue(record.getName());
                        newRow.getCell(1).setCellValue("");
                    }
                    newRow.getCell(2).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");
                    // Set cell values
                    if(record.getDebtStartPeriod() != null) {
                        newRow.getCell(3).setCellValue(record.getDebtStartPeriod());
                    }
                    if(record.getDebtEndPeriod() != null) {
                        newRow.getCell(4).setCellValue(record.getDebtEndPeriod());
                    }
                    if(record.getDebtDifference() != null) {
                        newRow.getCell(5).setCellValue(record.getDebtDifference());
                    }
                    if(record.getAgreementDescription() != null) {
                        newRow.getCell(6).setCellValue(record.getAgreementDescription());
                    }
                    if(record.getDebtStartDate() != null) {
                        newRow.getCell(7).setCellValue(DateUtils.getDateFormatted(record.getDebtStartDate()));
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    added++;

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);

                // merger last row cells 0 and 1
                sheet.addMergedRegion(new CellRangeAddress(templateRowIndex + added - 1, templateRowIndex + added - 1,0, 1));
                sheet.getRow(templateRowIndex + added - 1).getCell(0).getCellStyle().setAlignment(HorizontalAlignment.LEFT);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }
            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_14_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_14", e);
        }

        return null;
    }

//    private FilesDto getConsolidatedForm19KZTReportInputStream(Long reportId) {
//
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + reportId);
//            return null;
//        }
//        long start = System.currentTimeMillis();
//        Map<Integer, List<ConsolidatedKZTForm19RecordDto>> recordsMap = null;
//        try{
//            recordsMap = getConsolidatedBalanceKZTForm19Map(reportId);
//        }catch (IllegalStateException ex){
//            throw ex;
//        }
//
////        long end = System.currentTimeMillis();
////        System.out.println((end-start) / 1000 + " seconds - generating report data");
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_19.xlsx");
//        InputStream excelFileToRead = null;
//        try {
//            excelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_19.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//
//
//        try {
////            start = System.currentTimeMillis();
//            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");
//
//            start = System.currentTimeMillis();
//
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            int rows = sheet.getLastRowNum();
//            //int rowNum = 0;
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            int addedRecords = 0;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_19_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//
//                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
//                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
//                        List<ConsolidatedKZTForm19RecordDto> records = recordsMap.get(lineNumber);
//                        int recordsNum = records != null ? records.size() : 0;
//                        Cell nameCell = endOfTable ? row.getCell(0) : row.getCell(1);
//                        if (recordsNum > 0 && nameCell != null && nameCell.getCellType() == Cell.CELL_TYPE_STRING &&
//                                records.get(0).getName().equalsIgnoreCase(nameCell.getStringCellValue())) {
//                            // Set cell values
//
//                            if(records.get(0).getOtherEntityName() != null) {
//                                row.getCell(3).setCellValue(records.get(0).getOtherEntityName());
//                            }
//                            if(records.get(0).getPreviousAccountBalance() != null) {
//                                row.getCell(4).setCellValue(records.get(0).getPreviousAccountBalance());
//                            }
//                            if(records.get(0).getTurnover() != null) {
//                                row.getCell(5).setCellValue(records.get(0).getTurnover());
//                            }
//                            if(records.get(0).getCurrentAccountBalance() != null) {
//                                row.getCell(6).setCellValue(records.get(0).getCurrentAccountBalance());
//                            }
//                        }
//                        if (recordsNum > 1) {
//                            sheet.shiftRows(row.getRowNum() + 1, rows + addedRecords, recordsNum - 1);
//                            //insertRows(rowNum, row, sheet, records);
//                            int index = 1;
//                            for (int i = 1; i < records.size(); i++) {
//                                Row newRow = sheet.createRow(row.getRowNum() + index);
//                                newRow.createCell(0).setCellValue(records.get(i).getAccountNumber());
//                                newRow.createCell(1).setCellValue(records.get(i).getName());
//
//                                if(records.get(i).getOtherEntityName() != null) {
//                                    newRow.createCell(3).setCellValue(records.get(i).getOtherEntityName());
//                                }
//                                if(records.get(i).getPreviousAccountBalance() != null) {
//                                    newRow.createCell(4).setCellValue(records.get(i).getPreviousAccountBalance());
//                                }
//                                if(records.get(i).getTurnover() != null) {
//                                    newRow.createCell(5).setCellValue(records.get(i).getTurnover());
//                                }
//                                if(records.get(i).getCurrentAccountBalance() != null) {
//                                    newRow.createCell(6).setCellValue(records.get(i).getCurrentAccountBalance());
//                                }
//
//                                // set styles
//                                for(int j = 0; j < 7; j++){
//                                    if(newRow.getCell(j) == null){
//                                        newRow.createCell(j);
//                                    }
//                                    if(row.getCell(j) != null && row.getCell(j).getCellStyle() != null) {
//                                        newRow.getCell(j).setCellStyle(row.getCell(j).getCellStyle());
//                                    }
//                                }
//                                index++;
//                                addedRecords++;
//                            }
//                        }
//                    }
//
//                    //rowNum++;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.FIN_INVESTMENT_TYPE) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
//                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
//                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
//                }
//            }
//
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds - updating workbook instance");
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//            start = System.currentTimeMillis();
//            // write to new
//            String filePath = tmpDir + "/CONS_KZT_FORM_19_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        } catch (IOException e) {
//            // TODO: log error
//            //e.printStackTrace();
//            logger.error("IO Exception when exporting KZT_FORM_19", e);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedForm19KZTReportInputStream(Long reportId) {
        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
            return null;
        }
        List<ConsolidatedKZTForm19RecordDto> records = null;
        try {
            ListResponseDto KZTForm1ResponseDto = generateConsolidatedBalanceKZTForm19(reportId);
            if (KZTForm1ResponseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(KZTForm1ResponseDto.getMessage().getNameEn()) ? KZTForm1ResponseDto.getMessage().getNameEn() :
                        "Error occurred when generating KZT Form 19 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("KZT FORM 19 export (get records): " + errorMessage);
            }
            records = KZTForm1ResponseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_KZT_19.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_19.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 14;
            final int columnNum = 7;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedKZTForm19RecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);

                    for(int i = 0; i < columnNum ; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getAccountNumber());
                    newRow.getCell(1).setCellValue(record.getName());
                    if (added + 1 == records.size()) { //last element
                        newRow.getCell(0).setCellValue(record.getName());
                        newRow.getCell(1).setCellValue("");
                    }
                    newRow.getCell(2).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");
                    // Set cell values
                    if(record.getOtherEntityName() != null) {
                        newRow.getCell(3).setCellValue(record.getOtherEntityName());
                    }
                    if(record.getPreviousAccountBalance() != null) {
                        newRow.getCell(4).setCellValue(record.getPreviousAccountBalance());
                    }
                    if(record.getTurnover() != null) {
                        newRow.getCell(5).setCellValue(record.getTurnover());
                    }
                    if(record.getCurrentAccountBalance() != null) {
                        newRow.getCell(6).setCellValue(record.getCurrentAccountBalance());
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    added++;

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);

                // merger last row cells 0 and 1
                sheet.addMergedRegion(new CellRangeAddress(templateRowIndex + added - 1, templateRowIndex + added - 1,0, 1));
                sheet.getRow(templateRowIndex + added - 1).getCell(0).getCellStyle().setAlignment(HorizontalAlignment.LEFT);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }
            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_19_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_19", e);
        }

        return null;
    }

//    private FilesDto getConsolidatedForm22KZTReportInputStream(Long reportId) {
//
//        FilesDto filesDto = new FilesDto();
//        PeriodicReport report = this.periodReportRepository.findOne(reportId);
//        if(report == null){
//            logger.error("No report found for id=" + reportId);
//            return null;
//        }
//        long start = System.currentTimeMillis();
//        Map<Integer, List<ConsolidatedKZTForm22RecordDto>> recordsMap = null;
//        try{
//            recordsMap = getConsolidatedBalanceKZTForm22Map(reportId);
//        }catch (IllegalStateException ex){
//            throw ex;
//        }
////        long end = System.currentTimeMillis();
////        System.out.println((end-start) / 1000 + " seconds - generating report data");
//
//        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_22.xlsx");
//        InputStream excelFileToRead = null;
//        try {
//            excelFileToRead = resource.getInputStream();
//        } catch (IOException e) {
//            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_22.xlsx'");
//            return null;
//            //e.printStackTrace();
//        }
//
//        try {
////            start = System.currentTimeMillis();
//            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");
//
//            start = System.currentTimeMillis();
//
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//            int rows = sheet.getLastRowNum();
//            //int rowNum = 0;
//            boolean startOfTable = false;
//            boolean endOfTable = false;
//            int addedRecords = 0;
//            while (rowIterator.hasNext() && !endOfTable) { // each row
//                Row row = rowIterator.next();
//                if (startOfTable) {
//                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_22_LAST_RECORD)) {
//                        endOfTable = true;
//                    }
//
//                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
//                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
//                        List<ConsolidatedKZTForm22RecordDto> records = recordsMap.get(lineNumber);
//                        int recordsNum = records != null ? records.size() : 0;
//                        Cell nameCell = endOfTable ? row.getCell(0) : row.getCell(1);
//                        if (recordsNum > 0 && nameCell != null && nameCell.getCellType() == Cell.CELL_TYPE_STRING &&
//                                records.get(0).getName().equalsIgnoreCase(nameCell.getStringCellValue())) {
//                            // Set cell values
//
//                            if(records.get(0).getPreviousAccountBalance() != null) {
//                                row.getCell(3).setCellValue(records.get(0).getPreviousAccountBalance());
//                            }
//                            if(records.get(0).getTurnover() != null) {
//                                row.getCell(4).setCellValue(records.get(0).getTurnover());
//                            }
//                            if(records.get(0).getCurrentAccountBalance() != null) {
//                                row.getCell(5).setCellValue(records.get(0).getCurrentAccountBalance());
//                            }
//                        }
//                        if (recordsNum > 1) {
//                            sheet.shiftRows(row.getRowNum() + 1, rows + addedRecords, recordsNum - 1);
//                            //insertRows(rowNum, row, sheet, records);
//                            int index = 1;
//                            for (int i = 1; i < records.size(); i++) {
//                                Row newRow = sheet.createRow(row.getRowNum() + index);
//                                newRow.createCell(0).setCellValue(records.get(i).getAccountNumber());
//                                newRow.createCell(1).setCellValue(records.get(i).getName());
//
//                                if(records.get(i).getPreviousAccountBalance() != null) {
//                                    newRow.createCell(3).setCellValue(records.get(i).getPreviousAccountBalance());
//                                }
//                                if(records.get(i).getTurnover() != null) {
//                                    newRow.createCell(4).setCellValue(records.get(i).getTurnover());
//                                }
//                                if(records.get(i).getCurrentAccountBalance() != null) {
//                                    newRow.createCell(5).setCellValue(records.get(i).getCurrentAccountBalance());
//                                }
//
//                                // set styles
//                                for(int j = 0; j < 7; j++){
//                                    if(newRow.getCell(j) == null){
//                                        newRow.createCell(j);
//                                    }
//                                    if(row.getCell(j) != null && row.getCell(j).getCellStyle() != null) {
//                                        CellStyle newCellStyle = workbook.createCellStyle();
//                                        newCellStyle.cloneStyleFrom(row.getCell(j).getCellStyle());
//                                        newRow.getCell(j).setCellStyle(newCellStyle);
//
//                                        XSSFFont font = workbook.createFont();
//                                        font.setFontHeightInPoints((short)11);
//                                        font.setFontName("Times New Roman");
//                                        font.setBold(false);
//                                        font.setItalic(false);
//                                        newRow.getCell(j).getCellStyle().setFont(font);
//                                    }
//                                }
//                                index++;
//                                addedRecords++;
//                            }
//                        }
//                    }
//
//                    //rowNum++;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.INCOME_EXPENSE_TYPE) &&
//                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
//                    startOfTable = true;
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
//                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
//                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
//                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
//                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
//                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
//                }
//            }
//
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds - updating workbook instance");
//
//            //
//            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");
//
//            if(!tmpDir.exists()){
//                tmpDir.mkdir();
//            }
//
//            start = System.currentTimeMillis();
//            // write to new
//            String filePath = tmpDir + "/CONS_KZT_FORM_22_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
//            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//                workbook.write(outputStream);
//            }
//
////            end = System.currentTimeMillis();
////            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");
//
//            InputStream inputStream = new FileInputStream(filePath);
//            filesDto.setInputStream(inputStream);
//            filesDto.setFileName(filePath);
//            return filesDto;
//        } catch (IOException e) {
//            // TODO: log error
//            //e.printStackTrace();
//            logger.error("IO Exception when exporting KZT_FORM_22", e);
//        }
//
//        return null;
//    }

    private FilesDto getConsolidatedForm22KZTReportInputStream(Long reportId) {

        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
            return null;
        }
        List<ConsolidatedKZTForm22RecordDto> records = null;
        try {
            ListResponseDto KZTForm1ResponseDto = generateConsolidatedBalanceKZTForm22(reportId);
            if (KZTForm1ResponseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(KZTForm1ResponseDto.getMessage().getNameEn()) ? KZTForm1ResponseDto.getMessage().getNameEn() :
                        "Error occurred when generating KZT Form 22 report";
                //throw new IllegalStateException(errorMessage);
                logger.error("KZT FORM 22 export (get records): " + errorMessage);
            }
            records = KZTForm1ResponseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_NICKMF_cons_KZT_22.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_22.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 14;
            final int columnNum = 6;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(ConsolidatedKZTForm22RecordDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);

                    for(int i = 0; i < columnNum ; i++){
                        newRow.createCell(i);
                    }
                    newRow.getCell(0).setCellValue(record.getAccountNumber());
                    newRow.getCell(1).setCellValue(record.getName());
                    if (added + 1 == records.size()) { //last element
                        newRow.getCell(0).setCellValue(record.getName());
                        newRow.getCell(1).setCellValue("");
                    }
                    newRow.getCell(2).setCellValue(StringUtils.isEmpty(record.getAccountNumber()) ? record.getLineNumber() + "" : "");
                    // Set cell values
                    if(record.getPreviousAccountBalance() != null) {
                        newRow.getCell(3).setCellValue(record.getPreviousAccountBalance());
                    }
                    if(record.getTurnover() != null) {
                        newRow.getCell(4).setCellValue(record.getTurnover());
                    }
                    if(record.getCurrentAccountBalance() != null) {
                        newRow.getCell(5).setCellValue(record.getCurrentAccountBalance());
                    }

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    added++;

                    // TODO: bold
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);

                // merger last row cells 0 and 1
                sheet.addMergedRegion(new CellRangeAddress(templateRowIndex + added - 1, templateRowIndex + added - 1,0, 1));
                sheet.getRow(templateRowIndex + added - 1).getCell(0).getCellStyle().setAlignment(HorizontalAlignment.LEFT);
            }
            // Update placeholders
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)) {
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                } else if (ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)) {
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
                }
            }

            //
            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/CONS_KZT_FORM_22_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_22", e);
        }

        return null;
    }

    private FilesDto getTarragonGeneratedGeneralLedgerInputStream(Long reportId) {

        FilesDto filesDto = new FilesDto();
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + (reportId != null ? reportId.longValue() : null));
            return null;
        }
        List<TarragonGeneratedGeneralLedgerFormDto> records = null;
        try {
            ListResponseDto responseDto = this.periodicReportPEService.getTarragonGeneratedFormWithoutExcluded(reportId);
            if (responseDto.getStatus() == ResponseStatusType.FAIL) {
                String errorMessage = StringUtils.isNotEmpty(responseDto.getMessage().getNameEn()) ? responseDto.getMessage().getNameEn() :
                        "Error occurred when generating Tarragon GL";
                //throw new IllegalStateException(errorMessage);
                logger.error(errorMessage);
            }
            records = responseDto.getRecords();
        }catch (Exception ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/reporting/TEMPLATE_REP_TARRAGON_GL.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_REP_TARRAGON_GL.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);

            final int templateRowIndex = 1;
            final int columnNum = 12;
            int added = 0;
            if(records != null && !records.isEmpty()){
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), records.size() - 1);
                for(TarragonGeneratedGeneralLedgerFormDto record: records) {
                    Row newRow = sheet.createRow(templateRowIndex + 1 + added);
                    newRow.createCell(0).setCellValue(record.getAcronym());

                    CreationHelper createHelper = workbook.getCreationHelper();
                    CellStyle dateCellStyle = workbook.createCellStyle();
                    dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
                    Cell dateCell = newRow.createCell(1);
                    dateCell.setCellValue(record.getBalanceDate());
                    dateCell.setCellStyle(dateCellStyle);

                    newRow.createCell(2).setCellValue(record.getFinancialStatementCategory());
                    newRow.createCell(3).setCellValue(record.getGLAccount());
                    newRow.createCell(4).setCellValue(record.getFinancialStatementCategoryDescription());
                    newRow.createCell(5).setCellValue(record.getChartAccountsLongDescription());
                    newRow.createCell(6).setCellValue(record.getSubscriptionRedemptionEntity());
                    newRow.createCell(7).setCellValue(record.getNbAccountNumber());
                    newRow.createCell(8).setCellValue(record.getNicAccountName());
                    newRow.createCell(9).setCellValue(record.getGLAccountBalance());
                    newRow.createCell(10).setCellValue(record.getSegValCCY());
                    newRow.createCell(11).setCellValue(record.getFundCCY());

                    // set styles
                    for(int i = 0; i < columnNum; i++){
                        if(newRow.getCell(i) != null && sheet.getRow(templateRowIndex) != null &&
                                sheet.getRow(templateRowIndex).getCell(i) != null){
                            newRow.getCell(i).setCellStyle(sheet.getRow(templateRowIndex).getCell(i).getCellStyle());
                        }
                    }

                    added++;
                }
                sheet.shiftRows(templateRowIndex + 1, sheet.getLastRowNum(), -1);

                // merger last row cells 0 and 1
                sheet.addMergedRegion(new CellRangeAddress(templateRowIndex + added - 1, templateRowIndex + added - 1,0, 1));
                sheet.getRow(templateRowIndex + added - 1).getCell(0).getCellStyle().setAlignment(HorizontalAlignment.LEFT);
            }

            File tmpDir = new File(this.rootDirectory + "/tmp/nbrk_reporting");

            if(!tmpDir.exists()){
                tmpDir.mkdir();
            }

            // write to new
            String filePath = tmpDir + "/REP_TARRAGON_GL_" + MathUtils.getRandomNumber(0, 10000) + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            filesDto.setInputStream(inputStream);
            filesDto.setFileName(filePath);
            return filesDto;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting Tarragon GL", e);
        }

        return null;
    }


    /* ****************************************************************************************************************/

    private boolean isMatchingRecords(ConsolidatedBalanceFormRecordDto record1, ConsolidatedBalanceFormRecordDto record2){
        if((record1.getAccountNumber() == null && record2.getAccountNumber() != null) ||
                (record2.getAccountNumber() == null && record1.getAccountNumber() != null)){
            return false;
        }
        boolean accountNumberCheck = (record1.getAccountNumber() == null && record2.getAccountNumber() == null) ||
                record1.getAccountNumber() != null && record2.getAccountNumber() != null &&
                        record1.getAccountNumber().equalsIgnoreCase(record2.getAccountNumber());
        return accountNumberCheck && record1.getName() != null && record2.getName() != null &&
                record1.getName().equalsIgnoreCase(record2.getName()) &&
                record1.getLineNumber() != null && record2.getLineNumber() != null &&
                record1.getLineNumber() == record2.getLineNumber();
    }

    /**
     * Return statement of balance and operations data for specified report id.
     *
     * @param reportId - report id
     * @return - statement of balance and operations
     */
    @Override
    public TarragonStatementBalanceOperationsHolderDto getStatementBalanceOperations(Long reportId) {
        TarragonStatementBalanceOperationsHolderDto balanceRecordsHolder = this.statementBalanceService.getStatementBalanceDto(reportId);
        TarragonStatementBalanceOperationsHolderDto operationsRecordsHolder = this.statementOperationsService.getStatementOperations(reportId);

        //ConsolidatedReportRecordHolderDto balanceResults = this.statementBalanceService.get(reportId);
        //ConsolidatedReportRecordHolderDto operationsResults = this.statementOperationsService.get(reportId);
        balanceRecordsHolder.merge(operationsRecordsHolder);
        return balanceRecordsHolder;
    }

    /**
     * Return NOAL (Net Other Assets and Liabilities) data for specified report id.
     *
     * @param reportId - report id
     * @param tranche - tranche (1-A, 2-B)
     * @return NOAL
     */
    @Override
    public ConsolidatedReportRecordHolderDto getNOAL(Long reportId, int tranche) {
        ConsolidatedReportRecordHolderDto result = this.hfNOALService.get(reportId, tranche);
        if(result != null && result.getNoalTrancheAList() != null){
            Collections.sort(result.getNoalTrancheAList());
        }
        if(result != null && result.getNoalTrancheBList() != null){
            Collections.sort(result.getNoalTrancheBList());
        }
        return result;
    }

    @Override
    public ConsolidatedReportRecordHolderDto getSingularityITD(Long reportId) {
        if(reportId != null) {
            ConsolidatedReportRecordHolderDto holderDto = this.hfITDService.getParsedData(reportId);
            holderDto.setReport(getPeriodicReport(reportId));
            return holderDto;
        }
        return null;
    }
}