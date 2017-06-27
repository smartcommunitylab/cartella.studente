package it.smartcommunitylab.csengine.config;

import it.smartcommunitylab.csengine.security.CustomAuthenticationProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationProvider customAuthenticationProvider;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(customAuthenticationProvider);
	}
	
	@Bean
	AuthenticationProvider getAuthenticationProvider() {
		return new CustomAuthenticationProvider();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.headers()
			.frameOptions().disable();
		http
      .csrf()
			.disable()
			.authorizeRequests()
			.antMatchers("/console/**", "/upload/**")
			.authenticated()
			.anyRequest()
			.permitAll();
		http.formLogin().loginPage("/login").permitAll()
		.and().logout().permitAll();
	}
}
