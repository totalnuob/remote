package kz.nicnbk.repo.model.lookup.reporting;

public enum PEOperationsTypeLookup {

    INCOME("INCOME", "Income"),
    EXPENSES("EXPENSES", "Expenses");


    private String code;
    private String nameEn;

    PEOperationsTypeLookup(String code, String nameEn) {
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
