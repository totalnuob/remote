package kz.nicnbk.ws.rest;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.repo.model.employee.Employee;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.dto.authentication.ChangePasswordCredentialsDto;
import kz.nicnbk.service.dto.authentication.TokenUserInfo;
import kz.nicnbk.service.dto.authentication.UserCredentialsDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import kz.nicnbk.service.dto.employee.EmployeePagedSearchResult;
import kz.nicnbk.service.dto.employee.EmployeeSearchParamsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by magzumov on 02.08.2016.
 */


@RestController
@RequestMapping("/employee")
public class EmployeeServiceREST extends CommonServiceREST{

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TokenService tokenService;

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity findAll(){
        List<EmployeeDto> employees = this.employeeService.findAll();
        if(employees == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employees, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/get/{employeeId}", method = RequestMethod.GET)
    public ResponseEntity getEmployeeById(@PathVariable Long employeeId){
        EmployeeDto employeeDto = this.employeeService.getEmployeeById(employeeId);
        if(employeeDto == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employeeDto, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/getByUsername/{username}", method = RequestMethod.GET)
    public ResponseEntity getEmployeeByUsername(@PathVariable String username){
        EmployeeDto employeeDto = this.employeeService.getEmployeeByUsername(username);
        if(employeeDto == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employeeDto, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity<?> search(@RequestBody EmployeeSearchParamsDto searchParams) {
        EmployeePagedSearchResult searchResult = this.employeeService.search(searchParams);
        return buildNonNullResponse(searchResult);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody EmployeeDto employeeDto) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponseDto = this.employeeService.save(employeeDto, username);
        return buildEntitySaveResponse(saveResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity changeUserPassword(@RequestBody UserCredentialsDto credentials){
        if(credentials == null || StringUtils.isEmpty(credentials.getUsername())) {
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        }
        // get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenUserInfo tokenUserInfo = this.tokenService.decode((String)authentication.getDetails());
        if(tokenUserInfo == null || StringUtils.isEmpty(tokenUserInfo.getUsername())){
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        }

        boolean changed = this.employeeService.setPassword(credentials.getUsername(), credentials.getPassword(), tokenUserInfo.getUsername());
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
        boolean changed = this.employeeService.setPassword(tokenUserInfo.getUsername(), credentials.getNewPassword(), tokenUserInfo.getUsername());
        if(changed){
            // revoke username
            this.tokenService.revokeUsername(tokenUserInfo.getUsername());
            return new ResponseEntity<>(changed, null, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/deactivateUser", method = RequestMethod.POST)
    public ResponseEntity<?> deactivate(@RequestBody UserCredentialsDto credentials){
        if(credentials != null && credentials.getUsername() != null) {
            boolean deactivated = this.employeeService.deactivate(credentials.getUsername());
            if(deactivated){
                // revoke username token
                this.tokenService.revokeUsername(credentials.getUsername());
                return new ResponseEntity<>(null, null, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/activateUser", method = RequestMethod.POST)
    public ResponseEntity<?> activate(@RequestBody UserCredentialsDto credentials){
        if(credentials != null && credentials.getUsername() != null) {
            boolean activated = this.employeeService.activate(credentials.getUsername());
            if(activated) {
                return new ResponseEntity<>(null, null, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
    }
}
