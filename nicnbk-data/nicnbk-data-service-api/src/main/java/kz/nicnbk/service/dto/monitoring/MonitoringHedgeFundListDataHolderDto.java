package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */
public class MonitoringHedgeFundListDataHolderDto implements BaseDto{

    List<MonitoringHedgeFundDataHolderDto> data;
    List<MonitoringHedgeFundDateDoubleValueDto> returnsHFRI;
    List<MonitoringHedgeFundDateDoubleValueDto> returnsConsolidated;
    List<MonitoringHedgeFundDateDoubleValueDto> returnsClassA;
    List<MonitoringHedgeFundDateDoubleValueDto> returnsClassB;

    public MonitoringHedgeFundListDataHolderDto(){
        this.data = new ArrayList<>();
        this.returnsHFRI = new ArrayList<>();
        this.returnsConsolidated = new ArrayList<>();
        this.returnsClassA = new ArrayList<>();
        this.returnsClassB = new ArrayList<>();
    }

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

    public List<MonitoringHedgeFundDateDoubleValueDto> getReturnsConsolidated() {
        return returnsConsolidated;
    }

    public void setReturnsConsolidated(List<MonitoringHedgeFundDateDoubleValueDto> returnsConsolidated) {
        this.returnsConsolidated = returnsConsolidated;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getReturnsClassA() {
        return returnsClassA;
    }

    public void setReturnsClassA(List<MonitoringHedgeFundDateDoubleValueDto> returnsClassA) {
        this.returnsClassA = returnsClassA;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getReturnsClassB() {
        return returnsClassB;
    }

    public void setReturnsClassB(List<MonitoringHedgeFundDateDoubleValueDto> returnsClassB) {
        this.returnsClassB = returnsClassB;
    }
}
