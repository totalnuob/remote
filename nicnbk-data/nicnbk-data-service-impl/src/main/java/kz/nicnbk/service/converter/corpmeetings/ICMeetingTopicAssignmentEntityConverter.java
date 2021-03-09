package kz.nicnbk.service.converter.corpmeetings;

import kz.nicnbk.repo.model.corpmeetings.ICMeetingTopicAssignment;
import kz.nicnbk.service.api.tag.TagService;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.corpmeetings.ICMeetingTopicAssignmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ICMeetingTopicAssignmentEntityConverter extends BaseDozerEntityConverter<ICMeetingTopicAssignment, ICMeetingTopicAssignmentDto> {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private TagService tagService;

    @Override
    public ICMeetingTopicAssignment assemble(ICMeetingTopicAssignmentDto dto) {
        if (dto == null) {
            return null;
        }

        ICMeetingTopicAssignment entity = super.assemble(dto);
        return entity;
    }

    @Override
    public ICMeetingTopicAssignmentDto disassemble(ICMeetingTopicAssignment entity) {
        if (entity == null) {
            return null;
        }
        ICMeetingTopicAssignmentDto dto = super.disassemble(entity);
        return dto;
    }

    @Override
    public List<ICMeetingTopicAssignment> assembleList(List<ICMeetingTopicAssignmentDto> dtoList) {
        List<ICMeetingTopicAssignment> entities = new ArrayList<>();
        if(dtoList != null){
            for(ICMeetingTopicAssignmentDto dto:dtoList){
                entities.add(assemble(dto));
            }
        }
        return entities;
    }

    @Override
    public List<ICMeetingTopicAssignmentDto> disassembleList(List<ICMeetingTopicAssignment> entityList) {
        List<ICMeetingTopicAssignmentDto> dtoList = new ArrayList<>();
        if(entityList != null){
            for(ICMeetingTopicAssignment entity:entityList) {
                ICMeetingTopicAssignmentDto dto = disassemble(entity);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }
}
