package kz.nicnbk.service.api.employee;

import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.employee.*;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface EmployeeService extends BaseService {

    List<EmployeeDto> findAll();

    EmployeeDto getEmployeeById(Long empoyeeId);

    EmployeeDto getEmployeeByUsername(String username);

    EmployeeFullDto getFullEmployeeByUsername(String username);

    EmployeePagedSearchResult search(EmployeeSearchParamsDto searchParams);

    EntitySaveResponseDto save(EmployeeDto employeeDto, String updater);

    EntitySaveResponseDto saveAndChangePassword(EmployeeDto employeeDto, String password, String updater);

    EmployeeDto findActiveByUsernamePassword(String username, String password);

    EmployeeDto findByUsername(String username);

    boolean setPassword(String username, String password, String user);

    boolean deactivate(String username);

    boolean activate(String username);

    List<PositionDto> getAllPositions();

    List<RoleDto> getAllRoles();

    List<DepartmentDto> getAllDepartments();
}
