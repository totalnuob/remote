package kz.nicnbk.service.converter.pe;

import kz.nicnbk.repo.model.pe.Fund;
import kz.nicnbk.repo.model.pe.PeNetCashflow;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.pe.PeNetCashflowDto;
import org.springframework.stereotype.Component;

/**
 * Created by zhambyl on 12-Jan-17.
 */
@Component
public class PeNetCashflowEntityConverter extends BaseDozerEntityConverter<PeNetCashflow, PeNetCashflowDto>{

    @Override
    public PeNetCashflow assemble(PeNetCashflowDto dto){
        PeNetCashflow entity = super.assemble(dto);

        Fund fund = new Fund();
        fund.setId(dto.getFund().getId());
        entity.setFund(fund);
        return entity;
    }

    @Override
    public PeNetCashflowDto disassemble(PeNetCashflow entity){
        PeNetCashflowDto dto = super.disassemble(entity);
        return dto;
    }

}
