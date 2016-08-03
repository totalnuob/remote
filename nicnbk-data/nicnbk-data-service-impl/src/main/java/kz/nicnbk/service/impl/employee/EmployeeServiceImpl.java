package kz.nicnbk.service.impl.employee;

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
}
