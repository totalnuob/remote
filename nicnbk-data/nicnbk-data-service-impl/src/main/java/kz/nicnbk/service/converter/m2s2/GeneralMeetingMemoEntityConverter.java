package kz.nicnbk.service.converter.m2s2;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.m2s2.GeneralMeetingMemo;
import kz.nicnbk.repo.model.m2s2.MeetingArrangedBy;
import kz.nicnbk.repo.model.m2s2.MeetingType;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupTypeService;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.m2s2.GeneralMeetingMemoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


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
