package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_parsed_ucits_data_aum")
public class HedgeFundScreeningParsedUcitsDataAUM extends BaseEntity {

    private HedgeFundScreening screening;

    private Long fundId;
    private String fundName;
    private String returnsCurrency;
    private Date date;
    private Double value;

    private Double valueByCurrency;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="screening_id")
    public HedgeFundScreening getScreening() {
        return screening;
    }

    public void setScreening(HedgeFundScreening screening) {
        this.screening = screening;
    }

    @Column(name="fund_id")
    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    @Column(name="fund_name")
    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    @Column(name="return_currency")
    public String getReturnsCurrency() {
        return returnsCurrency;
    }

    public void setReturnsCurrency(String returnsCurrency) {
        this.returnsCurrency = returnsCurrency;
    }

    @Column(name="date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name="value")
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

//    @Transient
//    public Double getValueByCurrency() {
//        return valueByCurrency;
//    }
//
//    public void setValueByCurrency(Double valueByCurrency) {
//        this.valueByCurrency = valueByCurrency;
//    }
}
