package kz.nicnbk.service.api.employee;

import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.employee.EmployeeDto;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface EmployeeService extends BaseService {

    List<EmployeeDto> findAll();

    EmployeeDto findActiveByUsernamePassword(String username, String password);

    EmployeeDto findByUsername(String username);

    boolean setPassword(String username, String password, String user);

    boolean deactivate(String username);

    boolean activate(String username);

}
