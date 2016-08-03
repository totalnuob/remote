package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.m2s2.FundMeetingMemo;
import kz.nicnbk.service.datamanager.LookupTypeService;
import kz.nicnbk.service.dto.m2s2.FundMeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by magzumov on 03.08.2016.
 */
public abstract class FundMeetingMemoConverter<E extends FundMeetingMemo, DTO extends FundMeetingMemoDto> extends CommonMeetingMemoConverter<E, DTO> {

    @Autowired
    private LookupTypeService lookupTypeService;

    protected void assembleFundNonmappedFields(E entity, DTO dto){

        assembleNonmappedFields(entity, dto);
        assembleLazyNonmappedFields(entity, dto);

        // set currency
        if(StringUtils.isNotEmpty(dto.getFundSizeCurrency())) {
            Currency currency = lookupTypeService.findByTypeAndCode(Currency.class, dto.getFundSizeCurrency());
            entity.setFundSizeCurrency(currency);
        }
        // strategies
        Set<Strategy> strategies = new HashSet<>();
        if(dto.getStrategies() != null){
            for(BaseDictionaryDto strategyDto: dto.getStrategies()){
                Strategy strategy = lookupTypeService.findByTypeAndCode(Strategy.class, strategyDto.getCode());
                strategies.add(strategy);
            }
        }
        entity.setStrategies(strategies);

        // geographies
        Set<Geography> geographies = new HashSet<>();
        if(dto.getGeographies() != null){
            for(BaseDictionaryDto geographyDto: dto.getGeographies()){
                Geography geography = lookupTypeService.findByTypeAndCode(Geography.class, geographyDto.getCode());
                geographies.add(geography);
            }
        }
        entity.setGeographies(geographies);
    }

    protected void disassembleFundNonmappedFields(DTO dto, E entity){

        disassembleNonmappedFields(dto, entity);
        disassembleLazyNonmappedFields(dto, entity);

        // currency
        if(entity.getFundSizeCurrency() != null) {
            dto.setFundSizeCurrency(entity.getFundSizeCurrency().getCode());
        }

        // strategies
        Set<BaseDictionaryDto> strategies = new HashSet<>();
        if(entity.getStrategies() != null){
            for(Strategy strategy: entity.getStrategies()){
                BaseDictionaryDto strategyDto = new BaseDictionaryDto();
                strategyDto.setCode(strategy.getCode());
                strategyDto.setNameEn(strategy.getNameEn());
                strategies.add(strategyDto);
            }
        }
        dto.setStrategies(strategies);

        // geographies
        Set<BaseDictionaryDto> geographies = new HashSet<>();
        if(entity.getGeographies() != null){
            for(Geography geography: entity.getGeographies()){
                BaseDictionaryDto geographyDto = new BaseDictionaryDto();
                geographyDto.setCode(geography.getCode());
                geographyDto.setNameEn(geography.getNameEn());
                geographies.add(geographyDto);
            }
        }
        dto.setGeographies(geographies);
    }
}
