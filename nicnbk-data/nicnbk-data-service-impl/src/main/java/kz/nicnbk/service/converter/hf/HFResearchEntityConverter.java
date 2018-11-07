package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.hf.HFResearch;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hf.HFResearchDto;
import org.springframework.stereotype.Component;

/**
 * Created by zhambyl on 06/11/2018.
 */
@Component
public class HFResearchEntityConverter extends BaseDozerEntityConverter<HFResearch, HFResearchDto>{

    @Override
    public HFResearch assemble(HFResearchDto dto){
        HFResearch entity = super.assemble(dto);

        return entity;
    }

    @Override
    public HFResearchDto disassemble(HFResearch entity){
        HFResearchDto dto = super.disassemble(entity);
        return dto;
    }
}
