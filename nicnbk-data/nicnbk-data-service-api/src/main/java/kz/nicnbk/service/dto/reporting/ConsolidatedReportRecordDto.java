package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 15.05.2017.
 */
public class ConsolidatedReportRecordDto implements BaseDto {

    public static final int DEFAULT_CLASSIFICATIONS_SIZE = 2;
    public static final int DEFAULT_VALUES_SIZE = 5;

    private String name;
    private String[] classifications;
    private Double[] values;
    private String currency;

    // TODO: formatting?
    private boolean header;
    private boolean totalSum;
    private boolean formula;

    private int level;

    public ConsolidatedReportRecordDto(){}

    public ConsolidatedReportRecordDto(int classifications, int values){
        this.classifications = new String[classifications];
        this.values = new Double[values];
    }

    public ConsolidatedReportRecordDto(String name, String[] classifications, Double[] values, String currency,
                                       boolean header, boolean totalSum){
        this.name = name;
        this.classifications = classifications;
        this.values = values;
        this.currency = currency;
        this.header = header;
        this.totalSum = totalSum;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isFormula() {
        return formula;
    }

    public void setFormula(boolean formula) {
        this.formula = formula;
    }

    public void addClassification(String classification){
        if(this.classifications == null){
            this.classifications = new String[DEFAULT_CLASSIFICATIONS_SIZE];
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

    public void clearClassification(int index){
        if(this.classifications != null && this.classifications.length > index){
            this.classifications[index] = null;
        }
    }

    public boolean hasClassification(String classification){
        if(this.classifications != null){
            for(int i = 0; i < this.classifications.length; i++){
                if(classifications[i] != null && classifications[i].trim().equalsIgnoreCase(classification)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTotalClassification(){
        if(this.classifications != null && this.classifications.length > 0 && StringUtils.isNotEmpty(this.name)){
            for(String classification: this.classifications){
                if(this.name.trim().equalsIgnoreCase("Total " + classification) || this.name.trim().equalsIgnoreCase("Net " + classification)){
                    return true;
                }
            }
        }
        return false;
    }

    public void addValue(Double value){
        if(this.values == null){
            this.values = new Double[DEFAULT_VALUES_SIZE];
        }
        for(int i = 0; i < this.values.length; i++){
            if(this.values[i] == null){
                this.values[i] = value;
                break;
            }
        }
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public boolean isTotalSum() {
        return totalSum;
    }

    public void setTotalSum(boolean totalSum) {
        this.totalSum = totalSum;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
