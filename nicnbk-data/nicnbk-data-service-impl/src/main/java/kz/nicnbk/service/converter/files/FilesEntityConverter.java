package kz.nicnbk.service.converter.files;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.files.Files;
import kz.nicnbk.repo.model.files.FilesType;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.datamanager.LookupService;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.files.FilesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 07.07.2016.
 */
@Component
public class FilesEntityConverter extends BaseDozerEntityConverter<Files, FilesDto> {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public Files assemble(FilesDto dto) {
        Files entity = super.assemble(dto);
        //set type
        if(StringUtils.isNotEmpty(dto.getType())) {
            FilesType filesType = lookupService.findByTypeAndCode(FilesType.class, dto.getType());
            entity.setType(filesType);
        }
        //set creator
        if(StringUtils.isNotEmpty(dto.getInsertedBy())) {
            EmployeeDto employeeDto = this.employeeService.findByUsername(dto.getInsertedBy());
            if(employeeDto != null) {
                Employee employee = new Employee(employeeDto.getId());
                entity.setCreator(employee);
            }
        }
        return entity;
    }

    @Override
    public FilesDto disassemble(Files entity) {
        FilesDto dto = super.disassemble(entity);
        // set type
        if(entity.getType() != null) {
            dto.setType(entity.getType().getCode());
        }
        return dto;
    }
}
