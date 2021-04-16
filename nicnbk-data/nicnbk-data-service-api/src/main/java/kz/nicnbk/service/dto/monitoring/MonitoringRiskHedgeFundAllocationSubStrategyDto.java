package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

public class MonitoringRiskHedgeFundAllocationSubStrategyDto implements BaseDto{
    private String subStrategyName;

    //private Date firstDate;
    //private Date lastDate;
    private Date currentDate;
    private Date previousDate;
    private Double currentValue;
    private Double previousValue;

    public String getSubStrategyName() {
        return subStrategyName;
    }

    public void setSubStrategyName(String subStrategyName) {
        this.subStrategyName = subStrategyName;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Date getPreviousDate() {
        return previousDate;
    }

    public void setPreviousDate(Date previousDate) {
        this.previousDate = previousDate;
    }

    //    public Date getFirstDate() {
//        return firstDate;
//    }
//
//    public void setFirstDate(Date firstDate) {
//        this.firstDate = firstDate;
//    }
//
//    public Date getLastDate() {
//        return lastDate;
//    }
//
//    public void setLastDate(Date lastDate) {
//        this.lastDate = lastDate;
//    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public Double getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(Double previousValue) {
        this.previousValue = previousValue;
    }

}
