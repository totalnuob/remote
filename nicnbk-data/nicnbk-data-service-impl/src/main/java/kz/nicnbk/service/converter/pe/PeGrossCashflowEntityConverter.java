package kz.nicnbk.service.converter.pe;

import kz.nicnbk.repo.model.pe.Fund;
import kz.nicnbk.repo.model.pe.PeGrossCashflow;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.pe.PeGrossCashflowDto;
import org.springframework.stereotype.Component;

/**
 * Created by zhambyl on 05-Jan-17.
 */
@Component
public class PeGrossCashflowEntityConverter extends BaseDozerEntityConverter<PeGrossCashflow, PeGrossCashflowDto> {

    @Override
    public PeGrossCashflow assemble(PeGrossCashflowDto dto){
        PeGrossCashflow entity = super.assemble(dto);

        Fund fund = new Fund();
        fund.setId(dto.getFund().getId());
        entity.setFund(fund);
        return entity;
    }

    @Override
    public PeGrossCashflowDto disassemble(PeGrossCashflow entity){
        PeGrossCashflowDto dto = super.disassemble(entity);
        return dto;
    }
}
