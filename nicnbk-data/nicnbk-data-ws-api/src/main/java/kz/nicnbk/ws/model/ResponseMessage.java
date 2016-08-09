package kz.nicnbk.ws.model;

import java.io.Serializable;

/**
 * Created by magzumov on 09.08.2016.
 */
public class ResponseMessage implements Serializable {

    public static final String TYPE_ERROR = "ERROR";
    public static final String TYPE_SUCCESS = "SUCCESS";

    private String type;
    private String code;
    private String nameEn;
    private String nameKz;
    private String nameRu;

    public String getName(){
        // default name
        return this.nameEn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getNameKz() {
        return nameKz;
    }

    public void setNameKz(String nameKz) {
        this.nameKz = nameKz;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }
}
