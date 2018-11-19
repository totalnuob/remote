package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.api.hf.HedgeFundScreeningRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.hf.HedgeFundScreening;
import kz.nicnbk.repo.model.hf.HedgeFundScreeningParsedData;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningDto;
import kz.nicnbk.service.dto.hf.HedgeFundScreeningParsedDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HedgeFundScreeningParsedDataEntityConverter extends BaseDozerEntityConverter<HedgeFundScreeningParsedData, HedgeFundScreeningParsedDataDto> {

    @Override
    public HedgeFundScreeningParsedData assemble(HedgeFundScreeningParsedDataDto dto){
        HedgeFundScreeningParsedData entity = super.assemble(dto);
        return entity;
    }

    @Override
    public HedgeFundScreeningParsedDataDto disassemble(HedgeFundScreeningParsedData entity){
        HedgeFundScreeningParsedDataDto dto = super.disassemble(entity);
        return dto;
    }

    public List<HedgeFundScreeningParsedDataDto> disassembleList(List<HedgeFundScreeningParsedData> entities){
        List<HedgeFundScreeningParsedDataDto> dtoList = new ArrayList<>();
        if(entities != null){
            for(HedgeFundScreeningParsedData entity: entities){
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public List<HedgeFundScreeningParsedData> assembleList(List<HedgeFundScreeningParsedDataDto> dtos){
        List<HedgeFundScreeningParsedData> dtoList = new ArrayList<>();
        if(dtos != null){
            for(HedgeFundScreeningParsedDataDto dto: dtos){
                dtoList.add(assemble(dto));
            }
        }
        return dtoList;
    }


}
