package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by zhambyl on 05-Jan-17.
 */
@Entity(name = "pe_gross_cashflow")
public class PEGrossCashflow extends BaseEntity {

    private String companyName;
    private Date date;
    private double invested;
    private double realized;
    private double unrealized;
    private double grossCF;
    private double irr;
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

    public double getInvested() {
        return invested;
    }

    public void setInvested(double invested) {
        this.invested = invested;
    }

    public double getRealized() {
        return realized;
    }

    public void setRealized(double realized) {
        this.realized = realized;
    }

    public double getUnrealized() {
        return unrealized;
    }

    public void setUnrealized(double unrealized) {
        this.unrealized = unrealized;
    }

    public double getGrossCF() {
        return grossCF;
    }

    public void setGrossCF(double grossCF) {
        this.grossCF = grossCF;
    }

    public double getIrr() {
        return irr;
    }

    public void setIrr(double irr) {
        this.irr = irr;
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
