package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupTypeService;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.m2s2.MeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by magzumov on 03.08.2016.
 */
public abstract class CommonMeetingMemoConverter<E extends MeetingMemo, DTO extends MeetingMemoDto> extends BaseDozerEntityConverter<E, DTO> {

    @Autowired
    private LookupTypeService lookupTypeService;

    protected void assembleNonmappedFields(E entity, DTO dto){
        // meetingType
        if(StringUtils.isNotEmpty(dto.getMeetingType())) {
            MeetingType meetingType = lookupTypeService.findByTypeAndCode(MeetingType.class, dto.getMeetingType());
            entity.setMeetingType(meetingType);
        }
    }

    protected void assembleLazyNonmappedFields(E entity, DTO dto){
        // arranged by
        if(StringUtils.isNotEmpty(dto.getArrangedBy())){
            MeetingArrangedBy arrangedBy = lookupTypeService.findByTypeAndCode(MeetingArrangedBy.class, dto.getArrangedBy());
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
