package kz.nicnbk.service.converter.hf;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.common.Currency;
import kz.nicnbk.repo.model.hf.HFManager;
import kz.nicnbk.repo.model.hf.ManagerType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.hf.HFFirmDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by timur on 19.10.2016.
 */

@Component
public class HFManagerEntityConverter extends BaseDozerEntityConverter<HFManager, HFFirmDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public HFManager assemble(HFFirmDto dto){
        HFManager entity = super.assemble(dto);

        // manager type
        if(StringUtils.isNotEmpty(dto.getManagerType())) {
            ManagerType managerType = lookupService.findByTypeAndCode(ManagerType.class, dto.getManagerType());
            entity.setManagerType(managerType);
        }

        if(StringUtils.isNotEmpty(dto.getAumCurrency())) {
            Currency currency = lookupService.findByTypeAndCode(Currency.class, dto.getAumCurrency());
            entity.setAUMCurrency(currency);
        }

        return entity;
    }

    @Override
    public HFFirmDto disassemble(HFManager entity){
        HFFirmDto dto = super.disassemble(entity);
        // manager type
        if(entity.getManagerType() != null){
            dto.setManagerType(entity.getManagerType().getCode());
        }

        //AUM Currency
        if(entity.getAUMCurrency() != null){
            dto.setAumCurrency(entity.getAUMCurrency().getCode());
        }

        return dto;
    }

}
