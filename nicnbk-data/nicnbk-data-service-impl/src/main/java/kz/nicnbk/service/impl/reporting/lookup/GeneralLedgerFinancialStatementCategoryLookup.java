package kz.nicnbk.service.impl.reporting.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum GeneralLedgerFinancialStatementCategoryLookup {

    ASSETS("A", "Assets"),
    LIABILITY("L", "Liability"),
    EQUITY("E", "Equity"),
    EXPENSE("X", "Expense"),
    INCOME("I", "Income");

    private String code;
    private String nameEn;


    GeneralLedgerFinancialStatementCategoryLookup(String code, String nameEn) {

        this.code = code;
        this.nameEn = nameEn;
    }


    public String getCode() {
        return code;
    }

    public String getNameEn() {
        return nameEn;
    }
}
