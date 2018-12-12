package kz.nicnbk.repo.model.lookup.reporting;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum TarragonExcludeRecordTypeLookup {

    SCHEDULE_INVESTMENTS("SCHEDULE_INVESTMENTS"),
    CAPITAL_CALL("CAPITAL_CALL"),
    STATEMENT_CHANGES("STATEMENT_CHANGES"),
    STATEMENT_OPERATIONS("STATEMENT_OPERATIONS"),
    STATEMENT_BALANCE("STATEMENT_BALANCE");


    private String code;


    TarragonExcludeRecordTypeLookup(String code) {
        this.code = code;
    }


    public String getCode() {
        return code;
    }
}
