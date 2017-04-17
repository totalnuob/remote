package kz.nicnbk.ws.authentication;

import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.dto.authentication.TokenUserInfo;
import kz.nicnbk.ws.security.TokenAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by magzumov on 21.02.2017.
 */

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private TokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TokenAuthentication authenticationToken = (TokenAuthentication) authentication;
        String token = (String) authenticationToken.getDetails();
        if (StringUtils.isEmpty(token)) {
            // TODO: don't throw exception, log and return?
            throw new BadCredentialsException("Invalid token");
        }

        // verify token
        boolean verified = tokenService.verify(token);
        if(!verified){
            // TODO: don't throw exception, log and return???
            throw new BadCredentialsException("Invalid token or token expired");
        }

        // decode token and extract roles
        TokenUserInfo userInfo = this.tokenService.decode(token);
        Set<SimpleGrantedAuthority> roles = new HashSet<>();
        if(userInfo.getRoles() != null){
            for(String role: userInfo.getRoles()){
                if(role != null) {
                    roles.add(new SimpleGrantedAuthority(role));
                }
            }
        }

        // check revoked token
        Date revocationDate = this.tokenService.checkRevocationUsername(userInfo.getUsername());
        if(revocationDate != null && userInfo.getIssuedAt().compareTo(revocationDate) < 0){
            throw new BadCredentialsException("Invalid token: revoked");
        }

        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(), roles);
    }

    @Override
    public boolean supports(Class<?> authenticationClass) {
        return TokenAuthentication.class.isAssignableFrom(authenticationClass);
    }
}
