package kz.nicnbk.service.impl.reporting.hedgefunds;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.NumberUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.reporting.hedgefunds.SingularityNICChartOfAccountsRepository;
import kz.nicnbk.repo.model.reporting.hedgefunds.SingularityNICChartOfAccounts;
import kz.nicnbk.service.api.reporting.hedgefunds.HFGeneralLedgerBalanceService;
import kz.nicnbk.service.api.reporting.hedgefunds.HFNOALService;
import kz.nicnbk.service.api.reporting.hedgefunds.PeriodicReportHFService;
import kz.nicnbk.service.dto.common.ListResponseDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.impl.reporting.PeriodicReportConstants;
import org.apache.poi.util.StringUtil;
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
                            BigDecimal a = NumberUtils.getBigDecimal(noalTrancheASubscriptionsRecords.get(noalRecordDto.getName()));
                            BigDecimal sum = a.add(NumberUtils.getBigDecimal(noalRecordDto.getFunctionalAmount()));
                            noalTrancheASubscriptionsRecords.put(noalRecordDto.getName(), sum.doubleValue());
                        }else{
                            noalTrancheASubscriptionsRecords.put(noalRecordDto.getName(), noalRecordDto.getFunctionalAmount());
                        }

                    }else if (noalRecordDto.getAccountNumber().startsWith("1550")) {
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
        if(noalTrancheARecordHolder != null && noalTrancheBRecordHolder.getNoalTrancheAList() != null){
            for(SingularityNOALRecordDto noalRecordDto: noalTrancheBRecordHolder.getNoalTrancheAList()){
                if(noalRecordDto.getTransaction().equalsIgnoreCase("Ending Balance") || noalRecordDto.getTransaction().equalsIgnoreCase("Ending")) {
                    if (noalRecordDto.getAccountNumber().startsWith("1500")) {
                        if(noalTrancheBSubscriptionsRecords.get(noalRecordDto.getName()) != null){
                            BigDecimal a = NumberUtils.getBigDecimal(noalTrancheBSubscriptionsRecords.get(noalRecordDto.getName()));
                            BigDecimal sum = a.add(NumberUtils.getBigDecimal(noalRecordDto.getFunctionalAmount()));
                            noalTrancheBSubscriptionsRecords.put(noalRecordDto.getName(), sum.doubleValue());
                        }else{
                            noalTrancheBSubscriptionsRecords.put(noalRecordDto.getName(), noalRecordDto.getFunctionalAmount());
                        }
                    }else if (noalRecordDto.getAccountNumber().startsWith("1550")) {
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

                if(StringUtils.isEmpty(singularityAccountNumber)){
                    logger.error("Invalid Singularity Account Number: expected 'XXXX-XXXX-XXX-XXX' (four parts delimited with - ), found '" + glRecordDto.getGLAccount() + "'");
                    responseDto.setErrorMessageEn("Invalid Singularity Account Number: expected 'XXXX-XXXX-XXX-XXX' (four parts delimited with - ), found '" + glRecordDto.getGLAccount() + "'");
                }else {
                    NICReportingChartOfAccountsDto accountDto = getNICChartOfAccountsFromSingularityAccount(singularityAccountNumber, record.getGLAccountBalance());
                    if (accountDto != null) {
                        record.setNbAccountNumber(accountDto.getNBChartOfAccounts().getCode());
                        record.setNicAccountName(accountDto.getNameRu());
                    } else {
                        logger.error("No matching NIC Chart of Accounts record found for Singularity Account Number '" + singularityAccountNumber + "'");
                        responseDto.setErrorMessageEn("No matching NIC Chart of Accounts record found for Singularity Account Number '" + singularityAccountNumber + "'");
                    }
                }


                if(record.getGLAccount().startsWith(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1500)){ // SUBSCRIPTIONS - Tranche A
                    if(record.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)){
                        for (String key : noalTrancheASubscriptionsRecords.keySet()) {
                            GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                            newRecordDto.setSubscriptionRedemptionEntity(key);
                            newRecordDto.setGLAccountBalance(noalTrancheASubscriptionsRecords.get(key));
                            setAccountNameAdditionalDescription(newRecordDto);
                            records.add(newRecordDto);
                        }

                    }else if(record.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE)){ // SUBSCRIPTIONS - Tranche B
                        for (String key : noalTrancheBSubscriptionsRecords.keySet()) {
                            GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                            newRecordDto.setSubscriptionRedemptionEntity(key);
                            newRecordDto.setGLAccountBalance(noalTrancheBSubscriptionsRecords.get(key));
                            setAccountNameAdditionalDescription(newRecordDto);
                            records.add(newRecordDto);
                        }
                    }else{
                        logger.error("Invalid Acronym value: expected '" + PeriodicReportConstants.SINGULAR_CAPITAL_CASE +
                                "' or '" + PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE + "'");
                        responseDto.setErrorMessageEn("Invalid Acronym value: expected '" + PeriodicReportConstants.SINGULAR_CAPITAL_CASE +
                                "' or '" + PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE + "'");
                    }
                }else if(record.getGLAccount().startsWith(PeriodicReportConstants.GROSVENOR_ACCOUNT_NUMBER_1550)){ // REDEMPTIONS - Tranche A
                    if(record.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULAR_CAPITAL_CASE)){
                        for (String key : noalTrancheARedemptionsRecords.keySet()) {
                            GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                            newRecordDto.setSubscriptionRedemptionEntity(key);
                            newRecordDto.setGLAccountBalance(noalTrancheARedemptionsRecords.get(key));
                            setAccountNameAdditionalDescription(newRecordDto);
                            records.add(newRecordDto);
                        }

                    }else if(record.getAcronym().equalsIgnoreCase(PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE)){ // REDEMPTIONS - Tranche B
                        for (String key : noalTrancheBRedemptionsRecords.keySet()) {
                            GeneratedGeneralLedgerFormDto newRecordDto = new GeneratedGeneralLedgerFormDto(record);
                            newRecordDto.setSubscriptionRedemptionEntity(key);
                            newRecordDto.setGLAccountBalance(noalTrancheBRedemptionsRecords.get(key));
                            setAccountNameAdditionalDescription(newRecordDto);
                            records.add(newRecordDto);
                        }
                    }else{
                        logger.error("Invalid Acronym value: expected '" + PeriodicReportConstants.SINGULAR_CAPITAL_CASE +
                                "' or '" + PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE + "'");
                        responseDto.setErrorMessageEn("Invalid Acronym value: expected '" + PeriodicReportConstants.SINGULAR_CAPITAL_CASE +
                                "' or '" + PeriodicReportConstants.SINGULAR_B_CAPITAL_CASE + "'");
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

    private void setAccountNameAdditionalDescription(GeneratedGeneralLedgerFormDto record){
        if(record == null ||  record.getNbAccountNumber() == null){
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
}
