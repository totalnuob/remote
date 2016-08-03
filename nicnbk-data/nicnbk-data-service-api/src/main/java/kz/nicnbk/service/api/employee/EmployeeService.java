package kz.nicnbk.service.api.employee;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.employee.EmployeeDto;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface EmployeeService extends BaseService {

    List<EmployeeDto> findAll();
}
