package kz.nicnbk.service.dto.hf;
import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;

import java.util.Date;


public class HedgeFundScreeningSavedResultsDto extends CreateUpdateBaseEntityDto {

    private HedgeFundScreeningFilteredResultDto filteredResult;

    private Double fundAUM;
    private Double managerAUM;
    private Integer trackRecord;
    private Integer lookbackReturns;
    private Integer lookbackAUM;
    private Date startDate;
    private String startDateMonth;

    private String description;

    private int selectedLookbackReturn;
    private int selectedLookbackAUM;

    private boolean archived;

    public HedgeFundScreeningFilteredResultDto getFilteredResult() {
        return filteredResult;
    }

    public void setFilteredResult(HedgeFundScreeningFilteredResultDto filteredResult) {
        this.filteredResult = filteredResult;
    }

    public int getSelectedLookbackReturn() {
        return selectedLookbackReturn;
    }

    public void setSelectedLookbackReturn(int selectedLookbackReturn) {
        this.selectedLookbackReturn = selectedLookbackReturn;
    }

    public int getSelectedLookbackAUM() {
        return selectedLookbackAUM;
    }

    public void setSelectedLookbackAUM(int selectedLookbackAUM) {
        this.selectedLookbackAUM = selectedLookbackAUM;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Double getFundAUM() {
        return fundAUM;
    }

    public void setFundAUM(Double fundAUM) {
        this.fundAUM = fundAUM;
    }

    public Double getManagerAUM() {
        return managerAUM;
    }

    public void setManagerAUM(Double managerAUM) {
        this.managerAUM = managerAUM;
    }

    public Integer getTrackRecord() {
        return trackRecord;
    }

    public void setTrackRecord(Integer trackRecord) {
        this.trackRecord = trackRecord;
    }

    public Integer getLookbackReturns() {
        return lookbackReturns;
    }

    public void setLookbackReturns(Integer lookbackReturns) {
        this.lookbackReturns = lookbackReturns;
    }

    public Integer getLookbackAUM() {
        return lookbackAUM;
    }

    public void setLookbackAUM(Integer lookbackAUM) {
        this.lookbackAUM = lookbackAUM;
    }

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

    public String getStartDateMonth() {
        return startDateMonth;
    }

    public void setStartDateMonth(String startDateMonth) {
        this.startDateMonth = startDateMonth;
    }
}
