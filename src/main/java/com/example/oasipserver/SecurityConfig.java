package com.example.oasipserver;

import com.example.oasipserver.jwt.JwtAuthenticationEntryPoint;
import com.example.oasipserver.jwt.JwtRequestFilter;
import com.example.oasipserver.jwt.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtUserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(JwtUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint()).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/api/users/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users").hasRole("admin")
                .antMatchers(HttpMethod.GET, "/api/events").hasAnyRole("admin","student","lecturer")
                .antMatchers(HttpMethod.GET, "/api/events/{bookingId}").hasAnyRole("admin","student","lecturer")
                .antMatchers(HttpMethod.POST, "/api/events").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/events/{bookingId}").hasAnyRole("admin","student")
                .antMatchers(HttpMethod.DELETE, "/api/events/{bookingId}").hasAnyRole("admin","student")
                .antMatchers(HttpMethod.GET, "/api/upcoming").hasAnyRole("admin","student")
                .antMatchers(HttpMethod.GET, "/api/past").hasAnyRole("admin","student")
                .antMatchers(HttpMethod.GET, "/api/events/sort-date/{date}").hasAnyRole("admin","student")
                .antMatchers(HttpMethod.GET, "/api/categories").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/categories/{categoryId}").hasAnyRole("admin","lecturer")
                .antMatchers(HttpMethod.GET, "/api/users/refreshToken").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/checkUnique").permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}

