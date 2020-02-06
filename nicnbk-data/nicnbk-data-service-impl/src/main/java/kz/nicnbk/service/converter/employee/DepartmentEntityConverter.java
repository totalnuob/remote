package kz.nicnbk.service.converter.employee;

import kz.nicnbk.repo.model.employee.Department;
import kz.nicnbk.service.converter.dozer.BaseDozerTypedEntityConverter;
import kz.nicnbk.service.dto.employee.DepartmentDto;
import org.springframework.stereotype.Component;

/**
 * Created by pak on 06.02.2020.
 */
@Component
public class DepartmentEntityConverter extends BaseDozerTypedEntityConverter<Department, DepartmentDto> {
}
