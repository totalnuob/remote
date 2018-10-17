package kz.nicnbk.service.converter.corpmeetings;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.corpmeetings.CorpMeeting;
import kz.nicnbk.repo.model.corpmeetings.CorpMeetingType;
import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopic;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.corpmeetings.CorpMeetingDto;
import kz.nicnbk.service.dto.corpmeetings.ICMeetingTopicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
@Component
public class ICMeetingTopicEntityConverter extends BaseDozerEntityConverter<ICMeetingTopic, ICMeetingTopicDto> {

    @Autowired
    private LookupService lookupService;

    @Override
    public ICMeetingTopic assemble(ICMeetingTopicDto dto) {
        if (dto == null) {
            return null;
        }

        ICMeetingTopic entity = super.assemble(dto);

        if(entity.getIcMeeting() != null && entity.getIcMeeting().getId() == null){
            entity.setIcMeeting(null);
        }
        return entity;
    }

    @Override
    public ICMeetingTopicDto disassemble(ICMeetingTopic entity) {
        if (entity == null) {
            return null;
        }
        ICMeetingTopicDto dto = super.disassemble(entity); //mapper.map(entity, TripMemoDto.class);

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
    public List<ICMeetingTopic> assembleList(List<ICMeetingTopicDto> dtoList) {
        List<ICMeetingTopic> entities = new ArrayList<>();
        if(dtoList != null){
            for(ICMeetingTopicDto dto:dtoList){
                entities.add(assemble(dto));
            }
        }
        return entities;
    }

    @Override
    public List<ICMeetingTopicDto> disassembleList(List<ICMeetingTopic> entityList) {
        List<ICMeetingTopicDto> dto = new ArrayList<>();
        if(entityList != null){
            for(ICMeetingTopic entity:entityList) {
                dto.add(disassemble(entity));
            }
        }
        return dto;
    }
}
