package kz.nicnbk.service.converter.hr;

import kz.nicnbk.repo.model.hr.HRNews;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.hr.HRNewsDto;

import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 07.07.2016.
 */
@Component
public class HRNewsEntityConverter extends BaseDozerEntityConverter<HRNews, HRNewsDto> {

    @Override
    public HRNews assemble(HRNewsDto dto) {
        HRNews entity = super.assemble(dto);

        return entity;
    }

    @Override
    public HRNewsDto disassemble(HRNews entity) {
        HRNewsDto dto = super.disassemble(entity);
        return dto;
    }
}
