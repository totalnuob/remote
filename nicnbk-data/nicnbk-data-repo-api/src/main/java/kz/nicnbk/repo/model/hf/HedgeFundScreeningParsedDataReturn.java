package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_parsed_data_return")
public class HedgeFundScreeningParsedDataReturn extends BaseEntity {

    private HedgeFundScreening screening;

    private Long fundId;
    private String fundName;
    private String returnsCurrency;
    private Date date;
    private Double value;

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

    @Column(name="date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name="value", nullable = false)
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
