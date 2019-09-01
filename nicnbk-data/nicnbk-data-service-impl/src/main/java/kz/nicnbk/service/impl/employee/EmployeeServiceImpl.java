package kz.nicnbk.service.impl.employee;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.HashUtils;
import kz.nicnbk.common.service.util.PaginationUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.api.employee.PositionRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.repo.model.employee.Position;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.converter.employee.EmployeeEntityConverter;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.employee.EmployeePagedSearchResult;
import kz.nicnbk.service.dto.employee.EmployeeSearchParamsDto;
import kz.nicnbk.service.dto.employee.PositionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private PositionRepository positionRepository;

    @Override
    public List<EmployeeDto> findAll(){
        try {
            List<EmployeeDto> dtoList = new ArrayList<>();
            Iterator<Employee> entities = this.employeeRepository.findAll().iterator();
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
    public EntitySaveResponseDto save(EmployeeDto employeeDto, String updater) {
        EntitySaveResponseDto saveResponseDto = checkEmployeeProfile(employeeDto);
        if(saveResponseDto.getStatus() == null || saveResponseDto.getStatus().getCode().equalsIgnoreCase(ResponseStatusType.FAIL.getCode())){
            logger.error("Failed to save employee profile." + saveResponseDto.getMessage().getMessageText() + " [updater=" + updater + "]");
            return saveResponseDto;
        }
        try {
            Employee entity = null;

            if(employeeDto.getId() == null){
                entity = this.employeeEntityConverter.assemble(employeeDto);
            }else{
                entity = this.employeeRepository.findOne(employeeDto.getId());

                entity.setFirstName(employeeDto.getFirstName());
                entity.setLastName(employeeDto.getLastName());
                entity.setBirthDate(employeeDto.getBirthDate());

            }
            Position position = null;
            if(employeeDto.getPosition() != null && StringUtils.isNotEmpty(employeeDto.getPosition().getCode())){
                position = this.positionRepository.findByCode(employeeDto.getPosition().getCode());
            }
            entity.setPosition(position);

            this.employeeRepository.save(entity);
            logger.info("Successfully saved employee profile: id= " + entity.getId().longValue() + ", username=" + employeeDto.getUsername() + " [updater=" + updater + "]");
            saveResponseDto.setSuccessMessageEn("Successfully saved employee profile");
            return saveResponseDto;
        }catch (Exception ex){
            logger.error("Failed to save employee profile.", ex);
            saveResponseDto.setErrorMessageEn("Failed to save employee profile");
            return saveResponseDto;
        }
    }

    private EntitySaveResponseDto checkEmployeeProfile(EmployeeDto employeeDto){
        EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
        saveResponseDto.setStatus(ResponseStatusType.SUCCESS);

        if(StringUtils.isEmpty(employeeDto.getLastName())){
            saveResponseDto.setErrorMessageEn("Last name cannot be empty");
        }else if(StringUtils.isEmpty(employeeDto.getFirstName())){
            saveResponseDto.setErrorMessageEn("First name cannot be empty");
        }else if(employeeDto.getBirthDate() == null){
            saveResponseDto.setErrorMessageEn("Date of birth cannot be empty");
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
                String salt = employee.getSalt();
                String generatedPassword = generatePassword(password, salt);
                if (employee.getPassword().equals(generatedPassword)) {
                    // authentication OK, return employee info
                    EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(employee);
                    return employeeDto;
                } else {
                    // authentication failed
                    return null;
                }
            }
        }catch (Exception ex){
            logger.error("Employee search by username-password failed with error: username=" + username, ex);
        }
        return null;
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
    public List<PositionDto> getALlPositions() {
        List<PositionDto> positions = new ArrayList<>();
        List<Position> entities = this.positionRepository.getAllPositions();
        if(positions != null){
            // TODO: converter
            for(Position entity: entities){
                PositionDto positionDto = new PositionDto(entity.getId(), entity.getCode(), entity.getNameRu(),
                        entity.getNameEn(), entity.getNameKz(), null);
                if(entity.getDepartment() != null){
                    positionDto.setDepartment(new BaseDictionaryDto(entity.getDepartment().getCode(), entity.getDepartment().getNameEn(),
                            entity.getDepartment().getNameRu(), entity.getDepartment().getNameKz()));
                }
                positions.add(positionDto);
            }
        }
        return positions;
    }

    private String generateSalt(){
        return HashUtils.generateRandomText();
    }

    private static String generatePassword(String plainPassword, String salt){
        String generated = HashUtils.hashSHA256String(salt + plainPassword);
        return generated;
    }

}
