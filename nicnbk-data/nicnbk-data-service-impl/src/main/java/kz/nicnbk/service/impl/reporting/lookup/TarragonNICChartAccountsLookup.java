package kz.nicnbk.service.impl.reporting.lookup;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum TarragonNICChartAccountsLookup {

    CC_CASH_ADJ("CASH_ADJ", "Capital call cash adjustment"),
    CC_CAPITAL_ADJ("CAP_ADJ", "Capital call capital adjustment");

    private String code;
    private String nameEn;


    TarragonNICChartAccountsLookup(String code, String nameEn) {
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
