package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.common.service.util.DateUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningFilteredResult;

import java.util.Date;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScreeningParamsDto extends CreateUpdateBaseEntityDto<HedgeFundScreeningFilteredResult> {

    private Long screeningId;

    private Double fundAUM;
    private Double managerAUM;
    private Integer trackRecord;
    private Integer lookbackReturns;
    private Integer lookbackAUM;
    private Date startDate;
    private String startDateMonth;


    public HedgeFundScreeningParamsDto(){}

    public HedgeFundScreeningParamsDto(HedgeFundScreeningParamsDto other){
        this.screeningId = other.screeningId;
        this.fundAUM = other.fundAUM;
        this.managerAUM = other.managerAUM;
        this.trackRecord = other.trackRecord;
        this.lookbackReturns =  other.lookbackReturns;
        this.lookbackAUM = other.lookbackAUM;
        this.startDate = other.startDate;
        this.startDateMonth = other.startDateMonth;
    }

    public Long getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(Long screeningId) {
        this.screeningId = screeningId;
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

    public String getStartDateMonth() {
        return startDateMonth;
    }

    public void setStartDateMonth(String startDateMonth) {
        this.startDateMonth = startDateMonth;
    }

    public Date getStartDateFromTextOrCurrent(){
        Date startDate = null;
        if(StringUtils.isNotEmpty(this.startDateMonth)){
            Date monthDate = DateUtils.getDate("01." + this.startDateMonth);
            if(monthDate != null) {
                Date lastDayDate = DateUtils.getLastDayOfCurrentMonth(monthDate);
                startDate = lastDayDate;
            }
        }
        if(startDate == null){
            return this.startDate;
        }
        return startDate;
    }
}


