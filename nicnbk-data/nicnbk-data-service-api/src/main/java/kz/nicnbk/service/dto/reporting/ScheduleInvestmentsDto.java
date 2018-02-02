package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;

/**
 * Created by magzumov on 05.05.2017.
 */

public class ScheduleInvestmentsDto implements BaseDto{

    private Long id;
    private String name;
    private Double capitalCommitments;
    private Double netCost;
    private Double fairValue;
    private Double editedFairValue;
    private PeriodicReportDto report;

    private BaseDictionaryDto type;
    private BaseDictionaryDto strategy;
    private BaseDictionaryDto currency;
    private Integer tranche;
    private String description;
    private Boolean isTotalSum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCapitalCommitments() {
        return capitalCommitments;
    }

    public void setCapitalCommitments(Double capitalCommitments) {
        this.capitalCommitments = capitalCommitments;
    }

    public Double getNetCost() {
        return netCost;
    }

    public void setNetCost(Double netCost) {
        this.netCost = netCost;
    }

    public Double getFairValue() {
        return fairValue;
    }

    public void setFairValue(Double fairValue) {
        this.fairValue = fairValue;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }

    public BaseDictionaryDto getType() {
        return type;
    }

    public void setType(BaseDictionaryDto type) {
        this.type = type;
    }

    public BaseDictionaryDto getStrategy() {
        return strategy;
    }

    public void setStrategy(BaseDictionaryDto strategy) {
        this.strategy = strategy;
    }

    public BaseDictionaryDto getCurrency() {
        return currency;
    }

    public void setCurrency(BaseDictionaryDto currency) {
        this.currency = currency;
    }

    public Integer getTranche() {
        return tranche;
    }

    public void setTranche(Integer tranche) {
        this.tranche = tranche;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getTotalSum() {
        return isTotalSum;
    }

    public void setTotalSum(Boolean totalSum) {
        isTotalSum = totalSum;
    }

    public Double getEditedFairValue() {
        return editedFairValue;
    }

    public void setEditedFairValue(Double editedFairValue) {
        this.editedFairValue = editedFairValue;
    }
}
