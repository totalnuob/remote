package kz.nicnbk.service.datamanager;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.reporting.NICReportingChartOfAccountsDto;
import kz.nicnbk.service.dto.reporting.TarragonNICReportingChartOfAccountsDto;

import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */
public interface LookupService extends BaseService {

    <T extends BaseTypeEntity> T findByTypeAndCode(Class<T> clazz, String code);

    List<BaseDictionaryDto> getPrivateEquityStrategies();
    List<BaseDictionaryDto> getHedgeFundsStrategies();
    List<BaseDictionaryDto> getHedgeFundsSubStrategies(String strategy);
    List<BaseDictionaryDto> getRealEstateStrategies();
    List<BaseDictionaryDto> getGeographies();
    List<BaseDictionaryDto> getCurrencies();
    List<BaseDictionaryDto> getHedgeFundStatuses();
    List<BaseDictionaryDto> getSubscriptionFrequencies();
    List<BaseDictionaryDto> getRedemptionFrequencies();
    List<BaseDictionaryDto> getRedemptionNoticePeriods();
    List<BaseDictionaryDto> getSidePockets();

    List<BaseDictionaryDto> getPEIndustry();

    List<BaseDictionaryDto> getNBChartOfAccounts();
    List<NICReportingChartOfAccountsDto> getNICReportingChartOfAccounts(String code);
    List<TarragonNICReportingChartOfAccountsDto> getAddableTarragonNICReportingChartOfAccounts();

    List<BaseDictionaryDto> getReserveCalculationExpenseTypeLookup();
    List<BaseDictionaryDto> getReserveCalculationEntityTypeLookup();

}
