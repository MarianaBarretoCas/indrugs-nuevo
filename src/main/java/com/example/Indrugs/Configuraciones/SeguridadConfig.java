package com.example.Indrugs.Configuraciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasAuthority("Administrador")
                        .requestMatchers("/paciente/**").hasAuthority("Paciente")
                        .requestMatchers("/domi/**").hasAuthority("Domiciliario")
                        .requestMatchers("/login", "/", "/error/**", "/publicas/**", "/registrarse", "/css/**", "/iconos/**", "/JAVA_SCRIPT/**", "/imagenes/**").permitAll()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form.disable())

                .logout(logout -> logout.disable())

                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // El  12 son las veces que se hace el hash, o sea la fuerza de la seguridad
        return new BCryptPasswordEncoder(12);
    }


}

