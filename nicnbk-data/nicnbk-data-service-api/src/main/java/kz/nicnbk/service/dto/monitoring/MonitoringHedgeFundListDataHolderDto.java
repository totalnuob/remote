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

    List<MonitoringHedgeFundDateDoubleValueDto> returnsYTDConsolidated;
    List<MonitoringHedgeFundDateDoubleValueDto> returnsYTDClassA;
    List<MonitoringHedgeFundDateDoubleValueDto> returnsYTDClassB;

    List<MonitoringHedgeFundDateDoubleValueDto> returnsYTDHFRI;

    List<MonitoringHedgeFundDateDoubleValueDto> cumulativeReturnsHFRI;
    List<MonitoringHedgeFundDateDoubleValueDto> cumulativeReturnsConsolidated;
    List<MonitoringHedgeFundDateDoubleValueDto> cumulativeReturnsClassA;
    List<MonitoringHedgeFundDateDoubleValueDto> cumulativeReturnsClassB;

    public MonitoringHedgeFundListDataHolderDto(){
        this.data = new ArrayList<>();
        this.returnsHFRI = new ArrayList<>();
        this.returnsConsolidated = new ArrayList<>();
        this.returnsClassA = new ArrayList<>();
        this.returnsClassB = new ArrayList<>();

        this.returnsYTDConsolidated = new ArrayList<>();
        this.returnsYTDClassA = new ArrayList<>();
        this.returnsYTDClassB = new ArrayList<>();

        this.cumulativeReturnsHFRI = new ArrayList<>();
        this.cumulativeReturnsConsolidated = new ArrayList<>();
        this.cumulativeReturnsClassA = new ArrayList<>();
        this.cumulativeReturnsClassB = new ArrayList<>();
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

    public List<MonitoringHedgeFundDateDoubleValueDto> getCumulativeReturnsConsolidated() {
        return cumulativeReturnsConsolidated;
    }

    public void setCumulativeReturnsConsolidated(List<MonitoringHedgeFundDateDoubleValueDto> cumulativeReturnsConsolidated) {
        this.cumulativeReturnsConsolidated = cumulativeReturnsConsolidated;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getCumulativeReturnsClassA() {
        return cumulativeReturnsClassA;
    }

    public void setCumulativeReturnsClassA(List<MonitoringHedgeFundDateDoubleValueDto> cumulativeReturnsClassA) {
        this.cumulativeReturnsClassA = cumulativeReturnsClassA;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getCumulativeReturnsClassB() {
        return cumulativeReturnsClassB;
    }

    public void setCumulativeReturnsClassB(List<MonitoringHedgeFundDateDoubleValueDto> cumulativeReturnsClassB) {
        this.cumulativeReturnsClassB = cumulativeReturnsClassB;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getCumulativeReturnsHFRI() {
        return cumulativeReturnsHFRI;
    }

    public void setCumulativeReturnsHFRI(List<MonitoringHedgeFundDateDoubleValueDto> cumulativeReturnsHFRI) {
        this.cumulativeReturnsHFRI = cumulativeReturnsHFRI;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getReturnsYTDConsolidated() {
        return returnsYTDConsolidated;
    }

    public void setReturnsYTDConsolidated(List<MonitoringHedgeFundDateDoubleValueDto> returnsYTDConsolidated) {
        this.returnsYTDConsolidated = returnsYTDConsolidated;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getReturnsYTDClassA() {
        return returnsYTDClassA;
    }

    public void setReturnsYTDClassA(List<MonitoringHedgeFundDateDoubleValueDto> returnsYTDClassA) {
        this.returnsYTDClassA = returnsYTDClassA;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getReturnsYTDClassB() {
        return returnsYTDClassB;
    }

    public void setReturnsYTDClassB(List<MonitoringHedgeFundDateDoubleValueDto> returnsYTDClassB) {
        this.returnsYTDClassB = returnsYTDClassB;
    }

    public List<MonitoringHedgeFundDateDoubleValueDto> getReturnsYTDHFRI() {
        return returnsYTDHFRI;
    }

    public void setReturnsYTDHFRI(List<MonitoringHedgeFundDateDoubleValueDto> returnsYTDHFRI) {
        this.returnsYTDHFRI = returnsYTDHFRI;
    }
}
