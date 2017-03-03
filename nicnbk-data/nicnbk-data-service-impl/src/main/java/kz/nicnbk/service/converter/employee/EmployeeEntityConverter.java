package kz.nicnbk.service.converter.employee;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.employee.Role;
import kz.nicnbk.service.converter.dozer.BaseDozerEntityConverter;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by magzumov on 07.07.2016.
 */
@Component
public class EmployeeEntityConverter extends BaseDozerEntityConverter<Employee, EmployeeDto> {

}
