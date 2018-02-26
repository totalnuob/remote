package kz.nicnbk.service.impl.reporting;

import kz.nicnbk.common.service.util.*;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.reporting.*;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.CurrencyLookup;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.reporting.*;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.reporting.PeriodicDataService;
import kz.nicnbk.service.api.reporting.PeriodicReportNICKMFService;
import kz.nicnbk.service.api.reporting.PeriodicReportPrevYearInputService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFNOALService;
import kz.nicnbk.service.api.reporting.hedgefunds.PeriodicReportHFService;
import kz.nicnbk.service.api.reporting.privateequity.*;
import kz.nicnbk.service.converter.reporting.*;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.lookup.CurrencyRatesDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.PeriodicReportType;
import kz.nicnbk.service.impl.reporting.lookup.NICChartAccountsLookup;
import kz.nicnbk.service.impl.reporting.lookup.PeriodicDataTypeLookup;
import kz.nicnbk.service.impl.reporting.lookup.ReserveCalculationsExpenseTypeLookup;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
                    logger.error("Failed to create report, report date already exists: report date = " + DateUtils.getDateFormatted(entity.getReportDate()) + " [user " + updater + "]");
                    saveResponseDto.setErrorMessageEn("Failed to create report, report date already exists: report date = " + DateUtils.getDateFormatted(entity.getReportDate()) + " [user " + updater + "]");
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
                        logger.error("Error deleting file: file id=" + fileId + ", report di=" + periodicReport.getId());
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
            for(int i = 0; i < currentPeriodRecords.size(); i++) {
                ConsolidatedBalanceFormRecordDto currentRecord = currentPeriodRecords.get(i);
                if (currentRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_INVESTMENTS_TO_RETURN) && currentRecord.getAccountNumber() == null) {
                    header1Index = i + 1;
                } else if (currentRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PRE_SUBSCRIPTION) && currentRecord.getAccountNumber() == null) {
                    header2Index = i + 1;
                }
            }

            // Set previous month account balance
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if(previousReport != null && previousReport.getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
                List<ConsolidatedBalanceFormRecordDto> previousPeriodRecords = getConsolidatedBalanceUSDFormSaved(previousReport.getId());
                if(previousPeriodRecords != null){
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
        for(int i = 1; i <= 52; i++){
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
                recordDto.setCurrentAccountBalance(lineNumber == 30 || lineNumber == 45 || lineNumber == 49
                        ? 0 - nickMFRecord.getAccountBalance() : nickMFRecord.getAccountBalance());

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
                recordDto.setOtherEntityName(singularRecord.getAcronym());
                recordDto.setCurrentAccountBalance(lineNumber == 30 || lineNumber == 45  || lineNumber == 49
                        ? 0 - singularRecord.getGLAccountBalance() : singularRecord.getGLAccountBalance());
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
        List<GeneratedGeneralLedgerFormDto> tarragonRecords = this.periodicReportPEService.getTarragonGeneratedForm(reportId).getRecords();
        if(singularRecords != null){
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
                recordDto.setOtherEntityName(tarragonRecord.getAcronym());
                recordDto.setCurrentAccountBalance(lineNumber == 30 || lineNumber == 45  || lineNumber == 49
                        ? 0 - tarragonRecord.getGLAccountBalance() : tarragonRecord.getGLAccountBalance());
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
                if(inRecordDto.getLineNumber() == 20){
                    ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                    recordDto.setAccountNumber(PeriodicReportConstants.ACC_NUM_5510_010);
                    recordDto.setName(PeriodicReportConstants.RU_5510_010);
                    recordDto.setLineNumber(50);
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
                                currentRecord.setPreviousAccountBalance(previousRecord.getCurrentAccountBalance());
                                break;
                            }
                        }
                    }
                    int added = 0;
                    for(int i = 0; i < toAdd.size(); i++) {
                        ConsolidatedBalanceFormRecordDto recordToAdd = toAdd.get(i);
                        if(recordToAdd.getCurrentAccountBalance() != null && recordToAdd.getCurrentAccountBalance() != 0.0) {
                            recordToAdd.setPreviousAccountBalance(recordToAdd.getCurrentAccountBalance());
                            recordToAdd.setCurrentAccountBalance(null);
                            currentPeriodRecords.add(toAddIndex.get(i) + added, recordToAdd);
                            added++;
                        }
                    }

                }
            }else{
                logger.error("ConsolidatedIncomeExpenseUSDForm: No previous month report or report status is not 'SUBMITTED', report id " + reportId);
            }

            return currentPeriodRecords;
        }


    }

    public List<ConsolidatedBalanceFormRecordDto> generateConsolidatedIncomeExpenseUSDFormCurrent(Long reportId) {
        Map<Integer, Double> sums = new HashedMap();
        for(int i = 1; i <= 20; i++){
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
                recordDto.setCurrentAccountBalance(lineNumber == 8 || lineNumber == 10 || lineNumber == 12 || lineNumber == 15
                        ? 0 - nickMFRecord.getAccountBalance() : nickMFRecord.getAccountBalance());

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
                recordDto.setCurrentAccountBalance(lineNumber == 8 || lineNumber == 10 || lineNumber == 12 || lineNumber == 15
                        ? 0 - singularRecord.getGLAccountBalance() : singularRecord.getGLAccountBalance());
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
        List<GeneratedGeneralLedgerFormDto> tarragonRecords = this.periodicReportPEService.getTarragonGeneratedForm(reportId).getRecords();
        if(singularRecords != null){
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
                recordDto.setCurrentAccountBalance(lineNumber == 8 || lineNumber == 10 || lineNumber == 12 || lineNumber == 15
                        ? 0 - tarragonRecord.getGLAccountBalance() : tarragonRecord.getGLAccountBalance());
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
                                currentRecord.setPreviousAccountBalance(previousRecord.getCurrentAccountBalance());
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
                if(inRecordDto.getLineNumber() != null && inRecordDto.getLineNumber() == 20){
                    header_1_Balance = inRecordDto.getCurrentAccountBalance();

//                    if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
//                        Double sum = sums.get(recordDto.getLineNumber());
//                        sum = sum != null ? sum : 0.0;
//                        sum += recordDto.getCurrentAccountBalance();
//                        sums.put(recordDto.getLineNumber(), sum);
//                    }
                    break;
                }
            }
        }

        // Get consolidated balance
        Double header_3_1_Balance = null;
        ListResponseDto balanceUSDResponseDto = generateConsolidatedBalanceUSDForm(reportId);
        if(balanceUSDResponseDto.getStatus() == ResponseStatusType.FAIL){
            // TODO: handle error
        }
        List<ConsolidatedBalanceFormRecordDto> balanceFormRecords = balanceUSDResponseDto.getRecords();
        if(balanceFormRecords != null && !balanceFormRecords.isEmpty()){
            for(ConsolidatedBalanceFormRecordDto balanceRecordDto: balanceFormRecords){
                if(balanceRecordDto.getAccountNumber() != null && balanceRecordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010) &&
                        balanceRecordDto.getName().equalsIgnoreCase(PeriodicReportConstants.RU_5440_010_a)){
                    header_3_1_Balance = balanceRecordDto.getCurrentAccountBalance();

//                    if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
//                        Double sum = sums.get(recordDto.getLineNumber());
//                        sum = sum != null ? sum : 0.0;
//                        sum += recordDto.getCurrentAccountBalance();
//                        sums.put(recordDto.getLineNumber(), sum);
//                    }
                    break;
                }
            }
        }

        // Set values from other reports
        for(ConsolidatedBalanceFormRecordDto record: records){
            if(record.getLineNumber() != null){
                if(record.getLineNumber() == 1){
                    record.setCurrentAccountBalance(header_1_Balance);
                }else if(record.getLineNumber() == 3 && record.getSubLineNumber() != null && record.getSubLineNumber() == 1){
                    record.setCurrentAccountBalance(header_3_1_Balance);
                }

                if(record.getCurrentAccountBalance() != null) {
                    Double sum = sums.get(record.getLineNumber()) != null ? sums.get(record.getLineNumber()) : 0.0;
                    sums.put(record.getLineNumber(), sum + record.getCurrentAccountBalance());
                }
            }
        }

        // Sort
        Collections.sort(records);

        // Set total sums
        for(ConsolidatedBalanceFormRecordDto record: records){
            if(record.getLineNumber() != null){
                if((record.getLineNumber() == 3 || record.getLineNumber() == 4) && record.getSubLineNumber() == null){
                    record.setCurrentAccountBalance(sums.get(record.getLineNumber()));
                }else if(record.getLineNumber() == 5 && record.getSubLineNumber() == null){
                    record.setCurrentAccountBalance(sums.get(3) + sums.get(4));
                    sums.put(5, record.getCurrentAccountBalance());
                }else if(record.getLineNumber() == 6  && record.getSubLineNumber() == null){
                    record.setCurrentAccountBalance(sums.get(1) + sums.get(5));
                    sums.put(6, record.getCurrentAccountBalance());
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
                return responseDtoKZTForm1;
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
                        for(int i = 0; i < currentRecords.size(); i++){
                            ConsolidatedBalanceFormRecordDto currentRecord = currentRecords.get(i);
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

    private ListResponseDto generateConsolidatedBalanceKZTForm1Current(Long reportId) {
        ListResponseDto responseDto = new ListResponseDto();
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

        //Map<Integer, Double> sums = new HashedMap();
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
        double record1033_010 = 0;

        String record5440_010Name = PeriodicReportConstants.RU_5440_010_a;
        String record5440_010ReportName = PeriodicReportConstants.RU_5440_010_b;
        String record5440_010AccountNumber = PeriodicReportConstants.ACC_NUM_5440_010;
        double record5440_010 = 0;
        if(recordsUSD != null){
            for(ConsolidatedBalanceFormRecordDto record: recordsUSD){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record1033_010AccountNumber) &&
                        record.getName().equalsIgnoreCase(record1033_010Name)){
                    record1033_010 = MathUtils.add(record1033_010, MathUtils.multiply(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue()));
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record5440_010AccountNumber) &&
                        record.getName().equalsIgnoreCase(record5440_010Name)){
                    // Average rate
                    record5440_010 = MathUtils.add(record5440_010, MathUtils.multiply(record.getCurrentAccountBalance(), averageRate));
                }
            }
        }

        String record1283_020Name = PeriodicReportConstants.RU_1283_020;
        String record1283_020AccountNumber = PeriodicReportConstants.ACC_NUM_1283_020;
        double record1283_020 = 0;

        ListResponseDto KZTForm8ResponseDto = generateConsolidatedBalanceKZTForm8(reportId);
        if(KZTForm8ResponseDto.getStatus() == ResponseStatusType.FAIL){
            responseDto.setErrorMessageEn("Error generating KZT Form 8. " + KZTForm8ResponseDto.getMessage().getNameEn());
            return responseDto;
        }
        List<ConsolidatedKZTForm8RecordDto> form8Records = KZTForm8ResponseDto.getRecords();
        if(form8Records != null){
            for(ConsolidatedKZTForm8RecordDto record: form8Records){
                if(record.getAccountNumber() == null && record.getLineNumber() == 9 && record.getDebtEndPeriod() != null){
                    record1283_020 = record.getDebtEndPeriod();
                }
            }
        }

        String record1623_010Name = PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD;
        String record1623_010AccountNumber = PeriodicReportConstants.ACC_NUM_1623_010;
        double record1623_010 = 0;

        String record2923_010Name = PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD;
        String record2923_010AccountNumber = PeriodicReportConstants.ACC_NUM_2923_010;
        double record2923_010 = 0;

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

        String record2033_010Name = PeriodicReportConstants.RU_2033_010;
        String record2033_010AccountNumber = PeriodicReportConstants.ACC_NUM_2033_010;
        double record2033_010 = 0;

        String record2033_040Name = PeriodicReportConstants.RU_2033_040;
        String record2033_040AccountNumber = PeriodicReportConstants.ACC_NUM_2033_040;
        double record2033_040 = 0;

        String record2033_050Name = PeriodicReportConstants.RU_2033_050;
        String record2033_050AccountNumber = PeriodicReportConstants.ACC_NUM_2033_050;
        double record2033_050 = 0;

        ListResponseDto KZTForm7ResponseDto = generateConsolidatedBalanceKZTForm7(reportId);
        if(KZTForm7ResponseDto.getStatus() == ResponseStatusType.FAIL){
            responseDto.setErrorMessageEn("Error when generating KZT Form 7. " + KZTForm7ResponseDto.getMessage().getNameEn());
            return responseDto;
        }
        List<ConsolidatedKZTForm7RecordDto> form7Records = KZTForm7ResponseDto.getRecords();
        if(form7Records != null){
            for(ConsolidatedKZTForm7RecordDto record: form7Records){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record2033_010AccountNumber)){
                    record2033_010 = MathUtils.add(record2033_010, record.getDebtEndPeriod() != null ? record.getDebtEndPeriod() : 0);
                    if(record.getFairValueAdjustmentsEndPeriod() != null){
                        record2033_040 = MathUtils.add(record2033_040, record.getFairValueAdjustmentsEndPeriod().doubleValue() >= 0 ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0);
                        record2033_050 = MathUtils.add(record2033_050, record.getFairValueAdjustmentsEndPeriod().doubleValue() < 0 ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0);
                    }
                }
            }
        }

        String record3013_010Name = PeriodicReportConstants.RU_3013_010;
        String record3013_010AccountNumber = PeriodicReportConstants.ACC_NUM_3013_010;
        double record3013_010 = 0;

        String record3383_010Name = PeriodicReportConstants.RU_3383_010;
        String record3383_010AccountNumber = PeriodicReportConstants.ACC_NUM_3383_010;
        double record3383_010 = 0;

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
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3013_010AccountNumber) && record.getInterestEndPeriod() != null){
                    record3383_010 = MathUtils.add(record3383_010, record.getInterestEndPeriod());
                }
            }
        }

        String record3393_020Name = PeriodicReportConstants.RU_3393_020;
        String record3393_020AccountNumber = PeriodicReportConstants.ACC_NUM_3393_020;
        double record3393_020 = 0;
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

        String record5440_010_previousYearName = PeriodicReportConstants.RU_5440_010__LAST_YEAR;
        String record5520_010Name = PeriodicReportConstants.RU_5520_010;
        String record5520_010AccountNumber = PeriodicReportConstants.ACC_NUM_5520_010;
        double record5520_010 = 0;
        List<PreviousYearInputDataDto> previousYearInputData = this.prevYearInputService.getPreviousYearInputData(reportId);
        if(previousYearInputData != null){
            for(PreviousYearInputDataDto inputData: previousYearInputData){
                if(inputData.getChartOfAccounts() != null && inputData.getChartOfAccounts().getCode().startsWith(PeriodicReportConstants.ACC_NUM_5440_010) &&
                        inputData.getChartOfAccounts().getNameRu().equalsIgnoreCase(record5440_010_previousYearName)){
                    record5440_010 = MathUtils.add(record5440_010, inputData.getAccountBalanceKZT() != null ? inputData.getAccountBalanceKZT().doubleValue() : 0);
                }else if(inputData.getChartOfAccounts() != null && inputData.getChartOfAccounts().getCode().startsWith(record5520_010AccountNumber) &&
                        inputData.getChartOfAccounts().getNameRu().equalsIgnoreCase(record5520_010Name)){
                    record5520_010 = MathUtils.add(record5520_010, inputData.getAccountBalanceKZT() != null ? inputData.getAccountBalanceKZT().doubleValue() : 0);
                }
            }
        }

        String record5021_010AccountNumber = PeriodicReportConstants.ACC_NUM_5021_010;
        String record5022_010AccountNumber = PeriodicReportConstants.ACC_NUM_5022_010;

        String record5021_010Name = PeriodicReportConstants.COMMON_SHARES;
        String record5022_010Name = PeriodicReportConstants.COMMON_SHARES;

        double record5022_010 = 18765;

        // TODO: Refactor string literal
        double record5021_010 = 0;
        List<ReserveCalculationDto> reserveCalculations = null;
        try {
            reserveCalculations = this.reserveCalculationService.getReserveCalculationsByExpenseType(ReserveCalculationsExpenseTypeLookup.ADD.getCode());
        }catch (IllegalStateException ex){
            responseDto.setErrorMessageEn("Reserve Calculation failed. " + ex.getMessage());
            return responseDto;
        }

        if(reserveCalculations != null){
            BigDecimal sum = new BigDecimal("0");
            for(ReserveCalculationDto reserveCalculationDto: reserveCalculations){
                if(reserveCalculationDto.getCurrencyRate() == null){
                    String errorMessage = "One of the reserve calculation records has no currency rate: date '" + DateUtils.getDateFormatted(reserveCalculationDto.getDate()) + "'";
                    logger.error(errorMessage);
                    responseDto.setErrorMessageEn(errorMessage);
                    return responseDto;
                }
                if(reserveCalculationDto.getDate().compareTo(currentReport.getReportDate()) < 0) {
                    sum = MathUtils.add(sum, new BigDecimal(reserveCalculationDto.getAmountKZT() != null ? reserveCalculationDto.getAmountKZT().doubleValue() : 0));
                }
            }
            record5021_010 = MathUtils.subtract(sum.setScale(2, RoundingMode.HALF_UP).doubleValue(), record5022_010);
        }

        String record5450_010AccountNumber = PeriodicReportConstants.ACC_NUM_5450_010;
        String record5450_010Name = PeriodicReportConstants.RU_5450_010;

        double record5450_010 = 0;
        try{
            record5450_010 = getCurrencyReserveCalculationKZTForm1(currentReport.getId(), currentReport.getReportDate());
        }catch (IllegalStateException ex){
            responseDto.setErrorMessageEn(ex.getMessage());
            return responseDto;
        }

        Map<Integer, BigDecimal> sums = new HashedMap();
        for(int i = 1; i <= 52; i++){
            sums.put(i, new BigDecimal("0"));
        }

        String record5510_010AccountNumber = PeriodicReportConstants.ACC_NUM_5510_010;
        String record5510_010Name = PeriodicReportConstants.RU_5510_010;
        double record5510_010 = 0;

        ListResponseDto KZTForm2ResponseDto  = generateConsolidatedIncomeExpenseKZTForm2(reportId);
        if(KZTForm2ResponseDto.getStatus() == ResponseStatusType.FAIL){
            responseDto.setErrorMessageEn("Error generating KZT Form 2 report. " + KZTForm2ResponseDto.getMessage().getNameEn());
            return responseDto;
        }
        List<ConsolidatedBalanceFormRecordDto> form2Records = KZTForm2ResponseDto.getRecords();
        for(int i = form2Records.size() - 1; i >= 0; i--){
            if(form2Records.get(i).getAccountNumber() == null && form2Records.get(i).getLineNumber() != null && form2Records.get(i).getLineNumber() == 20 &&
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
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3383_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record3383_010Name)){
                record.setCurrentAccountBalance(record3383_010);
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
            }else{
                continue;
            }

            if(record.getLineNumber() != null){
                if (sums.get(record.getLineNumber()) == null) {
                    sums.put(record.getLineNumber(), new BigDecimal((record.getCurrentAccountBalance())));
                } else {
                    BigDecimal value = sums.get(record.getLineNumber());
                    sums.put(record.getLineNumber(), MathUtils.add(value, new BigDecimal(record.getCurrentAccountBalance())));
                }
            }
        }

        for(ConsolidatedBalanceFormRecordDto record: records){
            if(record.getLineNumber() != null && (record.getLineNumber() == 1 || record.getLineNumber() == 13)
                    && record.getAccountNumber() == null){
                Double value = MathUtils.add(MathUtils.add(sums.get(2), sums.get(8)), sums.get(11)).doubleValue();
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && (record.getLineNumber() == 14 || record.getLineNumber() == 25)
                    && record.getAccountNumber() == null){
                Double value = MathUtils.add(sums.get(16), sums.get(24)).doubleValue();
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && record.getLineNumber() == 26 && record.getAccountNumber() == null){
                Double value = MathUtils.add(sums.get(13), sums.get(25)).doubleValue();
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && (record.getLineNumber() == 27 || record.getLineNumber() == 35) && record.getAccountNumber() == null){
                Double value = MathUtils.add(sums.get(28), sums.get(30)).doubleValue();
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && record.getLineNumber() == 51 && record.getAccountNumber() == null){
                Double value = MathUtils.add(MathUtils.add(sums.get(45), sums.get(49)), sums.get(50)).doubleValue();
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && record.getLineNumber() == 52 && record.getAccountNumber() == null){
                Double value = MathUtils.add(MathUtils.add(sums.get(35), sums.get(43)), sums.get(51)).doubleValue();
                record.setCurrentAccountBalance(value);
                sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && sums.get(record.getLineNumber()) != null && record.getCurrentAccountBalance() == null &&
                    record.getAccountNumber() == null){
                record.setCurrentAccountBalance(sums.get(record.getLineNumber()).doubleValue());
                sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }
        }

        double totalSumCheck = 0;
        int correctionRecordIndex = 0;
        int correctionRecordHeader49Index = 0;
        int correctionRecordHeader51Index = 0;
        int correctionRecordHeader52Index = 0;
        double value = 0;
        double valueHeader49 = 0;
        double valueHeader51 = 0;
        double valueHeader52 = 0;
        for(int i = 0; i < records.size(); i++){
            ConsolidatedBalanceFormRecordDto record = records.get(i);
            if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 26){
                totalSumCheck = MathUtils.add(totalSumCheck, record.getCurrentAccountBalance());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null &&
                    (record.getLineNumber() == 27 || record.getLineNumber() == 45 || record.getLineNumber() == 50)){
                totalSumCheck = MathUtils.subtract(totalSumCheck, record.getCurrentAccountBalance());
            }else if(record.getLineNumber() != null && record.getLineNumber() == 49 && record.getAccountNumber()!= null &&
                    record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010)){
                totalSumCheck = MathUtils.subtract(totalSumCheck, record.getCurrentAccountBalance());
            }else if(record.getLineNumber() != null && record.getLineNumber() == 49 && record.getAccountNumber()!= null &&
                    record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5450_010)){
                correctionRecordIndex = i;
                value = record.getCurrentAccountBalance();
            }else if(record.getLineNumber() != null && record.getLineNumber() == 49 && record.getAccountNumber() == null){
                correctionRecordHeader49Index = i;
                valueHeader49 = record.getCurrentAccountBalance();
            }else if(record.getLineNumber() != null && record.getLineNumber() == 51 && record.getAccountNumber() == null){
                correctionRecordHeader51Index = i;
                valueHeader51 = record.getCurrentAccountBalance();
            }else if(record.getLineNumber() != null && record.getLineNumber() == 52 && record.getAccountNumber() == null){
                correctionRecordHeader52Index = i;
                valueHeader52 = record.getCurrentAccountBalance();
            }
        }

        double difference = MathUtils.subtract(value, totalSumCheck);
        if(difference > -1 && difference < 1 && correctionRecordIndex > 0){
            records.get(correctionRecordIndex).setCurrentAccountBalance(totalSumCheck);
            records.get(correctionRecordHeader49Index).setCurrentAccountBalance(MathUtils.subtract(valueHeader49, difference));
            records.get(correctionRecordHeader51Index).setCurrentAccountBalance(MathUtils.subtract(valueHeader51, difference));
            records.get(correctionRecordHeader52Index).setCurrentAccountBalance(MathUtils.subtract(valueHeader52, difference));
        }else if(difference > 1 || difference < -1){
            logger.error("Record '5450.010' (код строки #49) value = " + new BigDecimal(value).toPlainString() + ", #26 - #27 - #45 - #50 - '5440.010'(код строки #49) = " + new BigDecimal(totalSumCheck).toPlainString() +
                    ". Difference is not negligible = " + new BigDecimal(difference).toPlainString());
            responseDto.setErrorMessageEn("Record '5450.010' (код строки #49) value = " + new BigDecimal(value).toPlainString() + ", #26 - #27 - #45 - #50 - '5440.010'(код строки #49) = " + new BigDecimal(totalSumCheck).toPlainString() +
                    ". Difference is not negligible = " + new BigDecimal(difference).toPlainString());
        }

        responseDto.setRecords(records);
        return responseDto;
    }

    private Double getCurrencyReserveCalculationKZTForm1(Long reportId, Date reportDate){

        BigDecimal total = new BigDecimal("0");

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
            reserveCalculations = this.reserveCalculationService.getReserveCalculationsByExpenseType(ReserveCalculationsExpenseTypeLookup.ADD.getCode());
        }catch (IllegalStateException ex){
            throw ex;
        }
        if(reserveCalculations != null){
            BigDecimal sumKZTInitial = new BigDecimal("0");
            BigDecimal sumKZTOnReportDate = new BigDecimal("0");
            for(ReserveCalculationDto reserveCalculationDto: reserveCalculations){
                if(reserveCalculationDto.getDate().compareTo(reportDate) < 0) {
                    sumKZTInitial = sumKZTInitial.add(new BigDecimal(reserveCalculationDto.getAmountKZT() != null ? reserveCalculationDto.getAmountKZT().doubleValue() : 0));
                    BigDecimal calculatedAmount = reserveCalculationDto.getAmount() != null ?
                            new BigDecimal(reserveCalculationDto.getAmount().doubleValue()).multiply(new BigDecimal(currencyRatesDto.getValue())) : new BigDecimal("0");

                    sumKZTOnReportDate = sumKZTOnReportDate.add(calculatedAmount);
                }
            }
            total = total.add(sumKZTOnReportDate).subtract(sumKZTInitial);
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
                avgCurrencyRate = this.currencyRatesService.getAverageRateForFixedDateAndCurrency(DateUtils.getDate("31.12." + year), CurrencyLookup.USD.getCode());
                //avgCurrencyRate = this.currencyRatesService.getAverageRateForAllMonthsBeforeDateAndCurrency(DateUtils.getDate("31.12." + year), CurrencyLookup.USD.getCode(), scale);
                if(avgCurrencyRate == null){
                    logger.error("No average currency rate found for date '" + DateUtils.getDate("31.12." + year) + "'");
                    throw  new IllegalStateException("No average currency rate found for date '" + DateUtils.getDate("31.12." + year) + "'");
                }
            }catch (IllegalStateException ex){
                throw ex;
            }

            if(netProfitDto != null){
                total = total.add(new BigDecimal(netProfitDto.getValue()).multiply(new BigDecimal(currencyRatesDto.getValue())));
                total = total.subtract(new BigDecimal(netProfitDto.getValue()).multiply(new BigDecimal(avgCurrencyRate)));
            }
            if(reserveRevalutionDto != null){
                total = total.add(new BigDecimal(reserveRevalutionDto.getValue()).multiply(new BigDecimal(currencyRatesDto.getValue())));
                total = total.subtract(new BigDecimal(reserveRevalutionDto.getValue()).multiply(new BigDecimal(avgCurrencyRate)));
            }
        }

        List<ConsolidatedBalanceFormRecordDto> incomeExpenseRecords = generateConsolidatedIncomeExpenseUSDForm(reportId);
        for(int i = incomeExpenseRecords.size() - 1; i >= 0; i--){
            if(incomeExpenseRecords.get(i).getLineNumber() != null && incomeExpenseRecords.get(i).getLineNumber() == 20 && incomeExpenseRecords.get(i).getCurrentAccountBalance() != null){
                total = total.add(new BigDecimal(incomeExpenseRecords.get(i).getCurrentAccountBalance()).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(currencyRatesDto.getValue())));
                total = total.subtract(new BigDecimal(incomeExpenseRecords.get(i).getCurrentAccountBalance()).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(avgCurrencyRateForReportDate)));
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
                total = total.add(new BigDecimal(balanceRecords.get(i).getCurrentAccountBalance()).multiply(new BigDecimal(currencyRatesDto.getValue())));
                total = total.subtract(new BigDecimal(balanceRecords.get(i).getCurrentAccountBalance()).multiply(new BigDecimal(avgCurrencyRateForReportDate)));
                break;
            }
        }

        return total.doubleValue();
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

            BigDecimal record6150_030 = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal record7330_030 = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal record6283_080 = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal record7313_010 = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal record7473_080 = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);

            ListResponseDto KZTForm19ResponseDto = generateConsolidatedBalanceKZTForm19(reportId);
            if(KZTForm19ResponseDto.getStatus() == ResponseStatusType.FAIL){
                responseDto.setErrorMessageEn("Error generating KZT Form 19 report. " + KZTForm19ResponseDto.getMessage().getNameEn());
                return responseDto;
            }
            List<ConsolidatedKZTForm19RecordDto> form19Records = KZTForm19ResponseDto.getRecords();
            if(form19Records != null){
                for(ConsolidatedKZTForm19RecordDto record: form19Records){
                    if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030)){
                        record6150_030 = MathUtils.add(record6150_030, new BigDecimal(record.getCurrentAccountBalance()));
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030)){
                        record7330_030 = MathUtils.add(record7330_030, new BigDecimal(record.getCurrentAccountBalance() != null ? record.getCurrentAccountBalance() : 0));
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010)){
                        record7313_010 = MathUtils.add(record7313_010, new BigDecimal(record.getCurrentAccountBalance() != null ? record.getCurrentAccountBalance() : 0));
                    }
                }
            }

            ListResponseDto KZTForm22ResponseDto = generateConsolidatedBalanceKZTForm22(reportId);
            if(KZTForm22ResponseDto.getStatus() == ResponseStatusType.FAIL){
                responseDto.setErrorMessageEn("Error generating KZT Form 22 report. " + KZTForm22ResponseDto.getMessage().getNameEn());
                return responseDto;
            }
            List<ConsolidatedKZTForm22RecordDto> form22Records = KZTForm22ResponseDto.getRecords();
            if(form22Records != null){
                for(ConsolidatedKZTForm22RecordDto record: form22Records) {
                    if (record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6283_080)) {
                        record6283_080 = MathUtils.add(record6283_080, new BigDecimal(record.getCurrentAccountBalance() != null ? record.getCurrentAccountBalance() : 0));
                    } else if (record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7473_080)) {
                        record7473_080 = MathUtils.add(record7473_080, new BigDecimal(record.getCurrentAccountBalance() != null ? record.getCurrentAccountBalance() : 0));
                    }
                }
            }

            double sum = MathUtils.add(MathUtils.add(MathUtils.add(MathUtils.add(record6150_030, (record7330_030)), record6283_080), record7313_010), record7473_080).doubleValue();

            for(ConsolidatedBalanceFormRecordDto record: records){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030) &&
                        record.getLineNumber() != null && record.getLineNumber() == 8){
                    record.setCurrentAccountBalance(record6150_030.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030) &&
                        record.getLineNumber() != null && record.getLineNumber() == 8){
                    record.setCurrentAccountBalance(record7330_030.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6283_080) &&
                        record.getLineNumber() != null && record.getLineNumber() == 10){
                    record.setCurrentAccountBalance(record6283_080.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010) &&
                        record.getLineNumber() != null && record.getLineNumber() == 12){
                    record.setCurrentAccountBalance(record7313_010.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7473_080) &&
                        record.getLineNumber() != null && record.getLineNumber() == 15){
                    record.setCurrentAccountBalance(record7473_080.doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 8){
                    record.setCurrentAccountBalance(record6150_030.add(record7330_030).doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 10){
                    record.setCurrentAccountBalance(record6283_080.doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 12){
                    record.setCurrentAccountBalance(record7313_010.doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 15){
                    record.setCurrentAccountBalance(record7473_080.doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null &&
                        (record.getLineNumber() == 16 || record.getLineNumber() == 18 || record.getLineNumber() == 20)){
                    record.setCurrentAccountBalance(sum);
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
                            records.add(toAddIndices.get(i) + added, recordToAdd);
                            added++;
                        }
                    }
                }
            }

            responseDto.setRecords(records);
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
                return KZTForm3CurrentResponseDto;
            }
            List<ConsolidatedBalanceFormRecordDto> currentRecords = KZTForm3CurrentResponseDto.getRecords();

            if(currentRecords != null) {
                Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
                PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
                if (previousReport != null) {
                    List<ConsolidatedBalanceFormRecordDto> previousRecords = getConsolidatedBalanceKZTForm3Saved(previousReport.getId());
                    if (previousRecords != null) {
                        double added = 0;
                        for (ConsolidatedBalanceFormRecordDto currentRecord : currentRecords) {
                            for (ConsolidatedBalanceFormRecordDto prevRecord : previousRecords) {
                                if(isMatchingRecords(currentRecord, prevRecord)){
                                    currentRecord.setPreviousAccountBalance(prevRecord.getCurrentAccountBalance());
                                    if(currentRecord.getLineNumber() != null && currentRecord.getLineNumber() == 5 &&
                                            ((currentRecord.getSubLineNumber() == null || currentRecord.getSubLineNumber() == 1))){
                                        currentRecord.setCurrentAccountBalance(MathUtils.add(currentRecord.getCurrentAccountBalance(), currentRecord.getPreviousAccountBalance()));
                                        added = currentRecord.getPreviousAccountBalance();
                                    }
                                    break;
                                }
                            }
                        }

                        for (ConsolidatedBalanceFormRecordDto currentRecord : currentRecords) {
                            if(currentRecord.getLineNumber() != null && (currentRecord.getLineNumber() == 6 || currentRecord.getLineNumber() == 7)){
                                currentRecord.setCurrentAccountBalance(MathUtils.add(currentRecord.getCurrentAccountBalance(), added));
                            }
                        }
                    }
                }
            }

            responseDto.setRecords(currentRecords);
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

        double record1 = 0;

        ListResponseDto KZTForm2ResponseDto = generateConsolidatedIncomeExpenseKZTForm2(reportId);
        if(KZTForm2ResponseDto.getStatus() == ResponseStatusType.FAIL){
            responseDto.setErrorMessageEn("Error generating KZT Form2 report. " + KZTForm2ResponseDto.getMessage().getNameEn());
            return responseDto;
        }
        List<ConsolidatedBalanceFormRecordDto> form2Records = KZTForm2ResponseDto.getRecords();
        if(form2Records != null){
            for(int i = form2Records.size() - 1; i >= 0; i--){
                ConsolidatedBalanceFormRecordDto record = form2Records.get(i);
                if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 20){
                    record1 = record.getCurrentAccountBalance();
                }
            }
        }

        double record3_1 = 0;
        double record5_1 = 0;

        ListResponseDto responseDtoKZTForm1 = generateConsolidatedBalanceKZTForm1(reportId);
        if(responseDtoKZTForm1.getStatus() == ResponseStatusType.FAIL){
            return responseDtoKZTForm1;
        }
        List<ConsolidatedBalanceFormRecordDto> form1Records = responseDtoKZTForm1.getRecords();
        if(form1Records != null){
            for(int i = form1Records.size() - 1; i >= 0; i--){
                ConsolidatedBalanceFormRecordDto record = form1Records.get(i);
                if(record.getLineNumber() != null && record.getLineNumber() == 49 &&
                        record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010)){
                    List<PreviousYearInputDataDto> previousYearInputData = this.prevYearInputService.getPreviousYearInputData(reportId);
                    if(previousYearInputData != null){
                        for(PreviousYearInputDataDto previousYearInputDataDto: previousYearInputData){
                            if(previousYearInputDataDto.getAccountBalanceKZT() != null && previousYearInputDataDto.getChartOfAccounts() != null &&
                                    previousYearInputDataDto.getChartOfAccounts().getNBChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010)) {
                                record3_1 = MathUtils.subtract(record.getCurrentAccountBalance(), previousYearInputDataDto.getAccountBalanceKZT());
                            }
                        }
                    }
                }else if(record.getLineNumber() != null && record.getLineNumber() == 49 &&
                        record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5450_010)){
                    record5_1 = MathUtils.subtract(record.getCurrentAccountBalance(), record.getPreviousAccountBalance());
                }
            }
        }

        for(ConsolidatedBalanceFormRecordDto record: records){
            if(record.getLineNumber() != null && record.getLineNumber() == 1){
                record.setCurrentAccountBalance(record1);
            }else if(record.getLineNumber() != null && record.getLineNumber() == 3 && (record.getSubLineNumber() == null || record.getSubLineNumber() == 1)){
                record.setCurrentAccountBalance(record3_1);
            }else if(record.getLineNumber() != null && record.getLineNumber() == 5 && (record.getSubLineNumber() == null || record.getSubLineNumber() == 1)){
                record.setCurrentAccountBalance(record5_1);
            }else if(record.getLineNumber() != null && record.getLineNumber() == 6){
                record.setCurrentAccountBalance(MathUtils.add(record3_1, record5_1));
            }else if(record.getLineNumber() != null && record.getLineNumber() == 7){
                record.setCurrentAccountBalance(MathUtils.add(record1, MathUtils.add(record3_1, record5_1)));
            }
        }

        responseDto.setRecords(records);
        return responseDto;
    }

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
            List<ConsolidatedKZTForm6RecordDto> previousRecords = getConsolidatedBalanceKZTForm6Saved(previousReport.getId());


            // KZT 3 records
            Double formKZT3LineNumber1Difference = 0.0;
            Double formKZT3LineNumber6Difference = 0.0;

            ListResponseDto KZTForm3ResponseDto = generateConsolidatedTotalIncomeKZTForm3(reportId);
            if(KZTForm3ResponseDto.getStatus() == ResponseStatusType.FAIL){
                return KZTForm3ResponseDto;
            }
            List<ConsolidatedBalanceFormRecordDto> formKZT3Records = KZTForm3ResponseDto.getRecords();
            if(formKZT3Records != null){
                for(ConsolidatedBalanceFormRecordDto form3Record: formKZT3Records){
                    // 1
                    if(form3Record.getLineNumber() != null && form3Record.getLineNumber() == 1){
                        formKZT3LineNumber1Difference = MathUtils.subtract(form3Record.getCurrentAccountBalance(), form3Record.getPreviousAccountBalance());
                    }else if(form3Record.getLineNumber() != null && form3Record.getLineNumber() == 6){
                        formKZT3LineNumber6Difference = MathUtils.subtract(form3Record.getCurrentAccountBalance(), form3Record.getPreviousAccountBalance());
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
            }catch (IllegalStateException ex){
                responseDto.setErrorMessageEn(ex.getMessage());
                return responseDto;
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
                    sumLineNumber3Record.addValues(record);
                }else if(record.getLineNumber() != null && record.getLineNumber() == 3){
                    record.addValues(sumLineNumber3Record);
                }else if(record.getLineNumber() != null && record.getLineNumber() == 4){
                    record.setRetainedEarnings(formKZT3LineNumber1Difference);
                    record.setTotal(formKZT3LineNumber1Difference);

                    sumLineNumber6Record.addValues(record);
                }else if(record.getLineNumber() != null && record.getLineNumber() == 5){
                    record.setOtherReserves(formKZT3LineNumber6Difference);
                    record.setTotal(formKZT3LineNumber6Difference);

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

            List<ConsolidatedKZTForm7RecordDto> previousPeriodRecords = previousReport != null ? getConsolidatedBalanceKZTForm7Saved(previousReport.getId()) : null;
            if(previousPeriodRecords != null && !previousPeriodRecords.isEmpty()){
                int index = 0;
                // TODO: exclude zero records (which fields of ConsolidatedKZTForm7RecordDto to check for zero value?)
                for(ConsolidatedKZTForm7RecordDto prevRecord: previousPeriodRecords){
                    if(prevRecord.getAccountNumber() != null && prevRecord.getLineNumber() != null && prevRecord.getLineNumber() == 9 &&
                            (prevRecord.getDebtEndPeriod() == null || prevRecord.getDebtEndPeriod() == 0)){
                        continue;
                    }
                    prevRecord.setDebtStartPeriod(prevRecord.getDebtEndPeriod());
                    prevRecord.setFairValueAdjustmentsStartPeriod(prevRecord.getFairValueAdjustmentsEndPeriod());
                    prevRecord.setTotalStartPeriod(prevRecord.getTotalEndPeriod());
                    prevRecord.setDebtTurnover(null);
                    prevRecord.setFairValueAdjustmentsTurnoverPositive(null);
                    prevRecord.setFairValueAdjustmentsTurnoverNegative(null);
                    prevRecord.setDebtEndPeriod(null);
                    prevRecord.setFairValueAdjustmentsEndPeriod(null);
                    prevRecord.setTotalEndPeriod(null);
                    prevRecord.setBecameZero(false);

                    records.add(prevRecord);
                    if(prevRecord.getAccountNumber() != null && (prevRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT) ||
                            prevRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT))) {
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
            int index10 = 0;
            for(int i = 0; i < records.size(); i++){
                ConsolidatedKZTForm7RecordDto record = records.get(i);
                if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT) && indexPE == 0){
                    indexPE = i;
                }else if(record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT) && indexHF == 0){
                    indexHF = i;
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 10){
                    index10 = i;
                }
            }
            if(indexPE == 0){
                indexPE = index10;
            }
            if(indexHF == 0){
                indexHF = index10;
            }

            ListResponseDto KZTForm7CurrentResponseDto = getConsolidatedBalanceKZTForm7OnlyCurrentPeriod(reportId);
            if(KZTForm7CurrentResponseDto.getStatus() == ResponseStatusType.FAIL){
                return KZTForm7CurrentResponseDto;
            }
            List<ConsolidatedKZTForm7RecordDto> currentPeriodRecords = KZTForm7CurrentResponseDto.getRecords();

            ConsolidatedKZTForm7RecordDto totalRecord = new ConsolidatedKZTForm7RecordDto();
//            ConsolidatedKZTForm7RecordDto totalRecordPE = new ConsolidatedKZTForm7RecordDto();
//            ConsolidatedKZTForm7RecordDto totalRecordHF = new ConsolidatedKZTForm7RecordDto();

            for(ConsolidatedKZTForm7RecordDto currentRecord: currentPeriodRecords){
                if(currentRecord.getEntityName() == null){
                    break;
                }

                boolean newRecord = false;
                ConsolidatedKZTForm7RecordDto record = null;
                if(existingFundRecordsMap.get(currentRecord.getEntityName()) != null){
                    int index = existingFundRecordsMap.get(currentRecord.getEntityName());
                    record = records.get(index);
                    record.setDebtTurnover(currentRecord.getDebtTurnover());
                    record.setTotalEndPeriod(currentRecord.getTotalEndPeriod());

                }else{
                    // new record
                    record = currentRecord;
                    newRecord = true;
                }

                if(currentRecord.isBecameZero()){
                    record.setDebtTurnover(record.getDebtStartPeriod() != null ? MathUtils.subtract(0.0, record.getDebtStartPeriod()) : null);
                }

                // debtEndPeriod
                double debtEndPeriod =  MathUtils.add(record.getDebtStartPeriod(), record.getDebtTurnover());
                record.setDebtEndPeriod(debtEndPeriod);

                // fairValueAdjustmentsTurnover
                double fairValueAdjustmentsTurnover = MathUtils.subtract(record.getTotalEndPeriod(), record.getDebtEndPeriod());
                fairValueAdjustmentsTurnover = MathUtils.subtract(fairValueAdjustmentsTurnover, record.getFairValueAdjustmentsStartPeriod());
                if(fairValueAdjustmentsTurnover >=0 ){
                    record.setFairValueAdjustmentsTurnoverPositive(fairValueAdjustmentsTurnover);
                }else{
                    record.setFairValueAdjustmentsTurnoverNegative(fairValueAdjustmentsTurnover);
                }

                // setFairValueAdjustmentsEndPeriod
                double fairValueAdjustmentsEndPeriod = MathUtils.add(record.getFairValueAdjustmentsStartPeriod(), fairValueAdjustmentsTurnover);
                record.setFairValueAdjustmentsEndPeriod(fairValueAdjustmentsEndPeriod);

                // set total values
                addValuesKZTForm7(totalRecord, record);
//                if(record.getOtherName().equalsIgnoreCase(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)) {
//                    addValuesKZTForm7(totalRecordHF, record);
//                }else {
//                    addValuesKZTForm7(totalRecordPE, record);
//                }

                if(newRecord){
                    int index = record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT) ? indexPE :
                            record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT) ? indexHF : 0;
                    if(index > 0){
                        if(record.getPurchaseDate() == null) {
                            record.setPurchaseDate(DateUtils.getLastDayOfCurrentMonth(report.getReportDate()));
                        }
                        records.add(index, record);
                    }
                }

            }

            List<ConsolidatedKZTForm7RecordDto> nonEmptyRecords = new ArrayList();
            for(ConsolidatedKZTForm7RecordDto record: records){
                if(record.getAccountNumber() == null && (record.getLineNumber() == 7 || record.getLineNumber() == 9 || record.getLineNumber() == 12)){
                    record.setDebtTurnover(totalRecord.getDebtTurnover());
                    record.setFairValueAdjustmentsTurnoverPositive(totalRecord.getFairValueAdjustmentsTurnoverPositive());
                    record.setFairValueAdjustmentsTurnoverNegative(totalRecord.getFairValueAdjustmentsTurnoverNegative());
                    record.setDebtEndPeriod(totalRecord.getDebtEndPeriod());
                    record.setFairValueAdjustmentsEndPeriod(totalRecord.getFairValueAdjustmentsEndPeriod());
                    record.setTotalEndPeriod(totalRecord.getTotalEndPeriod());
                }

                if(record.getAccountNumber() != null && record.getLineNumber() != null && record.getLineNumber() == 9 &&
                        record.isEmpty()){
                    // do not add
                }else{
                    nonEmptyRecords.add(record);
                }
            }

            responseDto.setRecords(nonEmptyRecords);
            return responseDto;
        }
    }

    private ListResponseDto getConsolidatedBalanceKZTForm7OnlyCurrentPeriod(Long reportId) {

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

        Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
        PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);


        // TODO: same fund in both tranches !!

        Map<String, Double> fundTurnoverPE = new HashedMap();
        Map<String, Double> fundTurnoverHF = new HashedMap();
        // TARRAGON
        List<ScheduleInvestmentsDto> previousPeriodInvestments = this.scheduleInvestmentService.getScheduleInvestments(previousReport.getId());
        if(previousPeriodInvestments != null) {
            for (ScheduleInvestmentsDto investmentsDto: previousPeriodInvestments) {
                if(investmentsDto.getNetCost() != null && investmentsDto.getNetCost().doubleValue() != 0 &&
                        (investmentsDto.getTotalSum() == null || !investmentsDto.getTotalSum().booleanValue())) {
                    if (investmentsDto.getTranche() == 1) {
                        fundTurnoverPE.put(investmentsDto.getName(), MathUtils.multiply(investmentsDto.getNetCost(), 0.99));
                    } else {
                        fundTurnoverPE.put(investmentsDto.getName(), investmentsDto.getNetCost());
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
                    if (investmentsDto.getTranche() == 1) {
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

        // Singularity NOAL

        // TODO: Tranche B

        // current NOAL
        List<SingularityNOALRecordDto> currentNoalRecords = new ArrayList<>();
        List<SingularityNOALRecordDto> currentNoalTrancheARecords = this.hfNOALService.get(report.getId(), 1).getNoalTrancheAList();
        if(currentNoalTrancheARecords != null){
            currentNoalRecords.addAll(currentNoalTrancheARecords);
        }

        for(SingularityNOALRecordDto noalRecordDto: currentNoalRecords){
            Date date = DateUtils.getLastDayOfCurrentMonth(report.getReportDate());
            if(noalRecordDto.getTransaction() != null && noalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.EN_NOAL_PORTFOLIO_REDEMPTION) &&
                    noalRecordDto.getName() != null &&DateUtils.getDateFormatted(noalRecordDto.getDate()).equalsIgnoreCase(DateUtils.getDateFormatted(date))){
                double value = fundTurnoverHF.get(noalRecordDto.getName()) != null ? fundTurnoverHF.get(noalRecordDto.getName()).doubleValue() : 0;
                value = MathUtils.subtract(value, noalRecordDto.getTransactionAmount() != null ? noalRecordDto.getTransactionAmount().doubleValue() : 0);
                fundTurnoverHF.put(noalRecordDto.getName(), value);
            }
        }

        // previous NOAL
        List<SingularityNOALRecordDto> previousNoalRecords = new ArrayList<>();
        List<SingularityNOALRecordDto> previousNoalTrancheARecords = this.hfNOALService.get(previousReport.getId(), 1).getNoalTrancheAList();
        if(previousNoalTrancheARecords != null){
            previousNoalRecords.addAll(previousNoalTrancheARecords);
        }

        Date date = DateUtils.getLastDayOfCurrentMonth(previousReport.getReportDate());
        for(SingularityNOALRecordDto prevNoalRecordDto: previousNoalRecords){
            if(prevNoalRecordDto.getDate() != null && DateUtils.getDateFormatted(prevNoalRecordDto.getDate()).equalsIgnoreCase(DateUtils.getDateFormatted(date))){
                for(SingularityNOALRecordDto currentNoalRecordDto: currentNoalRecords){
                    if(prevNoalRecordDto.getTransaction() != null && prevNoalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.EN_NOAL_PORTFOLIO_REDEMPTION) &&
                            currentNoalRecordDto.getTransaction() != null && currentNoalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.EN_NOAL_PORTFOLIO_REDEMPTION) &&
                            prevNoalRecordDto.getName() != null && prevNoalRecordDto.getName().equalsIgnoreCase(currentNoalRecordDto.getName()) &&
                            DateUtils.getDateFormatted(currentNoalRecordDto.getDate()).equalsIgnoreCase(DateUtils.getDateFormatted(date))){
                        double prevValue = prevNoalRecordDto.getTransactionAmount() != null ? prevNoalRecordDto.getTransactionAmount().doubleValue() : 0;
                        double currValue = currentNoalRecordDto.getTransactionAmount() != null ? currentNoalRecordDto.getTransactionAmount().doubleValue() : 0;
                        if(prevValue != currValue){
                            double value = fundTurnoverHF.get(currentNoalRecordDto.getName()) != null ? fundTurnoverHF.get(currentNoalRecordDto.getName()).doubleValue() : 0;
                            value = MathUtils.subtract(MathUtils.add(value, currValue), prevValue);
                            fundTurnoverHF.put(currentNoalRecordDto.getName(), value);
                        }

                    }
                }
            }

        }

        // Singularity - General Ledger

        ConsolidatedReportRecordHolderDto currentDataHolder = this.generalLedgerBalanceService.get(reportId);
        if(currentDataHolder != null && currentDataHolder.getGeneralLedgerBalanceList() != null){
            for(SingularityGeneralLedgerBalanceRecordDto record: currentDataHolder.getGeneralLedgerBalanceList()){
                if(record.getFinancialStatementCategoryDescription() != null && record.getFinancialStatementCategoryDescription().equalsIgnoreCase(PeriodicReportConstants.EN_NET_REALIZED_GAIN_LOSS)){
                    //String fundName = record.getChartAccountsLongDescription().substring("Net Realized Gains/Losses from Portfolio Funds".length()).trim();
                    String fundName = record.getShortName();

                    double value = fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0;
                    value = MathUtils.add(value, (record.getGLAccountBalance() != null ? record.getGLAccountBalance().doubleValue() : 0));
                    fundTurnoverHF.put(fundName, value);
                }
            }
        }

        ConsolidatedReportRecordHolderDto previousDataHolder = this.generalLedgerBalanceService.get(previousReport.getId());
        if(previousDataHolder != null && previousDataHolder.getGeneralLedgerBalanceList() != null){
            for(SingularityGeneralLedgerBalanceRecordDto record: previousDataHolder.getGeneralLedgerBalanceList()){
                if(record.getFinancialStatementCategoryDescription() != null && record.getFinancialStatementCategoryDescription().equalsIgnoreCase(PeriodicReportConstants.EN_NET_REALIZED_GAIN_LOSS)){
//                    String fundName = record.getChartAccountsLongDescription().substring("Net Realized Gains/Losses from Portfolio Funds".length()).trim();
                    String fundName = record.getShortName() != null ? record.getShortName() : "";

                    double value = fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0;
                    value = MathUtils.subtract(value, (record.getGLAccountBalance() != null ? record.getGLAccountBalance().doubleValue() : 0));
                    fundTurnoverHF.put(fundName, value);
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
                        && recordUSD.getOtherEntityName() != null && (recordUSD.getOtherEntityName().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE) ||
                        recordUSD.getOtherEntityName().startsWith(PeriodicReportConstants.SINGULAR_LOWER_CASE))){
                    if (recordUSD.getName().startsWith(PeriodicReportConstants.RU_PRE_SUBSCRIPTION)) {
                        String fundName = recordUSD.getName().substring(PeriodicReportConstants.RU_PRE_SUBSCRIPTION.length()).trim();

                        double value = fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0;
                        value = MathUtils.add(value, recordUSD.getPreviousAccountBalance());

                        fundTurnoverHF.put(fundName, value);
                    }
                }else if(recordUSD.getAccountNumber() != null && recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2033_010)){
                    String name = null;
                    if(recordUSD.getOtherEntityName() != null && (recordUSD.getOtherEntityName().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE) ||
                            recordUSD.getOtherEntityName().startsWith(PeriodicReportConstants.TARRAGON_LOWER_CASE))){
                        name = PeriodicReportConstants.RU_PE_FUND_INVESTMENT;
                    }else if(recordUSD.getOtherEntityName() != null && (recordUSD.getOtherEntityName().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE) ||
                            recordUSD.getOtherEntityName().startsWith(PeriodicReportConstants.SINGULAR_LOWER_CASE))){
                        name = PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT;
                    }else{

                        logger.error("USD Report: Entity name unexpected for '" + recordUSD.getName() + "' : " + recordUSD.getOtherEntityName() + ". Expected starting with : " +
                                PeriodicReportConstants.TARRAGON_CAPITAL_CASE + ", or " + PeriodicReportConstants.TARRAGON_LOWER_CASE + ", or " +
                                PeriodicReportConstants.SINGULAR_CAPITAL_CASE + ", or " + PeriodicReportConstants.SINGULAR_LOWER_CASE);
                        responseDto.setErrorMessageEn("USD Report: Entity name unexpected for '" + recordUSD.getName() + "' : " + recordUSD.getOtherEntityName() + ". Expected starting with : " +
                                PeriodicReportConstants.TARRAGON_CAPITAL_CASE + ", or " + PeriodicReportConstants.TARRAGON_LOWER_CASE + ", or " +
                                PeriodicReportConstants.SINGULAR_CAPITAL_CASE + ", or " + PeriodicReportConstants.SINGULAR_LOWER_CASE);
                        return responseDto;
                    }
                    int fundNameIndex = name.equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT) ? PeriodicReportConstants.RU_PE_FUND_INVESTMENT.length() :
                            PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT.length();
                    String fundName = recordUSD.getName().substring(fundNameIndex).trim();

                    ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
                    if(recordUSD.getPreviousAccountBalance() != null && recordUSD.getPreviousAccountBalance().doubleValue() != 0 &&
                            (recordUSD.getCurrentAccountBalance() == null || recordUSD.getCurrentAccountBalance().doubleValue() == 0)){
                        newRecord.setBecameZero(true);
                    }

                    newRecord.setName(name);
                    newRecord.setAccountNumber(recordUSD.getAccountNumber());
                    newRecord.setLineNumber(9);
                    newRecord.setEntityName(fundName);
                    newRecord.setOtherName(recordUSD.getOtherEntityName());
                    //newRecord.setPurchaseDate();

                    newRecord.setTotalEndPeriod(MathUtils.multiply(recordUSD.getCurrentAccountBalance(), endCurrencyRatesDto.getValue()));

                    if(recordUSD.getOtherEntityName().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE) ||
                            recordUSD.getOtherEntityName().startsWith(PeriodicReportConstants.TARRAGON_LOWER_CASE)) {
                        if(fundTurnoverPE.get(fundName) == null) {
                            // case when schedule investment record current period is zero
                            newRecord.setDebtTurnover(null);
                            newRecord.setBecameZero(true);
                        }else{
                            newRecord.setDebtTurnover(MathUtils.multiply(fundTurnoverPE.get(fundName), endCurrencyRatesDto.getValue()));
                            fundTurnoverPE.put(fundName, null);
                        }
                    }else if(recordUSD.getOtherEntityName().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE) ||
                            recordUSD.getOtherEntityName().startsWith(PeriodicReportConstants.SINGULAR_LOWER_CASE)){
                        newRecord.setDebtTurnover(MathUtils.multiply(fundTurnoverHF.get(fundName), endCurrencyRatesDto.getValue()));
                        fundTurnoverHF.put(fundName, null);
                    }

                    if(newRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)) {
                        recordsPE.add(newRecord);
                    }else if(newRecord.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)){
                        recordsHF.add( newRecord);
                    }
                }
            }
        }

        // Add missing funds
        Set<String> keySet = fundTurnoverPE.keySet();
        if(keySet != null){
            Iterator<String> iterator = keySet.iterator();
            while(iterator.hasNext()){
                String fundName = iterator.next();
                if(fundTurnoverPE.get(fundName) != null) {
                    String name = PeriodicReportConstants.RU_PE_FUND_INVESTMENT;
                    ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
                    newRecord.setName(name);
                    newRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_2033_010);
                    newRecord.setLineNumber(9);
                    newRecord.setEntityName(fundName);
                    newRecord.setOtherName(PeriodicReportConstants.TARRAGON_CAPITAL_CASE);
                    //newRecord.setPurchaseDate();

                    newRecord.setTotalEndPeriod(0.0);
                    newRecord.setDebtTurnover(MathUtils.multiply(fundTurnoverPE.get(fundName), endCurrencyRatesDto.getValue()));

                    recordsPE.add( newRecord);
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

        responseDto.setRecords(recordsHF);
        return responseDto;
    }

    private void addValuesKZTForm7(ConsolidatedKZTForm7RecordDto totalRecord, ConsolidatedKZTForm7RecordDto record){
        double debtTurnover = totalRecord.getDebtTurnover() != null ? totalRecord.getDebtTurnover().doubleValue() : 0;
        debtTurnover = MathUtils.add(debtTurnover, (record.getDebtTurnover() != null ? record.getDebtTurnover().doubleValue() : 0));
        totalRecord.setDebtTurnover(debtTurnover);

        double totalFairValueAdjustmentsTurnoverPositive = totalRecord.getFairValueAdjustmentsTurnoverPositive() != null ? totalRecord.getFairValueAdjustmentsTurnoverPositive().doubleValue() : 0;
        totalFairValueAdjustmentsTurnoverPositive = MathUtils.add(totalFairValueAdjustmentsTurnoverPositive, (record.getFairValueAdjustmentsTurnoverPositive() != null ? record.getFairValueAdjustmentsTurnoverPositive().doubleValue() : 0));
        totalRecord.setFairValueAdjustmentsTurnoverPositive(totalFairValueAdjustmentsTurnoverPositive);

        double totalFairValueAdjustmentsTurnoverNegative = totalRecord.getFairValueAdjustmentsTurnoverNegative() != null ? totalRecord.getFairValueAdjustmentsTurnoverNegative().doubleValue() : 0;
        totalFairValueAdjustmentsTurnoverNegative = MathUtils.add(totalFairValueAdjustmentsTurnoverNegative, (record.getFairValueAdjustmentsTurnoverNegative() != null ? record.getFairValueAdjustmentsTurnoverNegative().doubleValue() : 0));
        totalRecord.setFairValueAdjustmentsTurnoverNegative(totalFairValueAdjustmentsTurnoverNegative);

        double debtEndPeriod = totalRecord.getDebtEndPeriod() != null ? totalRecord.getDebtEndPeriod().doubleValue() : 0;
        debtEndPeriod = MathUtils.add(debtEndPeriod, (record.getDebtEndPeriod() != null ? record.getDebtEndPeriod().doubleValue() : 0));
        totalRecord.setDebtEndPeriod(debtEndPeriod);

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
            List<ConsolidatedKZTForm8RecordDto> previousRecords = getConsolidatedBalanceKZTForm8Saved(previousReport.getId());

            int index = 0;
            List<ConsolidatedKZTForm8RecordDto> records = new ArrayList<>();
            Map<String, Integer> recordsMap = new HashMap<>();
            if(previousRecords != null){
                int addedIndex = 0;
                for(int i = 0; i < previousRecords.size(); i++){
                    ConsolidatedKZTForm8RecordDto record = previousRecords.get(i);
                    if(record.getAccountNumber() == null || (record.getDebtEndPeriod() != null && record.getDebtEndPeriod() != 0) ||
                            (record.getEndPeriodBalance() != null && record.getEndPeriodBalance() != 0)){
                        record.setDebtStartPeriod(record.getDebtEndPeriod());
                        record.setDebtEndPeriod(null);
                        record.setDebtDifference(null);
                        record.setStartPeriodBalance(record.getEndPeriodBalance());
                        record.setEndPeriodBalance(null);

                        records.add(record);
                        recordsMap.put(record.getName(), addedIndex);
                        addedIndex++;
                    }
                }
            }else{
                records = getConsolidatedBalanceKZTForm8LineHeaders();
            }
            for(int i = 0; i < records.size(); i++){
                ConsolidatedKZTForm8RecordDto record = records.get(i);
                if(record.getLineNumber() == 10){
                    index = i;
                    break;
                }
            }

            List<SingularityNOALRecordDto> noalRecords = new ArrayList<>();
            ConsolidatedReportRecordHolderDto currentNoalAHolder = this.hfNOALService.get(report.getId(), 1);
            List<SingularityNOALRecordDto> currentNoalTrancheARecords = currentNoalAHolder != null ? currentNoalAHolder.getNoalTrancheAList() : null;
            ConsolidatedReportRecordHolderDto currentNoalBHolder = this.hfNOALService.get(report.getId(), 2);
            List<SingularityNOALRecordDto> currentNoalTrancheBRecords = currentNoalBHolder != null ? currentNoalBHolder.getNoalTrancheBList() : null;
            ConsolidatedReportRecordHolderDto prevNoalAHolder = previousReport != null ? this.hfNOALService.get(previousReport.getId(), 1) : null;
            List<SingularityNOALRecordDto> previousNoalTrancheARecords = prevNoalAHolder != null ? prevNoalAHolder.getNoalTrancheAList() : null;
            ConsolidatedReportRecordHolderDto prevNoalBHolder =  previousReport != null ? this.hfNOALService.get(previousReport.getId(), 2) : null;
            List<SingularityNOALRecordDto> previousNoalTrancheBRecords = prevNoalBHolder != null ? prevNoalBHolder.getNoalTrancheBList() : null;
            if(currentNoalTrancheARecords != null){
                noalRecords.addAll(currentNoalTrancheARecords);
            }
            if(currentNoalTrancheBRecords != null){
                noalRecords.addAll(currentNoalTrancheBRecords);
            }
            if(previousNoalTrancheARecords != null){
                noalRecords.addAll(previousNoalTrancheARecords);
            }
            if(previousNoalTrancheBRecords != null){
                noalRecords.addAll(previousNoalTrancheBRecords);
            }

            ListResponseDto balanceUSDResponseDto = generateConsolidatedBalanceUSDForm(reportId);
            if(balanceUSDResponseDto.getStatus() == ResponseStatusType.FAIL){
                responseDto.setErrorMessageEn("Error generating USD report. " + balanceUSDResponseDto.getMessage().getNameEn());
                return responseDto;
            }
            List<ConsolidatedBalanceFormRecordDto> USDFormRecords = balanceUSDResponseDto.getRecords();

            ConsolidatedKZTForm8RecordDto totalRecord = new ConsolidatedKZTForm8RecordDto();
            if(records != null && index > 0){
                Double debtEndPeriod = 0.0;
                Double debtDifference = 0.0;
                Double endPeriodBalance = 0.0;
                for(ConsolidatedBalanceFormRecordDto recordUSD: USDFormRecords){
                    if(recordUSD.getLineNumber() == 8 && recordUSD.getAccountNumber() != null){
                        // check if exists in previous
                        if(recordsMap.get(recordUSD.getName()) != null){
                            int existingRecordIndex = recordsMap.get(recordUSD.getName());
                            ConsolidatedKZTForm8RecordDto existingRecord = records.get(existingRecordIndex);

                            existingRecord.setDebtEndPeriod(MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));
                            existingRecord.setDebtDifference(MathUtils.subtract(existingRecord.getDebtEndPeriod(), existingRecord.getDebtStartPeriod()));
                            existingRecord.setEndPeriodBalance(existingRecord.getDebtEndPeriod());

                            debtEndPeriod = existingRecord.getDebtEndPeriod();
                            debtDifference = existingRecord.getDebtDifference();
                            endPeriodBalance = existingRecord.getEndPeriodBalance();
                        }else {
                            // new record
                            ConsolidatedKZTForm8RecordDto newRecord = new ConsolidatedKZTForm8RecordDto();
                            newRecord.setLineNumber(9);
                            newRecord.setName(recordUSD.getName());
                            newRecord.setAccountNumber(recordUSD.getAccountNumber());

                            //newRecord.setDebtStartPeriod(MathUtils.multiply(record.getPreviousAccountBalance() != null ? record.getPreviousAccountBalance() : 0.0, startCurrencyRatesDto.getValue()));
                            newRecord.setDebtEndPeriod(MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));
                            newRecord.setDebtDifference(MathUtils.subtract(newRecord.getDebtEndPeriod(), newRecord.getDebtStartPeriod()));
                            newRecord.setEndPeriodBalance(newRecord.getDebtEndPeriod());

                            debtEndPeriod = newRecord.getDebtEndPeriod();
                            debtDifference = newRecord.getDebtDifference();
                            endPeriodBalance = newRecord.getEndPeriodBalance();

                            newRecord.setAgreementDescription(PeriodicReportConstants.SINGULARITY_AGREEMENT_DESC);

                            // Debt start date from NOAL
                            Date date = null;
                            for (SingularityNOALRecordDto noalRecord : noalRecords) {
                                if (newRecord.getAccountNumber() != null && newRecord.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1283_020)) {
                                    if (newRecord.getName().startsWith(PeriodicReportConstants.RU_INVESTMENTS_TO_RETURN)) {
                                        String name = newRecord.getName().substring(21).trim();
                                        if (StringUtils.isNotEmpty(name) && noalRecord.getName() != null && name.equalsIgnoreCase(noalRecord.getName())) {
                                            if (date == null || (noalRecord.getDate() != null && noalRecord.getDate().compareTo(date) < 0)) {
                                                date = noalRecord.getDate();
                                            }
                                        }
                                    } else if (newRecord.getName().startsWith(PeriodicReportConstants.RU_PRE_SUBSCRIPTION)) {
                                        String name = newRecord.getName().substring(24).trim();
                                        if (StringUtils.isNotEmpty(name) && noalRecord.getName() != null && name.equalsIgnoreCase(noalRecord.getName())) {
                                            if (date == null || (noalRecord.getDate() != null && noalRecord.getDate().compareTo(date) < 0)) {
                                                date = noalRecord.getDate();
                                            }
                                        }
                                    }
                                }
                            }
                            newRecord.setDebtStartDate(date);


                            records.add(index, newRecord);
                            index ++;
                        }

                        totalRecord.setDebtEndPeriod(MathUtils.add(totalRecord.getDebtEndPeriod(), debtEndPeriod));
                        totalRecord.setDebtDifference(MathUtils.add(totalRecord.getDebtDifference(), debtDifference));
                        totalRecord.setEndPeriodBalance(MathUtils.add(totalRecord.getEndPeriodBalance(), endPeriodBalance));
                    }
                }

            }

            // Set total sums
            for(ConsolidatedKZTForm8RecordDto record: records){
                if(record.getAccountNumber() == null && (record.getLineNumber() == 1 || record.getLineNumber() == 9 || record.getLineNumber() == 19)){
//                    record.setDebtStartPeriod(totalRecord.getDebtStartPeriod());
                    record.setDebtEndPeriod(totalRecord.getDebtEndPeriod());
                    record.setDebtDifference(totalRecord.getDebtDifference());
//                    record.setStartPeriodBalance(totalRecord.getStartPeriodBalance());
                    record.setEndPeriodBalance(totalRecord.getEndPeriodBalance());
                }
            }

            responseDto.setRecords(records);
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
            List<ConsolidatedKZTForm10RecordDto> prevRecords = getConsolidatedBalanceKZTForm10Saved(previousReport.getId());
            for(ConsolidatedKZTForm10RecordDto prevRecord: prevRecords){
                if(prevRecord.getAccountNumber() == null || (prevRecord.getEndPeriodAssets() != null && prevRecord.getEndPeriodAssets() != 0)) {
                    prevRecord.setStartPeriodAssets(prevRecord.getEndPeriodAssets());
                    prevRecord.setStartPeriodBalance(prevRecord.getEndPeriodBalance());

                    prevRecord.setTurnoverOther(null);
                    prevRecord.setEndPeriodAssets(null);
                    prevRecord.setEndPeriodBalance(null);

                    records.add(prevRecord);
                }
            }

            int indexLineToAdd2 = 0;
            int indexLineToAdd8 = 0;
            Map<String,Integer> addedRecordNames = new HashMap<>();
            for(int i = 0; i < records.size(); i++){
                ConsolidatedKZTForm10RecordDto record = records.get(i);
                if(record.getLineNumber() == 3){
                    indexLineToAdd2 = i;
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
                responseDto.setErrorMessageEn("Error generating KZT Form 1. " + balanceUSDResponseDto.getMessage().getNameEn());
                return responseDto;
            }
            List<ConsolidatedBalanceFormRecordDto> USDFormRecords = balanceUSDResponseDto.getRecords();
            Double total1623EndPeriod = 0.0;
            Double total2923EndPeriod = 0.0;
            if(USDFormRecords != null && (indexLineToAdd2 > 0 || indexLineToAdd8 > 0)){
                for(ConsolidatedBalanceFormRecordDto recordUSD: USDFormRecords){
                    if(recordUSD.getAccountNumber() != null && (recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1623_010) || recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2923_010))){
                        if(recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1623_010)) {
                            boolean recordExists = false;
                            for(ConsolidatedKZTForm10RecordDto record: records){
                                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1623_010) &&
                                        record.getName().equalsIgnoreCase(recordUSD.getName())){
                                    double endPeriodAssetsValue = MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue());
                                    record.setEndPeriodAssets(MathUtils.add(endPeriodAssetsValue, record.getEndPeriodAssets()));

                                    double endPeriodAssets = record.getEndPeriodAssets() != null ? record.getEndPeriodAssets() : 0.0;
                                    double startPeriodAssets = record.getStartPeriodAssets() != null ? record.getStartPeriodAssets() : 0.0;
                                    record.setTurnoverOther( MathUtils.subtract(endPeriodAssets, startPeriodAssets));
                                    recordExists = true;
                                    break;
                                }
                            }
                            if(!recordExists){
                                ConsolidatedKZTForm10RecordDto newRecord = new ConsolidatedKZTForm10RecordDto();
                                newRecord.setAccountNumber(recordUSD.getAccountNumber());
                                newRecord.setName(recordUSD.getName());
                                newRecord.setLineNumber(2);

                                newRecord.setEndPeriodAssets(MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));

                                double endPeriodAssets = newRecord.getEndPeriodAssets() != null ? newRecord.getEndPeriodAssets() : 0.0;
                                double startPeriodAssets = newRecord.getStartPeriodAssets() != null ? newRecord.getStartPeriodAssets() : 0.0;
                                newRecord.setTurnoverOther(MathUtils.subtract(endPeriodAssets, startPeriodAssets));

                                records.add(indexLineToAdd2, newRecord);

                                indexLineToAdd2++;
                                indexLineToAdd8++;
                            }

                            total1623EndPeriod = MathUtils.add(total1623EndPeriod, MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0, endCurrencyRatesDto.getValue()));
                        }else if(recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2923_010)) {
                            String name = null;
                            if(recordUSD.getName().equalsIgnoreCase(nameNICKMFMain) || recordUSD.getName().equalsIgnoreCase(nameNICKMFOther)){
                                name = nameNICKMFMain;
                            }else if(recordUSD.getName().equalsIgnoreCase(nameSingularityMain) || recordUSD.getName().equalsIgnoreCase(nameSingularityOther)){
                                name = nameSingularityMain;
                            }else{

                                logger.error("Report KZT 10: account number '2923.010' expected name mismatch: " + recordUSD.getName());
                                responseDto.setErrorMessageEn("USD Balance Report error: account number '2923.010' expected name mismatch  '" + recordUSD.getName() + "'");
                                return responseDto;
                            }

                            boolean recordExists = false;
                            for(ConsolidatedKZTForm10RecordDto record: records){
                                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2923_010) &&
                                        record.getName().equalsIgnoreCase(name)){
                                    double endPeriodAssetsUSDValue = MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue());
                                    record.setEndPeriodAssets(MathUtils.add(endPeriodAssetsUSDValue, record.getEndPeriodAssets()));

                                    double endPeriodAssets = record.getEndPeriodAssets() != null ? record.getEndPeriodAssets() : 0.0;
                                    double startPeriodAssets = record.getStartPeriodAssets() != null ? record.getStartPeriodAssets() : 0.0;
                                    record.setTurnoverOther(MathUtils.subtract(endPeriodAssets, startPeriodAssets));
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
                                newRecord.setTurnoverOther(MathUtils.subtract(endPeriodAssets, startPeriodAssets));

                                records.add(indexLineToAdd8, newRecord);

                                indexLineToAdd2++;
                                indexLineToAdd8++;
                            }
                            total2923EndPeriod = MathUtils.add(total2923EndPeriod, MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0, endCurrencyRatesDto.getValue()));
                        }
                    }
                }
            }

            // Set total sums
            for(ConsolidatedKZTForm10RecordDto record: records){
                if(record.getAccountNumber() == null && (record.getLineNumber() == 1)){
                    //record.setStartPeriodAssets(total1623StartPeriod);
                    record.setEndPeriodAssets(total1623EndPeriod);
                }else if(record.getAccountNumber() == null && (record.getLineNumber() == 2)){
                    //record.setStartPeriodAssets(total1623StartPeriod);
                    record.setEndPeriodAssets(total1623EndPeriod);
                }else if(record.getAccountNumber() == null && (record.getLineNumber() == 8)){
                    //record.setStartPeriodAssets(total2923StartPeriod.doubleValue());
                    record.setEndPeriodAssets(total2923EndPeriod);
                }else if(record.getAccountNumber() == null && (record.getLineNumber() == 4)){
                    //record.setStartPeriodAssets(total2923StartPeriod.doubleValue());
                    record.setEndPeriodAssets(total2923EndPeriod);
                }else if(record.getAccountNumber() == null && (record.getLineNumber() == 11)){
                    //record.setStartPeriodAssets(total1623StartPeriod + total2923StartPeriod.doubleValue());
                    record.setEndPeriodAssets(total1623EndPeriod + total2923EndPeriod);
                }

                double endPeriodAssets = record.getEndPeriodAssets() != null ? record.getEndPeriodAssets() : 0.0;
                double startPeriodAssets = record.getStartPeriodAssets() != null ? record.getStartPeriodAssets() : 0.0;
                record.setTurnoverOther(MathUtils.subtract(endPeriodAssets, startPeriodAssets));

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
            List<ConsolidatedKZTForm13RecordDto> previousRecords = getConsolidatedBalanceKZTForm13Saved(previousReport.getId());

            int index = 0;
            ConsolidatedKZTForm13RecordDto record3013_010 = null;
            boolean previousExists = false;
            if(previousRecords == null){
                previousRecords = getConsolidatedBalanceKZTForm13LineHeaders();
            }else{
                for(int i = 0; i < previousRecords.size(); i++){
                    ConsolidatedKZTForm13RecordDto record = previousRecords.get(i);
                    if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3013_010) &&
                            record.getName().equalsIgnoreCase(PeriodicReportConstants.BANK_LOANS_RECEIVED)){
                        record3013_010 = record;

                        record3013_010.setDebtStartPeriod(record.getDebtEndPeriod());
                        record3013_010.setInterestStartPeriod(record.getInterestEndPeriod());
                        record3013_010.setTotalStartPeriod(record.getTotalEndPeriod());

                        record3013_010.setDebtEndPeriod(null);
                        record3013_010.setInterestEndPeriod(null);
                        record3013_010.setTotalEndPeriod(null);

                        record3013_010.setDebtTurnover(null);
                        record3013_010.setInterestTurnover(null);
                        record3013_010.setTotalTurnover(null);

                        previousExists = true;
                    }
                    if(record.getLineNumber() == 3){
                        index = i;
                        break;
                    }
                }
            }

            if(record3013_010 == null) {
                record3013_010 = new ConsolidatedKZTForm13RecordDto();
                record3013_010.setAccountNumber(PeriodicReportConstants.ACC_NUM_3013_010);
                record3013_010.setName(PeriodicReportConstants.BANK_LOANS_RECEIVED);
                record3013_010.setLineNumber(2);
                record3013_010.setEntityName(PeriodicReportConstants.BANK_OF_MONREAL);
                Date startPeriod = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
                record3013_010.setStartPeriod(startPeriod);

                String endPeriodDateText = "16.09." + DateUtils.getYear(report.getReportDate());
                Date endPeriodDate = DateUtils.getDate(endPeriodDateText);
                if (report.getReportDate().compareTo(endPeriodDate) < 0) {
                    record3013_010.setEndPeriod(endPeriodDate);
                } else {
                    record3013_010.setEndPeriod(DateUtils.getDate("16.09." + (DateUtils.getYear(report.getReportDate()) + 1)));
                }
                record3013_010.setInterestRate(PeriodicReportConstants.KZT_13_INTEREST_RATE);
                record3013_010.setInterestPaymentCount(1);
            }

            boolean currentExists = false;
            ListResponseDto balanceUSDResponseDto = generateConsolidatedBalanceUSDForm(report.getId());
            if(balanceUSDResponseDto.getStatus() == ResponseStatusType.FAIL){
                responseDto.setErrorMessageEn("Error generating USD Balance report. " + balanceUSDResponseDto.getMessage().getNameEn());
                return responseDto;
            }
            List<ConsolidatedBalanceFormRecordDto> currentUSDFormRecords = balanceUSDResponseDto.getRecords();
            if(currentUSDFormRecords != null){
                for(ConsolidatedBalanceFormRecordDto record: currentUSDFormRecords){
                    if(record.getAccountNumber() != null && (record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3013_010)) && record3013_010.getName().equalsIgnoreCase(record.getName())){
//                        double debtEndPeriod = record3013_010.getDebtEndPeriod() != null ? record3013_010.getDebtEndPeriod() : 0;
                        double currentValue = record3013_010.getDebtEndPeriod() != null ? record3013_010.getDebtEndPeriod() : 0;
                        record3013_010.setDebtEndPeriod(MathUtils.add(currentValue, /*debtEndPeriod,*/ MathUtils.multiply(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue())));

                        currentExists = true;
                    }else if(record.getAccountNumber() != null && (record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3383_010))){
//                        double interestEndPeriod = record3013_010.getInterestEndPeriod() != null ? record3013_010.getInterestEndPeriod() : 0;
                        double currentValue = record3013_010.getInterestEndPeriod() != null ? record3013_010.getInterestEndPeriod() : 0;
                        record3013_010.setInterestEndPeriod(MathUtils.add(currentValue, /*interestEndPeriod,*/ MathUtils.multiply(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue())));

                        currentExists = true;
                    }

                }
            }

            if(currentExists) {
                double totalEndPeriod = record3013_010.getDebtEndPeriod() != null ? record3013_010.getDebtEndPeriod() : 0;
                totalEndPeriod = MathUtils.add(totalEndPeriod, record3013_010.getInterestEndPeriod() != null ? record3013_010.getInterestEndPeriod() : 0);
                record3013_010.setTotalEndPeriod(totalEndPeriod);

                double interestTurnover = record3013_010.getInterestEndPeriod() != null ? record3013_010.getInterestEndPeriod() : 0;
                interestTurnover = MathUtils.subtract(interestTurnover, record3013_010.getInterestStartPeriod() != null ? record3013_010.getInterestStartPeriod() : 0);
                record3013_010.setInterestTurnover(interestTurnover);

                double debtTurnover = record3013_010.getDebtEndPeriod() != null ? record3013_010.getDebtEndPeriod() : 0;
                debtTurnover = MathUtils.subtract(debtTurnover, record3013_010.getDebtStartPeriod() != null ? record3013_010.getDebtStartPeriod() : 0);
                record3013_010.setDebtTurnover(debtTurnover);
            }

            if(!previousExists && currentExists){
                previousRecords.add(index, record3013_010);
            }

            responseDto.setRecords(previousRecords);
            return responseDto;
        }
    }

    private String getAgreementDescription(String name){
        if(name == null){
            return null;
        }else if(name.contains(PeriodicReportConstants.TARRAGON_LOWER_CASE)){
            return PeriodicReportConstants.TARRAGON_AGREEMENT_DESC;
        }else if(name.contains(PeriodicReportConstants.SINGULARITY_LOWER_CASE)){
            return PeriodicReportConstants.SINGULARITY_AGREEMENT_DESC;
        }else if(name.contains("NICK MF")){
            return PeriodicReportConstants.NICK_MF_AGREEMENT_DESC;
        }
        return null;
    }

    @Override
    public  ListResponseDto generateConsolidatedBalanceKZTForm14(Long reportId) {

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
            if(previousReport != null){
                records = getConsolidatedBalanceKZTForm14Saved(previousReport.getId());
            }

            if(records == null){
                records = getConsolidatedBalanceKZTForm14LineHeaders();
            }

            int index = 0;
            for(int i = 0; i < records.size(); i++){
                ConsolidatedKZTForm14RecordDto record = records.get(i);
                record.setDebtStartPeriod(record.getDebtEndPeriod());

                record.setDebtEndPeriod(null);
                record.setDebtDifference(null);

                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3393_020) && record.getLineNumber() != null &&
                        record.getLineNumber() == 8) {
                    record.setAgreementDescription(getAgreementDescription(record.getName()));
                    if(record.getDebtStartDate() == null) {
                        record.setDebtStartDate(DateUtils.getLastDayOfCurrentMonth(report.getReportDate()));
                    }
                }

                if(record.getLineNumber() == 9 && index == 0){
                    index = i;
                }
            }

            List<ConsolidatedKZTForm14RecordDto> toAddRecords = new ArrayList<>();
            List<Integer> toAddIndices = new ArrayList<>();
            ConsolidatedKZTForm14RecordDto totalRecord = new ConsolidatedKZTForm14RecordDto();
            ListResponseDto balanceUSDResponseDto = generateConsolidatedBalanceUSDForm(report.getId());
            if(balanceUSDResponseDto.getStatus() == ResponseStatusType.FAIL){
                responseDto.setErrorMessageEn("Error generating USD Balance report. " + balanceUSDResponseDto.getMessage().getNameEn());
                return responseDto;
            }
            List<ConsolidatedBalanceFormRecordDto> USDFormRecords = balanceUSDResponseDto.getRecords();
            if(records != null && index > 0){
                for(ConsolidatedBalanceFormRecordDto recordUSD: USDFormRecords){
                    boolean exists = false;
                    if(recordUSD.getAccountNumber() != null && recordUSD.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3393_020)){
                        for(ConsolidatedKZTForm14RecordDto record: records){
                            if(record.getAccountNumber() != null && recordUSD.getAccountNumber().equalsIgnoreCase(record.getAccountNumber()) &&
                                    record.getName().equalsIgnoreCase(recordUSD.getName())){

                                record.setDebtEndPeriod(MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));

                                double debtEndPeriodValue = totalRecord.getDebtEndPeriod() != null ? totalRecord.getDebtEndPeriod() : 0;
                                totalRecord.setDebtEndPeriod(MathUtils.add(debtEndPeriodValue, record.getDebtEndPeriod()));

                                if(record.getDebtStartPeriod() != null && record.getDebtEndPeriod() != null) {
                                    BigDecimal start = new BigDecimal(record.getDebtStartPeriod());
                                    BigDecimal end = new BigDecimal(record.getDebtEndPeriod());
                                    BigDecimal diff = end.subtract(start).setScale(2, RoundingMode.HALF_UP);
                                    record.setDebtDifference(diff.doubleValue());

                                    double debtDiffValue = totalRecord.getDebtDifference() != null ? totalRecord.getDebtDifference() : 0;
                                    totalRecord.setDebtDifference(debtDiffValue + record.getDebtDifference());
                                }
                                exists = true;
                                break;
                            }else{
                            }
                        }
                        if(!exists){
                            ConsolidatedKZTForm14RecordDto newRecord = new ConsolidatedKZTForm14RecordDto();
                            newRecord.setLineNumber(8);
                            newRecord.setName(recordUSD.getName());
                            newRecord.setAccountNumber(recordUSD.getAccountNumber());

                            newRecord.setDebtEndPeriod(MathUtils.multiply(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));
                            newRecord.setDebtDifference(newRecord.getDebtEndPeriod());

                            newRecord.setAgreementDescription(getAgreementDescription(newRecord.getName()));
                            newRecord.setDebtStartDate(DateUtils.getLastDayOfCurrentMonth(report.getReportDate()));

                            //records.add(index, newRecord);
                            toAddRecords.add(newRecord);
                            toAddIndices.add(index);

                            index ++;

                            // total values
                            double debtEndPeriodValue = totalRecord.getDebtEndPeriod() != null ? totalRecord.getDebtEndPeriod() : 0;
                            totalRecord.setDebtEndPeriod(MathUtils.add(debtEndPeriodValue, newRecord.getDebtEndPeriod()));

                            double debtDiffValue = totalRecord.getDebtDifference() != null ? totalRecord.getDebtDifference() : 0;
                            totalRecord.setDebtDifference(debtDiffValue + (newRecord.getDebtDifference() != null ? newRecord.getDebtDifference() : 0));
                        }



                    }
                }

                for(int i = 0; i < toAddRecords.size(); i++){
                    ConsolidatedKZTForm14RecordDto record = toAddRecords.get(i);
                    records.add(toAddIndices.get(i), record);
                }
            }

            // Set total sums
            for(ConsolidatedKZTForm14RecordDto record: records){
                if(record.getAccountNumber() == null && (record.getLineNumber() == 1 || record.getLineNumber() == 8 || record.getLineNumber() == 17)){
                    record.setDebtEndPeriod(totalRecord.getDebtEndPeriod());
                    record.setDebtDifference(totalRecord.getDebtDifference());
                }
            }

            responseDto.setRecords(records);
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
                previousRecords = getConsolidatedBalanceKZTForm19Saved(previousReport.getId());
            }

            if(previousRecords != null && !previousRecords.isEmpty()){
                int index = 0;
                for(ConsolidatedKZTForm19RecordDto record: currentRecords){
                    for(int i = index; i < previousRecords.size(); i++){
                        if(record.getName().equalsIgnoreCase(previousRecords.get(i).getName()) && record.getLineNumber() != null &&
                                previousRecords.get(i).getLineNumber() != null && record.getLineNumber() == previousRecords.get(i).getLineNumber()){
                            record.setPreviousAccountBalance(previousRecords.get(i).getCurrentAccountBalance());
                            if(record.getPreviousAccountBalance() != null && record.getCurrentAccountBalance() != null) {
                                record.setTurnover(MathUtils.subtract(record.getCurrentAccountBalance(), record.getPreviousAccountBalance()));
                            }
                            break;
                        }
                    }
                }
            }

            // TODO: records in previous not int current?

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

        BigDecimal record6150_030HF = new BigDecimal("0");
        BigDecimal record6150_030PE = new BigDecimal("0");
        BigDecimal record7330_030HF = new BigDecimal("0");
        BigDecimal record7330_030PE = new BigDecimal("0");
        BigDecimal record7313_010 = new BigDecimal("0");

        List<ConsolidatedBalanceFormRecordDto> incomeExpenseUSDRecords = generateConsolidatedIncomeExpenseUSDForm(reportId);
        for(ConsolidatedBalanceFormRecordDto recordDto: incomeExpenseUSDRecords){
            if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030) &&
                    recordDto.getOtherEntityName() != null && (recordDto.getOtherEntityName().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE) ||
                    recordDto.getOtherEntityName().startsWith(PeriodicReportConstants.SINGULAR_LOWER_CASE))){
                record6150_030HF = MathUtils.add(record6150_030HF, new BigDecimal(MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate)));
            }else if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030) &&
                    recordDto.getOtherEntityName() != null && (recordDto.getOtherEntityName().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE) ||
                    recordDto.getOtherEntityName().startsWith(PeriodicReportConstants.TARRAGON_LOWER_CASE))){
                record6150_030PE = MathUtils.add(record6150_030PE, new BigDecimal(MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate)));
            }else if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030) &&
                    recordDto.getOtherEntityName() != null && (recordDto.getOtherEntityName().startsWith(PeriodicReportConstants.TARRAGON_CAPITAL_CASE) ||
                    recordDto.getOtherEntityName().startsWith(PeriodicReportConstants.TARRAGON_LOWER_CASE))){
                record7330_030PE = MathUtils.add(record7330_030PE, new BigDecimal(MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate)));
            }else if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030) &&
                    recordDto.getOtherEntityName() != null && (recordDto.getOtherEntityName().startsWith(PeriodicReportConstants.SINGULAR_CAPITAL_CASE) ||
                    recordDto.getOtherEntityName().startsWith(PeriodicReportConstants.SINGULAR_LOWER_CASE))){
                record7330_030HF =  MathUtils.add(record7330_030HF, new BigDecimal(MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate)));
            }else if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010)){
                record7313_010 =  MathUtils.add(record7313_010, new BigDecimal(MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate)));
            }
        }

        double sum = MathUtils.add(record6150_030HF, record6150_030PE, record7330_030HF, record7330_030PE, record7313_010).doubleValue();

        List<ConsolidatedKZTForm19RecordDto> records = getConsolidatedBalanceKZTForm19LineHeaders();
        for(ConsolidatedKZTForm19RecordDto record: records){
            if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)){
                record.setCurrentAccountBalance(record6150_030HF.doubleValue());
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)){
                record.setCurrentAccountBalance(record6150_030PE.doubleValue());
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT)){
                record.setCurrentAccountBalance(record7330_030HF.doubleValue());
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.RU_PE_FUND_INVESTMENT)){
                record.setCurrentAccountBalance(record7330_030PE.doubleValue());
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010) &&
                    record.getName().equalsIgnoreCase(PeriodicReportConstants.BANK_LOANS_RECEIVED)){
                record.setCurrentAccountBalance(record7313_010.doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 32){
                record.setCurrentAccountBalance(MathUtils.add(record6150_030HF, record6150_030PE, record7330_030HF, record7330_030PE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 34){
                record.setCurrentAccountBalance(MathUtils.add(record6150_030HF, record6150_030PE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 36){
                record.setCurrentAccountBalance(MathUtils.add(record7330_030HF, record7330_030PE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 42){
                record.setCurrentAccountBalance(record7313_010.doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 43){
                record.setCurrentAccountBalance(record7313_010.doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 54){
                record.setCurrentAccountBalance(sum);
            }
        }

        List<ConsolidatedKZTForm19RecordDto> recordsNoEmpty = new ArrayList<>();
        for(ConsolidatedKZTForm19RecordDto record: records){
            if((record.getCurrentAccountBalance() != null && record.getCurrentAccountBalance().doubleValue() != 0) ||
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
                    for(int i = index; i < previousRecords.size(); i++){
                        if(record.getName().equalsIgnoreCase(previousRecords.get(i).getName()) && record.getLineNumber() != null &&
                                previousRecords.get(i).getLineNumber() != null && record.getLineNumber() == previousRecords.get(i).getLineNumber()){
                            record.setPreviousAccountBalance(previousRecords.get(i).getCurrentAccountBalance());
                            if(record.getPreviousAccountBalance() != null && record.getCurrentAccountBalance() != null) {
                                record.setTurnover(MathUtils.subtract(record.getCurrentAccountBalance(), record.getPreviousAccountBalance()));
                            }
                            break;
                        }
                    }
                }
            }

            // TODO: records in previous not int current?

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
        BigDecimal record1Sum = new BigDecimal("0");
        BigDecimal record2Sum = new BigDecimal("0");
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
                record1Sum = record1Sum.add(new BigDecimal(newRecord.getCurrentAccountBalance()));
            }else if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7473_080)){
                ConsolidatedKZTForm22RecordDto newRecord = new ConsolidatedKZTForm22RecordDto(recordDto.getName(), 2);
                newRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_7473_080);
                newRecord.setCurrentAccountBalance(MathUtils.multiply(recordDto.getCurrentAccountBalance(), averageRate));
                records.add(index2, newRecord);

                index1++;
                index2++;
                record2Sum = MathUtils.add(record2Sum, new BigDecimal(newRecord.getCurrentAccountBalance() != null ? newRecord.getCurrentAccountBalance() : 0));
            }
        }
        for(ConsolidatedKZTForm22RecordDto record: records){
            if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 1){
                record.setCurrentAccountBalance(record1Sum.doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 2){
                record.setCurrentAccountBalance(record2Sum.doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 3){
                record.setCurrentAccountBalance(record1Sum.add(record2Sum).doubleValue());
            }
        }

        responseDto.setRecords(records);
        return responseDto;
    }


    // Helpers
    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceUSDFormLineHeaders(){
        // TODO: get from DB ?
        List<ConsolidatedBalanceFormRecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные активы", 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("Деньги (1010-1060)", 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("Вклады размещенные (за вычетом резервов на обесценение) (1150.020-1150.100, 1160.070, 1160.080, 1270.090-1270.110, 1290.070, 1290.090)", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка (1120, 1270.020, 1270.050)", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, имеющиеся в наличии для продажи (за вычетом резервов на обесценение) (1140, 1160.050, 1160.060, 1270.040, 1270.070, 1290.050)", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, удерживаемые до погашения (за вычетом резервов на обесценение) (1130, 1160.030, 1160.040, 1270.030, 1270.060, 1290.030)", 6));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные финансовые инвестиции (1110, 1150.010, 1150.110-1150.140, 1160.010, 1160.020, 1160.090, 1270.010, 1270.080, 1270.120, 1270.130, 1280.010, 1290.010, 1290.110, 1290.130)", 7));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочная торговая и прочая дебиторская задолженность (1210-1260, 1280.020, 1290.130, 1610)", 8));
        headers.add(new ConsolidatedBalanceFormRecordDto("Текущие налоговые активы (1410-1430)", 9));
        headers.add(new ConsolidatedBalanceFormRecordDto("Запасы (1310-1360)", 10));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные активы (1620, 1630)", 11));
        headers.add(new ConsolidatedBalanceFormRecordDto("Активы (или выбывающие группы), предназначенные для продажи (1510-1520)", 12));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого краткосрочных активов (сумма строк 2-12)", 13));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные активы", 14));
        headers.add(new ConsolidatedBalanceFormRecordDto("Вклады размещенные (за вычетом резервов на обесценение) (2040.010-2040.060, 2050.070, 2050.080, 2170.060, 2170.070, 1290.080, 1290.100)", 15));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, имеющиеся в наличии для продажи (за вычетом резервов на обесценение) (2030, 2050.050, 2050.060, 2170.030, 2170,050, 1290.060)", 16));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, удерживаемые до погашения (за вычетом резервов на обесценение) (2020, 2050.030, 2050.040, 2170.020, 2170.040, 1290.040)", 17));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные финансовые инвестиции (2010, 2040.070-2040.100, 2050.010, 2050.020, 2170.010, 2170.080, 1290.020, 1290.120, 1290.130)", 18));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочная торговая и прочая дебиторская задолженность (2110-2160, 2180, 2910)", 19));
        headers.add(new ConsolidatedBalanceFormRecordDto("Инвестиции, учитываемые методом долевого участия (2210)", 20));
        headers.add(new ConsolidatedBalanceFormRecordDto("Основные средства (2410-2430)", 21));
        headers.add(new ConsolidatedBalanceFormRecordDto("Нематериальные активы (2730-2750)", 22));
        headers.add(new ConsolidatedBalanceFormRecordDto("Отложенные налоговые активы (2810)", 23));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные активы (2310-2330, 2510-2520, 2610-2630, 2920-2940)", 24));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого долгосрочных активов (сумма строк 15-24)", 25));
        headers.add(new ConsolidatedBalanceFormRecordDto("Всего активы (сумма строк 13, 25)", 26));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные обязательства", 27));
        headers.add(new ConsolidatedBalanceFormRecordDto("Займы полученные (3010, 3020, 3380.010, 3380.020)", 28));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные финансовые обязательства (3040, 3050, 3380.030-3380.050, 3390.010)", 29));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочная торговая и прочая кредиторская задолженность (3310-3340, 3360, 3390.020, 3510)", 30));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные резервы (3410-3440)", 31));
        headers.add(new ConsolidatedBalanceFormRecordDto("Текущие налоговые обязательства (3110-3190, 3210-3240)", 32));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные обязательства (3030, 3350, 3370, 3520, 3540)", 33));
        headers.add(new ConsolidatedBalanceFormRecordDto("Обязательства выбывающих групп, предназначенных для продажи (3530)", 34));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого краткосрочных обязательств (сумма строк 28-34)", 35));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные  обязательства", 36));
        headers.add(new ConsolidatedBalanceFormRecordDto("Займы полученные (4010, 4020, 4160.010, 4160.020)", 37));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные финансовые обязательства (4030, 4160.030-4160.040)", 38));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочная торговая и прочая кредиторская задолженность (4110-4150, 4170, 4410)", 39));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные резервы (4210-4240)", 40));
        headers.add(new ConsolidatedBalanceFormRecordDto("Отложенные налоговые обязательства (4310)", 41));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные обязательства (4420, 4430)", 42));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого долгосрочных обязательств (сумма строк 37-42)", 43));
        headers.add(new ConsolidatedBalanceFormRecordDto("Капитал", 44));
        headers.add(new ConsolidatedBalanceFormRecordDto("Уставный капитал (5010-5030)", 45));
        headers.add(new ConsolidatedBalanceFormRecordDto("Эмиссионный доход (5310)", 46));
        headers.add(new ConsolidatedBalanceFormRecordDto("Выкупленные собственные долевые инструменты (5210)", 47));
        headers.add(new ConsolidatedBalanceFormRecordDto("Резервный капитал (5410, 5460)", 48));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие резервы (5420-5450)", 49));
        headers.add(new ConsolidatedBalanceFormRecordDto("Нераспределенная прибыль (непокрытый убыток) (5510, 5520)", 50));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого капитал (сумма строк 45-50)", 51));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.USD_FORM_1_LAST_RECORD, 52));
        return headers;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedIncomeExpenseUSDFormLineHeaders(){

        // TODO: get from DB
        List<ConsolidatedBalanceFormRecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedBalanceFormRecordDto("Выручка (6010-6030)", 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("Себестоимость реализованной продукции и услуг (7010)", 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("Валовая прибыль (убыток) (сумма строк 1, 2)", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы в виде вознаграждения по размещенным вкладам (6110.090-6110.130, 6110.210-6110.250)", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы в виде вознаграждения по приобретенным ценным бумагам (6110.030-6110.070, 6110.170-6110.200)", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от купли-продажи ценных бумаг (6280.010-6280.030, 7470.010-7470.030)", 6));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, оцениваемых по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка (6150.010, 7330.010)", 7));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, имеющихся в наличии для продажи (6150.020, 6150.030, 7330.020, 7330.030)", 8));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от переоценки иностранной валюты (6250, 7430)", 9));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие доходы (6210-6240, 6260, 6270, 6110.010, 6110.020, 6110.080, 6110.140, 6110.260, 6110.270, 6110.320, 6110.330, 6120, 6130, 6140, 6150.040, 6150.050,6160, 6280.040-6280.080, 6290, 6110.150, 6110.160, 6110.280-6110.310)", 10));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по приобретенным ценным бумагам (7310.100-7310.130)", 11));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по полученным займам и финансовой аренде (7310.010-7310.040, 7310.080, 7310.090, 7310.190-7310.220, 7320.010)", 12));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по реализации (7110)", 13));
        headers.add(new ConsolidatedBalanceFormRecordDto("Административные расходы (7210)", 14));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие расходы (7310.050-7310.070, 7310.140-7310.180, 7310.230, 7310.240, 7330.040, 7330.050, 7340.010-7340.030, 7410, 7420, 7440-7460, 7470.040-7470.080, 7480)", 15));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) до налогообложения (сумма строк 3-15)", 16));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по подоходному налогу (7710)", 17));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) после налогообложения от продолжающейся деятельности (сумма строк 16, 17)", 18));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) от прекращенной деятельности (6310, 7510)", 19));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.USD_FORM_2_LAST_RECORD, 20));
        return headers;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedIncomeExpenseKZTForm2LineHeaders(){

        List<ConsolidatedBalanceFormRecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedBalanceFormRecordDto("Выручка (6010-6030)", 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("Себестоимость реализованной продукции и услуг (7010)", 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("Валовая прибыль (убыток) (сумма строк 1, 2)", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы в виде вознаграждения по размещенным вкладам (6110.090-6110.130, 6110.210-6110.250)", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы в виде вознаграждения по приобретенным ценным бумагам (6110.030-6110.070, 6110.170-6110.200)", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от купли-продажи ценных бумаг (6280.010-6280.030, 7470.010-7470.030)", 6));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, оцениваемых по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка (6150.010, 7330.010)", 7));
        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, имеющихся в наличии для продажи (6150.020, 6150.030, 7330.020, 7330.030)", 8));

        ConsolidatedBalanceFormRecordDto record6150_030 = new ConsolidatedBalanceFormRecordDto("Доходы от изменения справедливой стоимости  долгосрочных финансовых инвестиций, имеющихся в наличии для продажи", 8);
        record6150_030.setAccountNumber(PeriodicReportConstants.ACC_NUM_6150_030);
        headers.add(record6150_030);

        ConsolidatedBalanceFormRecordDto record7330_030 = new ConsolidatedBalanceFormRecordDto("Расходы от изменения справедливой стоимости  долгосрочных финансовых инвестиций, имеющихся в наличии для продажи", 8);
        record7330_030.setAccountNumber(PeriodicReportConstants.ACC_NUM_7330_030);
        headers.add(record7330_030);

        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от переоценки иностранной валюты (6250, 7430)", 9));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие доходы (6210-6240, 6260, 6270, 6110.010, 6110.020, 6110.080, 6110.140, 6110.260, 6110.270, 6110.320, 6110.330, 6120, 6130, 6140, 6150.040, 6150.050,6160, 6280.040-6280.080, 6290, 6110.150, 6110.160, 6110.280-6110.310, 6400)", 10));

        ConsolidatedBalanceFormRecordDto record6283_080 = new ConsolidatedBalanceFormRecordDto("Прочие доходы", 10);
        record6283_080.setAccountNumber(PeriodicReportConstants.ACC_NUM_6283_080);
        headers.add(record6283_080);

        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по приобретенным ценным бумагам (7310.100-7310.130)", 11));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по полученным займам и финансовой аренде (7310.010-7310.040, 7310.080, 7310.090, 7310.190-7310.220, 7320.010)", 12));

        ConsolidatedBalanceFormRecordDto record7313_010 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_7313_010, 12);
        record7313_010.setAccountNumber(PeriodicReportConstants.ACC_NUM_7313_010);
        headers.add(record7313_010);

        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по реализации (7110)", 13));
        headers.add(new ConsolidatedBalanceFormRecordDto("Административные расходы (7210)", 14));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие расходы (7310.050-7310.070, 7310.140-7310.180, 7310.230, 7310.240, 7330.040, 7330.050, 7340.010-7340.030, 7410, 7420, 7440-7460, 7470.040-7470.080, 7480, 7600)", 15));

        ConsolidatedBalanceFormRecordDto record7473_080 = new ConsolidatedBalanceFormRecordDto("Прочие расходы", 15);
        record7473_080.setAccountNumber(PeriodicReportConstants.ACC_NUM_7473_080);
        headers.add(record7473_080);

        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) до налогообложения (сумма строк 3-15)", 16));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по подоходному налогу (7710)", 17));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) после налогообложения от продолжающейся деятельности (сумма строк 16, 17)", 18));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) от прекращенной деятельности (6310, 7510)", 19));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.USD_FORM_2_LAST_RECORD, 20));
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
        records.add(new ConsolidatedKZTForm6RecordDto("Подоходный налог по операциям с собственникам", 13));
        records.add(new ConsolidatedKZTForm6RecordDto("Операции с собственниками, отраженные непосредственно в составе капитала за период (сумма строк 8-13)", 14));
        records.add(new ConsolidatedKZTForm6RecordDto("Остаток на конец текущего отчетного периода (сумма строк 3, 6, 14)", 15));
        return records;
    }

    private List<ConsolidatedKZTForm10RecordDto> getConsolidatedBalanceKZTForm10LineHeaders(){
        List<ConsolidatedKZTForm10RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm10RecordDto("Прочие краткосрочные активы (сумма строк 2-3)", 1));
        records.add(new ConsolidatedKZTForm10RecordDto(PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD, 2));
        records.add(new ConsolidatedKZTForm10RecordDto("Прочие краткосрочные активы", 3));
        records.add(new ConsolidatedKZTForm10RecordDto("Прочие долгосрочные активы (сумма строк 5-10 )", 4));
        records.add(new ConsolidatedKZTForm10RecordDto("Инвестиции в недвижимость", 5));
        records.add(new ConsolidatedKZTForm10RecordDto("Биологические активы", 6));
        records.add(new ConsolidatedKZTForm10RecordDto("Разведочные и оценочные активы", 7));
        records.add(new ConsolidatedKZTForm10RecordDto(PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD, 8));
        records.add(new ConsolidatedKZTForm10RecordDto("Незавершенное строительство", 9));
        records.add(new ConsolidatedKZTForm10RecordDto("Прочие долгосрочные активы", 10));
        records.add(new ConsolidatedKZTForm10RecordDto("ВСЕГО  (сумма строк 1, 4)", 11));

        return records;
    }

    private List<ConsolidatedKZTForm8RecordDto> getConsolidatedBalanceKZTForm8LineHeaders(){
        List<ConsolidatedKZTForm8RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная торговая и прочая дебиторская задолженность (сумма строк 2-9)", 1));
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность покупателей и заказчиков", 2));
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность дочерних организаций", 3));
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность ассоциированных и совместных организаций", 4));
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность филиалов и представительств", 5));
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность работников", 6));
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочная дебиторская задолженность по аренде", 7));
        records.add(new ConsolidatedKZTForm8RecordDto("Краткосрочные авансы выданные", 8));
        records.add(new ConsolidatedKZTForm8RecordDto(PeriodicReportConstants.RU_1283_020, 9));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная торговая и прочая дебиторская задолженность (сумма строк 11-18)", 10));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная задолженность покупателей и заказчиков", 11));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная дебиторская задолженность дочерних организаций", 12));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная дебиторская задолженность ассоциированных и совместных организаций", 13));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная дебиторская задолженность филиалов и представительств", 14));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная дебиторская задолженность работников", 15));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочная дебиторская задолженность по аренде", 16));
        records.add(new ConsolidatedKZTForm8RecordDto("Долгосрочные авансы выданные", 17));
        records.add(new ConsolidatedKZTForm8RecordDto("Прочая долгосрочная дебиторская задолженность", 18));
        records.add(new ConsolidatedKZTForm8RecordDto("ВСЕГО (сумма строк 1, 10)", 19));

        return records;
    }

    private List<ConsolidatedKZTForm14RecordDto> getConsolidatedBalanceKZTForm14LineHeaders(){
        List<ConsolidatedKZTForm14RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная торговая и прочая дебиторская задолженность (сумма строк 2-8)", 1));
        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная кредиторская задолженность поставщикам и подрядчикам", 2));
        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная кредиторская задолженность дочерним организациям", 3));
        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная кредиторская задолженность ассоциированным и совместным организациям", 4));
        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная кредиторская задолженность филиалам и представительствам", 5));
        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочная задолженность по аренде", 6));
        records.add(new ConsolidatedKZTForm14RecordDto("Краткосрочные авансы полученные", 7));
        records.add(new ConsolidatedKZTForm14RecordDto(PeriodicReportConstants.RU_3393_020, 8));
        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная торговая и прочая кредиторская задолженность (сумма строк 10-16)", 9));
        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная кредиторская задолженность поставщикам и подрядчикам", 10));
        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная кредиторская задолженность дочерним организациям", 11));
        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная кредиторская задолженность ассоциированным и совместным организациям", 12));
        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная кредиторская задолженность филиалам и представительствам", 13));
        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочная задолженность по аренде", 14));
        records.add(new ConsolidatedKZTForm14RecordDto("Долгосрочные авансы полученные", 15));
        records.add(new ConsolidatedKZTForm14RecordDto("Прочая долгосрочная кредиторская задолженность", 16));
        records.add(new ConsolidatedKZTForm14RecordDto("ВСЕГО (сумма строк 1, 9)", 17));

        return records;
    }

    private List<ConsolidatedKZTForm13RecordDto> getConsolidatedBalanceKZTForm13LineHeaders(){
        List<ConsolidatedKZTForm13RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm13RecordDto("Краткосрочные финансовые обязательства (сумма строк 2, 3)", 1));
        records.add(new ConsolidatedKZTForm13RecordDto("Займы полученные", 2));
        records.add(new ConsolidatedKZTForm13RecordDto("Прочие краткосрочные финансовые обязательства", 3));
        records.add(new ConsolidatedKZTForm13RecordDto("Долгосрочные финансовые обязательства (сумма строк 5, 6)", 4));
        records.add(new ConsolidatedKZTForm13RecordDto("Займы полученные", 5));
        records.add(new ConsolidatedKZTForm13RecordDto("Прочие долгосрочные финансовые обязательства)", 6));
        records.add(new ConsolidatedKZTForm13RecordDto("ВСЕГО (сумма строк 1, 4)", 7));

        return records;
    }

    private List<ConsolidatedKZTForm7RecordDto> getConsolidatedBalanceKZTForm7LineHeaders(){
        List<ConsolidatedKZTForm7RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm7RecordDto("Краткосрочные финансовые инвестиции (сумма строк 2-6)", 1));
        records.add(new ConsolidatedKZTForm7RecordDto("Вклады размещенные, всего, в том числе", 2));
        records.add(new ConsolidatedKZTForm7RecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка, всего, в том числе", 3));
        records.add(new ConsolidatedKZTForm7RecordDto("Финансовые инвестиции, имеющиеся в наличии для продажи, всего в том числе", 4));
        records.add(new ConsolidatedKZTForm7RecordDto("Финансовые инвестиции, удерживаемые до погашения, всего, в том числе", 5));
        records.add(new ConsolidatedKZTForm7RecordDto("Прочие краткосрочные финансовые инвестиции, всего, в том числ", 6));
        records.add(new ConsolidatedKZTForm7RecordDto("Долгосрочные финансовые инвестиции (сумма строк 8-11)", 7));
        records.add(new ConsolidatedKZTForm7RecordDto("Вклады размещенные, всего, в том числе", 8));
        records.add(new ConsolidatedKZTForm7RecordDto("Финансовые инвестиции, имеющиеся в наличии для продажи, всего в том числе", 9));
        records.add(new ConsolidatedKZTForm7RecordDto("Финансовые инвестиции, удерживаемые до погашения, всего, в том числе", 10));
        records.add(new ConsolidatedKZTForm7RecordDto("Прочие долгосрочные финансовые инвестиции, всего, в том числе", 11));
        records.add(new ConsolidatedKZTForm7RecordDto("ВСЕГО (сумма строк 1,7)", 12));
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

        headers.add(new ConsolidatedBalanceFormRecordDto("Вклады размещенные (за вычетом резервов на обесценение) (1150.020-1150.100, 1160.070, 1160.080, 1270.090-1270.110, 1290.070, 1290.090)", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка (1120, 1270.020, 1270.050)", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, имеющиеся в наличии для продажи (за вычетом резервов на обесценение) (1140, 1160.050, 1160.060, 1270.040, 1270.070, 1290.050)", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, удерживаемые до погашения (за вычетом резервов на обесценение) (1130, 1160.030, 1160.040, 1270.030, 1270.060, 1290.030)", 6));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные финансовые инвестиции (1110, 1150.010, 1150.110-1150.140, 1160.010, 1160.020, 1160.090, 1270.010, 1270.080, 1270.120, 1270.130, 1280.010, 1290.010, 1290.110, 1290.130)", 7));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочная торговая и прочая дебиторская задолженность (1210-1260, 1280.020, 1290.130, 1610)", 8));

        ConsolidatedBalanceFormRecordDto record8 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_1283_020, 8);
        record8.setAccountNumber(PeriodicReportConstants.ACC_NUM_1283_020);
        headers.add(record8);

        headers.add(new ConsolidatedBalanceFormRecordDto("Текущие налоговые активы (1410-1430)", 9));
        headers.add(new ConsolidatedBalanceFormRecordDto("Запасы (1310-1360)", 10));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные активы (1620, 1630)", 11));

        ConsolidatedBalanceFormRecordDto record11 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD, 11);
        record11.setAccountNumber(PeriodicReportConstants.ACC_NUM_1623_010);
        headers.add(record11);

        headers.add(new ConsolidatedBalanceFormRecordDto("Активы (или выбывающие группы), предназначенные для продажи (1510-1520)", 12));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого краткосрочных активов (сумма строк 2-12)", 13));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные активы", 14));
        headers.add(new ConsolidatedBalanceFormRecordDto("Вклады размещенные (за вычетом резервов на обесценение) (2040.010-2040.060, 2050.070, 2050.080, 2170.060, 2170.070, 1290.080, 1290.100)", 15));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, имеющиеся в наличии для продажи (за вычетом резервов на обесценение) (2030, 2050.050, 2050.060, 2170.030, 2170,050, 1290.060)", 16));

        ConsolidatedBalanceFormRecordDto record16_1 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_2033_010, 16);
        record16_1.setAccountNumber(PeriodicReportConstants.ACC_NUM_2033_010);
        headers.add(record16_1);

        ConsolidatedBalanceFormRecordDto record16_2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_2033_040, 16);
        record16_2.setAccountNumber(PeriodicReportConstants.ACC_NUM_2033_040);
        headers.add(record16_2);

        ConsolidatedBalanceFormRecordDto record16_3 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_2033_050, 16);
        record16_3.setAccountNumber("2033.050");
        headers.add(record16_3);

        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, удерживаемые до погашения (за вычетом резервов на обесценение) (2020, 2050.030, 2050.040, 2170.020, 2170.040, 1290.040)", 17));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные финансовые инвестиции (2010, 2040.070-2040.100, 2050.010, 2050.020, 2170.010, 2170.080, 1290.020, 1290.120, 1290.130)", 18));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочная торговая и прочая дебиторская задолженность (2110-2160, 2180, 2910)", 19));
        headers.add(new ConsolidatedBalanceFormRecordDto("Инвестиции, учитываемые методом долевого участия (2210)", 20));
        headers.add(new ConsolidatedBalanceFormRecordDto("Основные средства (2410-2430)", 21));
        headers.add(new ConsolidatedBalanceFormRecordDto("Нематериальные активы (2730-2750)", 22));
        headers.add(new ConsolidatedBalanceFormRecordDto("Отложенные налоговые активы (2810)", 23));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные активы (2310-2330, 2510-2520, 2610-2630, 2920-2940)", 24));

        ConsolidatedBalanceFormRecordDto record24 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_EXPENSES_FUTURE_PERIOD, 24);
        record24.setAccountNumber(PeriodicReportConstants.ACC_NUM_2923_010);
        headers.add(record24);

        headers.add(new ConsolidatedBalanceFormRecordDto("Итого долгосрочных активов (сумма строк 15-24)", 25));
        headers.add(new ConsolidatedBalanceFormRecordDto("Всего активы (сумма строк 13, 25)", 26));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные обязательства", 27));
        headers.add(new ConsolidatedBalanceFormRecordDto("Займы полученные (3010, 3020, 3380.010, 3380.020)", 28));

        ConsolidatedBalanceFormRecordDto record28_1 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_3013_010, 28);
        record28_1.setAccountNumber(PeriodicReportConstants.ACC_NUM_3013_010);
        headers.add(record28_1);

        ConsolidatedBalanceFormRecordDto record28_2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_3383_010, 28);
        record28_2.setAccountNumber(PeriodicReportConstants.ACC_NUM_3383_010);
        headers.add(record28_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные финансовые обязательства (3040, 3050, 3380.030-3380.050, 3390.010)", 29));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочная торговая и прочая кредиторская задолженность (3310-3340, 3360, 3390.020, 3510)", 30));

        ConsolidatedBalanceFormRecordDto record30 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_3393_020, 30);
        record30.setAccountNumber(PeriodicReportConstants.ACC_NUM_3393_020);
        headers.add(record30);


        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные резервы (3410-3440)", 31));
        headers.add(new ConsolidatedBalanceFormRecordDto("Текущие налоговые обязательства (3110-3190, 3210-3240)", 32));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные обязательства (3030, 3350, 3370, 3520, 3540)", 33));
        headers.add(new ConsolidatedBalanceFormRecordDto("Обязательства выбывающих групп, предназначенных для продажи (3530)", 34));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого краткосрочных обязательств (сумма строк 28-34)", 35));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные  обязательства", 36));
        headers.add(new ConsolidatedBalanceFormRecordDto("Займы полученные (4010, 4020, 4160.010, 4160.020)", 37));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные финансовые обязательства (4030, 4160.030-4160.040)", 38));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочная торговая и прочая кредиторская задолженность (4110-4150, 4170, 4410)", 39));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные резервы (4210-4240)", 40));
        headers.add(new ConsolidatedBalanceFormRecordDto("Отложенные налоговые обязательства (4310)", 41));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие долгосрочные обязательства (4420, 4430)", 42));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого долгосрочных обязательств (сумма строк 37-42)", 43));
        headers.add(new ConsolidatedBalanceFormRecordDto("Капитал", 44));
        headers.add(new ConsolidatedBalanceFormRecordDto("Уставный капитал (5010-5030)", 45));

        ConsolidatedBalanceFormRecordDto record45_1 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.COMMON_SHARES, 45);
        record45_1.setAccountNumber(PeriodicReportConstants.ACC_NUM_5021_010);
        headers.add(record45_1);

        ConsolidatedBalanceFormRecordDto record45_2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.COMMON_SHARES, 45);
        record45_2.setAccountNumber(PeriodicReportConstants.ACC_NUM_5022_010);
        headers.add(record45_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Эмиссионный доход (5310)", 46));
        headers.add(new ConsolidatedBalanceFormRecordDto("Выкупленные собственные долевые инструменты (5210)", 47));
        headers.add(new ConsolidatedBalanceFormRecordDto("Резервный капитал (5410, 5460)", 48));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие резервы (5420-5450)", 49));

        ConsolidatedBalanceFormRecordDto record49_1 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_5440_010_b, 49);
        record49_1.setAccountNumber(PeriodicReportConstants.ACC_NUM_5440_010);
        headers.add(record49_1);

        ConsolidatedBalanceFormRecordDto record49_2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_5450_010, 49);
        record49_2.setAccountNumber(PeriodicReportConstants.ACC_NUM_5450_010);
        headers.add(record49_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Нераспределенная прибыль (непокрытый убыток) (5510, 5520)", 50));


        ConsolidatedBalanceFormRecordDto record50_1 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_5510_010, 50);
        record50_1.setAccountNumber(PeriodicReportConstants.ACC_NUM_5510_010);
        headers.add(record50_1);

        ConsolidatedBalanceFormRecordDto record50_2 = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_5520_010, 50);
        record50_2.setAccountNumber(PeriodicReportConstants.ACC_NUM_5520_010);
        headers.add(record50_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Итого капитал (сумма строк 45-50)", 51));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.USD_FORM_1_LAST_RECORD, 52));
        return headers;
    }

    private List<ConsolidatedKZTForm19RecordDto> getConsolidatedBalanceKZTForm19LineHeaders(){
        List<ConsolidatedKZTForm19RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы в виде вознаграждения по размещенным вкладам (сумма строк 2-11)", 1));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по вкладам до востребования", 2));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по краткосрочным срочным вкладам", 3));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по долгосрочным срочным вкладам", 4));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по краткосрочным прочим вкладам", 5));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по долгосрочным прочим вкладам", 6));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по вкладам до востребования", 7));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по краткосрочным срочным вкладам", 8));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по долгосрочным срочным вкладам", 9));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по прочим краткосрочным вкладам", 10));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по прочим долгосрочным вкладам", 11));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы в виде вознаграждения по приобретенным ценным бумагам (сумма строк 13-21)", 12));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по краткосрочным финансовым инвестициям, оцениваемым по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка", 13));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по краткосрочным финансовым инвестициям, удерживаемым до погашения", 14));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по долгосрочным финансовым инвестициям, удерживаемым до погашения", 15));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по краткосрочным финансовым инвестициям, имеющимся в наличии для продажи", 16));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по вознаграждениям по долгосрочным финансовым инвестициям, имеющимся в наличии для продажи", 17));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по краткосрочным финансовым инвестициям, удерживаемым до погашения", 18));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по долгосрочным финансовым инвестициям, удерживаемым до погашения", 19));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по краткосрочным финансовым инвестициям, имеющимся в наличии для продажи", 20));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы по амортизации дисконта по долгосрочным финансовым инвестициям, имеющимся в наличии для продажи", 21));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы (расходы) от купли-продажи ценных бумаг (сумма строк 23-28)", 22));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от покупки-продажи финансовых инвестиций, оцениваемых по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка", 23));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от покупки-продажи краткосрочных финансовых инвестиций, имеющихся в наличии для продажи", 24));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от покупки-продажи долгосрочных финансовых инвестиций, имеющихся в наличии для продажи", 25));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от покупки-продажи финансовых инвестиций, оцениваемых по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка", 26));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от покупки-продажи краткосрочных финансовых инвестиций, имеющихся в наличии для продажи", 27));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от покупки-продажи долгосрочных финансовых инвестиций, имеющихся в наличии для продажи", 28));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, оцениваемых по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка (сумма строк 30, 31)", 29));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от изменения справедливой стоимости финансовых инвестиций, оцениваемых по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка", 30));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от изменения справедливой стоимости финансовых инвестиций, оцениваемых по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка", 31));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы (расходы) от изменения стоимости ценных бумаг, имеющихся в наличии для продажи (сумма строк 33-36)", 32));
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от изменения справедливой стоимости краткосрочных финансовых инвестиций, имеющихся в наличии для продажи", 33));
        records.add(new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.INCOME_FAIR_VALUE_CHANGES, 34));

        ConsolidatedKZTForm19RecordDto record6150_030HF = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT, 34);
        record6150_030HF.setAccountNumber(PeriodicReportConstants.ACC_NUM_6150_030);
        record6150_030HF.setOtherEntityName(PeriodicReportConstants.SINGULARITY_LOWER_CASE);
        records.add(record6150_030HF);

        ConsolidatedKZTForm19RecordDto record6150_030PE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_PE_FUND_INVESTMENT, 34);
        record6150_030PE.setAccountNumber(PeriodicReportConstants.ACC_NUM_6150_030);
        record6150_030PE.setOtherEntityName(PeriodicReportConstants.TARRAGON_CAPITAL_CASE);
        records.add(record6150_030PE);

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от изменения справедливой стоимости краткосрочных финансовых инвестиций, имеющихся в наличии для продажи", 35));
        records.add(new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.EXPENSE_FAIR_VALUE_CHANGES, 36));

        ConsolidatedKZTForm19RecordDto record7330_030HF = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_HEDGE_FUND_INVESTMENT, 36);
        record7330_030HF.setAccountNumber(PeriodicReportConstants.ACC_NUM_7330_030);
        record7330_030HF.setOtherEntityName(PeriodicReportConstants.SINGULARITY_LOWER_CASE);
        records.add(record7330_030HF);

        ConsolidatedKZTForm19RecordDto record7330_030PE = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_PE_FUND_INVESTMENT, 36);
        record7330_030PE.setAccountNumber(PeriodicReportConstants.ACC_NUM_7330_030);
        record7330_030PE.setOtherEntityName(PeriodicReportConstants.TARRAGON_CAPITAL_CASE);
        records.add(record7330_030PE);

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы в виде вознаграждения по приобретенным ценным бумагам (сумма строк 38-41) ", 37));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным краткосрочным финансовым инвестициям, удерживаемым до погашения", 38));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным долгосрочным финансовым инвестициям, удерживаемым до погашения", 39));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным краткосрочным финансовым инвестициям, имеющимся в наличии для продажи", 40));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным долгосрочным финансовым инвестициям, имеющимся в наличии для продажи", 41));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы в виде вознаграждения по полученным займам и финансовой аренде (сумма строк 43-53)", 42));
        records.add(new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.RU_7313_010, 43));

        ConsolidatedKZTForm19RecordDto record7313_010 = new ConsolidatedKZTForm19RecordDto(PeriodicReportConstants.BANK_LOANS_RECEIVED, 43);
        record7313_010.setAccountNumber(PeriodicReportConstants.ACC_NUM_7313_010);
        record7313_010.setOtherEntityName("Bank of Monreal");
        records.add(record7313_010);

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по вознаграждениям по долгосрочным банковским займам", 44));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по вознаграждениям по краткосрочным займам, полученным от организаций, осуществляющих банковские операции, без лицензии уполномоченного органа", 45));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по вознаграждениям по долгосрочным займам, полученным от организаций, осуществляющих банковские операции, без лицензии уполномоченного органа", 46));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по краткосрочным предоставленным займам", 47));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по долгосрочным предоставленным займам", 48));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации дисконта по краткосрочным банковским займам полученным", 49));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации дисконта по долгосрочным банковским займам полученным", 50));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации дисконта по краткосрочным займам, полученным от организаций, осуществляющих банковские операции, без лицензии уполномоченного органа", 51));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации дисконта по долгосрочным займам, полученным от организаций, осуществляющих банковские операции, без лицензии уполномоченного органа", 52));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы на выплату процентов по финансовой аренде", 53));
        records.add(new ConsolidatedKZTForm19RecordDto("ВСЕГО (сумма строк 1, 12, 22, 29, 32, 37 и 42)", 54));

        return records;
    }

    private List<ConsolidatedKZTForm22RecordDto> getConsolidatedBalanceKZTForm22LineHeaders(){
        List<ConsolidatedKZTForm22RecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedKZTForm22RecordDto("Прочие доходы", 1));
        headers.add(new ConsolidatedKZTForm22RecordDto("Прочие расходы", 2));
        headers.add(new ConsolidatedKZTForm22RecordDto("ВСЕГО (сумма строк 1, 2)", 3));

        return headers;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedTotalIncomeKZTForm3LineHeaders(){
        List<ConsolidatedBalanceFormRecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedBalanceFormRecordDto("Чистая прибыль (убыток)", 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочий совокупный доход", 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("Резерв по переоценке финансовых активов, имеющихся в наличии для продажи всего (сумма строк 3.1-3.3)", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("- чистое изменение справедливой стоимости", 3, 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("- чистое изменение справедливой стоимости, перенесенное в состав прибыли или убытка", 3, 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("- обесценение, перенесенное в состав прибыли или убытка ", 3, 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Переоценка основных средств всего (сумма строк 4.1-4.3)", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("- изменение стоимости от переоценки", 4, 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("- перенос переоценки основных средств при амортизации", 4, 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("- перенос переоценки основных средств при выбытии", 4, 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие операции (сумма строк 5.1-5.2)", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto("- резерв на пересчет иностранной валюты по зарубежной деятельности", 5, 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("- прочие", 5, 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочий совокупный доход (сумма строк 3, 4, 5)", 6));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.KZT_FORM_3_LAST_RECORD, 7));

        return headers;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedTotalIncomeUSDFormLineHeaders(){
        // TODO: get from DB
        List<ConsolidatedBalanceFormRecordDto> headers = new ArrayList<>();
        headers.add(new ConsolidatedBalanceFormRecordDto("Чистая прибыль (убыток)", 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочий совокупный доход", 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("Резерв по переоценке финансовых активов, имеющихся в наличии для продажи всего (сумма строк 3.1-3.3)", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("- чистое изменение справедливой стоимости", 3, 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("- чистое изменение справедливой стоимости, перенесенное в состав прибыли или убытка", 3, 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("- обесценение, перенесенное в состав прибыли или убытка ", 3, 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Переоценка основных средств всего (сумма строк 4.1-4.3)", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("- изменение стоимости от переоценки", 4, 1));
        headers.add(new ConsolidatedBalanceFormRecordDto("- перенос переоценки основных средств при амортизации", 4, 2));
        headers.add(new ConsolidatedBalanceFormRecordDto("- перенос переоценки основных средств при выбытии", 4, 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочий совокупный доход (сумма строк 3, 4)", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.USD_FORM_3_LAST_RECORD, 6));

        return headers;
    }

    private void setConsolidatedBalanceUSDFormHeaderSumsAndClearOtherEntityName(List<ConsolidatedBalanceFormRecordDto> records, Map<Integer, Double> sums){
        if(records != null && sums != null) {
            for (int i = 0; i < records.size(); i++) {
                ConsolidatedBalanceFormRecordDto record = records.get(i);
                if (record.getAccountNumber() == null) {
                    if (record.getLineNumber() == 1) {
                        record.setCurrentAccountBalance(sums.get(2) + sums.get(8) + sums.get(11));
                        sums.put(1, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 13) {
                        record.setCurrentAccountBalance(sums.get(2) + sums.get(3) + sums.get(4) + sums.get(5) + sums.get(6) +
                                sums.get(7) + sums.get(8) + sums.get(9) + sums.get(10) + sums.get(11) + sums.get(12));
                        sums.put(13, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 14) {
                        record.setCurrentAccountBalance(sums.get(16) + sums.get(24));
                        sums.put(14, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 25) {
                        record.setCurrentAccountBalance(sums.get(15) + sums.get(16) + sums.get(17) + sums.get(18) + sums.get(19) +
                                sums.get(20) + sums.get(21) + sums.get(22) + sums.get(23) + sums.get(24));
                        sums.put(25, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 26) {
                        record.setCurrentAccountBalance(sums.get(13) + sums.get(25));
                        sums.put(26, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 35) {
                        record.setCurrentAccountBalance(sums.get(28) + sums.get(29) + sums.get(30) + sums.get(31) + sums.get(32) +
                                sums.get(33) + sums.get(34));
                        sums.put(35, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 43) {
                        record.setCurrentAccountBalance(sums.get(37) + sums.get(38) + sums.get(39) + sums.get(40) + sums.get(41) + sums.get(42));
                        sums.put(43, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 51) {
                        record.setCurrentAccountBalance(sums.get(45) + sums.get(46) + sums.get(47) + sums.get(48) + sums.get(49) + sums.get(50));
                        sums.put(51, record.getCurrentAccountBalance());
                    } else if (record.getLineNumber() == 52) {
                        record.setCurrentAccountBalance(sums.get(35) + sums.get(43) + sums.get(51));
                        sums.put(52, record.getCurrentAccountBalance());
                    }

                    if(record.getCurrentAccountBalance() == 0.0){
                        record.setCurrentAccountBalance(null);
                    }
                }else{
                    if(record.getLineNumber() != 16 && record.getLineNumber() != 8){
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
                    if (record.getLineNumber() == 16) {
                        record.setCurrentAccountBalance(sums.get(3) + sums.get(4) + sums.get(5) + sums.get(6) + sums.get(7) +
                                sums.get(8) + sums.get(9) + sums.get(10) + sums.get(11) + sums.get(12) + sums.get(13) + sums.get(14) + sums.get(15));
                        sums.put(16, record.getCurrentAccountBalance());
                    }else if (record.getLineNumber() == 18) {
                        record.setCurrentAccountBalance(sums.get(16) + sums.get(17));
                        sums.put(18, record.getCurrentAccountBalance());
                    }else if (record.getLineNumber() == 20) {
                        record.setCurrentAccountBalance(sums.get(18) + sums.get(19));
                        sums.put(20, record.getCurrentAccountBalance());
                    }

                    if(record.getCurrentAccountBalance() == 0.0){
                        record.setCurrentAccountBalance(null);
                    }
                }else{
                    if(record.getLineNumber() != 8){
                        record.setOtherEntityName(null);
                    }
                }
            }
        }
    }

    private List<ConsolidatedBalanceFormRecordDto> setConsolidatedBalanceUSDFormAdditionalHeaders(List<ConsolidatedBalanceFormRecordDto> records){
        List<ConsolidatedBalanceFormRecordDto> newRecords = new ArrayList<>();
        boolean header1283020aAdded = false;
        ConsolidatedBalanceFormRecordDto header1283020aRecord = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_INVESTMENTS_TO_RETURN, 8);
        //header1283020aRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_1283_020);
        ConsolidatedBalanceFormRecordDto header1283020bRecord = new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_PRE_SUBSCRIPTION, 8);
        //header1283020bRecord.setAccountNumber(PeriodicReportConstants.ACC_NUM_1283_020);
        boolean header1283020bAdded = false;
        if(records != null) {
            for (int i = 0; i < records.size(); i++) {
                ConsolidatedBalanceFormRecordDto record = records.get(i);
                if (record.getLineNumber() == 8 && record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1283_020)) {
                    if (record.getName().startsWith(PeriodicReportConstants.RU_INVESTMENTS_TO_RETURN)) {
                        if (!header1283020aAdded) {
                            header1283020aRecord.setCurrentAccountBalance(record.getCurrentAccountBalance());
                            newRecords.add(header1283020aRecord);
                            header1283020aAdded = true;
                        } else {
                            header1283020aRecord.setCurrentAccountBalance(header1283020aRecord.getCurrentAccountBalance() + record.getCurrentAccountBalance());
                        }
                    }
                    if (record.getName().startsWith(PeriodicReportConstants.RU_PRE_SUBSCRIPTION)) {
                        if (!header1283020bAdded) {
                            header1283020bRecord.setCurrentAccountBalance(record.getCurrentAccountBalance());
                            newRecords.add(header1283020bRecord);
                            header1283020bAdded = true;
                        } else {
                            header1283020bRecord.setCurrentAccountBalance(header1283020bRecord.getCurrentAccountBalance() + record.getCurrentAccountBalance());
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
                if(record.getLineNumber() != null && record.getLineNumber() == 9){
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
                new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.INCOME_FAIR_VALUE_CHANGES, 8);
        ConsolidatedBalanceFormRecordDto header7330030Record =
                new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.EXPENSE_FAIR_VALUE_CHANGES, 8);
        ConsolidatedBalanceFormRecordDto header7313010Record =
                new ConsolidatedBalanceFormRecordDto(PeriodicReportConstants.RU_7313_010, 12);
        if(records != null) {
            for (int i = 0; i < records.size(); i++) {
                ConsolidatedBalanceFormRecordDto record = records.get(i);
                if (record.getLineNumber() == 8 && record.getAccountNumber() != null /* && record.getName().startsWith("Реализованные доходы/расходы по инвестициям")*/) {
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
                }else if (record.getLineNumber() == 12 && record.getAccountNumber() != null /* && record.getName().startsWith("Реализованные доходы/расходы по инвестициям")*/) {
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
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1283_020)){
            return 8;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1623_010)) {
            return 11;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2033_010)){
            return 16;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_2923_010)){
            return 24;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3013_010) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3383_010)){
            return 28;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3393_020)){
            return 30;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5021_010) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5022_010)){
            return 45;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5440_010)){
            return 49;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5510_010) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_5520_010)){
            return 50;
        }

        return 0;
    }

    private int getConsolidatedIncomeExpenseUSDFormLineNumberByAccountNumber(String accountNumber) {

        // TODO: load from DB

        if(accountNumber == null){
            return 0;
        }else if (accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_020) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030) ||
                accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_020) || accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030)) {
            return 8;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6283_080)){
            return 10;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7313_010)){
            return 12;
        }else if(accountNumber.equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7473_080)){
            return 15;
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
            throw new IllegalStateException(errorMessage);
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
            throw new IllegalStateException(errorMessage);
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
            throw new IllegalStateException(KZTForm3ResponseDto.getMessage().getNameEn());
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
            throw new IllegalStateException(errorMessage);
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
            throw new IllegalStateException(errorMessage);
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
                    "Error occurred when generating KZT Form 7 report";
            throw new IllegalStateException(errorMessage);
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
            throw new IllegalStateException(errorMessage);
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
            throw new IllegalStateException(errorMessage);
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
            throw new IllegalStateException(errorMessage);
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
            throw new IllegalStateException(errorMessage);
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
            throw new IllegalStateException(errorMessage);
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
    public InputStream getExportFileStream(Long reportId, String type) {
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
        }

        return null;
    }

    private InputStream getConsolidatedBalanceUSDReportInputStream(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = getConsolidatedBalanceUSDFormMap(reportId);

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_USD_1.xlsx");
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
            Iterator<Row> rowIterator = sheet.iterator();
            int rows = sheet.getLastRowNum();
            //int rowNum = 0;
            boolean startOfTable = false;
            boolean endOfTable = false;
            int addedRecords = 0;
            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if (startOfTable) {
                    Cell cell = row.getCell(1);
                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.USD_FORM_1_LAST_RECORD)) {
                        endOfTable = true;
                    }
                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING && row.getCell(2).getStringCellValue() != null) {
                        int lineNumber = Integer.parseInt(row.getCell(2).getStringCellValue());
                        List<ConsolidatedBalanceFormRecordDto> records = recordsMap.get(lineNumber);
                        int recordsNum = records != null ? records.size() : 0;
                        if (recordsNum > 0 && row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING &&
                                records.get(0).getName().equalsIgnoreCase(row.getCell(1).getStringCellValue())) {
                            if(records.get(0).getCurrentAccountBalance() != null) {
                                row.getCell(4).setCellValue(records.get(0).getCurrentAccountBalance());
                            }
                            if(records.get(0).getPreviousAccountBalance() != null) {
                                row.getCell(5).setCellValue(records.get(0).getPreviousAccountBalance());
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
                                newRow.createCell(2).setCellType(Cell.CELL_TYPE_STRING);
                                if (records.get(i).getOtherEntityName() != null) {
                                    newRow.createCell(3).setCellValue(records.get(i).getOtherEntityName());
                                } else {
                                    newRow.createCell(3).setCellType(Cell.CELL_TYPE_STRING);
                                }
                                if (records.get(i).getCurrentAccountBalance() != null) {
                                    newRow.createCell(4).setCellValue(records.get(i).getCurrentAccountBalance());
                                } else {
                                    newRow.createCell(4).setCellType(Cell.CELL_TYPE_NUMERIC);
                                }
                                if (records.get(i).getPreviousAccountBalance() != null) {
                                    newRow.createCell(5).setCellValue(records.get(i).getPreviousAccountBalance());
                                } else {
                                    newRow.createCell(5).setCellType(Cell.CELL_TYPE_NUMERIC);
                                }

                                // set styles
                                newRow.getCell(0).setCellStyle(row.getCell(0).getCellStyle());
                                newRow.getCell(1).setCellStyle(row.getCell(1).getCellStyle());
                                newRow.getCell(2).setCellStyle(row.getCell(2).getCellStyle());
                                newRow.getCell(3).setCellStyle(row.getCell(3).getCellStyle());
                                if (row.getCell(4) != null && row.getCell(4).getCellStyle() != null) {
                                    newRow.getCell(4).setCellStyle(row.getCell(4).getCellStyle());
                                }
                                newRow.getCell(5).setCellStyle(row.getCell(5).getCellStyle());
                                index++;
                                addedRecords++;
                            }
                        }
                    }

                    //rowNum++;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.SUBACCOUNT_GROUP_NAME) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
                    startOfTable = true;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
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
            String filePath = this.rootDirectory + "/tmp/CONS_BLNC_USD_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        }  catch (IOException e) {
            logger.error("IO Exception when exporting USD_FORM_1", e);
        }

        return null;
    }

    private InputStream getConsolidatedIncomeExpenseUSDReportInputStream(Long reportId){

        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = getConsolidatedIncomeExpenseUSDFormMap(reportId);

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_USD_2.xlsx");
        InputStream ExcelFileToRead = null;
        try {
            ExcelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_USD_2.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(ExcelFileToRead);
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
                    Cell cell = row.getCell(1);
                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.USD_FORM_2_LAST_RECORD)) {
                        endOfTable = true;
                    }
                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC){
                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
                        List<ConsolidatedBalanceFormRecordDto> records = recordsMap.get(lineNumber);
                        int recordsNum = records != null ? records.size() : 0;
                        if (recordsNum > 0 && row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING &&
                                records.get(0).getName().equalsIgnoreCase(row.getCell(1).getStringCellValue())) {
                            if(records.get(0).getCurrentAccountBalance() != null) {
                                row.getCell(4).setCellValue(records.get(0).getCurrentAccountBalance());
                            }
                            if(records.get(0).getPreviousAccountBalance() != null) {
                                row.getCell(5).setCellValue(records.get(0).getPreviousAccountBalance());
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
                                newRow.createCell(2).setCellType(Cell.CELL_TYPE_STRING);
                                if (records.get(i).getOtherEntityName() != null) {
                                    newRow.createCell(3).setCellValue(records.get(i).getOtherEntityName());
                                } else {
                                    newRow.createCell(3).setCellType(Cell.CELL_TYPE_STRING);
                                }
                                if (records.get(i).getCurrentAccountBalance() != null) {
                                    newRow.createCell(4).setCellValue(records.get(i).getCurrentAccountBalance());
                                } else {
                                    newRow.createCell(4).setCellType(Cell.CELL_TYPE_NUMERIC);
                                }
                                if (records.get(i).getPreviousAccountBalance() != null) {
                                    newRow.createCell(5).setCellValue(records.get(i).getPreviousAccountBalance());
                                } else {
                                    newRow.createCell(5).setCellType(Cell.CELL_TYPE_NUMERIC);
                                }

                                // set styles
                                newRow.getCell(0).setCellStyle(row.getCell(0).getCellStyle());
                                newRow.getCell(1).setCellStyle(row.getCell(1).getCellStyle());
                                newRow.getCell(2).setCellStyle(row.getCell(2).getCellStyle());
                                newRow.getCell(3).setCellStyle(row.getCell(3).getCellStyle());
                                if (row.getCell(4) != null && row.getCell(4).getCellStyle() != null) {
                                    newRow.getCell(4).setCellStyle(row.getCell(4).getCellStyle());
                                }
                                newRow.getCell(5).setCellStyle(row.getCell(5).getCellStyle());
                                index++;
                                addedRecords++;
                            }
                        }
                    }

                    //rowNum++;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.SUBACCOUNT_GROUP_NAME) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.LINE_CODE)){
                    startOfTable = true;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
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
            String filePath = this.rootDirectory + "/tmp/INCOME_EXP_USD_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        } catch (IOException e) {
            logger.error("IO Exception when exporting USD_FORM_2", e);
        }

        return null;
    }

    private InputStream getConsolidatedTotalIncomeUSDReportInputStream(Long reportId){
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        List<ConsolidatedBalanceFormRecordDto> records = generateConsolidatedTotalIncomeUSDForm(reportId);

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_USD_3.xlsx");
        InputStream ExcelFileToRead = null;
        try {
            ExcelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_USD_3.xlsx'");
            return null;
            //e.printStackTrace();
        }

        try {
            XSSFWorkbook  workbook = new XSSFWorkbook(ExcelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            boolean startOfTable = false;
            boolean endOfTable = false;
            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if (startOfTable) {
                    Cell cell = row.getCell(1);
                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.USD_FORM_3_LAST_RECORD)) {
                        endOfTable = true;
                    }
                    if (row.getCell(0) != null) {
                        for(ConsolidatedBalanceFormRecordDto record: records){
                            if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), record.getName()) && record.getCurrentAccountBalance() != null){
                                row.getCell(2).setCellValue(record.getCurrentAccountBalance());
                            }
                            if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), record.getName()) && record.getPreviousAccountBalance() != null){
                                row.getCell(3).setCellValue(record.getPreviousAccountBalance());
                            }
                        }
                    }

                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.INDICATORS_NAME) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.LINE_CODE) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.ON_CURRENT_PERIOD_DATE)){
                    startOfTable = true;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
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
            String filePath = this.rootDirectory + "/tmp/CONS_TOTAL_INCOME_USD_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        } catch (IOException e) {
            logger.error("IO Exception when exporting USD_FORM_3", e);
        }

        return null;
    }

    private InputStream getConsolidatedForm1KZTReportInputStream(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = null;
        try{
            recordsMap = getConsolidatedBalanceKZTForm1Map(reportId);
        }catch (IllegalStateException ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_1.xlsx");
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
            Iterator<Row> rowIterator = sheet.iterator();
            int rows = sheet.getLastRowNum();
            //int rowNum = 0;
            boolean startOfTable = false;
            boolean endOfTable = false;
            int addedRecords = 0;
            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if (startOfTable) {
                    Cell cell = row.getCell(1);
                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.USD_FORM_1_LAST_RECORD)) {
                        endOfTable = true;
                    }
                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_STRING && row.getCell(2).getStringCellValue() != null) {
                        int lineNumber = Integer.parseInt(row.getCell(2).getStringCellValue());
                        List<ConsolidatedBalanceFormRecordDto> records = recordsMap.get(lineNumber);
                        int recordsNum = records != null ? records.size() : 0;
                        if (recordsNum > 0 && row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING &&
                                records.get(0).getName().equalsIgnoreCase(row.getCell(1).getStringCellValue())) {
                            if(records.get(0).getCurrentAccountBalance() != null) {
                                row.getCell(4).setCellValue(records.get(0).getCurrentAccountBalance());
                            }
                            if(records.get(0).getPreviousAccountBalance() != null) {
                                row.getCell(5).setCellValue(records.get(0).getPreviousAccountBalance());
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
                                newRow.createCell(2).setCellType(Cell.CELL_TYPE_STRING);
                                if (records.get(i).getOtherEntityName() != null) {
                                    newRow.createCell(3).setCellValue(records.get(i).getOtherEntityName());
                                } else {
                                    newRow.createCell(3).setCellType(Cell.CELL_TYPE_STRING);
                                }
                                if (records.get(i).getCurrentAccountBalance() != null) {
                                    newRow.createCell(4).setCellValue(records.get(i).getCurrentAccountBalance());
                                } else {
                                    newRow.createCell(4).setCellType(Cell.CELL_TYPE_NUMERIC);
                                }
                                if (records.get(i).getPreviousAccountBalance() != null) {
                                    newRow.createCell(5).setCellValue(records.get(i).getPreviousAccountBalance());
                                } else {
                                    newRow.createCell(5).setCellType(Cell.CELL_TYPE_NUMERIC);
                                }

                                // set styles
                                newRow.getCell(0).setCellStyle(row.getCell(0).getCellStyle());
                                newRow.getCell(1).setCellStyle(row.getCell(1).getCellStyle());
                                newRow.getCell(2).setCellStyle(row.getCell(2).getCellStyle());
                                newRow.getCell(3).setCellStyle(row.getCell(3).getCellStyle());
                                if (row.getCell(4) != null && row.getCell(4).getCellStyle() != null) {
                                    newRow.getCell(4).setCellStyle(row.getCell(4).getCellStyle());
                                }
                                newRow.getCell(5).setCellStyle(row.getCell(5).getCellStyle());
                                index++;
                                addedRecords++;
                            }
                        }
                    }

                    //rowNum++;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.SUBACCOUNT_GROUP_NAME) &&
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
            String filePath = this.rootDirectory + "/tmp/CONS_KZT_FORM_1_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        } catch (IOException ex) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_1", ex);
        }

        return null;
    }

    private InputStream getConsolidatedForm2KZTReportInputStream(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = null;
        try{
            recordsMap = getConsolidatedBalanceKZTForm2Map(reportId);
        }catch (IllegalStateException ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_2.xlsx");
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
            Iterator<Row> rowIterator = sheet.iterator();
            int rows = sheet.getLastRowNum();
            //int rowNum = 0;
            boolean startOfTable = false;
            boolean endOfTable = false;
            int addedRecords = 0;
            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if (startOfTable) {
                    Cell cell = row.getCell(1);
                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.USD_FORM_2_LAST_RECORD)) {
                        endOfTable = true;
                    }
                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC && row.getCell(2).getNumericCellValue() > 0) {
                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
                        List<ConsolidatedBalanceFormRecordDto> records = recordsMap.get(lineNumber);
                        int recordsNum = records != null ? records.size() : 0;
                        if (recordsNum > 0 && row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING &&
                                records.get(0).getName().equalsIgnoreCase(row.getCell(1).getStringCellValue())) {
                            if(records.get(0).getCurrentAccountBalance() != null) {
                                row.getCell(4).setCellValue(records.get(0).getCurrentAccountBalance());
                            }
                            if(records.get(0).getPreviousAccountBalance() != null) {
                                row.getCell(5).setCellValue(records.get(0).getPreviousAccountBalance());
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
                                newRow.createCell(2).setCellType(Cell.CELL_TYPE_STRING);
                                if (records.get(i).getOtherEntityName() != null) {
                                    newRow.createCell(3).setCellValue(records.get(i).getOtherEntityName());
                                } else {
                                    newRow.createCell(3).setCellType(Cell.CELL_TYPE_STRING);
                                }
                                if (records.get(i).getCurrentAccountBalance() != null) {
                                    newRow.createCell(4).setCellValue(records.get(i).getCurrentAccountBalance());
                                } else {
                                    newRow.createCell(4).setCellType(Cell.CELL_TYPE_NUMERIC);
                                }
                                if (records.get(i).getPreviousAccountBalance() != null) {
                                    newRow.createCell(5).setCellValue(records.get(i).getPreviousAccountBalance());
                                } else {
                                    newRow.createCell(5).setCellType(Cell.CELL_TYPE_NUMERIC);
                                }

                                // set styles
                                newRow.getCell(0).setCellStyle(row.getCell(0).getCellStyle());
                                newRow.getCell(1).setCellStyle(row.getCell(1).getCellStyle());
                                newRow.getCell(2).setCellStyle(row.getCell(2).getCellStyle());
                                newRow.getCell(3).setCellStyle(row.getCell(3).getCellStyle());
                                if (row.getCell(4) != null && row.getCell(4).getCellStyle() != null) {
                                    newRow.getCell(4).setCellStyle(row.getCell(4).getCellStyle());
                                }
                                newRow.getCell(5).setCellStyle(row.getCell(5).getCellStyle());
                                index++;
                                addedRecords++;
                            }
                        }
                    }

                    //rowNum++;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.SUBACCOUNT_GROUP_NUMBER) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.SUBACCOUNT_GROUP_NAME) &&
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
            String filePath = this.rootDirectory + "/tmp/CONS_KZT_FORM_2_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        }catch (IOException ex) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_2", ex);
        }

        return null;
    }

    private InputStream getConsolidatedForm3KZTReportInputStream(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = null;
        try{
            recordsMap = getConsolidatedBalanceKZTForm3Map(reportId);
        }catch (IllegalStateException ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_3.xlsx");
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
            Iterator<Row> rowIterator = sheet.iterator();

            boolean startOfTable = false;
            boolean endOfTable = false;
            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if (startOfTable) {
                    Cell cell = row.getCell(0);
                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, PeriodicReportConstants.KZT_FORM_3_LAST_RECORD)) {
                        endOfTable = true;
                    }
                    int lineNumber = 0;
                    int sublineNumber = 0;
                    if(row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC){
                        lineNumber = (int) row.getCell(1).getNumericCellValue();
                    } else if (row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING && row.getCell(1).getStringCellValue() != null) {
                        String lineNumberText = row.getCell(1).getStringCellValue();

                        if (lineNumberText.contains(".")) {
                            lineNumber = Integer.parseInt(lineNumberText.split("\\.")[0]);
                            sublineNumber = Integer.parseInt(lineNumberText.split("\\.")[1]);
                        }else{
                            lineNumber = Integer.parseInt(lineNumberText);
                        }
                    }

                    if(lineNumber > 0){
                        List<ConsolidatedBalanceFormRecordDto> records = recordsMap.get(lineNumber);
                        for(ConsolidatedBalanceFormRecordDto record: records){
                            if(row.getCell(0) != null && row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING &&
                                    record.getName().equalsIgnoreCase(row.getCell(0).getStringCellValue())){
                                if(record.getSubLineNumber() == null || (sublineNumber > 0 && record.getSubLineNumber() == sublineNumber)){
                                        if(record.getCurrentAccountBalance() != null) {
                                            row.getCell(2).setCellValue(record.getCurrentAccountBalance());
                                        }
                                        if(record.getPreviousAccountBalance() != null) {
                                            row.getCell(3).setCellValue(record.getPreviousAccountBalance());
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    //rowNum++;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.INDICATORS_NAME) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.LINE_CODE)){
                    startOfTable = true;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
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
            String filePath = this.rootDirectory + "/tmp/CONS_KZT_FORM_3_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        }catch (IOException e) {

            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_3", e);
        }

        return null;
    }

    private InputStream getConsolidatedForm6KZTReportInputStream(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        Map<Integer, List<ConsolidatedKZTForm6RecordDto>> recordsMap = null;
        try{
            recordsMap = getConsolidatedBalanceKZTForm6Map(reportId);
        }catch (IllegalStateException ex){
            throw ex;
        }

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_6.xlsx");
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
            Iterator<Row> rowIterator = sheet.iterator();

            boolean startOfTable = false;
            boolean endOfTable = false;
            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if (startOfTable) {
                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_6_LAST_RECORD)) {
                        endOfTable = true;
                    }
                    int lineNumber = 0;
                    if(row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC){
                        lineNumber = (int) row.getCell(1).getNumericCellValue();
                    } else if (row.getCell(1) != null && row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING && row.getCell(1).getStringCellValue() != null) {
                        String lineNumberText = row.getCell(1).getStringCellValue();
                        lineNumber = Integer.parseInt(lineNumberText);
                    }

                    if(lineNumber > 0){
                        List<ConsolidatedKZTForm6RecordDto> records = recordsMap.get(lineNumber);
                        for(ConsolidatedKZTForm6RecordDto record: records){
                            if(row.getCell(0) != null && row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING &&
                                    record.getName().equalsIgnoreCase(row.getCell(0).getStringCellValue())){
                                if(record.getLineNumber() != null && record.getLineNumber() == lineNumber){
                                    if(record.getShareholderEquity() != null) {
                                        row.getCell(2).setCellValue(record.getShareholderEquity());
                                    }
                                    if(record.getAdditionalPaidinCapital() != null) {
                                        row.getCell(3).setCellValue(record.getAdditionalPaidinCapital());
                                    }
                                    if(record.getRedeemedOwnEquityInstruments() != null) {
                                        row.getCell(4).setCellValue(record.getRedeemedOwnEquityInstruments());
                                    }
                                    if(record.getReserveCapital() != null) {
                                        row.getCell(5).setCellValue(record.getReserveCapital());
                                    }
                                    if(record.getOtherReserves() != null) {
                                        row.getCell(6).setCellValue(record.getOtherReserves());
                                    }
                                    if(record.getRetainedEarnings() != null) {
                                        row.getCell(7).setCellValue(record.getRetainedEarnings());
                                    }
                                    if(record.getTotal() != null) {
                                        row.getCell(8).setCellValue(record.getTotal());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    //rowNum++;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.INDICATORS_NAME) &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.LINE_CODE)){
                    startOfTable = true;
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER)){
                    String date = DateUtils.getDateRussianTextualDateOnFirstDayNextMonth(report.getReportDate());
                    row.getCell(0).setCellValue(PeriodicReportConstants.KZT_REPORT_HEADER_DATE_TEXT + date);
                }else if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), PeriodicReportConstants.KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY)){
                    Date date = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
                    row.getCell(2).setCellValue(DateUtils.getDateFormatted(date));
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
            String filePath = this.rootDirectory + "/tmp/CONS_KZT_FORM_6_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        }catch (IOException e) {

            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_6", e);
        }

        return null;
    }

    private InputStream getConsolidatedForm7KZTReportInputStream(Long reportId) {
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
                            if(records.get(0).getDebtStartPeriod() != null) {
                                row.getCell(10).setCellValue(records.get(0).getDebtStartPeriod());
                            }
                            if(records.get(0).getFairValueAdjustmentsStartPeriod() != null) {
                                row.getCell(15).setCellValue(records.get(0).getFairValueAdjustmentsStartPeriod());
                            }
                            if(records.get(0).getTotalStartPeriod() != null) {
                                row.getCell(17).setCellValue(records.get(0).getTotalStartPeriod());
                            }
                            if(records.get(0).getDebtTurnover() != null) {
                                row.getCell(19).setCellValue(records.get(0).getDebtTurnover());
                            }
                            if(records.get(0).getFairValueAdjustmentsTurnoverPositive() != null) {
                                row.getCell(24).setCellValue(records.get(0).getFairValueAdjustmentsTurnoverPositive());
                            }
                            if(records.get(0).getFairValueAdjustmentsTurnoverNegative() != null) {
                                row.getCell(25).setCellValue(records.get(0).getFairValueAdjustmentsTurnoverNegative());
                            }
                            if(records.get(0).getDebtEndPeriod() != null) {
                                row.getCell(28).setCellValue(records.get(0).getDebtEndPeriod());
                            }
                            if(records.get(0).getFairValueAdjustmentsEndPeriod() != null) {
                                row.getCell(33).setCellValue(records.get(0).getFairValueAdjustmentsEndPeriod());
                            }
                            if(records.get(0).getTotalEndPeriod() != null) {
                                row.getCell(35).setCellValue(records.get(0).getTotalEndPeriod());
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
                                if(records.get(i).getDebtStartPeriod() != null) {
                                    newRow.createCell(10).setCellValue(records.get(i).getDebtStartPeriod());
                                }
                                if(records.get(i).getFairValueAdjustmentsStartPeriod() != null) {
                                    newRow.createCell(15).setCellValue(records.get(i).getFairValueAdjustmentsStartPeriod());
                                }
                                if(records.get(i).getTotalStartPeriod() != null) {
                                    newRow.createCell(17).setCellValue(records.get(i).getTotalStartPeriod());
                                }
                                if(records.get(i).getDebtTurnover() != null) {
                                    newRow.createCell(19).setCellValue(records.get(i).getDebtTurnover());
                                }
                                if(records.get(i).getFairValueAdjustmentsTurnoverPositive() != null) {
                                    newRow.createCell(24).setCellValue(records.get(i).getFairValueAdjustmentsTurnoverPositive());
                                }
                                if(records.get(i).getFairValueAdjustmentsTurnoverNegative() != null) {
                                    newRow.createCell(25).setCellValue(records.get(i).getFairValueAdjustmentsTurnoverNegative());
                                }
                                if(records.get(i).getDebtEndPeriod() != null) {
                                    newRow.createCell(28).setCellValue(records.get(i).getDebtEndPeriod());
                                }
                                if(records.get(i).getFairValueAdjustmentsEndPeriod() != null) {
                                    newRow.createCell(33).setCellValue(records.get(i).getFairValueAdjustmentsEndPeriod());
                                }
                                if(records.get(i).getTotalEndPeriod() != null) {
                                    newRow.createCell(35).setCellValue(records.get(i).getTotalEndPeriod());
                                }

                                // set styles
                                for(int j = 0; j < 36; j++){
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
            File tmpDir = new File(this.rootDirectory + "/tmp");

            String[]entries = tmpDir.list();
            if(entries != null) {
                for (String s : entries) {
                    File currentFile = new File(tmpDir.getPath(), s);
                    currentFile.delete();
                }
            }

//            start = System.currentTimeMillis();
            // write to new
            String filePath = this.rootDirectory + "/tmp/CONS_KZT_FORM_7_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_7", e);
        }

        return null;
    }

    private InputStream getConsolidatedForm8KZTReportInputStream(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        long start = System.currentTimeMillis();
        Map<Integer, List<ConsolidatedKZTForm8RecordDto>> recordsMap = null;
        try{
            recordsMap = getConsolidatedBalanceKZTForm8Map(reportId);
        }catch (IllegalStateException ex){
            throw ex;
        }

//        long end = System.currentTimeMillis();
//        System.out.println((end-start) / 1000 + " seconds - generating report data");

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_8.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_8.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
//            start = System.currentTimeMillis();
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");

            start = System.currentTimeMillis();

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
                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_8_LAST_RECORD)) {
                        endOfTable = true;
                    }
                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
                        List<ConsolidatedKZTForm8RecordDto> records = recordsMap.get(lineNumber);
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
                            if(records.get(0).getStartPeriodBalance() != null) {
                                row.getCell(16).setCellValue(records.get(0).getStartPeriodBalance());
                            }
                            if(records.get(0).getEndPeriodBalance() != null) {
                                row.getCell(17).setCellValue(records.get(0).getEndPeriodBalance());
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
                                if(records.get(i).getStartPeriodBalance() != null) {
                                    newRow.createCell(16).setCellValue(records.get(i).getStartPeriodBalance());
                                }
                                if(records.get(i).getEndPeriodBalance() != null) {
                                    newRow.createCell(17).setCellValue(records.get(i).getEndPeriodBalance());
                                }

                                // set styles
                                for(int j = 0; j < 18; j++){
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
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.DEBTOR_NAME) &&
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
            File tmpDir = new File(this.rootDirectory + "/tmp");

            String[]entries = tmpDir.list();
            if(entries != null) {
                for (String s : entries) {
                    File currentFile = new File(tmpDir.getPath(), s);
                    currentFile.delete();
                }
            }

            start = System.currentTimeMillis();
            // write to new
            String filePath = this.rootDirectory + "/tmp/CONS_KZT_FORM_7_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_8", e);
        }

        return null;
    }

    private InputStream getConsolidatedForm10KZTReportInputStream(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        long start = System.currentTimeMillis();
        Map<Integer, List<ConsolidatedKZTForm10RecordDto>> recordsMap = null;
        try{
            recordsMap = getConsolidatedBalanceKZTForm10Map(reportId);
        }catch (IllegalStateException ex){
            throw ex;
        }
//        long end = System.currentTimeMillis();
//        System.out.println((end-start) / 1000 + " seconds - generating report data");

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_10.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_10.xlsx'");
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
                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_10_LAST_RECORD)) {
                        endOfTable = true;
                    }

                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
                        List<ConsolidatedKZTForm10RecordDto> records = recordsMap.get(lineNumber);
                        int recordsNum = records != null ? records.size() : 0;
                        Cell nameCell = endOfTable ? row.getCell(0) : row.getCell(1);
                        if (recordsNum > 0 && nameCell != null && nameCell.getCellType() == Cell.CELL_TYPE_STRING &&
                                records.get(0).getName().equalsIgnoreCase(nameCell.getStringCellValue())) {
                            // Set cell values
                            if(records.get(0).getStartPeriodAssets() != null) {
                                row.getCell(4).setCellValue(records.get(0).getStartPeriodAssets());
                            }
                            if(records.get(0).getTurnoverOther() != null) {
                                row.getCell(14).setCellValue(records.get(0).getTurnoverOther());
                            }
                            if(records.get(0).getEndPeriodAssets() != null) {
                                row.getCell(16).setCellValue(records.get(0).getEndPeriodAssets());
                            }
                            if(records.get(0).getStartPeriodBalance() != null) {
                                row.getCell(24).setCellValue(records.get(0).getStartPeriodBalance());
                            }
                            if(records.get(0).getEndPeriodBalance() != null) {
                                row.getCell(25).setCellValue(records.get(0).getEndPeriodBalance());
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

                                if(records.get(0).getStartPeriodAssets() != null) {
                                    newRow.createCell(4).setCellValue(records.get(i).getStartPeriodAssets());
                                }
                                if(records.get(0).getTurnoverOther() != null) {
                                    newRow.createCell(14).setCellValue(records.get(i).getTurnoverOther());
                                }
                                if(records.get(0).getEndPeriodAssets() != null) {
                                    newRow.createCell(16).setCellValue(records.get(i).getEndPeriodAssets());
                                }
                                if(records.get(0).getStartPeriodBalance() != null) {
                                    newRow.createCell(24).setCellValue(records.get(i).getStartPeriodBalance());
                                }
                                if(records.get(0).getEndPeriodBalance() != null) {
                                    newRow.createCell(25).setCellValue(records.get(i).getEndPeriodBalance());
                                }

                                // set styles
                                for(int j = 0; j < 26; j++){
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
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.ASSETS_DEBTOR_NAME) &&
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
            File tmpDir = new File(this.rootDirectory + "/tmp");

            String[]entries = tmpDir.list();
            if(entries != null) {
                for (String s : entries) {
                    File currentFile = new File(tmpDir.getPath(), s);
                    currentFile.delete();
                }
            }

            start = System.currentTimeMillis();
            // write to new
            String filePath = this.rootDirectory + "/tmp/CONS_KZT_FORM_10_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_10", e);
        }

        return null;
    }

    private InputStream getConsolidatedForm13KZTReportInputStream(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        long start = System.currentTimeMillis();
        Map<Integer, List<ConsolidatedKZTForm13RecordDto>> recordsMap = null;
        try{
            recordsMap = getConsolidatedBalanceKZTForm13Map(reportId);
        }catch (IllegalStateException ex){
            throw ex;
        }
//        long end = System.currentTimeMillis();
//        System.out.println((end-start) / 1000 + " seconds - generating report data");

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_13.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_13.xlsx'");
            return null;
            //e.printStackTrace();
        }
        try {
//            start = System.currentTimeMillis();
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");

            start = System.currentTimeMillis();

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
                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_13_LAST_RECORD)) {
                        endOfTable = true;
                    }

                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
                        List<ConsolidatedKZTForm13RecordDto> records = recordsMap.get(lineNumber);
                        int recordsNum = records != null ? records.size() : 0;
                        Cell nameCell = endOfTable ? row.getCell(0) : row.getCell(1);
                        if (recordsNum > 0 && nameCell != null && nameCell.getCellType() == Cell.CELL_TYPE_STRING &&
                                records.get(0).getName().equalsIgnoreCase(nameCell.getStringCellValue())) {
                            // Set cell values

                            if(records.get(0).getEntityName() != null) {
                                row.getCell(3).setCellValue(records.get(0).getEntityName());
                            }
                            if(records.get(0).getStartPeriod() != null) {
                                row.getCell(4).setCellValue(DateUtils.getDateFormatted(records.get(0).getStartPeriod()));
                            }
                            if(records.get(0).getEndPeriod() != null) {
                                row.getCell(5).setCellValue(DateUtils.getDateFormatted(records.get(0).getEndPeriod()));
                            }
                            if(records.get(0).getInterestRate() != null) {
                                row.getCell(6).setCellValue(records.get(0).getInterestRate());
                            }
                            if(records.get(0).getInterestPaymentCount() != null) {
                                row.getCell(7).setCellValue(records.get(0).getInterestPaymentCount());
                            }
                            if(records.get(0).getDebtStartPeriod() != null) {
                                row.getCell(9).setCellValue(records.get(0).getDebtStartPeriod());
                            }
                            if(records.get(0).getInterestStartPeriod() != null) {
                                row.getCell(12).setCellValue(records.get(0).getInterestStartPeriod());
                            }
                            if(records.get(0).getTotalStartPeriod() != null) {
                                row.getCell(15).setCellValue(records.get(0).getTotalStartPeriod());
                            }
                            if(records.get(0).getDebtTurnover() != null) {
                                row.getCell(17).setCellValue(records.get(0).getDebtTurnover());
                            }
                            if(records.get(0).getInterestTurnover() != null) {
                                row.getCell(20).setCellValue(records.get(0).getInterestTurnover());
                            }
                            if(records.get(0).getDebtEndPeriod() != null) {
                                row.getCell(24).setCellValue(records.get(0).getDebtEndPeriod());
                            }
                            if(records.get(0).getInterestEndPeriod() != null) {
                                row.getCell(27).setCellValue(records.get(0).getInterestEndPeriod());
                            }
                            if(records.get(0).getTotalEndPeriod() != null) {
                                row.getCell(30).setCellValue(records.get(0).getTotalEndPeriod());
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
                                if(records.get(i).getStartPeriod() != null) {
                                    newRow.createCell(4).setCellValue(DateUtils.getDateFormatted(records.get(i).getStartPeriod()));
                                }
                                if(records.get(i).getEndPeriod() != null) {
                                    newRow.createCell(5).setCellValue(DateUtils.getDateFormatted(records.get(i).getEndPeriod()));
                                }
                                if(records.get(i).getInterestRate() != null) {
                                    newRow.createCell(6).setCellValue(records.get(i).getInterestRate());
                                }
                                if(records.get(i).getInterestPaymentCount() != null) {
                                    newRow.createCell(7).setCellValue(records.get(i).getInterestPaymentCount());
                                }
                                if(records.get(i).getDebtStartPeriod() != null) {
                                    newRow.createCell(9).setCellValue(records.get(i).getDebtStartPeriod());
                                }
                                if(records.get(i).getInterestStartPeriod() != null) {
                                    newRow.createCell(12).setCellValue(records.get(i).getInterestStartPeriod());
                                }
                                if(records.get(i).getTotalStartPeriod() != null) {
                                    newRow.createCell(15).setCellValue(records.get(i).getTotalStartPeriod());
                                }
                                if(records.get(i).getDebtTurnover() != null) {
                                    newRow.createCell(17).setCellValue(records.get(i).getDebtTurnover());
                                }
                                if(records.get(i).getInterestTurnover() != null) {
                                    newRow.createCell(20).setCellValue(records.get(i).getInterestTurnover());
                                }
                                if(records.get(i).getDebtEndPeriod() != null) {
                                    newRow.createCell(24).setCellValue(records.get(i).getDebtEndPeriod());
                                }
                                if(records.get(i).getInterestEndPeriod() != null) {
                                    newRow.createCell(27).setCellValue(records.get(i).getInterestEndPeriod());
                                }
                                if(records.get(i).getTotalEndPeriod() != null) {
                                    newRow.createCell(30).setCellValue(records.get(i).getTotalEndPeriod());
                                }

                                // set styles
                                for(int j = 0; j < 31; j++){
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
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.FIN_LIABILITIES_TYPE) &&
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
            File tmpDir = new File(this.rootDirectory + "/tmp");

            String[]entries = tmpDir.list();
            if(entries != null) {
                for (String s : entries) {
                    File currentFile = new File(tmpDir.getPath(), s);
                    currentFile.delete();
                }
            }

            start = System.currentTimeMillis();
            // write to new
            String filePath = this.rootDirectory + "/tmp/CONS_KZT_FORM_13_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        }catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_13", e);
        }

        return null;
    }

    private InputStream getConsolidatedForm14KZTReportInputStream(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        long start = System.currentTimeMillis();
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

            start = System.currentTimeMillis();

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
            File tmpDir = new File(this.rootDirectory + "/tmp");

            String[]entries = tmpDir.list();
            if(entries != null) {
                for (String s : entries) {
                    File currentFile = new File(tmpDir.getPath(), s);
                    currentFile.delete();
                }
            }

            start = System.currentTimeMillis();
            // write to new
            String filePath = this.rootDirectory + "/tmp/CONS_KZT_FORM_14_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_14", e);
        }

        return null;
    }

    private InputStream getConsolidatedForm19KZTReportInputStream(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        long start = System.currentTimeMillis();
        Map<Integer, List<ConsolidatedKZTForm19RecordDto>> recordsMap = null;
        try{
            recordsMap = getConsolidatedBalanceKZTForm19Map(reportId);
        }catch (IllegalStateException ex){
            throw ex;
        }

//        long end = System.currentTimeMillis();
//        System.out.println((end-start) / 1000 + " seconds - generating report data");

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_19.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_19.xlsx'");
            return null;
            //e.printStackTrace();
        }


        try {
//            start = System.currentTimeMillis();
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");

            start = System.currentTimeMillis();

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
                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_19_LAST_RECORD)) {
                        endOfTable = true;
                    }

                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
                        List<ConsolidatedKZTForm19RecordDto> records = recordsMap.get(lineNumber);
                        int recordsNum = records != null ? records.size() : 0;
                        Cell nameCell = endOfTable ? row.getCell(0) : row.getCell(1);
                        if (recordsNum > 0 && nameCell != null && nameCell.getCellType() == Cell.CELL_TYPE_STRING &&
                                records.get(0).getName().equalsIgnoreCase(nameCell.getStringCellValue())) {
                            // Set cell values

                            if(records.get(0).getOtherEntityName() != null) {
                                row.getCell(3).setCellValue(records.get(0).getOtherEntityName());
                            }
                            if(records.get(0).getPreviousAccountBalance() != null) {
                                row.getCell(4).setCellValue(records.get(0).getPreviousAccountBalance());
                            }
                            if(records.get(0).getTurnover() != null) {
                                row.getCell(5).setCellValue(records.get(0).getTurnover());
                            }
                            if(records.get(0).getCurrentAccountBalance() != null) {
                                row.getCell(6).setCellValue(records.get(0).getCurrentAccountBalance());
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

                                if(records.get(i).getOtherEntityName() != null) {
                                    newRow.createCell(3).setCellValue(records.get(i).getOtherEntityName());
                                }
                                if(records.get(i).getPreviousAccountBalance() != null) {
                                    newRow.createCell(4).setCellValue(records.get(i).getPreviousAccountBalance());
                                }
                                if(records.get(i).getTurnover() != null) {
                                    newRow.createCell(5).setCellValue(records.get(i).getTurnover());
                                }
                                if(records.get(i).getCurrentAccountBalance() != null) {
                                    newRow.createCell(6).setCellValue(records.get(i).getCurrentAccountBalance());
                                }

                                // set styles
                                for(int j = 0; j < 7; j++){
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
            File tmpDir = new File(this.rootDirectory + "/tmp");

            String[]entries = tmpDir.list();
            if(entries != null) {
                for (String s : entries) {
                    File currentFile = new File(tmpDir.getPath(), s);
                    currentFile.delete();
                }
            }

            start = System.currentTimeMillis();
            // write to new
            String filePath = this.rootDirectory + "/tmp/CONS_KZT_FORM_19_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_19", e);
        }

        return null;
    }

    private InputStream getConsolidatedForm22KZTReportInputStream(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        long start = System.currentTimeMillis();
        Map<Integer, List<ConsolidatedKZTForm22RecordDto>> recordsMap = null;
        try{
            recordsMap = getConsolidatedBalanceKZTForm22Map(reportId);
        }catch (IllegalStateException ex){
            throw ex;
        }
//        long end = System.currentTimeMillis();
//        System.out.println((end-start) / 1000 + " seconds - generating report data");

        Resource resource = new ClassPathResource("export_template/TEMPLATE_NICKMF_cons_KZT_22.xlsx");
        InputStream excelFileToRead = null;
        try {
            excelFileToRead = resource.getInputStream();
        } catch (IOException e) {
            logger.error("Reporting: Export file template not found: 'TEMPLATE_NICKMF_cons_KZT_22.xlsx'");
            return null;
            //e.printStackTrace();
        }

        try {
//            start = System.currentTimeMillis();
            XSSFWorkbook  workbook = new XSSFWorkbook(excelFileToRead);
//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds -creating workbook instance from template file");

            start = System.currentTimeMillis();

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
                    if (row.getCell(0) != null && ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), PeriodicReportConstants.KZT_FORM_22_LAST_RECORD)) {
                        endOfTable = true;
                    }

                    if (row.getCell(2) != null && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        int lineNumber = (int) row.getCell(2).getNumericCellValue();
                        List<ConsolidatedKZTForm22RecordDto> records = recordsMap.get(lineNumber);
                        int recordsNum = records != null ? records.size() : 0;
                        Cell nameCell = endOfTable ? row.getCell(0) : row.getCell(1);
                        if (recordsNum > 0 && nameCell != null && nameCell.getCellType() == Cell.CELL_TYPE_STRING &&
                                records.get(0).getName().equalsIgnoreCase(nameCell.getStringCellValue())) {
                            // Set cell values

                            if(records.get(0).getPreviousAccountBalance() != null) {
                                row.getCell(3).setCellValue(records.get(0).getPreviousAccountBalance());
                            }
                            if(records.get(0).getTurnover() != null) {
                                row.getCell(4).setCellValue(records.get(0).getTurnover());
                            }
                            if(records.get(0).getCurrentAccountBalance() != null) {
                                row.getCell(5).setCellValue(records.get(0).getCurrentAccountBalance());
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

                                if(records.get(i).getPreviousAccountBalance() != null) {
                                    newRow.createCell(3).setCellValue(records.get(i).getPreviousAccountBalance());
                                }
                                if(records.get(i).getTurnover() != null) {
                                    newRow.createCell(4).setCellValue(records.get(i).getTurnover());
                                }
                                if(records.get(i).getCurrentAccountBalance() != null) {
                                    newRow.createCell(5).setCellValue(records.get(i).getCurrentAccountBalance());
                                }

                                // set styles
                                for(int j = 0; j < 7; j++){
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
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), PeriodicReportConstants.INCOME_EXPENSE_TYPE) &&
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
            File tmpDir = new File(this.rootDirectory + "/tmp");

            String[]entries = tmpDir.list();
            if(entries != null) {
                for (String s : entries) {
                    File currentFile = new File(tmpDir.getPath(), s);
                    currentFile.delete();
                }
            }

            start = System.currentTimeMillis();
            // write to new
            String filePath = this.rootDirectory + "/tmp/CONS_KZT_FORM_22_" + new Date().getTime() + ".xlsx";
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

//            end = System.currentTimeMillis();
//            System.out.println((end-start) / 1000 + " seconds - writing workbook to output file");

            InputStream inputStream = new FileInputStream(filePath);
            return inputStream;
        } catch (IOException e) {
            // TODO: log error
            //e.printStackTrace();
            logger.error("IO Exception when exporting KZT_FORM_22", e);
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
    public ConsolidatedReportRecordHolderDto getStatementBalanceOperations(Long reportId) {
        ConsolidatedReportRecordHolderDto balanceResults = this.statementBalanceService.get(reportId);
        ConsolidatedReportRecordHolderDto operationsResults = this.statementOperationsService.get(reportId);
        balanceResults.merge(operationsResults);
        return balanceResults;
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
}