package kz.nicnbk.repo.model.reporting;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 18.07.2017.
 */

@Entity
@Table(name = "periodic_data")
public class PeriodicData extends BaseEntity {

    private Date date;
    private Double value;
    private Double correction;
    private PeriodicDataType type;
    private Boolean revaluated;

    @Column(name = "date")
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "value")
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getCorrection() {
        return correction;
    }

    public void setCorrection(Double correction) {
        this.correction = correction;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    public PeriodicDataType getType() {
        return type;
    }

    public void setType(PeriodicDataType type) {
        this.type = type;
    }

    @Column(name="revaluated")
    public Boolean getRevaluated() {
        return revaluated;
    }

    public void setRevaluated(Boolean revaluated) {
        this.revaluated = revaluated;
    }
}
