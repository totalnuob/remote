package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScoringResultFund;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hf.HedgeFundScoringResultFundDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HedgeFundScoringResultFundConverter extends BaseDozerEntityConverter<HedgeFundScoringResultFund, HedgeFundScoringResultFundDto> {

    @Override
    public HedgeFundScoringResultFund assemble(HedgeFundScoringResultFundDto dto){
        HedgeFundScoringResultFund entity = super.assemble(dto);

        //
        return entity;
    }

    @Override
    public HedgeFundScoringResultFundDto disassemble(HedgeFundScoringResultFund entity){
        HedgeFundScoringResultFundDto dto = super.disassemble(entity);
        return dto;
    }

    public List<HedgeFundScoringResultFundDto> disassembleList(List<HedgeFundScoringResultFund> entities){
        List<HedgeFundScoringResultFundDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFundScoringResultFund entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public List<HedgeFundScoringResultFund> assembleList(List<HedgeFundScoringResultFundDto> dtoList){
        List<HedgeFundScoringResultFund> entities = new ArrayList<>();
        if(dtoList != null){
            for(HedgeFundScoringResultFundDto dto: dtoList){
                entities.add(assemble(dto));
            }
        }
        return entities;
    }

}
