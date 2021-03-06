package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by magzumov on 03.08.2016.
 */
public abstract class CommonMeetingMemoConverter<E extends MeetingMemo, DTO extends MeetingMemoDto> extends BaseDozerEntityConverter<E, DTO> {

    @Autowired
    private LookupService lookupService;

    protected void assembleNonmappedFields(E entity, DTO dto){
        // meetingType
        if(StringUtils.isNotEmpty(dto.getMeetingType())) {
            MeetingType meetingType = lookupService.findByTypeAndCode(MeetingType.class, dto.getMeetingType());
            entity.setMeetingType(meetingType);
        }

        // tags
        if(dto.getTags() != null && dto.getTags().length > 0){
             entity.setTags(Arrays.toString(dto.getTags()));
        }
    }

    protected void assembleLazyNonmappedFields(E entity, DTO dto){
        // arranged by
        if(StringUtils.isNotEmpty(dto.getArrangedBy())){
            MeetingArrangedBy arrangedBy = lookupService.findByTypeAndCode(MeetingArrangedBy.class, dto.getArrangedBy());
            entity.setArrangedBy(arrangedBy);
        }
        // attendees
        if(dto.getAttendeesNIC() != null){
            Set<Employee> employees = new HashSet<>();
            for(EmployeeDto employeeDto: dto.getAttendeesNIC()){
                // TODO: check id or handle error
                Employee employee = new Employee();
                employee.setId(employeeDto.getId());
                employees.add(employee);
            }
            entity.setAttendeesNIC(employees);
        }
    }

    protected void disassembleNonmappedFields(DTO dto, E entity){
        // meetingType
        if(entity.getMeetingType() != null){
            dto.setMeetingType(entity.getMeetingType().getCode());
        }

        // tags
        if(entity.getTags() != null){
            String[] tags = entity.getTags().replace("[", "").replace("]", "").split(", ");
            String[] result = new String[tags.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = tags[i];
            }
            dto.setTags(result);
        }

        // creator
        if(entity.getCreator() != null){
            dto.setOwner(entity.getCreator().getUsername());
        }

        // updater
        if(entity.getUpdater() != null){
            dto.setUpdater(entity.getUpdater().getUsername());
        }
    }

    protected void disassembleLazyNonmappedFields(DTO dto, E entity){
        // arranged by
        if(entity.getArrangedBy() != null) {
            dto.setArrangedBy(entity.getArrangedBy().getCode());
        }
        // attendees
        Set<EmployeeDto> employees = new HashSet<>();
        if(entity.getAttendeesNIC() != null){
            for(Employee employee: entity.getAttendeesNIC()){
                EmployeeDto employeeDto = new EmployeeDto();
                employeeDto.setId(employee.getId());
                employees.add(employeeDto);
            }
        }
        dto.setAttendeesNIC(employees);
    }
}
