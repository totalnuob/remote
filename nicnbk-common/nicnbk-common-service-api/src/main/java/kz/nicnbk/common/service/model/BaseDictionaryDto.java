package kz.nicnbk.common.service.model;

/**
 * Created by magzumov on 08.07.2016.
 */
public class BaseDictionaryDto implements BaseDto{
    private String code;
    private String nameEn;
    private String nameRu;
    private String nameKz;

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
}
