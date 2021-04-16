package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.BaseEntityDto;

import java.util.Date;

/**
 * Created by Pak on 20.06.2019.
 */

public class MonitoringRiskHFMonthlyReportDto extends BaseEntityDto {

    private Date reportDate;

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
}
