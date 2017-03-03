package kz.nicnbk.ws.rest;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.dto.authentication.ChangePasswordCredentialsDto;
import kz.nicnbk.service.dto.authentication.TokenUserInfo;
import kz.nicnbk.service.dto.authentication.UserCredentialsDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    private TokenService tokenService;

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public List<EmployeeDto> findAll(){
        List<EmployeeDto> employees = this.employeeService.findAll();
        return employees;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity changeUserPassword(@RequestBody UserCredentialsDto credentials){
        if(credentials == null || StringUtils.isEmpty(credentials.getUsername())) {
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        }
        boolean changed = this.employeeService.setPassword(credentials.getUsername(), credentials.getPassword());
        // TODO: revoke??
        this.tokenService.revokeUsername(credentials.getUsername());

        return new ResponseEntity<>(changed, null, HttpStatus.OK);

    }

    @RequestMapping(value = "/changeSelfPassword", method = RequestMethod.POST)
    public ResponseEntity changeSelfPassword(@RequestBody ChangePasswordCredentialsDto credentials){

        // check request
        if(credentials == null || StringUtils.isEmpty(credentials.getCurrentPassword()) ||
                StringUtils.isEmpty(credentials.getNewPassword())) {
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        }

        // get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenUserInfo tokenUserInfo = this.tokenService.decode((String)authentication.getDetails());
        if(tokenUserInfo == null || StringUtils.isEmpty(tokenUserInfo.getUsername())){
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        }

        // check user current password
        EmployeeDto employeeDto = this.employeeService.findActiveByUsernamePassword(tokenUserInfo.getUsername(), credentials.getCurrentPassword());
        if(employeeDto == null){
            return new ResponseEntity<>(null, null, HttpStatus.UNAUTHORIZED);
        }

        // change password
        boolean changed = this.employeeService.setPassword(tokenUserInfo.getUsername(), credentials.getNewPassword());

        // TODO: revoke??
        this.tokenService.revokeUsername(tokenUserInfo.getUsername());

        return new ResponseEntity<>(changed, null, HttpStatus.OK);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/deactivateUser", method = RequestMethod.POST)
    public ResponseEntity<?> deactivate(@RequestBody UserCredentialsDto credentials){
        if(credentials != null && credentials.getUsername() != null) {
            boolean deactivated = this.employeeService.deactivate(credentials.getUsername());

            // revoke username token
            this.tokenService.revokeUsername(credentials.getUsername());
            return new ResponseEntity<>(null, null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/activateUser", method = RequestMethod.POST)
    public ResponseEntity<?> activate(@RequestBody UserCredentialsDto credentials){
        if(credentials != null && credentials.getUsername() != null) {
            boolean activated = this.employeeService.activate(credentials.getUsername());
            return new ResponseEntity<>(null, null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
    }
}
