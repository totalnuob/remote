package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedDataAUM;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedUcitsDataAUM;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningParsedDataDateValueDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HedgeFundScreeningParsedUcitsDataAUMEntityConverter extends BaseDozerEntityConverter<HedgeFundScreeningParsedUcitsDataAUM, HedgeFundScreeningParsedDataDateValueDto> {

    @Override
    public HedgeFundScreeningParsedUcitsDataAUM assemble(HedgeFundScreeningParsedDataDateValueDto dto){
        HedgeFundScreeningParsedUcitsDataAUM entity = super.assemble(dto);
        return entity;
    }

    @Override
    public HedgeFundScreeningParsedDataDateValueDto disassemble(HedgeFundScreeningParsedUcitsDataAUM entity){
        HedgeFundScreeningParsedDataDateValueDto dto = super.disassemble(entity);
        return dto;
    }

    public List<HedgeFundScreeningParsedDataDateValueDto> disassembleList(List<HedgeFundScreeningParsedUcitsDataAUM> entities){
        List<HedgeFundScreeningParsedDataDateValueDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFundScreeningParsedUcitsDataAUM entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public List<HedgeFundScreeningParsedUcitsDataAUM> assembleList(List<HedgeFundScreeningParsedDataDateValueDto> dtos){
        List<HedgeFundScreeningParsedUcitsDataAUM> dtoList = new ArrayList<>();
        if(dtos != null){
            for(HedgeFundScreeningParsedDataDateValueDto dto: dtos){
                dtoList.add(assemble(dto));
            }
        }
        return dtoList;
    }


}
