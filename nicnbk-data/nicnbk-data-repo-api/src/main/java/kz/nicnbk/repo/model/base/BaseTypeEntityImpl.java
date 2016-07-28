package kz.nicnbk.repo.model.base;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseTypeEntityImpl extends BaseTypeEntity {


    // TODO: add localization
//    @Transient
//    public String getNameByLanguage(LanguageLookup lookup) {
//        return getNameByLanguage(lookup.getCode());
//    }
//
//    @Transient
//    public String getNameByLanguage(String langCode) {
//
//    }
}
