package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedDataReturn;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningParsedDataDateValueDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HedgeFundScreeningParsedDataReturnEntityConverter extends BaseDozerEntityConverter<HedgeFundScreeningParsedDataReturn, HedgeFundScreeningParsedDataDateValueDto> {

    @Override
    public HedgeFundScreeningParsedDataReturn assemble(HedgeFundScreeningParsedDataDateValueDto dto){
        HedgeFundScreeningParsedDataReturn entity = super.assemble(dto);
        return entity;
    }

    @Override
    public HedgeFundScreeningParsedDataDateValueDto disassemble(HedgeFundScreeningParsedDataReturn entity){
        HedgeFundScreeningParsedDataDateValueDto dto = super.disassemble(entity);
        return dto;
    }

    public List<HedgeFundScreeningParsedDataDateValueDto> disassembleList(List<HedgeFundScreeningParsedDataReturn> entities){
        List<HedgeFundScreeningParsedDataDateValueDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFundScreeningParsedDataReturn entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public List<HedgeFundScreeningParsedDataReturn> assembleList(List<HedgeFundScreeningParsedDataDateValueDto> dtos){
        List<HedgeFundScreeningParsedDataReturn> dtoList = new ArrayList<>();
        if(dtos != null){
            for(HedgeFundScreeningParsedDataDateValueDto dto: dtos){
                dtoList.add(assemble(dto));
            }
        }
        return dtoList;
    }


}
