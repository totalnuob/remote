package kz.nicnbk.service.dto.pe;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.pe.PENetCashflow;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by zhambyl on 12-Jan-17.
 */
public class PENetCashflowDto extends BaseEntityDto<PENetCashflow> implements Comparable<PENetCashflowDto>{

    private String fundName;

    private String currency;

    @DateTimeFormat(pattern="dd-MM-yyyy")
    private Date transactionDate;

    private double drawn;

    private double distributed;

    private double nav;

    private double netCF;

    private String typeOfFundTransaction;

//    private PEFundDto fund;

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

//    public PEFundDto getFund() {
//        return fund;
//    }
//
//    public void setFund(PEFundDto fund) {
//        this.fund = fund;
//    }

    @Override
    public int compareTo(PENetCashflowDto dto) {
        return this.transactionDate.compareTo(dto.getTransactionDate());
    }
}