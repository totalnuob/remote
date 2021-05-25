package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.AuthenticationService;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.dto.authentication.AuthenticatedUserDto;
import kz.nicnbk.service.dto.authentication.TokenUserInfo;
import kz.nicnbk.service.dto.authentication.UserCredentialsDto;
import kz.nicnbk.service.dto.employee.UserPasswordDto;
import kz.nicnbk.service.dto.employee.UserTokenDto;
import kz.nicnbk.ws.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by magzumov on 21.02.2017.
 */

@RestController
public class AuthenticationServiceREST {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceREST.class);

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TokenService tokenService;

    @RequestMapping(value="/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> authenticate(@RequestBody UserCredentialsDto credentials, HttpServletRequest req, HttpServletResponse response){

        // authenticate
        AuthenticatedUserDto userDto = authenticationService.authenticate(credentials.getUsername(), credentials.getPassword(), credentials.getOtp());
        if(userDto == null){
            // invalid credentials
            response.addCookie(getClearTokenCookie());
            return new ResponseEntity<>(null, null, HttpStatus.UNAUTHORIZED);
        }

        // create token
        String token = this.tokenService.create(userDto);
        response.addCookie(getTokenCookie(token));
        return new ResponseEntity<>(userDto, null, HttpStatus.OK);
    }

    @RequestMapping(value="/signout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        response.addCookie(getClearTokenCookie());
        return new ResponseEntity<>(new Response(true, null), null, HttpStatus.OK);
    }

    @RequestMapping(value="/requestReset", method = RequestMethod.POST)
    public ResponseEntity<?> reset(@RequestBody String emailAddress, HttpServletRequest request, HttpServletResponse response){
        String email = emailAddress.substring(1, emailAddress.length()-1);
        // verify
        AuthenticatedUserDto userDto = authenticationService.verifyForReset(email);
        if(userDto == null){
            // invalid credentials
            response.addCookie(getClearTokenCookie());
            return new ResponseEntity<>(null, null, HttpStatus.UNAUTHORIZED);
        }

        // create token
        String token = this.tokenService.createForReset(userDto);
        response.addCookie(getTokenCookie(token));
        authenticationService.sendHtmlResetLink(email, userDto.getUsername(), token);
        return new ResponseEntity<>(true, null, HttpStatus.OK);
    }

    @RequestMapping(value = "/confirmReset", method = RequestMethod.POST)
    public ResponseEntity<?> confirmReset(@RequestBody UserTokenDto userTokenDto, HttpServletRequest request, HttpServletResponse response) {

        logger.info(userTokenDto.getUsername());
        logger.info(userTokenDto.getToken());
        boolean isValid = this.employeeService.checkResetToken(userTokenDto.getUsername(), userTokenDto.getToken());
        return new ResponseEntity<>(isValid, null, HttpStatus.OK);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity<?> changePassword(@RequestBody UserPasswordDto userPasswordDto, HttpServletRequest request, HttpServletResponse response) {
        boolean isReset = this.employeeService.setPassword(userPasswordDto.getUsername(), userPasswordDto.getNewPassword(), userPasswordDto.getUsername());
        return new ResponseEntity<>(isReset, null, HttpStatus.OK);
    }


    @RequestMapping(value="/checkToken", method = RequestMethod.POST)
    public ResponseEntity<?> checkToken(){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        TokenUserInfo tokenUserInfo = this.tokenService.decode(token);
        if(tokenUserInfo != null){
            return new ResponseEntity<>(true, null, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(false, null, HttpStatus.OK);
        }
    }

    private Cookie getClearTokenCookie(){
        Cookie cookie = getTokenCookie(null);
        cookie.setMaxAge(0);
        return cookie;
    }

    private Cookie getTokenCookie(String value){
        Cookie cookie = new Cookie("token", value);
        //cookie.setPath(?);
        cookie.setMaxAge(-1);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
