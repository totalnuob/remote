package kz.nicnbk.repo.model.lookup.reporting;

/**
 * Created by magzumov on 18.07.2016.
 */
public enum InvestmentTypeLookup {

    FUND_INVESTMENT("FUNDINVEST"),
    CO_INVESTMENT("COINVEST");

    private String code;


    InvestmentTypeLookup(String code) {
        this.code = code;
    }


    public String getCode() {
        return code;
    }
}
