package kz.nicnbk.service.impl.reporting.hedgefunds;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.NumberUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.reporting.hedgefunds.SingularityNICChartOfAccountsRepository;
import kz.nicnbk.repo.model.lookup.reporting.ChartAccountsTypeLookup;
import kz.nicnbk.repo.model.lookup.reporting.NICChartAccountsTypeLookup;
import kz.nicnbk.repo.model.reporting.hedgefunds.SingularityNICChartOfAccounts;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFNOALService;
import kz.nicnbk.service.api.reporting.hedgefunds.PeriodicReportHFService;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.hedgefunds.ExcludeSingularityRecordDto;
import kz.nicnbk.service.impl.reporting.PeriodicReportConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by magzumov on 18.01.2018.
 */
@Service
public class PeriodicReportHFServiceImpl implements PeriodicReportHFService {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicReportHFServiceImpl.class);

    @Autowired
    private HFGeneralLedgerBalanceService generalLedgerBalanceService;

    @Autowired
    private HFNOALService hfNOALService;

    @Autowired
    private SingularityNICChartOfAccountsRepository singularityNICChartOfAccountsRepository;

    @Override
    public ListResponseDto getSingularGeneratedForm(Long reportId) {
        ListResponseDto responseDto = new ListResponseDto();
        ConsolidatedReportRecordHolderDto generalLedgerRecordsHolder = this.generalLedgerBalanceService.getWithoutExcludedRecords(reportId);
        ConsolidatedReportRecordHolderDto noalTrancheARecordHolder = this.hfNOALService.get(reportId, 1);
        ConsolidatedReportRecordHolderDto noalTrancheBRecordHolder = this.hfNOALService.get(reportId, 2);

        Map<String, Double> noalTrancheASubscriptionsRecords = new HashMap<>();
        Map<String, Double> noalTrancheARedemptionsRecords = new HashMap<>();
        if(noalTrancheARecordHolder != null && noalTrancheARecordHolder.getNoalTrancheAList() != null){
            for(SingularityNOALRecordDto noalRecordDto: noalTrancheARecordHolder.getNoalTrancheAList()){
                if(noalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.GCM_NOAL_TRANSACTION_ENDING_BALANCE_NAME_V1) ||
                        noalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.GCM_NOAL_TRANSACTION_ENDING_BALANCE_NAME_V2)) {
                    if(noalRecordDto.getAccountNumber() == null){
                        continue;
                    }
                    if (noalRecordDto.getAccountNumber().startsWith(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1500)) {
                        if(noalTrancheASubscriptionsRecords.get(noalRecordDto.getName()) != null){
                            BigDecimal a = NumberUtils.getBigDecimal(noalTrancheASubscriptionsRecords.get(noalRecordDto.getName()));
                            BigDecimal sum = a.add(NumberUtils.getBigDecimal(noalRecordDto.getFunctionalAmount()));
                            noalTrancheASubscriptionsRecords.put(noalRecordDto.getName(), sum.doubleValue());
                        }else{
                            noalTrancheASubscriptionsRecords.put(noalRecordDto.getName(), noalRecordDto.getFunctionalAmount());
                        }

                    }else if (noalRecordDto.getAccountNumber().startsWith(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1550) ||
                            noalRecordDto.getAccountNumber().startsWith(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1210)) {
                        if(noalTrancheARedemptionsRecords.get(noalRecordDto.getName()) != null){
                            BigDecimal a = NumberUtils.getBigDecimal(noalTrancheARedemptionsRecords.get(noalRecordDto.getName()));
                            BigDecimal sum = a.add(NumberUtils.getBigDecimal(noalRecordDto.getFunctionalAmount()));
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
        if(noalTrancheBRecordHolder != null && noalTrancheBRecordHolder.getNoalTrancheBList() != null){
            for(SingularityNOALRecordDto noalRecordDto: noalTrancheBRecordHolder.getNoalTrancheBList()){
                if(noalRecordDto.getTransaction() != null && (noalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.GCM_NOAL_TRANSACTION_ENDING_BALANCE_NAME_V1) ||
                        noalRecordDto.getTransaction().equalsIgnoreCase(PeriodicReportConstants.GCM_NOAL_TRANSACTION_ENDING_BALANCE_NAME_V2))) {
                    if(noalRecordDto.getAccountNumber() == null){
                        continue;
                    }
                    if (noalRecordDto.getAccountNumber().startsWith(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1500)) {
                        if(noalTrancheBSubscriptionsRecords.get(noalRecordDto.getName()) != null){
                            BigDecimal a = NumberUtils.getBigDecimal(noalTrancheBSubscriptionsRecords.get(noalRecordDto.getName()));
                            BigDecimal sum = a.add(NumberUtils.getBigDecimal(noalRecordDto.getFunctionalAmount()));
                            noalTrancheBSubscriptionsRecords.put(noalRecordDto.getName(), sum.doubleValue());
                        }else{
                            noalTrancheBSubscriptionsRecords.put(noalRecordDto.getName(), noalRecordDto.getFunctionalAmount());
                        }
                    }else if (noalRecordDto.getAccountNumber().startsWith(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1550) ||
                            noalRecordDto.getAccountNumber().startsWith(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1210)) {
                        if(noalTrancheBRedemptionsRecords.get(noalRecordDto.getName()) != null){
                            BigDecimal a = NumberUtils.getBigDecimal(noalTrancheBRedemptionsRecords.get(noalRecordDto.getName()));
                            BigDecimal sum = a.add(NumberUtils.getBigDecimal(noalRecordDto.getFunctionalAmount()));
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
                record.setId(glRecordDto.getId());
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

                record.setAdjustedRedemption(glRecordDto.getAdjustedRedemption());

                if(StringUtils.isEmpty(singularityAccountNumber)){
                    logger.error("Invalid Singularity Account Number: expected 'XXXX-XXXX-XXX-XXX' (four parts delimited with - ), found '" + glRecordDto.getGLAccount() + "'");
                    responseDto.setErrorMessageEn("Invalid Singularity Account Number: expected 'XXXX-XXXX-XXX-XXX' (four parts delimited with - ), found '" + glRecordDto.getGLAccount() + "'");
                }else {
                    NICReportingChartOfAccountsDto accountDto = getNICChartOfAccountsFromSingularityAccount(singularityAccountNumber, record.getGLAccountBalance());
                    if (accountDto != null) {
                        if(accountDto.getCode() != null && accountDto.getCode().equalsIgnoreCase(NICChartAccountsTypeLookup.NOMATCH.getCode())){
                            // no match, exclude
                            continue;
                        }
                        record.setNbAccountNumber(accountDto.getNBChartOfAccounts().getCode());
//                        if(hasOtherEntityName(accountDto)){
//                            String fundName = StringUtils.isNotEmpty(glRecordDto.getShortName()) ? " " + glRecordDto.getShortName() : "";
//                            record.setNicAccountName(accountDto.getNameRu() + fundName);
//                        }else {
//                            record.setNicAccountName(accountDto.getNameRu());
//                        }
                        record.setNicAccountName(accountDto.getNameRu());
                    } else {
                        logger.error("No matching NIC Chart of Accounts record found for Singularity Account Number '" + singularityAccountNumber + "'");
                        responseDto.setErrorMessageEn("No matching NIC Chart of Accounts record found for Singularity Account Number '" + singularityAccountNumber + "'");
                    }
                }


                if(record.getGLAccount().startsWith(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1500)){ // SUBSCRIPTIONS - Tranche A
                    if(record.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULARITY_A_LOWER_CASE)){
                        for (String key : noalTrancheASubscriptionsRecords.keySet()) {
                            GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                            newRecordDto.setSubscriptionRedemptionEntity(key);
                            newRecordDto.setGLAccountBalance(noalTrancheASubscriptionsRecords.get(key));
                            setAccountNameAdditionalDescription(newRecordDto);

                            NICReportingChartOfAccountsDto accountDto = getNICChartOfAccountsFromSingularityAccount(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1500, newRecordDto.getGLAccountBalance());
                            if (accountDto != null) {
                                newRecordDto.setNbAccountNumber(accountDto.getNBChartOfAccounts().getCode());
                                if(hasOtherEntityName(accountDto)){
                                    String fundName = StringUtils.isNotEmpty(newRecordDto.getSubscriptionRedemptionEntity()) ? " " + newRecordDto.getSubscriptionRedemptionEntity() : "";
                                    newRecordDto.setNicAccountName(accountDto.getNameRu() + fundName);
                                }else {
                                    newRecordDto.setNicAccountName(accountDto.getNameRu());
                                }
                            }
                            records.add(newRecordDto);
                        }

                    }else if(record.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULARITY_B_LOWER_CASE)){ // SUBSCRIPTIONS - Tranche B
                        for (String key : noalTrancheBSubscriptionsRecords.keySet()) {
                            GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                            newRecordDto.setSubscriptionRedemptionEntity(key);
                            newRecordDto.setGLAccountBalance(noalTrancheBSubscriptionsRecords.get(key));
                            setAccountNameAdditionalDescription(newRecordDto);

                            NICReportingChartOfAccountsDto accountDto = getNICChartOfAccountsFromSingularityAccount(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1500, newRecordDto.getGLAccountBalance());
                            if (accountDto != null) {
                                newRecordDto.setNbAccountNumber(accountDto.getNBChartOfAccounts().getCode());
                                if(hasOtherEntityName(accountDto)){
                                    String fundName = StringUtils.isNotEmpty(newRecordDto.getSubscriptionRedemptionEntity()) ? " " + newRecordDto.getSubscriptionRedemptionEntity() : "";
                                    newRecordDto.setNicAccountName(accountDto.getNameRu() + fundName);
                                }else {
                                    newRecordDto.setNicAccountName(accountDto.getNameRu());
                                }
                            }
                            records.add(newRecordDto);
                        }
                    }else{
                        String errorMessage = "Invalid Acronym value: expected '" + PeriodicReportConstants.SINGULARITY_A_LOWER_CASE +
                                "' or '" + PeriodicReportConstants.SINGULARITY_B_LOWER_CASE + "'";
                        logger.error(errorMessage);
                        responseDto.setErrorMessageEn(errorMessage);
                    }
                }else if(record.getGLAccount().startsWith(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1550)){ // REDEMPTIONS - Tranche A
                    if(record.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULARITY_A_LOWER_CASE)){
                        for (String key : noalTrancheARedemptionsRecords.keySet()) {
                            GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                            newRecordDto.setSubscriptionRedemptionEntity(key);
                            newRecordDto.setGLAccountBalance(noalTrancheARedemptionsRecords.get(key));
                            setAccountNameAdditionalDescription(newRecordDto);

                            NICReportingChartOfAccountsDto accountDto = getNICChartOfAccountsFromSingularityAccount(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1550, newRecordDto.getGLAccountBalance());
                            if (accountDto != null) {
                                newRecordDto.setNbAccountNumber(accountDto.getNBChartOfAccounts().getCode());
                                if(hasOtherEntityName(accountDto)){
                                    String fundName = StringUtils.isNotEmpty(newRecordDto.getSubscriptionRedemptionEntity()) ? " " + newRecordDto.getSubscriptionRedemptionEntity() : "";
                                    newRecordDto.setNicAccountName(accountDto.getNameRu() + fundName);
                                }else {
                                    newRecordDto.setNicAccountName(accountDto.getNameRu());
                                }
                            }
                            records.add(newRecordDto);
                        }

                    }else if(record.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULARITY_B_LOWER_CASE)){ // REDEMPTIONS - Tranche B
                        for (String key : noalTrancheBRedemptionsRecords.keySet()) {
                            GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                            newRecordDto.setSubscriptionRedemptionEntity(key);
                            newRecordDto.setGLAccountBalance(noalTrancheBRedemptionsRecords.get(key));
                            setAccountNameAdditionalDescription(newRecordDto);

                            NICReportingChartOfAccountsDto accountDto = getNICChartOfAccountsFromSingularityAccount(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1550, newRecordDto.getGLAccountBalance());
                            if (accountDto != null) {
                                newRecordDto.setNbAccountNumber(accountDto.getNBChartOfAccounts().getCode());
                                if(hasOtherEntityName(accountDto)){
                                    String fundName = StringUtils.isNotEmpty(newRecordDto.getSubscriptionRedemptionEntity()) ? " " + newRecordDto.getSubscriptionRedemptionEntity() : "";
                                    newRecordDto.setNicAccountName(accountDto.getNameRu() + fundName);
                                }else {
                                    newRecordDto.setNicAccountName(accountDto.getNameRu());
                                }
                            }
                            records.add(newRecordDto);
                        }
                    }else{
                        String errorMessage = "Invalid Acronym value: expected '" + PeriodicReportConstants.SINGULARITY_A_LOWER_CASE +
                                "' or '" + PeriodicReportConstants.SINGULARITY_B_LOWER_CASE + "'";
                        logger.error(errorMessage);
                        responseDto.setErrorMessageEn(errorMessage);
                    }
                }else {
                    setAccountNameAdditionalDescription(record);
                    records.add(record);
                }
            }
        }
        responseDto.setRecords(records);
        return responseDto;
    }

    private boolean hasOtherEntityName(NICReportingChartOfAccountsDto accountDto){
        return accountDto.getNBChartOfAccounts() != null && accountDto.getNBChartOfAccounts().getCode() != null &&
                (accountDto.getNBChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010)
                || accountDto.getNBChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1283_020)
                || accountDto.getNBChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1183_040)
                || accountDto.getNBChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6113_030)
                || accountDto.getNBChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6280_010)
                || accountDto.getNBChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7470_010)
                || accountDto.getNBChartOfAccounts().getCode().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_010));
    }

    @Override
    public boolean excludeIncludeSingularityRecord(ExcludeSingularityRecordDto excludeRecordDto, String username) {
        boolean result = this.generalLedgerBalanceService.excludeIncludeSingularityRecord(excludeRecordDto.getRecordId(), username);
        return result;
    }

    private void setAccountNameAdditionalDescription(GeneratedGeneralLedgerFormDto record){
        if(record == null ||  record.getNbAccountNumber() == null){
            return;
        }
        if(record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1123_010) ||
                record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_030) ||
                record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6280_010) ||
                record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7470_010) ||
                record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_010) ||
                record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_7330_010) ||
                record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6150_030)){
            String fundName = record.getShortName() != null ? " " + record.getShortName() : "";
            record.setNicAccountName(record.getNicAccountName() + fundName);
        }else if(record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1283_020) ||
                record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_1183_040) ||
                record.getNbAccountNumber().equalsIgnoreCase(PeriodicReportConstants.ACC_NUM_6113_030)){
            String entityName = record.getSubscriptionRedemptionEntity() != null ? " " + record.getSubscriptionRedemptionEntity() : "";
            record.setNicAccountName(record.getNicAccountName() + entityName);
        }
    }

    private NICReportingChartOfAccountsDto getNICChartOfAccountsFromSingularityAccount(String accountNumber, Double accountBalance){
        List<SingularityNICChartOfAccounts> entities = this.singularityNICChartOfAccountsRepository.findBySingularityAccountNumber(accountNumber);
        if(entities != null && !entities.isEmpty()){
            SingularityNICChartOfAccounts entity = entities.get(0);
            boolean isPositiveOnly = entity.getChartAccountsType() != null &&
                    entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.POSITIVE.getCode()) ? true : false;
            boolean isNegativeOnly = entity.getChartAccountsType() != null &&
                    entity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.NEGATIVE.getCode()) ? true : false;
            if(entities.size() == 1) {
                if (isPositiveOnly && accountBalance < 0) {
                    return null;
                } else if (isNegativeOnly && accountBalance >= 0) {
                    return null;
                }
            }else if(entities.size() == 2){
                boolean found = false;
                for(SingularityNICChartOfAccounts anEntity: entities) {
                    if (accountBalance != null && accountBalance.doubleValue() < 0) {
                        if (anEntity.getChartAccountsType() != null &&
                                anEntity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.NEGATIVE.getCode()) ) {
                            entity = anEntity;
                            found = true;
                            break;
                        }
                    } else if (accountBalance != null && accountBalance.doubleValue() >= 0) {
                        if (anEntity.getChartAccountsType() != null &&
                                anEntity.getChartAccountsType().getCode().equalsIgnoreCase(ChartAccountsTypeLookup.POSITIVE.getCode()) ) {
                            entity =anEntity;
                            found = true;
                            break;
                        }
                    }
                }
                if(!found){
                    logger.error("Error setting Singularity NIC Chart of accounts for '" + accountNumber + "' " +
                            " : positiveOnly/negativeOnly flags not set properly");
                }
            }else if(entities.size() > 2){
                logger.error("Error setting Singularity NIC Chart of accounts for '" + accountNumber + "' " +
                        " : more than 2 mappings found.");
            }

//            if(accountNumber.equalsIgnoreCase("4200") || accountNumber.equalsIgnoreCase("4900")){
//                for(SingularityNICChartOfAccounts anEntity: entities){
//                    if(accountBalance > 0){ // 7330.030
//                        if(anEntity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase("7330.030")){
//                            entity = anEntity;
//                        }
//                    }else{ // 6150.030
//                        if(anEntity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase("6150.030")){
//                            entity = anEntity;
//                        }
//                    }
//
//                }
//            }else if(accountNumber.equalsIgnoreCase("2810")){
//                for(SingularityNICChartOfAccounts anEntity: entities){
//                    if(accountBalance >= 0 && anEntity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase("1623.010")){
//                            entity = anEntity;
//                    }else if(accountBalance < 0 && anEntity.getNicReportingChartOfAccounts().getNbChartOfAccounts().getCode().equalsIgnoreCase("3393.020")){
//                            entity = anEntity;
//                    }
//                }
//            }

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
}
