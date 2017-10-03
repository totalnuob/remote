package kz.nicnbk.repo.model.reporting.hedgefunds;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.reporting.PeriodicReport;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 20.04.2017.
 */

@Entity
@Table(name = "rep_hf_noal")
public class ReportingHFNOAL extends CreateUpdateBaseEntity{

    private Date date;
    private String transaction;
    private String name;
    private Date effectiveDate;
    private Double transactionAmount;
    private Currency transactionAmountCCY;
    private Double functionalAmount;
    private Currency functionalAmountCCY;
    private int tranche;
    private String accountNumber;
    private PeriodicReport report;


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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_currency_id")
    public Currency getTransactionAmountCCY() {
        return transactionAmountCCY;
    }

    public void setTransactionAmountCCY(Currency transactionAmountCCY) {
        this.transactionAmountCCY = transactionAmountCCY;
    }

    public Double getFunctionalAmount() {
        return functionalAmount;
    }

    public void setFunctionalAmount(Double functionalAmount) {
        this.functionalAmount = functionalAmount;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "func_currency_id")
    public Currency getFunctionalAmountCCY() {
        return functionalAmountCCY;
    }

    public void setFunctionalAmountCCY(Currency functionalAmountCCY) {
        this.functionalAmountCCY = functionalAmountCCY;
    }

    @Column(name="tranche", nullable = false)
    public int getTranche() {
        return tranche;
    }

    public void setTranche(int tranche) {
        this.tranche = tranche;
    }

    @Column(name="account_number")
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
