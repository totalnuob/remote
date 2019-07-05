package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;

import java.util.Date;

/**
 * Created by magzumov on 07.07.2016.
 */
public class MonitoringHedgeFundSearchParamsDto implements BaseDto {

    private Date monitoringDate;

    public Date getMonitoringDate() {
        return monitoringDate;
    }

    public void setMonitoringDate(Date monitoringDate) {
        this.monitoringDate = monitoringDate;
    }
}
