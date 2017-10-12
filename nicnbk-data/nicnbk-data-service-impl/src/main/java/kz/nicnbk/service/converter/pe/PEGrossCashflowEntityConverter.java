package kz.nicnbk.service.converter.pe;

import kz.nicnbk.repo.model.pe.PEFund;
import kz.nicnbk.repo.model.pe.PEGrossCashflow;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.pe.PEGrossCashflowDto;
import org.springframework.stereotype.Component;

/**
 * Created by zhambyl on 05-Jan-17.
 */
@Component
public class PEGrossCashflowEntityConverter extends BaseDozerEntityConverter<PEGrossCashflow, PEGrossCashflowDto> {

    @Override
    public PEGrossCashflow assemble(PEGrossCashflowDto dto){
        return super.assemble(dto);
    }

    @Override
    public PEGrossCashflowDto disassemble(PEGrossCashflow entity){
        return super.disassemble(entity);
    }
}