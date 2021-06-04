package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.StringUtils;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningFundParamDataDto implements BaseDto {

    private HedgeFundScreeningDto screening;
    private String fundName;

    private Double omega;
    private Double annualizedReturn;
    private Double sortino;
    private Double alpha;
    private Double beta;
    private Double cfVar;

    public HedgeFundScreeningDto getScreening() {
        return screening;
    }

    public void setScreening(HedgeFundScreeningDto screening) {
        this.screening = screening;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public Double getOmega() {
        return omega;
    }

    public void setOmega(Double omega) {
        this.omega = omega;
    }

    public Double getAnnualizedReturn() {
        return annualizedReturn;
    }

    public void setAnnualizedReturn(Double annualizedReturn) {
        this.annualizedReturn = annualizedReturn;
    }

    public Double getSortino() {
        return sortino;
    }

    public void setSortino(Double sortino) {
        this.sortino = sortino;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public Double getCfVar() {
        return cfVar;
    }

    public void setCfVar(Double cfVar) {
        this.cfVar = cfVar;
    }

    public boolean isEmpty(){
        return StringUtils.isEmpty(fundName) || (this.omega == null && this.annualizedReturn == null && this.sortino == null  &&
                this.alpha == null && this.beta == null && this.cfVar == null);
    }
}


