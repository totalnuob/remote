package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.model.HierarchicalBaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.reporting.privateequity.PEGeneralLedgerFormDataRepository;
import kz.nicnbk.repo.api.reporting.privateequity.TarragonNICChartOfAccountsRepository;

import kz.nicnbk.repo.model.lookup.reporting.*;
import kz.nicnbk.repo.model.reporting.PeriodicReport;
import kz.nicnbk.repo.model.reporting.privateequity.PEGeneralLedgerFormData;
import kz.nicnbk.repo.model.reporting.privateequity.TarragonNICChartOfAccounts;
import kz.nicnbk.service.api.reporting.PeriodicReportService;
import kz.nicnbk.service.api.reporting.privateequity.*;
import kz.nicnbk.service.converter.reporting.PEGeneralLedgerFormDataConverter;
import kz.nicnbk.service.dto.common.EntityListSaveResponseDto;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.privateequity.ExcludeTarragonRecordDto;
import kz.nicnbk.service.dto.reporting.privateequity.PEGeneralLedgerFormDataDto;
import kz.nicnbk.service.dto.reporting.privateequity.TarragonGeneratedGeneralLedgerFormDto;
import kz.nicnbk.service.impl.reporting.PeriodicReportConstants;
import kz.nicnbk.service.impl.reporting.lookup.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 17.01.2018.
 */
