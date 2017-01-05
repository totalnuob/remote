package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.hf.HedgeFund;
import kz.nicnbk.repo.model.hf.HedgeFundReturn;
import kz.nicnbk.repo.model.hf.InvestorBase;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hf.InvestorBaseDto;
import kz.nicnbk.service.dto.hf.ReturnDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by magzumov on 15.12.2016.
 */
@Component
public class HedgeFundReturnEntityConverter extends BaseDozerEntityConverter<HedgeFundReturn, ReturnDto> {

    @Override
    public HedgeFundReturn assemble(ReturnDto dto){
        HedgeFundReturn entity = super.assemble(dto);

        // hedge fund
        HedgeFund hedgeFund = new HedgeFund();
        hedgeFund.setId(dto.getFund().getId());
        entity.setFund(hedgeFund);
        return entity;
    }

}
