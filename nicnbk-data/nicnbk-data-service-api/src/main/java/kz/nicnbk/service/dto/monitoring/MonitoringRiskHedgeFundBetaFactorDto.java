package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by Pak on 20.06.2019.
 */

public class MonitoringRiskHedgeFundBetaFactorDto implements BaseDto {
    private String name;
    private Double valueSinceInception;
    private Double value12M;

    public MonitoringRiskHedgeFundBetaFactorDto(){}

    public MonitoringRiskHedgeFundBetaFactorDto(String name,Double valueSinceInception, Double value12M){
        this.name = name;
        this.valueSinceInception = valueSinceInception;
        this.value12M = value12M;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValueSinceInception() {
        return valueSinceInception;
    }

    public void setValueSinceInception(Double valueSinceInception) {
        this.valueSinceInception = valueSinceInception;
    }

    public Double getValue12M() {
        return value12M;
    }

    public void setValue12M(Double value12M) {
        this.value12M = value12M;
    }
}
