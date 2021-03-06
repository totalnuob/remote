package kz.nicnbk.service.converter.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.CorpMeetingType;
import kz.nicnbk.repo.model.corpmeetings.ICMeeting;
import kz.nicnbk.repo.model.corpmeetings.ICMeetingPlaceType;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.corpmeetings.ICMeetingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhambyl on 04-Aug-16.
 */
@Component
public class ICMeetingsEntityConverter extends BaseDozerEntityConverter<ICMeeting, ICMeetingDto> {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private CorpMeetingService corpMeetingService;

    @Override
    public ICMeeting assemble(ICMeetingDto dto) {
        if (dto == null) {
            return null;
        }

        ICMeeting entity = super.assemble(dto);

        if(dto.getPlace() != null){
            ICMeetingPlaceType place = this.lookupService.findByTypeAndCode(ICMeetingPlaceType.class, dto.getPlace());
            entity.setPlace(place);
        }
        if(dto.getCorpMeetingType() != null){
            CorpMeetingType type = this.lookupService.findByTypeAndCode(CorpMeetingType.class, dto.getCorpMeetingType());
            entity.setCorpMeetingType(type);
        }

        return entity;
    }

    @Override
    public ICMeetingDto disassemble(ICMeeting entity) {
        if (entity == null) {
            return null;
        }
        ICMeetingDto dto = super.disassemble(entity); //mapper.map(entity, TripMemoDto.class);

        // creator
        if(entity.getCreator() != null){
            dto.setCreator(entity.getCreator().getUsername());
        }

        // updater
        if(entity.getUpdater() != null){
            dto.setUpdater(entity.getUpdater().getUsername());
        }
        if(entity.getPlace() != null){
            dto.setPlace(entity.getPlace().getCode());
        }
        if(entity.getCorpMeetingType() != null){
            dto.setCorpMeetingType(entity.getCorpMeetingType().getCode());
        }

        return dto;
    }

    public ICMeetingDto disassembleLimited(ICMeeting entity) {
        if (entity == null) {
            return null;
        }
        ICMeetingDto dto = super.disassemble(entity); //mapper.map(entity, TripMemoDto.class);

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
    public List<ICMeeting> assembleList(List<ICMeetingDto> dtoList) {
        List<ICMeeting> entities = new ArrayList<>();
        if(dtoList != null){
            for(ICMeetingDto dto:dtoList){
                entities.add(assemble(dto));
            }
        }
        return entities;
    }

    @Override
    public List<ICMeetingDto> disassembleList(List<ICMeeting> entityList) {
        List<ICMeetingDto> dto = new ArrayList<>();
        if(entityList != null){
            for(ICMeeting entity:entityList) {
                dto.add(disassemble(entity));
            }
        }
        return dto;
    }
}
