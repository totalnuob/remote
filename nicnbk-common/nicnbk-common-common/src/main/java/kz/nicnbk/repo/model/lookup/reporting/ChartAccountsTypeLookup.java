package kz.nicnbk.repo.model.lookup.reporting;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum ChartAccountsTypeLookup {

    ALL("ALL"),
    NEGATIVE("NEGATIVE"),
    POSITIVE("POSITIVE");

    private String code;


    ChartAccountsTypeLookup(String code) {
        this.code = code;
    }


    public String getCode() {
        return code;
    }
}
