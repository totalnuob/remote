package kz.nicnbk.repo.model.risk;

import kz.nicnbk.repo.model.base.BaseTypeEntityImpl;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "monitoring_risk_hf_portfolio_type")
public class MonitoringRiskHFPortfolioType extends BaseTypeEntityImpl {

    public MonitoringRiskHFPortfolioType(){}

    public MonitoringRiskHFPortfolioType(int id){
        setId(id);
    }
}
