package com.zmk.security.test.security;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.zmk.security.test.service.CustomeAuthenticationProvider;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	CustomeAuthenticationProvider customeAuthenticationProvider;
    @Autowired
    PasswordEncoder passwordEncoder;
//
//	@Bean
//    public UserDetailsService userDetailsService() {
////		List<UserDetails> userDetailsList = new ArrayList<>();
////		userDetailsList.add(User.withUsername("u1").password(passwordEncoder.encode("u1"))
////				.roles("EMPLOYEE", "USER").build());
////		userDetailsList.add(User.withUsername("m1").password(passwordEncoder.encode("m1"))
////				.roles("MANAGER1", "USER").build());
////		userDetailsList.add(User.withUsername("a").password(passwordEncoder.encode("a"))
////				.roles("ADMIN", "USER").build());
////
////		return new InMemoryUserDetailsManager(userDetailsList);
//		 return new UserDetailsServiceImpl();
//    }
//
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService());
//        authProvider.setPasswordEncoder(passwordEncoder);
//        return authProvider;
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
    	try {
			auth.inMemoryAuthentication()
			.withUser("ad")
//			.password("{noop}ad")
			.password(passwordEncoder.encode("ad"))
			.roles("ADMIN", "USER");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        auth.authenticationProvider(authenticationProvider());
    	auth.authenticationProvider(customeAuthenticationProvider);
        
    }
	
 
	@Autowired
    DataSource dataSource;
	

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .passwordEncoder(passwordEncoder)
//                .usersByUsernameQuery("select username1, password1, enabled from users where username1 = ?")
//                .authoritiesByUsernameQuery("select *  from authorities where username1 = ?");
//    }
    
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private LogoutHandler logoutHandler;
    
    

    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        .antMatchers("/login1")
            .permitAll()
        .antMatchers("/home**","/hello")
            .hasAuthority("USER")//hasAnyRole => must add ROLE_ before USER -> become hasAnyRole("ROLE_USER") | hasAuthority -> don't need add prefix ROLE_
        .antMatchers("/admin1**")
            .hasAuthority("ADMIN1")
        .antMatchers("/admin2**")
            .hasAuthority("ADMIN2")
        .antMatchers("/manager1**")
            .hasAuthority("MANAGER1")
        .antMatchers("/manager2**")
            .hasAuthority("MANAGER2")
        .antMatchers("/user**")
            .hasAuthority("USER")
        .antMatchers("/admin**")
            .hasAuthority("ADMIN")// admin them tai vi tri theo thu tu nay, neu vi tri khac se overlap voi /admin1** + /admin2**
        .and()
            .formLogin().usernameParameter("username1").passwordParameter("password1")// parameters name at view login2.html
            .loginPage("/login1")
            .loginProcessingUrl("/login_actionview_processing")// action at view login2.html
            .successHandler(new AuthenticationSuccessHandler() {
                @Override
                public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
                    redirectStrategy.sendRedirect(request, response, "/home");
                }
            })
            //.defaultSuccessUrl("/home")// login thanh cong se chuyen vao day
            .failureUrl("/login1?error=true")
            .permitAll()
        .and()
            .logout()
            .logoutUrl("/logout_app1")// if want logout -> run url: localhost:port/logout_app1
            //.logoutSuccessUrl("/login1?logout=true")
            .addLogoutHandler(logoutHandler)
            //.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
            .invalidateHttpSession(true)
            .permitAll()
        .and()
        	.exceptionHandling().accessDeniedPage("/403")
        .and()
            .csrf()
            .disable();
    }
}