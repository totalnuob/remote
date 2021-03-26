package kz.nicnbk.repo.model.risk;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "risk_stress_tests")
public class RiskStressTests extends CreateUpdateBaseEntity{

    private Date date;
    private String name;
    private Double value;

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}