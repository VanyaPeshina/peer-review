package com.telerikacademy.finalprojectpeerreview.security;

import com.telerikacademy.finalprojectpeerreview.auth.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;

import static com.telerikacademy.finalprojectpeerreview.security.enums.ApplicationUserRole.SIMPLE_USER;
import static com.telerikacademy.finalprojectpeerreview.security.enums.ApplicationUserRole.ADMIN;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserService applicationUserService) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                //handle roles and authentication
                .authorizeRequests()
             /*   .anyRequest().permitAll()*/
                .antMatchers("/fonts/**", "/css/**", "/dashboard/**", "/js/**", "/static/**", "/images/**").permitAll()
                .antMatchers("/", "/login", "/register", "/dashboard").permitAll()
                .antMatchers("/api").permitAll()   //hasAnyRole(ADMIN.name(), SIMPLE_USER.name())
                .antMatchers("management/api/**", "/management/**").hasRole(ADMIN.name())
                .anyRequest()
                .authenticated()

                .and()
                //handle login
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .passwordParameter("password")
                .usernameParameter("username")

                .and()
                //remember me
                .rememberMe()
                .tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(21))
                .key("somethingverysecured")
                .rememberMeParameter("remember-me")

                .and()
                //handle logout
                .logout()
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) //leave only if csrf is disabled
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("remember-me")
                .logoutSuccessUrl("/"); //defaults to 2 weeks
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder); //for passwords to be decoded
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }
}
