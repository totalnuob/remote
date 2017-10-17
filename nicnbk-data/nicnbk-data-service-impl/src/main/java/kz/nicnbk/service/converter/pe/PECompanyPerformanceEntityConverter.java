package kz.nicnbk.service.converter.pe;

import kz.nicnbk.repo.model.pe.PECompanyPerformance;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceDto;
import org.springframework.stereotype.Component;

/**
 * Created by Pak on 03.10.2017.
 */
@Component
public class PECompanyPerformanceEntityConverter extends BaseDozerEntityConverter<PECompanyPerformance, PECompanyPerformanceDto> {

//    @Override
//    public PECompanyPerformance assemble(PECompanyPerformanceDto dto){
//        return super.assemble(dto);
//    }
//
//    @Override
//    public PECompanyPerformanceDto disassemble(PECompanyPerformance entity){
//        return super.disassemble(entity);
//    }
}
