package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.BaseEntityDto;

import java.util.Date;

/**
 * Created by Pak on 20.06.2019.
 */

public class RiskStressTestsDto extends BaseEntityDto implements BaseDto {
    private Date date;
    private String name;
    private Double value;

    public RiskStressTestsDto(){}

    public RiskStressTestsDto(Date date, String name, Double value){
        this.date = date;
        this.name = name;
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
