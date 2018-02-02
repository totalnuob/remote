package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 25.10.2017.
 */
public class ConsolidatedKZTForm10RecordDto implements BaseDto {
    private String accountNumber;
    private String name;
    private Integer lineNumber;

    private Double startPeriodAssets;
    private Double turnoverOther;
    private Double endPeriodAssets;

    private Double startPeriodBalance;
    private Double endPeriodBalance;

    public ConsolidatedKZTForm10RecordDto(){}

    public ConsolidatedKZTForm10RecordDto(String name, Integer lineNumber){
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

    public Double getStartPeriodAssets() {
        return startPeriodAssets;
    }

    public void setStartPeriodAssets(Double startPeriodAssets) {
        this.startPeriodAssets = startPeriodAssets;
    }

    public Double getTurnoverOther() {
        return turnoverOther;
    }

    public void setTurnoverOther(Double turnoverOther) {
        this.turnoverOther = turnoverOther;
    }

    public Double getEndPeriodAssets() {
        return endPeriodAssets;
    }

    public void setEndPeriodAssets(Double endPeriodAssets) {
        this.endPeriodAssets = endPeriodAssets;
    }

    public Double getStartPeriodBalance() {
        return startPeriodBalance;
    }

    public void setStartPeriodBalance(Double startPeriodBalance) {
        this.startPeriodBalance = startPeriodBalance;
    }

    public Double getEndPeriodBalance() {
        return endPeriodBalance;
    }

    public void setEndPeriodBalance(Double endPeriodBalance) {
        this.endPeriodBalance = endPeriodBalance;
    }
}
