package kz.nicnbk.service.impl.employee;

import kz.nicnbk.common.service.util.HashUtils;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.api.employee.EmployeeRepository;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.converter.employee.EmployeeEntityConverter;
import kz.nicnbk.service.dto.employee.EmployeeDto;
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

    @Autowired
    private EmployeeRepository empoloyeeRepository;

    @Autowired
    private EmployeeEntityConverter employeeEntityConverter;

    @Override
    public List<EmployeeDto> findAll(){
        List<EmployeeDto> dtoList = new ArrayList<>();
        Iterator<Employee> entities = this.empoloyeeRepository.findAll().iterator();
        while(entities.hasNext()){
            EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(entities.next());
            dtoList.add(employeeDto);
        }
        return dtoList;
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

        Employee employee = this.empoloyeeRepository.findByUsername(username);
        if(employee != null){
            // check active
            if(employee.getActive() != null && !employee.getActive()){
                return null;
            }
            String salt = employee.getSalt();
            String generatedPassword = generatePassword(password, salt);
            if(employee.getPassword().equals(generatedPassword)){
                // authentication OK, return employee info
                EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(employee);
                return employeeDto;
            }else{
                // authentication failed
                return null;
            }
        }
        return null;
    }

    @Override
    public EmployeeDto findByUsername(String username) {
        if(StringUtils.isEmpty(username)){
            return null;
        }
        Employee employee = this.empoloyeeRepository.findByUsername(username);
        if(employee != null){
            EmployeeDto employeeDto = this.employeeEntityConverter.disassemble(employee);
            return employeeDto;
        }
        return null;
    }

    @Override
    public boolean setPassword(String username, String password){
        try {
            Employee employee = this.empoloyeeRepository.findByUsername(username);
            if(employee != null){
                String salt = generateSalt();
                String newPassword = generatePassword(password, salt);
                employee.setSalt(salt);
                employee.setPassword(newPassword);

                this.empoloyeeRepository.save(employee);
                return true;
            }
        }catch(Exception ex){
            // TODO: log error
        }
        return false;
    }

    @Override
    public boolean deactivate(String username) {
        Employee employee = this.empoloyeeRepository.findByUsername(username);
        employee.setActive(false);
        this.empoloyeeRepository.save(employee);
        return true;
    }

    @Override
    public boolean activate(String username) {
        Employee employee = this.empoloyeeRepository.findByUsername(username);
        employee.setActive(true);
        this.empoloyeeRepository.save(employee);
        return true;
    }

    private String generateSalt(){
        return HashUtils.generateRandomText();
    }

    private static String generatePassword(String plainPassword, String salt){
        String generated = HashUtils.hashSHA256String(salt + plainPassword);
        return generated;
    }

}
