package kz.nicnbk.service.converter.hf;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.hf.*;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.hf.HedgeFundDto;
import kz.nicnbk.service.dto.hf.HedgeFundScoringDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HedgeFundScoringConverter extends BaseDozerEntityConverter<HedgeFundScoring, HedgeFundScoringDto> {

    @Override
    public HedgeFundScoring assemble(HedgeFundScoringDto dto){
        HedgeFundScoring entity = super.assemble(dto);

        //
        return entity;
    }

    @Override
    public HedgeFundScoringDto disassemble(HedgeFundScoring entity){
        HedgeFundScoringDto dto = super.disassemble(entity);
        return dto;
    }

    public List<HedgeFundScoringDto> disassembleList(List<HedgeFundScoring> entities){
        List<HedgeFundScoringDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFundScoring entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public List<HedgeFundScoring> assembleList(List<HedgeFundScoringDto> dtoList){
        List<HedgeFundScoring> entities = new ArrayList<>();
        if(dtoList != null){
            for(HedgeFundScoringDto dto: dtoList){
                entities.add(assemble(dto));
            }
        }
        return entities;
    }

}
