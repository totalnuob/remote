package kz.nicnbk.service.converter.pe;

import kz.nicnbk.repo.model.pe.PECompanyPerformanceIdd;
import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.pe.PECompanyPerformanceIddDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pak on 17.10.2017.
 */
@Component
public class PECompanyPerformanceIddEntityConverter extends BaseDozerEntityConverter<PECompanyPerformanceIdd, PECompanyPerformanceIddDto> {

    public PECompanyPerformanceIdd assembleWithFundId(PECompanyPerformanceIddDto dto, Long fundId) {
        PECompanyPerformanceIdd entity = super.assemble(dto);
        entity.setFund(new PEFund(fundId));
        return entity;
    }

    public List<PECompanyPerformanceIdd> assembleListWithFundId(List<PECompanyPerformanceIddDto> dtoList, Long fundId) {

        List<PECompanyPerformanceIdd> entityList = new ArrayList<>();

        for (PECompanyPerformanceIddDto dto : dtoList) {
            PECompanyPerformanceIdd entity = this.assembleWithFundId(dto, fundId);
            entityList.add(entity);
        }

        return entityList;
    }

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
