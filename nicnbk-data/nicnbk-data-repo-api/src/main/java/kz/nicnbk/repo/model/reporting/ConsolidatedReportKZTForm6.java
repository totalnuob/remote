package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_report_form_6")
public class ConsolidatedReportKZTForm6 extends CreateUpdateBaseEntity{

    private PeriodicReport report;

    private String name;
    private Integer lineNumber;
    private Double shareholderEquity;
    private Double additionalPaidinCapital;
    private Double redeemedOwnEquityInstruments;
    private Double reserveCapital;
    private Double otherReserves;
    private Double retainedEarnings;
    private Double total;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id", nullable = false)
    public PeriodicReport getReport() {
        return report;
    }

    public void setReport(PeriodicReport report) {
        this.report = report;
    }

    @Column(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="line_number")
    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Column(name="shareholder_equity")
    public Double getShareholderEquity() {
        return shareholderEquity;
    }

    public void setShareholderEquity(Double shareholderEquity) {
        this.shareholderEquity = shareholderEquity;
    }

    @Column(name="additional_capital")
    public Double getAdditionalPaidinCapital() {
        return additionalPaidinCapital;
    }

    public void setAdditionalPaidinCapital(Double additionalPaidinCapital) {
        this.additionalPaidinCapital = additionalPaidinCapital;
    }

    @Column(name="redeemed_own_instrum")
    public Double getRedeemedOwnEquityInstruments() {
        return redeemedOwnEquityInstruments;
    }

    public void setRedeemedOwnEquityInstruments(Double redeemedOwnEquityInstruments) {
        this.redeemedOwnEquityInstruments = redeemedOwnEquityInstruments;
    }

    @Column(name="reserve_capital")
    public Double getReserveCapital() {
        return reserveCapital;
    }

    public void setReserveCapital(Double reserveCapital) {
        this.reserveCapital = reserveCapital;
    }

    @Column(name="other_reserves")
    public Double getOtherReserves() {
        return otherReserves;
    }

    public void setOtherReserves(Double otherReserves) {
        this.otherReserves = otherReserves;
    }

    @Column(name="retained_earnings")
    public Double getRetainedEarnings() {
        return retainedEarnings;
    }

    public void setRetainedEarnings(Double retainedEarnings) {
        this.retainedEarnings = retainedEarnings;
    }

    @Column(name="total")
    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
