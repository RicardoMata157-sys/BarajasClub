package com.mx.web.bajarasClub.config;


import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

//	@Bean
//	public PasswordEncoder passwordEncoder() {
//	  return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
//	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	  String idForEncode = "bcrypt";
	  Map<String, PasswordEncoder> encoders = new HashMap<>();
	  encoders.put("bcrypt", new BCryptPasswordEncoder());
	  encoders.put("noop", NoOpPasswordEncoder.getInstance());
	  return new DelegatingPasswordEncoder(idForEncode, encoders);
	}
//
//    // Usuarios en memoria (cámbialo luego por JDBC/JPA si quieres)
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
//        var admin = User.withUsername("admin")
//                .password(encoder.encode("admin123"))
//                .roles("ADMIN")
//                .build();
//
//        var user = User.withUsername("user")
//                .password(encoder.encode("user123"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }
//    
	
	@Bean
	  public JdbcUserDetailsManager userDetailsService(DataSource dataSource) {
	    JdbcUserDetailsManager mgr = new JdbcUserDetailsManager(dataSource);
	    // Usa el esquema por defecto de Spring Security, o personaliza aquí:
	    mgr.setUsersByUsernameQuery(
	        "select username, case when password like '{%' then password else '{noop}' || password end as password,  enabled  from users where username=?");
	    mgr.setAuthoritiesByUsernameQuery(
	        "select username, 'ROLE_' || rol_sistema as authority  from users where username=?");
	    return mgr;
	  }
  
	
	
	 @Bean
	  public UserDetailsPasswordService userDetailsPasswordService(DataSource dataSource,
	                                                               UserDetailsService uds) {
	    JdbcTemplate jdbc = new JdbcTemplate(dataSource);
	    return new UserDetailsPasswordService() {
	      @Override
	      public UserDetails updatePassword(UserDetails user, String newPassword) {
	        // newPassword ya viene encriptado por el PasswordEncoder
	        jdbc.update("update users set password=? where username=?", newPassword, user.getUsername());
	        return uds.loadUserByUsername(user.getUsername());
	      }
	    };
	  }
	
	
	
	 @Bean
	  public DaoAuthenticationProvider authenticationProvider(UserDetailsService uds,
	                                                          PasswordEncoder encoder,
	                                                          UserDetailsPasswordService pwdService) {
	    DaoAuthenticationProvider p = new DaoAuthenticationProvider();
	    p.setUserDetailsService(uds);
	    p.setPasswordEncoder(encoder);
	    p.setUserDetailsPasswordService(pwdService); // ✅ ahora sí, con un bean que implementa la interfaz
	    return p;
	  }
    
 

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	
    	
        http
            // Autorización por rutas
            .authorizeHttpRequests(auth -> auth
            		  .antMatchers("/error").permitAll()
            	      .antMatchers("/uploads/**","/css/**","/js/**","/img/**","/webjars/**","/detalle/**","/confirmar").permitAll()
            	      .antMatchers(HttpMethod.GET, "/").permitAll()
            	      .antMatchers(HttpMethod.GET, "/consulta_boleto").permitAll()
            	      .antMatchers(HttpMethod.GET, "/consulta").permitAll()
            	      .antMatchers(HttpMethod.GET, "/confirmar").permitAll()
            	      .antMatchers(HttpMethod.GET, "/detalle/**").permitAll()
            	
            	      .antMatchers(HttpMethod.POST, "/detalle/**/seleccion", "/detalle/**/limpiar-auto").permitAll()
            	      .antMatchers(HttpMethod.GET, "/detalle/**/limpiar-auto").permitAll()
            	      .antMatchers(HttpMethod.GET, "/admin/rifa/**/seleccion").permitAll()
            	      .antMatchers(HttpMethod.POST, "/admin/rifas").hasRole("ADMIN")
            	      .antMatchers(HttpMethod.GET, "/admin/rifas/**").hasRole("ADMIN")
            	      .antMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
            	      .anyRequest().authenticated()
            )

            // Form login personalizado
            .formLogin(form -> form
                .loginPage("/login")                 // GET para mostrar el form
                .loginProcessingUrl("/login")        // POST del form (no necesitas controlador para el POST)
                .defaultSuccessUrl("/admin/rifas/nueva", true)   // adonde llega luego de login correcto
                .failureUrl("/login?error")          // si falla
                .permitAll()
            )

            // Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // Remember-me (opcional)
            .rememberMe(Customizer.withDefaults())

            // CSRF habilitado por defecto (bien para formularios)
            
            ;
        
        http.csrf(csrf -> csrf
        		 .ignoringAntMatchers("/detalle/**/seleccion",
        		          "/detalle/**/auto-numeros",
        		          "/detalle/**/limpiar-auto",
        		          "/detalle/**/numeros-simple",
        		          "/detalle/**/enviar-sms",
        		          "/detalle/**/enviar-whatsapp")
        		.ignoringRequestMatchers(
        		        new AntPathRequestMatcher("/admin/rifa/**/limpiar-auto", "POST"),
        		        new AntPathRequestMatcher("/detalle/enviar-whatsapp", "POST"),
        		        new AntPathRequestMatcher("/admin/rifa/**/seleccion", "POST"),
        		        new AntPathRequestMatcher("/detalle/confirmar", "POST"),
        		        new AntPathRequestMatcher("/detalle/{id}", "GET")
        		    )
        		  .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//        		  .ignoringRequestMatchers(new AntPathRequestMatcher("/admin/rifa/**/limpiar-auto", "POST"))
//        		  .ignoringRequestMatchers(new AntPathRequestMatcher("/admin/rifa/**/seleccion", "POST"))
        		);

        return http.build();
    }
}