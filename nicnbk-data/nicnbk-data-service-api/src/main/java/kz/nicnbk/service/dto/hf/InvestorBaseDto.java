package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.repo.model.hf.HedgeFund;

/**
 * Created by magzumov on 15.12.2016.
 */
public class InvestorBaseDto implements BaseDto {
    private String category;
    private String fund;
    private HedgeFundDto2 hedgeFund;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    public HedgeFundDto2 getHedgeFund() {
        return hedgeFund;
    }

    public void setHedgeFund(HedgeFundDto2 hedgeFund) {
        this.hedgeFund = hedgeFund;
    }
}
