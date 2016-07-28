package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.converter.files.FilesEntityConverter;
import kz.nicnbk.service.datamanager.LookupTypeService;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by magzumov on 08.07.2016.
 */
@Component
public class MeetingMemoEntityConverter extends BaseDozerEntityConverter<MeetingMemo, MeetingMemoDto> {

    @Autowired
    private FilesEntityConverter filesEntityConverter;

    @Autowired
    private LookupTypeService lookupTypeService;

    @Override
    public MeetingMemo assemble(MeetingMemoDto dto){
        MeetingMemo entity = super.assemble(dto);
        if(StringUtils.isNotEmpty(dto.getMeetingType())) {
            MeetingType meetingType = lookupTypeService.findByTypeAndCode(MeetingType.class, dto.getMeetingType());
            entity.setMeetingType(meetingType);
        }

        //MemoType memoType = lookupTypeService.findByTypeAndCode(MemoType.class, dto.getMemoType());
        //entity.setMemoType(memoType);

        //MeetingArrangedBy arrangedBy = lookupTypeService.findByTypeAndCode(MeetingArrangedBy.class, dto.getArrangedBy());
        //entity.setArrangedBy(arrangedBy);

        return entity;
    }

    @Override
    public MeetingMemoDto disassemble(MeetingMemo entity){
        MeetingMemoDto dto = super.disassemble(entity);
        if(entity.getMeetingType() != null) {
            dto.setMeetingType(entity.getMeetingType().getCode());
        }
        //dto.setMemoType(entity.getMemoType().getCode());
        //dto.setArrangedBy(entity.getArrangedBy().getCode());
        return dto;
    }

    @Override
    public Class<MeetingMemo> getEntityClass() {
        return MeetingMemo.class;
    }

    @Override
    public Class<MeetingMemoDto> getDtoClass() {
        return MeetingMemoDto.class;
    }
}
