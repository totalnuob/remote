package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedData;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedParamData;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningFundParamDataDto;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningParsedDataDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HedgeFundScreeningParsedFundParamsDataEntityConverter extends BaseDozerEntityConverter<HedgeFundScreeningParsedParamData, HedgeFundScreeningFundParamDataDto> {

    public List<HedgeFundScreeningFundParamDataDto> disassembleList(List<HedgeFundScreeningParsedParamData> entities){
        List<HedgeFundScreeningFundParamDataDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFundScreeningParsedParamData entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public List<HedgeFundScreeningParsedParamData> assembleList(List<HedgeFundScreeningFundParamDataDto> dtos){
        List<HedgeFundScreeningParsedParamData> entities = new ArrayList<>();
        if(dtos != null){
            for(HedgeFundScreeningFundParamDataDto dto: dtos){
                entities.add(assemble(dto));
            }
        }
        return entities;
    }


}
