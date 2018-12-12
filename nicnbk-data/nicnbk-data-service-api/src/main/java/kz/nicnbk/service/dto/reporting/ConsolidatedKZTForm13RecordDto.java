package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

/**
 * Created by magzumov on 25.10.2017.
 */
public class ConsolidatedKZTForm13RecordDto implements BaseDto {
    private String accountNumber;
    private String name;
    private Integer lineNumber;

    private String entityName;
    private Date startPeriod;
    private Date endPeriod;
    private String interestRate;
    private Integer interestPaymentCount;
    private String currency;

    private Double debtStartPeriod;
    private Double interestStartPeriod;
    private Double totalStartPeriod;

    private Double debtTurnover;
    private Double interestTurnover;
    //private Double totalTurnover;

    private Double debtEndPeriod;
    private Double interestEndPeriod;
    private Double totalEndPeriod;


    public ConsolidatedKZTForm13RecordDto(){}

    public ConsolidatedKZTForm13RecordDto(String name, Integer lineNumber){
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

    public Date getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(Date startPeriod) {
        this.startPeriod = startPeriod;
    }

    public Date getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(Date endPeriod) {
        this.endPeriod = endPeriod;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public Double getInterestRateAsDouble(){
        if(this.interestRate != null){
            try{
                String textValue = this.interestRate.replace("%", "");
                textValue = textValue.replace(",", ".");
                Double value = Double.parseDouble(textValue);
                value = value / 100;
                return value;
            }catch (Exception e){
            }
        }
        return null;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getInterestPaymentCount() {
        return interestPaymentCount;
    }

    public void setInterestPaymentCount(Integer interestPaymentCount) {
        this.interestPaymentCount = interestPaymentCount;
    }

    public Double getDebtStartPeriod() {
        return debtStartPeriod;
    }

    public void setDebtStartPeriod(Double debtStartPeriod) {
        this.debtStartPeriod = debtStartPeriod;
    }

    public Double getInterestStartPeriod() {
        return interestStartPeriod;
    }

    public void setInterestStartPeriod(Double interestStartPeriod) {
        this.interestStartPeriod = interestStartPeriod;
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

    public Double getInterestTurnover() {
        return interestTurnover;
    }

    public void setInterestTurnover(Double interestTurnover) {
        this.interestTurnover = interestTurnover;
    }

//    public Double getTotalTurnover() {
//        return totalTurnover;
//    }
//
//    public void setTotalTurnover(Double totalTurnover) {
//        this.totalTurnover = totalTurnover;
//    }

    public Double getDebtEndPeriod() {
        return debtEndPeriod;
    }

    public void setDebtEndPeriod(Double debtEndPeriod) {
        this.debtEndPeriod = debtEndPeriod;
    }

    public Double getInterestEndPeriod() {
        return interestEndPeriod;
    }

    public void setInterestEndPeriod(Double interestEndPeriod) {
        this.interestEndPeriod = interestEndPeriod;
    }

    public Double getTotalEndPeriod() {
        return totalEndPeriod;
    }

    public void setTotalEndPeriod(Double totalEndPeriod) {
        this.totalEndPeriod = totalEndPeriod;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isEmptyAmounts(){
        return this.debtStartPeriod == null && this.interestStartPeriod == null && this.totalStartPeriod == null &&
                this.debtTurnover == null && this.interestTurnover == null &&
                this.debtEndPeriod == null && this.interestEndPeriod == null && this.totalEndPeriod == null;
    }
}
