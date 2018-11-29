package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedUcitsData;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningParsedDataDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HedgeFundScreeningParsedUcitsDataEntityConverter extends BaseDozerEntityConverter<HedgeFundScreeningParsedUcitsData, HedgeFundScreeningParsedDataDto> {

    @Override
    public HedgeFundScreeningParsedUcitsData assemble(HedgeFundScreeningParsedDataDto dto){
        HedgeFundScreeningParsedUcitsData entity = super.assemble(dto);
        return entity;
    }

    @Override
    public HedgeFundScreeningParsedDataDto disassemble(HedgeFundScreeningParsedUcitsData entity){
        HedgeFundScreeningParsedDataDto dto = super.disassemble(entity);
        return dto;
    }

    public List<HedgeFundScreeningParsedDataDto> disassembleList(List<HedgeFundScreeningParsedUcitsData> entities){
        List<HedgeFundScreeningParsedDataDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFundScreeningParsedUcitsData entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public List<HedgeFundScreeningParsedUcitsData> assembleList(List<HedgeFundScreeningParsedDataDto> dtos){
        List<HedgeFundScreeningParsedUcitsData> dtoList = new ArrayList<>();
        if(dtos != null){
            for(HedgeFundScreeningParsedDataDto dto: dtos){
                dtoList.add(assemble(dto));
            }
        }
        return dtoList;
    }


}
