package kz.nicnbk.service.dto.monitoring;

import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;

import java.util.List;

/**
 * Created by Pak on 20.06.2019.
 */

public class LiquidPortfolioResultDto extends ResponseDto {

    private List<LiquidPortfolioDto> liquidPortfolioDtoList;

    public LiquidPortfolioResultDto(List<LiquidPortfolioDto> liquidPortfolioDtoList, ResponseStatusType status, String messageRu, String messageEn, String messageKz) {
        super(status, messageRu, messageEn, messageKz);
        this.liquidPortfolioDtoList = liquidPortfolioDtoList;
    }

    public List<LiquidPortfolioDto> getLiquidPortfolioDtoList() {
        return liquidPortfolioDtoList;
    }

    public void setLiquidPortfolioDtoList(List<LiquidPortfolioDto> liquidPortfolioDtoList) {
        this.liquidPortfolioDtoList = liquidPortfolioDtoList;
    }
}
