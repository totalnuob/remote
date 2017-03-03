package kz.nicnbk.ws.security;

import kz.nicnbk.ws.authentication.TokenAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by magzumov on 21.02.2017.
 */

@Configuration
@EnableWebSecurity
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    protected AbstractAuthenticationProcessingFilter getTokenAuthenticationFilter() throws Exception {
        TokenAuthenticationFilter filter = new TokenAuthenticationFilter( new RegexRequestMatcher("^/.*", null));
        filter.setAuthenticationManager(this.authenticationManagerBean());
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.
                // TODO: enable csrf
                csrf().disable().

                sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().

                authorizeRequests().
                    antMatchers("/authenticate").permitAll().
                    antMatchers(HttpMethod.OPTIONS, "/**").permitAll().
                    anyRequest().authenticated().

                and().addFilterBefore(getTokenAuthenticationFilter(), AnonymousAuthenticationFilter.class).

                exceptionHandling().
                authenticationEntryPoint(unauthorizedEntryPoint()).

                and().httpBasic().disable();

       // http.addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);

    }

    //@Override
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.
                authenticationProvider(tokenAuthenticationProvider());
    }

    @Bean
    public AuthenticationProvider tokenAuthenticationProvider() {
        return new TokenAuthenticationProvider();
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        };
    }
}
