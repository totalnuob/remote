package kz.nicnbk.service.converter.dozer;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.service.converter.BaseEntityConverter;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by magzumov on 07.07.2016.
 */
@Service
public abstract class BaseDozerEntityConverter<E extends BaseEntity, DTO extends BaseDto> extends BaseEntityConverter<E, DTO> {

    @Autowired
    private Mapper mapper;

    @Override
    public E assemble(DTO dto) {
        E entity = mapper.map(dto, getEntityClass());
        return entity;
    }

    @Override
    public DTO disassemble(E entity) {
        DTO dto = mapper.map(entity, getDtoClass());
        return dto;
    }


    // TODO: get generic type parameter

    public abstract Class<E> getEntityClass();

    public abstract Class<DTO> getDtoClass();
}
