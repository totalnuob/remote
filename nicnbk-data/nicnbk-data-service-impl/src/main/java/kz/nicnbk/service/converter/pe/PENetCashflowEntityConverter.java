package kz.nicnbk.service.converter.pe;

import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PENetCashflow;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.pe.PENetCashflowDto;
import org.springframework.stereotype.Component;

/**
 * Created by zhambyl on 12-Jan-17.
 */
@Component
public class PENetCashflowEntityConverter extends BaseDozerEntityConverter<PENetCashflow, PENetCashflowDto>{

    @Override
    public PENetCashflow assemble(PENetCashflowDto dto){
        PENetCashflow entity = super.assemble(dto);

//        PEFund PEFund = new PEFund();
//        PEFund.setId(dto.getFund().getId());
//        entity.setFund(PEFund);
        return entity;
    }

    @Override
    public PENetCashflowDto disassemble(PENetCashflow entity){
        PENetCashflowDto dto = super.disassemble(entity);
        return dto;
    }
}