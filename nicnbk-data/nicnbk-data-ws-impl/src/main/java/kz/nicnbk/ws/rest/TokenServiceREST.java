package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.dto.authentication.UserCredentialsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by magzumov on 21.02.2017.
 */

@RestController
@RequestMapping("/token")
public class TokenServiceREST {

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/revoke", method = RequestMethod.POST)
    public ResponseEntity<?> revoke(@RequestBody UserCredentialsDto credentials){
        if(credentials != null && credentials.getUsername() != null) {
            this.tokenService.revokeUsername(credentials.getUsername());
            return new ResponseEntity<>(null, null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/cancelRevocation", method = RequestMethod.POST)
    public ResponseEntity<?> cancelRevocation(@RequestBody UserCredentialsDto credentials){
        if(credentials != null && credentials.getUsername() != null) {
            this.tokenService.cancelRevocationUsername(credentials.getUsername());
            return new ResponseEntity<>(null, null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
    }

}
