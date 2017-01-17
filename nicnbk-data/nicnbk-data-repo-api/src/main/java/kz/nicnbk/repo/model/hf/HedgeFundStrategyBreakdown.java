package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.common.Substrategy;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;

import javax.persistence.*;

/**
 * Created by magzumov on 05.07.2016.
 */
@Entity(name="hf_strategy_breakdown")
public class HedgeFundStrategyBreakdown extends BaseEntity{

    private HedgeFund fund;

    private Substrategy substrategy;

    private Double value;

    public HedgeFundStrategyBreakdown(){}

    public HedgeFundStrategyBreakdown(Long fundId, Integer substrategyId){
        HedgeFund fund = new HedgeFund();
        fund.setId(fundId);
        this.fund = fund;
        Substrategy substrategy= new Substrategy();
        substrategy.setId(substrategyId);
        this.substrategy = substrategy;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="fund_id", nullable = false)
    public HedgeFund getFund() {
        return fund;
    }

    public void setFund(HedgeFund fund) {
        this.fund = fund;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="substrategy_id", nullable = false)
    public Substrategy getSubstrategy() {
        return substrategy;
    }

    public void setSubstrategy(Substrategy substrategy) {
        this.substrategy = substrategy;
    }

    @Column(name="value")
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
