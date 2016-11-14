package kz.nicnbk.service.converter.hf;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.common.Country;
import kz.nicnbk.repo.model.common.Strategy;
import kz.nicnbk.repo.model.hf.HFManager;
import kz.nicnbk.repo.model.hf.ManagerType;
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

        // manager type
        if(StringUtils.isNotEmpty(dto.getManagerType())) {
            ManagerType managerType = lookupService.findByTypeAndCode(ManagerType.class, dto.getManagerType());
            entity.setManagerType(managerType);
        }
        //strategy
        if(StringUtils.isNotEmpty(dto.getStrategy())) {
            Strategy strategy = lookupService.findByTypeAndCode(Strategy.class, dto.getStrategy());
            entity.setStrategy(strategy);
        }
        //status
//        if(StringUtils.isNotEmpty(dto.getStatus())) {
//            FundStatus status = lookupService.findByTypeAndCode(FundStatus.class, dto.getStatus());
//            entity.setStatus(status);
//        }

        //legal structure
//        if(StringUtils.isNotEmpty(dto.getLegalStructure())) {
//            LegalStructure legalStructure = lookupService.findByTypeAndCode(LegalStructure.class, dto.getLegalStructure());
//            entity.setLegalStructure(legalStructure);
//        }

        //domicile country
        if(StringUtils.isNotEmpty(dto.getDomicileCountry())) {
            Country domicileCountry = lookupService.findByTypeAndCode(Country.class, dto.getDomicileCountry());
            entity.setDomicileCountry(domicileCountry);
        }
        return entity;
    }

    @Override
    public HFManagerDto disassemble(HFManager entity){
        HFManagerDto dto = super.disassemble(entity);
        // manager type
        if(entity.getManagerType() != null){
            dto.setManagerType(entity.getManagerType().getCode());
        }

        // strategy
        if(entity.getStrategy() != null){
            dto.setStrategy(entity.getStrategy().getCode());
        }

        // status
//        if(entity.getStatus() != null){
//            dto.setStatus(entity.getStatus().getCode());
//        }

        // legal structure
//        if(entity.getLegalStructure() != null){
//            dto.setLegalStructure(entity.getLegalStructure().getCode());
//        }

        // domicile country
        if(entity.getDomicileCountry() != null){
            dto.setDomicileCountry(entity.getDomicileCountry().getCode());
        }
        return dto;
    }

}
