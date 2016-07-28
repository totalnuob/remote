package kz.nicnbk.service.datamanager;

import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.service.api.base.BaseService;

/**
 * Created by magzumov on 07.07.2016.
 */
public interface LookupTypeService extends BaseService {

    <T extends BaseTypeEntity> T findByTypeAndCode(Class<T> clazz, String code);
}
