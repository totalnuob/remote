package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */


@RestController
@RequestMapping("/employee")
public class EmployeeServiceREST {

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public List<EmployeeDto> findAll(){
        List<EmployeeDto> employees = this.employeeService.findAll();
        return employees;
    }
}
