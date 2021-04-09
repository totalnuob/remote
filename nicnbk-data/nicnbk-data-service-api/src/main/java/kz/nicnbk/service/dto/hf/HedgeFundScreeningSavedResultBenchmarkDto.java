package kz.nicnbk.service.dto.hf;
import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;

import java.util.Date;


public class HedgeFundScreeningSavedResultBenchmarkDto extends CreateUpdateBaseEntityDto {

    private HedgeFundScreeningSavedResultsDto savedResults;

    private BaseDictionaryDto benchmark;
    private Date date;
    //private Double returnValue;
    private Double calculatedMonthReturn;
    private Double indexValue;
    private Double ytd;

    public HedgeFundScreeningSavedResultsDto getSavedResults() {
        return savedResults;
    }

    public void setSavedResults(HedgeFundScreeningSavedResultsDto savedResults) {
        this.savedResults = savedResults;
    }

    public BaseDictionaryDto getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(BaseDictionaryDto benchmark) {
        this.benchmark = benchmark;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

//    public Double getReturnValue() {
//        return returnValue;
//    }
//
//    public void setReturnValue(Double returnValue) {
//        this.returnValue = returnValue;
//    }

    public Double getYtd() {
        return ytd;
    }

    public void setYtd(Double ytd) {
        this.ytd = ytd;
    }

    public Double getCalculatedMonthReturn() {
        return calculatedMonthReturn;
    }

    public void setCalculatedMonthReturn(Double calculatedMonthReturn) {
        this.calculatedMonthReturn = calculatedMonthReturn;
    }

    public Double getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Double indexValue) {
        this.indexValue = indexValue;
    }
}
