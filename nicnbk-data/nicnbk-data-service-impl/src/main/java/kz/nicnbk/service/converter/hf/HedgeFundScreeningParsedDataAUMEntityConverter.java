package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedDataAUM;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningParsedDataDateValueDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HedgeFundScreeningParsedDataAUMEntityConverter extends BaseDozerEntityConverter<HedgeFundScreeningParsedDataAUM, HedgeFundScreeningParsedDataDateValueDto> {

    @Override
    public HedgeFundScreeningParsedDataAUM assemble(HedgeFundScreeningParsedDataDateValueDto dto){
        HedgeFundScreeningParsedDataAUM entity = super.assemble(dto);
        return entity;
    }

    @Override
    public HedgeFundScreeningParsedDataDateValueDto disassemble(HedgeFundScreeningParsedDataAUM entity){
        HedgeFundScreeningParsedDataDateValueDto dto = super.disassemble(entity);
        return dto;
    }

    public List<HedgeFundScreeningParsedDataDateValueDto> disassembleList(List<HedgeFundScreeningParsedDataAUM> entities){
        List<HedgeFundScreeningParsedDataDateValueDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFundScreeningParsedDataAUM entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public List<HedgeFundScreeningParsedDataAUM> assembleList(List<HedgeFundScreeningParsedDataDateValueDto> dtos){
        List<HedgeFundScreeningParsedDataAUM> dtoList = new ArrayList<>();
        if(dtos != null){
            for(HedgeFundScreeningParsedDataDateValueDto dto: dtos){
                dtoList.add(assemble(dto));
            }
        }
        return dtoList;
    }


}
