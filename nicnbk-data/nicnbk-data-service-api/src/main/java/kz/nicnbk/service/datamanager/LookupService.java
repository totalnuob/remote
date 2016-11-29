package kz.nicnbk.service.datamanager;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.service.api.base.BaseService;

import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */
public interface LookupService extends BaseService {

    <T extends BaseTypeEntity> T findByTypeAndCode(Class<T> clazz, String code);

    List<BaseDictionaryDto> getPrivateEquityStrategies();
    List<BaseDictionaryDto> getHedgeFundsStrategy();
    List<BaseDictionaryDto> getRealEstateStrategies();

    List<BaseDictionaryDto> getGeographies();

    List<BaseDictionaryDto> getCurrencies();

    List<BaseDictionaryDto> getPEIndustry();
}
