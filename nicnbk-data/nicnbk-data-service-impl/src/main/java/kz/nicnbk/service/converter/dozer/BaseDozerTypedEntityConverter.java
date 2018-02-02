package kz.nicnbk.service.converter.dozer;

import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.repo.model.base.BaseTypeEntity;
import kz.nicnbk.service.converter.BaseTypedEntityConverter;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;

/**
 * Created by magzumov on 07.07.2016.
 */
@Service
public abstract class BaseDozerTypedEntityConverter<E extends BaseTypeEntity, DTO extends BaseDto> extends BaseTypedEntityConverter<E, DTO> {

    @Autowired
    private Mapper mapper;

    public E getEntityInstance() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<E> type = (Class<E>) superClass.getActualTypeArguments()[0];
        try {
            return type.newInstance();
        }
        catch (Exception e) {
            // Oops, no default constructor
            throw new RuntimeException(e);
        }
    }

    public DTO getDTOInstance() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<DTO> type = (Class<DTO>) superClass.getActualTypeArguments()[1];
        try {
            return type.newInstance();
        }
        catch (Exception e) {
            // Oops, no default constructor
            throw new RuntimeException(e);
        }
    }

    @Override
    public E assemble(DTO dto) {
        E entity = getEntityInstance();
        mapper.map(dto, entity);
        return entity;
    }

    @Override
    public DTO disassemble(E entity) {
        DTO dto = getDTOInstance();
        mapper.map(entity, dto);
        return dto;
    }

    public Mapper getMapper(){
        return this.mapper;
    }
}
