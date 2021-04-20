package kz.nicnbk.service.converter.hf;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.CurrencyRates;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResultFunds;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningSavedResults;
import kz.nicnbk.repo.model.lookup.CurrencyLookup;
import kz.nicnbk.repo.model.markers.Lookup;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningParsedDataDto;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningSavedResultFundsDto;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningSavedResultsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HedgeFundScreeningSavedResultFundsEntityConverter extends BaseDozerEntityConverter<HedgeFundScreeningSavedResultFunds, HedgeFundScreeningSavedResultFundsDto> {

    @Autowired
    private LookupService lookupService;

    public HedgeFundScreeningSavedResultFunds assemble(HedgeFundScreeningParsedDataDto fundDto, int type,Long saveResultsId){
        HedgeFundScreeningSavedResultFunds entity = new HedgeFundScreeningSavedResultFunds();
        entity.setSavedResults(new HedgeFundScreeningSavedResults(saveResultsId));
        entity.setType(type);
        entity.setFundId(fundDto.getFundId());
        entity.setFundName(fundDto.getFundName());
        entity.setInvestmentManager(fundDto.getInvestmentManager());
        entity.setMainStrategy(fundDto.getMainStrategy());
        if(StringUtils.isNotEmpty(fundDto.getCurrency()) && !fundDto.getCurrency().equalsIgnoreCase(CurrencyLookup.USD.getCode())) {
            // non-USD
            Currency currency = this.lookupService.findByTypeAndCode(Currency.class, fundDto.getCurrency());
            entity.setCurrency(currency);

            entity.setFundAUM(fundDto.getFundAUMByCurrency());
            entity.setFundAUMUSD(fundDto.getFundAUM());
        }else{
            entity.setFundAUM(fundDto.getFundAUM());
            entity.setFundAUMUSD(fundDto.getFundAUM());
        }
        entity.setFundAUMDate(fundDto.getFundAUMDate());
        entity.setStrategyAUM(fundDto.getStrategyAUM());
        entity.setManagerAUM(fundDto.getManagerAUM());
        entity.setEditedFundAUM(fundDto.getEditedFundAUM());
        entity.setEditedFundAUMComment(fundDto.getEditedFundAUMComment());
        entity.setEditedFundAUMDate(fundDto.getEditedFundAUMDate());
        entity.setRecentFundAUM(fundDto.getRecentFundAUM());
        entity.setRecentFundAUMDate(fundDto.getRecentFundAUMDate());
        entity.setRecentTrackRecordDate(fundDto.getRecentTrackRecordDate());
        entity.setAnnualizedReturn(fundDto.getAnnualizedReturn());
        entity.setSortino(fundDto.getSortino());
        entity.setAlpha(fundDto.getAlpha());
        entity.setBeta(fundDto.getBeta());
        entity.setOmega(fundDto.getOmega());
        entity.setCfVar(fundDto.getCfVar());
        entity.setTotalScore(fundDto.getTotalScore());

        //entity.setStrategyAUMCheck(fundDto.getStrategyAUMCheck());
        entity.setFundAUMCheck(fundDto.getFundAUMCheck());
        entity.setManagerAUMCheck(fundDto.getManagerAUMCheck());
        entity.setTrackRecordCheck(fundDto.getTrackRecordCheck());
        entity.setExcluded(fundDto.isExcluded());
        entity.setAdded(fundDto.isAdded());
        return  entity;
    }

    public List<HedgeFundScreeningSavedResultFunds> assembleList(List<HedgeFundScreeningParsedDataDto> fundDtoList, int type, Long saveResultsId){
        List<HedgeFundScreeningSavedResultFunds> entities = new ArrayList<>();
        if(fundDtoList != null){
            for(HedgeFundScreeningParsedDataDto fundDto: fundDtoList){
                entities.add(assemble(fundDto, type, saveResultsId));
            }
        }
        return entities;
    }


}
