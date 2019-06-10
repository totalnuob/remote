package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_added_fund_return")
public class HedgeFundScreeningAddedFundReturn extends BaseEntity {

    private HedgeFundScreeningAddedFund addedFund;
    private Date date;
    private Double value;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="added_fund_id")
    public HedgeFundScreeningAddedFund getAddedFund() {
        return addedFund;
    }

    public void setAddedFund(HedgeFundScreeningAddedFund addedFund) {
        this.addedFund = addedFund;
    }

    @Column(name="date")
//    @Temporal(TemporalType.DATE)
//    @DateTimeFormat(pattern="dd-MM-yyyy")
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

}
