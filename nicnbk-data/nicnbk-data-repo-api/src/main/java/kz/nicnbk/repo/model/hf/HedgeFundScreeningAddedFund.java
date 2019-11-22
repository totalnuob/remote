package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_added_fund")
public class HedgeFundScreeningAddedFund extends BaseEntity {

    //private HedgeFundScreening screening;
    HedgeFundScreeningFilteredResult filteredResult;

    private Long fundId;
    private String fundName;
    private String investmentManager;
    private String mainStrategy;

    private Double managerAUM;

    private Double fundAUM;
    private Date fundAUMDate;

    private String fundAUMComment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="filtered_result_id")
    public HedgeFundScreeningFilteredResult getFilteredResult() {
        return filteredResult;
    }

    public void setFilteredResult(HedgeFundScreeningFilteredResult filteredResult) {
        this.filteredResult = filteredResult;
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

    @Column(name="fund_aum")
    public Double getFundAUM() {
        return fundAUM;
    }

    public void setFundAUM(Double fundAUM) {
        this.fundAUM = fundAUM;
    }

    @Column(name="fund_aum_date")
    public Date getFundAUMDate() {
        return fundAUMDate;
    }

    public void setFundAUMDate(Date fundAUMDate) {
        this.fundAUMDate = fundAUMDate;
    }

    @Column(name="fund_aum_comment")
    public String getFundAUMComment() {
        return fundAUMComment;
    }

    public void setFundAUMComment(String fundAUMComment) {
        this.fundAUMComment = fundAUMComment;
    }
}
