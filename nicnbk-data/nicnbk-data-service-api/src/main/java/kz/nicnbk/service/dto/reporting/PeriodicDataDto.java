package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.common.service.util.MathUtils;

import java.util.Date;

/**
 * Created by magzumov on 18.07.2017.
 */
public class PeriodicDataDto extends BaseEntityDto {
    private Date date;
    private Double value;
    private Double correction;
    private BaseDictionaryDto type;
    private Boolean revaluated;
    private boolean editable;

    public PeriodicDataDto(){}

//    public PeriodicDataDto(Date date, String type, Double value){
//        this.date = date;
//        if(this.type == null){
//            this.type = new BaseDictionaryDto();
//        }
//        this.type.setCode(type);
//        this.value = value;
//    }

    public PeriodicDataDto(Date date, String type, Double value, Double correction){
        this.date = date;
        if(this.type == null){
            this.type = new BaseDictionaryDto();
        }
        this.type.setCode(type);
        this.value = value;
        this.correction = correction;
    }

    public PeriodicDataDto(Long id, Date date, String type, Double value){
        super.setId(id);
        this.date = date;
        if(this.type == null){
            this.type = new BaseDictionaryDto();
        }
        this.type.setCode(type);
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

    public BaseDictionaryDto getType() {
        return type;
    }

    public void setType(BaseDictionaryDto type) {
        this.type = type;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Boolean getRevaluated() {
        return revaluated;
    }

    public void setRevaluated(Boolean revaluated) {
        this.revaluated = revaluated;
    }

    public Double getCorrection() {
        return correction;
    }

    public void setCorrection(Double correction) {
        this.correction = correction;
    }

    public Double getTotal(){
        return MathUtils.add(this.value, this.correction);
    }
}
