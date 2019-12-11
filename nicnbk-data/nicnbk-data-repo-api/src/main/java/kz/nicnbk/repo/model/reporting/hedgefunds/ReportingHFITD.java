package kz.nicnbk.repo.model.reporting.hedgefunds;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.reporting.PeriodicReport;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_hf_itd")
public class ReportingHFITD extends CreateUpdateBaseEntity{

    private int tranche;
    private PeriodicReport report;

    private String investmentName;
    private Double subscriptions;
    private Double profitLoss;
    private Double redemptions;
    private Double closingBalance;

    @Column(name="investment_name")
    public String getInvestmentName() {
        return investmentName;
    }

    public void setInvestmentName(String investmentName) {
        this.investmentName = investmentName;
    }

    @Column(name="subscriptions")
    public Double getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Double subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Column(name="profit_loss")
    public Double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(Double profitLoss) {
        this.profitLoss = profitLoss;
    }

    @Column(name="redemptions")
    public Double getRedemptions() {
        return redemptions;
    }

    public void setRedemptions(Double redemptions) {
        this.redemptions = redemptions;
    }

    @Column(name="closing_balance")
    public Double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Double closingBalance) {
        this.closingBalance = closingBalance;
    }

    @Column(name="tranche", nullable = false)
    public int getTranche() {
        return tranche;
    }

    public void setTranche(int tranche) {
        this.tranche = tranche;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }

}
