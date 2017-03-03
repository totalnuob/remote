package kz.nicnbk.ws.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by magzumov on 22.02.2017.
 */
public class TokenAuthentication extends AbstractAuthenticationToken {

    private String token;

    public TokenAuthentication(String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
    }


    @Override
    public Object getDetails(){
        return this.token;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        if (isAuthenticated) {
            throw new IllegalArgumentException("illegal argument: cannot set authenticated");
        }
        super.setAuthenticated(false);
    }
}
