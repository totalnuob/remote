package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.base.DataConstraints;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_saved_results_edited_fund")
public class HedgeFundScreeningSavedResultsEditedFund extends CreateUpdateBaseEntity {

    private HedgeFundScreeningSavedResults savedResults;

    HedgeFundScreeningParsedData parsedData;

    private Boolean excluded;
    private String excludeComment;
    private Boolean excludeFromStrategyAUM;

    private Double editedFundAUM;
    private String editedFundAUMComment;
    private Date editedFundAUMDate;

    private Double managerAUM;

    public HedgeFundScreeningSavedResultsEditedFund(){}

    public HedgeFundScreeningSavedResultsEditedFund(Long id){
        setId(id);
    }


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="saved_result_id", nullable = false)
    public HedgeFundScreeningSavedResults getSavedResults() {
        return savedResults;
    }

    public void setSavedResults(HedgeFundScreeningSavedResults savedResults) {
        this.savedResults = savedResults;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parsed_data_id", nullable = false)
    public HedgeFundScreeningParsedData getParsedData() {
        return parsedData;
    }

    public void setParsedData(HedgeFundScreeningParsedData parsedData) {
        this.parsedData = parsedData;
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

    @Column(name="edited_fund_aum_comment", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_LONG)
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

    @Column(name="excluded")
    public Boolean getExcluded() {
        return excluded;
    }

    public void setExcluded(Boolean excluded) {
        this.excluded = excluded;
    }

    @Column(name="exclude_comment", length = DataConstraints.C_TYPE_ENTITY_DESCRIPTION_LONG)
    public String getExcludeComment() {
        return excludeComment;
    }

    public void setExcludeComment(String excludeComment) {
        this.excludeComment = excludeComment;
    }

    @Column(name="exclude_strategy_aum")
    public Boolean getExcludeFromStrategyAUM() {
        return excludeFromStrategyAUM;
    }

    public void setExcludeFromStrategyAUM(Boolean excludeFromStrategyAUM) {
        this.excludeFromStrategyAUM = excludeFromStrategyAUM;
    }
}
