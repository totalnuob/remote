package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.hf.HedgeFund;
import kz.nicnbk.repo.model.hf.InvestorBase;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hf.InvestorBaseDto;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 15.12.2016.
 */
@Component
public class InvestorBaseEntityConverter extends BaseDozerEntityConverter<InvestorBase, InvestorBaseDto> {

    public InvestorBase assemble(InvestorBaseDto dto){
        InvestorBase entity = super.assemble(dto);

        // hedge fund
        HedgeFund hedgeFund = new HedgeFund();
        hedgeFund.setId(dto.getHedgeFund().getId());
        entity.setHedgeFund(hedgeFund);
        return entity;
    }

}
