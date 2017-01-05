package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;

/**
 * Created by magzumov on 15.12.2016.
 */
@Entity(name="hf_investor_base")
public class InvestorBase extends BaseEntity {

    private String category;
    private String fund;
    private HedgeFund hedgeFund;

    @Column(name="category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Column(name="fund")
    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="hf_fund_id", nullable = false)
    public HedgeFund getHedgeFund() {
        return hedgeFund;
    }

    public void setHedgeFund(HedgeFund hedgeFund) {
        this.hedgeFund = hedgeFund;
    }
}
