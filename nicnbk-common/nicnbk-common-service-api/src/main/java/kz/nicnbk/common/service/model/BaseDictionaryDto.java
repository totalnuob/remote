package kz.nicnbk.common.service.model;

/**
 * Created by magzumov on 08.07.2016.
 */
public class BaseDictionaryDto implements BaseDto{
    private Integer id;
    private String code;
    private String nameEn;
    private String nameRu;
    private String nameKz;
    private BaseDictionaryDto parent;
    private Boolean editable;
    private Boolean deletable;

    public BaseDictionaryDto(){}

    public BaseDictionaryDto(String code, String nameEn, String nameRu, String nameKz){
        this.code = code;
        this.nameEn = nameEn;
        this.nameRu = nameRu;
        this.nameKz = nameKz;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public String getNameKz() {
        return nameKz;
    }

    public void setNameKz(String nameKz) {
        this.nameKz = nameKz;
    }

    public BaseDictionaryDto getParent() {
        return parent;
    }

    public void setParent(BaseDictionaryDto parent) {
        this.parent = parent;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getDeletable() {
        return deletable;
    }

    public void setDeletable(Boolean deletable) {
        this.deletable = deletable;
    }
}
