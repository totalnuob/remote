package kz.nicnbk.common.service.util;

public class WorstDrawdownDto {
    private Double worstDDValue;
    private Integer worstDDPeriod;

    public Double getWorstDDValue() {
        return worstDDValue;
    }

    public void setWorstDDValue(Double worstDDValue) {
        this.worstDDValue = worstDDValue;
    }

    public Integer getWorstDDPeriod() {
        return worstDDPeriod;
    }

    public void setWorstDDPeriod(Integer worstDDPeriod) {
        this.worstDDPeriod = worstDDPeriod;
    }
}
