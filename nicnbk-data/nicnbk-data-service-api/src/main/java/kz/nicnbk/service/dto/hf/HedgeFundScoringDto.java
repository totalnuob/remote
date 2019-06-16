package kz.nicnbk.service.dto.hf;

import kz.nicnbk.common.service.model.CreateUpdateBaseEntityDto;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningFilteredResult;

import java.util.Date;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */
public class HedgeFundScoringDto extends CreateUpdateBaseEntityDto<HedgeFundScreeningFilteredResult> {

    private HedgeFundScreeningFilteredResultDto filteredResultDto;
    private Date date;

    private List<HedgeFundScoringResultFundDto> funds;

    public HedgeFundScreeningFilteredResultDto getFilteredResultDto() {
        return filteredResultDto;
    }

    public void setFilteredResultDto(HedgeFundScreeningFilteredResultDto filteredResultDto) {
        this.filteredResultDto = filteredResultDto;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<HedgeFundScoringResultFundDto> getFunds() {
        return funds;
    }

    public void setFunds(List<HedgeFundScoringResultFundDto> funds) {
        this.funds = funds;
    }
}


