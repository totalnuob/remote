package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;

import java.util.List;

/**
 * Created by Pak on 13.06.2019.
 */

public class NicPortfolioResultDto extends ResponseDto {

    private List<NicPortfolioDto> nicPortfolioDtoList;

    public NicPortfolioResultDto(List<NicPortfolioDto> nicPortfolioDtoList, ResponseStatusType status, String messageRu, String messageEn, String messageKz) {
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
