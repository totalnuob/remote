package kz.nicnbk.service.converter.files;

import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupTypeService;
import kz.nicnbk.service.dto.files.FilesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 07.07.2016.
 */
@Component
public class FilesEntityConverter extends BaseDozerEntityConverter<Files, FilesDto> {

    @Autowired
    private LookupTypeService lookupTypeService;

    @Override
    public Files assemble(FilesDto dto) {
        Files entity = super.assemble(dto);
        //set type
        FilesType filesType = lookupTypeService.findByTypeAndCode(FilesType.class, dto.getType());
        entity.setType(filesType);
        return entity;
    }

    @Override
    public FilesDto disassemble(Files entity) {
        FilesDto dto = super.disassemble(entity);
        // set type
        dto.setType(entity.getType().getCode());
        return dto;
    }

    @Override
    public Class<Files> getEntityClass() {
        return Files.class;
    }

    @Override
    public Class<FilesDto> getDtoClass() {
        return FilesDto.class;
    }
}
