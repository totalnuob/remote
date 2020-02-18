package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;
import kz.nicnbk.repo.model.common.Currency;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_saved_results")
public class HedgeFundScreeningSavedResults extends CreateUpdateBaseEntity {

    private HedgeFundScreeningFilteredResult filteredResult;

    /* Copied filter values for archived results*/
    private Double fundAUM;
    private Double managerAUM;
    private Integer trackRecord;
    private Integer lookbackReturns;
    private Integer lookbackAUM;
    private Date startDate;

    private String description;

    private int selectedLookbackReturn;
    private int selectedLookbackAUM;

    private boolean archived;

    public HedgeFundScreeningSavedResults(){}

    public HedgeFundScreeningSavedResults(Long id){
        setId(id);
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="filtered_result_id", nullable = false)
    public HedgeFundScreeningFilteredResult getFilteredResult() {
        return filteredResult;
    }

    public void setFilteredResult(HedgeFundScreeningFilteredResult filteredResult) {
        this.filteredResult = filteredResult;
    }

    @Column(name="selected_lookback_return")
    public int getSelectedLookbackReturn() {
        return selectedLookbackReturn;
    }

    public void setSelectedLookbackReturn(int selectedLookbackReturn) {
        this.selectedLookbackReturn = selectedLookbackReturn;
    }

    @Column(name="selected_lookback_aum")
    public int getSelectedLookbackAUM() {
        return selectedLookbackAUM;
    }

    public void setSelectedLookbackAUM(int selectedLookbackAUM) {
        this.selectedLookbackAUM = selectedLookbackAUM;
    }

    @Column(name="archived")
    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    @Column(name="fund_aum")
    public Double getFundAUM() {
        return fundAUM;
    }

    public void setFundAUM(Double fundAUM) {
        this.fundAUM = fundAUM;
    }

    @Column(name="manager_aum")
    public Double getManagerAUM() {
        return managerAUM;
    }

    public void setManagerAUM(Double managerAUM) {
        this.managerAUM = managerAUM;
    }

    @Column(name="track_record")
    public Integer getTrackRecord() {
        return trackRecord;
    }

    public void setTrackRecord(Integer trackRecord) {
        this.trackRecord = trackRecord;
    }

    @Column(name="lookback_returns")
    public Integer getLookbackReturns() {
        return lookbackReturns;
    }

    public void setLookbackReturns(Integer lookbackReturns) {
        this.lookbackReturns = lookbackReturns;
    }

    @Column(name="lookback_aum")
    public Integer getLookbackAUM() {
        return lookbackAUM;
    }

    public void setLookbackAUM(Integer lookbackAUM) {
        this.lookbackAUM = lookbackAUM;
    }

    @Column(name="start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
