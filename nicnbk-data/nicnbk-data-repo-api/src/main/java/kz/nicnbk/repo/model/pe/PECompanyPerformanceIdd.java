package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Pak on 17.10.2017.
 */
@Entity(name = "pe_company_performance_idd")
public class PECompanyPerformanceIdd extends BaseEntity {

    private Date date;

    @Column(nullable = false)
    private String companyName;
    private Double invested;
    private Double realized;
    private Double unrealized;
    private Double totalValue;
    private Double multiple;
    private Boolean autoCalculation;
    private Double grossIrr;
    private Double netIrr;
    private PEFund fund;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getInvested() {
        return invested;
    }

    public void setInvested(Double invested) {
        this.invested = invested;
    }

    public Double getRealized() {
        return realized;
    }

    public void setRealized(Double realized) {
        this.realized = realized;
    }

    public Double getUnrealized() {
        return unrealized;
    }

    public void setUnrealized(Double unrealized) {
        this.unrealized = unrealized;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public Double getMultiple() {
        return multiple;
    }

    public void setMultiple(Double multiple) {
        this.multiple = multiple;
    }

    public Boolean getAutoCalculation() {
        return autoCalculation;
    }

    public void setAutoCalculation(Boolean autoCalculation) {
        this.autoCalculation = autoCalculation;
    }

    public Double getGrossIrr() {
        return grossIrr;
    }

    public void setGrossIrr(Double grossIrr) {
        this.grossIrr = grossIrr;
    }

    public Double getNetIrr() {
        return netIrr;
    }

    public void setNetIrr(Double netIrr) {
        this.netIrr = netIrr;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="fund_id", nullable = false)
    public PEFund getFund() {
        return fund;
    }

    public void setFund(PEFund fund) {
        this.fund = fund;
    }
}