@Service
public class PeriodicReportPEServiceImpl implements PeriodicReportPEService {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicReportPEServiceImpl.class);

    @Autowired
    private PeriodicReportService periodicReportService;

    @Autowired
    private PEGeneralLedgerFormDataConverter peGeneralLedgerFormDataConverter;


    @Autowired
    private PEGeneralLedgerFormDataRepository peGeneralLedgerFormDataRepository;


    @Autowired
    private TarragonNICChartOfAccountsRepository tarragonNICChartOfAccountsRepository;

    @Autowired
    private PEStatementBalanceService statementBalanceService;

    @Autowired
    private PEStatementOperationsService statementOperationsService;

    @Autowired
    private PEScheduleInvestmentService scheduleInvestmentService;

    @Autowired
    private PEStatementChangesService statementChangesService;

    @Autowired
    private ReserveCalculationService reserveCalculationService;


    @Transactional // if DB operation fails, no record will be saved, i.e. no partial commits
    @Override
    public EntityListSaveResponseDto savePEGeneralLedgerFormData(PEGeneralLedgerFormDataHolderDto dataHolderDto) {
        EntityListSaveResponseDto entityListSaveResponseDto = new EntityListSaveResponseDto();
        try {
            if(dataHolderDto != null && dataHolderDto.getRecords() != null){
                checkPEGeneralLedgerFormData(dataHolderDto.getRecords());
                // check report status
                PeriodicReportDto periodicReport = this.periodicReportService.getPeriodicReport(dataHolderDto.getReport().getId());
                if(periodicReport == null || periodicReport.getStatus().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
                    Long reportId = periodicReport != null ? periodicReport.getId() : null;
                    logger.error("Cannot edit report with status 'SUBMITTED': report id " + reportId);
                    entityListSaveResponseDto.setErrorMessageEn("Cannot edit report with status 'SUBMITTED': report id ");
                    return entityListSaveResponseDto;
                }

                for(PEGeneralLedgerFormDataDto dto: dataHolderDto.getRecords()){
                    PEGeneralLedgerFormData entity = this.peGeneralLedgerFormDataConverter.assemble(dto);
                    entity.setReport(new PeriodicReport(dataHolderDto.getReport().getId()));
                    this.peGeneralLedgerFormDataRepository.save(entity);
                }
                entityListSaveResponseDto.setSuccessMessageEn("Successfully saved records");
                return entityListSaveResponseDto;
            }
        }catch (IllegalArgumentException ex){
            entityListSaveResponseDto.setErrorMessageEn("Input validation failed. " + ex.getMessage());
        }catch (Exception ex){
            logger.error("Error saving Tarragon GL Form data", ex);
            entityListSaveResponseDto.setErrorMessageEn("Error saving Tarragon GL Form data");
        }
        return entityListSaveResponseDto;
    }

    private void checkPEGeneralLedgerFormData(List<PEGeneralLedgerFormDataDto> records){
        if(records != null){
//            double totalAssets = 0.0;
//            double totalOther = 0.0;
            for(PEGeneralLedgerFormDataDto record: records){
//                if(record.getTranche() != 1 && record.getTranche() != 2){
//                    throw new IllegalArgumentException("Tranche value invalid : " + record.getTranche() + "; expected values 1, 2");
//                }
                if(!isValidFinancialStatementCategory(record.getFinancialStatementCategory())){
                    throw new IllegalArgumentException("Financial statement category value invalid : '" + record.getFinancialStatementCategory() + "'; expected values A, L, E, X, I");
                }
                if(StringUtils.isEmpty(record.getTarragonNICChartOfAccountsName())){
                    throw new IllegalArgumentException("Chart oof Accounts Name value missing");
                }else{
                    // check valid ?
                }
                if(record.getGLAccountBalance() == null){
                    throw new IllegalArgumentException("Account Balance value missing");
                }
//                if(record.getFinancialStatementCategory().equalsIgnoreCase("A")){
//                    totalAssets += record.getGLAccountBalance().doubleValue();
//                }else{
//                    totalOther += record.getGLAccountBalance().doubleValue();
//                }
            }

//            double difference = totalAssets - totalOther;
//            if(difference > 2 || difference < -2){
//                throw new IllegalArgumentException("Total Assets (" + totalAssets + ") not equal to Total Other (" + totalOther + "); difference can be between -1 and 1");
//            }
        }

    }

    private boolean isValidFinancialStatementCategory(String value){
        return StringUtils.isNotEmpty(value) && (value.equalsIgnoreCase(GeneralLedgerFinancialStatementCategoryLookup.ASSETS.getCode()) || value.equalsIgnoreCase("L") ||
                value.equalsIgnoreCase("E") || value.equalsIgnoreCase("X") || value.equalsIgnoreCase("I"));
    }

    @Override
    public boolean deletePEGeneralLedgerFormDataRecordById(Long recordId) {
        Long reportId = null;
        try {
            PEGeneralLedgerFormData entity = this.peGeneralLedgerFormDataRepository.findOne(recordId);
            if(entity == null){
                logger.error("No record found to delete: record id " + recordId);
                return false;
            }
            reportId = entity.getReport().getId();
            if (entity.getReport().getStatus() != null) {
                if(entity.getReport().getStatus().getCode().equalsIgnoreCase(PeriodicReportType.SUBMITTED.getCode())){
                    return false;
                }
                this.peGeneralLedgerFormDataRepository.delete(entity);
                return true;
            }
        }catch (Exception ex){
            logger.error("Error deleting PE General Ledger Record: report id " + reportId +  ", record id " + recordId, ex);
        }
        return false;
    }

    @Override
    public List<TarragonGeneratedGeneralLedgerFormDto> getTarragonGLAddedRecordsPreviousMonth(Long reportId) {
        try {
            List<TarragonGeneratedGeneralLedgerFormDto> records = new ArrayList<>();
            PeriodicReportDto currentReport = this.periodicReportService.getPeriodicReport(reportId);
            if (currentReport != null) {
                Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
                PeriodicReportDto previousReport = this.periodicReportService.findReportByReportDate(previousDate);
                if(previousReport != null) {
                    List<PEGeneralLedgerFormData> addedRecords =
                            this.peGeneralLedgerFormDataRepository.getEntitiesByReportId(previousReport.getId(), new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
                    if (addedRecords != null) {
                        for (PEGeneralLedgerFormData entity : addedRecords) {
                            PEGeneralLedgerFormDataDto addedRecordDto = this.peGeneralLedgerFormDataConverter.disassemble(entity);
                            TarragonGeneratedGeneralLedgerFormDto recordDto = new TarragonGeneratedGeneralLedgerFormDto();
                            recordDto.setAcronym(addedRecordDto.getTranche() == 1 ? PETrancheTypeLookup.TARRAGON_A.getNameEn() : PETrancheTypeLookup.TARRAGON_B.getNameEn());
                            if (addedRecordDto.getReport() != null) {
                                recordDto.setBalanceDate(addedRecordDto.getReport().getReportDate());
                            }
                            recordDto.setFinancialStatementCategory(addedRecordDto.getFinancialStatementCategory());
                            recordDto.setChartAccountsLongDescription(addedRecordDto.getTarragonNICChartOfAccountsName());
                            recordDto.setNbAccountNumber(addedRecordDto.getNbAccountNumber());
                            String entityName = addedRecordDto.getEntityName() != null ? addedRecordDto.getEntityName() : "";
                            recordDto.setNicAccountName(addedRecordDto.getNicAccountName() + entityName);
                            recordDto.setSubscriptionRedemptionEntity(entityName);
                            recordDto.setGLAccountBalance(addedRecordDto.getGLAccountBalance());
                            recordDto.setAdded(true);
                            recordDto.setAddedRecordId(entity.getId());
                            records.add(recordDto);
                        }
                    }
                }

                setNICChartOfAccounts(records);
            }
            return records;
        }catch(Exception ex){
            logger.error("Error loading Tarragon Generated form: report id " + reportId, ex);
            return null;
        }
    }

    private boolean hasOtherEntityName(TarragonNICChartOfAccounts accountDto){
        return accountDto != null && accountDto.getNicReportingChartOfAccounts() != null && accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null &&
                accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode() != null &&
                (accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010)
                        || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1183_040)
                        || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6113_030)
                        || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_010)
                        || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6280_010)
                        || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7470_010)
                        || accountDto.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_010));
    }

    private void setNICChartOfAccounts(List<TarragonGeneratedGeneralLedgerFormDto> records){
        if(records != null) {
            for(GeneratedGeneralLedgerFormDto record: records){
                if(record.getNicAccountName() == null) {
                    List<TarragonNICChartOfAccounts> nicChartOfAccounts =
                            this.tarragonNICChartOfAccountsRepository.findByTarragonChartOfAccountsNameAndAddable(record.getChartAccountsLongDescription(), false);
                    if (nicChartOfAccounts != null && !nicChartOfAccounts.isEmpty()) {
                        if(nicChartOfAccounts.size() == 1){
                            TarragonNICChartOfAccounts entity = nicChartOfAccounts.get(0);
                            if(entity.getChartAccountsType() != null && entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.POSITIVE.getCode()) &&
                                    record.getGLAccountBalance() != null && record.getGLAccountBalance() < 0){
                                continue;
                            }else if(entity.getChartAccountsType() != null &&
                                    entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.NEGATIVE.getCode()) &&
                                    record.getGLAccountBalance() != null && record.getGLAccountBalance() >= 0){
                                continue;
                            }
                            if(hasOtherEntityName(entity)){
                                String fundName = StringUtils.isNotEmpty(record.getSubscriptionRedemptionEntity()) ? " " + record.getSubscriptionRedemptionEntity() : "";
                                record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu() + fundName);
                            }else {
                                record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                            }
                            if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                                record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                            }
                        }else if(nicChartOfAccounts.size() == 2){
                            boolean found = false;
                            for(TarragonNICChartOfAccounts entity: nicChartOfAccounts) {
                                if (record.getGLAccountBalance() != null && record.getGLAccountBalance() < 0) {
                                    if (entity.getChartAccountsType() != null &&
                                            entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.NEGATIVE.getCode())) {

                                        if(hasOtherEntityName(entity)){
                                            String fundName = StringUtils.isNotEmpty(record.getSubscriptionRedemptionEntity()) ? " " + record.getSubscriptionRedemptionEntity() : "";
                                            record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu() + fundName);
                                        }else {
                                            record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                                        }
                                        if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                                            record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                                        }
                                        found = true;
                                        break;
                                    }
                                } else if (record.getGLAccountBalance() != null && record.getGLAccountBalance() >= 0) {
                                    if (entity.getChartAccountsType() != null &&
                                            entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.POSITIVE.getCode())) {
                                        if(hasOtherEntityName(entity)){
                                            String fundName = StringUtils.isNotEmpty(record.getSubscriptionRedemptionEntity()) ? " " + record.getSubscriptionRedemptionEntity() : "";
                                            record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu() + fundName);
                                        }else {
                                            record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                                        }
                                        if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                                            record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                                        }
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if(!found){
                                logger.error("Error setting Tarragon NIC Chart of accounts for '" + record.getChartAccountsLongDescription() + "' " +
                                        " : positiveOnly/negativeOnly flags not set properly");
                            }
                        }else{
                            logger.error("Error setting Tarragon NIC Chart of accounts for '" + record.getChartAccountsLongDescription() + "' " +
                                    " : more than 2 mappings found.");
                        }
