package travel.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Бін для кодування паролів.
     * Ви просили Base64.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return Base64.getEncoder().encodeToString(rawPassword.toString().getBytes());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                String encodedRawPassword = Base64.getEncoder().encodeToString(rawPassword.toString().getBytes());
                return encodedRawPassword.equals(encodedPassword);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Публічний доступ (статичні файли, логін, помилки)
                .requestMatchers(
                    "/", 
                    "/login", 
                    "/error", 
                    "/error/403", 
                    "/css/**", 
                    "/js/**",
                    "/images/**"
                ).permitAll()

                // Будь-яка адреса, що починається з "/admin/", доступна ТІЛЬКИ адміну.
                // Якщо User спробує зайти сюди вручну -> отримає 403.
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                // Доступ для ADMIN до SQL виконавця
                .requestMatchers("/sql", "/execute-sql").hasAuthority("ROLE_ADMIN")
                
                // Доступ для USER та ADMIN до сторінок перегляду
                .requestMatchers("/countries", "/offers", "/travel-types").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                // API
                .requestMatchers("/api/v1/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                // Всі інші запити вимагають автентифікації
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            
            // Коли Spring бачить порушення прав (User лізе в /admin),
            // він перенаправляє на нашу сторінку "/error/403"
            .exceptionHandling(e -> e.accessDeniedPage("/error/403"));

        return http.build();
    }
}