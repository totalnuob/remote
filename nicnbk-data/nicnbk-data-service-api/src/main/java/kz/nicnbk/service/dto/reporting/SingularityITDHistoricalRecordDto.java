package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

public class SingularityITDHistoricalRecordDto implements BaseDto{

    private Long id;
    private int tranche;
    private PeriodicReportDto periodicReport;

    private Date date;
    private String fundName;
    private String portfolio;
    private Double netContribution;
    private Double endingBalance;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public Double getNetContribution() {
        return netContribution;
    }

    public void setNetContribution(Double netContribution) {
        this.netContribution = netContribution;
    }

    public Double getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(Double endingBalance) {
        this.endingBalance = endingBalance;
    }

    public boolean isEmpty(){
        return this.date == null && this.fundName == null && this.portfolio == null && this.netContribution == null && this.endingBalance == null;
    }
}
