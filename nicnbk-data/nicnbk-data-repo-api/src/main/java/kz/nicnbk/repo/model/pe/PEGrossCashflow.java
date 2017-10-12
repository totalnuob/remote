package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhambyl on 05-Jan-17.
 */
@Entity(name = "pe_gross_cashflow")
public class PEGrossCashflow extends BaseEntity {

    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private Date date;
    private Double invested;
    private Double realized;
    private Double unrealized;
    private Double grossCF;
    private Boolean autoCalculation;
//    private Double irr;
    private PEFund fund;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Double getGrossCF() {
        return grossCF;
    }

    public void setGrossCF(Double grossCF) {
        this.grossCF = grossCF;
    }

    public Boolean getAutoCalculation() {
        return autoCalculation;
    }

    public void setAutoCalculation(Boolean autoCalculation) {
        this.autoCalculation = autoCalculation;
    }

//    public Double getIrr() {
//        return irr;
//    }
//
//    public void setIrr(Double irr) {
//        this.irr = irr;
//    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="fund_id", nullable = false)
    public PEFund getFund() {
        return fund;
    }

    public void setFund(PEFund fund) {
        this.fund = fund;
    }
}