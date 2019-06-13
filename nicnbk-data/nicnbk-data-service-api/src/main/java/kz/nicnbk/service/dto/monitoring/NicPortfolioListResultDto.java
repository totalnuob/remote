package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.service.dto.common.StatusResultDto;
import kz.nicnbk.service.dto.common.StatusResultType;

import java.util.List;

/**
 * Created by Pak on 13.06.2019.
 */
@Deprecated
public class NicPortfolioListResultDto extends StatusResultDto {

    private List<NicPortfolioDto> nicPortfolioDtoList;

    public NicPortfolioListResultDto(List<NicPortfolioDto> nicPortfolioDtoList, StatusResultType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.nicPortfolioDtoList = nicPortfolioDtoList;
    }

    public List<NicPortfolioDto> getNicPortfolioDtoList() {
        return nicPortfolioDtoList;
    }

    public void setNicPortfolioDtoList(List<NicPortfolioDto> nicPortfolioDtoList) {
        this.nicPortfolioDtoList = nicPortfolioDtoList;
    }
}
