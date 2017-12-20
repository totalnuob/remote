package kz.nicnbk.service.dto.common;

/**
 * Created by magzumov on 05.05.2017.
 */
public enum StatusResultType {

    SUCCESS("SUCCESS"),
    FAIL("FAIL");

    StatusResultType(String code){
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
}
