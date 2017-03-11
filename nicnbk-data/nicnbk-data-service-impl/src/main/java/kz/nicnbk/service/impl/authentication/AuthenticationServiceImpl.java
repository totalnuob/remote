package kz.nicnbk.service.impl.authentication;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.service.api.authentication.AuthenticationService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.dto.authentication.AuthenticatedUserDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by magzumov on 21.02.2017.
 */

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private EmployeeService employeeService;

    @Override
    public AuthenticatedUserDto authenticate(String username, String password) {
        EmployeeDto employeeDto = employeeService.findActiveByUsernamePassword(username, password);
        if(employeeDto == null){
            return null;
        }else{
            AuthenticatedUserDto authenticatedUserDto = new AuthenticatedUserDto();
            authenticatedUserDto.setUsername(employeeDto.getUsername());
            if(employeeDto.getRoles() != null){
                Set<String> roles = new HashSet<>();
                for(BaseDictionaryDto role: employeeDto.getRoles()){
                    roles.add(convertToOutputRoleName(role.getCode()));
                }
                authenticatedUserDto.setRoles(roles);
            }
            return authenticatedUserDto;
        }
    }

    // TODO: change ROLE.CODE field size and remove this conversion, store long code
    private String convertToOutputRoleName(String roleName){
        switch(roleName){
            case "ADMIN":
                return "ROLE_ADMIN";
            case "NEWS_EDIT":
                return "ROLE_NEWS_EDITOR";
            case "HF_EDIT":
                return "ROLE_HEDGE_FUND_EDITOR";
            case "HF_VIEW":
                return "ROLE_HEDGE_FUND_VIEWER";
            case "PE_EDIT":
                return "ROLE_PRIVATE_EQUITY_EDITOR";
            case "PE_VIEW":
                return "ROLE_PRIVATE_EQUITY_VIEWER";
            case "RE_EDIT":
                return "ROLE_REAL_ESTATE_EDITOR";
            case "RE_VIEW":
                return "ROLE_REAL_ESTATE_VIEWER";
            default:
                return null;
        }
    }
}
