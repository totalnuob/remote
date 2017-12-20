package kz.nicnbk.service.impl.reporting;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.HierarchicalBaseDictionaryDto;
import kz.nicnbk.common.service.util.*;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.lookup.StrategyRepository;
import kz.nicnbk.repo.api.reporting.*;
import kz.nicnbk.repo.api.reporting.hedgefunds.SingularityNICChartOfAccountsRepository;
import kz.nicnbk.repo.api.reporting.privateequity.PEGeneralLedgerFormDataRepository;
import kz.nicnbk.repo.api.reporting.privateequity.TarragonNICChartOfAccountsRepository;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.reporting.*;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFGeneralLedgerBalance;
import kz.nicnbk.repo.model.reporting.hedgefunds.ReportingHFNOAL;
import kz.nicnbk.repo.model.reporting.hedgefunds.SingularityNICChartOfAccounts;
import kz.nicnbk.repo.model.reporting.privateequity.*;
import kz.nicnbk.service.api.common.CurrencyRatesService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.reporting.PeriodicDataService;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFNOALService;
import kz.nicnbk.service.api.reporting.privateequity.*;
import kz.nicnbk.service.converter.reporting.*;
import kz.nicnbk.service.dto.common.CurrencyRatesDto;
import kz.nicnbk.service.dto.common.FileUploadResultDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.StatusResultType;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.exception.ExcelFileParseException;
import kz.nicnbk.service.dto.reporting.privateequity.PEGeneralLedgerFormDataDto;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
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
    private PEStatementOperationsService statementOperatinsService;

    @Autowired
    private StrategyRepository strategyRepository;

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
    private ReportingOtherInfoConverter reportingOtherInfoConverter;

    @Autowired
    private ReportingOtherInfoRepository reportingOtherInfoRepository;


    @Autowired
    private NICKMFReportingDataConverter nickmfReportingDataConverter;

    @Autowired
    private NICKMFReportingDataRepository nickmfReportingDataRepository;


    @Autowired
    private SingularityNICChartOfAccountsRepository singularityNICChartOfAccountsRepository;

    @Autowired
    private PEGeneralLedgerFormDataConverter peGeneralLedgerFormDataConverter;
    @Autowired
    private TarragonNICChartOfAccountsRepository tarragonNICChartOfAccountsRepository;

    @Autowired
    private PEGeneralLedgerFormDataRepository peGeneralLedgerFormDataRepository;

    @Autowired
    private PreviousYearInputDataRepository previousYearInputDataRepository;

    @Autowired
    private NICReportingChartOfAccountsRepository nicReportingChartOfAccountsRepository;

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


    /* ****************************************************************************************************************/
    /* ****************************************************************************************************************/

    /**
     * Assemble entity from DTO data, save to database and return entity id.
     * Set entity updater to specified username.
     *
     * @param dto - dto
     * @param updater - username of updater
     * @return - entity id
     */
    @Override
    public Long save(PeriodicReportDto dto, String updater) {
        try {
            PeriodicReport entity = this.periodicReportConverter.assemble(dto);
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

                // TODO: updater check not null
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

    /**
     *
     * @param fileId
     * @return
     */
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

    /**
     *
     * @param id
     * @return
     */
    @Override
    public PeriodicReportDto get(Long id) {
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
     *
     * @return
     */
    @Override
    public List<PeriodicReportDto> getAll() {

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
            logger.error("Error loading all period reports", ex);
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
    public PeriodicReportInputFilesHolder getPeriodicReportFiles(Long reportId) {
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
                    holder.setReport(this.periodicReportConverter.disassemble(entities.get(0).getPeriodicReport()));
                }
            }
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
    public FilesDto getPeriodicReportFile(Long reportId, String type){
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
    public boolean safeDelete(Long reportId, FileTypeLookup fileTypeLookup, String username) {
        try{
            PeriodicReportFiles entity = this.periodicReportFilesRepository.getEntityByReportIdAndFileType(reportId, fileTypeLookup.getCode());
            if (entity != null) {
                boolean deleted = fileService.safeDelete(entity.getFile().getId());
                if(!deleted){
                    logger.error("Failed to delete(safe) periodic report - monthly cash statement file: report id=" + reportId+ ", file id=" + entity.getFile().getId() + ", by " + username);
                }else{
                    logger.info("Deleted(safe) periodic report - monthly cash statement file: report id=" + reportId+ ", file id=" + entity.getFile().getId() + ", by " + username);
                }
                return true;
            }else{
                logger.error("Failed to delete(safe) periodic report - monthly cash statement file: report id=" + reportId+ ", file type=" + fileTypeLookup.getCode() + ", by " + username +
                        ". Periodic Report File entity not found");
            }
        }catch (Exception ex){
            logger.error("Failed to delete(safe) attachment with exception: report id=" + reportId + ", file type=" + fileTypeLookup.getCode() + ", by " + username, ex);
        }
        return false;
    }

    @Transactional
    @Override
    public boolean savePEGeneralLedgerFormData(PEGeneralLedgerFormDataHolderDto dataHolderDto) {
        try {
            if(dataHolderDto != null && dataHolderDto.getRecords() != null){

                checkPEGeneralLedgerFormData(dataHolderDto.getRecords());

                // TODO: check report status
                PeriodicReport periodicReport = this.periodReportRepository.findOne(dataHolderDto.getReport().getId());
                if(periodicReport != null && periodicReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
                    return false;
                }

                for(PEGeneralLedgerFormDataDto dto: dataHolderDto.getRecords()){
                    PEGeneralLedgerFormData entity = this.peGeneralLedgerFormDataConverter.assemble(dto);
                    entity.setReport(new PeriodicReport(dataHolderDto.getReport().getId()));
                    this.peGeneralLedgerFormDataRepository.save(entity);
                }
                return true;
            }
        }catch (IllegalArgumentException ex){
            logger.error("Error saving Tarragon GL Form data: input validation failed", ex);
        }catch (Exception ex){
            logger.error("Error saving Tarragon GL Form data.", ex);
        }
        return false;
    }

    private void checkPEGeneralLedgerFormData(List<PEGeneralLedgerFormDataDto> records){
        if(records != null){
            double totalAssets = 0.0;
            double totalOther = 0.0;
            for(PEGeneralLedgerFormDataDto record: records){
                if(record.getTranche() != 1 && record.getTranche() != 2){
                    throw new IllegalArgumentException("Tranche value invalid : " + record.getTranche() + "; expected values 1, 2");
                }
                if(!isValidFinancialStatementCategory(record.getFinancialStatementCategory())){
                    throw new IllegalArgumentException("Financial statement category value invalid : " + record.getFinancialStatementCategory() + "; expected values A, L, E, X, I");
                }
                if(StringUtils.isEmpty(record.getTarragonNICChartOfAccountsName())){
                    throw new IllegalArgumentException("Chart oof Accounts Name value missing");
                }else{
                    // check valid ?
                }
                if(record.getGLAccountBalance() == null){
                    throw new IllegalArgumentException("Account Balance value missing");
                }
                if(record.getFinancialStatementCategory().equalsIgnoreCase("A")){
                    totalAssets += record.getGLAccountBalance().doubleValue();
                }else{
                    totalOther += record.getGLAccountBalance().doubleValue();
                }
            }

//            double difference = totalAssets - totalOther;
//            if(difference > 2 || difference < -2){
//                throw new IllegalArgumentException("Total Assets (" + totalAssets + ") not equal to Total Other (" + totalOther + "); difference can be between -1 and 1");
//            }
        }

    }

    private boolean isValidFinancialStatementCategory(String value){
        return StringUtils.isNotEmpty(value) && (value.equalsIgnoreCase("A") || value.equalsIgnoreCase("L") ||
                value.equalsIgnoreCase("E") || value.equalsIgnoreCase("X") || value.equalsIgnoreCase("I"));
    }

    @Override
    public boolean deletePEGeneralLedgerFormDataRecordById(Long recordId) {
        try {
            PEGeneralLedgerFormData entity = this.peGeneralLedgerFormDataRepository.findOne(recordId);
            if (entity != null) {
                if(entity.getReport().getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
                    return false;
                }
                this.peGeneralLedgerFormDataRepository.delete(entity);
                return true;
            }
        }catch (Exception ex){
            logger.error("Error deleting PE General Ledger Record with id=" + recordId, ex);
        }
        return false;
    }

    @Override
    public boolean saveUpdatedTarragonInvestment(UpdateTarragonInvestmentDto updateDto) {
        boolean saved = this.scheduleInvestmentService.updateScheduleInvestments(updateDto);
        return saved;
    }

    @Override
    public List<PreviousYearInputDataDto> getPreviousYearInputData(Long reportId) {
        List<PreviousYearInputDataDto> records = new ArrayList<>();
        List<PreviousYearInputData> entities = previousYearInputDataRepository.getEntitiesByReportId(reportId);
        if(entities != null){
            for(PreviousYearInputData entity: entities){
                PreviousYearInputDataDto record = new PreviousYearInputDataDto();
                if(entity.getChartOfAccounts() != null) {
                    BaseDictionaryDto nicChartAccountsBaseDto = disassemble(entity.getChartOfAccounts());
                    NICReportingChartOfAccountsDto nicChartAccountsDto = new NICReportingChartOfAccountsDto(nicChartAccountsBaseDto);
                    if (entity.getChartOfAccounts().getNbChartOfAccounts() != null) {
                        nicChartAccountsDto.setNBChartOfAccounts(disassemble(entity.getChartOfAccounts().getNbChartOfAccounts()));
                    }
                    record.setChartOfAccounts(nicChartAccountsDto);
                }
                record.setAccountBalance(entity.getAccountBalance());
                record.setAccountBalanceKZT(entity.getAccountBalanceKZT());
                if(entity.getReport() != null) {
                    record.setReport(periodicReportConverter.disassemble(entity.getReport()));
                }
                records.add(record);
            }
        }
        return records;
    }

    @Override
    public List<PreviousYearInputDataDto> getPreviousYearInputDataFromPreviousMonth(Long reportId) {

        List<PreviousYearInputDataDto> records = new ArrayList<>();

        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport != null) {
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            List<PreviousYearInputData> entities = previousYearInputDataRepository.getEntitiesByReportId(previousReport.getId());
            if (entities != null) {
                for (PreviousYearInputData entity : entities) {
                    PreviousYearInputDataDto record = new PreviousYearInputDataDto();
                    if (entity.getChartOfAccounts() != null) {
                        BaseDictionaryDto nicChartAccountsBaseDto = disassemble(entity.getChartOfAccounts());
                        NICReportingChartOfAccountsDto nicChartAccountsDto = new NICReportingChartOfAccountsDto(nicChartAccountsBaseDto);
                        if (entity.getChartOfAccounts().getNbChartOfAccounts() != null) {
                            nicChartAccountsDto.setNBChartOfAccounts(disassemble(entity.getChartOfAccounts().getNbChartOfAccounts()));
                        }
                        record.setChartOfAccounts(nicChartAccountsDto);
                    }
                    record.setAccountBalance(entity.getAccountBalance());
                    record.setAccountBalanceKZT(entity.getAccountBalanceKZT());
                    if (entity.getReport() != null) {
                        record.setReport(periodicReportConverter.disassemble(entity.getReport()));
                    }
                    records.add(record);
                }
            }
        }

        return records;
    }

    @Override
    public boolean savePreviousYearInputData(List<PreviousYearInputDataDto> records, Long reportId) {
        if(records != null && reportId != null){
            try {

                // TODO: check report status
                PeriodicReport periodicReport = this.periodReportRepository.findOne(reportId);
                if(periodicReport != null && periodicReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
                    return false;
                }

                // TODO: rollback on error? Transactional?
                this.previousYearInputDataRepository.deleteAllByReportId(reportId);

                for (PreviousYearInputDataDto record : records) {
                    PreviousYearInputData entity = new PreviousYearInputData();
                    entity.setAccountBalance(record.getAccountBalance());
                    entity.setAccountBalanceKZT(record.getAccountBalanceKZT());
                    entity.setReport(new PeriodicReport(reportId));
                    if (record.getChartOfAccounts() != null) {
                        NICReportingChartOfAccounts nicReportingChartOfAccounts = this.nicReportingChartOfAccountsRepository.findByCode(record.getChartOfAccounts().getCode());
                        if (nicReportingChartOfAccounts != null) {
                            entity.setChartOfAccounts(nicReportingChartOfAccounts);
                        } else {
                            // TODO: throw error
                        }
                    } else {
                        // TODO: throw error
                    }

                    this.previousYearInputDataRepository.save(entity);
                }
            }catch (Exception ex){

                // TODO: rollback on error? Transactional?

                logger.error("Error saving previous year input data for reportId=" + reportId, ex);
                return false;
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean safeDeleteFile(Long fileId) {
        try{
            PeriodicReportFiles periodicReportFiles = this.periodicReportFilesRepository.getEntityByFileId(fileId);

            if (periodicReportFiles != null) {

                PeriodicReport periodicReport = periodicReportFiles.getPeriodicReport();

                // TODO: check status
                if(periodicReport == null || (periodicReport.getStatus() != null && periodicReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED"))){

                    // TODO: log error
                    // TODO: error message

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
                    }else{

                        // TODO: recover from error


                        logger.error("Error deleting file: file id=" + fileId + ", report di=" + periodicReport.getId());
                    }
                }
                return true;
            }else{
                logger.error("Failed to delete(safe) reporting input file: file id=" + fileId);
            }
        }catch (Exception ex){
            logger.error("Failed to delete(safe) reporting input file: file id=" + fileId, ex);
        }
        return false;
    }

    @Override
    public boolean markReportAsFinal(Long reportId) {
        try {
            PeriodicReport report = this.periodReportRepository.findOne(reportId);

            // TODO: which report can be made final

            // save USD forms
            List<ConsolidatedBalanceFormRecordDto> balanceRecords = getConsolidatedBalanceUSDForm(reportId);
            boolean saved = saveConsolidatedUSDFormBalance(balanceRecords, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            List<ConsolidatedBalanceFormRecordDto> incomeExpenseRecords = getConsolidatedIncomeExpenseUSDForm(reportId);
            saved = saveConsolidatedUSDFormIncomeExpense(incomeExpenseRecords, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            List<ConsolidatedBalanceFormRecordDto> totalIncomeRecords = getConsolidatedTotalIncomeUSDForm(reportId);
            saved = saveConsolidatedUSDFormTotalIncome(incomeExpenseRecords, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            // save kzt form 1
            List<ConsolidatedBalanceFormRecordDto> form1Records = getConsolidatedBalanceKZTForm1(reportId);
            saved = saveConsolidatedKZTForm1(form1Records, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            // save kzt form 2
            List<ConsolidatedBalanceFormRecordDto> form2Records = getConsolidatedIncomeExpenseKZTForm2(reportId);
            saved = saveConsolidatedKZTForm2(form2Records, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            // save kzt form 3
            List<ConsolidatedBalanceFormRecordDto> form3Records = getConsolidatedTotalIncomeKZTForm3(reportId);
            saved = saveConsolidatedKZTForm3(form3Records, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            // save from 7
            List<ConsolidatedKZTForm7RecordDto> form7Records = getConsolidatedBalanceKZTForm7(reportId);
            saved = saveConsolidatedKZTForm7(form7Records, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            // save from 8
            List<ConsolidatedKZTForm8RecordDto> form8Records = getConsolidatedBalanceKZTForm8(reportId);
            saved = saveConsolidatedKZTForm8(form8Records, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            List<ConsolidatedKZTForm10RecordDto> form10Records = getConsolidatedBalanceKZTForm10(reportId);
            saved = saveConsolidatedKZTForm10(form10Records, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            List<ConsolidatedKZTForm13RecordDto> form13Records = getConsolidatedBalanceKZTForm13(reportId);
            saved = saveConsolidatedKZTForm13(form13Records, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            List<ConsolidatedKZTForm14RecordDto> form14Records = getConsolidatedBalanceKZTForm14(reportId);
            saved = saveConsolidatedKZTForm14(form14Records, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            List<ConsolidatedKZTForm19RecordDto> form19Records = getConsolidatedBalanceKZTForm19(reportId);
            saved = saveConsolidatedKZTForm19(form19Records, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            List<ConsolidatedKZTForm22RecordDto> form22Records = getConsolidatedBalanceKZTForm22(reportId);
            saved = saveConsolidatedKZTForm22(form22Records, reportId);
            if(!saved){
                // TODO: handle error
                return false;
            }

            ReportStatus status = new ReportStatus();
            status.setId(3);
            report.setStatus(status);
            this.periodReportRepository.save(report);
            return true;
        }catch (Exception ex){
            logger.error("Error marking report as final: report id=" + reportId, ex);
            return false;
        }
    }

    private boolean saveConsolidatedKZTForm7(List<ConsolidatedKZTForm7RecordDto> records, Long reportId){
        if(records != null){
            try {
                List<ConsolidatedReportKZTForm7> existingEntities = this.consolidatedReportKZTForm7Repository.getEntitiesByReportId(reportId);
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
                List<ConsolidatedReportKZTForm8> existingEntities = this.consolidatedReportKZTForm8Repository.getEntitiesByReportId(reportId);
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
                List<ConsolidatedReportKZTForm10> existingEntities = this.consolidatedReportKZTForm10Repository.getEntitiesByReportId(reportId);
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
                List<ConsolidatedReportKZTForm13> existingEntities = this.consolidatedReportKZTForm13Repository.getEntitiesByReportId(reportId);
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
                List<ConsolidatedReportKZTForm14> existingEntities = this.consolidatedReportKZTForm14Repository.getEntitiesByReportId(reportId);
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
                List<ConsolidatedReportKZTForm19> existingEntities = this.consolidatedReportKZTForm19Repository.getEntitiesByReportId(reportId);
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
                List<ConsolidatedReportKZTForm22> existingEntities = this.consolidatedReportKZTForm22Repository.getEntitiesByReportId(reportId);
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

    private boolean saveConsolidatedKZTForm1(List<ConsolidatedBalanceFormRecordDto> records, Long reportId){
        if(records != null){
            try {
                List<ConsolidatedReportKZTForm1> existingEntities = this.consolidatedReportKZTForm1Repository.getEntitiesByReportId(reportId);
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
                List<ConsolidatedReportKZTForm2> existingEntities = this.consolidatedReportKZTForm2Repository.getEntitiesByReportId(reportId);
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
                List<ConsolidatedReportKZTForm3> existingEntities = this.consolidatedReportKZTForm3Repository.getEntitiesByReportId(reportId);
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

    private boolean saveConsolidatedUSDFormBalance(List<ConsolidatedBalanceFormRecordDto> records, Long reportId){
        if(records != null){
            try {
                List<ConsolidatedReportUSDFormBalance> existingEntities = this.consolidatedReportUSDFormBalanceRepository.getEntitiesByReportId(reportId);
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
                List<ConsolidatedReportUSDFormIncomeExpense> existingEntities = this.consolidatedReportUSDFormIncomeExpenseRepository.getEntitiesByReportId(reportId);
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
                List<ConsolidatedReportUSDFormTotalIncome> existingEntities = this.consolidatedReportUSDFormTotalIncomeRepository.getEntitiesByReportId(reportId);
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

        if(type.equalsIgnoreCase("CONS_BALANCE_USD")){
            return getConsolidatedBalanceUSDReportInputStream(reportId);
        }else if(type.equalsIgnoreCase("INCOME_EXP_USD")){
            return getConsolidatedIncomeExpenseUSDReportInputStream(reportId);
        }else if(type.equalsIgnoreCase("TOTAL_INCOME_USD")){
            return getConsolidatedTotalIncomeUSDReportInputStream(reportId);
        }

        return null;
    }

    @Override
    public List<GeneratedGeneralLedgerFormDto> getTarragonGLAddedRecordsPreviousMonth(Long reportId) {
        List<GeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport != null) {
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            List<PEGeneralLedgerFormData> addedRecods =
                    this.peGeneralLedgerFormDataRepository.getEntitiesByReportId(previousReport.getId(), new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
            if (addedRecods != null) {
                for (PEGeneralLedgerFormData entity : addedRecods) {
                    PEGeneralLedgerFormDataDto addedRecordDto = this.peGeneralLedgerFormDataConverter.disassemble(entity);
                    GeneratedGeneralLedgerFormDto recordDto = new GeneratedGeneralLedgerFormDto();
                    recordDto.setAcronym(addedRecordDto.getTranche() == 1 ? "TARRAGON" : "TARRAGON B");
                    if (addedRecordDto.getReport() != null) {
                        recordDto.setBalanceDate(addedRecordDto.getReport().getReportDate());
                    }
                    recordDto.setFinancialStatementCategory(addedRecordDto.getFinancialStatementCategory());
                    recordDto.setChartAccountsLongDescription(addedRecordDto.getTarragonNICChartOfAccountsName());
                    recordDto.setNbAccountNumber(addedRecordDto.getNbAccountNumber());
                    String entityName = addedRecordDto.getEntityName() != null ? " " + addedRecordDto.getEntityName() : "";
                    recordDto.setNicAccountName(addedRecordDto.getNicAccountName() + entityName);
                    recordDto.setSubscriptionRedemptionEntity(entityName);
                    recordDto.setGLAccountBalance(addedRecordDto.getGLAccountBalance());
                    recordDto.setAdded(true);
                    recordDto.setAddedRecordId(entity.getId());
                    records.add(recordDto);
                }
            }

            setNICChartOfAccounts(records);
        }
        return records;
    }

    @Override
    public List<ConsolidatedKZTForm8RecordDto> getConsolidatedBalanceKZTForm8(Long reportId) {

        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedBalanceKZTForm8Saved(report.getId());
        }else{
            Date rateDate = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
            // Find exchange rate
            CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, "USD");
            if(endCurrencyRatesDto == null){
                logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate));

                // TODO: return error message
                return null;
            }

            int index = 0;
            List<ConsolidatedKZTForm8RecordDto> records = getConsolidatedBalanceKZTForm8LineHeaders();
            for(int i = 0; i < records.size(); i++){
                ConsolidatedKZTForm8RecordDto record = records.get(i);
                if(record.getLineNumber() == 10){
                    index = i;
                    break;
                }
            }

            Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            List<ConsolidatedKZTForm8RecordDto> previousRecords = getConsolidatedBalanceKZTForm8Saved(previousReport.getId());

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

            List<ConsolidatedBalanceFormRecordDto> USDFormRecords = getConsolidatedBalanceUSDForm(reportId);
            ConsolidatedKZTForm8RecordDto totalRecord = new ConsolidatedKZTForm8RecordDto();
            if(records != null && index > 0){
                for(ConsolidatedBalanceFormRecordDto record: USDFormRecords){
                    if(record.getLineNumber() == 8 && record.getAccountNumber() != null){
                        ConsolidatedKZTForm8RecordDto newRecord = new ConsolidatedKZTForm8RecordDto();
                        newRecord.setLineNumber(9);
                        newRecord.setName(record.getName());
                        newRecord.setAccountNumber(record.getAccountNumber());

                        // Set start period (Previous Period Report)
                        for(ConsolidatedKZTForm8RecordDto previousRecord: previousRecords){
                            if(newRecord.getName() != null && previousRecord.getName() != null &&
                                    newRecord.getName().equalsIgnoreCase(previousRecord.getName()) &&
                                    newRecord.getLineNumber() != null&& previousRecord.getLineNumber() != null &&
                                    newRecord.getLineNumber() == previousRecord.getLineNumber()){
                                newRecord.setDebtStartPeriod(previousRecord.getDebtEndPeriod());
                                newRecord.setStartPeriodBalance(previousRecord.getEndPeriodBalance());
                            }
                        }
                        //newRecord.setDebtStartPeriod(convertByRate(record.getPreviousAccountBalance() != null ? record.getPreviousAccountBalance() : 0.0, startCurrencyRatesDto.getValue()));
                        newRecord.setDebtEndPeriod(convertByRate(record.getCurrentAccountBalance() != null ? record.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));
                        if(newRecord.getDebtStartPeriod() != null && newRecord.getDebtEndPeriod() != null) {
                            BigDecimal start = new BigDecimal(newRecord.getDebtStartPeriod());
                            BigDecimal end = new BigDecimal(newRecord.getDebtEndPeriod());
                            BigDecimal diff = end.subtract(start).setScale(2, RoundingMode.HALF_UP);
                            newRecord.setDebtDifference(diff.doubleValue());
                        }

                        newRecord.setAgreementDescription("Investment Management Agreement of Singularity Ltd. from 14.07.2015");

                        // TODO: Debt start date from NOAL
                        Date date = null;
                        for(SingularityNOALRecordDto noalRecord: noalRecords){
                            if(newRecord.getAccountNumber() != null && newRecord.getAccountNumber().equalsIgnoreCase("1283.020")) {
                                if (newRecord.getName().startsWith("Инвестиции к возврату")) {
                                    String name = newRecord.getName().substring(21).trim();
                                    if(StringUtils.isNotEmpty(name) && noalRecord.getName() != null && name.equalsIgnoreCase(noalRecord.getName())){
                                        if(date == null || (noalRecord.getDate() != null && noalRecord.getDate().compareTo(date) < 0)){
                                            date = noalRecord.getDate();
                                        }
                                    }
                                } else if (newRecord.getName().startsWith("Предварительная подписка")) {
                                    String name = newRecord.getName().substring(24).trim();
                                    if(StringUtils.isNotEmpty(name) && noalRecord.getName() != null && name.equalsIgnoreCase(noalRecord.getName())){
                                        if(date == null || (noalRecord.getDate() != null && noalRecord.getDate().compareTo(date) < 0)){
                                            date = noalRecord.getDate();
                                        }
                                    }
                                }
                            }
                        }
                        newRecord.setDebtStartDate(date);
                        newRecord.setEndPeriodBalance(newRecord.getDebtEndPeriod());

                        records.add(index, newRecord);

                        if(newRecord.getDebtStartPeriod() != null) {
                            totalRecord.setDebtStartPeriod((totalRecord.getDebtStartPeriod() != null ? totalRecord.getDebtStartPeriod() : 0.0) + newRecord.getDebtStartPeriod());
                        }
                        if(newRecord.getDebtEndPeriod() != null) {
                            totalRecord.setDebtEndPeriod((totalRecord.getDebtEndPeriod() != null ? totalRecord.getDebtEndPeriod() : 0.0) + newRecord.getDebtEndPeriod());
                        }
                        if(newRecord.getDebtDifference() != null) {
                            totalRecord.setDebtDifference((totalRecord.getDebtDifference() != null ? totalRecord.getDebtDifference() : 0.0) + newRecord.getDebtDifference());
                        }
                        if(newRecord.getStartPeriodBalance() != null) {
                            totalRecord.setStartPeriodBalance((totalRecord.getStartPeriodBalance() != null ? totalRecord.getStartPeriodBalance() : 0.0) + newRecord.getStartPeriodBalance());
                        }
                        if(newRecord.getEndPeriodBalance() != null) {
                            totalRecord.setEndPeriodBalance((totalRecord.getEndPeriodBalance() != null ? totalRecord.getEndPeriodBalance() : 0.0) + newRecord.getEndPeriodBalance());
                        }

                        index ++;
                    }
                }
            }

            // Set total sums
            for(ConsolidatedKZTForm8RecordDto record: records){
                if(record.getAccountNumber() == null && (record.getLineNumber() == 1 || record.getLineNumber() == 9 || record.getLineNumber() == 19)){
                    record.setDebtStartPeriod(totalRecord.getDebtStartPeriod());
                    record.setDebtEndPeriod(totalRecord.getDebtEndPeriod());
                    record.setDebtDifference(totalRecord.getDebtDifference());
                    record.setStartPeriodBalance(totalRecord.getStartPeriodBalance());
                    record.setEndPeriodBalance(totalRecord.getEndPeriodBalance());
                }
            }

            return records;
        }
    }

    @Override
    public List<ConsolidatedKZTForm10RecordDto> getConsolidatedBalanceKZTForm10(Long reportId) {

        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedBalanceKZTForm10Saved(reportId);
        }else{
            Date rateDate = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
            // Find exchange rate
            CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, "USD");
            if(endCurrencyRatesDto == null){
                logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate));

                // TODO: return error message
                return null;
            }

            Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            List<ConsolidatedKZTForm10RecordDto> prevRecords = getConsolidatedBalanceKZTForm10Saved(previousReport.getId());
            for(ConsolidatedKZTForm10RecordDto prevRecord: prevRecords){
                prevRecord.setStartPeriodAssets(prevRecord.getEndPeriodAssets());
                prevRecord.setStartPeriodBalance(prevRecord.getEndPeriodBalance());

                prevRecord.setTurnoverOther(null);
                prevRecord.setEndPeriodAssets(null);
                prevRecord.setEndPeriodBalance(null);
            }

            int indexLineToAdd2 = 0;
            int indexLineToAdd8 = 0;
            Map<String,Integer> addedRecordNames = new HashMap<>();
            for(int i = 0; i < prevRecords.size(); i++){
                ConsolidatedKZTForm10RecordDto record = prevRecords.get(i);
                if(record.getLineNumber() == 3){
                    indexLineToAdd2 = i;
                }else if(record.getLineNumber() == 9){
                    indexLineToAdd8 = i;
                }
            }

            //List<ConsolidatedBalanceFormRecordDto> previousUSDFormRecords = previousReport != null ? getConsolidatedBalanceUSDFormSaved(previousReport.getId()) : null;

            String nameNICKMFMain = "Организационные расходы NICK MF";
            String nameNICKMFOther = "Начисленная амортизация - Организационные расходы NICK MF";
            // TODO: Organization costs NICK MF

            String nameSingularityMain = "Организационные расходы Singularity";
            String nameSingularityOther = "Начисленная амортизация - Организационные расходы Singularity";
            // TODO: Organization costs Singularity

            List<ConsolidatedBalanceFormRecordDto> USDFormRecords = getConsolidatedBalanceUSDForm(report.getId());
            Double total1623EndPeriod = 0.0;
            Double total2923EndPeriod = 0.0;
            if(USDFormRecords != null && (indexLineToAdd2 > 0 || indexLineToAdd8 > 0)){
                for(ConsolidatedBalanceFormRecordDto recordUSD: USDFormRecords){
                    if(recordUSD.getAccountNumber() != null && (recordUSD.getAccountNumber().equalsIgnoreCase("1623.010") || recordUSD.getAccountNumber().equalsIgnoreCase("2923.010"))){
                        if(recordUSD.getAccountNumber().equalsIgnoreCase("1623.010")) {
                            boolean recordExists = false;
                            for(ConsolidatedKZTForm10RecordDto record: prevRecords){
                                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("1623.010") &&
                                        record.getName().equalsIgnoreCase(recordUSD.getName()) && record.getLineNumber() == recordUSD.getLineNumber()){
                                    record.setEndPeriodAssets(convertByRate(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));

                                    double endPeriodAssets = record.getEndPeriodAssets() != null ? record.getEndPeriodAssets() : 0.0;
                                    double startPeriodAssets = record.getStartPeriodAssets() != null ? record.getStartPeriodAssets() : 0.0;
                                    record.setTurnoverOther( endPeriodAssets - startPeriodAssets);
                                    recordExists = true;
                                    break;
                                }
                            }
                            if(!recordExists){
                                ConsolidatedKZTForm10RecordDto newRecord = new ConsolidatedKZTForm10RecordDto();
                                newRecord.setAccountNumber(recordUSD.getAccountNumber());
                                newRecord.setName(recordUSD.getName());
                                newRecord.setLineNumber(recordUSD.getLineNumber());

                                newRecord.setEndPeriodAssets(convertByRate(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));

                                double endPeriodAssets = newRecord.getEndPeriodAssets() != null ? newRecord.getEndPeriodAssets() : 0.0;
                                double startPeriodAssets = newRecord.getStartPeriodAssets() != null ? newRecord.getStartPeriodAssets() : 0.0;
                                newRecord.setTurnoverOther( endPeriodAssets - startPeriodAssets);

                                prevRecords.add(indexLineToAdd2, newRecord);

                                indexLineToAdd2++;
                                indexLineToAdd8++;
                            }

                            total1623EndPeriod += convertByRate(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0, endCurrencyRatesDto.getValue());
                        }else if(recordUSD.getAccountNumber().equalsIgnoreCase("2923.010")) {
                            String name = null;
                            if(recordUSD.getName().equalsIgnoreCase(nameNICKMFMain) || recordUSD.getName().equalsIgnoreCase(nameNICKMFOther)){
                                name = nameNICKMFMain;
                            }else if(recordUSD.getName().equalsIgnoreCase(nameSingularityMain) || recordUSD.getName().equalsIgnoreCase(nameSingularityOther)){
                                name = nameSingularityMain;
                            }else{

                                logger.error("Report KZT 10: account number '2923.010' expected name mismatch: recordUSD.getName()");
                                // TODO: log error
                                return null;
                            }

                            boolean recordExists = false;
                            for(ConsolidatedKZTForm10RecordDto record: prevRecords){
                                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("2923.010") &&
                                        record.getName().equalsIgnoreCase(recordUSD.getName()) && record.getLineNumber() == recordUSD.getLineNumber()){
                                    record.setEndPeriodAssets(convertByRate(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));

                                    double endPeriodAssets = record.getEndPeriodAssets() != null ? record.getEndPeriodAssets() : 0.0;
                                    double startPeriodAssets = record.getStartPeriodAssets() != null ? record.getStartPeriodAssets() : 0.0;
                                    record.setTurnoverOther( endPeriodAssets - startPeriodAssets);
                                    recordExists = true;
                                    break;
                                }
                            }

                            if(!recordExists){
                                ConsolidatedKZTForm10RecordDto newRecord = new ConsolidatedKZTForm10RecordDto();
                                newRecord.setAccountNumber(recordUSD.getAccountNumber());
                                newRecord.setName(recordUSD.getName());
                                newRecord.setLineNumber(recordUSD.getLineNumber());

                                newRecord.setEndPeriodAssets(convertByRate(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));

                                double endPeriodAssets = newRecord.getEndPeriodAssets() != null ? newRecord.getEndPeriodAssets() : 0.0;
                                double startPeriodAssets = newRecord.getStartPeriodAssets() != null ? newRecord.getStartPeriodAssets() : 0.0;
                                newRecord.setTurnoverOther( endPeriodAssets - startPeriodAssets);

                                prevRecords.add(indexLineToAdd2, newRecord);

                                indexLineToAdd2++;
                                indexLineToAdd8++;
                            }
                            total2923EndPeriod += convertByRate(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0, endCurrencyRatesDto.getValue());
                        }
                    }
                }
            }

            // Set total sums
            for(ConsolidatedKZTForm10RecordDto record: prevRecords){
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
                record.setTurnoverOther(endPeriodAssets - startPeriodAssets);

                record.setStartPeriodBalance(record.getStartPeriodAssets());
                record.setEndPeriodBalance(record.getEndPeriodAssets());

            }

            return prevRecords;
        }
    }

    @Override
    public List<ConsolidatedKZTForm14RecordDto> getConsolidatedBalanceKZTForm14(Long reportId) {

        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedBalanceKZTForm14Saved(report.getId());
        }else{
            Date rateDate = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
            // Find exchange rate
            CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, "USD");
            if(endCurrencyRatesDto == null){
                logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate));

                // TODO: return error message
                return null;
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

                record.setAgreementDescription("Investment Management Agreement of Singularity Ltd. from 14.07.2015");
                record.setDebtStartDate(DateUtils.getLastDayOfPreviousMonth(report.getReportDate()));

                if(record.getLineNumber() == 9){
                    index = i;
                    break;
                }
            }

            ConsolidatedKZTForm14RecordDto totalRecord = new ConsolidatedKZTForm14RecordDto();
            List<ConsolidatedBalanceFormRecordDto> USDFormRecords = getConsolidatedBalanceUSDForm(report.getId());
            if(records != null && index > 0){
                for(ConsolidatedBalanceFormRecordDto recordUSD: USDFormRecords){
                    if(recordUSD.getAccountNumber() != null && recordUSD.getAccountNumber().equalsIgnoreCase("3393.020")){
                        for(ConsolidatedKZTForm14RecordDto record: records){
                            if(record.getAccountNumber() != null && recordUSD.getAccountNumber().equalsIgnoreCase(record.getAccountNumber()) &&
                                    record.getName().equalsIgnoreCase(recordUSD.getName()) && record.getLineNumber() != null &&
                                    recordUSD.getLineNumber() != null && recordUSD.getLineNumber() == record.getLineNumber()){

                                record.setDebtEndPeriod(convertByRate(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));

                                double debtEndPeriodValue = totalRecord.getDebtEndPeriod() != null ? totalRecord.getDebtEndPeriod() : 0;
                                totalRecord.setDebtEndPeriod(debtEndPeriodValue + record.getDebtEndPeriod());

                                if(record.getDebtStartPeriod() != null && record.getDebtEndPeriod() != null) {
                                    BigDecimal start = new BigDecimal(record.getDebtStartPeriod());
                                    BigDecimal end = new BigDecimal(record.getDebtEndPeriod());
                                    BigDecimal diff = end.subtract(start).setScale(2, RoundingMode.HALF_UP);
                                    record.setDebtDifference(diff.doubleValue());

                                    double debtDiffValue = totalRecord.getDebtDifference() != null ? totalRecord.getDebtDifference() : 0;
                                    totalRecord.setDebtDifference(debtDiffValue + record.getDebtDifference());
                                }
                            }else{
                                ConsolidatedKZTForm14RecordDto newRecord = new ConsolidatedKZTForm14RecordDto();
                                newRecord.setLineNumber(8);
                                newRecord.setName(record.getName());
                                newRecord.setAccountNumber(record.getAccountNumber());

                                newRecord.setDebtEndPeriod(convertByRate(recordUSD.getCurrentAccountBalance() != null ? recordUSD.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));
                                newRecord.setDebtDifference(newRecord.getDebtEndPeriod());

                                newRecord.setAgreementDescription("Investment Management Agreement of Singularity Ltd. from 14.07.2015");
                                newRecord.setDebtStartDate(DateUtils.getLastDayOfPreviousMonth(report.getReportDate()));

                                records.add(index, newRecord);

                                index ++;

                                // total values
                                double debtEndPeriodValue = totalRecord.getDebtEndPeriod() != null ? totalRecord.getDebtEndPeriod() : 0;
                                totalRecord.setDebtEndPeriod(debtEndPeriodValue + newRecord.getDebtEndPeriod());

                                double debtDiffValue = totalRecord.getDebtDifference() != null ? totalRecord.getDebtDifference() : 0;
                                totalRecord.setDebtDifference(debtDiffValue + (newRecord.getDebtDifference() != null ? newRecord.getDebtDifference() : 0));
                            }
                        }



                    }
                }
            }

            // Set total sums
            for(ConsolidatedKZTForm14RecordDto record: records){
                if(record.getAccountNumber() == null && (record.getLineNumber() == 1 || record.getLineNumber() == 8 || record.getLineNumber() == 17)){
                    record.setDebtEndPeriod(totalRecord.getDebtEndPeriod());
                    record.setDebtDifference(totalRecord.getDebtDifference());
                }
            }

            return records;
        }

    }

    @Override
    public List<ConsolidatedKZTForm13RecordDto> getConsolidatedBalanceKZTForm13(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedBalanceKZTForm13Saved(reportId);
        }else{
            Date rateDate = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
            // Find exchange rate
            CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, "USD");
            if(endCurrencyRatesDto == null){
                logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate));

                // TODO: return error message
                return null;
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
                    if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("3013.010") &&
                            record.getName().equalsIgnoreCase("Банковские займы полученные")){
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
                record3013_010.setAccountNumber("3013.010");
                record3013_010.setName("Банковские займы полученные");
                record3013_010.setLineNumber(2);
                record3013_010.setEntityName("Bank of Monreal");
                Date startPeriod = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
                record3013_010.setStartPeriod(startPeriod);

                String endPeriodDateText = "16.09." + DateUtils.getCurrentYear();
                Date endPeriodDate = DateUtils.getDate(endPeriodDateText);
                if (report.getReportDate().compareTo(endPeriodDate) < 0) {
                    record3013_010.setEndPeriod(endPeriodDate);
                } else {
                    record3013_010.setEndPeriod(DateUtils.getDate("16.09." + (DateUtils.getCurrentYear() + 1)));
                }
                record3013_010.setInterestRate("4%");
                record3013_010.setInterestPaymentCount(1);
            }

            boolean currentExists = false;
            List<ConsolidatedBalanceFormRecordDto> currentUSDFormRecords = getConsolidatedBalanceUSDForm(report.getId());
            if(currentUSDFormRecords != null){
                for(ConsolidatedBalanceFormRecordDto record: currentUSDFormRecords){
                    if(record.getAccountNumber() != null && (record.getAccountNumber().equalsIgnoreCase("3013.010")) && record3013_010.getName().equalsIgnoreCase(record.getName())){
                        double debtEndPeriod = record3013_010.getDebtEndPeriod() != null ? record3013_010.getDebtEndPeriod() : 0;
                        record3013_010.setDebtEndPeriod(debtEndPeriod + convertByRate(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue()));

                        currentExists = true;
                    }else if(record.getAccountNumber() != null && (record.getAccountNumber().equalsIgnoreCase("3383.010"))){
                        double interestEndPeriod = record3013_010.getInterestEndPeriod() != null ? record3013_010.getInterestEndPeriod() : 0;
                        record3013_010.setInterestEndPeriod(interestEndPeriod + convertByRate(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue()));

                        currentExists = true;
                    }

                }
            }

            if(currentExists) {
                double totalEndPeriod = record3013_010.getDebtEndPeriod() != null ? record3013_010.getDebtEndPeriod() : 0;
                totalEndPeriod += record3013_010.getInterestEndPeriod() != null ? record3013_010.getInterestEndPeriod() : 0;
                record3013_010.setTotalEndPeriod(totalEndPeriod);

                double interestTurnover = record3013_010.getInterestEndPeriod() != null ? record3013_010.getInterestEndPeriod() : 0;
                interestTurnover -= record3013_010.getInterestStartPeriod() != null ? record3013_010.getInterestStartPeriod() : 0;
                record3013_010.setInterestTurnover(interestTurnover);

                double debtTurnover = record3013_010.getDebtEndPeriod() != null ? record3013_010.getDebtEndPeriod() : 0;
                debtTurnover -= record3013_010.getDebtStartPeriod() != null ? record3013_010.getDebtStartPeriod() : 0;
                record3013_010.setDebtTurnover(debtTurnover);
            }

            if(!previousExists && currentExists){
                previousRecords.add(index, record3013_010);
            }

            return previousRecords;
        }
    }

    @Override
    public List<ConsolidatedKZTForm7RecordDto> getConsolidatedBalanceKZTForm7(Long reportId) {

        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedBalanceKZTForm7Saved(report.getId());
        }else{
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);

            Map<String, ConsolidatedKZTForm7RecordDto> previousRecordsMap = new HashedMap();
            List<ConsolidatedKZTForm7RecordDto> previousPeriodRecords = previousReport != null ? getConsolidatedBalanceKZTForm7Saved(previousReport.getId()) : null;
            if(previousPeriodRecords != null && !previousPeriodRecords.isEmpty()){
                for(ConsolidatedKZTForm7RecordDto record: previousPeriodRecords){
                    if(StringUtils.isEmpty(record.getAccountNumber())){
                        // headers
                        previousRecordsMap.put(record.getLineNumber() + "", record);
                    }else if(StringUtils.isNotEmpty(record.getEntityName())) {
                        // funds
                        previousRecordsMap.put(record.getEntityName(), record);
                    }
                }
            }else{
                // TODO: handle error
                // TODO: No previous KZT Form 7 data

                //return null;
            }

            List<ConsolidatedKZTForm7RecordDto> currentPeriodRecords = getConsolidatedBalanceKZTForm7CurrentPeriod(reportId);
            ConsolidatedKZTForm7RecordDto totalRecord = new ConsolidatedKZTForm7RecordDto();
            ConsolidatedKZTForm7RecordDto totalRecordPE = new ConsolidatedKZTForm7RecordDto();
            ConsolidatedKZTForm7RecordDto totalRecordHF = new ConsolidatedKZTForm7RecordDto();
            for(ConsolidatedKZTForm7RecordDto record: currentPeriodRecords){
                if(record.getLineNumber() != 7 && record.getLineNumber() != 9 && record.getLineNumber() != 12){
                    continue;
                }
                ConsolidatedKZTForm7RecordDto previousRecord = StringUtils.isNotEmpty(record.getEntityName()) ? previousRecordsMap.get(record.getEntityName()) :
                        previousRecordsMap.get(record.getLineNumber() + "");

                // startPeriod
                if(previousRecord != null){
                    record.setDebtStartPeriod(previousRecord.getDebtEndPeriod());
                    record.setFairValueAdjustmentsStartPeriod(previousRecord.getFairValueAdjustmentsEndPeriod());
                    record.setTotalStartPeriod(previousRecord.getTotalEndPeriod());
                }

                if(record.getAccountNumber() == null){
                    continue;
                }

                if(record.isBecameZero()){
                    record.setDebtTurnover(record.getDebtStartPeriod() != null ? 0 - record.getDebtStartPeriod().doubleValue() : null);
                }

                // debtEndPeriod
                double debtEndPeriod = record.getDebtStartPeriod() != null ? record.getDebtStartPeriod() : 0;
                debtEndPeriod += record.getDebtTurnover() != null ? record.getDebtTurnover() : 0;
                record.setDebtEndPeriod(debtEndPeriod);

                // fairValueAdjustmentsTurnover
                double fairValueAdjustmentsTurnover = record.getTotalEndPeriod() != null ? record.getTotalEndPeriod() : 0;
                fairValueAdjustmentsTurnover -= record.getDebtEndPeriod() != null ? record.getDebtEndPeriod() : 0;
                fairValueAdjustmentsTurnover -= record.getFairValueAdjustmentsStartPeriod() != null ? record.getFairValueAdjustmentsStartPeriod() : 0;
                if(fairValueAdjustmentsTurnover >=0 ){
                    record.setFairValueAdjustmentsTurnoverPositive(fairValueAdjustmentsTurnover);
                }else{
                    record.setFairValueAdjustmentsTurnoverNegative(fairValueAdjustmentsTurnover);
                }

                // setFairValueAdjustmentsEndPeriod
                double fairValueAdjustmentsEndPeriod = record.getFairValueAdjustmentsStartPeriod() != null ? record.getFairValueAdjustmentsStartPeriod() : 0;
                fairValueAdjustmentsEndPeriod += fairValueAdjustmentsTurnover;
                record.setFairValueAdjustmentsEndPeriod(fairValueAdjustmentsEndPeriod);

                // set total values
                addValuesKZTForm7(totalRecord, record);

                if(record.getOtherName().equalsIgnoreCase("SINGULAR")) {
                    addValuesKZTForm7(totalRecordHF, record);
                }else {
                    addValuesKZTForm7(totalRecordPE, record);
                }

            }

            for(ConsolidatedKZTForm7RecordDto record: currentPeriodRecords){
                if(record.getAccountNumber() == null && (record.getLineNumber() == 7 || record.getLineNumber() == 9 || record.getLineNumber() == 12)){
                    record.setDebtTurnover(totalRecord.getDebtTurnover());
                    record.setFairValueAdjustmentsTurnoverPositive(totalRecord.getFairValueAdjustmentsTurnoverPositive());
                    record.setFairValueAdjustmentsTurnoverNegative(totalRecord.getFairValueAdjustmentsTurnoverNegative());
                    record.setDebtEndPeriod(totalRecord.getDebtEndPeriod());
                    record.setFairValueAdjustmentsEndPeriod(totalRecord.getFairValueAdjustmentsEndPeriod());
                    record.setTotalEndPeriod(totalRecord.getTotalEndPeriod());
                }
            }

            return currentPeriodRecords;
        }
    }

    @Override
    public List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceKZTForm1(Long reportId) {
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        if(currentReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedBalanceKZTForm1Saved(reportId);
        }else{
            List<ConsolidatedBalanceFormRecordDto> currentRecrods = getConsolidatedBalanceKZTForm1Current(reportId);

            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if (previousReport != null) {
                List<ConsolidatedBalanceFormRecordDto> previousRecords = getConsolidatedBalanceKZTForm1Saved(previousReport.getId());
                if(previousRecords != null){
                    for(ConsolidatedBalanceFormRecordDto currentRecord: currentRecrods){
                        for(ConsolidatedBalanceFormRecordDto prevRecord: previousRecords){
                            if(currentRecord.getName() != null && prevRecord.getName() != null &&
                                    currentRecord.getName().equalsIgnoreCase(prevRecord.getName()) &&
                                    currentRecord.getLineNumber() != null && prevRecord.getLineNumber() != null &&
                                    currentRecord.getLineNumber() == prevRecord.getLineNumber()){
                                currentRecord.setPreviousAccountBalance(prevRecord.getCurrentAccountBalance());
                            }
                        }
                    }
                }
            }

            return currentRecrods;
        }



    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceKZTForm1Current(Long reportId) {
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        Date rateDate = DateUtils.getFirstDayOfNextMonth(currentReport.getReportDate());
        // Find exchange rate
        CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, "USD");
        if(endCurrencyRatesDto == null || endCurrencyRatesDto.getValue() == null){
            logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate));

            // TODO: return error message
            return null;
        }

        Date averageRateDate = DateUtils.getLastDayOfCurrentMonth(currentReport.getReportDate());
        Double averageRate = this.currencyRatesService.getAverageRateForDateAndCurrency(averageRateDate, "USD", 2);
        if(averageRate == null){
            logger.error("No average currency rate found for date '" + DateUtils.getDateFormatted(averageRateDate));

            // TODO: return error message
            return null;
        }

        //Map<Integer, Double> sums = new HashedMap();
//        for(int i = 1; i <= 52; i++){
//            sums.put(i, 0.0);
//        }
        // Add line number headers
        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedBalanceKZTForm1LineHeaders();

        List<ConsolidatedBalanceFormRecordDto> recordsUSD = getConsolidatedBalanceUSDForm(reportId);

        String record1033_010Name = "Деньги на текущих счетах";
        String record1033_010AccountNumber = "1033.010";
        double record1033_010 = 0;

        String record5440_010Name = "Резерв по переоценке финансовых инвестиций, имеющихся в наличии для продажи";
        String record5440_010ReportName = "Резерв на переоценку финансовых инвестиций, имеющихся в наличии для продажи";
        String record5440_010AccountNumber = "5440.010";
        double record5440_010 = 0;
        if(recordsUSD != null){
            for(ConsolidatedBalanceFormRecordDto record: recordsUSD){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record1033_010AccountNumber) &&
                        record.getName().equalsIgnoreCase(record1033_010Name)){
                    record1033_010 += convertByRate(record.getCurrentAccountBalance(), endCurrencyRatesDto.getValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record5440_010AccountNumber) &&
                        record.getName().equalsIgnoreCase(record5440_010Name)){
                    // Average rate
                    record5440_010 += convertByRate(record.getCurrentAccountBalance(), averageRate);
                }
            }
        }

        String record1283_020Name = "Прочая краткосрочная дебиторская задолженность";
        String record1283_020AccountNumber = "1283.020";
        double record1283_020 = 0;
        List<ConsolidatedKZTForm8RecordDto> form8Records = getConsolidatedBalanceKZTForm8(reportId);
        if(form8Records != null){
            for(ConsolidatedKZTForm8RecordDto record: form8Records){
                if(record.getAccountNumber() == null && record.getLineNumber() == 9 && record.getDebtEndPeriod() != null){
                    record1283_020 = record.getDebtEndPeriod();
                }
            }
        }

        String record1623_010Name = "Расходы будущих периодов";
        String record1623_010AccountNumber = "1623.010";
        double record1623_010 = 0;

        String record2923_010Name = "Расходы будущих периодов";
        String record2923_010AccountNumber = "2923.010";
        double record2923_010 = 0;

        List<ConsolidatedKZTForm10RecordDto> form10Records = getConsolidatedBalanceKZTForm10(reportId);
        if(form10Records != null){
            for(ConsolidatedKZTForm10RecordDto record: form10Records){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record1623_010AccountNumber) && record.getEndPeriodBalance() != null){
                    record1623_010 += record.getEndPeriodBalance();
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record2923_010AccountNumber) && record.getEndPeriodBalance() != null){
                    record2923_010 += record.getEndPeriodBalance();
                }
            }
        }

        String record2033_010Name = "Долгосрочные ценные бумаги, имеющиеся в наличии для продажи";
        String record2033_010AccountNumber = "2033.010";
        double record2033_010 = 0;

        String record2033_040Name = "Положительная корректировка справедливой стоимости долгосрочных ценных бумаг, имеющихся в наличии для продажи";
        String record2033_040AccountNumber = "2033.040";
        double record2033_040 = 0;

        String record2033_050Name = "Отрицательная корректировка справедливой стоимости долгосрочных ценных бумаг, имеющихся в наличии для продажи";
        String record2033_050AccountNumber = "2033.050";
        double record2033_050 = 0;
        List<ConsolidatedKZTForm7RecordDto> form7Records = getConsolidatedBalanceKZTForm7(reportId);
        if(form7Records != null){
            for(ConsolidatedKZTForm7RecordDto record: form7Records){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record2033_010AccountNumber)){
                    record2033_010 += record.getDebtEndPeriod() != null ? record.getDebtEndPeriod() : 0;
                    if(record.getFairValueAdjustmentsEndPeriod() != null){
                        record2033_040 += record.getFairValueAdjustmentsEndPeriod().doubleValue() >= 0 ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0;
                        record2033_050 += record.getFairValueAdjustmentsEndPeriod().doubleValue() < 0 ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0;
                    }
                }
            }
        }

        String record3013_010Name = "Краткосрочные банковские займы полученные";
        String record3013_010AccountNumber = "3013.010";
        double record3013_010 = 0;

        String record3383_010Name = "Начисленные расходы в виде вознаграждения по краткосрочным банковским займам полученным";
        String record3383_010AccountNumber = "3383.010";
        double record3383_010 = 0;

        List<ConsolidatedKZTForm13RecordDto> form13Records = getConsolidatedBalanceKZTForm13(reportId);
        if(form13Records != null){
            for(ConsolidatedKZTForm13RecordDto record: form13Records){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3013_010AccountNumber) && record.getDebtEndPeriod() != null){
                    record3013_010 += record.getDebtEndPeriod();
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3013_010AccountNumber) && record.getInterestEndPeriod() != null){
                    record3383_010 += record.getInterestEndPeriod();
                }
            }
        }

        String record3393_020Name = "Прочая краткосрочная кредиторская задолженность";
        String record3393_020AccountNumber = "3393.020";
        double record3393_020 = 0;
        List<ConsolidatedKZTForm14RecordDto> form14Records = getConsolidatedBalanceKZTForm14(reportId);
        if(form14Records != null){
            for(ConsolidatedKZTForm14RecordDto record: form14Records){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record3393_020AccountNumber)){
                    record3393_020 += record.getDebtEndPeriod() != null ? record.getDebtEndPeriod().doubleValue() : 0;
                }
            }
        }

        String record5440_010_previousYearName = "Резерв по переоценке финансовых инвестиций, имеющихся в наличии для продажи (прошлый год)";
        String record5520_010Name = "Нераспределенная прибыль (непокрытый убыток) прошлых лет";
        String record5520_010AccountNumber = "5520.010";
        double record5520_010 = 0;
        List<PreviousYearInputDataDto> previousYearInputData = getPreviousYearInputData(reportId);
        if(previousYearInputData != null){
            for(PreviousYearInputDataDto inputData: previousYearInputData){
                if(inputData.getChartOfAccounts() != null && inputData.getChartOfAccounts().getCode().startsWith("5440.010") &&
                        inputData.getChartOfAccounts().getNameRu().equalsIgnoreCase(record5440_010_previousYearName)){
                    record5440_010 += inputData.getAccountBalanceKZT() != null ? inputData.getAccountBalanceKZT().doubleValue() : 0;
                }else if(inputData.getChartOfAccounts() != null && inputData.getChartOfAccounts().getCode().startsWith(record5520_010AccountNumber) &&
                        inputData.getChartOfAccounts().getNameRu().equalsIgnoreCase(record5520_010Name)){
                    record5520_010 += inputData.getAccountBalanceKZT() != null ? inputData.getAccountBalanceKZT().doubleValue() : 0;
                }
            }
        }

        // TODO: ?

        String record5021_010AccountNumber = "5021.010";
        String record5022_010AccountNumber = "5022.010";

        String record5021_010Name = "Простые акции";
        String record5022_010Name = "Простые акции";

        // TODO: ??
        double record5022_010 = 18765;

        // TODO: Refactor string literal
        double record5021_010 = 0;
        List<ReserveCalculationDto> reserveCalculations = this.reserveCalculationService.getReserveCalculationsByExpenseType("ADD");
        if(reserveCalculations != null){
            BigDecimal sum = new BigDecimal("0");
            for(ReserveCalculationDto reserveCalculationDto: reserveCalculations){
                sum = sum.add(new BigDecimal(reserveCalculationDto.getAmountKZT() != null ? reserveCalculationDto.getAmountKZT().doubleValue() : 0));
            }
            record5021_010 = sum.setScale(2, RoundingMode.HALF_UP).doubleValue() - record5022_010;
        }

        String record5450_010AccountNumber = "5450.010";
        String record5450_010Name = "Резерв на пересчет иностранной валюты по зарубежной деятельности";
        double record5450_010 = getCurrencyReserve(currentReport.getId(), currentReport.getReportDate());

        Map<Integer, BigDecimal> sums = new HashedMap();
        for(int i = 1; i <= 52; i++){
            sums.put(i, new BigDecimal("0"));
        }

        String record5510_010AccountNumber = "5510.010";
        String record5510_010Name = "Нераспределенная прибыль (непокрытый убыток) отчетного года";
        double record5510_010 = 0;

        // TODO: from IncomeExpense KZT
        List<ConsolidatedBalanceFormRecordDto> form2Records = getConsolidatedIncomeExpenseKZTForm2(reportId);
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
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase(record5022_010AccountNumber) &&
                    record.getName().equalsIgnoreCase(record5022_010Name)){
                record.setCurrentAccountBalance(record5022_010);
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
                    sums.put(record.getLineNumber(), value.add(new BigDecimal(record.getCurrentAccountBalance())));
                }
            }
        }

        for(ConsolidatedBalanceFormRecordDto record: records){
             if(record.getLineNumber() != null && (record.getLineNumber() == 1 || record.getLineNumber() == 13)
                    && record.getAccountNumber() == null){
                 record.setCurrentAccountBalance(sums.get(2).add(sums.get(8)).add(sums.get(11)).doubleValue());
                 sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && (record.getLineNumber() == 14 || record.getLineNumber() == 25)
                    && record.getAccountNumber() == null){
                 record.setCurrentAccountBalance(sums.get(16).add(sums.get(24)).doubleValue());
                 sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && record.getLineNumber() == 26 && record.getAccountNumber() == null){
                 record.setCurrentAccountBalance(sums.get(13).add(sums.get(25)).doubleValue());
                 sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && (record.getLineNumber() == 27 || record.getLineNumber() == 35) && record.getAccountNumber() == null){
                 record.setCurrentAccountBalance(sums.get(28).add(sums.get(30)).doubleValue());
                 sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && record.getLineNumber() == 51 && record.getAccountNumber() == null){
                 record.setCurrentAccountBalance(sums.get(45).add(sums.get(49)).add(sums.get(50)).doubleValue());
                 sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && record.getLineNumber() == 52 && record.getAccountNumber() == null){
                 record.setCurrentAccountBalance(sums.get(35).add(sums.get(43)).add(sums.get(51)).doubleValue());
                 sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }else if(record.getLineNumber() != null && sums.get(record.getLineNumber()) != null && record.getCurrentAccountBalance() == null &&
                    record.getAccountNumber() == null){
                 record.setCurrentAccountBalance(sums.get(record.getLineNumber()).doubleValue());
                 sums.put(record.getLineNumber(), new BigDecimal(record.getCurrentAccountBalance()));
            }
        }


        return records;
    }

    @Override
    public List<ConsolidatedBalanceFormRecordDto> getConsolidatedIncomeExpenseKZTForm2(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedBalanceKZTForm2Saved(reportId);
        }else{
            List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedIncomeExpenseKZTForm2LineHeaders();

            BigDecimal record6150_030 = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal record7330_030 = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal record6283_080 = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal record7313_010 = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal record7473_080 = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);