//                        for(TarragonNICChartOfAccounts entity: nicChartOfAccounts){
//                            if(record.getChartAccountsLongDescription().equalsIgnoreCase("Current tax (expense) benefit")){
//                                if(record.getGLAccountBalance() != null && record.getGLAccountBalance() < 0){
//                                    if(entity.getNegativeOnly() != null && entity.getNegativeOnly().booleanValue()){
//                                        record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
//                                        if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
//                                            record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
//                                        }
//                                    }
//                                }else if(record.getGLAccountBalance() != null && record.getGLAccountBalance() >= 0){
//                                    if(entity.getPositiveOnly() != null && entity.getPositiveOnly().booleanValue()){
//                                        record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
//                                        if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
//                                            record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
//                                        }
//                                    }
//                                }
//
//                            }else {
//                                record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
//                                if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
//                                    record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
//                                }
//                            }
//                        }
                    }else{
                        // no match found
                    }
                }
            }
        }
    }

    @Override
    public ListResponseDto getTarragonGeneratedFormWithExcluded(Long reportId){
        try {
            ListResponseDto responseDto = new ListResponseDto();

            PeriodicReportDto report = this.periodicReportService.getPeriodicReport(reportId);
            if(report == null){
                logger.error("Error getting Tarragon Generated GL Form: report is not found for report id " + reportId);
                responseDto.setErrorMessageEn("Error getting Tarragon Generated GL Form: report is not found for report id " + reportId);
                return responseDto;
            }

            List<TarragonGeneratedGeneralLedgerFormDto> records = new ArrayList<>();

            // Balance and Operations
            List<StatementBalanceOperationsDto> balanceRecords = this.statementBalanceService.getStatementBalanceRecords(reportId);
            records.addAll(processBalance(balanceRecords));

            List<StatementBalanceOperationsDto> operationsRecords = this.statementOperationsService.getStatementOperationsRecords(reportId);
            records.addAll(processOperations(operationsRecords));

            // Investments
            List<ScheduleInvestmentsDto> investments = this.scheduleInvestmentService.getScheduleInvestments(reportId);
            records.addAll(processScheduleInvestments(investments));

            // Statement changes
            List<StatementChangesDto> changes = this.statementChangesService.getStatementChanges(reportId);
            records.addAll(processStatementChanges(changes));

            // update account balance
            List<TarragonGeneratedGeneralLedgerFormDto> updatedRecords = new ArrayList<>();
            for (TarragonGeneratedGeneralLedgerFormDto record : records) {
                if (record.getGLAccountBalance() != null && record.getGLAccountBalance().doubleValue() != 0.0) {
                    if (record.getGLAccountBalance() != null && record.getFinancialStatementCategory() != null &&
                            !record.getFinancialStatementCategory().equalsIgnoreCase(GeneralLedgerFinancialStatementCategoryLookup.ASSETS.getCode())) {
                        if(record.getNbAccountNumber() != null && record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060) &&
                                record.getNicAccountName().equalsIgnoreCase(PeriodicReportConstants.RU_3053_060)){
                            // 3053.060 - Прочие краткосрочные финансовые обязательства
                            // do not change sign, skip

                        }else {
                            Double newValue = MathUtils.multiply(record.getGLAccountBalance() != null ? record.getGLAccountBalance() : 0.0, -1.0);
                            record.setGLAccountBalance(newValue);
                        }
                    }
                    updatedRecords.add(record);
                } else {
                    // skip zero-values
                }
            }

            // From capital calls
            List<ReserveCalculationDto> reserveCalculationRecords =
                    this.reserveCalculationService.getReserveCalculationsForMonth(ReserveCalculationsExpenseTypeLookup.ADD.getCode(), report.getReportDate(), true);
            if(reserveCalculationRecords != null){
                for(ReserveCalculationDto reserveCalculationDto: reserveCalculationRecords){
                    if(reserveCalculationDto.getRecipient() == null || !reserveCalculationDto.getRecipient().getCode().startsWith("TARR")){
                        continue;
                    }
                    TarragonGeneratedGeneralLedgerFormDto recordDto = new TarragonGeneratedGeneralLedgerFormDto();
                    String acronym = reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TARRAGON_A.getCode()) ? PETrancheTypeLookup.TARRAGON_A.getNameEn() :
                            reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TARRAGON_B.getCode()) ? PETrancheTypeLookup.TARRAGON_B.getNameEn() :
                                    reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TARRAGON_A2.getCode()) ? PETrancheTypeLookup.TARRAGON_A2.getNameEn():
                                            reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TARRAGON_B2.getCode()) ? PETrancheTypeLookup.TARRAGON_B2.getNameEn():
                                                    "TARRAGON";
                    if(acronym.equalsIgnoreCase("TARRAGON")) {
                        logger.error("Error generating Tarragon GL Form: capital call recipient is specified as 'Tarragon', must be either 'Tarragon A' or 'Tarragon B'");
                        responseDto.setErrorMessageEn("Error generating Tarragon GL Form: capital call recipient is specified as 'Tarragon', must be either 'Tarragon A' or 'Tarragon B'");
                        return responseDto;
                    }
                    recordDto.setAcronym(acronym);
                    recordDto.setBalanceDate(report.getReportDate());

                    Double amount = reserveCalculationDto.getAmountToSPV() != null ?
                            reserveCalculationDto.getAmountToSPV() : reserveCalculationDto.getAmount();
                    recordDto.setGLAccountBalance(amount);
                    recordDto.setAdded(false);
                    recordDto.setEditable(false);

                    recordDto.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.ASSETS.getCode());
                    recordDto.setChartAccountsLongDescription(TarragonNICChartAccountsLookup.CC_CASH_ADJ.getNameEn());
                    recordDto.setNbAccountNumber(NICChartAccountsLookup.CURRENT_ACCOUNT_CASH.getCodeNBRK());
                    recordDto.setNicAccountName(NICChartAccountsLookup.CURRENT_ACCOUNT_CASH.getNameRu());

                    recordDto.setId(reserveCalculationDto.getId());
                    recordDto.setType(TarragonExcludeRecordTypeLookup.CAPITAL_CALL.getCode());
                    recordDto.setExcludeFromTarragonCalculation(reserveCalculationDto.getExcludeFromTarragonCalculation());

                    updatedRecords.add(recordDto);

                    TarragonGeneratedGeneralLedgerFormDto recordDtoOpposite = new TarragonGeneratedGeneralLedgerFormDto();
                    recordDtoOpposite.setAcronym(acronym);
                    recordDtoOpposite.setBalanceDate(report.getReportDate());
                    recordDtoOpposite.setGLAccountBalance(MathUtils.subtract(0.0, amount));
                    recordDtoOpposite.setAdded(false);
                    recordDtoOpposite.setEditable(false);

                    recordDtoOpposite.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.EQUITY.getCode());
                    recordDtoOpposite.setChartAccountsLongDescription(TarragonNICChartAccountsLookup.CC_CAPITAL_ADJ.getNameEn());
                    recordDtoOpposite.setNbAccountNumber(NICChartAccountsLookup.SHAREHOLDER_EQUITY_NBRK.getCodeNBRK());
                    recordDtoOpposite.setNicAccountName(NICChartAccountsLookup.SHAREHOLDER_EQUITY_NBRK.getNameRu());

                    recordDtoOpposite.setId(reserveCalculationDto.getId());
                    recordDtoOpposite.setType(TarragonExcludeRecordTypeLookup.CAPITAL_CALL.getCode());
                    recordDtoOpposite.setExcludeFromTarragonCalculation(reserveCalculationDto.getExcludeOppositeFromTarragonCalculation());
                    updatedRecords.add(recordDtoOpposite);
                }

            }


            // Added records
            Double netRealizedTrancheA = 0.0;
            Double netRealizedTrancheB = 0.0;
            List<PEGeneralLedgerFormData> addedRecods =
                    this.peGeneralLedgerFormDataRepository.getEntitiesByReportId(reportId, new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, "id")));
            if (addedRecods != null) {
                for (PEGeneralLedgerFormData entity : addedRecods) {
                    PEGeneralLedgerFormDataDto addedRecordDto = this.peGeneralLedgerFormDataConverter.disassemble(entity);
                    TarragonGeneratedGeneralLedgerFormDto recordDto = new TarragonGeneratedGeneralLedgerFormDto();
                    if(entity.getTrancheType() != null){
                        recordDto.setAcronym(entity.getTrancheType().getNameEn());
                    }else if(recordDto.getAcronym() == null && addedRecordDto.getTranche() > 0) {
                        recordDto.setAcronym(addedRecordDto.getTranche() == 1 ? PETrancheTypeLookup.TARRAGON_A.getNameEn() : PETrancheTypeLookup.TARRAGON_B.getNameEn());
                    }

                    if (addedRecordDto.getReport() != null) {
                        recordDto.setBalanceDate(addedRecordDto.getReport().getReportDate());
                    }
                    recordDto.setFinancialStatementCategory(addedRecordDto.getFinancialStatementCategory());
                    recordDto.setChartAccountsLongDescription(addedRecordDto.getTarragonNICChartOfAccountsName());
                    recordDto.setNbAccountNumber(addedRecordDto.getNbAccountNumber());
                    String entityName = StringUtils.isNotEmpty(addedRecordDto.getEntityName()) ? " " + addedRecordDto.getEntityName() : "";
                    recordDto.setNicAccountName(addedRecordDto.getNicAccountName() + entityName);
                    recordDto.setSubscriptionRedemptionEntity(entityName);
                    recordDto.setGLAccountBalance(addedRecordDto.getGLAccountBalance());
                    recordDto.setAdded(true);
                    recordDto.setAddedRecordId(entity.getId());
                    updatedRecords.add(recordDto);

                    if (recordDto.getChartAccountsLongDescription().equalsIgnoreCase("Net Realized Gains/Losses from Portfolio Funds")) {
                        switch (recordDto.getAcronym()) {
                            case "Tarragon A":
                                netRealizedTrancheA += recordDto.getGLAccountBalance();
                                break;
                            case "Tarragon A-2":
                                netRealizedTrancheA += recordDto.getGLAccountBalance();
                                break;
                            case "Tarragon B":
                                netRealizedTrancheB += recordDto.getGLAccountBalance();
                                break;
                            case "Tarragon B-2":
                                netRealizedTrancheB += recordDto.getGLAccountBalance();
                                break;
                        }
                    }
                }
            }

            setNICChartOfAccounts(updatedRecords);
            Collections.sort(updatedRecords);


            // Check Net Realized Gains Losses
            // UPDATE: "Net realized gain on investments" record added from Statement of Operations, no need to add Net realized records
