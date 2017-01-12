package kz.nicnbk.repo.api.lookup;

import kz.nicnbk.repo.model.base.BaseTypeEntity;


@Deprecated
public interface LookupTypeDao {

    <T extends BaseTypeEntity> T findByTypeAndCode(Class<T> clazz, String code);


}
