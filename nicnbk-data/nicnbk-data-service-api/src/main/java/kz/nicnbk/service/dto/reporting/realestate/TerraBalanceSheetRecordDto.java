package kz.nicnbk.service.dto.reporting.realestate;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.HierarchicalBaseDictionaryDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;

/**
 * Created by magzumov on 09.10.2017.
 */
public class TerraBalanceSheetRecordDto implements BaseDto {
    private Long id;
    private String name;
    private Double valueGP;
    private Double valueNICKMF;
    private Double grandTotal;
    private PeriodicReportDto report;

    private Boolean excludeFromTerraCalculation;

    private HierarchicalBaseDictionaryDto type;
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

    public Double getValueGP() {
        return valueGP;
    }

    public void setValueGP(Double valueGP) {
        this.valueGP = valueGP;
    }

    public Double getValueNICKMF() {
        return valueNICKMF;
    }

    public void setValueNICKMF(Double valueNICKMF) {
        this.valueNICKMF = valueNICKMF;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
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

    public Boolean getTotalSum() {
        return isTotalSum;
    }

    public void setTotalSum(Boolean totalSum) {
        isTotalSum = totalSum;
    }

    public Boolean getExcludeFromTerraCalculation() {
        return excludeFromTerraCalculation;
    }

    public void setExcludeFromTerraCalculation(Boolean excludeFromTerraCalculation) {
        this.excludeFromTerraCalculation = excludeFromTerraCalculation;
    }
}