//            BigDecimal record6150_030Previous = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
//            BigDecimal record7330_030Previous = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
//            BigDecimal record6283_080Previous = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
//            BigDecimal record7313_010Previous = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);
//            BigDecimal record7473_080Previous = new BigDecimal("0").setScale(2, BigDecimal.ROUND_HALF_UP);

            List<ConsolidatedKZTForm19RecordDto> form19Records = getConsolidatedBalanceKZTForm19(reportId);
            if(form19Records != null){
                for(ConsolidatedKZTForm19RecordDto record: form19Records){
                    if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("6150.030")){
                        record6150_030 = record6150_030.add(new BigDecimal(record.getCurrentAccountBalance()));
                        //record6150_030Previous = record6150_030Previous.add(new BigDecimal(record.getPreviousAccountBalance() != null ? record.getPreviousAccountBalance() : 0));
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("7330.030")){
                        record7330_030 = record7330_030.add(new BigDecimal(record.getCurrentAccountBalance() != null ? record.getCurrentAccountBalance() : 0));
                        //record7330_030Previous = record7330_030Previous.add(new BigDecimal(record.getPreviousAccountBalance() != null ? record.getPreviousAccountBalance() : 0));
                    }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("7313.010")){
                        record7313_010 = record7313_010.add(new BigDecimal(record.getCurrentAccountBalance() != null ? record.getCurrentAccountBalance() : 0));
                        //record7313_010Previous = record7313_010Previous.add(new BigDecimal(record.getPreviousAccountBalance() != null ? record.getPreviousAccountBalance() : 0));
                    }
                }
            }

            List<ConsolidatedKZTForm22RecordDto> form22Records = getConsolidatedBalanceKZTForm22(reportId);
            if(form22Records != null){
                for(ConsolidatedKZTForm22RecordDto record: form22Records) {
                    if (record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("6283.080")) {
                        record6283_080 = record6283_080.add(new BigDecimal(record.getCurrentAccountBalance() != null ? record.getCurrentAccountBalance() : 0));
                        //record6283_080Previous = record6283_080Previous.add(new BigDecimal(record.getPreviousAccountBalance() != null ? record.getPreviousAccountBalance() : 0));
                    } else if (record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("7473.080")) {
                        record7473_080 = record7473_080.add(new BigDecimal(record.getCurrentAccountBalance() != null ? record.getCurrentAccountBalance() : 0));
                        //record7473_080Previous = record7473_080Previous.add(new BigDecimal(record.getPreviousAccountBalance() != null ? record.getPreviousAccountBalance() : 0));
                    }
                }
            }

            double sum = record6150_030.add(record7330_030).add(record6283_080).add(record7313_010).add(record7473_080).doubleValue();
            //double sumPrevious = record6150_030Previous.add(record7330_030Previous).add(record6283_080Previous).add(record7313_010Previous).add(record7473_080Previous).doubleValue();

            for(ConsolidatedBalanceFormRecordDto record: records){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("6150.030") &&
                        record.getLineNumber() != null && record.getLineNumber() == 8){
                    record.setCurrentAccountBalance(record6150_030.doubleValue());
                    //record.setPreviousAccountBalance(record6150_030Previous.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("7330.030") &&
                        record.getLineNumber() != null && record.getLineNumber() == 8){
                    record.setCurrentAccountBalance(record7330_030.doubleValue());
                    //record.setPreviousAccountBalance(record7330_030Previous.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("6283.080") &&
                        record.getLineNumber() != null && record.getLineNumber() == 10){
                    record.setCurrentAccountBalance(record6283_080.doubleValue());
                    //record.setPreviousAccountBalance(record6283_080Previous.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("7313.010") &&
                        record.getLineNumber() != null && record.getLineNumber() == 12){
                    record.setCurrentAccountBalance(record7313_010.doubleValue());
                    //record.setPreviousAccountBalance(record7313_010Previous.doubleValue());
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("7473.080") &&
                        record.getLineNumber() != null && record.getLineNumber() == 15){
                    record.setCurrentAccountBalance(record7473_080.doubleValue());
                    //record.setPreviousAccountBalance(record7473_080Previous.doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 8){
                    record.setCurrentAccountBalance(record6150_030.add(record7330_030).doubleValue());
                    //record.setPreviousAccountBalance(record6150_030Previous.add(record7330_030Previous).doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 10){
                    record.setCurrentAccountBalance(record6283_080.doubleValue());
                    //record.setPreviousAccountBalance(record6283_080Previous.doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 12){
                    record.setCurrentAccountBalance(record7313_010.doubleValue());
                    //record.setPreviousAccountBalance(record7313_010Previous.doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 15){
                    record.setCurrentAccountBalance(record7473_080.doubleValue());
                    //record.setPreviousAccountBalance(record7473_080Previous.doubleValue());
                }else if(record.getAccountNumber() == null && record.getLineNumber() != null &&
                        (record.getLineNumber() == 16 || record.getLineNumber() == 18 || record.getLineNumber() == 20)){
                    record.setCurrentAccountBalance(sum);
                    //record.setPreviousAccountBalance(sumPrevious);
                }
            }

            Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if (previousReport != null) {
                List<ConsolidatedBalanceFormRecordDto> previousRecords = getConsolidatedBalanceKZTForm2Saved(previousReport.getId());
                if(previousRecords != null){
                    for(ConsolidatedBalanceFormRecordDto currentRecord: records){
                        for(ConsolidatedBalanceFormRecordDto prevRecord: previousRecords){
                            if(currentRecord.getName() != null && prevRecord.getName() != null &&
                                    currentRecord.getName().equalsIgnoreCase(prevRecord.getName()) &&
                                    currentRecord.getLineNumber() != null && prevRecord.getLineNumber() != null &&
                                    currentRecord.getLineNumber() == prevRecord.getLineNumber()){
                                currentRecord.setPreviousAccountBalance(prevRecord.getCurrentAccountBalance());
                                break;
                            }
                        }
                    }
                }
            }

            return records;
        }
    }

    @Override
    public List<ConsolidatedBalanceFormRecordDto> getConsolidatedTotalIncomeKZTForm3(Long reportId){
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(report.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedBalanceKZTForm3Saved(reportId);
        }else{
            List<ConsolidatedBalanceFormRecordDto> currentRecords = getConsolidatedTotalIncomeKZTForm3Current(reportId);

            if(currentRecords != null) {
                Date previousDate = DateUtils.getLastDayOfPreviousMonth(report.getReportDate());
                PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
                if (previousReport != null) {
                    List<ConsolidatedBalanceFormRecordDto> previousRecords = getConsolidatedBalanceKZTForm3Saved(previousReport.getId());
                    if (previousRecords != null) {
                        for (ConsolidatedBalanceFormRecordDto currentRecord : currentRecords) {
                            for (ConsolidatedBalanceFormRecordDto prevRecord : previousRecords) {
                                if(currentRecord.getName() != null && currentRecord.getLineNumber() != null &&
                                        prevRecord.getName() != null && prevRecord.getLineNumber() != null &&
                                        currentRecord.getName().equalsIgnoreCase(prevRecord.getName()) &&
                                        currentRecord.getLineNumber() == prevRecord.getLineNumber()){
                                    currentRecord.setPreviousAccountBalance(prevRecord.getCurrentAccountBalance());
                                }
                            }
                        }
                    }
                }
            }

            return currentRecords;
        }
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedTotalIncomeKZTForm3Current(Long reportId) {

        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedTotalIncomeKZTForm3LineHeaders();

        double record1 = 0;

        List<ConsolidatedBalanceFormRecordDto> form2Records = getConsolidatedIncomeExpenseKZTForm2(reportId);
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
        List<ConsolidatedBalanceFormRecordDto> form1Records = getConsolidatedBalanceKZTForm1(reportId);
        if(form1Records != null){
            for(int i = form1Records.size() - 1; i >= 0; i--){
                ConsolidatedBalanceFormRecordDto record = form1Records.get(i);
                if(record.getLineNumber() != null && record.getLineNumber() == 49 &&
                        record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("5440.010")){
                    List<PreviousYearInputDataDto> previousYearInputData = getPreviousYearInputData(reportId);
                    if(previousYearInputData != null){
                        for(PreviousYearInputDataDto previousYearInputDataDto: previousYearInputData){
                            if(previousYearInputDataDto.getAccountBalanceKZT() != null && previousYearInputDataDto.getChartOfAccounts() != null &&
                                    previousYearInputDataDto.getChartOfAccounts().getNBChartOfAccounts().getCode().equalsIgnoreCase("5440.010")) {
                                record3_1 = MathUtils.subtract(record.getCurrentAccountBalance(), previousYearInputDataDto.getAccountBalanceKZT());
                            }
                        }
                    }
                }else if(record.getLineNumber() != null && record.getLineNumber() == 49 &&
                        record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("5450.010")){
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

        return records;
    }

    @Override
    public List<ConsolidatedKZTForm19RecordDto> getConsolidatedBalanceKZTForm19(Long reportId){
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }
        if(currentReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedBalanceKZTForm19Saved(reportId);
        }
        else{
            List<ConsolidatedKZTForm19RecordDto> currentRecords = getConsolidatedKZTForm19Current(reportId);

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

            return currentRecords;
        }

    }

    private List<ConsolidatedKZTForm19RecordDto> getConsolidatedKZTForm19Current(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        Double averageRate = this.currencyRatesService.getAverageRateForDateAndCurrency(report.getReportDate(), "USD", 2);


        BigDecimal record6150_030HF = new BigDecimal("0");
        BigDecimal record6150_030PE = new BigDecimal("0");
        BigDecimal record7330_030HF = new BigDecimal("0");
        BigDecimal record7330_030PE = new BigDecimal("0");
        BigDecimal record7313_010 = new BigDecimal("0");

        List<ConsolidatedBalanceFormRecordDto> incomeExpenseUSDRecords = getConsolidatedIncomeExpenseUSDForm(reportId);
        for(ConsolidatedBalanceFormRecordDto recordDto: incomeExpenseUSDRecords){
            if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase("6150.030") &&
                    recordDto.getOtherEntityName() != null && (recordDto.getOtherEntityName().startsWith("SINGULAR") || recordDto.getOtherEntityName().startsWith("Singular"))){
                record6150_030HF = record6150_030HF.add(new BigDecimal(recordDto.getCurrentAccountBalance()).multiply(new BigDecimal(averageRate)));
            }else if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase("6150.030") &&
                    recordDto.getOtherEntityName() != null && (recordDto.getOtherEntityName().startsWith("TARRAGON") || recordDto.getOtherEntityName().startsWith("Tarragon"))){
                record6150_030PE = record6150_030PE.add(new BigDecimal(recordDto.getCurrentAccountBalance()).multiply(new BigDecimal(averageRate)));
            }else if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase("7330.030") &&
                    recordDto.getOtherEntityName() != null && (recordDto.getOtherEntityName().startsWith("TARRAGON") || recordDto.getOtherEntityName().startsWith("Tarragon"))){
                record7330_030PE = record7330_030PE.add(new BigDecimal(recordDto.getCurrentAccountBalance()).multiply(new BigDecimal(averageRate)));
            }else if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase("7330.030") &&
                    recordDto.getOtherEntityName() != null && (recordDto.getOtherEntityName().startsWith("SINGULAR") || recordDto.getOtherEntityName().startsWith("Singular"))){
                record7330_030HF = record7330_030HF.add(new BigDecimal(recordDto.getCurrentAccountBalance()).multiply(new BigDecimal(averageRate)));
            }else if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase("7313.010")){
                record7313_010 = record7313_010.add(new BigDecimal(recordDto.getCurrentAccountBalance()).multiply(new BigDecimal(averageRate)));
            }
        }

        double sum = record6150_030HF.add(record6150_030PE).add(record7330_030HF).add(record7330_030PE).add(record7313_010).doubleValue();

        List<ConsolidatedKZTForm19RecordDto> records = getConsolidatedBalanceKZTForm19LineHeaders();
        for(ConsolidatedKZTForm19RecordDto record: records){
            if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("6150.030") &&
                    record.getName().equalsIgnoreCase("Инвестиции в хедж-фонд")){
                record.setCurrentAccountBalance(record6150_030HF.doubleValue());
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("6150.030") &&
                    record.getName().equalsIgnoreCase("Инвестиции в фонд частного капитала")){
                record.setCurrentAccountBalance(record6150_030PE.doubleValue());
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("7330.030") &&
                    record.getName().equalsIgnoreCase("Инвестиции в хедж-фонд")){
                record.setCurrentAccountBalance(record7330_030HF.doubleValue());
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("7330.030") &&
                    record.getName().equalsIgnoreCase("Инвестиции в фонд частного капитала")){
                record.setCurrentAccountBalance(record7330_030PE.doubleValue());
            }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("7313.010") &&
                    record.getName().equalsIgnoreCase("Банковские займы полученные")){
                record.setCurrentAccountBalance(record7313_010.doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 32){
                record.setCurrentAccountBalance(record6150_030HF.add(record6150_030PE).add(record7330_030HF).add(record7330_030PE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 34){
                record.setCurrentAccountBalance(record6150_030HF.add(record6150_030PE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 36){
                record.setCurrentAccountBalance(record7330_030HF.add(record7330_030PE).doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 42){
                record.setCurrentAccountBalance(record7313_010.doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 43){
                record.setCurrentAccountBalance(record7313_010.doubleValue());
            }else if(record.getAccountNumber() == null && record.getLineNumber() != null && record.getLineNumber() == 54){
                record.setCurrentAccountBalance(sum);
            }
        }

        return records;
    }

    @Override
    public List<ConsolidatedKZTForm22RecordDto> getConsolidatedBalanceKZTForm22(Long reportId) {
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(currentReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedBalanceKZTForm22Saved(reportId);
        }else{
            List<ConsolidatedKZTForm22RecordDto> currentRecords = getConsolidatedKZTForm22Current(reportId);

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

            return currentRecords;
        }
    }

    private List<ConsolidatedKZTForm22RecordDto> getConsolidatedKZTForm22Current(Long reportId) {
        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        Double averageRate = this.currencyRatesService.getAverageRateForDateAndCurrency(report.getReportDate(), "USD", 2);

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

        List<ConsolidatedBalanceFormRecordDto> incomeExpenseUSDRecords = getConsolidatedIncomeExpenseUSDForm(reportId);
        for(ConsolidatedBalanceFormRecordDto recordDto: incomeExpenseUSDRecords){
            if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase("6283.080")){
                ConsolidatedKZTForm22RecordDto newRecord = new ConsolidatedKZTForm22RecordDto(recordDto.getName(), 1);
                newRecord.setAccountNumber("6283.080");
                newRecord.setCurrentAccountBalance(convertByRate(recordDto.getCurrentAccountBalance(), averageRate));
                records.add(index1, newRecord);

                index1++;
                index2++;
                record1Sum = record1Sum.add(new BigDecimal(newRecord.getCurrentAccountBalance()));
            }else if(recordDto.getAccountNumber() != null && recordDto.getAccountNumber().equalsIgnoreCase("7473.080")){
                ConsolidatedKZTForm22RecordDto newRecord = new ConsolidatedKZTForm22RecordDto(recordDto.getName(), 2);
                newRecord.setAccountNumber("7473.080");
                newRecord.setCurrentAccountBalance(convertByRate(recordDto.getCurrentAccountBalance(), averageRate));
                records.add(index2, newRecord);

                index1++;
                index2++;
                record2Sum = record2Sum.add(new BigDecimal(newRecord.getCurrentAccountBalance()));
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

        return records;
    }

    private Double getCurrencyReserve(Long reportId, Date reportDate){
        BigDecimal total = new BigDecimal("0");

        Date nextDay = DateUtils.getNextDay(reportDate);
        CurrencyRatesDto currencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(nextDay, "USD");
        Double avgCurrencyRateForReportDate = this.currencyRatesService.getAverageRateForDateAndCurrency(DateUtils.getNextDay(reportDate), "USD", 2);

        List<ReserveCalculationDto> reserveCalculations = this.reserveCalculationService.getReserveCalculationsByExpenseType("ADD");
        if(reserveCalculations != null){
            BigDecimal sumKZTInitial = new BigDecimal("0");
            BigDecimal sumKZTOnReportDate = new BigDecimal("0");
            for(ReserveCalculationDto reserveCalculationDto: reserveCalculations){
                sumKZTInitial = sumKZTInitial.add(new BigDecimal(reserveCalculationDto.getAmountKZT() != null ? reserveCalculationDto.getAmountKZT().doubleValue() : 0));
                BigDecimal calculatedAmount = reserveCalculationDto.getAmount() != null ?
                        new BigDecimal(reserveCalculationDto.getAmount().doubleValue()).multiply(new BigDecimal(currencyRatesDto.getValue())) : new BigDecimal("0");
                sumKZTOnReportDate = sumKZTOnReportDate.add(calculatedAmount);
            }
            total = total.add(sumKZTOnReportDate).subtract(sumKZTInitial);
        }

        int currentYear = DateUtils.getYear(reportDate);
        for(int year = 2015; year < currentYear; year++){
            PeriodicDataDto netProfitDto = this.periodicDataService.get(DateUtils.getDate("31.12." + year), "NET_PROFIT");
            PeriodicDataDto reserveRevalutionDto = this.periodicDataService.get(DateUtils.getDate("31.12." + year), "RSRV_REVAL");

            // TODO: ?
            int scale = year == 2015 ? 4 : 2;
            Double avgCurrencyRate = this.currencyRatesService.getAverageRateForDateAndCurrency(DateUtils.getDate("31.12." + year), "USD", scale);

            if(netProfitDto != null){
                total = total.add(new BigDecimal(netProfitDto.getValue()).multiply(new BigDecimal(currencyRatesDto.getValue())));
                total = total.subtract(new BigDecimal(netProfitDto.getValue()).multiply(new BigDecimal(avgCurrencyRate)));
            }
            if(reserveRevalutionDto != null){
                total = total.add(new BigDecimal(reserveRevalutionDto.getValue()).multiply(new BigDecimal(currencyRatesDto.getValue())));
                total = total.subtract(new BigDecimal(reserveRevalutionDto.getValue()).multiply(new BigDecimal(avgCurrencyRate)));
            }
        }

        List<ConsolidatedBalanceFormRecordDto> incomeExpenseRecords = getConsolidatedIncomeExpenseUSDForm(reportId);
        for(int i = incomeExpenseRecords.size() - 1; i >= 0; i--){
            if(incomeExpenseRecords.get(i).getLineNumber() != null && incomeExpenseRecords.get(i).getLineNumber() == 20 && incomeExpenseRecords.get(i).getCurrentAccountBalance() != null){
                total = total.add(new BigDecimal(incomeExpenseRecords.get(i).getCurrentAccountBalance()).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(currencyRatesDto.getValue())));
                total = total.subtract(new BigDecimal(incomeExpenseRecords.get(i).getCurrentAccountBalance()).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(avgCurrencyRateForReportDate)));
                break;
            }
        }

        List<ConsolidatedBalanceFormRecordDto> balanceRecords = getConsolidatedBalanceUSDForm(reportId);
        for(int i = balanceRecords.size() - 1; i >= 0; i--){
            if(balanceRecords.get(i).getCurrentAccountBalance() != null && balanceRecords.get(i).getLineNumber() != null && balanceRecords.get(i).getLineNumber() == 49 &&
                    balanceRecords.get(i).getAccountNumber().equalsIgnoreCase("5440.010") &&
                    balanceRecords.get(i).getName().equalsIgnoreCase("Резерв по переоценке финансовых инвестиций, имеющихся в наличии для продажи")){
                total = total.add(new BigDecimal(balanceRecords.get(i).getCurrentAccountBalance()).multiply(new BigDecimal(currencyRatesDto.getValue())));
                total = total.subtract(new BigDecimal(balanceRecords.get(i).getCurrentAccountBalance()).multiply(new BigDecimal(avgCurrencyRateForReportDate)));
                break;
            }
        }

        return total.doubleValue();
    }

    @Override
    public List<ReserveCalculationDto> getReserveCalculation() {
        List<ReserveCalculationDto> records = this.reserveCalculationService.getReserveCalculations();
        return records;
    }

    @Override
    public boolean saveReserveCalculation(List<ReserveCalculationDto> records) {
        boolean saved = this.reserveCalculationService.save(records);
        return saved;
    }

    private void addValuesKZTForm7(ConsolidatedKZTForm7RecordDto totalRecord, ConsolidatedKZTForm7RecordDto record){
        double debtTurnover = totalRecord.getDebtTurnover() != null ? totalRecord.getDebtTurnover().doubleValue() : 0;
        debtTurnover += (record.getDebtTurnover() != null ? record.getDebtTurnover().doubleValue() : 0);
        totalRecord.setDebtTurnover(debtTurnover);

        double totalFairValueAdjustmentsTurnoverPositive = totalRecord.getFairValueAdjustmentsTurnoverPositive() != null ? totalRecord.getFairValueAdjustmentsTurnoverPositive().doubleValue() : 0;
        totalFairValueAdjustmentsTurnoverPositive += (record.getFairValueAdjustmentsTurnoverPositive() != null ? record.getFairValueAdjustmentsTurnoverPositive().doubleValue() : 0);
        totalRecord.setFairValueAdjustmentsTurnoverPositive(totalFairValueAdjustmentsTurnoverPositive);

        double totalFairValueAdjustmentsTurnoverNegative = totalRecord.getFairValueAdjustmentsTurnoverNegative() != null ? totalRecord.getFairValueAdjustmentsTurnoverNegative().doubleValue() : 0;
        totalFairValueAdjustmentsTurnoverNegative += (record.getFairValueAdjustmentsTurnoverNegative() != null ? record.getFairValueAdjustmentsTurnoverNegative().doubleValue() : 0);
        totalRecord.setFairValueAdjustmentsTurnoverNegative(totalFairValueAdjustmentsTurnoverNegative);

        double debtEndPeriod = totalRecord.getDebtEndPeriod() != null ? totalRecord.getDebtEndPeriod().doubleValue() : 0;
        debtEndPeriod += (record.getDebtEndPeriod() != null ? record.getDebtEndPeriod().doubleValue() : 0);
        totalRecord.setDebtEndPeriod(debtEndPeriod);

        double fairValueAdjustmentsEndPeriod = totalRecord.getFairValueAdjustmentsEndPeriod() != null ? totalRecord.getFairValueAdjustmentsEndPeriod().doubleValue() : 0;
        fairValueAdjustmentsEndPeriod += (record.getFairValueAdjustmentsEndPeriod() != null ? record.getFairValueAdjustmentsEndPeriod().doubleValue() : 0);
        totalRecord.setFairValueAdjustmentsEndPeriod(fairValueAdjustmentsEndPeriod);

        double totalEndPeriod = totalRecord.getTotalEndPeriod() != null ? totalRecord.getTotalEndPeriod().doubleValue() : 0;
        totalEndPeriod += (record.getTotalEndPeriod() != null ? record.getTotalEndPeriod().doubleValue() : 0);
        totalRecord.setTotalEndPeriod(totalEndPeriod);
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

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceKZTForm3Saved(Long reportId){
        List<ConsolidatedReportKZTForm3> entities = this.consolidatedReportKZTForm3Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedBalanceFormRecordDto> dtoList = this.consolidatedKZTForm3Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceKZTForm2Saved(Long reportId){
        List<ConsolidatedReportKZTForm2> entities = this.consolidatedReportKZTForm2Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedBalanceFormRecordDto> dtoList = this.consolidatedKZTForm2Converter.disassembleList(entities);

        return dtoList;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceKZTForm1Saved(Long reportId){
        List<ConsolidatedReportKZTForm1> entities = this.consolidatedReportKZTForm1Repository.getEntitiesByReportId(reportId);
        List<ConsolidatedBalanceFormRecordDto> dtoList = this.consolidatedKZTForm1Converter.disassembleList(entities);

        return dtoList;
    }

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

    private List<ConsolidatedKZTForm7RecordDto> getConsolidatedBalanceKZTForm7CurrentPeriod(Long reportId) {

        PeriodicReport report = this.periodReportRepository.findOne(reportId);
        if(report == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        Date rateDate = DateUtils.getFirstDayOfNextMonth(report.getReportDate());
        // Find exchange rate
        CurrencyRatesDto endCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(rateDate, "USD");
        if(endCurrencyRatesDto == null){
            logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate));

            // TODO: return error message
            return null;
        }

        Date previousRateDate = DateUtils.getFirstDayOfCurrentMonth(report.getReportDate());
        CurrencyRatesDto startCurrencyRatesDto = this.currencyRatesService.getRateForDateAndCurrency(previousRateDate, "USD");
        if(startCurrencyRatesDto == null){
            logger.error("No currency rate found for date '" + DateUtils.getDateFormatted(rateDate));

            // TODO: return error message
            return null;
        }


        int index = 0;
        List<ConsolidatedKZTForm7RecordDto> records = getConsolidatedBalanceKZTForm7LineHeaders();
        for(int i = 0; i < records.size(); i++){
            ConsolidatedKZTForm7RecordDto record = records.get(i);
            if(record.getLineNumber() == 10){
                index = i;
                break;
            }
        }

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
                        investmentsDto.getTotalSum() != null && !investmentsDto.getTotalSum().booleanValue()) {
                    if (investmentsDto.getTranche() == 1) {
                        fundTurnoverPE.put(investmentsDto.getName(), investmentsDto.getNetCost() * 0.99);
                    } else {
                        fundTurnoverPE.put(investmentsDto.getName(), investmentsDto.getNetCost());
                    }
                }
            }
        }
        List<ScheduleInvestmentsDto> currentPeriodInvestments = this.scheduleInvestmentService.getScheduleInvestments(report.getId());
        if(currentPeriodInvestments != null) {
            for (ScheduleInvestmentsDto investmentsDto: currentPeriodInvestments) {
                if(investmentsDto.getNetCost() != null && investmentsDto.getNetCost().doubleValue() != 0 &&
                        investmentsDto.getTotalSum() != null && !investmentsDto.getTotalSum().booleanValue()) {
                    double previousValue = fundTurnoverPE.get(investmentsDto.getName()) != null ? fundTurnoverPE.get(investmentsDto.getName()).doubleValue() : 0;
                    double currentValue = investmentsDto.getNetCost() != null ? investmentsDto.getNetCost().doubleValue() : 0;
                    if (investmentsDto.getTranche() == 1) {
                        currentValue = currentValue * 0.99;
                    }
                    fundTurnoverPE.put(investmentsDto.getName(), currentValue - previousValue);
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
            if(noalRecordDto.getTransaction() != null && noalRecordDto.getTransaction().equalsIgnoreCase("Portfolio Fund Redemption/Withdrawal - Corp") &&
                    noalRecordDto.getName() != null &&DateUtils.getDateFormatted(noalRecordDto.getDate()).equalsIgnoreCase(DateUtils.getDateFormatted(date))){
                double value = fundTurnoverHF.get(noalRecordDto.getName()) != null ? fundTurnoverHF.get(noalRecordDto.getName()).doubleValue() : 0;
                value += noalRecordDto.getTransactionAmount() != null ? noalRecordDto.getTransactionAmount().doubleValue() : 0;
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
                    if(prevNoalRecordDto.getTransaction() != null && prevNoalRecordDto.getTransaction().equalsIgnoreCase("Portfolio Fund Redemption/Withdrawal - Corp") &&
                            currentNoalRecordDto.getTransaction() != null && currentNoalRecordDto.getTransaction().equalsIgnoreCase("Portfolio Fund Redemption/Withdrawal - Corp") &&
                            prevNoalRecordDto.getName() != null && prevNoalRecordDto.getName().equalsIgnoreCase(currentNoalRecordDto.getName()) &&
                            DateUtils.getDateFormatted(currentNoalRecordDto.getDate()).equalsIgnoreCase(DateUtils.getDateFormatted(date))){
                        double prevValue = prevNoalRecordDto.getTransactionAmount() != null ? prevNoalRecordDto.getTransactionAmount().doubleValue() : 0;
                        double currValue = currentNoalRecordDto.getTransactionAmount() != null ? currentNoalRecordDto.getTransactionAmount().doubleValue() : 0;
                        if(prevValue != currValue){
                            double value = fundTurnoverHF.get(currentNoalRecordDto.getName()) != null ? fundTurnoverHF.get(currentNoalRecordDto.getName()).doubleValue() : 0;
                            value += currValue - prevValue;
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
                if(record.getChartAccountsDescription() != null && record.getChartAccountsDescription().equalsIgnoreCase("Net Realized Gains/Losses")){
                    //String fundName = record.getChartAccountsLongDescription().substring("Net Realized Gains/Losses from Portfolio Funds".length()).trim();
                    String fundName = record.getShortName();

                    double value = fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0;
                    value -= (record.getGLAccountBalance() != null ? record.getGLAccountBalance().doubleValue() : 0);
                    fundTurnoverHF.put(fundName, value);
                }
            }
        }

        ConsolidatedReportRecordHolderDto previousDataHolder = this.generalLedgerBalanceService.get(previousReport.getId());
        if(previousDataHolder != null && previousDataHolder.getGeneralLedgerBalanceList() != null){
            for(SingularityGeneralLedgerBalanceRecordDto record: previousDataHolder.getGeneralLedgerBalanceList()){
                if(record.getChartAccountsDescription() != null && record.getChartAccountsDescription().equalsIgnoreCase("Net Realized Gains/Losses")){
//                    String fundName = record.getChartAccountsLongDescription().substring("Net Realized Gains/Losses from Portfolio Funds".length()).trim();
                    String fundName = record.getShortName() != null ? record.getShortName() : "";

                    double value = fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0;
                    value += (record.getGLAccountBalance() != null ? record.getGLAccountBalance().doubleValue() : 0);
                    fundTurnoverHF.put(fundName, value);
                }
            }
        }


        List<ConsolidatedBalanceFormRecordDto> USDFormRecords = getConsolidatedBalanceUSDForm(report.getId());
        if(records != null && index > 0){
            for(ConsolidatedBalanceFormRecordDto record: USDFormRecords){
                if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("1283.020")
                        && record.getOtherEntityName() != null && record.getOtherEntityName().startsWith("SINGULAR")){
                    if (record.getName().startsWith("Предварительная подписка")) {
                        String fundName = record.getName().substring("Предварительная подписка".length()).trim();

                        double value = fundTurnoverPE.get(fundName) != null ? fundTurnoverPE.get(fundName).doubleValue() :
                                fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0;
                        value += record.getPreviousAccountBalance() != null ? record.getPreviousAccountBalance().doubleValue() : 0;

                        if(record.getOtherEntityName().startsWith("TARRAGON")) {
                            fundTurnoverPE.put(fundName, value);
                        }else if(record.getOtherEntityName().startsWith("SINGULAR")){
                            fundTurnoverHF.put(fundName, value);
                        }
                    }
                }else if(record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("2033.010")){
                    String name = null;
                    if(record.getOtherEntityName() != null && record.getOtherEntityName().startsWith("TARRAGON")){
                        name = "Инвестиции в фонд частного капитала";
                    }else if(record.getOtherEntityName() != null && record.getOtherEntityName().startsWith("SINGULAR")){
                        name = "Инвестиции в хедж-фонд";
                    }else{

                        // TODO: error?
                        return null;
                    }
                    int fundNameIndex = name.equalsIgnoreCase("Инвестиции в фонд частного капитала") ? "Инвестиции в фонд частного капитала".length() :
                            "Инвестиции в фонд".length();
                    String fundName = record.getName().substring(fundNameIndex).trim();

                    ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
                    if(record.getPreviousAccountBalance() != null && record.getPreviousAccountBalance().doubleValue() != 0 &&
                            (record.getCurrentAccountBalance() == null || record.getCurrentAccountBalance().doubleValue() == 0)){
                        newRecord.setBecameZero(true);
                    }
                    newRecord.setName(name);
                    newRecord.setAccountNumber(record.getAccountNumber());
                    newRecord.setLineNumber(9);
                    newRecord.setEntityName(fundName);
                    newRecord.setOtherName(record.getOtherEntityName());
                    //newRecord.setPurchaseDate();

                    newRecord.setTotalEndPeriod(convertByRate(record.getCurrentAccountBalance() != null ? record.getCurrentAccountBalance() : 0.0, endCurrencyRatesDto.getValue()));

                    if(record.getOtherEntityName().startsWith("TARRAGON")) {
                        newRecord.setDebtTurnover(convertByRate((fundTurnoverPE.get(fundName) != null ? fundTurnoverPE.get(fundName).doubleValue() : 0), endCurrencyRatesDto.getValue()));
                        fundTurnoverPE.put(fundName, null);
                    }else if(record.getOtherEntityName().startsWith("SINGULAR")){
                        newRecord.setDebtTurnover(convertByRate((fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0), endCurrencyRatesDto.getValue()));
                        fundTurnoverHF.put(fundName, null);
                    }

                    records.add(index, newRecord);

                    index ++;

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
                    String name = "Инвестиции в фонд частного капитала";
                    ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
                    newRecord.setName(name);
                    newRecord.setAccountNumber("2033.010");
                    newRecord.setLineNumber(9);
                    newRecord.setEntityName(fundName);
                    newRecord.setOtherName("TARRAGON");
                    //newRecord.setPurchaseDate();

                    newRecord.setTotalEndPeriod(0.0);
                    newRecord.setDebtTurnover(convertByRate((fundTurnoverPE.get(fundName) != null ? fundTurnoverPE.get(fundName).doubleValue() : 0), endCurrencyRatesDto.getValue()));

                    records.add(index, newRecord);

                    index ++;
                }
            }
        }

        Set<String> keySet2 = fundTurnoverHF.keySet();
        if(keySet != null){
            Iterator<String> iterator = keySet2.iterator();
            while(iterator.hasNext()){
                String fundName = iterator.next();
                if(fundTurnoverHF.get(fundName) != null) {
                    String name = "Инвестиции в хэдж-фонд";
                    ConsolidatedKZTForm7RecordDto newRecord = new ConsolidatedKZTForm7RecordDto();
                    newRecord.setName(name);
                    newRecord.setAccountNumber("2033.010");
                    newRecord.setLineNumber(9);
                    newRecord.setEntityName(fundName);
                    newRecord.setOtherName("SINGULAR");
                    //newRecord.setPurchaseDate();

                    newRecord.setTotalEndPeriod(0.0);
                    newRecord.setDebtTurnover(convertByRate((fundTurnoverHF.get(fundName) != null ? fundTurnoverHF.get(fundName).doubleValue() : 0), endCurrencyRatesDto.getValue()));

                    records.add(index, newRecord);

                    index ++;
                }
            }
        }

        return records;
    }

    private Double convertByRate(Double value, Double rate){
        if(value == null || rate == null){
            return null;
        }
        if(value == 0.0){
            return 0.0;
        }

        BigDecimal calculated = new BigDecimal(value);
        calculated = calculated.multiply(new BigDecimal(rate)).setScale(2, RoundingMode.HALF_UP);
        //calculated = calculated.multiply(new BigDecimal(rate));
        return calculated.doubleValue();
    }

    private List<ConsolidatedKZTForm10RecordDto> getConsolidatedBalanceKZTForm10LineHeaders(){
        List<ConsolidatedKZTForm10RecordDto> records = new ArrayList<>();
        records.add(new ConsolidatedKZTForm10RecordDto("Прочие краткосрочные активы (сумма строк 2-3)", 1));
        records.add(new ConsolidatedKZTForm10RecordDto("Расходы будущих периодов", 2));
        records.add(new ConsolidatedKZTForm10RecordDto("Прочие краткосрочные активы", 3));
        records.add(new ConsolidatedKZTForm10RecordDto("Прочие долгосрочные активы (сумма строк 5-10 )", 4));
        records.add(new ConsolidatedKZTForm10RecordDto("Инвестиции в недвижимость", 5));
        records.add(new ConsolidatedKZTForm10RecordDto("Биологические активы", 6));
        records.add(new ConsolidatedKZTForm10RecordDto("Разведочные и оценочные активы", 7));
        records.add(new ConsolidatedKZTForm10RecordDto("Расходы будущих периодов", 8));
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
        records.add(new ConsolidatedKZTForm8RecordDto("Прочая краткосрочная дебиторская задолженность", 9));
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
        records.add(new ConsolidatedKZTForm14RecordDto("Прочая краткосрочная кредиторская задолженность", 8));
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

        ConsolidatedBalanceFormRecordDto record2 = new ConsolidatedBalanceFormRecordDto("Деньги на текущих счетах", 2);
        record2.setAccountNumber("1033.010");
        headers.add(record2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Вклады размещенные (за вычетом резервов на обесценение) (1150.020-1150.100, 1160.070, 1160.080, 1270.090-1270.110, 1290.070, 1290.090)", 3));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, оцениваемые по справедливой стоимости, изменения которой отражаются в составе прибыли или убытка (1120, 1270.020, 1270.050)", 4));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, имеющиеся в наличии для продажи (за вычетом резервов на обесценение) (1140, 1160.050, 1160.060, 1270.040, 1270.070, 1290.050)", 5));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, удерживаемые до погашения (за вычетом резервов на обесценение) (1130, 1160.030, 1160.040, 1270.030, 1270.060, 1290.030)", 6));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные финансовые инвестиции (1110, 1150.010, 1150.110-1150.140, 1160.010, 1160.020, 1160.090, 1270.010, 1270.080, 1270.120, 1270.130, 1280.010, 1290.010, 1290.110, 1290.130)", 7));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочная торговая и прочая дебиторская задолженность (1210-1260, 1280.020, 1290.130, 1610)", 8));

        ConsolidatedBalanceFormRecordDto record8 = new ConsolidatedBalanceFormRecordDto("Прочая краткосрочная дебиторская задолженность", 8);
        record8.setAccountNumber("1283.020");
        headers.add(record8);

        headers.add(new ConsolidatedBalanceFormRecordDto("Текущие налоговые активы (1410-1430)", 9));
        headers.add(new ConsolidatedBalanceFormRecordDto("Запасы (1310-1360)", 10));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные активы (1620, 1630)", 11));

        ConsolidatedBalanceFormRecordDto record11 = new ConsolidatedBalanceFormRecordDto("Расходы будущих периодов", 11);
        record11.setAccountNumber("1623.010");
        headers.add(record11);

        headers.add(new ConsolidatedBalanceFormRecordDto("Активы (или выбывающие группы), предназначенные для продажи (1510-1520)", 12));
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого краткосрочных активов (сумма строк 2-12)", 13));
        headers.add(new ConsolidatedBalanceFormRecordDto("Долгосрочные активы", 14));
        headers.add(new ConsolidatedBalanceFormRecordDto("Вклады размещенные (за вычетом резервов на обесценение) (2040.010-2040.060, 2050.070, 2050.080, 2170.060, 2170.070, 1290.080, 1290.100)", 15));
        headers.add(new ConsolidatedBalanceFormRecordDto("Финансовые инвестиции, имеющиеся в наличии для продажи (за вычетом резервов на обесценение) (2030, 2050.050, 2050.060, 2170.030, 2170,050, 1290.060)", 16));

        ConsolidatedBalanceFormRecordDto record16_1 = new ConsolidatedBalanceFormRecordDto("Долгосрочные ценные бумаги, имеющиеся в наличии для продажи", 16);
        record16_1.setAccountNumber("2033.010");
        headers.add(record16_1);

        ConsolidatedBalanceFormRecordDto record16_2 = new ConsolidatedBalanceFormRecordDto("Положительная корректировка справедливой стоимости долгосрочных ценных бумаг, имеющихся в наличии для продажи", 16);
        record16_2.setAccountNumber("2033.040");
        headers.add(record16_2);

        ConsolidatedBalanceFormRecordDto record16_3 = new ConsolidatedBalanceFormRecordDto("Отрицательная корректировка справедливой стоимости долгосрочных ценных бумаг, имеющихся в наличии для продажи", 16);
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

        ConsolidatedBalanceFormRecordDto record24 = new ConsolidatedBalanceFormRecordDto("Расходы будущих периодов", 24);
        record24.setAccountNumber("2923.010");
        headers.add(record24);

        headers.add(new ConsolidatedBalanceFormRecordDto("Итого долгосрочных активов (сумма строк 15-24)", 25));
        headers.add(new ConsolidatedBalanceFormRecordDto("Всего активы (сумма строк 13, 25)", 26));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочные обязательства", 27));
        headers.add(new ConsolidatedBalanceFormRecordDto("Займы полученные (3010, 3020, 3380.010, 3380.020)", 28));

        ConsolidatedBalanceFormRecordDto record28_1 = new ConsolidatedBalanceFormRecordDto("Краткосрочные банковские займы полученные", 28);
        record28_1.setAccountNumber("3013.010");
        headers.add(record28_1);

        ConsolidatedBalanceFormRecordDto record28_2 = new ConsolidatedBalanceFormRecordDto("Начисленные расходы в виде вознаграждения по краткосрочным банковским займам полученным", 28);
        record28_2.setAccountNumber("3383.010");
        headers.add(record28_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие краткосрочные финансовые обязательства (3040, 3050, 3380.030-3380.050, 3390.010)", 29));
        headers.add(new ConsolidatedBalanceFormRecordDto("Краткосрочная торговая и прочая кредиторская задолженность (3310-3340, 3360, 3390.020, 3510)", 30));

        ConsolidatedBalanceFormRecordDto record30 = new ConsolidatedBalanceFormRecordDto("Прочая краткосрочная кредиторская задолженность", 30);
        record30.setAccountNumber("3393.020");
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

        ConsolidatedBalanceFormRecordDto record45_1 = new ConsolidatedBalanceFormRecordDto("Простые акции", 45);
        record45_1.setAccountNumber("5021.010");
        headers.add(record45_1);

        ConsolidatedBalanceFormRecordDto record45_2 = new ConsolidatedBalanceFormRecordDto("Простые акции", 45);
        record45_2.setAccountNumber("5022.010");
        headers.add(record45_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Эмиссионный доход (5310)", 46));
        headers.add(new ConsolidatedBalanceFormRecordDto("Выкупленные собственные долевые инструменты (5210)", 47));
        headers.add(new ConsolidatedBalanceFormRecordDto("Резервный капитал (5410, 5460)", 48));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие резервы (5420-5450)", 49));

        ConsolidatedBalanceFormRecordDto record49_1 = new ConsolidatedBalanceFormRecordDto("Резерв на переоценку финансовых инвестиций, имеющихся в наличии для продажи", 49);
        record49_1.setAccountNumber("5440.010");
        headers.add(record49_1);

        ConsolidatedBalanceFormRecordDto record49_2 = new ConsolidatedBalanceFormRecordDto("Резерв на пересчет иностранной валюты по зарубежной деятельности", 49);
        record49_2.setAccountNumber("5450.010");
        headers.add(record49_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Нераспределенная прибыль (непокрытый убыток) (5510, 5520)", 50));


        ConsolidatedBalanceFormRecordDto record50_1 = new ConsolidatedBalanceFormRecordDto("Нераспределенная прибыль (непокрытый убыток) отчетного года", 50);
        record50_1.setAccountNumber("5510.010");
        headers.add(record50_1);

        ConsolidatedBalanceFormRecordDto record50_2 = new ConsolidatedBalanceFormRecordDto("Нераспределенная прибыль (непокрытый убыток) прошлых лет", 50);
        record50_2.setAccountNumber("5520.010");
        headers.add(record50_2);

        headers.add(new ConsolidatedBalanceFormRecordDto("Итого капитал (сумма строк 45-50)", 51));
        headers.add(new ConsolidatedBalanceFormRecordDto("Всего обязательства и капитал (сумма строк 35, 43, 51)", 52));
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
        records.add(new ConsolidatedKZTForm19RecordDto("Доходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи", 34));

        ConsolidatedKZTForm19RecordDto record6150_030HF = new ConsolidatedKZTForm19RecordDto("Инвестиции в хедж-фонд", 34);
        record6150_030HF.setAccountNumber("6150.030");
        record6150_030HF.setOtherEntityName("Singularity");
        records.add(record6150_030HF);

        ConsolidatedKZTForm19RecordDto record6150_030PE = new ConsolidatedKZTForm19RecordDto("Инвестиции в фонд частного капитала", 34);
        record6150_030PE.setAccountNumber("6150.030");
        record6150_030PE.setOtherEntityName("Tarragon");
        records.add(record6150_030PE);

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от изменения справедливой стоимости краткосрочных финансовых инвестиций, имеющихся в наличии для продажи", 35));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи", 36));

        ConsolidatedKZTForm19RecordDto record7330_030HF = new ConsolidatedKZTForm19RecordDto("Инвестиции в хедж-фонд", 36);
        record7330_030HF.setAccountNumber("7330.030");
        record7330_030HF.setOtherEntityName("Singularity");
        records.add(record7330_030HF);

        ConsolidatedKZTForm19RecordDto record7330_030PE = new ConsolidatedKZTForm19RecordDto("Инвестиции в фонд частного капитала", 36);
        record7330_030PE.setAccountNumber("7330.030");
        record7330_030PE.setOtherEntityName("Tarragon");
        records.add(record7330_030PE);

        records.add(new ConsolidatedKZTForm19RecordDto("Расходы в виде вознаграждения по приобретенным ценным бумагам (сумма строк 38-41) ", 37));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным краткосрочным финансовым инвестициям, удерживаемым до погашения", 38));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным долгосрочным финансовым инвестициям, удерживаемым до погашения", 39));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным краткосрочным финансовым инвестициям, имеющимся в наличии для продажи", 40));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по амортизации премии по приобретенным долгосрочным финансовым инвестициям, имеющимся в наличии для продажи", 41));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы в виде вознаграждения по полученным займам и финансовой аренде (сумма строк 43-53)", 42));
        records.add(new ConsolidatedKZTForm19RecordDto("Расходы по вознаграждениям по краткосрочным банковским займам", 43));

        ConsolidatedKZTForm19RecordDto record7313_010 = new ConsolidatedKZTForm19RecordDto("Банковские займы полученные", 43);
        record7313_010.setAccountNumber("7313.010");
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
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого совокупного дохода (сумма строк 1, 5)", 7));

        return headers;
    }

    private Map<Integer, List<ConsolidatedBalanceFormRecordDto>> getConsolidatedBalanceUSDFormMap(Long reportId){

        // TODO: return map from data, not from list ??
        // TODO: or OK?
        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedBalanceUSDForm(reportId);

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
        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedIncomeExpenseUSDForm(reportId);

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

    private InputStream getConsolidatedBalanceUSDReportInputStream(Long reportId){
        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = getConsolidatedBalanceUSDFormMap(reportId);

        String tempaltePath = "C:\\Users\\magzumov\\Desktop\\TEMPLATE_ NICKMF_cons_USD.xlsx";

        InputStream ExcelFileToRead = null;
        try {
            ExcelFileToRead = new FileInputStream(tempaltePath);
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
                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "Всего обязательства и капитал (сумма строк 35, 43, 51)")) {
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
                }
                if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), "Номер группы субсчетов") &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), "Наименование группы субсчетов") &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), "Код строки")){
                    startOfTable = true;
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private InputStream getConsolidatedIncomeExpenseUSDReportInputStream(Long reportId){
        Map<Integer, List<ConsolidatedBalanceFormRecordDto>> recordsMap = getConsolidatedIncomeExpenseUSDFormMap(reportId);

        String tempaltePath = "C:\\Users\\magzumov\\Desktop\\TEMPLATE_ NICKMF_cons_USD_опиу.xlsx";

        InputStream ExcelFileToRead = null;
        try {
            ExcelFileToRead = new FileInputStream(tempaltePath);
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
                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "Чистая прибыль (убыток) (сумма строк 18, 19)")) {
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
                }
                if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), "Номер группы субсчетов") &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), "Наименование группы субсчетов") &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), "Код строки")){
                    startOfTable = true;
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private InputStream getConsolidatedTotalIncomeUSDReportInputStream(Long reportId){
        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedTotalIncomeUSDForm(reportId);

        String tempaltePath = "C:\\Users\\magzumov\\Desktop\\TEMPLATE_ NICKMF_cons_USD_опсд.xlsx";

        InputStream ExcelFileToRead = null;
        try {
            ExcelFileToRead = new FileInputStream(tempaltePath);
            XSSFWorkbook  workbook = new XSSFWorkbook(ExcelFileToRead);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            boolean startOfTable = false;
            boolean endOfTable = false;
            while (rowIterator.hasNext() && !endOfTable) { // each row
                Row row = rowIterator.next();
                if (startOfTable) {
                    Cell cell = row.getCell(1);
                    if (cell != null && ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "Итого совокупного дохода (сумма строк 1, 5)")) {
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

                }
                if(ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(0), "Наименование показателей") &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(1), "Код строки") &&
                        ExcelUtils.isCellStringValueEqualIgnoreCase(row.getCell(2), "На отчетную дату текущего периода")){
                    startOfTable = true;
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean deleteParsedFileData(Long reportId, String fileType){

        if (fileType.equalsIgnoreCase("NB_REP_T1")){
            return this.scheduleInvestmentService.deleteByReportId(reportId);
        }else if(fileType.equalsIgnoreCase("NB_REP_T2")){
            boolean deleted = this.statementBalanceService.deleteByReportId(reportId);
            if(deleted) {
                return this.statementOperatinsService.deleteByReportId(reportId);
            }else {


                // TODO: recover from failure
                return false;

            }
        }else if(fileType.equalsIgnoreCase("NB_REP_T3")){
            return this.statementCashflowsService.deleteByReportId(reportId);
        }else if(fileType.equalsIgnoreCase("NB_REP_T4")){
            return this.statementChangesService.deleteByReportId(reportId);
        }else if(fileType.equalsIgnoreCase("NB_REP_SGL")){
            return this.generalLedgerBalanceService.deleteByReportId(reportId);
        }else if (fileType.equalsIgnoreCase("NB_REP_SNA")) {
            return this.hfNOALService.deleteByReportId(reportId, 1);
        }else if(fileType.equalsIgnoreCase("NB_REP_SNB")) {
            return this.hfNOALService.deleteByReportId(reportId, 2);
        }

        return false;
    }

    // TODO: refactor as common lookup converter
    private BaseDictionaryDto disassemble(BaseTypeEntity entity){
        BaseDictionaryDto dto = new BaseDictionaryDto();
        dto.setCode(entity.getCode());
        dto.setNameEn(entity.getNameEn());
        dto.setNameRu(entity.getNameRu());
        dto.setNameKz(entity.getNameKz());
        return dto;
    }

    /**
     *
     * @param reportId
     * @param filesDto
     * @return
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
        }else if(fileType.equals(FileTypeLookup.NB_REP_SINGULAR_GENERAL_LEDGER.getCode())){
            return parseSingularGeneralLedger(filesDto, reportId);
        }else if(fileType.equals(FileTypeLookup.NB_REP_SN_TRANCHE_A.getCode())){
            return parseSingularNOAL(filesDto, reportId, 1);
        }else if(fileType.equals(FileTypeLookup.NB_REP_SN_TRANCHE_B.getCode())){
            return parseSingularNOAL(filesDto, reportId, 2);
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
    public boolean saveOtherInfo(ReportOtherInfoDto dto) {
        try {
            ReportOtherInfo entity = this.reportingOtherInfoConverter.assemble(dto);
            this.reportingOtherInfoRepository.save(entity);
            return true;
        }catch (Exception ex){
            logger.error("Error saving other info.");
        }
        return false;
    }


    @Transactional
    @Override
    public boolean saveNICKMFReportingData(NICKMFReportingDataHolderDto dataHolderDto) {

        // TODO: error message to REST (i.e. UI)

        try {
            if(dataHolderDto != null && dataHolderDto.getRecords() != null){

                checkNICKMFReportingData(dataHolderDto.getRecords());

                // TODO: check report status
                PeriodicReport periodicReport = this.periodReportRepository.findOne(dataHolderDto.getReport().getId());
                if(periodicReport != null && periodicReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
                    return false;
                }

                this.nickmfReportingDataRepository.deleteAllByReportId(dataHolderDto.getReport().getId());
                for(NICKMFReportingDataDto dto: dataHolderDto.getRecords()){
                    NICKMFReportingData entity = this.nickmfReportingDataConverter.assemble(dto);
                    entity.setReport(new PeriodicReport(dataHolderDto.getReport().getId()));
                    this.nickmfReportingDataRepository.save(entity);
                }
                return true;
            }
        }catch (IllegalArgumentException ex){
            logger.error("Error saving NICK MF Reporting data: input validation failed", ex);

            // TODO: return error message from IllegalArgumentException

        }catch (Exception ex){
            logger.error("Error saving NICK MF Reporting data.", ex);
        }
        return false;
    }

    private void checkNICKMFReportingData(List<NICKMFReportingDataDto> records){
        if(records != null){
            double totalSum = 0.0;
            Set<String> codes = new HashSet<>();
            for(NICKMFReportingDataDto record: records){

                if(codes.contains(record.getNicChartOfAccountsCode())){
                    throw new IllegalArgumentException("Duplicate chart of accounts code: " + record.getNbChartOfAccountsCode());
                }

                if(StringUtils.isEmpty(record.getNicChartOfAccountsCode())){
                    throw new IllegalArgumentException("Record missing 'NicChartOfAccountsCode' value");
                }else{
                    // check code exists ???
                }

                if(record.getAccountBalance() == null){
                    throw new IllegalArgumentException("Record missing 'AccountBalance' value");
                }else{
                    totalSum += record.getAccountBalance().doubleValue();
                }

                codes.add(record.getNicChartOfAccountsCode());
            }

            //
//            if(totalSum > 2 || totalSum < -2){
//                throw new IllegalArgumentException("Total sum = " + totalSum +" ; expected value 0 (or between -1 and 1)");
//            }
        }
    }

    @Override
    public NICKMFReportingDataHolderDto getNICKMFReportingData(Long reportId){
        NICKMFReportingDataHolderDto holderDto = new NICKMFReportingDataHolderDto();
        List<NICKMFReportingData> entities = this.nickmfReportingDataRepository.getEntitiesByReportId(reportId);
        PeriodicReport report = periodReportRepository.findOne(reportId);
        if(report != null){
            holderDto.setReport(this.periodicReportConverter.disassemble(report));
        }
        if(entities != null) {
            List<NICKMFReportingDataDto> records = new ArrayList<>();
            for(NICKMFReportingData entity: entities){
                NICKMFReportingDataDto dto = this.nickmfReportingDataConverter.disassemble(entity);
                records.add(dto);
            }
            holderDto.setRecords(records);
        }
        return holderDto;
    }

    @Override
    public NICKMFReportingDataHolderDto getNICKMFReportingDataFromPreviousMonth(Long reportId){
        NICKMFReportingDataHolderDto holderDto = new NICKMFReportingDataHolderDto();
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport != null){
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if(previousReport != null){
                List<NICKMFReportingData> entities = this.nickmfReportingDataRepository.getEntitiesByReportId(previousReport.getId());
                if(currentReport != null){
                    holderDto.setReport(this.periodicReportConverter.disassemble(currentReport));
                }
                if(entities != null) {
                    List<NICKMFReportingDataDto> records = new ArrayList<>();
                    for(NICKMFReportingData entity: entities){
                        NICKMFReportingDataDto dto = this.nickmfReportingDataConverter.disassemble(entity);
                        records.add(dto);
                    }
                    holderDto.setRecords(records);
                }
            }
        }

        return holderDto;
    }

    /**
     * Return schedule of investments data for specified report id.
     *
     * @param reportId - report id
     * @return - schedule of investments data
     */
    @Override
    public ConsolidatedReportRecordHolderDto getScheduleInvestments(Long reportId) {
        ConsolidatedReportRecordHolderDto results = this.scheduleInvestmentService.get(reportId);
        return results;
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

        ConsolidatedReportRecordHolderDto operationsResults = this.statementOperatinsService.get(reportId);

        balanceResults.merge(operationsResults);

        return balanceResults;
    }

    /**
     * Return statement of cash flows data for specified report id.
     * @param reportId - report id
     * @return - statement of cash flows
     */
    @Override
    public ConsolidatedReportRecordHolderDto getStatementCashflows(Long reportId){
        ConsolidatedReportRecordHolderDto statementCashflows = this.statementCashflowsService.get(reportId);
        return statementCashflows;
    }

    /**
     * Return statement of changes data frr specified report id.
     *
     * @param reportId - report id
     * @return - statement of changes
     */
    @Override
    public ConsolidatedReportRecordHolderDto getStatementChanges(Long reportId){
        ConsolidatedReportRecordHolderDto statementChanges = this.statementChangesService.get(reportId);
        return statementChanges;
    }

    /**
     * Return general ledger balance data for specified report id.
     *
     * @param reportId - report id
     * @return - general ledger balance
     */
    @Override
    public ConsolidatedReportRecordHolderDto getGeneralLedgerBalance(Long reportId){
        ConsolidatedReportRecordHolderDto generalLedgerRecordsHolder = this.generalLedgerBalanceService.get(reportId);
        return generalLedgerRecordsHolder;
    }

    @Override
    public List<GeneratedGeneralLedgerFormDto> getSingularGeneratedForm(Long reportId) {
        ConsolidatedReportRecordHolderDto generalLedgerRecordsHolder = this.generalLedgerBalanceService.get(reportId);
        ConsolidatedReportRecordHolderDto noalTrancheARecordHolder = this.hfNOALService.get(reportId, 1);
        ConsolidatedReportRecordHolderDto noalTrancheBRecordHolder = this.hfNOALService.get(reportId, 2);

        Map<String, Double> noalTrancheASubscriptionsRecords = new HashMap<>();
        Map<String, Double> noalTrancheARedemptionsRecords = new HashMap<>();
        if(noalTrancheARecordHolder != null && noalTrancheARecordHolder.getNoalTrancheAList() != null){
            for(SingularityNOALRecordDto noalRecordDto: noalTrancheARecordHolder.getNoalTrancheAList()){
                if(noalRecordDto.getTransaction().equalsIgnoreCase("Ending Balance") || noalRecordDto.getTransaction().equalsIgnoreCase("Ending")) {
                    if (noalRecordDto.getAccountNumber().startsWith("1500")) {
                        if(noalTrancheASubscriptionsRecords.get(noalRecordDto.getName()) != null){
                            BigDecimal a = getBigDecimal(noalTrancheASubscriptionsRecords.get(noalRecordDto.getName()));
                            BigDecimal sum = a.add(getBigDecimal(noalRecordDto.getFunctionalAmount()));
                            noalTrancheASubscriptionsRecords.put(noalRecordDto.getName(), sum.doubleValue());
                        }else{
                            noalTrancheASubscriptionsRecords.put(noalRecordDto.getName(), noalRecordDto.getFunctionalAmount());
                        }

                    }else if (noalRecordDto.getAccountNumber().startsWith("1550")) {
                        if(noalTrancheARedemptionsRecords.get(noalRecordDto.getName()) != null){
                            BigDecimal a = getBigDecimal(noalTrancheARedemptionsRecords.get(noalRecordDto.getName()));
                            BigDecimal sum = a.add(getBigDecimal(noalRecordDto.getFunctionalAmount()));
                            noalTrancheARedemptionsRecords.put(noalRecordDto.getName(), sum.doubleValue());
                        }else{
                            noalTrancheARedemptionsRecords.put(noalRecordDto.getName(), noalRecordDto.getFunctionalAmount());
                        }

                    }
                }

            }
        }

        Map<String, Double> noalTrancheBSubscriptionsRecords = new HashMap<>();
        Map<String, Double> noalTrancheBRedemptionsRecords = new HashMap<>();
        if(noalTrancheARecordHolder != null && noalTrancheBRecordHolder.getNoalTrancheAList() != null){
            for(SingularityNOALRecordDto noalRecordDto: noalTrancheBRecordHolder.getNoalTrancheAList()){
                if(noalRecordDto.getTransaction().equalsIgnoreCase("Ending Balance") || noalRecordDto.getTransaction().equalsIgnoreCase("Ending")) {
                    if (noalRecordDto.getAccountNumber().startsWith("1500")) {
                        if(noalTrancheBSubscriptionsRecords.get(noalRecordDto.getName()) != null){
                            BigDecimal a = getBigDecimal(noalTrancheBSubscriptionsRecords.get(noalRecordDto.getName()));
                            BigDecimal sum = a.add(getBigDecimal(noalRecordDto.getFunctionalAmount()));
                            noalTrancheBSubscriptionsRecords.put(noalRecordDto.getName(), sum.doubleValue());
                        }else{
                            noalTrancheBSubscriptionsRecords.put(noalRecordDto.getName(), noalRecordDto.getFunctionalAmount());
                        }
                    }else if (noalRecordDto.getAccountNumber().startsWith("1550")) {
                        if(noalTrancheBRedemptionsRecords.get(noalRecordDto.getName()) != null){
                            BigDecimal a = getBigDecimal(noalTrancheBRedemptionsRecords.get(noalRecordDto.getName()));
                            BigDecimal sum = a.add(getBigDecimal(noalRecordDto.getFunctionalAmount()));
                            noalTrancheBRedemptionsRecords.put(noalRecordDto.getName(), sum.doubleValue());
                        }else{
                            noalTrancheBRedemptionsRecords.put(noalRecordDto.getName(), noalRecordDto.getFunctionalAmount());
                        }
                    }
                }

            }
        }

        List<GeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(generalLedgerRecordsHolder != null && !generalLedgerRecordsHolder.getGeneralLedgerBalanceList().isEmpty()){
            for(SingularityGeneralLedgerBalanceRecordDto glRecordDto: generalLedgerRecordsHolder.getGeneralLedgerBalanceList()){
                GeneratedGeneralLedgerFormDto record = new GeneratedGeneralLedgerFormDto();
                record.setAcronym(glRecordDto.getAcronym());
                record.setBalanceDate(glRecordDto.getBalanceDate());
                record.setFinancialStatementCategory(glRecordDto.getFinancialStatementCategory());
                record.setGLAccount(glRecordDto.getGLAccount());
                record.setFinancialStatementCategoryDescription(glRecordDto.getFinancialStatementCategoryDescription());
                record.setChartAccountsLongDescription(glRecordDto.getChartAccountsLongDescription());
                record.setShortName(glRecordDto.getShortName());
                record.setGLAccountBalance(glRecordDto.getGLAccountBalance());
                record.setSegValCCY(glRecordDto.getSegValCCY());
                record.setFundCCY(glRecordDto.getFundCCY());

                String singularityAccountNumber = glRecordDto.getGLAccount() != null && glRecordDto.getGLAccount().split("-").length > 0
                    ? glRecordDto.getGLAccount().split("-")[0] : null;

                if(singularityAccountNumber == null){


                    // TODO: check on parsing !!!


                    logger.error("No matching NIC Chart of Accounts record found for Singularity Account Number '" + glRecordDto.getGLAccount() + "'");
                    throw new IllegalStateException("No matching NIC Chart of Accounts record found for Singularity Account Number '" + glRecordDto.getGLAccount() + "'");
                }
                NICReportingChartOfAccountsDto accountDto = getNICChartOfAccountsFromSingularityAccount(singularityAccountNumber, record.getGLAccountBalance());
                if(accountDto != null){
                    record.setNbAccountNumber(accountDto.getNBChartOfAccounts().getCode());
                    record.setNicAccountName(accountDto.getNameRu());
                }else{
                    logger.error("No matching NIC Chart of Accounts record found for Singularity Account Number '" + singularityAccountNumber + "'");
                    throw new IllegalStateException("No matching NIC Chart of Accounts record found for Singularity Account Number '" + singularityAccountNumber + "'");
                }


               if(record.getGLAccount().startsWith("1500")){ // SUBSCRIPTIONS - Tranche A
                   if(record.getAcronym().equalsIgnoreCase("SINGULAR")){
                       for (String key : noalTrancheASubscriptionsRecords.keySet()) {
                           GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                           newRecordDto.setSubscriptionRedemptionEntity(key);
                           newRecordDto.setGLAccountBalance(noalTrancheASubscriptionsRecords.get(key));
                           setAccountNameAdditionalDescription(newRecordDto);
                           records.add(newRecordDto);
                       }

                   }else if(record.getAcronym().equalsIgnoreCase("SINGULAR B")){ // SUBSCRIPTIONS - Tranche B
                       for (String key : noalTrancheBSubscriptionsRecords.keySet()) {
                           GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                           newRecordDto.setSubscriptionRedemptionEntity(key);
                           newRecordDto.setGLAccountBalance(noalTrancheBSubscriptionsRecords.get(key));
                           setAccountNameAdditionalDescription(newRecordDto);
                           records.add(newRecordDto);
                       }
                   }else{
                       // TODO: ??
                   }
                }else if(record.getGLAccount().startsWith("1550")){ // REDEMPTIONS - Tranche A
                   if(record.getAcronym().equalsIgnoreCase("SINGULAR")){
                       for (String key : noalTrancheARedemptionsRecords.keySet()) {
                           GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                           newRecordDto.setSubscriptionRedemptionEntity(key);
                           newRecordDto.setGLAccountBalance(noalTrancheARedemptionsRecords.get(key));
                           setAccountNameAdditionalDescription(newRecordDto);
                           records.add(newRecordDto);
                       }

                   }else if(record.getAcronym().equalsIgnoreCase("SINGULAR B")){ // REDEMPTIONS - Tranche B
                       for (String key : noalTrancheBRedemptionsRecords.keySet()) {
                           GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                           newRecordDto.setSubscriptionRedemptionEntity(key);
                           newRecordDto.setGLAccountBalance(noalTrancheBRedemptionsRecords.get(key));
                           setAccountNameAdditionalDescription(newRecordDto);
                           records.add(newRecordDto);
                       }
                   }else{
                       // TODO: ??
                   }
               }else {
                   setAccountNameAdditionalDescription(record);
                   records.add(record);
               }
            }
        }
        return records;
    }

    @Override
    public ListResponseDto getTarragonGeneratedForm(Long reportId){
        ListResponseDto responseDto = new ListResponseDto();
        List<GeneratedGeneralLedgerFormDto> records = new ArrayList<>();

        // Balance and Operations
        List<StatementBalanceOperationsDto> balanceRecords = this.statementBalanceService.getStatementBalanceRecords(reportId);
        records.addAll(processBalance(balanceRecords));

        List<StatementBalanceOperationsDto> operationsRecords = this.statementOperatinsService.getStatementOperationsRecords(reportId);
        records.addAll(processOperations(operationsRecords));

        // Investments
        List<ScheduleInvestmentsDto> investments = this.scheduleInvestmentService.getScheduleInvestments(reportId);
        records.addAll(processScheduleInvestments(investments));

        // Statement changes
        List<StatementChangesDto> changes = this.statementChangesService.getStatementChanges(reportId);
        records.addAll(processStatementChanges(changes));

        // update account balance
        List<GeneratedGeneralLedgerFormDto> updatedRecords = new ArrayList<>();
        BigDecimal sum = new BigDecimal("0");
        for(GeneratedGeneralLedgerFormDto record: records){
            if(record.getGLAccountBalance() != null && record.getGLAccountBalance().doubleValue() != 0.0){
                if(record.getGLAccountBalance() != null && record.getFinancialStatementCategory() != null &&
                        !record.getFinancialStatementCategory().equalsIgnoreCase("A")){
                    BigDecimal newValue = new BigDecimal(record.getGLAccountBalance()).multiply(new BigDecimal(-1));
                    record.setGLAccountBalance(newValue.doubleValue());
                    sum = sum.add(newValue);
                }
                updatedRecords.add(record);
            }else{
                // skip zero-values
            }
        }

        // Added records
        Double netRealizedTrancheA = 0.0;
        Double netRealizedTrancheB = 0.0;
        List<PEGeneralLedgerFormData> addedRecods =
                this.peGeneralLedgerFormDataRepository.getEntitiesByReportId(reportId, new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
        if(addedRecods != null) {
            for(PEGeneralLedgerFormData entity: addedRecods) {
                PEGeneralLedgerFormDataDto addedRecordDto = this.peGeneralLedgerFormDataConverter.disassemble(entity);
                GeneratedGeneralLedgerFormDto recordDto = new GeneratedGeneralLedgerFormDto();
                recordDto.setAcronym(addedRecordDto.getTranche() == 1 ? "TARRAGON" : "TARRAGON B");
                if(addedRecordDto.getReport() != null) {
                    recordDto.setBalanceDate(addedRecordDto.getReport().getReportDate());
                }
                recordDto.setFinancialStatementCategory(addedRecordDto.getFinancialStatementCategory());
                recordDto.setChartAccountsLongDescription(addedRecordDto.getTarragonNICChartOfAccountsName());
                recordDto.setNbAccountNumber(addedRecordDto.getNbAccountNumber());
                String entityName = addedRecordDto.getEntityName() != null ? " " + addedRecordDto.getEntityName() : "";
                recordDto.setNicAccountName(addedRecordDto.getNicAccountName() + entityName);
                recordDto.setSubscriptionRedemptionEntity(entityName);
                recordDto.setGLAccountBalance(addedRecordDto.getGLAccountBalance());
                recordDto.setAdded(true);
                recordDto.setAddedRecordId(entity.getId());
                updatedRecords.add(recordDto);

                if(recordDto.getChartAccountsLongDescription().equalsIgnoreCase("Net Realized Gains/Losses from Portfolio Funds")){
                    switch(recordDto.getAcronym()) {
                        case "TARRAGON":
                            netRealizedTrancheA += recordDto.getGLAccountBalance();
                            break;
                        case "TARRAGON B":
                            netRealizedTrancheB += recordDto.getGLAccountBalance();
                            break;
                    }
                }
            }
        }

        setNICChartOfAccounts(updatedRecords);
        Collections.sort(updatedRecords);


        // Check Net Realized Gains Losses
        if(operationsRecords != null) {
            for (StatementBalanceOperationsDto record : operationsRecords) {
                if (record.getName().equalsIgnoreCase("Net realized gain on investments")) {
                    double value = record.getNICKMFShareConsolidated() != null ? record.getNICKMFShareConsolidated().doubleValue() : 0.0;
                    if (record.getTranche() == 1 && ((netRealizedTrancheA + value) < -2 ||
                            (netRealizedTrancheA + value) > 2)) {
                        String errorMessage = "{Tranche A] Statement of operations 'Net realized gain on investments' = " + value +
                                ", sum of net realized gains/losses = " + netRealizedTrancheA;
                        logger.error(errorMessage);
                        responseDto.setMessageEn(errorMessage);
                        responseDto.setStatus(StatusResultType.FAIL);
                    } else if (record.getTranche() == 2 && ((netRealizedTrancheB + value) < -2 ||
                            (netRealizedTrancheB + value) > 2)) {
                        String errorMessage = "{Tranche B] Statement of operations 'Net realized gain on investments' = " + value +
                                ", sum of net realized gains/losses = " + netRealizedTrancheB;
                        logger.error(errorMessage);
                        responseDto.setMessageEn(errorMessage);
                        responseDto.setStatus(StatusResultType.FAIL);
                    }
                }
            }
        }

        responseDto.setRecords(updatedRecords);
        if(responseDto.getStatus() == null){
            responseDto.setStatus(StatusResultType.SUCCESS);
        }
        return responseDto;
    }


    @Override
    public List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceUSDForm(Long reportId){
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(currentReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedBalanceUSDFormSaved(reportId);
        }else{
            List<ConsolidatedBalanceFormRecordDto> currentPeriodRecords = getConsolidatedBalanceUSDFormCurrent(reportId);

            int header1Index = 0; // Инвестиции к возврату
            int header2Index = 0; // Предварительная подписка
            for(int i = 0; i < currentPeriodRecords.size(); i++) {
                ConsolidatedBalanceFormRecordDto currentRecord = currentPeriodRecords.get(i);
                if (currentRecord.getName().equalsIgnoreCase("Инвестиции к возврату") && currentRecord.getAccountNumber() == null) {
                    header1Index = i + 1;
                } else if (currentRecord.getName().equalsIgnoreCase("Предварительная подписка") && currentRecord.getAccountNumber() == null) {
                    header2Index = i + 1;
                }
            }

            // Set previous month account balance
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if(previousReport != null && previousReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
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
                                if(previousRecord.getName().startsWith("Инвестиции к возврату") && previousRecord.getAccountNumber() != null){
                                    toAddIndex.add(header1Index);
                                }else if(previousRecord.getName().startsWith("Предварительная подписка") && previousRecord.getAccountNumber() != null){
                                    toAddIndex.add(header2Index);
                                }else {
                                    toAddIndex.add(i + 1);
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
                logger.error("ConsolidatedBalanceUSDForm: No previous month report or report status is not 'SUBMITTED'");
            }

            return currentPeriodRecords;
        }
    }


    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceUSDFormCurrent(Long reportId) {
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
        NICKMFReportingDataHolderDto nickmfReportingDataHolderDto = getNICKMFReportingData(reportId);
        if(nickmfReportingDataHolderDto != null && nickmfReportingDataHolderDto.getRecords() != null){
            for(NICKMFReportingDataDto nickMFRecord: nickmfReportingDataHolderDto.getRecords()){
                if(nickMFRecord.getNbChartOfAccountsCode().equalsIgnoreCase("5440.010") &&
                        !nickMFRecord.getNicChartOfAccountsName().equalsIgnoreCase("Резерв по переоценке финансовых инвестиций, имеющихся в наличии для продажи")){
                    // include only 'Резерв на переоценку  финансовых инвестиций, имеющихся в наличии для продажи'
                    continue;
                }else if(nickMFRecord.getNbChartOfAccountsCode().equalsIgnoreCase("5520.010")){
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
                    // TODO: ERROR
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
                    sum += recordDto.getCurrentAccountBalance();
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add Singular records
        List<GeneratedGeneralLedgerFormDto> singularRecords = getSingularGeneratedForm(reportId);
        if(singularRecords != null){
            for(GeneratedGeneralLedgerFormDto singularRecord: singularRecords){
                if(singularRecord.getNbAccountNumber().equalsIgnoreCase("5440.010") &&
                        !singularRecord.getNicAccountName().equalsIgnoreCase("Резерв по переоценке финансовых инвестиций, имеющихся в наличии для продажи")){
                    // include only 'Резерв на переоценку  финансовых инвестиций, имеющихся в наличии для продажи'
                    continue;
                }else if(singularRecord.getNbAccountNumber().equalsIgnoreCase("5520.010")){
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
                    // TODO: ERROR
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
                    sum += recordDto.getCurrentAccountBalance();
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add Tarragon records
        List<GeneratedGeneralLedgerFormDto> tarragonRecords = getTarragonGeneratedForm(reportId).getRecords();
        if(singularRecords != null){
            for(GeneratedGeneralLedgerFormDto tarragonRecord: tarragonRecords){
                if(tarragonRecord.getNbAccountNumber() != null && tarragonRecord.getNbAccountNumber().equalsIgnoreCase("5440.010") &&
                        !tarragonRecord.getNicAccountName().equalsIgnoreCase("Резерв по переоценке финансовых инвестиций, имеющихся в наличии для продажи")){
                    // include only 'Резерв на переоценку  финансовых инвестиций, имеющихся в наличии для продажи'
                    continue;
                }else if(tarragonRecord.getNbAccountNumber() != null && tarragonRecord.getNbAccountNumber().equalsIgnoreCase("5520.010")){
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
                    // TODO: ERROR
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
                    sum += recordDto.getCurrentAccountBalance();
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add previous year input
        List<PreviousYearInputDataDto> previousYearRecords = getPreviousYearInputData(reportId);
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
                    // TODO: error
                    continue;
                }
                records.add(recordDto);

                if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                    Double sum = sums.get(recordDto.getLineNumber());
                    sum = sum != null ? sum : 0.0;
                    sum += recordDto.getCurrentAccountBalance();
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        List<ConsolidatedBalanceFormRecordDto> incomeExpenseRecords = getConsolidatedIncomeExpenseUSDForm(reportId);
        if(incomeExpenseRecords != null && !incomeExpenseRecords.isEmpty()){
            for(int i = incomeExpenseRecords.size() - 1; i < incomeExpenseRecords.size(); i--){
                ConsolidatedBalanceFormRecordDto inRecordDto = incomeExpenseRecords.get(i);
                if(inRecordDto.getLineNumber() == 20){
                    ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                    recordDto.setAccountNumber("5510.010");
                    recordDto.setName("Нераспределенная прибыль (непокрытый убыток) отчетного года");
                    recordDto.setLineNumber(50);
                    recordDto.setCurrentAccountBalance(inRecordDto.getCurrentAccountBalance());
                    records.add(recordDto);

                    if(StringUtils.isNotEmpty(recordDto.getAccountNumber()) && recordDto.getLineNumber() != null && recordDto.getCurrentAccountBalance() != null){
                        Double sum = sums.get(recordDto.getLineNumber());
                        sum = sum != null ? sum : 0.0;
                        sum += recordDto.getCurrentAccountBalance();
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
        return recordsNoDuplicates;
    }

    @Override
    public List<ConsolidatedBalanceFormRecordDto> getConsolidatedIncomeExpenseUSDForm(Long reportId){
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(currentReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedIncomeExpenseUSDFormSaved(reportId);
        }else{
            List<ConsolidatedBalanceFormRecordDto> currentPeriodRecords = getConsolidatedIncomeExpenseUSDFormCurrent(reportId);

            // Set previous month account balance
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if(previousReport != null && previousReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
                List<ConsolidatedBalanceFormRecordDto> previousPeriodRecords = getConsolidatedIncomeExpenseUSDFormSaved(previousReport.getId());
                if(previousPeriodRecords != null){
                    List<ConsolidatedBalanceFormRecordDto> toAdd = new ArrayList<>();
                    List<Integer> toAddIndex = new ArrayList<>();
                    for(ConsolidatedBalanceFormRecordDto previousRecord: previousPeriodRecords){
                        int header1Index = 0; // Доходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи
                        int header2Index = 0; // Расходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи
                        for(int i = 0; i < currentPeriodRecords.size(); i++){
                            ConsolidatedBalanceFormRecordDto currentRecord = currentPeriodRecords.get(i);
                            if(currentRecord.getName().equalsIgnoreCase("Доходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи") &&
                                    currentRecord.getAccountNumber() == null){
                                header1Index = i;
                            }else if(currentRecord.getName().equalsIgnoreCase("Расходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи") &&
                                    currentRecord.getAccountNumber() == null){
                                header2Index = i;
                            }

                            if(previousRecord.getLineNumber() + 1 == (currentRecord.getLineNumber())){
                                // next line number
                                if(previousRecord.getAccountNumber() != null && previousRecord.getAccountNumber().equalsIgnoreCase("6150.030")){
                                    toAddIndex.add(header1Index + 1);
                                }else if(previousRecord.getAccountNumber() != null && previousRecord.getAccountNumber().equalsIgnoreCase("7330.030")){
                                    toAddIndex.add(header2Index + 1);
                                }else {
                                    toAddIndex.add(i);
                                }
                                toAdd.add(previousRecord);
                                break;
                            }

                            if(currentRecord.getName().equalsIgnoreCase(previousRecord.getName()) && currentRecord.getLineNumber() != null &&
                                    previousRecord.getLineNumber() != null && previousRecord.getLineNumber() == currentRecord.getLineNumber()){
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
                logger.error("ConsolidatedIncomeExpenseUSDForm: No previous month report or report status is not 'SUBMITTED'");
            }

            return currentPeriodRecords;
        }


    }

    public List<ConsolidatedBalanceFormRecordDto> getConsolidatedIncomeExpenseUSDFormCurrent(Long reportId) {
        Map<Integer, Double> sums = new HashedMap();
        for(int i = 1; i <= 20; i++){
            sums.put(i, 0.0);
        }
        // Add line number headers
        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedIncomeExpenseUSDFormLineHeaders();

        // Add NICK MF records
        NICKMFReportingDataHolderDto nickmfReportingDataHolderDto = getNICKMFReportingData(reportId);
        if(nickmfReportingDataHolderDto != null && nickmfReportingDataHolderDto.getRecords() != null){
            for(NICKMFReportingDataDto nickMFRecord: nickmfReportingDataHolderDto.getRecords()){
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setAccountNumber(nickMFRecord.getNbChartOfAccountsCode());
                recordDto.setName(nickMFRecord.getNicChartOfAccountsName());
                int lineNumber = getConsolidatedIncomeExpenseUSDFormLineNumberByAccountNumber(nickMFRecord.getNbChartOfAccountsCode());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    // TODO: ERROR
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
                    sum += recordDto.getCurrentAccountBalance();
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add Singular records
        List<GeneratedGeneralLedgerFormDto> singularRecords = getSingularGeneratedForm(reportId);
        if(singularRecords != null){
            for(GeneratedGeneralLedgerFormDto singularRecord: singularRecords){
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setAccountNumber(singularRecord.getNbAccountNumber());
                recordDto.setName(singularRecord.getNicAccountName());
                int lineNumber = getConsolidatedIncomeExpenseUSDFormLineNumberByAccountNumber(singularRecord.getNbAccountNumber());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    // TODO: ERROR
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
                    sum += recordDto.getCurrentAccountBalance();
                    sums.put(recordDto.getLineNumber(), sum);
                }
            }
        }

        // Add Tarragon records
        List<GeneratedGeneralLedgerFormDto> tarragonRecords = getTarragonGeneratedForm(reportId).getRecords();
        if(singularRecords != null){
            for(GeneratedGeneralLedgerFormDto tarragonRecord: tarragonRecords){
                ConsolidatedBalanceFormRecordDto recordDto = new ConsolidatedBalanceFormRecordDto();
                recordDto.setAccountNumber(tarragonRecord.getNbAccountNumber());
                recordDto.setName(tarragonRecord.getNicAccountName());
                int lineNumber = getConsolidatedIncomeExpenseUSDFormLineNumberByAccountNumber(tarragonRecord.getNbAccountNumber());
                if(lineNumber > 0){
                    recordDto.setLineNumber(lineNumber);
                }else{
                    // TODO: ERROR
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
                    sum += recordDto.getCurrentAccountBalance();
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
    public List<ConsolidatedBalanceFormRecordDto> getConsolidatedTotalIncomeUSDForm(Long reportId){
        PeriodicReport currentReport = this.periodReportRepository.findOne(reportId);
        if(currentReport == null){
            logger.error("No report found for id=" + reportId);
            return null;
        }

        if(currentReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")){
            return getConsolidatedTotalIncomeUSDFormSaved(reportId);
        }else {

            List<ConsolidatedBalanceFormRecordDto> currentPeriodRecords = getConsolidatedTotalIncomeUSDFormCurrent(reportId);

            // Set previous month account balance
            Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
            PeriodicReport previousReport = this.periodReportRepository.findByReportDate(previousDate);
            if (previousReport != null && previousReport.getStatus().getCode().equalsIgnoreCase("SUBMITTED")) {
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
                logger.error("ConsolidatedTotalIncomeUSDForm: No previous month report or report status is not 'SUBMITTED'");
            }

            return currentPeriodRecords;
        }
    }

    public List<ConsolidatedBalanceFormRecordDto> getConsolidatedTotalIncomeUSDFormCurrent(Long reportId) {
        Map<Integer, Double> sums = new HashedMap();
        for(int i = 1; i <= 6; i++){
            sums.put(i, 0.0);
        }
        // Add line number headers
        List<ConsolidatedBalanceFormRecordDto> records = getConsolidatedTotalIncomeUSDFormLineHeaders();

        // Get consolidated income expense
        Double header_1_Balance = null;
        List<ConsolidatedBalanceFormRecordDto> incomeExpenseFormRecords = getConsolidatedIncomeExpenseUSDForm(reportId);
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
        List<ConsolidatedBalanceFormRecordDto> balanceFormRecords = getConsolidatedBalanceUSDForm(reportId);
        if(balanceFormRecords != null && !balanceFormRecords.isEmpty()){
            for(ConsolidatedBalanceFormRecordDto balanceRecordDto: balanceFormRecords){
                if(balanceRecordDto.getAccountNumber() != null && balanceRecordDto.getAccountNumber().equalsIgnoreCase("5440.010") &&
                        balanceRecordDto.getName().equalsIgnoreCase("Резерв по переоценке финансовых инвестиций, имеющихся в наличии для продажи")){
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
        headers.add(new ConsolidatedBalanceFormRecordDto("Итого совокупного дохода (сумма строк 1, 5)", 6));

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
        ConsolidatedBalanceFormRecordDto header1283020aRecord = new ConsolidatedBalanceFormRecordDto("Инвестиции к возврату", 8);
        //header1283020aRecord.setAccountNumber("1283.020");
        ConsolidatedBalanceFormRecordDto header1283020bRecord = new ConsolidatedBalanceFormRecordDto("Предварительная подписка", 8);
        //header1283020bRecord.setAccountNumber("1283.020");
        boolean header1283020bAdded = false;
        if(records != null) {
            for (int i = 0; i < records.size(); i++) {
                ConsolidatedBalanceFormRecordDto record = records.get(i);
                if (record.getLineNumber() == 8 && record.getAccountNumber() != null && record.getAccountNumber().equalsIgnoreCase("1283.020")) {
                    if (record.getName().startsWith("Инвестиции к возврату")) {
                        if (!header1283020aAdded) {
                            header1283020aRecord.setCurrentAccountBalance(record.getCurrentAccountBalance());
                            newRecords.add(header1283020aRecord);
                            header1283020aAdded = true;
                        } else {
                            header1283020aRecord.setCurrentAccountBalance(header1283020aRecord.getCurrentAccountBalance() + record.getCurrentAccountBalance());
                        }
                    }
                    if (record.getName().startsWith("Предварительная подписка")) {
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
                new ConsolidatedBalanceFormRecordDto("Доходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи", 8);
        ConsolidatedBalanceFormRecordDto header7330030Record =
                new ConsolidatedBalanceFormRecordDto("Расходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи", 8);
        ConsolidatedBalanceFormRecordDto header7313010Record =
                new ConsolidatedBalanceFormRecordDto("Расходы по вознаграждениям по краткосрочным банковским займам", 12);
        if(records != null) {
            for (int i = 0; i < records.size(); i++) {
                ConsolidatedBalanceFormRecordDto record = records.get(i);
                if (record.getLineNumber() == 8 && record.getAccountNumber() != null /* && record.getName().startsWith("Реализованные доходы/расходы по инвестициям")*/) {
                    if (record.getAccountNumber().equalsIgnoreCase("6150.030")){
                        if (!header6150030Added) {
                            header6150030Record.setCurrentAccountBalance(record.getCurrentAccountBalance());
                            newRecords.add(header6150030Record);
                            header6150030Added = true;
                        } else {
                            header6150030Record.setCurrentAccountBalance(header6150030Record.getCurrentAccountBalance() + record.getCurrentAccountBalance());
                        }
                    }else if (record.getAccountNumber().equalsIgnoreCase("7330.030")) {
                        if (!header7330030Added) {
                            header7330030Record.setCurrentAccountBalance(record.getCurrentAccountBalance());
                            newRecords.add(header7330030Record);
                            header7330030Added = true;
                        } else {
                            header7330030Record.setCurrentAccountBalance(header7330030Record.getCurrentAccountBalance() + record.getCurrentAccountBalance());
                        }
                    }
                }else if (record.getLineNumber() == 12 && record.getAccountNumber() != null /* && record.getName().startsWith("Реализованные доходы/расходы по инвестициям")*/) {
                    if (record.getAccountNumber().equalsIgnoreCase("7313.010")){
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
        }else if(accountNumber.equalsIgnoreCase("1033.010")){
            return 2;
        }else if(accountNumber.equalsIgnoreCase("1283.020")){
            return 8;
        }else if(accountNumber.equalsIgnoreCase("1623.010")) {
            return 11;
        }else if(accountNumber.equalsIgnoreCase("2033.010")){
            return 16;
        }else if(accountNumber.equalsIgnoreCase("2923.010")){
            return 24;
        }else if(accountNumber.equalsIgnoreCase("3013.010") || accountNumber.equalsIgnoreCase("3383.010")){
            return 28;
        }else if(accountNumber.equalsIgnoreCase("3393.020")){
            return 30;
        }else if(accountNumber.equalsIgnoreCase("5021.010") || accountNumber.equalsIgnoreCase("5022.010")){
            return 45;
        }else if(accountNumber.equalsIgnoreCase("5440.010")){
            return 49;
        }else if(accountNumber.equalsIgnoreCase("5510.010") || accountNumber.equalsIgnoreCase("5520.010")){
            return 50;
        }

        return 0;
    }

    private int getConsolidatedIncomeExpenseUSDFormLineNumberByAccountNumber(String accountNumber) {

        // TODO: load from DB

        if(accountNumber == null){
            return 0;
        }else if (accountNumber.equalsIgnoreCase("6150.020") || accountNumber.equalsIgnoreCase("6150.030") ||
                accountNumber.equalsIgnoreCase("7330.020") || accountNumber.equalsIgnoreCase("7330.030")) {
            return 8;
        }else if(accountNumber.equalsIgnoreCase("6283.080")){
            return 10;
        }else if(accountNumber.equalsIgnoreCase("7313.010")){
            return 12;
        }else if(accountNumber.equalsIgnoreCase("7473.080")){
            return 15;
        }

        return 0;
    }

    private List<ConsolidatedBalanceFormRecordDto> getConsolidatedBalanceUSDFormLineHeaders(){

        // TODO: get from DB
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
        headers.add(new ConsolidatedBalanceFormRecordDto("Всего обязательства и капитал (сумма строк 35, 43, 51)", 52));
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
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие доходы (6210-6240, 6260, 6270, 6110.010, 6110.020, 6110.080, 6110.140, 6110.260, 6110.270, 6110.320, 6110.330, 6120, 6130, 6140, 6150.040, 6150.050,6160, 6280.040-6280.080, 6290, 6110.150, 6110.160, 6110.280-6110.310) ", 10));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по приобретенным ценным бумагам (7310.100-7310.130)", 11));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по полученным займам и финансовой аренде (7310.010-7310.040, 7310.080, 7310.090, 7310.190-7310.220, 7320.010)", 12));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по реализации (7110)", 13));
        headers.add(new ConsolidatedBalanceFormRecordDto("Административные расходы (7210)", 14));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие расходы (7310.050-7310.070, 7310.140-7310.180, 7310.230, 7310.240, 7330.040, 7330.050, 7340.010-7340.030, 7410, 7420, 7440-7460, 7470.040-7470.080, 7480)", 15));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) до налогообложения (сумма строк 3-15)", 16));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по подоходному налогу (7710)", 17));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) после налогообложения от продолжающейся деятельности (сумма строк 16, 17)", 18));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) от прекращенной деятельности (6310, 7510)", 19));
        headers.add(new ConsolidatedBalanceFormRecordDto("Чистая прибыль (убыток) (сумма строк 18, 19)", 20));
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
        record6150_030.setAccountNumber("6150.030");
        headers.add(record6150_030);

        ConsolidatedBalanceFormRecordDto record7330_030 = new ConsolidatedBalanceFormRecordDto("Расходы от изменения справедливой стоимости  долгосрочных финансовых инвестиций, имеющихся в наличии для продажи", 8);
        record7330_030.setAccountNumber("7330.030");
        headers.add(record7330_030);

        headers.add(new ConsolidatedBalanceFormRecordDto("Доходы (расходы) от переоценки иностранной валюты (6250, 7430)", 9));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие доходы (6210-6240, 6260, 6270, 6110.010, 6110.020, 6110.080, 6110.140, 6110.260, 6110.270, 6110.320, 6110.330, 6120, 6130, 6140, 6150.040, 6150.050,6160, 6280.040-6280.080, 6290, 6110.150, 6110.160, 6110.280-6110.310) ", 10));

        ConsolidatedBalanceFormRecordDto record6283_080 = new ConsolidatedBalanceFormRecordDto("Прочие расходы", 10);
        record6283_080.setAccountNumber("6283.080");
        headers.add(record6283_080);

        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по приобретенным ценным бумагам (7310.100-7310.130)", 11));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы в виде вознаграждения по полученным займам и финансовой аренде (7310.010-7310.040, 7310.080, 7310.090, 7310.190-7310.220, 7320.010)", 12));

        ConsolidatedBalanceFormRecordDto record7313_010 = new ConsolidatedBalanceFormRecordDto("Расходы по вознаграждениям по краткосрочным банковским займам", 12);
        record7313_010.setAccountNumber("7313.010");
        headers.add(record7313_010);

        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по реализации (7110)", 13));
        headers.add(new ConsolidatedBalanceFormRecordDto("Административные расходы (7210)", 14));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прочие расходы (7310.050-7310.070, 7310.140-7310.180, 7310.230, 7310.240, 7330.040, 7330.050, 7340.010-7340.030, 7410, 7420, 7440-7460, 7470.040-7470.080, 7480)", 15));

        ConsolidatedBalanceFormRecordDto record7473_080 = new ConsolidatedBalanceFormRecordDto("Прочие расходы", 15);
        record7473_080.setAccountNumber("7473.080");
        headers.add(record7473_080);

        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) до налогообложения (сумма строк 3-15)", 16));
        headers.add(new ConsolidatedBalanceFormRecordDto("Расходы по подоходному налогу (7710)", 17));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) после налогообложения от продолжающейся деятельности (сумма строк 16, 17)", 18));
        headers.add(new ConsolidatedBalanceFormRecordDto("Прибыль (убыток) от прекращенной деятельности (6310, 7510)", 19));
        headers.add(new ConsolidatedBalanceFormRecordDto("Чистая прибыль (убыток) (сумма строк 18, 19)", 20));
        return headers;
    }

    private void setNICChartOfAccounts(List<GeneratedGeneralLedgerFormDto> records){
        if(records != null) {
            for(GeneratedGeneralLedgerFormDto record: records){
                if(record.getNicAccountName() == null) {
                    TarragonNICChartOfAccounts nicChartOfAccounts =
                            this.tarragonNICChartOfAccountsRepository.findByTarragonChartOfAccountsNameAndAddable(record.getChartAccountsLongDescription(), false);
                    if (nicChartOfAccounts != null) {
                        record.setNicAccountName(nicChartOfAccounts.getNicReportingChartOfAccounts().getNameRu());
                        if (nicChartOfAccounts.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                            record.setNbAccountNumber(nicChartOfAccounts.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                        }
                    }else{
                        // no match found
                    }
                }
            }
        }
    }

    private List<GeneratedGeneralLedgerFormDto> processStatementChanges(List<StatementChangesDto> changes){
        List<GeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(changes != null){
            for(StatementChangesDto dto: changes){
                if(dto.getTotalSum() != null && dto.getTotalSum().booleanValue()){
                    // skip total sums
                    continue;
                }
                if(dto.getName().equalsIgnoreCase("Capital Contributions") || dto.getName().equalsIgnoreCase("Distributions") ||
                        dto.getName().equalsIgnoreCase("Unrealized Gain (Loss)") || dto.getName().equalsIgnoreCase("Unrealized Gain (Loss), Net of Related Taxes")){

                    GeneratedGeneralLedgerFormDto trancheARecord = new GeneratedGeneralLedgerFormDto();
                    String acronym = "TARRAGON";
                    trancheARecord.setAcronym(acronym);
                    trancheARecord.setBalanceDate(dto.getReport().getReportDate());
                    trancheARecord.setChartAccountsLongDescription(dto.getName());
                    trancheARecord.setFinancialStatementCategory("E");
                    //record.setSegVal1("");
                    //record.setNbAccountNumber("");
                    //record.setNicAccountName("");
                    trancheARecord.setGLAccountBalance(dto.getTrancheA());
                    //record.setFundCCY();
                    //record.setSegValCCY();

                    GeneratedGeneralLedgerFormDto trancheBRecord = new GeneratedGeneralLedgerFormDto(trancheARecord);
                    trancheBRecord.setAcronym("TARRAGON B");
                    trancheBRecord.setGLAccountBalance(dto.getTrancheB());

                    records.add(trancheARecord);
                    records.add(trancheBRecord);
                }

            }
        }
        return records;
    }

    private List<GeneratedGeneralLedgerFormDto> processOperations(List<StatementBalanceOperationsDto> operationsRecords){
        List<GeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(operationsRecords != null){
            for(StatementBalanceOperationsDto balanceRecord: operationsRecords){
                if(balanceRecord.getTotalSum() != null && balanceRecord.getTotalSum().booleanValue()){
                    // skip total sums
                    continue;
                }
                GeneratedGeneralLedgerFormDto record = new GeneratedGeneralLedgerFormDto();
                if(isIncome(balanceRecord)){
                    record.setFinancialStatementCategory("I");
                }else if(isExpenses(balanceRecord)){
                    record.setFinancialStatementCategory("X");
                }else{
                    continue;
                }
//                if(isEquity(balanceRecord)){
//                    record.setFinancialStatementCategory("E");
//                }

                String acronym = balanceRecord.getTranche() != null && balanceRecord.getTranche() == 1 ? "TARRAGON" :
                        balanceRecord.getTranche() != null && balanceRecord.getTranche() == 2 ? "TARRAGON B" : "UNMATCHED";
                record.setAcronym(acronym);
                record.setBalanceDate(balanceRecord.getReport().getReportDate());
                record.setChartAccountsLongDescription(balanceRecord.getName());
                //record.setSegVal1("");
                //record.setNbAccountNumber("");
                //record.setNicAccountName("");
                Double accountBalance = balanceRecord.getNICKMFShareConsolidated();
                record.setGLAccountBalance(accountBalance);
                //record.setFundCCY();
                //record.setSegValCCY();

                records.add(record);
            }
        }
        return records;
    }

    private List<GeneratedGeneralLedgerFormDto> processBalance(List<StatementBalanceOperationsDto> balanceRecords){
        List<GeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(balanceRecords != null){
            for(StatementBalanceOperationsDto balanceRecord: balanceRecords){
                if(balanceRecord.getTotalSum() != null && balanceRecord.getTotalSum().booleanValue()){
                    // skip total sums
                    continue;
                }
                if(balanceRecord.getType() != null && balanceRecord.getType().getCode().equalsIgnoreCase("PRTN_CAP")){
                    // skip Partners capital
                    continue;
                }
                if(balanceRecord.getType() != null && balanceRecord.getType().getCode().equalsIgnoreCase("FAIR_VAL")){
                    // skip 'Investments at fair value'
                    continue;
                }
                GeneratedGeneralLedgerFormDto record = new GeneratedGeneralLedgerFormDto();
                if(isAssets(balanceRecord)){
                    record.setFinancialStatementCategory("A");
                }else if(isLiabilities(balanceRecord)){
                    record.setFinancialStatementCategory("L");
                }else{
                    // ?
                }

                String acronym = balanceRecord.getTranche() != null && balanceRecord.getTranche() == 1 ? "TARRAGON" :
                        balanceRecord.getTranche() != null && balanceRecord.getTranche() == 2 ? "TARRAGON B" : "UNMATCHED";
                record.setAcronym(acronym);
                record.setBalanceDate(balanceRecord.getReport().getReportDate());
                record.setChartAccountsLongDescription(balanceRecord.getName());
                //record.setSegVal1("");
                //record.setNbAccountNumber("");
                //record.setNicAccountName("");
                Double accountBalance = balanceRecord.getNICKMFShareConsolidated();
                record.setGLAccountBalance(accountBalance);
                //record.setFundCCY();
                //record.setSegValCCY();

                records.add(record);
            }
        }
        return records;
    }

    private boolean isIncome(StatementBalanceOperationsDto dto){
        if(dto != null){
            HierarchicalBaseDictionaryDto type = dto.getType();
            while(type != null){
                if(type.getCode().equalsIgnoreCase("INCOME") && !dto.getName().equalsIgnoreCase("FDAP tax expense")){
                    return true;
                }
                type = type.getParent();
            }
        }
        return false;
    }

    private boolean isExpenses(StatementBalanceOperationsDto dto){
        if(dto != null){
            if(dto.getName() != null && dto.getName().equalsIgnoreCase("FDAP tax expense")){
                return true;
            }
            HierarchicalBaseDictionaryDto type = dto.getType();
            while(type != null){
                if(type.getCode().equalsIgnoreCase("EXPENSES")){
                    return true;
                }
                type = type.getParent();
            }
        }
        return false;
    }

    private boolean isAssets(StatementBalanceOperationsDto dto){
        if(dto != null){
            HierarchicalBaseDictionaryDto type = dto.getType();
            while(type != null){
                if(type.getCode().equalsIgnoreCase("ASSETS")){
                 return true;
                }
                type = type.getParent();
            }
        }
        return false;
    }

    private boolean isLiabilities(StatementBalanceOperationsDto dto){
        if(dto != null){
            HierarchicalBaseDictionaryDto type = dto.getType();
            while(type != null){
                if(type.getCode().equalsIgnoreCase("LIABLTY")){
                    return true;
                }
                type = type.getParent();
            }
        }
        return false;
    }

    private List<GeneratedGeneralLedgerFormDto>  processScheduleInvestments(List<ScheduleInvestmentsDto> investments){
        List<GeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(investments != null ){
            boolean madeEditable = false;
            for(ScheduleInvestmentsDto investment: investments){
                if(investment.getTotalSum() != null && !investment.getTotalSum()) { // total sum records not added
                    String acronym = investment.getTranche() != null && investment.getTranche() == 1 ? "TARRAGON" :
                            investment.getTranche() != null && investment.getTranche() == 2 ? "TARRAGON B" : "UNMATCHED";
                    GeneratedGeneralLedgerFormDto record = new GeneratedGeneralLedgerFormDto();
                    record.setAcronym(acronym);
                    record.setBalanceDate(investment.getReport().getReportDate());
                    record.setFinancialStatementCategory("A");
                    record.setChartAccountsLongDescription(investment.getName());
                    record.setSegVal1("1200");
                    record.setNbAccountNumber("2033.010");
                    record.setNicAccountName("Инвестиции в фонд частного капитала " + investment.getName());
                    Double accountBalance = investment.getFairValue() != null && investment.getTranche() != null ?
                            (investment.getTranche() == 1 ? investment.getFairValue().doubleValue() * 0.99 : investment.getFairValue().doubleValue()) : null;
                    record.setGLAccountBalance(investment.getEditedFairValue() != null ? investment.getEditedFairValue() : accountBalance);
                    //record.setFundCCY();
                    //record.setSegValCCY();

                    if(!madeEditable && investment.getTranche() != null && investment.getTranche() == 1){
                        record.setEditable(true);
                        madeEditable = true;
                    }
                    records.add(record);
                }
            }
        }
        return records;
    }

    private void setAccountNameAdditionalDescription(GeneratedGeneralLedgerFormDto record){
        if(record == null){
            return;
        }
        if(record.getNbAccountNumber().equalsIgnoreCase("2033.010")){
//            String fundName = record.getChartAccountsLongDescription() != null && record.getChartAccountsLongDescription().startsWith("Investment in Portfolio Fund") ?
//                    " " + record.getChartAccountsLongDescription().substring("Investment in Portfolio Fund".length()).trim() : "";
            String fundName = record.getShortName() != null ? " " + record.getShortName() : "";
            record.setNicAccountName(record.getNicAccountName() + fundName);
        }else if(record.getNbAccountNumber().equalsIgnoreCase("1283.020")){
            String entityName = record.getSubscriptionRedemptionEntity() != null ? " " + record.getSubscriptionRedemptionEntity() : "";
            record.setNicAccountName(record.getNicAccountName() + entityName);
        }else if(record.getNbAccountNumber().equalsIgnoreCase("7330.030") || record.getNbAccountNumber().equalsIgnoreCase("6150.030")){
//            String fundName = record.getChartAccountsLongDescription() != null && record.getChartAccountsLongDescription().startsWith("Net Realized Gains/Losses from Portfolio Funds") ?
//                    " " + record.getChartAccountsLongDescription().substring("Net Realized Gains/Losses from Portfolio Funds".length()).trim() : "";

            String fundName = record.getShortName() != null ? " " + record.getShortName() : "";
            record.setNicAccountName(record.getNicAccountName() + fundName);
            //Net Realized Gains/Losses from Portfolio Fund
        }
    }

    private NICReportingChartOfAccountsDto getNICChartOfAccountsFromSingularityAccount(String accountNumber, Double accountBalance){
        List<SingularityNICChartOfAccounts> entities = this.singularityNICChartOfAccountsRepository.findBySingularityAccountNumber(accountNumber);
        if(entities != null && !entities.isEmpty()){
            SingularityNICChartOfAccounts entity = entities.get(0);
            if(accountNumber.equalsIgnoreCase("4200") || accountNumber.equalsIgnoreCase("4900")){
                for(SingularityNICChartOfAccounts anEntity: entities){
                    if(accountBalance > 0){ // 7330.030
                        if(anEntity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase("7330.030")){
                            entity = anEntity;
                        }
                    }else{ // 6150.030
                        if(anEntity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase("6150.030")){
                            entity = anEntity;
                        }
                    }

                }
            }

            NICReportingChartOfAccountsDto dto = new NICReportingChartOfAccountsDto();
            dto.setCode(entity.getNicReportingChartOfAccounts().getCode());
            dto.setNameRu(entity.getNicReportingChartOfAccounts().getNameRu());
            if(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                BaseDictionaryDto nbChartOfAccounts = new BaseDictionaryDto();
                nbChartOfAccounts.setCode(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                dto.setNBChartOfAccounts(nbChartOfAccounts);
            }
            return dto;
        }

        return null;
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
    public ReportOtherInfoDto getOtherInfo(Long reportId){
        ReportOtherInfo entity = this.reportingOtherInfoRepository.getEntityByReportId(reportId);
        if(entity != null) {
            ReportOtherInfoDto dto = this.reportingOtherInfoConverter.disassemble(entity);
            FilesDto filesDto = getPeriodicReportFile(reportId, FileTypeLookup.NB_REP_MONTHLY_CASH_STATEMENT.getCode());
            if(filesDto != null){
                dto.setMonthlyCashStatementFileId(filesDto.getId());
                dto.setMonthlyCashStatementFileName(filesDto.getFileName());
            }
            return dto;
        }else{
            PeriodicReport report = periodReportRepository.findOne(reportId);
            if(report != null){
                ReportOtherInfoDto reportOtherInfoDto = new ReportOtherInfoDto();
                reportOtherInfoDto.setReport(this.periodicReportConverter.disassemble(report));
                return reportOtherInfoDto;
            }
        }
        return null;
    }


    // TODO: @Transactional????

    /* Schedule of Investments ******************************************************/

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
                return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - Schedule of Investments", "");
            }else{
                logger.error("Error saving 'Schedule of Investments' file parsed data into database");
                return new FileUploadResultDto(StatusResultType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Schedule of Investments' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Schedule of Investments' file with error. Stack trace: \n" + ExceptionUtils.getStackTrace(e));
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error when processing 'Schedule of Investments' file", "");
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

    /* Statement of Assets, Liabilities, Partners Capital ****************************/

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
            List<ReportingPEStatementOperations> operationsEntities1 = this.statementOperatinsService.assembleList(operationsRecordsSheet1, 1, reportId); // TODO: tranche type constant !!!
            // sheet 2 - Tranche B
            List<ReportingPEStatementOperations> operationsEntities2 = this.statementOperatinsService.assembleList(operationsRecordsSheet2, 2, reportId); // TODO: tranche type constant !!!

            /* SAVE TO DB **************************************************************************************/
            // BALANCE
            boolean savedBalance = this.statementBalanceService.save(entities1);
            if(savedBalance) {
                savedBalance = this.statementBalanceService.save(entities2);
            }
            if(!savedBalance){
                // TODO: rollback? or transactional?

                logger.error("Error saving 'Schedule of Investments' file data to database (statement of balance)");
                return new FileUploadResultDto(StatusResultType.FAIL, "", "Error saving 'Schedule of Investments' file data to database (statement of balance)", "");
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

                logger.error("Error saving 'Schedule of Investments' file data to database (statement of operations)");
                return new FileUploadResultDto(StatusResultType.FAIL, "", "Error saving 'Schedule of Investments' file data to database (statement of operations)", "");
            }
        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Statement of Assets, Liabilities and Partners Capital' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Statement of Assets, Liabilities and Partners Capital' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error processing 'Statement of Assets, Liabilities and Partners Capital' file'", "");
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

    /* Statement of Cash Flows ********************************************************/
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
                return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - Statement of Cashflows", "");
            }else{
                logger.error("Error saving 'Statement of Cashflows' file parsed data into database");
                return new FileUploadResultDto(StatusResultType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Statement of Cash flows' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Statement of Cash flows' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error processing 'Statement of Cashflows' file'", "");
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

    /* Statement of Changes in Partners' Capital ********************************************************/

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

    @Deprecated
    private List<ConsolidatedReportRecordDto> _checkSumsStatementChanges(List<ConsolidatedReportRecordDto> records){
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

    @Deprecated
    private List<ConsolidatedReportRecordDto> _parseStatementChangesRaw(Iterator<Row> rowIterator){
        int[] indicesWithValues = {2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,18,20,22,24};
        List<ConsolidatedReportRecordDto> records = new ArrayList<>();
        int rowNum = 0;
        while (rowIterator.hasNext()) { // each row
            Row row = rowIterator.next();

            if(rowNum == 0){
                Cell cell = row.getCell(0);
                if (!ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "Tarragon LP")) {
                    logger.error("File header check failed for 'Statement of Changes in Partners Capital' file: row=1, cell=1. Expected 'Tarragon LP'");
                    throw new ExcelFileParseException("File header check failed : row=1, cell=1. Expected 'Tarragon LP'");
                }
            }else if(rowNum == 1){
                Cell cell = row.getCell(0);
                if (!ExcelUtils.isCellStringValueEqualIgnoreCase(cell, "Consolidated Statement of Changes in Partner’s Capital for NICK Master Fund Ltd.")) {
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

                    recordDto.setWithSumFormula(isSumFormulaCell(row.getCell(indicesWithValues[0])));

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

    @Deprecated
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

    /* SINGULARITY GENERAL LEDGER *************************************************************************************/

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
                return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - Singular General Ledger Balance", "");
            }else{
                logger.error("Error saving 'Singular General Ledger Balance' file parsed data into database");
                return new FileUploadResultDto(StatusResultType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Singular General Ledger Balance' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Singular General Ledger Balance' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error processing 'Singular General Ledger Balance' file'", "");
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

    /* SINGULARITY NOAL ***********************************************************************************************/

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
                return new FileUploadResultDto(StatusResultType.SUCCESS, "", "Successfully processed the file - Singular NOAL", "");
            }else{
                logger.error("Error saving 'Singular NOAL' file parsed data into database");
                return new FileUploadResultDto(StatusResultType.FAIL, "", "Error saving to database", "");
            }

        }catch (ExcelFileParseException e) {
            logger.error("Error parsing 'Singular NOAL' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", e.getMessage(), "");
        }catch (Exception e){
            logger.error("Error parsing 'Singular NOAL' file with error: " + e.getMessage());
            return new FileUploadResultDto(StatusResultType.FAIL, "", "Error processing 'Singular NOAL' file'", "");
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
                            logger.error("Error parsing 'Singularity NOAL' file: date is invalid");
                            throw new ExcelFileParseException("Error parsing 'Singularity NOAL' file: date is invalid");
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
        BigDecimal functionalSum = getBigDecimal(0.0).setScale(2, RoundingMode.HALF_UP);
        BigDecimal endingBalanceSum = getBigDecimal(0.0).setScale(2, RoundingMode.HALF_UP);
        if(records != null){
            for(SingularityNOALRecordDto record: records){
                if(record.getTransaction() == null){
                    logger.error(trancheName + "NOAL 'Transaction' value is missing");
                    throw new ExcelFileParseException(trancheName + "NOAL 'Transaction' value is missing");
                } else if(record.getTransaction().equalsIgnoreCase("Ending Balance") || record.getTransaction().equalsIgnoreCase("Ending")){
                    if(endingBalanceSum.setScale(2, RoundingMode.HALF_UP).doubleValue() != NumberUtils.getDouble(record.getFunctionalAmount())){
                        logger.error(trancheName + "NOAL Ending Balance does not match for '" + record.getName() +
                                "': found " + NumberUtils.getDouble(record.getFunctionalAmount()) + ", expected " + endingBalanceSum);
                        throw new ExcelFileParseException(trancheName + "NOAL Ending Balance does not match for '" + record.getName() +
                                "': found " + NumberUtils.getDouble(record.getFunctionalAmount()) + ", expected " + endingBalanceSum);
                    }
                    endingBalanceSum = getBigDecimal(0.0);
                }else if(record.getTransaction().equalsIgnoreCase("REPORT TOTAL")){
                    if(functionalSum.setScale(2, RoundingMode.HALF_UP).doubleValue() != NumberUtils.getDouble(record.getFunctionalAmount())){
                        logger.error(trancheName + "NOAL 'Report Total' does not match: found " +
                                NumberUtils.getDouble(record.getFunctionalAmount()) + ", expected " + functionalSum);
                        throw new ExcelFileParseException(trancheName + "NOAL 'Report Total' does not match: found " +
                                NumberUtils.getDouble(record.getFunctionalAmount()) + ", expected " + functionalSum);
                    }
                    functionalSum = getBigDecimal(0.0);
                }else{
                    endingBalanceSum = endingBalanceSum.add(getBigDecimal(record.getFunctionalAmount()));
                    functionalSum = functionalSum.add(getBigDecimal(record.getFunctionalAmount()));
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
                                totalSum[i] += recordDto.getValues()[i] != null ? recordDto.getValues()[i] : 0.0;
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


    /**
     * Returns BigDecimal instance from the given double value.
     * If value is null, then returns Big Decimal of 0.
     * Sets scale=2 and ROUND_HALF_UP rounding.
     *
     * @param value - double value
     * @return - BigDecimal
     */
    private BigDecimal getBigDecimal(Double value){
        return new BigDecimal(NumberUtils.getDouble(value)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    private void loadForm8(){
        String csvFile = "C:/Users/magzumov/Desktop/07-f-8.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            List<ConsolidatedReportKZTForm8> entities = new ArrayList<>();
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile), "Cp1251"));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length != 10){
                    System.out.println("Error " + data.length);
                    return;
                }
                for(int i = 0; i < 10; i++){
                    if(data[i].equals("null") || data[i].equals("") || data[i].equals("-")){
                        data[i] = null;
                    }else if((i == 3 || i == 4 || i ==  5 || i ==  7 || i ==  8 || i == 9) && data[i] != null){
                        data[i] = data[i].replace(",", ".");
                    }
                }

                ConsolidatedReportKZTForm8 entity = new ConsolidatedReportKZTForm8();
                entity.setAccountNumber(data[0]);
                entity.setName(data[1]);
                entity.setLineNumber(Integer.parseInt(data[2]));
                if(data[3] != null) {
                    entity.setDebtStartPeriod(Double.parseDouble(data[3]));
                }
                if(data[4] != null) {
                    entity.setDebtEndPeriod(Double.parseDouble(data[4]));
                }

                if(data[5] != null) {
                    entity.setDebtDifference(Double.parseDouble(data[5]));
                }

                entity.setAgreementDescription(data[6]);

                Date date = null;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    if(data[7] != null) {
                        date = simpleDateFormat.parse(data[7]);
                    }
                } catch (ParseException e) {
                    //e.printStackTrace();
                }
                entity.setDebtStartDate(date);

                if(data[8] != null) {
                    entity.setStartPeriodBalance(Double.parseDouble(data[8]));
                }
                if(data[9] != null) {
                    entity.setEndPeriodBalance(Double.parseDouble(data[9]));
                }

                entity.setReport(new PeriodicReport(35L));

                entities.add(entity);

            }

            this.consolidatedReportKZTForm8Repository.save(entities);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadForm10(){
        String csvFile = "C:/Users/magzumov/Desktop/07-f-10.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            List<ConsolidatedReportKZTForm10> entities = new ArrayList<>();
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile), "Cp1251"));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length != 8){
                    System.out.println("Error " + data.length);
                    return;
                }
                for(int i = 0; i < 8; i++){
                    if(data[i].equals("null") || data[i].equals("") || data[i].equals("-")){
                        data[i] = null;
                    }else if(i > 2 && data[i] != null){
                        data[i] = data[i].replace(",", ".");
                    }
                }

                ConsolidatedReportKZTForm10 entity = new ConsolidatedReportKZTForm10();
                entity.setAccountNumber(data[0]);
                entity.setName(data[1]);
                entity.setLineNumber(Integer.parseInt(data[2]));

                if(data[3] != null) {
                    entity.setStartPeriodAssets(Double.parseDouble(data[3]));
                }
                if(data[4] != null) {
                    entity.setTurnoverOther(Double.parseDouble(data[4]));
                }

                if(data[5] != null) {
                    entity.setEndPeriodAssets(Double.parseDouble(data[5]));
                }

                if(data[6] != null) {
                    entity.setStartPeriodBalance(Double.parseDouble(data[6]));
                }
                if(data[7] != null) {
                    entity.setEndPeriodBalance(Double.parseDouble(data[7]));
                }

                entity.setReport(new PeriodicReport(35L));

                entities.add(entity);

            }

            this.consolidatedReportKZTForm10Repository.save(entities);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadForm13(){
        List<ConsolidatedReportKZTForm13> entities = new ArrayList<>();
        entities.add(new ConsolidatedReportKZTForm13("Краткосрочные финансовые обязательства (сумма строк 2, 3)", 1));
        entities.add(new ConsolidatedReportKZTForm13("Займы полученные", 2));
        entities.add(new ConsolidatedReportKZTForm13("Прочие краткосрочные финансовые обязательства", 3));
        entities.add(new ConsolidatedReportKZTForm13("Долгосрочные финансовые обязательства (сумма строк 5, 6)", 4));
        entities.add(new ConsolidatedReportKZTForm13("Займы полученные", 5));
        entities.add(new ConsolidatedReportKZTForm13("Прочие долгосрочные финансовые обязательства)", 6));
        entities.add(new ConsolidatedReportKZTForm13("ВСЕГО (сумма строк 1, 4)", 7));
        for(ConsolidatedReportKZTForm13 entity: entities){
            entity.setReport(new PeriodicReport(35L));
        }

        this.consolidatedReportKZTForm13Repository.save(entities);

    }

    private void loadForm14(){
        String csvFile = "C:/Users/magzumov/Desktop/07-f-14.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            List<ConsolidatedReportKZTForm14> entities = new ArrayList<>();
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile), "Cp1251"));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length != 8){
                    System.out.println("Error " + data.length);
                    return;
                }
                for(int i = 0; i < 8; i++){
                    if(data[i].equals("null") || data[i].equals("") || data[i].equals("-")){
                        data[i] = null;
                    }else if((i == 3 || i == 4 || i == 5 || i == 7) && data[i] != null){
                        data[i] = data[i].replace(",", ".");
                    }
                }

                ConsolidatedReportKZTForm14 entity = new ConsolidatedReportKZTForm14();
                entity.setAccountNumber(data[0]);
                entity.setName(data[1]);
                entity.setLineNumber(Integer.parseInt(data[2]));

                if(data[3] != null) {
                    entity.setDebtStartPeriod(Double.parseDouble(data[3]));
                }
                if(data[4] != null) {
                    entity.setDebtEndPeriod(Double.parseDouble(data[4]));
                }

                if(data[5] != null) {
                    entity.setDebtDifference(Double.parseDouble(data[5]));
                }

                entity.setAgreementDescription(data[6]);

                Date date = null;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    if(data[7] != null) {
                        date = simpleDateFormat.parse(data[7]);
                    }
                } catch (ParseException e) {
                    //e.printStackTrace();
                }
                entity.setDebtStartDate(date);


                entity.setReport(new PeriodicReport(35L));

                entities.add(entity);

            }

            this.consolidatedReportKZTForm14Repository.save(entities);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadForm19(){
        String csvFile = "C:/Users/magzumov/Desktop/07-f-19.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            List<ConsolidatedReportKZTForm19> entities = new ArrayList<>();
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile), "Cp1251"));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length != 7){
                    System.out.println("Error " + data.length);
                    return;
                }
                for(int i = 0; i < 7; i++){
                    if(data[i].equals("null") || data[i].equals("") || data[i].equals("-")){
                        data[i] = null;
                    }else if(i > 3 && data[i] != null){
                        data[i] = data[i].replace(",", ".");
                    }
                }

                ConsolidatedReportKZTForm19 entity = new ConsolidatedReportKZTForm19();
                entity.setAccountNumber(data[0]);
                entity.setName(data[1]);
                entity.setLineNumber(Integer.parseInt(data[2]));
                entity.setOtherEntityName(data[3]);

                if(data[4] != null) {
                    entity.setPreviousAccountBalance(Double.parseDouble(data[4]));
                }

                if(data[5] != null) {
                    entity.setTurnover(Double.parseDouble(data[5]));
                }
                if(data[6] != null) {
                    entity.setCurrentAccountBalance(Double.parseDouble(data[6]));
                }

                entity.setReport(new PeriodicReport(35L));

                entities.add(entity);

            }

            this.consolidatedReportKZTForm19Repository.save(entities);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadForm22(){
        String csvFile = "C:/Users/magzumov/Desktop/07-f-22.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            List<ConsolidatedReportKZTForm22> entities = new ArrayList<>();
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile), "Cp1251"));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length != 6){
                    System.out.println("Error " + data.length);
                    return;
                }
                for(int i = 0; i < 6; i++){
                    if(data[i].equals("null") || data[i].equals("") || data[i].equals("-")){
                        data[i] = null;
                    }else if(i > 2 && data[i] != null){
                        data[i] = data[i].replace(",", ".");
                    }
                }

                ConsolidatedReportKZTForm22 entity = new ConsolidatedReportKZTForm22();
                entity.setAccountNumber(data[0]);
                entity.setName(data[1]);
                entity.setLineNumber(Integer.parseInt(data[2]));

                if(data[3] != null) {
                    entity.setPreviousAccountBalance(Double.parseDouble(data[3]));
                }
                if(data[4] != null) {
                    entity.setTurnover(Double.parseDouble(data[4]));
                }

                if(data[5] != null) {
                    entity.setCurrentAccountBalance(Double.parseDouble(data[5]));
                }

                entity.setReport(new PeriodicReport(35L));

                entities.add(entity);

            }

            this.consolidatedReportKZTForm22Repository.save(entities);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadForm1(){
        String csvFile = "C:/Users/magzumov/Desktop/07-f-1.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            List<ConsolidatedReportKZTForm1> entities = new ArrayList<>();
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile), "Cp1251"));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length != 6){
                    System.out.println("Error " + data.length);
                    return;
                }
                for(int i = 0; i < 6; i++){
                    if(data[i].equals("null") || data[i].equals("") || data[i].equals("-")){
                        data[i] = null;
                    }else if(i > 3 && data[i] != null){
                        data[i] = data[i].replace(",", ".");
                    }
                }

                ConsolidatedReportKZTForm1 entity = new ConsolidatedReportKZTForm1();
                entity.setAccountNumber(data[0]);
                entity.setName(data[1]);
                entity.setLineNumber(Integer.parseInt(data[2]));
                entity.setOtherEntityName(data[3]);

                if(data[4] != null) {
                    entity.setCurrentAccountBalance(Double.parseDouble(data[4]));
                }

                if(data[5] != null) {
                    entity.setPreviousAccountBalance(Double.parseDouble(data[5]));
                }

                entity.setReport(new PeriodicReport(35L));

                entities.add(entity);

            }

            this.consolidatedReportKZTForm1Repository.save(entities);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadForm2(){
        String csvFile = "C:/Users/magzumov/Desktop/07-f-2.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            List<ConsolidatedReportKZTForm2> entities = new ArrayList<>();
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile), "Cp1251"));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length != 6){
                    System.out.println("Error " + data.length);
                    return;
                }
                for(int i = 0; i < 6; i++){
                    if(data[i].equals("null") || data[i].equals("") || data[i].equals("-")){
                        data[i] = null;
                    }else if(i > 3 && data[i] != null){
                        data[i] = data[i].replace(",", ".");
                    }
                }

                ConsolidatedReportKZTForm2 entity = new ConsolidatedReportKZTForm2();
                entity.setAccountNumber(data[0]);
                entity.setName(data[1]);
                entity.setLineNumber(Integer.parseInt(data[2]));
                entity.setOtherEntityName(data[3]);

                if(data[4] != null) {
                    entity.setCurrentAccountBalance(Double.parseDouble(data[4]));
                }

                if(data[5] != null) {
                    entity.setPreviousAccountBalance(Double.parseDouble(data[5]));
                }

                entity.setReport(new PeriodicReport(35L));

                entities.add(entity);

            }

            this.consolidatedReportKZTForm2Repository.save(entities);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadForm3(){
        String csvFile = "C:/Users/magzumov/Desktop/07-f-3.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            List<ConsolidatedReportKZTForm3> entities = new ArrayList<>();
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile), "Cp1251"));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length != 5){
                    System.out.println("Error " + data.length);
                    return;
                }
                for(int i = 0; i < 5; i++){
                    if(data[i].equals("null") || data[i].equals("") || data[i].equals("-")){
                        data[i] = null;
                    }else if(i > 2 && data[i] != null){
                        data[i] = data[i].replace(",", ".");
                    }
                }

                ConsolidatedReportKZTForm3 entity = new ConsolidatedReportKZTForm3();
                entity.setName(data[0]);
                entity.setLineNumber(Integer.parseInt(data[1]));
                if(data[2] != null) {
                    entity.setSubLineNumber(Integer.parseInt(data[2]));
                }

                if(data[3] != null) {
                    entity.setCurrentAccountBalance(Double.parseDouble(data[3]));
                }

                if(data[4] != null) {
                    entity.setPreviousAccountBalance(Double.parseDouble(data[4]));
                }

                entity.setReport(new PeriodicReport(35L));

                entities.add(entity);

            }

            this.consolidatedReportKZTForm3Repository.save(entities);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadFormUSD1(){
        String csvFile = "C:/Users/magzumov/Desktop/07-f-usd-1.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            List<ConsolidatedReportUSDFormBalance> entities = new ArrayList<>();
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile), "Cp1251"));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length != 6){
                    System.out.println("Error " + data.length);
                    return;
                }
                for(int i = 0; i < 6; i++){
                    if(data[i].equals("null") || data[i].equals("") || data[i].equals("-")){
                        data[i] = null;
                    }else if(i > 3 && data[i] != null){
                        data[i] = data[i].replace(",", ".");
                    }
                }

                ConsolidatedReportUSDFormBalance entity = new ConsolidatedReportUSDFormBalance();
                entity.setAccountNumber(data[0]);
                entity.setName(data[1]);
                entity.setLineNumber(Integer.parseInt(data[2]));
                entity.setOtherEntityName(data[3]);

                if(data[4] != null) {
                    entity.setCurrentAccountBalance(Double.parseDouble(data[4]));
                }

                if(data[5] != null) {
                    entity.setPreviousAccountBalance(Double.parseDouble(data[5]));
                }

                entity.setReport(new PeriodicReport(35L));

                entities.add(entity);

            }

            this.consolidatedReportUSDFormBalanceRepository.save(entities);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadFormUSD2(){
        String csvFile = "C:/Users/magzumov/Desktop/07-f-usd-2.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            List<ConsolidatedReportUSDFormIncomeExpense> entities = new ArrayList<>();
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile), "Cp1251"));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length != 6){
                    System.out.println("Error " + data.length);
                    return;
                }
                for(int i = 0; i < 6; i++){
                    if(data[i].equals("null") || data[i].equals("") || data[i].equals("-")){
                        data[i] = null;
                    }else if(i > 3 && data[i] != null){
                        data[i] = data[i].replace(",", ".");
                    }
                }

                ConsolidatedReportUSDFormIncomeExpense entity = new ConsolidatedReportUSDFormIncomeExpense();
                entity.setAccountNumber(data[0]);
                entity.setName(data[1]);
                entity.setLineNumber(Integer.parseInt(data[2]));
                entity.setOtherEntityName(data[3]);

                if(data[4] != null) {
                    entity.setCurrentAccountBalance(Double.parseDouble(data[4]));
                }

                if(data[5] != null) {
                    entity.setPreviousAccountBalance(Double.parseDouble(data[5]));
                }

                entity.setReport(new PeriodicReport(35L));

                entities.add(entity);

            }

            this.consolidatedReportUSDFormIncomeExpenseRepository.save(entities);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadFormUSD3(){
        String csvFile = "C:/Users/magzumov/Desktop/07-f-usd-3.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        int count = 0;
        try {

            List<ConsolidatedReportUSDFormTotalIncome> entities = new ArrayList<>();
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFile), "Cp1251"));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);
                if(data.length != 5){
                    System.out.println("Error " + data.length);
                    return;
                }
                for(int i = 0; i < 5; i++){
                    if(data[i].equals("null") || data[i].equals("") || data[i].equals("-")){
                        data[i] = null;
                    }else if(i > 2 && data[i] != null){
                        data[i] = data[i].replace(",", ".");
                    }
                }

                ConsolidatedReportUSDFormTotalIncome entity = new ConsolidatedReportUSDFormTotalIncome();
                entity.setName(data[0]);
                entity.setLineNumber(Integer.parseInt(data[1]));
                if(data[2] != null) {
                    entity.setSubLineNumber(Integer.parseInt(data[2]));
                }

                if(data[3] != null) {
                    entity.setCurrentAccountBalance(Double.parseDouble(data[3]));
                }

                if(data[4] != null) {
                    entity.setPreviousAccountBalance(Double.parseDouble(data[4]));
                }

                entity.setReport(new PeriodicReport(35L));

                entities.add(entity);

            }

            this.consolidatedReportUSDFormTotalIncomeRepository.save(entities);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
