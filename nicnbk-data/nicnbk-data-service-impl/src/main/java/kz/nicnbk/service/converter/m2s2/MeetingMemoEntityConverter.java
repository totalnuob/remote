package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import org.springframework.stereotype.Component;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class MeetingMemoEntityConverter extends CommonMeetingMemoConverter<MeetingMemo, MeetingMemoDto> {

    @Override
    public MeetingMemo assemble(MeetingMemoDto dto){
        MeetingMemo entity = super.assemble(dto);
        assembleNonmappedFields(entity, dto);

        return entity;
    }

    @Override
    public MeetingMemoDto disassemble(MeetingMemo entity){
        MeetingMemoDto dto = super.disassemble(entity);
        disassembleNonmappedFields(dto, entity);
        return dto;
    }
}
