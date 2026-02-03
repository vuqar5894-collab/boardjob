package az.vugar.boardjob.config;

import az.vugar.boardjob.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomUserDetailsService customUserDetailsService;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/index", "/login", "/register", "/about",
                                                                "/blog", "/blog-single", "/contact",
                                                                "/services", "/service-single",
                                                                "/portfolio", "/portfolio-single",
                                                                "/testimonials", "/faq", "/gallery",
                                                                "/css/**", "/js/**", "/images/**", "/fonts/**",
                                                                "/job-listings", "/job-single",
                                                                "/jobs", "/jobs/**")
                                                .permitAll()
                                                .requestMatchers("/jobs/post").hasRole("EMPLOYER")
                                                .requestMatchers("/employer/**").hasRole("EMPLOYER")
                                                .requestMatchers("/candidate/**").hasRole("CANDIDATE")
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .userDetailsService(customUserDetailsService)
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/", true)
                                                .usernameParameter("email")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
