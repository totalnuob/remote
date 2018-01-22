package kz.nicnbk.service.converter.pe;

import kz.nicnbk.repo.model.pe.PECompanyPerformanceIdd;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceIddDto;
import org.springframework.stereotype.Component;

/**
 * Created by Pak on 17.10.2017.
 */
@Component
public class PECompanyPerformanceIddEntityConverter extends BaseDozerEntityConverter<PECompanyPerformanceIdd, PECompanyPerformanceIddDto> {

//    @Override
//    public PECompanyPerformanceIdd assemble(PECompanyPerformanceIddDto dto){
//        return super.assemble(dto);
//    }
//
//    @Override
//    public PECompanyPerformanceIddDto disassemble(PECompanyPerformanceIdd entity){
//        return super.disassemble(entity);
//    }
}
