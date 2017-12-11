package kz.nicnbk.service.converter;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.model.BaseDto;
import kz.nicnbk.repo.model.base.BaseEntity;
import kz.nicnbk.repo.model.base.BaseTypeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */
public abstract class BaseEntityConverter<E extends BaseEntity, DTO extends BaseDto> extends BaseConverter{

    public abstract E assemble(DTO dto);

    public abstract DTO disassemble(E entity);

    public List<E> assembleList(List<DTO> dtoList){
        List<E> entityList = new ArrayList<>();
        if(dtoList != null){
            for(DTO dto: dtoList) {
                entityList.add(assemble(dto));
            }
        }
        return entityList;
    }

    public List<DTO> disassembleList(List<E> entityList){
        List<DTO> dtoList = new ArrayList<>();
        if(entityList != null){
            for(E entity: entityList) {
                dtoList.add(disassemble(entity));
            }
        }
        return dtoList;
    }

    public static BaseDictionaryDto disassembleTypeEntity(BaseTypeEntity entity){
        BaseDictionaryDto dto = new BaseDictionaryDto();
        dto.setCode(entity.getCode());
        dto.setNameEn(entity.getNameEn());
        dto.setNameRu(entity.getNameRu());
        dto.setNameKz(entity.getNameKz());
        return dto;
    }
}