//            if (operationsRecords != null) {
//                for (StatementBalanceOperationsDto record : operationsRecords) {
//                    if (record.getName().equalsIgnoreCase("Net realized gain on investments")) {
//                        double value = record.getNICKMFShareConsolidated() != null ? record.getNICKMFShareConsolidated().doubleValue() : 0.0;
//                        if (record.getTranche() == 1 && ((netRealizedTrancheA + value) < -2 ||
//                                (netRealizedTrancheA + value) > 2)) {
//                            String errorMessage = "{Tranche A] Statement of operations 'Net realized gain on investments' = " + value +
//                                    ", sum of net realized gains/losses = " + netRealizedTrancheA;
//                            logger.error(errorMessage);
//                            responseDto.setErrorMessageEn(errorMessage);
//                            break;
//                        } else if (record.getTranche() == 2 && ((netRealizedTrancheB + value) < -2 ||
//                                (netRealizedTrancheB + value) > 2)) {
//                            String errorMessage = "{Tranche B] Statement of operations 'Net realized gain on investments' = " + value +
//                                    ", sum of net realized gains/losses = " + netRealizedTrancheB;
//                            logger.error(errorMessage);
//                            responseDto.setErrorMessageEn(errorMessage);
//                            break;
//                        }
//                    }
//                }
//            }

            responseDto.setRecords(updatedRecords);
            responseDto.setStatus(ResponseStatusType.SUCCESS);
            return responseDto;
        }catch (Exception ex){
            logger.error("Error loading Tarragon Generated form: report id " + reportId, ex);
            return null;
        }
    }

    @Override
    public ListResponseDto getTarragonGeneratedFormWithoutExcluded(Long reportId) {
        ListResponseDto responseDto = getTarragonGeneratedFormWithExcluded(reportId);

        if(responseDto.getStatus() == null || responseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.SUCCESS.getCode())){
            if(responseDto.getRecords() != null){
                List<TarragonGeneratedGeneralLedgerFormDto> newRecords = new ArrayList<>();
                for(TarragonGeneratedGeneralLedgerFormDto record: (List<TarragonGeneratedGeneralLedgerFormDto>) responseDto.getRecords()){
                    if(record.getExcludeFromTarragonCalculation() == null || !record.getExcludeFromTarragonCalculation().booleanValue()){
                        newRecords.add(record);
                    }
                }
                responseDto.setRecords(newRecords);
            }
        }

        return responseDto;
    }

    @Override
    public boolean excludeIncludeTarragonRecord(ExcludeTarragonRecordDto recordDto, String username) {
        try {
            if (recordDto.getType() == null) {
                return false;
            }
            boolean result = false;
            if (recordDto.getType().equalsIgnoreCase(TarragonExcludeRecordTypeLookup.CAPITAL_CALL.getCode())) {
                result = this.reserveCalculationService.excludeIncludeRecord(recordDto.getRecordId(), recordDto.getName(), ExcludeRecordTypeLookup.TARRAGON);
            } else if (recordDto.getType().equalsIgnoreCase(TarragonExcludeRecordTypeLookup.STATEMENT_BALANCE.getCode())) {
                result = this.statementBalanceService.excludeIncludeTarragonRecord(recordDto.getRecordId());
                //return result;
            } else if (recordDto.getType().equalsIgnoreCase(TarragonExcludeRecordTypeLookup.STATEMENT_OPERATIONS.getCode())) {
                result = this.statementOperationsService.excludeIncludeTarragonRecord(recordDto.getRecordId());
                //return result;
            } else if (recordDto.getType().equalsIgnoreCase(TarragonExcludeRecordTypeLookup.STATEMENT_CHANGES.getCode())) {
                result = this.statementChangesService.excludeIncludeTarragonRecord(recordDto.getRecordId());
                //return result;
            } else if (recordDto.getType().equalsIgnoreCase(TarragonExcludeRecordTypeLookup.SCHEDULE_INVESTMENTS.getCode())) {
                result = this.scheduleInvestmentService.excludeIncludeTarragonRecord(recordDto.getRecordId());
                //return result;
            }
            if(result){
                logger.info("Successfully excluded/included Tarragon record: id=" + recordDto.getRecordId() + ", type=" + recordDto.getType() + ", name='" + recordDto.getName() + "' [user " + username + "]");
            }else{
                logger.error("Error excluding/including Tarragon record: id=" + recordDto.getRecordId() + ", type=" + recordDto.getType() + ", name='"  + recordDto.getName() + "' [user " + username + "]");
            }
            return result;
        }catch (Exception ex){
            logger.error("Error when including/excluding Tarragon record: id=" + recordDto.getRecordId() + ", type=" + recordDto.getType(), ex);
            return false;
        }
    }

    private List<TarragonGeneratedGeneralLedgerFormDto> processStatementChanges(List<StatementChangesDto> changes){
        List<TarragonGeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(changes != null){
            for(StatementChangesDto dto: changes){
                if(dto.getTotalSum() != null && dto.getTotalSum().booleanValue()){
                    // skip total sums
                    continue;
                }
//                if(dto.getName().equalsIgnoreCase("Capital Contributions") || dto.getName().equalsIgnoreCase("Distributions") ||
//                        dto.getName().equalsIgnoreCase("Unrealized Gain (Loss)") ||
//                        dto.getName().equalsIgnoreCase("Unrealized Gain (Loss), Net of Related Taxes")){

                if(dto.getName().equalsIgnoreCase(PeriodicReportConstants.TARR_STATEMENT_CHANGES_CONTRIBUTIONS_RECEIVED) ||
                        dto.getName().equalsIgnoreCase(PeriodicReportConstants.TARR_STATEMENT_CHANGES_DISTRIBUTIONS_PAID)){

                    TarragonGeneratedGeneralLedgerFormDto trancheARecord = new TarragonGeneratedGeneralLedgerFormDto();
                    trancheARecord.setAcronym(PETrancheTypeLookup.TARRAGON_A.getNameEn());
                    trancheARecord.setBalanceDate(dto.getReport().getReportDate());
                    trancheARecord.setChartAccountsLongDescription(dto.getName());
                    trancheARecord.setFinancialStatementCategory("E");
                    trancheARecord.setGLAccountBalance(dto.getTrancheA());
                    trancheARecord.setId(dto.getId());
                    trancheARecord.setExcludeFromTarragonCalculation(dto.getExcludeFromTarragonCalculation());
                    trancheARecord.setType(TarragonExcludeRecordTypeLookup.STATEMENT_CHANGES.getCode());
                    records.add(trancheARecord);

                    TarragonGeneratedGeneralLedgerFormDto trancheBRecord = new TarragonGeneratedGeneralLedgerFormDto(trancheARecord);
                    trancheBRecord.setAcronym(PETrancheTypeLookup.TARRAGON_B.getNameEn());
                    trancheBRecord.setGLAccountBalance(dto.getTrancheB());
                    trancheBRecord.setExcludeFromTarragonCalculation(dto.getExcludeFromTarragonCalculation());
                    trancheBRecord.setType(TarragonExcludeRecordTypeLookup.STATEMENT_CHANGES.getCode());
                    records.add(trancheBRecord);

                    TarragonGeneratedGeneralLedgerFormDto trancheA2Record = new TarragonGeneratedGeneralLedgerFormDto(trancheARecord);
                    trancheA2Record.setAcronym(PETrancheTypeLookup.TARRAGON_A2.getNameEn());
                    trancheA2Record.setGLAccountBalance(dto.getTrancheA2());
                    trancheA2Record.setExcludeFromTarragonCalculation(dto.getExcludeFromTarragonCalculation());
                    trancheA2Record.setType(TarragonExcludeRecordTypeLookup.STATEMENT_CHANGES.getCode());
                    records.add(trancheA2Record);

                    TarragonGeneratedGeneralLedgerFormDto trancheB2Record = new TarragonGeneratedGeneralLedgerFormDto(trancheARecord);
                    trancheB2Record.setAcronym(PETrancheTypeLookup.TARRAGON_B2.getNameEn());
                    trancheB2Record.setGLAccountBalance(dto.getTrancheB2());
                    trancheB2Record.setExcludeFromTarragonCalculation(dto.getExcludeFromTarragonCalculation());
                    trancheB2Record.setType(TarragonExcludeRecordTypeLookup.STATEMENT_CHANGES.getCode());
                    records.add(trancheB2Record);
                }

            }
        }
        return records;
    }

    private List<TarragonGeneratedGeneralLedgerFormDto> processOperations(List<StatementBalanceOperationsDto> operationsRecords){
        List<TarragonGeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(operationsRecords != null){
            for(StatementBalanceOperationsDto operationsRecord: operationsRecords){
                if(operationsRecord.getTotalSum() != null && operationsRecord.getTotalSum().booleanValue()){
                    // skip total sums
                    continue;
                }
                TarragonGeneratedGeneralLedgerFormDto record = new TarragonGeneratedGeneralLedgerFormDto();
                if(isIncome(operationsRecord)){
                    record.setFinancialStatementCategory("I");
                }else if(isExpenses(operationsRecord)){
                    record.setFinancialStatementCategory("X");
                }// TODO: controlled list of accepted/matched values
                else if(operationsRecord.getName().equalsIgnoreCase(PeriodicReportConstants.TARR_OPERATIONS_INCOME_TYPE_NET_REALIZED_GAIN_NAME) ||
                        operationsRecord.getName().equalsIgnoreCase(PeriodicReportConstants.TARR_OPERATIONS_INCOME_TYPE_NET_REALIZED_GAIN_LOSS_LONG_NAME) ||
                        operationsRecord.getName().equalsIgnoreCase(PeriodicReportConstants.TARR_OPERATIONS_INCOME_TYPE_DEFERRED_TAX_BENEFIT_NAME) ||
                        operationsRecord.getName().equalsIgnoreCase(PeriodicReportConstants.TARR_OPERATIONS_INCOME_TYPE_NET_CHANGE_UNREALIZED_APPR_DEPR_LONG_NAME)) {
                    record.setFinancialStatementCategory("I");
                }else{
                    continue;
                }
//                if(isEquity(balanceRecord)){
//                    record.setFinancialStatementCategory("E");
//                }

                String acronym = operationsRecord.getTrancheType() != null &&
                        operationsRecord.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_A.getCode()) ? PETrancheTypeLookup.TARRAGON_A.getNameEn() :
                        operationsRecord.getTrancheType() != null &&
                        operationsRecord.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_B.getCode()) ? PETrancheTypeLookup.TARRAGON_B.getNameEn() :
                        operationsRecord.getTrancheType() != null &&
                        operationsRecord.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_A2.getCode()) ? PETrancheTypeLookup.TARRAGON_A2.getNameEn() :
                        operationsRecord.getTrancheType() != null &&
                        operationsRecord.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_B2.getCode()) ? PETrancheTypeLookup.TARRAGON_B2.getNameEn() : "UNMATCHED";
                if(operationsRecord.getTrancheType() == null && operationsRecord.getTranche() != null
                        && acronym.equalsIgnoreCase("UNMATCHED")){
                    acronym = operationsRecord.getTranche().intValue() == 1 ? PETrancheTypeLookup.TARRAGON_A.getNameEn() : PETrancheTypeLookup.TARRAGON_B.getNameEn();
                }
                record.setAcronym(acronym);
                record.setBalanceDate(operationsRecord.getReport().getReportDate());
                record.setChartAccountsLongDescription(operationsRecord.getName());
                Double accountBalance = operationsRecord.getNICKMFShareConsolidated();
                record.setGLAccountBalance(accountBalance);
                record.setId(operationsRecord.getId());
                record.setExcludeFromTarragonCalculation(operationsRecord.getExcludeFromTarragonCalculation());
                record.setType(TarragonExcludeRecordTypeLookup.STATEMENT_OPERATIONS.getCode());

                records.add(record);
            }
        }
        return records;
    }

    private List<TarragonGeneratedGeneralLedgerFormDto> processBalance(List<StatementBalanceOperationsDto> balanceRecords){
        List<TarragonGeneratedGeneralLedgerFormDto> records = new ArrayList<>();
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
                TarragonGeneratedGeneralLedgerFormDto record = new TarragonGeneratedGeneralLedgerFormDto();
                if(isAssets(balanceRecord)){
                    record.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.ASSETS.getCode());
                }else if(isLiabilities(balanceRecord)){
                    record.setFinancialStatementCategory("L");
                }else{
                    // ?
                }

                String acronym = balanceRecord.getTrancheType() != null &&
                        balanceRecord.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_A.getCode()) ? PETrancheTypeLookup.TARRAGON_A.getNameEn():
                        balanceRecord.getTrancheType() != null &&
                        balanceRecord.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_B.getCode()) ? PETrancheTypeLookup.TARRAGON_B.getNameEn() :
                        balanceRecord.getTrancheType() != null &&
                        balanceRecord.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_A2.getCode()) ? PETrancheTypeLookup.TARRAGON_A2.getNameEn() :
                        balanceRecord.getTrancheType() != null &&
                        balanceRecord.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_B2.getCode()) ? PETrancheTypeLookup.TARRAGON_B2.getNameEn() : "UNMATCHED";
                if(balanceRecord.getTrancheType() == null && balanceRecord.getTranche() != null
                        && acronym.equalsIgnoreCase("UNMATCHED")){
                    acronym = balanceRecord.getTranche().intValue() == 1 ? PETrancheTypeLookup.TARRAGON_A.getNameEn() : PETrancheTypeLookup.TARRAGON_B.getNameEn();
                }
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
                record.setExcludeFromTarragonCalculation(balanceRecord.getExcludeFromTarragonCalculation());
                record.setId(balanceRecord.getId());
                record.setType(TarragonExcludeRecordTypeLookup.STATEMENT_BALANCE.getCode());

                records.add(record);
            }
        }
        return records;
    }

    private List<TarragonGeneratedGeneralLedgerFormDto>  processScheduleInvestments(List<ScheduleInvestmentsDto> investments){
        List<TarragonGeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(investments != null ){
            boolean madeEditable = false;
            for(ScheduleInvestmentsDto investment: investments){
                if(investment.getTotalSum() == null || !investment.getTotalSum().booleanValue()) { // total sum records not added
                    String acronym = investment.getTrancheType() != null &&
                            investment.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_A.getCode()) ? PETrancheTypeLookup.TARRAGON_A.getNameEn() :
                            investment.getTrancheType() != null &&
                            investment.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_B.getCode()) ? PETrancheTypeLookup.TARRAGON_B.getNameEn() :
                            investment.getTrancheType() != null &&
                            investment.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_A2.getCode()) ? PETrancheTypeLookup.TARRAGON_A2.getNameEn() :
                            investment.getTrancheType() != null &&
                            investment.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_B2.getCode()) ? PETrancheTypeLookup.TARRAGON_B2.getNameEn() : "UNMATCHED";

                    if(investment.getTrancheType() == null && investment.getTranche() != null
                            && acronym.equalsIgnoreCase("UNMATCHED")){
                        acronym = investment.getTranche().intValue() == 1 ? PETrancheTypeLookup.TARRAGON_A.getNameEn() : PETrancheTypeLookup.TARRAGON_B.getNameEn();
                    }
                    TarragonGeneratedGeneralLedgerFormDto record = new TarragonGeneratedGeneralLedgerFormDto();
                    record.setAcronym(acronym);
                    record.setBalanceDate(investment.getReport().getReportDate());
                    record.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.ASSETS.getCode());
                    record.setChartAccountsLongDescription(investment.getName());
                    record.setSegVal1("1200");
                    //record.setNbAccountNumber(PeriodicReportConstants.ACC_NUM_2033_010);
                    record.setNbAccountNumber(PeriodicReportConstants.ACC_NUM_1123_010);
                    record.setNicAccountName(PeriodicReportConstants.RU_PE_FUND_INVESTMENT + " " + investment.getName());
                    Double accountBalance = investment.getFairValue() != null && investment.getTrancheType() != null ?
                            (investment.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_A.getCode()) ||
                            investment.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_A2.getCode())
                                    ? MathUtils.multiply(investment.getFairValue().doubleValue(), 0.99) :
                                    investment.getFairValue().doubleValue()) : null;
                    record.setGLAccountBalance(investment.getEditedFairValue() != null ? investment.getEditedFairValue() : accountBalance);


                    if(!madeEditable && investment.getTrancheType() != null &&
                            (investment.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_A.getCode()) ||
                            investment.getTrancheType().getCode().equalsIgnoreCase(PETrancheTypeLookup.TARRAGON_A2.getCode()))){
                        record.setEditable(true);
                        madeEditable = true;
                    }

                    if(record.getGLAccountBalance() != null && record.getGLAccountBalance() < 0){
                        //record.setGLAccountBalance(MathUtils.multiply(record.getGLAccountBalance(), -1.0));
                        record.setNbAccountNumber(PeriodicReportConstants.ACC_NUM_3053_060);
                        record.setFinancialStatementCategory("L");
                        record.setNicAccountName(PeriodicReportConstants.RU_3053_060);
                        record.setSubscriptionRedemptionEntity((StringUtils.isNotEmpty(acronym) ? acronym + " - " : "") + investment.getName());
                    }
                    record.setId(investment.getId());
                    record.setExcludeFromTarragonCalculation(investment.getExcludeFromTarragonCalculation());
                    record.setType(TarragonExcludeRecordTypeLookup.SCHEDULE_INVESTMENTS.getCode());
                    records.add(record);
                }
            }
        }
        return records;
    }

    private boolean isAssets(StatementBalanceOperationsDto dto){
        if(dto != null){
            HierarchicalBaseDictionaryDto type = dto.getType();
            while(type != null){
                if(type.getCode().equalsIgnoreCase(PEBalanceTypeLookup.ASSETS.getCode())){
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
                if(type.getCode().equalsIgnoreCase(PEBalanceTypeLookup.LIABILITIES.getCode())){
                    return true;
                }
                type = type.getParent();
            }
        }
        return false;
    }

    private boolean isIncome(StatementBalanceOperationsDto dto){
        if(dto != null){
            HierarchicalBaseDictionaryDto type = dto.getType();
            while(type != null){
                if(type.getCode().equalsIgnoreCase(PEOperationsTypeLookup.INCOME.getCode()) &&
                        !dto.getName().equalsIgnoreCase(PeriodicReportConstants.TARR_OPERATIONS_TAX_EXPENSE_NAME)){
                    return true;
                }
                type = type.getParent();
            }
        }
        return false;
    }

    private boolean isExpenses(StatementBalanceOperationsDto dto){
        if(dto != null){
            if(dto.getName() != null && dto.getName().equalsIgnoreCase(PeriodicReportConstants.TARR_OPERATIONS_TAX_EXPENSE_NAME)){
                return true;
            }
            HierarchicalBaseDictionaryDto type = dto.getType();
            while(type != null){
                if(type.getCode().equalsIgnoreCase(PEOperationsTypeLookup.EXPENSES.getCode())){
                    return true;
                }
                type = type.getParent();
            }
        }
        return false;
    }
}
