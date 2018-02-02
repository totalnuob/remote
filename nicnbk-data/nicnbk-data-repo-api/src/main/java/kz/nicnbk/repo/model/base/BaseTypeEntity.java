package kz.nicnbk.repo.model.base;

import kz.nicnbk.repo.model.markers.Identity;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseTypeEntity implements Identity<Integer>, Serializable {

    private Integer id;

    private String code;

    private String nameRu;

    private String nameEn;

    private String nameKz;

    BaseTypeEntity(){}

    BaseTypeEntity(Integer id){
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "code", length = DataConstraints.C_TYPE_ENTITY_CODE, nullable = false, unique = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name_ru", length = DataConstraints.C_TYPE_ENTITY_NAME, nullable = false)
    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    @Column(name = "name_en", length = DataConstraints.C_TYPE_ENTITY_NAME)
    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    @Column(name = "name_kz", length = DataConstraints.C_TYPE_ENTITY_NAME)
    public String getNameKz() {
        return nameKz;
    }

    public void setNameKz(String nameKz) {
        this.nameKz = nameKz;
    }

//    String getNameByLanguage(String langCode);
//
//    String getNameByLanguage(LanguageLookup lookup);
}
