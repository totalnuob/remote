package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.DateUtils;

import java.util.Date;

/**
 * Created by magzumov on 25.08.2017.
 */
public class SingularityNOALRecordDto implements BaseDto, Comparable {
    private Date date;
    private String transaction;
    private String name;
    private Date effectiveDate;
    private Double transactionAmount;
    private String transactionAmountCCY;
    private Double functionalAmount;
    private String functionalAmountCCY;
    private int tranche;
    private String accountNumber;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionAmountCCY() {
        return transactionAmountCCY;
    }

    public void setTransactionAmountCCY(String transactionAmountCCY) {
        this.transactionAmountCCY = transactionAmountCCY;
    }

    public Double getFunctionalAmount() {
        return functionalAmount;
    }

    public void setFunctionalAmount(Double functionalAmount) {
        this.functionalAmount = functionalAmount;
    }

    public String getFunctionalAmountCCY() {
        return functionalAmountCCY;
    }

    public void setFunctionalAmountCCY(String functionalAmountCCY) {
        this.functionalAmountCCY = functionalAmountCCY;
    }

    public int getTranche() {
        return tranche;
    }

    public void setTranche(int tranche) {
        this.tranche = tranche;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public int compareTo(Object o) {
        return this.accountNumber != null && ((SingularityNOALRecordDto) o).accountNumber != null ?
                this.accountNumber.compareTo(((SingularityNOALRecordDto) o).accountNumber) : 1;
    }

}
