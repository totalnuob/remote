package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 05.05.2017.
 */

public class StatementChangesDto implements BaseDto{

    private Long id;
    private String name;
    private Double trancheA;
    private Double trancheB;
    private Double total;
    private PeriodicReportDto report;
    private Boolean isTotalSum;
    private Boolean excludeFromTarragonCalculation;

    private Double trancheA2;
    private Double trancheB2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTrancheA() {
        return trancheA;
    }

    public void setTrancheA(Double trancheA) {
        this.trancheA = trancheA;
    }

    public Double getTrancheB() {
        return trancheB;
    }

    public void setTrancheB(Double trancheB) {
        this.trancheB = trancheB;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }

    public Boolean getTotalSum() {
        return isTotalSum;
    }

    public void setTotalSum(Boolean totalSum) {
        isTotalSum = totalSum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getExcludeFromTarragonCalculation() {
        return excludeFromTarragonCalculation;
    }

    public void setExcludeFromTarragonCalculation(Boolean excludeFromTarragonCalculation) {
        this.excludeFromTarragonCalculation = excludeFromTarragonCalculation;
    }

    public Double getTrancheA2() {
        return trancheA2;
    }

    public void setTrancheA2(Double trancheA2) {
        this.trancheA2 = trancheA2;
    }

    public Double getTrancheB2() {
        return trancheB2;
    }

    public void setTrancheB2(Double trancheB2) {
        this.trancheB2 = trancheB2;
    }
}
