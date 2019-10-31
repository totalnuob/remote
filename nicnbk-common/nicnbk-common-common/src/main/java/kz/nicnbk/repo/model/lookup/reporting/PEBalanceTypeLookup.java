package kz.nicnbk.repo.model.lookup.reporting;

public enum PEBalanceTypeLookup {

    ASSETS("ASSETS", "Assets"),
    LIABILITIES("LIABLTY", "Liabilities");


    private String code;
    private String nameEn;

    PEBalanceTypeLookup(String code, String nameEn) {
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
