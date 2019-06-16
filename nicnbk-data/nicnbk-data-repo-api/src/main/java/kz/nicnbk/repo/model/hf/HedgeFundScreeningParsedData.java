package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.files.Files;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_parsed_data")
public class HedgeFundScreeningParsedData extends BaseEntity {

    private HedgeFundScreening screening;

    private Long fundId;
    private String fundName;
    private String investmentManager;
    private String mainStrategy;

    private Double managerAUM;

    private Double editedFundAUM;
    private String editedFundAUMComment;
    private Date editedFundAUMDate;

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

    @Column(name="edited_fund_aum")
    public Double getEditedFundAUM() {
        return editedFundAUM;
    }

    public void setEditedFundAUM(Double editedFundAUM) {
        this.editedFundAUM = editedFundAUM;
    }

    @Column(name="edited_fund_aum_comment", columnDefinition = "TEXT")
    public String getEditedFundAUMComment() {
        return editedFundAUMComment;
    }

    public void setEditedFundAUMComment(String editedFundAUMComment) {
        this.editedFundAUMComment = editedFundAUMComment;
    }

    @Column(name="edited_fund_aum_date")
    public Date getEditedFundAUMDate() {
        return editedFundAUMDate;
    }

    public void setEditedFundAUMDate(Date editedFundAUMDate) {
        this.editedFundAUMDate = editedFundAUMDate;
    }
}
