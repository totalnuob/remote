package kz.nicnbk.repo.model.risk;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "monitoring_risk_hf_portfolio_return")
public class MonitoringRiskHFPortfolioReturn extends CreateUpdateBaseEntity {

    private Date date;
    private Double value;
    private MonitoringRiskHFPortfolioType portfolioType;

    @Column(name = "date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "return_value", nullable = false)
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_type_id", nullable = false)
    public MonitoringRiskHFPortfolioType getPortfolioType() {
        return portfolioType;
    }

    public void setPortfolioType(MonitoringRiskHFPortfolioType portfolioType) {
        this.portfolioType = portfolioType;
    }
}
