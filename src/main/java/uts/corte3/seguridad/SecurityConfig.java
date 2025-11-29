package uts.corte3.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final MongoUserDetailsService userDetails;

    // Constructor manual
    public SecurityConfig(MongoUserDetailsService userDetails) {
        this.userDetails = userDetails;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetails);
        p.setPasswordEncoder(encoder);
        return p;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // ---------------------------------------
                // RECURSOS Y RUTAS PÚBLICAS
                // ---------------------------------------
                .requestMatchers(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/login",
                        "/error",
                        "/registro/profesional"     // ⭐ NUEVO: acceso público al registro profesional
                ).permitAll()

                // ---------------------------------------
                // API REST (ya lo tenías)
                // ---------------------------------------
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/api/stai/**").hasAnyRole(
                        "MODERADOR", "EVALUADOR", "PROFESIONAL", "MIEMBRO", "ADMIN")

                // ---------------------------------------
                // ZONA ADMIN
                // ---------------------------------------
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ---------------------------------------
                // ZONA MODERADOR
                // ---------------------------------------
                .requestMatchers("/moderador/**").hasRole("MODERADOR")

                // ---------------------------------------
                // ZONA PROFESIONAL
                // ---------------------------------------
                .requestMatchers("/profesional/**").hasRole("PROFESIONAL")

                // ---------------------------------------
                // ZONA ME (usuario general)
                // ---------------------------------------
                .requestMatchers("/evaluador/**").hasRole("EVALUADOR")

                .requestMatchers("/me/**").hasAnyRole(
                        "MODERADOR", "EVALUADOR", "PROFESIONAL", "MIEMBRO", "ADMIN")

                // ---------------------------------------
                // RESTO DE RUTAS → requieren autenticación
                // ---------------------------------------
                .anyRequest().authenticated()
            )

            // ---------------------------------------
            // FORM LOGIN
            // ---------------------------------------
            .formLogin(form -> form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/post-login", true)
            )

            // ---------------------------------------
            // LOGOUT
            // ---------------------------------------
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )

            // Para herramientas API
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
