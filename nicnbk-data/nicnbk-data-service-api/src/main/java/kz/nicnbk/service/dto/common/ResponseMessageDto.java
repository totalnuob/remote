package kz.nicnbk.service.dto.common;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Dto class for response message.
 *
 * Created by magzumov.
 */
public class ResponseMessageDto implements BaseDto {

    private String type;
    private String code;
    private String nameEn;
    private String nameKz;
    private String nameRu;

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

    public String getMessageText(){
        if(nameEn != null){
            return nameEn;
        }else if(nameRu != null){
            return nameRu;
        }else if(nameKz != null){
            return nameKz;
        }
        return null;
    }
}
