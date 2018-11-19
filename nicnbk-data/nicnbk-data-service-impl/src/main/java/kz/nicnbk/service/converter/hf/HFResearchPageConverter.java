package kz.nicnbk.service.converter.hf;

import kz.nicnbk.repo.model.hf.HFResearchPage;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hf.HFResearchPageDto;
import org.springframework.stereotype.Component;

/**
 * Created by zhambyl on 12/11/2018.
 */
@Component
public class HFResearchPageConverter extends BaseDozerEntityConverter<HFResearchPage, HFResearchPageDto> {


    @Override
    public HFResearchPage assemble(HFResearchPageDto dto) {
        HFResearchPage entity = super.assemble(dto);

        return entity;
    }

    @Override
    public HFResearchPageDto disassemble(HFResearchPage entity) {
        HFResearchPageDto dto = super.disassemble(entity);

        return dto;
    }
}
