package kz.nicnbk.repo.model.lookup.reporting;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum CapitalCallExportTypeLookup {

    ORDER("ORDER"),
    TO_OPERATIONS("OPs"),
    TO_SPV("SPV"),
    ADM_FEE_TO_SPV("ADM_FEE_SPV");

    private String code;


    CapitalCallExportTypeLookup(String code) {
        this.code = code;
    }


    public String getCode() {
        return code;
    }
}
