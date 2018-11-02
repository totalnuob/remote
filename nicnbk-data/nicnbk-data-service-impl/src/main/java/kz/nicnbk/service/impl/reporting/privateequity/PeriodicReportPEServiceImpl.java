package kz.nicnbk.service.impl.reporting.privateequity;

import kz.nicnbk.common.service.model.HierarchicalBaseDictionaryDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.MathUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.reporting.privateequity.PEGeneralLedgerFormDataRepository;
import kz.nicnbk.repo.api.reporting.privateequity.TarragonNICChartOfAccountsRepository;

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
import kz.nicnbk.service.dto.reporting.privateequity.PEGeneralLedgerFormDataDto;
import kz.nicnbk.service.impl.reporting.PeriodicReportConstants;
import kz.nicnbk.service.impl.reporting.lookup.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
                if(record.getTranche() != 1 && record.getTranche() != 2){
                    throw new IllegalArgumentException("Tranche value invalid : " + record.getTranche() + "; expected values 1, 2");
                }
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
        return StringUtils.isNotEmpty(value) && (value.equalsIgnoreCase("A") || value.equalsIgnoreCase("L") ||
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
    public List<GeneratedGeneralLedgerFormDto> getTarragonGLAddedRecordsPreviousMonth(Long reportId) {
        try {
            List<GeneratedGeneralLedgerFormDto> records = new ArrayList<>();
            PeriodicReportDto currentReport = this.periodicReportService.getPeriodicReport(reportId);
            if (currentReport != null) {
                Date previousDate = DateUtils.getLastDayOfPreviousMonth(currentReport.getReportDate());
                PeriodicReportDto previousReport = this.periodicReportService.findReportByReportDate(previousDate);
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
                        String entityName = addedRecordDto.getEntityName() != null ? addedRecordDto.getEntityName() : "";
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
        }catch(Exception ex){
            logger.error("Error loading Tarragon Generated form: report id " + reportId, ex);
            return null;
        }
    }

    private void setNICChartOfAccounts(List<GeneratedGeneralLedgerFormDto> records){
        if(records != null) {
            for(GeneratedGeneralLedgerFormDto record: records){
                if(record.getNicAccountName() == null) {
                    List<TarragonNICChartOfAccounts> nicChartOfAccounts =
                            this.tarragonNICChartOfAccountsRepository.findByTarragonChartOfAccountsNameAndAddable(record.getChartAccountsLongDescription(), false);
                    if (nicChartOfAccounts != null && !nicChartOfAccounts.isEmpty()) {
                        if(nicChartOfAccounts.size() == 1){
                            TarragonNICChartOfAccounts entity = nicChartOfAccounts.get(0);
                            if(entity.getPositiveOnly() != null && entity.getPositiveOnly().booleanValue() &&
                                    record.getGLAccountBalance() != null && record.getGLAccountBalance() < 0){
                                continue;
                            }else if(entity.getNegativeOnly() != null && entity.getNegativeOnly().booleanValue() &&
                                    record.getGLAccountBalance() != null && record.getGLAccountBalance() >= 0){
                                continue;
                            }
                            record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                            if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                                record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                            }
                        }else if(nicChartOfAccounts.size() == 2){
                            boolean found = false;
                            for(TarragonNICChartOfAccounts entity: nicChartOfAccounts) {
                                if (record.getGLAccountBalance() != null && record.getGLAccountBalance() < 0) {
                                    if (entity.getNegativeOnly() != null && entity.getNegativeOnly().booleanValue()) {
                                        record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                                        if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                                            record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                                        }
                                        found = true;
                                        break;
                                    }
                                } else if (record.getGLAccountBalance() != null && record.getGLAccountBalance() >= 0) {
                                    if (entity.getPositiveOnly() != null && entity.getPositiveOnly().booleanValue()) {
                                        record.setNicAccountName(entity.getNicReportingChartOfAccounts().getNameRu());
                                        if (entity.getNicReportingChartOfAccounts().getNbChartOfAccounts() != null) {
                                            record.setNbAccountNumber(entity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode());
                                        }
                                    }
                                    found = true;
                                    break;
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
    public ListResponseDto getTarragonGeneratedForm(Long reportId){
        try {
            ListResponseDto responseDto = new ListResponseDto();

            PeriodicReportDto report = this.periodicReportService.getPeriodicReport(reportId);
            if(report == null){
                logger.error("Error getting Tarragon Generated GL Form: report is not found for report id " + reportId);
                responseDto.setErrorMessageEn("Error getting Tarragon Generated GL Form: report is not found for report id " + reportId);
                return responseDto;
            }

            List<GeneratedGeneralLedgerFormDto> records = new ArrayList<>();

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
            List<GeneratedGeneralLedgerFormDto> updatedRecords = new ArrayList<>();
            //BigDecimal sum = new BigDecimal("0");
            //Double sum = 0.0;
            for (GeneratedGeneralLedgerFormDto record : records) {
                if (record.getGLAccountBalance() != null && record.getGLAccountBalance().doubleValue() != 0.0) {
                    if (record.getGLAccountBalance() != null && record.getFinancialStatementCategory() != null &&
                            !record.getFinancialStatementCategory().equalsIgnoreCase("A")) {
                        if(record.getNbAccountNumber() != null && record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_3053_060) &&
                                record.getNicAccountName().equalsIgnoreCase(PeriodicReportConstants.RU_3053_060)){
                            // 3053.060 - Прочие краткосрочные финансовые обязательства
                            // do not change sign, skip

                        }else {
                            Double newValue = MathUtils.multiply(record.getGLAccountBalance() != null ? record.getGLAccountBalance() : 0.0, -1.0);
                            record.setGLAccountBalance(newValue);
                            //sum = MathUtils.add(sum, newValue);
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
                    GeneratedGeneralLedgerFormDto recordDto = new GeneratedGeneralLedgerFormDto();
                    String acronym = reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TARRAGON_A.getCode()) ? "TARRAGON A" :
                            reserveCalculationDto.getRecipient().getCode().equalsIgnoreCase(ReserveCalculationsEntityTypeLookup.TARRAGON_B.getCode()) ? "TARRAGON B" : "TARRAGON";
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

                    updatedRecords.add(recordDto);

                    GeneratedGeneralLedgerFormDto recordDtoOpposite = new GeneratedGeneralLedgerFormDto();
                    recordDtoOpposite.setAcronym(acronym);
                    recordDtoOpposite.setBalanceDate(report.getReportDate());
                    recordDtoOpposite.setGLAccountBalance(MathUtils.subtract(0.0, amount));
                    recordDtoOpposite.setAdded(false);
                    recordDtoOpposite.setEditable(false);

                    recordDtoOpposite.setFinancialStatementCategory(GeneralLedgerFinancialStatementCategoryLookup.EQUITY.getCode());
                    recordDtoOpposite.setChartAccountsLongDescription(TarragonNICChartAccountsLookup.CC_CAPITAL_ADJ.getNameEn());
                    recordDtoOpposite.setNbAccountNumber(NICChartAccountsLookup.SHAREHOLDER_EQUITY_NBRK.getCodeNBRK());
                    recordDtoOpposite.setNicAccountName(NICChartAccountsLookup.SHAREHOLDER_EQUITY_NBRK.getNameRu());

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
                    GeneratedGeneralLedgerFormDto recordDto = new GeneratedGeneralLedgerFormDto();
                    recordDto.setAcronym(addedRecordDto.getTranche() == 1 ? "TARRAGON" : "TARRAGON B");
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
                }else if(balanceRecord.getName().equalsIgnoreCase("Net realized gain on investments")) {
                    record.setFinancialStatementCategory("I");
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

    private List<GeneratedGeneralLedgerFormDto>  processScheduleInvestments(List<ScheduleInvestmentsDto> investments){
        List<GeneratedGeneralLedgerFormDto> records = new ArrayList<>();
        if(investments != null ){
            boolean madeEditable = false;
            for(ScheduleInvestmentsDto investment: investments){
                if(investment.getTotalSum() != null && !investment.getTotalSum()) { // total sum records not added
                    String acronym = investment.getTranche() != null && investment.getTranche() == 1 ? "TARRAGON" :
                            investment.getTranche() != null && investment.getTranche() == 2 ? "TARRAGON B" : "";
                    GeneratedGeneralLedgerFormDto record = new GeneratedGeneralLedgerFormDto();
                    record.setAcronym(acronym);
                    record.setBalanceDate(investment.getReport().getReportDate());
                    record.setFinancialStatementCategory("A");
                    record.setChartAccountsLongDescription(investment.getName());
                    record.setSegVal1("1200");
                    record.setNbAccountNumber("2033.010");
                    record.setNicAccountName("Инвестиции в фонд частного капитала " + investment.getName());
                    Double accountBalance = investment.getFairValue() != null && investment.getTranche() != null ?
                            (investment.getTranche() == 1 ? MathUtils.multiply(investment.getFairValue().doubleValue(), 0.99) : investment.getFairValue().doubleValue()) : null;
                    record.setGLAccountBalance(investment.getEditedFairValue() != null ? investment.getEditedFairValue() : accountBalance);
                    //record.setFundCCY();
                    //record.setSegValCCY();

                    if(!madeEditable && investment.getTranche() != null && investment.getTranche() == 1){
                        record.setEditable(true);
                        madeEditable = true;
                    }

                    if(record.getGLAccountBalance() != null && record.getGLAccountBalance() < 0){
                        //record.setGLAccountBalance(MathUtils.multiply(record.getGLAccountBalance(), -1.0));
                        record.setNbAccountNumber("3053.060");
                        record.setFinancialStatementCategory("L");
                        record.setNicAccountName("Прочие краткосрочные финансовые обязательства");
                        record.setSubscriptionRedemptionEntity((StringUtils.isNotEmpty(acronym) ? acronym + " - " : "") + investment.getName());
                    }
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
}
