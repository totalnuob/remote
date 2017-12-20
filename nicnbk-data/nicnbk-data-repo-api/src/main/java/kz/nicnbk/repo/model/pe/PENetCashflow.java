package kz.nicnbk.repo.model.pe;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by zhambyl on 12-Jan-17.
 */
@Entity(name = "pe_net_cashflow")
public class PENetCashflow extends BaseEntity{

    private String fundName;
    private String currency;
    private Date transactionDate;
    private double drawn;
    private double distributed;
    private double nav;
    private double netCF;
    private String typeOfFundTransaction;
    private PEFund fund;

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public double getDrawn() {
        return drawn;
    }

    public void setDrawn(double drawn) {
        this.drawn = drawn;
    }

    public double getDistributed() {
        return distributed;
    }

    public void setDistributed(double distributed) {
        this.distributed = distributed;
    }

    public double getNav() {
        return nav;
    }

    public void setNav(double nav) {
        this.nav = nav;
    }

    public double getNetCF() {
        return netCF;
    }

    public void setNetCF(double netCF) {
        this.netCF = netCF;
    }

    public String getTypeOfFundTransaction() {
        return typeOfFundTransaction;
    }

    public void setTypeOfFundTransaction(String typeOfFundTransaction) {
        this.typeOfFundTransaction = typeOfFundTransaction;
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