package kz.nicnbk.service.api.employee;

import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.authentication.UserRoles;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.employee.*;

import java.util.Date;
import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
public interface EmployeeService extends BaseService {

    List<EmployeeDto> findAll();

    List<EmployeeDto> findActiveAll();

    List<EmployeeDto> findEligibleForReset();

    List<EmployeeDto> findICMembers();

    List<EmployeeDto> findUsersWithRole(String role, Boolean active);

    List<EmployeeDto> findByDepartmentAndActive(int id, Boolean active);

    List<EmployeeDto> findExecutivesAndActive();

    List<EmployeeDto> findByDepartmentAndActiveWithExecutives(int id);
    List<EmployeeDto> findByDepartmentWithExecutives(int id);

    List<EmployeeDto> findEmployeesByRoleCodes(String[] codes);

    EmployeeDto getEmployeeById(Long empoyeeId);

    EmployeeDto getEmployeeByUsername(String username);

    EmployeeFullDto getFullEmployeeByUsername(String username);

    EmployeeDto getEmployeeByEmail(String email);

    EmployeePagedSearchResult search(EmployeeSearchParamsDto searchParams);

    EntitySaveResponseDto save(EmployeeFullDto employeeFullDto, String updater, Boolean isAdmin);

    EntitySaveResponseDto saveAndChangePassword(EmployeeFullDto employeeFullDto, String password, Boolean emailCheckbox, String updater);

    EmployeeDto findActiveByUsernamePassword(String username, String password);

    EmployeeDto findActiveByUsernamePasswordCode(String username, String password, String otp);

    EmployeeDto findByUsername(String username);

    boolean checkRoleByUsername(UserRoles role, String username);

    boolean setPassword(String username, String password, String user);

    boolean deactivate(String username);

    boolean activate(String username);

    List<PositionDto> getAllPositions();

    List<RoleDto> getAllRoles();

    List<DepartmentDto> getAllDepartments();

    boolean registerMfa(String username, String secret, String otp);

    EmployeeDto findAdmin();

    boolean setResetToken(String username, String token);

    boolean checkResetToken(String username, String token);
}
