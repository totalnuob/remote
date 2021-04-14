package kz.nicnbk.service.impl.employee;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.HashUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.employee.DepartmentRepository;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.employee.PositionRepository;
import kz.nicnbk.repo.api.employee.RoleRepository;
import kz.nicnbk.repo.model.employee.Department;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.employee.Position;
import kz.nicnbk.repo.model.employee.Role;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.email.EmailService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.converter.employee.DepartmentEntityConverter;
import kz.nicnbk.service.converter.employee.EmployeeEntityConverter;
import kz.nicnbk.service.converter.employee.EmployeeFullEntityConverter;
import kz.nicnbk.service.converter.employee.RoleEntityConverter;
import kz.nicnbk.service.dto.authentication.UserRoles;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.employee.*;
import kz.nicnbk.service.impl.authentication.TotpServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by magzumov on 02.08.2016.
 */
@Service
public class EmployeeServiceImpl implements EmployeeService{

    public static final int DEFAULT_PAGE_SIZE =  20;
    public static final int DEFAULT_PAGES_PER_VIEW = 5;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeEntityConverter employeeEntityConverter;

    @Autowired
    private EmployeeFullEntityConverter employeeFullEntityConverter;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleEntityConverter roleEntityConverter;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentEntityConverter departmentEntityConverter;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Override
    public List<EmployeeDto> findAll(){
        try {
            List<EmployeeDto> dtoList = new ArrayList<>();
            Iterator<Employee> entities = this.employeeRepository.findAll(new Sort(Sort.Direction.ASC, "firstName")).iterator();
            while (entities.hasNext()) {
                EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(entities.next());
                dtoList.add(employeeDto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load full employee list", ex);
        }
        return null;
    }

    @Override
    public List<EmployeeDto> findActiveAll(){
        try {
            List<EmployeeDto> dtoList = new ArrayList<>();
            Iterator<Employee> entities = this.employeeRepository.findAll(new Sort(Sort.Direction.ASC, "firstName")).iterator();
            while (entities.hasNext()) {
                EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(entities.next());
                dtoList.add(employeeDto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load full employee list", ex);
        }
        return null;
    }

    @Override
    public List<EmployeeDto> findICMembers(){
        try {
            List<EmployeeDto> dtoList = findEmployeesByRoleCode(UserRoles.IC_MEMBER.getCode(), true);
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load full employee list", ex);
        }
        return null;
    }

    @Override
    public List<EmployeeDto> findUsersWithRole(String role,Boolean active){
        try {
            List<EmployeeDto> dtoList = findEmployeesByRoleCode(role, active);
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load full employee list", ex);
        }
        return null;
    }

    @Override
    public List<EmployeeDto> findByDepartmentAndActive(int departmentId, Boolean active){
        try {
            List<EmployeeDto> dtoList = new ArrayList<>();
            List<Employee> entities = this.employeeRepository.findByPositionDepartmentIdAndActive(departmentId, active);
            for(Employee employee: entities) {
                EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(employee);
                dtoList.add(employeeDto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load employee list by department", ex);
        }
        return null;
    }

    @Override
    public List<EmployeeDto> findExecutivesAndActive(){
        try {
            List<EmployeeDto> dtoList = new ArrayList<>();
            String[] executives = {"CEO", "DEP_CEO", "MNG_DIR"};
            List<Employee> entities = this.employeeRepository.findByPositionCodesAndActive( true, executives);
            for(Employee employee: entities) {
                EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(employee);
                dtoList.add(employeeDto);
            }
            return dtoList;
        }catch (Exception ex){
            logger.error("Failed to load employee list by department", ex);
        }
        return null;
    }

    @Override
    public List<EmployeeDto> findByDepartmentAndActiveWithExecutives(int departmentId){
        List<EmployeeDto> employees = findByDepartmentAndActive(departmentId, true);
        employees.addAll(findExecutivesAndActive());
        return employees;
    }

    @Override
    public List<EmployeeDto> findByDepartmentWithExecutives(int departmentId){
        List<EmployeeDto> employees = findByDepartmentAndActive(departmentId, null);
        employees = employees != null ? employees : new ArrayList<>();
        employees.addAll(findExecutivesAndActive());
        return employees;
    }


    private List<EmployeeDto> findEmployeesByRoleCode(String code, Boolean active){
        List<EmployeeDto> employeeList = new ArrayList<>();
        List<EmployeeDto> allEmployees = findAll();
        if(allEmployees != null){
            for(EmployeeDto employeeDto: allEmployees){
                if(active != null){
                    if(employeeDto.getActive() == null || active.booleanValue() != employeeDto.getActive().booleanValue()){
                        //skip employee
                        continue;
                    }
                }
                if(employeeDto.getRoles() != null){
                    for(BaseDictionaryDto role: employeeDto.getRoles()){
                        if(role.getCode() != null && role.getCode().equalsIgnoreCase(code)){
                            employeeList.add(employeeDto);
                        }
                    }
                }
            }
        }
        return employeeList;
    }

    @Override
    public List<EmployeeDto> findEmployeesByRoleCodes(String[] codes){
        List<EmployeeDto> employeeList = new ArrayList<>();
        List<EmployeeDto> allEmployees = findAll();
        if(allEmployees != null && codes != null){
            for(EmployeeDto employeeDto: allEmployees){
                boolean added = false;
                if(employeeDto.getRoles() != null){
                    for(BaseDictionaryDto role: employeeDto.getRoles()){
                        for(String code: codes) {
                            if (role.getCode() != null && role.getCode().equalsIgnoreCase(code)) {
                                employeeList.add(employeeDto);
                                added = true;
                                break;
                            }
                        }
                        if(added){
                            break;
                        }
                    }
                }
            }
        }
        return employeeList;
    }

    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        if(employeeId != null) {
            Employee employee = this.employeeRepository.findOne(employeeId);
            if (employee != null) {
                EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(employee);
                return employeeDto;
            }
        }
        return null;
    }

    @Override
    public EmployeeDto getEmployeeByUsername(String username) {
        if(username != null) {
            Employee employee = this.employeeRepository.findByUsername(username);
            if (employee != null) {
                EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(employee);
                return employeeDto;
            }
        }
        return null;
    }

    @Override
    public EmployeeFullDto getFullEmployeeByUsername(String username) {
        if(username != null) {
            Employee employee = this.employeeRepository.findByUsername(username);
            if (employee != null) {
                return this.employeeFullEntityConverter.disassemble(employee);
            }
        }
        return null;
    }

    @Override
    public EmployeePagedSearchResult search(EmployeeSearchParamsDto searchParams) {
        try {
            Page<Employee> entityPage = null;
            int page = 0;
            if (searchParams == null || searchParams.isEmpty()) {
                int pageSize = searchParams != null && searchParams.getPageSize() > 0 ? searchParams.getPageSize() : DEFAULT_PAGE_SIZE;
                page = searchParams != null && searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                entityPage = employeeRepository.findAll(new PageRequest(page, pageSize, new Sort(Sort.Direction.ASC, "lastName", "firstName")));
            } else {
                page = searchParams.getPage() > 0 ? searchParams.getPage() - 1 : 0;
                Boolean status = searchParams.getStatusBoolean();
                entityPage = employeeRepository.search(searchParams.getFirstNameOrEmpty(), searchParams.getLastNameOrEmpty(),
                        status, new PageRequest(page, searchParams.getPageSize(), new Sort(Sort.Direction.ASC, "lastName", "firstName")));
            }

            EmployeePagedSearchResult result = new EmployeePagedSearchResult();
            if (entityPage != null) {
                result.setTotalElements(entityPage.getTotalElements());
                if (entityPage.getTotalElements() > 0) {
                    result.setShowPageFrom(PaginationUtils.getShowPageFrom(DEFAULT_PAGES_PER_VIEW, page));
                    result.setShowPageTo(PaginationUtils.getShowPageTo(DEFAULT_PAGES_PER_VIEW,
                            page, result.getShowPageFrom(), entityPage.getTotalPages()));
                }
                result.setTotalPages(entityPage.getTotalPages());
                result.setCurrentPage(page + 1);
                if (searchParams != null) {
                    result.setSearchParams(searchParams.getSearchParamsAsString());
                }
                result.setEmployees(employeeEntityConverter.disassembleList(entityPage.getContent()));
            }
            return result;
        }catch(Exception ex){
            // TODO: log search params
            logger.error("Error searching trip memos", ex);
        }
        return null;
    }

    @Override
    public EntitySaveResponseDto save(EmployeeFullDto employeeFullDto, String updater, Boolean isAdmin) {
        EntitySaveResponseDto saveResponseDto = checkEmployeeProfile(employeeFullDto);
        if(saveResponseDto.getStatus() == null || saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
            logger.error("Failed to save employee profile." + saveResponseDto.getMessage().getMessageText() + " [updater=" + updater + "]");
            return saveResponseDto;
        }

        try {
            Employee entity = null;

            if (employeeFullDto.getId() == null) {
                if (isAdmin) {
                    entity = this.employeeEntityConverter.assemble(employeeFullDto);
                    entity.setLocked(employeeFullDto.getLocked());
                    entity.setFailedLoginAttempts(employeeFullDto.getFailedLoginAttempts());
                } else {
                    logger.error("Failed to save employee profile");
                    saveResponseDto.setErrorMessageEn("Failed to save employee profile");
                    return saveResponseDto;
                }
            } else {
                entity = this.employeeRepository.findOne(employeeFullDto.getId());

                if(isAdmin){
                    if(entity.getActive() != employeeFullDto.getActive()) {
                        this.tokenService.revokeUsername(entity.getUsername());
                    }
                    entity.setActive(employeeFullDto.getActive());
                    entity.setLocked(employeeFullDto.getLocked());
                    entity.setFailedLoginAttempts(employeeFullDto.getFailedLoginAttempts());
                    entity.setMfaEnabled(employeeFullDto.getMfaEnabled());
                }

                entity.setFirstName(employeeFullDto.getFirstName());
                entity.setLastName(employeeFullDto.getLastName());
                entity.setBirthDate(employeeFullDto.getBirthDate());
                entity.setPatronymic(employeeFullDto.getPatronymic());

                entity.setLastNameRu(employeeFullDto.getLastNameRu());
                entity.setFirstNameRu(employeeFullDto.getFirstNameRu());
                entity.setPatronymicRu(employeeFullDto.getPatronymicRu());
                entity.setLastNameRuPossessive(employeeFullDto.getLastNameRuPossessive());

                entity.setEmail(employeeFullDto.getEmail());

            }
            Position position = null;
            if(employeeFullDto.getPosition() != null && StringUtils.isNotEmpty(employeeFullDto.getPosition().getCode())){
                position = this.positionRepository.findByCode(employeeFullDto.getPosition().getCode());
            }
            entity.setPosition(position);

            if (isAdmin) {
                Set<Role> roles = new HashSet<>();
                if(employeeFullDto.getRoles() != null) {
                    for (BaseDictionaryDto dto: employeeFullDto.getRoles()) {
                        roles.add(this.roleRepository.findOne(dto.getId()));
                    }
                }
                if(entity.getRoles() == null || !entity.getRoles().equals(roles)) {
                    this.tokenService.revokeUsername(entity.getUsername());
                }
                entity.setRoles(roles);
            }

            Long id = this.employeeRepository.save(entity).getId();
            logger.info("Successfully saved employee profile: id= " + entity.getId().longValue() + ", username=" + employeeFullDto.getUsername() + " [updater=" + updater + "]");
            saveResponseDto.setSuccessMessageEn("Successfully saved employee profile");
            saveResponseDto.setEntityId(id);
            return saveResponseDto;
        }catch (Exception ex){
            logger.error("Failed to save employee profile.", ex);
            saveResponseDto.setErrorMessageEn("Failed to save employee profile");
            return saveResponseDto;
        }
    }

    @Override
    public EntitySaveResponseDto saveAndChangePassword(EmployeeFullDto employeeFullDto, String password, Boolean emailCheckbox, String updater) {
        EntitySaveResponseDto saveResponseDto = this.save(employeeFullDto, updater, true);
        if (emailCheckbox != null ? emailCheckbox : false) {
            sendPasswordByEmail(employeeFullDto, password);
        }
        if(saveResponseDto.getStatus() == null || saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())) {
            return saveResponseDto;
        }
        if(employeeFullDto == null || !this.setPassword(employeeFullDto.getUsername(), password, updater)) {
            saveResponseDto.setErrorMessageEn("Employee profile was saved without the new password");
        }
        return saveResponseDto;
    }

    private void sendPasswordByEmail(EmployeeFullDto employeeFullDto, String password) {
        emailService.sendMail(employeeFullDto.getEmail(), "UNIC – password reset",
                "UNIC Password has been updated by system administrator.\n" +
                        "New generated password: " + password + ".");
    }

    private EntitySaveResponseDto checkEmployeeProfile(EmployeeDto employeeDto){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        saveResponseDto.setStatus(ResponseStatusType.SUCCESS);

        if (employeeDto == null) {
            saveResponseDto.setErrorMessageEn("Error saving profile");
            return saveResponseDto;
        }

        if(StringUtils.isEmpty(employeeDto.getLastName())){
            saveResponseDto.setErrorMessageEn("Last name cannot be empty");
        }else if(StringUtils.isEmpty(employeeDto.getFirstName())){
            saveResponseDto.setErrorMessageEn("First name cannot be empty");
//        }else if(employeeDto.getBirthDate() == null){
//            saveResponseDto.setErrorMessageEn("Date of birth cannot be empty");
        }else if(StringUtils.isEmpty(employeeDto.getUsername())){
            saveResponseDto.setErrorMessageEn("Username cannot be empty");
        }

        if(employeeDto.getId() != null) {
            Employee entity = this.employeeRepository.findOne(employeeDto.getId());
            if(entity == null){
                logger.error("Failed to save employee profile with id=" + employeeDto.getId().longValue() + ". No entity with id found");
                saveResponseDto.setErrorMessageEn("Failed to save employee profile with id=" + employeeDto.getId().longValue() + ". No entity with id found");
                return saveResponseDto;
            }
            if(!entity.getUsername().equals(employeeDto.getUsername())){
                // CANNOT CHANGE USERNAME
                logger.error("Failed to save employee profile: username cannot be changed");
                saveResponseDto.setErrorMessageEn("Failed to save employee profile: username cannot be changed");
                return saveResponseDto;
            }
        }
        return saveResponseDto;
    }

    /**
     * Returns employee matching specified credentials, or null if no match found.
     *
     * @param username - employee username
     * @param password - employee password
     * @return - employee or null
     */
    @Override
    public EmployeeDto findActiveByUsernamePassword(String username, String password) {

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return null;
        }

        try {
            Employee employee = this.employeeRepository.findByUsername(username);
            if (employee != null) {
                // check active
                if (employee.getActive() != null && !employee.getActive()) {
                    return null;
                }
                if (employee.getLocked() != null && employee.getLocked()) {
                    this.failedLoginAttempt(employee);
                    return null;
                }
                String salt = employee.getSalt();
                String generatedPassword = generatePassword(password, salt);
                if (employee.getPassword().equals(generatedPassword)) {
                    // authentication OK, return employee info
                    this.successfulLoginAttempt(employee);
                    EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(employee);
                    return employeeDto;
                } else {
                    // authentication failed
                    this.failedLoginAttempt(employee);
                    return null;
                }
            }
        }catch (Exception ex){
            logger.error("Employee search by username-password failed with error: username=" + username, ex);
        }
        return null;
    }

    @Override
    public EmployeeDto findActiveByUsernamePasswordCode(String username, String password, String otp) {

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            return null;
        }

        try {
            Employee employee = this.employeeRepository.findByUsername(username);
            if (employee != null) {
                if (employee.getActive() != null && !employee.getActive()) {
                    return null;
                }
                if (employee.getLocked() != null && employee.getLocked()) {
                    this.failedLoginAttempt(employee);
                    return null;
                }
                String salt = employee.getSalt();
                String generatedPassword = generatePassword(password, salt);
                if (employee.getPassword().equals(generatedPassword)) {
                    if (employee.getMfaEnabled() == null || !employee.getMfaEnabled()) {
                        this.successfulLoginAttempt(employee);
                        return this.employeeEntityConverter.disassemble(employee);
                    }

                    if (!StringUtils.isEmpty(otp) && !StringUtils.isEmpty(employee.getSecret())) {
                        TotpServiceImpl generator = new TotpServiceImpl(employee.getSecret());
                        if (generator.verify(otp)) {
                            this.successfulLoginAttempt(employee);
                            return this.employeeEntityConverter.disassemble(employee);
                        }
                    }

                    this.failedLoginAttempt(employee);
                    return null;
                } else {
                    this.failedLoginAttempt(employee);
                    return null;
                }
            }
        }catch (Exception ex){
            logger.error("Employee search by username-password-code failed with error: username=" + username, ex);
        }
        return null;
    }

    private Boolean successfulLoginAttempt(Employee employee) {
        if (employee == null) {
            return false;
        }
        employee.setFailedLoginAttempts(0);
        employee.setLocked(false);
        this.employeeRepository.save(employee);
        return true;
    }

    private Boolean failedLoginAttempt(Employee employee) {
        if (employee == null) {
            return false;
        }
        if (employee.getFailedLoginAttempts() == null) {
            employee.setFailedLoginAttempts(1);
        } else {
            employee.setFailedLoginAttempts(employee.getFailedLoginAttempts() + 1);
        }
        if (employee.getFailedLoginAttempts() >= 3) {
            employee.setLocked(true);
        }
        this.employeeRepository.save(employee);
        return true;
    }

    @Override
    public EmployeeDto findByUsername(String username) {
        if(StringUtils.isEmpty(username)){
            return null;
        }
        try {
            Employee employee = this.employeeRepository.findByUsername(username);
            if (employee != null) {
                EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(employee);
                return employeeDto;
            }
        }catch (Exception ex){
            logger.error("Failed to search by employee username: username=" + username, ex);
        }
        return null;
    }

    @Override
    public boolean checkRoleByUsername(UserRoles role, String username){
        Employee employee = this.employeeRepository.findByUsername(username);
        if(employee != null && employee.getRoles() != null && role != null){
            for(Role aRole: employee.getRoles()){
                if(aRole.getCode().equalsIgnoreCase(role.getCode())){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean setPassword(String username, String password, String user){
        try {
            Employee employee = this.employeeRepository.findByUsername(username);
            if(employee != null){
                String salt = generateSalt();
                String newPassword = generatePassword(password, salt);
                employee.setSalt(salt);
                employee.setPassword(newPassword);

                this.employeeRepository.save(employee);
                logger.info("Successfully changed password: username=" + username + ", by " + user);
                this.tokenService.revokeUsername(username);
                return true;
            }
        }catch(Exception ex){
            logger.error("Failed to change password: username=" + username + ", by " + user, ex);
        }
        return false;
    }

    @Override
    public boolean deactivate(String username) {
        try {
            Employee employee = this.employeeRepository.findByUsername(username);
            employee.setActive(false);
            this.employeeRepository.save(employee);
            logger.info("Deactivated user: " + username);
            return true;
        }catch (Exception ex){
            logger.error("Failed to deactivate user: username=" + username, ex);
        }
        return false;
    }

    @Override
    public boolean activate(String username) {
        try {
            Employee employee = this.employeeRepository.findByUsername(username);
            employee.setActive(true);
            this.employeeRepository.save(employee);
            logger.info("Activated user: " + username);
            return true;
        }catch(Exception ex){
            logger.error("Failed to activate user: username=" + username, ex);
        }
        return false;
    }

    @Override
    public List<PositionDto> getAllPositions() {
        List<PositionDto> positions = new ArrayList<>();
        List<Position> entities = this.positionRepository.getAllPositions();
        if(positions != null){
            // TODO: converter
            for(Position entity: entities){
                PositionDto positionDto = new PositionDto(entity.getId(), entity.getCode(), entity.getNameRu(),
                        entity.getNameEn(), entity.getNameKz(), null);
                if(entity.getDepartment() != null){
                    positionDto.setDepartment(new DepartmentDto(entity.getDepartment()));
                }
                positions.add(positionDto);
            }
        }
        return positions;
    }

    @Override
    public List<RoleDto> getAllRoles() {
        List<Role> roles = this.roleRepository.getAllRoles();
        if(roles == null) {
            return null;
        }
        return this.roleEntityConverter.disassembleList(roles);
    }

    @Override
    public List<DepartmentDto> getAllDepartments() {
        List<Department> departments = this.departmentRepository.getAllDepartments();
        if(departments == null){
            return null;
        }
        return this.departmentEntityConverter.disassembleList(departments);
    }

    @Override
    public boolean registerMfa(String username, String secret, String otp) {

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(secret) || StringUtils.isEmpty(otp)) {
            return false;
        }

        try {
            Employee employee = this.employeeRepository.findByUsername(username);

            if (employee == null) {
                return false;
            }

            TotpServiceImpl generator = new TotpServiceImpl(secret);
            if (generator.verify(otp)) {
                employee.setMfaEnabled(true);
                employee.setSecret(secret);
                this.employeeRepository.save(employee);
                logger.info("Successfully registered MFA: id= " + employee.getId().longValue() + ", username=" + employee.getUsername());
                return true;
            }
        } catch (Exception ex) {
            logger.error("Failed to register MFA.", ex);
        }

        return false;
    }

    private String generateSalt(){
        return HashUtils.generateRandomText();
    }

    private static String generatePassword(String plainPassword, String salt){
        String generated = HashUtils.hashSHA256String(salt + plainPassword);
        return generated;
    }

    public EmployeeDto findAdmin(){
        EmployeeDto admin = findByUsername("magzumov");
        return admin;
    }

}
