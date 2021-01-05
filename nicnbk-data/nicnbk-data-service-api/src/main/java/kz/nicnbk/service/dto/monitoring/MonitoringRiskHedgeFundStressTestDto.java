package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;

public class MonitoringRiskHedgeFundStressTestDto implements BaseDto {
    private String name;
    private Date date;
    private Double value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
