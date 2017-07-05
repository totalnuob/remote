package kz.nicnbk.service.dto.reporting;

/**
 * Created by magzumov on 10.05.2017.
 */

@Deprecated
public enum ConsolidatedStatementType {

    ASSETS("ASSETS"),
    LIABILITIES("LIABILITIES"),
    INCOME("INCOME"),
    EXPENSES("EXPENSES"),
    BALANCE_OTHER("BALANCE_OTHER"),
    OPERATIONS_OTHER("OPERATIONS_OTHER"),
    EQUITY("EQUITY");

    ConsolidatedStatementType(String code){
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
}
