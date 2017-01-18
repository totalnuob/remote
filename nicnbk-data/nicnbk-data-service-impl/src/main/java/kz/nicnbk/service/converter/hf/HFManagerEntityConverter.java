package kz.nicnbk.service.converter.hf;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.hf.HFManager;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.hf.HFManagerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HFManagerEntityConverter extends BaseDozerEntityConverter<HFManager, HFManagerDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public HFManager assemble(HFManagerDto dto){
        HFManager entity = super.assemble(dto);

        if(StringUtils.isNotEmpty(dto.getAumCurrency())) {
            Currency currency = lookupService.findByTypeAndCode(Currency.class, dto.getAumCurrency());
            entity.setAUMCurrency(currency);
        }

        return entity;
    }

    @Override
    public HFManagerDto disassemble(HFManager entity){
        HFManagerDto dto = super.disassemble(entity);

        //AUM Currency
        if(entity.getAUMCurrency() != null){
            dto.setAumCurrency(entity.getAUMCurrency().getCode());
        }

        return dto;
    }

}
