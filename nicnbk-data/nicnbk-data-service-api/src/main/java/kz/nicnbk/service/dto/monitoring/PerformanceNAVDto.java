package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.common.service.model.BaseEntityDto;
import kz.nicnbk.repo.model.monitoring.PerformanceNAV;

import java.util.Date;

/**
 * Created by Pak on 13.06.2019.
 */

public class PerformanceNAVDto extends BaseEntityDto<PerformanceNAV> {

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
