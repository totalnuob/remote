package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

/**
 * Created by magzumov on 07.07.2016.
 */
public class MonitoringHedgeFundDateDoubleValueDto implements BaseDto{

    private Date date;
    private Double value;

    public MonitoringHedgeFundDateDoubleValueDto(){}

    public MonitoringHedgeFundDateDoubleValueDto(Date date, Double value){
        this.date = date;
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
