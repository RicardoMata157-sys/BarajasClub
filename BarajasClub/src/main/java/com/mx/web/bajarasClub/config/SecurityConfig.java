package com.mx.web.bajarasClub.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // {bcrypt}
    }

    // Usuarios en memoria (cámbialo luego por JDBC/JPA si quieres)
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        var user = User.withUsername("user")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
    
  

    
 

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Autorización por rutas
            .authorizeHttpRequests(auth -> auth
            		  .antMatchers("/error").permitAll()
            	      .antMatchers("/uploads/**","/css/**","/js/**","/img/**","/webjars/**").permitAll()
            	      .antMatchers(HttpMethod.GET, "/").permitAll()
            	      .antMatchers(HttpMethod.POST, "/admin/rifas").hasRole("ADMIN")
            	      .antMatchers(HttpMethod.GET, "/admin/rifas/**").hasRole("ADMIN")
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

        return http.build();
    }
}