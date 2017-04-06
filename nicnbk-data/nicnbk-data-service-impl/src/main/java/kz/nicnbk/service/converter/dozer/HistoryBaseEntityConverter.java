package kz.nicnbk.service.converter.dozer;

import kz.nicnbk.common.service.model.HistoryBaseEntityDto;
import kz.nicnbk.repo.model.base.HistoryBaseEntity;

/**
 * Created by magzumov on 24.03.2017.
 */
public abstract class HistoryBaseEntityConverter<E extends HistoryBaseEntity, DTO extends HistoryBaseEntityDto> extends BaseDozerEntityConverter<E, DTO>{

    @Override
    public E assemble(DTO dto) {
        E entity = getEntityInstance();
        getMapper().map(dto, entity);
        return entity;
    }

    @Override
    public DTO disassemble(E entity) {
        DTO dto = getDTOInstance();
        getMapper().map(entity, dto);
        return dto;
    }
}
