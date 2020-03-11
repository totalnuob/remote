package kz.nicnbk.repo.model.reporting.hedgefunds;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.reporting.PeriodicReport;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_hf_itd_hrs")
public class ReportingHFITDHistorical extends CreateUpdateBaseEntity{

    private int tranche;
    private PeriodicReport report;

    private Date date;
    private String fundName;
    private String portfolio;
    private Double netContribution;
    private Double endingBalance;

    @Column(name="date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name="fund_name", nullable = false)
    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    @Column(name="portfolio")
    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    @Column(name="net_contribution")
    public Double getNetContribution() {
        return netContribution;
    }

    public void setNetContribution(Double netContribution) {
        this.netContribution = netContribution;
    }

    @Column(name="ending_balance", nullable = false)
    public Double getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(Double endingBalance) {
        this.endingBalance = endingBalance;
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
