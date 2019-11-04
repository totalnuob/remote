package kz.nicnbk.repo.model.lookup.reporting;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum TerraRecordTypeLookup {

    CAPITAL_CALL("CAP_CALL"),
    GENERAL_LEDGER("GL"),
    @Deprecated BALANCE_SHEET("BAL_SHEET"),
    @Deprecated SECURITIES_COST("SEC_COST");


    private String code;


    TerraRecordTypeLookup(String code) {
        this.code = code;
    }


    public String getCode() {
        return code;
    }
}
