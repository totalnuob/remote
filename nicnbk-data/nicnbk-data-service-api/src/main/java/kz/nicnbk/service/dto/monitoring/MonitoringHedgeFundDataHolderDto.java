package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;

import java.util.Date;

/**
 * Created by magzumov on 07.07.2016.
 */
public class MonitoringHedgeFundDataHolderDto extends CreateUpdateBaseEntityDto implements Comparable{

    private Date date;
    private MonitoringHedgeFundDataDto monitoringData;

    public MonitoringHedgeFundDataHolderDto(){
        this.monitoringData = new MonitoringHedgeFundDataDto();
        this.monitoringData.setOverall(new MonitoringHedgeFundDataOverallDto());
        this.monitoringData.setClassA(new MonitoringHedgeFundDataClassADto());
        this.monitoringData.setClassB(new MonitoringHedgeFundDataClassBDto());
    }

    public MonitoringHedgeFundDataHolderDto(Date date){
        this.date = date;
        this.monitoringData = new MonitoringHedgeFundDataDto();
        this.monitoringData.setOverall(new MonitoringHedgeFundDataOverallDto());
        this.monitoringData.setClassA(new MonitoringHedgeFundDataClassADto());
        this.monitoringData.setClassB(new MonitoringHedgeFundDataClassBDto());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MonitoringHedgeFundDataDto getMonitoringData() {
        return monitoringData;
    }

    public void setMonitoringData(MonitoringHedgeFundDataDto monitoringData) {
        this.monitoringData = monitoringData;
    }

    @Override
    public int compareTo(Object o) {
        return (0 - this.date.compareTo(((MonitoringHedgeFundDataHolderDto) o).date));
    }
}
