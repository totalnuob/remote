package kz.nicnbk.ws.rest;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.dto.authentication.ChangePasswordCredentialsDto;
import kz.nicnbk.service.dto.authentication.MfaRegistrationDto;
import kz.nicnbk.service.dto.authentication.TokenUserInfo;
import kz.nicnbk.service.dto.authentication.UserCredentialsDto;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.employee.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    @RequestMapping(value = "/findActiveAll", method = RequestMethod.GET)
    public ResponseEntity findActiveAll(){
        List<EmployeeDto> employees = this.employeeService.findActiveAll();
        if(employees == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employees, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/findICMembers", method = RequestMethod.GET)
    public ResponseEntity findICMembers(){
        List<EmployeeDto> employees = this.employeeService.findICMembers();
        if(employees == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employees, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/findMBMembers", method = RequestMethod.GET)
    public ResponseEntity findMBMembers(){
        List<EmployeeDto> employees = this.employeeService.findMBMembers();
        if(employees == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employees, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/findByDepartmentAndActive/{departmentId}", method = RequestMethod.GET)
    public ResponseEntity findByDepartmentAndActive(@PathVariable int departmentId){
        List<EmployeeDto> employees = this.employeeService.findByDepartmentAndActive(departmentId, true);
        if(employees == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employees, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/findExecutivesAndActive", method = RequestMethod.GET)
    public ResponseEntity findExecutivesAndActive(){
        List<EmployeeDto> employees = this.employeeService.findExecutivesAndActive();
        if(employees == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employees, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/findByDepartmentAndActiveWithExecutives/{departmentId}", method = RequestMethod.GET)
    public ResponseEntity findByDepartmentAndActiveWithExecutives(@PathVariable int departmentId){
        List<EmployeeDto> employees = this.employeeService.findByDepartmentAndActiveWithExecutives(departmentId);
        if(employees == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employees, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/findByDepartmentWithExecutives/{departmentId}", method = RequestMethod.GET)
    public ResponseEntity findByDepartmentWithExecutives(@PathVariable int departmentId){
        List<EmployeeDto> employees = this.employeeService.findByDepartmentWithExecutives(departmentId);
        if(employees == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employees, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/findUsersWithRole/{role}", method = RequestMethod.GET)
    public ResponseEntity findUsersWithRole(@PathVariable String role){
        List<EmployeeDto> employees = this.employeeService.findUsersWithRole(role, null);
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/getFullByUsername/{username}", method = RequestMethod.GET)
    public ResponseEntity getFullEmployeeByUsername(@PathVariable String username){
        EmployeeFullDto employeeFullDto = this.employeeService.getFullEmployeeByUsername(username);
        if(employeeFullDto == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(employeeFullDto, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity<?> search(@RequestBody EmployeeSearchParamsDto searchParams) {
        EmployeePagedSearchResult searchResult = this.employeeService.search(searchParams);
        return buildNonNullResponse(searchResult);
    }

    @PreAuthorize(" hasRole('ROLE_USER_PROFILE_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody EmployeeFullDto employeeFullDto) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponseDto = this.employeeService.save(employeeFullDto, username, false);
        return buildEntitySaveResponse(saveResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/saveAdmin", method = RequestMethod.POST)
    public ResponseEntity<?> saveAdmin(@RequestBody EmployeeFullDto employeeFullDto) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponseDto = this.employeeService.save(employeeFullDto, username, true);
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/saveAndChangePassword", method = RequestMethod.POST)
    public ResponseEntity<?> saveAndChangePassword(@RequestBody EmployeePasswordDto employeePasswordDto) {

        EmployeeFullDto employeeFullDto = employeePasswordDto.getEmployeeFullDto();
        String password = employeePasswordDto.getPassword();
        Boolean emailCheckbox = employeePasswordDto.getEmailCheckbox();

        if(StringUtils.isEmpty(password)) {
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        }

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        EntitySaveResponseDto saveResponseDto = this.employeeService.saveAndChangePassword(employeeFullDto, password, emailCheckbox, username);
        return buildEntitySaveResponse(saveResponseDto);
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

    @RequestMapping(value = "/getAllPositions", method = RequestMethod.GET)
    public ResponseEntity getAllPositions(){
        List<PositionDto> positions = this.employeeService.getAllPositions();
        if(positions == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(positions, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/getAllRoles", method = RequestMethod.GET)
    public ResponseEntity getAllRoles(){
        List<RoleDto> roles = this.employeeService.getAllRoles();
        if(roles == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            return new ResponseEntity<>(roles, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/getAllDepartments", method = RequestMethod.GET)
    public ResponseEntity getAllDepartments(){
        List<DepartmentDto> departments = this.employeeService.getAllDepartments();
        if(departments == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(departments, null, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/registerMfa", method = RequestMethod.POST)
    public ResponseEntity registerMfa(@RequestBody MfaRegistrationDto registrationDto){
        // get authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenUserInfo tokenUserInfo = this.tokenService.decode((String)authentication.getDetails());
        if(tokenUserInfo == null || StringUtils.isEmpty(tokenUserInfo.getUsername())){
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        }

        if (this.employeeService.registerMfa(tokenUserInfo.getUsername(), registrationDto.getSecret(), registrationDto.getOtp())) {
            return new ResponseEntity<>(123, null, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
    }
}
