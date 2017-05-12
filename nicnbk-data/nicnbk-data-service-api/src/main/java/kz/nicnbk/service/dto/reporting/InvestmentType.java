package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.repo.model.hf.InvestorBase;

/**
 * Created by magzumov on 10.05.2017.
 */
public enum InvestmentType {

    FUND_INVESTMENT("FUND_INV"),
    CO_INVESTMENT("COINVEST");

    InvestmentType(String code){
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
}
