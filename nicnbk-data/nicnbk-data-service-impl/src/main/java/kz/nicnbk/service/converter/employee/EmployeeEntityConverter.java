package kz.nicnbk.service.converter.employee;

import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import org.springframework.stereotype.Component;

/**
 * Created by magzumov on 07.07.2016.
 */
@Component
public class EmployeeEntityConverter extends BaseDozerEntityConverter<Employee, EmployeeDto> {

}
