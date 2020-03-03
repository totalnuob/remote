package kz.nicnbk.service.converter.employee;

import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.employee.EmployeeFullDto;
import org.springframework.stereotype.Component;

/**
 * Created by pak on 14.02.2020.
 */
@Component
public class EmployeeFullEntityConverter extends BaseDozerEntityConverter<Employee, EmployeeFullDto> {
}
