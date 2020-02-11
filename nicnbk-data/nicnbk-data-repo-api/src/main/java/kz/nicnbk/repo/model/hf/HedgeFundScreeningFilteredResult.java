package kz.nicnbk.repo.model.hf;

import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.CreateUpdateBaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by magzumov on 04.07.2016.
 */

@Entity
@Table(name = "hf_screening_filtered_result")
public class HedgeFundScreeningFilteredResult extends CreateUpdateBaseEntity {

    private HedgeFundScreening screening;

    private Double fundAUM;
    private Double managerAUM;
    private Integer trackRecord;
    private Integer lookbackReturns;
    private Integer lookbackAUM;
    private Date startDate;

    private String description;

    public HedgeFundScreeningFilteredResult(){}

    public HedgeFundScreeningFilteredResult(Long id){
        setId(id);
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="screening_id", nullable = false)
    public HedgeFundScreening getScreening() {
        return screening;
    }

    public void setScreening(HedgeFundScreening screening) {
        this.screening = screening;
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
