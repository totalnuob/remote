package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.StringUtils;

/**
 * Created by magzumov on 05.05.2017.
 */

public class SingularityITDRecordDto implements BaseDto{

    private Long id;
    private int tranche;
    private PeriodicReportDto periodicReport;

    private String investmentName;
    private Double subscriptions;
    private Double profitLoss;
    private Double redemptions;
    private Double closingBalance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTranche() {
        return tranche;
    }

    public void setTranche(int tranche) {
        this.tranche = tranche;
    }

    public PeriodicReportDto getPeriodicReport() {
        return periodicReport;
    }

    public void setPeriodicReport(PeriodicReportDto periodicReport) {
        this.periodicReport = periodicReport;
    }

    public String getInvestmentName() {
        return investmentName;
    }

    public void setInvestmentName(String investmentName) {
        this.investmentName = investmentName;
    }

    public Double getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Double subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(Double profitLoss) {
        this.profitLoss = profitLoss;
    }

    public Double getRedemptions() {
        return redemptions;
    }

    public void setRedemptions(Double redemptions) {
        this.redemptions = redemptions;
    }

    public Double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Double closingBalance) {
        this.closingBalance = closingBalance;
    }

    public boolean isEmpty(){
        return StringUtils.isEmpty(this.investmentName) && this.tranche == 0 &&
                this.subscriptions == null && this.profitLoss == null &&
                this.redemptions == null && this.closingBalance == null;
    }
}
