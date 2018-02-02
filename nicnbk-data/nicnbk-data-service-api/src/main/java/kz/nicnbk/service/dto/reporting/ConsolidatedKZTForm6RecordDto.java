package kz.nicnbk.service.dto.reporting;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.common.service.util.MathUtils;

import java.util.Date;

/**
 * Created by magzumov on 25.10.2017.
 */
public class ConsolidatedKZTForm6RecordDto implements BaseDto {

    private String name;
    private Integer lineNumber;

    /* Уставный каптал */
    private Double shareholderEquity;

    /* Дополнительно оплаченный капитал */
    private Double additionalPaidinCapital;

    /*  Выкупленные собственные долевые инструменты*/
    private Double redeemedOwnEquityInstruments;

    /* Резервный капитал */
    private Double reserveCapital;

    /* Прочие резервы */
    private Double otherReserves;

    /* Нераспределенная прибыль */
    private Double retainedEarnings;

    private Double total;



    public ConsolidatedKZTForm6RecordDto(){}

    public ConsolidatedKZTForm6RecordDto(String name, Integer lineNumber){
        this.name = name;
        this.lineNumber = lineNumber;
    }

    public ConsolidatedKZTForm6RecordDto(ConsolidatedKZTForm6RecordDto copy){
        this.name = copy.name;
        this.lineNumber = copy.lineNumber;
        this.shareholderEquity = copy.shareholderEquity;
        this.additionalPaidinCapital = copy.additionalPaidinCapital;
        this.redeemedOwnEquityInstruments = copy.redeemedOwnEquityInstruments;
        this.reserveCapital = copy.reserveCapital;
        this.otherReserves = copy.otherReserves;
        this.retainedEarnings = copy.retainedEarnings;
        this.total = copy.total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Double getShareholderEquity() {
        return shareholderEquity;
    }

    public void setShareholderEquity(Double shareholderEquity) {
        this.shareholderEquity = shareholderEquity;
    }

    public Double getAdditionalPaidinCapital() {
        return additionalPaidinCapital;
    }

    public void setAdditionalPaidinCapital(Double additionalPaidinCapital) {
        this.additionalPaidinCapital = additionalPaidinCapital;
    }

    public Double getRedeemedOwnEquityInstruments() {
        return redeemedOwnEquityInstruments;
    }

    public void setRedeemedOwnEquityInstruments(Double redeemedOwnEquityInstruments) {
        this.redeemedOwnEquityInstruments = redeemedOwnEquityInstruments;
    }

    public Double getReserveCapital() {
        return reserveCapital;
    }

    public void setReserveCapital(Double reserveCapital) {
        this.reserveCapital = reserveCapital;
    }

    public Double getOtherReserves() {
        return otherReserves;
    }

    public void setOtherReserves(Double otherReserves) {
        this.otherReserves = otherReserves;
    }

    public Double getRetainedEarnings() {
        return retainedEarnings;
    }

    public void setRetainedEarnings(Double retainedEarnings) {
        this.retainedEarnings = retainedEarnings;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void addValues(ConsolidatedKZTForm6RecordDto other){
        this.shareholderEquity = MathUtils.add(this.shareholderEquity, other.shareholderEquity);
        this.additionalPaidinCapital = MathUtils.add(this.additionalPaidinCapital, other.additionalPaidinCapital);
        this.redeemedOwnEquityInstruments = MathUtils.add(this.redeemedOwnEquityInstruments, other.redeemedOwnEquityInstruments);
        this.reserveCapital = MathUtils.add(this.reserveCapital, other.reserveCapital);
        this.otherReserves = MathUtils.add(this.otherReserves, other.otherReserves);
        this.retainedEarnings = MathUtils.add(this.retainedEarnings, other.retainedEarnings);
        this.total = MathUtils.add(this.total, other.total);
    }
}
