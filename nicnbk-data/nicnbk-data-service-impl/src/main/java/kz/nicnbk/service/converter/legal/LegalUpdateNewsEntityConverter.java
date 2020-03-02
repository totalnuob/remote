package kz.nicnbk.service.converter.legal;

import kz.nicnbk.repo.model.legal.LegalUpdateNews;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.legal.LegalUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 07.07.2016.
 */
@Component
public class LegalUpdateNewsEntityConverter extends BaseDozerEntityConverter<LegalUpdateNews, LegalUpdateDto> {

    @Override
    public LegalUpdateNews assemble(LegalUpdateDto dto) {
        LegalUpdateNews entity = super.assemble(dto);
        return entity;
    }

    @Override
    public LegalUpdateDto disassemble(LegalUpdateNews entity) {
        LegalUpdateDto dto = super.disassemble(entity);

        if(entity.getCreator() != null){
            dto.setCreator(entity.getCreator().getUsername());
        }
        return dto;
    }
}
