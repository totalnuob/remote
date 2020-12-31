package kz.nicnbk.service.datamanager;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.repo.model.lookup.BaseLookup;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.repo.model.m2s2.MemoType;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.reporting.*;
import kz.nicnbk.service.dto.reporting.realestate.TerraNICReportingChartOfAccountsDto;
import kz.nicnbk.service.dto.strategy.StrategyDto;

import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */
public interface LookupService extends BaseService {
    String PE_BALANCE_TYPE = "PE_BALANCE_TYPE";
    String PE_OPS_TYPE = "PE_OPS_TYPE";
    String PE_CASHFLOW_TYPE = "PE_CASHFLOW_TYPE";
    String PE_INVESTMENT_TYPE = "PE_INVESTMENT_TYPE";
    String HF_CHART_ACCOUNTS_TYPE = "HF_CHART_ACCOUNTS_TYPE";
    String RE_CHART_ACCOUNTS_TYPE = "RE_CHART_ACCOUNTS_TYPE";
    String RE_BALANCE_TYPE = "RE_BALANCE_TYPE";
    String RE_PROFIT_LOSS_TYPE = "RE_PROFIT_LOSS_TYPE";
    String NB_CHART_ACCOUNTS = "NB_CHART_ACCOUNTS";
    String STRATEGY = "STRATEGY";

    <T extends BaseTypeEntity> T findByTypeAndCode(Class<T> clazz, String code);

    List<StrategyDto> getAllStrategies();
    StrategyDto getStrategyByNameEndAndType(String nameEn, int type);
    List<BaseDictionaryDto> getPrivateEquityStrategies();
    List<BaseDictionaryDto> getHedgeFundsStrategies();
    List<BaseDictionaryDto> getHedgeFundsSubStrategies(String strategy);
    List<BaseDictionaryDto> getRealEstateStrategies();
    List<BaseDictionaryDto> getGeographies();
    List<BaseDictionaryDto> getCurrencies();
    List<BaseDictionaryDto> getBenchmarkTypes();
    List<BaseDictionaryDto> getPortfolioVarTypes();
    List<BaseDictionaryDto> getHedgeFundStatuses();
    List<BaseDictionaryDto> getSubscriptionFrequencies();
    List<BaseDictionaryDto> getRedemptionFrequencies();
    List<BaseDictionaryDto> getRedemptionNoticePeriods();
    List<BaseDictionaryDto> getSidePockets();

    List<BaseDictionaryDto> getPEIndustry();
    List<BaseDictionaryDto> getTarragonTrancheTypes();
    List<BaseDictionaryDto> getTerraTrancheTypes();

    List<BaseDictionaryDto> getNBChartOfAccounts();
    List<NICReportingChartOfAccountsDto> getNICReportingChartOfAccounts(String code);
    List<BaseDictionaryDto> getNICReportingChartOfAccountsType();
    NICChartOfAccountsPagedSearchResultDto searchNICReportingChartOfAccounts(NICChartOfAccountsSearchParamsDto searchParams);
    List<TarragonNICReportingChartOfAccountsDto> getAddableTarragonNICReportingChartOfAccounts();
    List<TerraNICReportingChartOfAccountsDto> getAddableTerraNICReportingChartOfAccounts();

    List<BaseDictionaryDto> getReserveCalculationExpenseTypeLookup();
    List<BaseDictionaryDto> getReserveCalculationEntityTypeLookup();
    List<BaseDictionaryDto> getReserveCalculationExportSignerTypeLookup();
    List<BaseDictionaryDto> getReserveCalculationExportDoerTypeLookup();
    List<BaseDictionaryDto> getReserveCalculationExportApproveListTypeLookup();

    List<BaseDictionaryDto> getMMFields();

    List<BaseDictionaryDto> getAllNewsTypes();
    List<BaseDictionaryDto> getAllMemoTypes();
    List<BaseDictionaryDto> getAllMeetingTypes();

    List<BaseDictionaryDto> getAllCorpMeetingTypes();

    List<BaseDictionaryDto> getNBReportingTarragonBalanceTypeLookup();
    List<BaseDictionaryDto> getNBReportingTarragonOperationsTypeLookup();
    List<BaseDictionaryDto> getNBReportingTarragonCashflowsTypeLookup();
    List<BaseDictionaryDto> getNBReportingTarragonInvestmentTypeLookup();
    List<BaseDictionaryDto> getNBReportingSingularityChartAccountsTypeLookup();
    List<BaseDictionaryDto> getNBReportingTerraChartAccountsTypeLookup();
    List<BaseDictionaryDto> getNBReportingTerraBalanceTypeLookup();
    List<BaseDictionaryDto> getNBReportingTerraProfitLossTypeLookup();
    List<BaseDictionaryDto> getPeriodicDataTypesLookup();

    public EntitySaveResponseDto saveStrategyLookup(StrategyDto strategyDto, String username);
    EntitySaveResponseDto saveTypedLookupValue(String type, BaseDictionaryDto lookup, String username);
    boolean deleteTypedLookupValueByTypeAndId(String type, Integer id, String username);

    List<CommonNICReportingChartOfAccountsDto> getSingularityNICReportingChartOfAccounts();
    List<CommonNICReportingChartOfAccountsDto> getTarragonNICReportingChartOfAccounts();
    List<CommonNICReportingChartOfAccountsDto> getTerraNICReportingChartOfAccounts();

    EntitySaveResponseDto saveMatchingNICChartAccounts(String type, CommonNICReportingChartOfAccountsDto dto, String username);
    EntitySaveResponseDto saveNICChartOfAccounts(NICReportingChartOfAccountsDto dto, String username);

    boolean deleteMatchingLookupByTypeAndId(String type, Long id, String username);

    List<BaseDictionaryDto> getICMeetingTopicTypes();
    List<BaseDictionaryDto> getICMeetingAbsenceTypes();
    List<BaseDictionaryDto> getICMeetingPlaceTypes();
    List<BaseDictionaryDto> getICMeetingVoteTypes();
}

