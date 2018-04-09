package kz.nicnbk.service.converter.corpmeetings;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.corpmeetings.CorpMeeting;
import kz.nicnbk.repo.model.corpmeetings.CorpMeetingType;
import kz.nicnbk.repo.model.tripmemo.TripMemo;
import kz.nicnbk.repo.model.tripmemo.TripType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.corpmeetings.CorpMeetingDto;
import kz.nicnbk.service.dto.tripmemo.TripMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
@Component
public class CorpMeetingsEntityConverter extends BaseDozerEntityConverter<CorpMeeting, CorpMeetingDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public CorpMeeting assemble(CorpMeetingDto dto) {
        if (dto == null) {
            return null;
        }

        CorpMeeting entity = super.assemble(dto);

        // type
        if(StringUtils.isNotEmpty(dto.getType())){
            CorpMeetingType type = lookupService.findByTypeAndCode(CorpMeetingType.class, dto.getType());
            entity.setType(type);
        }

        return entity;
    }

    @Override
    public CorpMeetingDto disassemble(CorpMeeting entity) {
        if (entity == null) {
            return null;
        }
        CorpMeetingDto dto = super.disassemble(entity); //mapper.map(entity, TripMemoDto.class);

        // trip type
        if(entity.getType() != null){
            dto.setType(entity.getType().getCode());
        }

        // creator
        if(entity.getCreator() != null){
            dto.setCreator(entity.getCreator().getUsername());
        }

        // updater
        if(entity.getUpdater() != null){
            dto.setUpdater(entity.getUpdater().getUsername());
        }
        return dto;
    }

    @Override
    public List<CorpMeeting> assembleList(List<CorpMeetingDto> dtoList) {
        List<CorpMeeting> entities = new ArrayList<>();
        if(dtoList != null){
            for(CorpMeetingDto dto:dtoList){
                entities.add(assemble(dto));
            }
        }
        return entities;
    }

    @Override
    public List<CorpMeetingDto> disassembleList(List<CorpMeeting> entityList) {
        List<CorpMeetingDto> dto = new ArrayList<>();
        if(entityList != null){
            for(CorpMeeting entity:entityList) {
                dto.add(disassemble(entity));
            }
        }
        return dto;
    }
}
