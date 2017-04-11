package kz.nicnbk.service.impl.employee;

import kz.nicnbk.common.service.util.HashUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.converter.employee.EmployeeEntityConverter;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */
@Service
public class EmployeeServiceImpl implements EmployeeService{

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeEntityConverter employeeEntityConverter;

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

    private String generateSalt(){
        return HashUtils.generateRandomText();
    }

    private static String generatePassword(String plainPassword, String salt){
        String generated = HashUtils.hashSHA256String(salt + plainPassword);
        return generated;
    }

}
