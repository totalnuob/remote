package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_parsed_ucits_data")
public class HedgeFundScreeningParsedUcitsData extends BaseEntity {

    private HedgeFundScreening screening;

    private Long fundId;
    private String fundName;
    private String investmentManager;
    private String mainStrategy;

    private Double managerAUM;

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

    @Column(name="investment_manager")
    public String getInvestmentManager() {
        return investmentManager;
    }

    public void setInvestmentManager(String investmentManager) {
        this.investmentManager = investmentManager;
    }

    @Column(name="main_strategy")
    public String getMainStrategy() {
        return mainStrategy;
    }

    public void setMainStrategy(String mainStrategy) {
        this.mainStrategy = mainStrategy;
    }

    @Column(name="manager_aum")
    public Double getManagerAUM() {
        return managerAUM;
    }

    public void setManagerAUM(Double managerAUM) {
        this.managerAUM = managerAUM;
    }
}
