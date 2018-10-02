package kz.nicnbk.service.converter.reporting;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.api.reporting.PeriodicDataTypeRepository;
import kz.nicnbk.repo.model.reporting.PeriodicData;
import kz.nicnbk.repo.model.reporting.PeriodicDataType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.reporting.PeriodicDataDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class PeriodicDataEntityConverter extends BaseDozerEntityConverter<PeriodicData, PeriodicDataDto> {

    @Autowired
    private PeriodicDataTypeRepository periodicDataTypeRepository;

    @Override
    public PeriodicData assemble(PeriodicDataDto dto){
        PeriodicData entity = super.assemble(dto);
        if (dto.getType() != null && dto.getType().getCode() != null) {
            PeriodicDataType type = this.periodicDataTypeRepository.findByCode(dto.getType().getCode());
            entity.setType(type);
        }
        return entity;
    }

    @Override
    public PeriodicDataDto disassemble(PeriodicData entity){
        PeriodicDataDto dto = super.disassemble(entity);
        if (entity.getType() != null) {
            BaseDictionaryDto typeDto = new BaseDictionaryDto(entity.getType().getCode(), entity.getType().getNameEn(),
                    entity.getType().getNameRu(), entity.getType().getNameKz());
            dto.setType(typeDto);
        }

        return dto;
    }
}
