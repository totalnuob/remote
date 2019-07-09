package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */
public class MonitoringHedgeFundListDataHolderDto implements BaseDto{

    List<MonitoringHedgeFundDataHolderDto> data;
    List<MonitoringHedgeFundDateDoubleValueDto> returnsHFRI;

    public List<MonitoringHedgeFundDataHolderDto> getData() {
        return data;
    }

    public void setData(List<MonitoringHedgeFundDataHolderDto> data) {
        this.data = data;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getReturnsHFRI() {
        return returnsHFRI;
    }

    public void setReturnsHFRI(List<MonitoringHedgeFundDateDoubleValueDto> returnsHFRI) {
        this.returnsHFRI = returnsHFRI;
    }
}
