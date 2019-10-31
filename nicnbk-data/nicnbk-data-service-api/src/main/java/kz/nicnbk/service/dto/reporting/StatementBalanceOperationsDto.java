package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.HierarchicalBaseDictionaryDto;

/**
 * Created by magzumov on 09.10.2017.
 */
public class StatementBalanceOperationsDto implements BaseDto {
    private Long id;
    private String name;
    private Double tarragonMFTotal;
    private Double tarragonMFShareGP;
    private Double tarragonMFShareNICK;
    private Double tarragonLP;
    private Double NICKMFShareTotal;
    private Double consolidationAdjustments;
    private Double NICKMFShareConsolidated;

    private PeriodicReportDto report;

    private HierarchicalBaseDictionaryDto type;
    private Integer tranche;
    private Boolean isTotalSum;
    private Boolean excludeFromTarragonCalculation;

    private BaseDictionaryDto trancheType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTarragonMFTotal() {
        return tarragonMFTotal;
    }

    public void setTarragonMFTotal(Double tarragonMFTotal) {
        this.tarragonMFTotal = tarragonMFTotal;
    }

    public Double getTarragonMFShareGP() {
        return tarragonMFShareGP;
    }

    public void setTarragonMFShareGP(Double tarragonMFShareGP) {
        this.tarragonMFShareGP = tarragonMFShareGP;
    }

    public Double getTarragonMFShareNICK() {
        return tarragonMFShareNICK;
    }

    public void setTarragonMFShareNICK(Double tarragonMFShareNICK) {
        this.tarragonMFShareNICK = tarragonMFShareNICK;
    }

    public Double getTarragonLP() {
        return tarragonLP;
    }

    public void setTarragonLP(Double tarragonLP) {
        this.tarragonLP = tarragonLP;
    }

    public Double getNICKMFShareTotal() {
        return NICKMFShareTotal;
    }

    public void setNICKMFShareTotal(Double NICKMFShareTotal) {
        this.NICKMFShareTotal = NICKMFShareTotal;
    }

    public Double getConsolidationAdjustments() {
        return consolidationAdjustments;
    }

    public void setConsolidationAdjustments(Double consolidationAdjustments) {
        this.consolidationAdjustments = consolidationAdjustments;
    }

    public Double getNICKMFShareConsolidated() {
        return NICKMFShareConsolidated;
    }

    public void setNICKMFShareConsolidated(Double NICKMFShareConsolidated) {
        this.NICKMFShareConsolidated = NICKMFShareConsolidated;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }

    public HierarchicalBaseDictionaryDto getType() {
        return type;
    }

    public void setType(HierarchicalBaseDictionaryDto type) {
        this.type = type;
    }

    public Integer getTranche() {
        return tranche;
    }

    public void setTranche(Integer tranche) {
        this.tranche = tranche;
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

    public BaseDictionaryDto getTrancheType() {
        return trancheType;
    }

    public void setTrancheType(BaseDictionaryDto trancheType) {
        this.trancheType = trancheType;
    }
}
