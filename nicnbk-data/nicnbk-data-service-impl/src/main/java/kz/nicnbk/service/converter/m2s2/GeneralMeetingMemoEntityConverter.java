package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.m2s2.GeneralMeetingMemo;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupTypeService;
import kz.nicnbk.service.dto.m2s2.GeneralMeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class GeneralMeetingMemoEntityConverter extends BaseDozerEntityConverter<GeneralMeetingMemo, GeneralMeetingMemoDto> {

    @Autowired
    private LookupTypeService lookupTypeService;

    @Override
    public GeneralMeetingMemo assemble(GeneralMeetingMemoDto dto){
        GeneralMeetingMemo entity = super.assemble(dto);

        // TODO: refactor code duplication
        if(StringUtils.isNotEmpty(dto.getMeetingType())) {
            MeetingType meetingType = lookupTypeService.findByTypeAndCode(MeetingType.class, dto.getMeetingType());
            entity.setMeetingType(meetingType);
        }

        return entity;
    }

    @Override
    public GeneralMeetingMemoDto disassemble(GeneralMeetingMemo entity){
        GeneralMeetingMemoDto dto = super.disassemble(entity);

        // TODO: refactor code duplication
        // set meeting type
        if(entity.getMeetingType() != null){
            dto.setMeetingType(entity.getMeetingType().getCode());
        }
        return dto;
    }

    @Override
    public Class<GeneralMeetingMemo> getEntityClass() {
        return GeneralMeetingMemo.class;
    }

    @Override
    public Class<GeneralMeetingMemoDto> getDtoClass() {
        return GeneralMeetingMemoDto.class;
    }
}
