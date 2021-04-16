package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseEntityDto;

import java.util.Date;

/**
 * Created by Pak on 20.06.2019.
 */

public class MonitoringRiskHFReturnsHolderDto extends BaseEntityDto {

    private MonitoringRiskHFMonthlyReportDto report;
    private String fileType;

    public MonitoringRiskHFMonthlyReportDto getReport() {
        return report;
    }

    public void setReport(MonitoringRiskHFMonthlyReportDto report) {
        this.report = report;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isEmpty(){
        return this.report == null || this.report.getId() == null || this.fileType == null;
    }
}
