package kz.nicnbk.service.converter.pe;

import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PEFundCompaniesPerformance;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.pe.PEFundCompaniesPerformanceDto;
import org.springframework.stereotype.Component;

/**
 * Created by Pak on 03.10.2017.
 */
@Component
public class PEFundCompaniesPerformanceEntityConverter extends BaseDozerEntityConverter<PEFundCompaniesPerformance, PEFundCompaniesPerformanceDto> {

    @Override
    public PEFundCompaniesPerformance assemble(PEFundCompaniesPerformanceDto dto){
        PEFundCompaniesPerformance entity = super.assemble(dto);
        entity.setId(null);
        return entity;
    }

    @Override
    public PEFundCompaniesPerformanceDto disassemble(PEFundCompaniesPerformance entity){
        PEFundCompaniesPerformanceDto dto = super.disassemble(entity);
        return dto;
    }
}
