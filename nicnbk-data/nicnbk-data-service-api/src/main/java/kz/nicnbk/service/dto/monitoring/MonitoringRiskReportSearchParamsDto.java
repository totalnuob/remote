package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;

import java.util.Date;
import java.util.List;

/**
 * Created by Pak on 20.06.2019.
 */

public class MonitoringRiskReportSearchParamsDto implements BaseDto {
    private Date date;
    private Date previousDate;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getPreviousDate() {
        return previousDate;
    }

    public void setPreviousDate(Date previousDate) {
        this.previousDate = previousDate;
    }
}
