package kz.nicnbk.service.converter.pe;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.common.PEIndustry;
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
public class PeFundEntityConverter extends BaseDozerEntityConverter<PEFund, PeFundDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public PEFund assemble(PeFundDto dto){
        PEFund entity = super.assemble(dto);

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
        Set<PEIndustry> industries = new HashSet<>();

        if(dto.getIndustry() != null){
            for(BaseDictionaryDto industryDto: dto.getIndustry()){
                PEIndustry PEIndustry = lookupService.findByTypeAndCode(PEIndustry.class, industryDto.getCode());
                industries.add(PEIndustry);
            }
        }
        entity.setPEIndustry(industries);

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
    public PeFundDto disassemble(PEFund entity){
        PeFundDto dto = super.disassemble(entity);
        return dto;
    }

    public List<PeFundDto> disassembleList(List<PEFund> entities){
        List<PeFundDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(PEFund entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public Set<PeFundDto> disassembleSet(List<PEFund> entities){
        Set<PeFundDto> dtoSet = new HashSet<>();
        if(entities != null){
            for(PEFund entity: entities){
                dtoSet.add(disassemble(entity));
            }
        }
        return dtoSet;
    }
}
