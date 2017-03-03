package kz.nicnbk.ws.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by magzumov on 22.02.2017.
 */
public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    public TokenAuthenticationFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, final FilterChain chain) throws IOException, ServletException {

        if((((HttpServletRequest)req).getRequestURI().equals("/authenticate") && ((HttpServletRequest)req).getMethod().equalsIgnoreCase("POST"))
                || ((HttpServletRequest)req).getMethod().equalsIgnoreCase("OPTIONS")) {
            chain.doFilter(req, res);
            return;
        }

        //On success keep going on the chain
        this.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                chain.doFilter(request, response);
            }
        });

        super.doFilter(req, res, chain);

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        String token = null;
        if(request.getCookies() != null){
            for(Cookie cookie: request.getCookies()){
                if(cookie.getName().equalsIgnoreCase("token")){
                    token = cookie.getValue();
                    break;
                }
            }
        }

        TokenAuthentication tokenAuthentication = new TokenAuthentication(token, null);
        tokenAuthentication.setDetails(authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(tokenAuthentication);
    }


}
