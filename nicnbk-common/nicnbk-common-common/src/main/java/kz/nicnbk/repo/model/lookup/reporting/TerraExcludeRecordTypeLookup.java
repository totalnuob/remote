package kz.nicnbk.repo.model.lookup.reporting;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum TerraExcludeRecordTypeLookup {

    CAPITAL_CALL("CAP_CALL"),
    BALANCE_SHEET("BAL_SHEET"),
    SECURITIES_COST("SEC_COST");


    private String code;


    TerraExcludeRecordTypeLookup(String code) {
        this.code = code;
    }


    public String getCode() {
        return code;
    }
}
