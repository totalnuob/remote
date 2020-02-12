package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_saved_results_added_fund_return")
public class HedgeFundScreeningSavedResultsAddedFundReturn extends CreateUpdateBaseEntity {

    private HedgeFundScreeningSavedResultsAddedFund addedFund;
    private Date date;
    private Double value;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="added_fund_id", nullable = false)
    public HedgeFundScreeningSavedResultsAddedFund getAddedFund() {
        return addedFund;
    }

    public void setAddedFund(HedgeFundScreeningSavedResultsAddedFund addedFund) {
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
