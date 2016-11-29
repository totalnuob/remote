package kz.nicnbk.service.converter.pe;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.pe.Fund;
import kz.nicnbk.repo.model.pe.common.Industry;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.pe.PeFundDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 15-Nov-16.
 */
@Component
public class PeFundEntityConverter extends BaseDozerEntityConverter<Fund, PeFundDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public Fund assemble(PeFundDto dto){
        Fund entity = super.assemble(dto);

        // strategies
        Set<Strategy> strategies = new HashSet<>();

        if(dto.getStrategy() != null){
            for(BaseDictionaryDto strategyDto: dto.getStrategy()){
                Strategy strategy = lookupService.findByTypeAndCode(Strategy.class, strategyDto.getCode());
                strategies.add(strategy);
            }
        }
        entity.setStrategy(strategies);

        //IndustryFocus
        Set<Industry> industries = new HashSet<>();

        if(dto.getIndustry() != null){
            for(BaseDictionaryDto industryDto: dto.getIndustry()){
                Industry industry = lookupService.findByTypeAndCode(Industry.class, industryDto.getCode());
                industries.add(industry);
            }
        }
        entity.setIndustry(industries);

        // geographies
        Set<Geography> geographies = new HashSet<>();
        if(dto.getGeography() != null){
            for(BaseDictionaryDto geographyDto: dto.getGeography()){
                Geography geography = lookupService.findByTypeAndCode(Geography.class, geographyDto.getCode());
                geographies.add(geography);
            }
        }
        entity.setGeography(geographies);

        return entity;
    }

    @Override
    public PeFundDto disassemble(Fund entity){
        PeFundDto dto = super.disassemble(entity);
        return dto;
    }

    public List<PeFundDto> disassembleList(List<Fund> entities){
        List<PeFundDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(Fund entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public Set<PeFundDto> disassembleSet(List<Fund> entities){
        Set<PeFundDto> dtoSet = new HashSet<>();
        if(entities != null){
            for(Fund entity: entities){
                dtoSet.add(disassemble(entity));
            }
        }
        return dtoSet;
    }
}
