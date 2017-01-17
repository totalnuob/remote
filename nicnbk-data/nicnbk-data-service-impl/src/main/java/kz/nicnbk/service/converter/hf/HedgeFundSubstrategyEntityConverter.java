package kz.nicnbk.service.converter.hf;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.common.Substrategy;
import kz.nicnbk.repo.model.hf.HedgeFund;
import kz.nicnbk.repo.model.hf.HedgeFundSubstrategy;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.hf.HedgeFundSubstrategyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 14.12.2016.
 */
@Component
public class HedgeFundSubstrategyEntityConverter  extends BaseDozerEntityConverter<HedgeFundSubstrategy, HedgeFundSubstrategyDto> {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private HedgeFundEntityConverter hedgeFundEntityConverter;

    @Override
    public HedgeFundSubstrategy assemble(HedgeFundSubstrategyDto dto){

        HedgeFund fund = new HedgeFund();
        fund.setId(dto.getFund().getId());
        Substrategy substrategy = lookupService.findByTypeAndCode(Substrategy.class, dto.getSubstrategy().getCode());

        HedgeFundSubstrategy entity = new HedgeFundSubstrategy();
        entity.setFund(fund);
        entity.setSubstrategy(substrategy);

        entity.setValue(dto.getValue());
        return entity;
    }

    @Override
    public HedgeFundSubstrategyDto disassemble(HedgeFundSubstrategy entity){
        HedgeFundSubstrategyDto dto = new HedgeFundSubstrategyDto();
        dto.setValue(entity.getValue());

        BaseDictionaryDto substrategyDto = new BaseDictionaryDto();
        substrategyDto.setCode(entity.getSubstrategy().getCode());
        substrategyDto.setNameEn(entity.getSubstrategy().getNameEn());
        dto.setSubstrategy(substrategyDto);

        //dto.setPEFund();
        return dto;
    }
}
