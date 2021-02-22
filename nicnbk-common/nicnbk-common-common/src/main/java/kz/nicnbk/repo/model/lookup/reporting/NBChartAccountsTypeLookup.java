package kz.nicnbk.repo.model.lookup.reporting;


public enum NBChartAccountsTypeLookup {

    NOMATCH("0000.000");


    private String code;

    NBChartAccountsTypeLookup(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
