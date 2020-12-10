package kz.nicnbk.service.dto.tag;

/**
 * Created by magzumov on 21.02.2017.
 */
public enum TagTypes {
    IC("IC");

    TagTypes(String code){
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
}
