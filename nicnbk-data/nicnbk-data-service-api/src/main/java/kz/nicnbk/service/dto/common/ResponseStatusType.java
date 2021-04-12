package kz.nicnbk.service.dto.common;

/**
 * Response status type enum.
 *
 * Created by magzumov.
 */
public enum ResponseStatusType {

    SUCCESS("SUCCESS"),
    OK_WITH_ERRORS("OK_WITH_ERRORS"),
    FAIL("FAIL");

    ResponseStatusType(String code){
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
}
