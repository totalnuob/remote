package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.repo.model.m2s2.GeneralMeetingMemo;
import kz.nicnbk.service.dto.m2s2.GeneralMeetingMemoDto;
import org.springframework.stereotype.Component;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class GeneralMeetingMemoEntityConverter extends CommonMeetingMemoConverter<GeneralMeetingMemo, GeneralMeetingMemoDto> {

    @Override
    public GeneralMeetingMemo assemble(GeneralMeetingMemoDto dto){
        GeneralMeetingMemo entity = super.assemble(dto);
        assembleNonmappedFields(entity, dto);
        assembleLazyNonmappedFields(entity, dto);
        return entity;
    }

    @Override
    public GeneralMeetingMemoDto disassemble(GeneralMeetingMemo entity){
        GeneralMeetingMemoDto dto = super.disassemble(entity);
        disassembleNonmappedFields(dto, entity);
        disassembleLazyNonmappedFields(dto, entity);
        return dto;
    }
}
