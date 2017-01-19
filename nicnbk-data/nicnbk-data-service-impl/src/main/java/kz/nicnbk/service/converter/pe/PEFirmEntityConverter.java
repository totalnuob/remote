package kz.nicnbk.service.converter.pe;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.common.Geography;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.pe.PEFirm;
import kz.nicnbk.repo.model.pe.PEIndustry;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.pe.PEFirmDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhambyl on 15-Nov-16.
 */
@Component
public class PEFirmEntityConverter extends BaseDozerEntityConverter<PEFirm,PEFirmDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public PEFirm assemble(PEFirmDto dto){

        PEFirm entity = super.assemble(dto);

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

        if(dto.getIndustryFocus() != null){
            for(BaseDictionaryDto industryDto: dto.getIndustryFocus()){
                PEIndustry PEIndustry = lookupService.findByTypeAndCode(PEIndustry.class, industryDto.getCode());
                industries.add(PEIndustry);
            }
        }
        entity.setIndustryFocus(industries);

        // geographies
        Set<Geography> geographies = new HashSet<>();
        if(dto.getGeographyFocus() != null){
            for(BaseDictionaryDto geographyDto: dto.getGeographyFocus()){
                Geography geography = lookupService.findByTypeAndCode(Geography.class, geographyDto.getCode());
                geographies.add(geography);
            }
        }
        entity.setGeographyFocus(geographies);

        return entity;
    }

    @Override
    public PEFirmDto disassemble(PEFirm entity){
        PEFirmDto dto = super.disassemble(entity);

        return dto;
    }

    public Set<PEFirmDto> disassembleSet(List<PEFirm> entities) {
        Set<PEFirmDto> dtoSet = new HashSet<>();
        if(entities != null){
            for(PEFirm entity: entities){
                dtoSet.add(disassemble(entity));
            }
        }
        return dtoSet;
    }
}