package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

/**
 * Created by magzumov on 25.10.2017.
 */
public class ConsolidatedKZTForm7RecordDto implements BaseDto {
    private String accountNumber;
    private String name;
    private Integer lineNumber;

    private String entityName;
    private String otherName;
    private Date purchaseDate;

    private Double debtStartPeriod;
    private Double fairValueAdjustmentsStartPeriod;
    private Double totalStartPeriod;

    private Double debtTurnover;
    private Double fairValueAdjustmentsTurnoverPositive;
    private Double fairValueAdjustmentsTurnoverNegative;

    private Double debtEndPeriod;
    private Double fairValueAdjustmentsEndPeriod;
    private Double totalEndPeriod;

    private boolean becameZero;

    private String currency = "USD";


    public ConsolidatedKZTForm7RecordDto(){}

    public ConsolidatedKZTForm7RecordDto(String name, Integer lineNumber){
        this.name = name;
        this.lineNumber = lineNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Double getDebtStartPeriod() {
        return debtStartPeriod;
    }

    public void setDebtStartPeriod(Double debtStartPeriod) {
        this.debtStartPeriod = debtStartPeriod;
    }

    public Double getFairValueAdjustmentsStartPeriod() {
        return fairValueAdjustmentsStartPeriod;
    }

    public void setFairValueAdjustmentsStartPeriod(Double fairValueAdjustmentsStartPeriod) {
        this.fairValueAdjustmentsStartPeriod = fairValueAdjustmentsStartPeriod;
    }

    public Double getTotalStartPeriod() {
        return totalStartPeriod;
    }

    public void setTotalStartPeriod(Double totalStartPeriod) {
        this.totalStartPeriod = totalStartPeriod;
    }

    public Double getDebtTurnover() {
        return debtTurnover;
    }

    public void setDebtTurnover(Double debtTurnover) {
        this.debtTurnover = debtTurnover;
    }

    public Double getFairValueAdjustmentsTurnoverPositive() {
        return fairValueAdjustmentsTurnoverPositive;
    }

    public void setFairValueAdjustmentsTurnoverPositive(Double fairValueAdjustmentsTurnoverPositive) {
        this.fairValueAdjustmentsTurnoverPositive = fairValueAdjustmentsTurnoverPositive;
    }

    public Double getFairValueAdjustmentsTurnoverNegative() {
        return fairValueAdjustmentsTurnoverNegative;
    }

    public void setFairValueAdjustmentsTurnoverNegative(Double fairValueAdjustmentsTurnoverNegative) {
        this.fairValueAdjustmentsTurnoverNegative = fairValueAdjustmentsTurnoverNegative;
    }

    public Double getDebtEndPeriod() {
        return debtEndPeriod;
    }

    public void setDebtEndPeriod(Double debtEndPeriod) {
        this.debtEndPeriod = debtEndPeriod;
    }

    public Double getFairValueAdjustmentsEndPeriod() {
        return fairValueAdjustmentsEndPeriod;
    }

    public void setFairValueAdjustmentsEndPeriod(Double fairValueAdjustmentsEndPeriod) {
        this.fairValueAdjustmentsEndPeriod = fairValueAdjustmentsEndPeriod;
    }

    public Double getTotalEndPeriod() {
        return totalEndPeriod;
    }

    public void setTotalEndPeriod(Double totalEndPeriod) {
        this.totalEndPeriod = totalEndPeriod;
    }

    public boolean isBecameZero() {
        return becameZero;
    }

    public void setBecameZero(boolean becameZero) {
        this.becameZero = becameZero;
    }

    public boolean isEmpty(){
        return (this.debtStartPeriod == null || this.debtStartPeriod == 0) &&
                (this.fairValueAdjustmentsStartPeriod == null || this.fairValueAdjustmentsStartPeriod == 0) &&
                (this.totalStartPeriod == null || this.totalStartPeriod == 0) &&
                (this.debtTurnover == null || this.debtTurnover == 0) &&
                (this.fairValueAdjustmentsTurnoverPositive == null || this.fairValueAdjustmentsTurnoverPositive == 0) &&
                (this.fairValueAdjustmentsTurnoverNegative == null || this.fairValueAdjustmentsTurnoverNegative == 0) &&
                (this.debtEndPeriod == null || this.debtEndPeriod == 0) &&
                (this.fairValueAdjustmentsEndPeriod == null || this.fairValueAdjustmentsEndPeriod == 0) &&
                (this.totalEndPeriod == null || this.totalEndPeriod == 0);
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
