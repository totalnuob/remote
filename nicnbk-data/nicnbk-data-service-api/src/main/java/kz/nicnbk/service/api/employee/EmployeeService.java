package kz.nicnbk.service.api.employee;

import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.employee.EmployeePagedSearchResult;
import kz.nicnbk.service.dto.employee.EmployeeSearchParamsDto;
import kz.nicnbk.service.dto.employee.PositionDto;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface EmployeeService extends BaseService {

    List<EmployeeDto> findAll();

    EmployeeDto getEmployeeById(Long empoyeeId);

    EmployeeDto getEmployeeByUsername(String username);

    EmployeePagedSearchResult search(EmployeeSearchParamsDto searchParams);

    EntitySaveResponseDto save(EmployeeDto employeeDto, String updater);

    EmployeeDto findActiveByUsernamePassword(String username, String password);

    EmployeeDto findByUsername(String username);

    boolean setPassword(String username, String password, String user);

    boolean deactivate(String username);

    boolean activate(String username);

    List<PositionDto> getALlPositions();

}
