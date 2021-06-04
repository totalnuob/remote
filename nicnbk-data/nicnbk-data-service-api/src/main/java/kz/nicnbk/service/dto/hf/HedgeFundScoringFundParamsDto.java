package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningFilteredResult;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScoringFundParamsDto extends CreateUpdateBaseEntityDto<HedgeFundScreeningFilteredResult> {

    private Long screeningId;
    private Long filteredResultId;
    private Integer lookbackAUM;
    private Integer lookbackReturn;

    public Long getFilteredResultId() {
        return filteredResultId;
    }

    public void setFilteredResultId(Long filteredResultId) {
        this.filteredResultId = filteredResultId;
    }

    public Integer getLookbackAUM() {
        return lookbackAUM;
    }

    public void setLookbackAUM(Integer lookbackAUM) {
        this.lookbackAUM = lookbackAUM;
    }

    public Integer getLookbackReturn() {
        return lookbackReturn;
    }

    public void setLookbackReturn(Integer lookbackReturn) {
        this.lookbackReturn = lookbackReturn;
    }

    public Long getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(Long screeningId) {
        this.screeningId = screeningId;
    }
}


