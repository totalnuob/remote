package kz.nicnbk.repo.model.monitoring;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by Pak on 13.06.2019.
 */

@Entity(name = "monitoring_performance_nav")
public class PerformanceNAV extends BaseEntity {

    private Date date;
    private Double privateEquity;
    private Double hedgeFunds;
    private Double realEstate;
    private Double fixedIncome;
    private Double publicEquity;
    private Double other;
    private Double alternativePortfolioTotal;
    private Double transitionPortfolio;
    private Double transfer;
    private Double total;
}
