package com.bescalonadev.springboot.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bescalonadev.springboot.app.auth.handler.LoginSuccessHandler;
import com.bescalonadev.springboot.app.models.service.JpaUserDetailsService;

//Habilita el uso de la anotacion @Secured para restringir el accesso a paginas desde los controladores
@EnableGlobalMethodSecurity(securedEnabled=true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private LoginSuccessHandler successHandler;
	
	//Conexion a la BD
	@Autowired
	DataSource dataSource;
	
	//Ubicado en el paquete services
	@Autowired
	private JpaUserDetailsService userDetailsService; 
	
	//Encripta las claves, se exporta de MvcConfig
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/", "/css/**","/js/**","/images/**","/listar**","/locale","/api/clientes/**","/vendor/**","/fonts/**","/categoria/listar","/producto/listar").permitAll()
		.anyRequest().authenticated()
		.and()
			.formLogin()
			.successHandler(successHandler).loginPage("/login")
			.permitAll()
		.and()
		.logout().permitAll()
		.and()
		.exceptionHandling().accessDeniedPage("/error_403");
	}

	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder build) throws Exception{
		//Configuracion para JPA
		build.userDetailsService(userDetailsService)
		.passwordEncoder(passwordEncoder);
	}
}
