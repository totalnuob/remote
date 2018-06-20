package kz.nicnbk.service.dto.reporting.realestate;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.service.dto.reporting.PeriodicReportDto;

import java.util.List;

/**
 * Created by magzumov on 15.06.2018.
 */
public class TerraCombinedDataHolderDto implements BaseDto {

    List<TerraBalanceSheetRecordDto> balanceSheetRecords;
    List<TerraProfitLossRecordDto> profitLossRecords;
    List<TerraSecuritiesCostRecordDto> securitiesCostRecords;

    private PeriodicReportDto report;

    public List<TerraBalanceSheetRecordDto> getBalanceSheetRecords() {
        return balanceSheetRecords;
    }

    public void setBalanceSheetRecords(List<TerraBalanceSheetRecordDto> balanceSheetRecords) {
        this.balanceSheetRecords = balanceSheetRecords;
    }

    public List<TerraProfitLossRecordDto> getProfitLossRecords() {
        return profitLossRecords;
    }

    public void setProfitLossRecords(List<TerraProfitLossRecordDto> profitLossRecords) {
        this.profitLossRecords = profitLossRecords;
    }

    public List<TerraSecuritiesCostRecordDto> getSecuritiesCostRecords() {
        return securitiesCostRecords;
    }

    public void setSecuritiesCostRecords(List<TerraSecuritiesCostRecordDto> securitiesCostRecords) {
        this.securitiesCostRecords = securitiesCostRecords;
    }

    public PeriodicReportDto getReport() {
        return report;
    }

    public void setReport(PeriodicReportDto report) {
        this.report = report;
    }
}
