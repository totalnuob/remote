package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 15.05.2017.
 */
public class ConsolidatedReportRecordDto implements BaseDto {

    private String name;
    private String[] classifications;
    private Double[] values;

    public ConsolidatedReportRecordDto(){}

    public ConsolidatedReportRecordDto(int classifications, int values){
        this.classifications = new String[classifications];
        this.values = new Double[values];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getClassifications() {
        return classifications;
    }

    public void setClassifications(String[] classifications) {
        this.classifications = classifications;
    }

    public Double[] getValues() {
        return values;
    }

    public void setValues(Double[] values) {
        this.values = values;
    }

    public void addClassification(String classification){
        if(this.classifications == null){
            this.classifications = new String[5];
        }
        for(int i = 0; i < this.classifications.length; i++){
            if(this.classifications[i] == null){
                this.classifications[i] = classification;
                break;
            }
        }
    }

    public String getLastClassification(){
        if(this.classifications != null){
            for(int i = this.classifications.length - 1; i >= 0; i--){
                if(this.classifications[i] != null){
                    return this.classifications[i];
                }
            }
        }
        return null;
    }

    public void addValue(Double value){
        if(this.values == null){
            this.values = new Double[20];
        }
        for(int i = 0; i < this.values.length; i++){
            if(this.values[i] == null){
                this.values[i] = value;
                break;
            }
        }
    }

}
