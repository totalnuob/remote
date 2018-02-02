package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseEntityDto;

import java.util.Date;

/**
 * Created by magzumov on 18.07.2017.
 */
public class PeriodicDataDto extends BaseEntityDto {
    private Date date;
    private Double value;
    private String type;

    public PeriodicDataDto(){}

    public PeriodicDataDto(Date date, String type, Double value){
        this.date = date;
        this.type = type;
        this.value = value;
    }

    public PeriodicDataDto(Long id, Date date, String type, Double value){
        super.setId(id);

        this.date = date;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
