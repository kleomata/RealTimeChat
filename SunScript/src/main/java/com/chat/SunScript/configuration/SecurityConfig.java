package com.chat.SunScript.configuration;

import com.chat.SunScript.repository.AdminRepository;
import com.chat.SunScript.service.custom.CustomAdminDetailsService;
import com.chat.SunScript.service.custom.CustomCombinedDetailService;
import com.chat.SunScript.service.custom.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private CustomAdminDetailsService customAdminDetailsService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AdminRepository adminRepository;

    // Password encode
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
       DaoAuthenticationProvider adminProvider = new DaoAuthenticationProvider();
       adminProvider.setUserDetailsService(customAdminDetailsService);
       adminProvider.setPasswordEncoder(passwordEncoder());

       DaoAuthenticationProvider userProvider = new DaoAuthenticationProvider();
       userProvider.setUserDetailsService(customUserDetailsService);
       userProvider.setPasswordEncoder(passwordEncoder());

        List<AuthenticationProvider> providers = Arrays.asList(adminProvider, userProvider);

        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                for (AuthenticationProvider provider : providers) {
                    Authentication auth = provider.authenticate(authentication);
                    if (auth != null) {
                        return auth;
                    }
                }
                return null;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return DaoAuthenticationProvider.class.isAssignableFrom(authentication);
            }
        };

    }

    // Authentication Manager setup
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .authenticationProvider(authenticationProvider());

        return authenticationManagerBuilder.build();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/chat/admin/create").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/chat/admin/login").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/chat/user/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/chat/user/login").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/chat/user/search/*").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/chat/user/profile").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/chat/user/profile/image/*").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/chat/user/background/image/*").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/chat/user/discriminator/*").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/chat/user/userInfo/*").hasRole("USER")


                        // Follow
                                .requestMatchers(HttpMethod.POST, "/api/chat/user/follow").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/api/chat/user/checkFollow/*").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/api/chat/user/checkFollowBack/*").hasRole("USER")
                                .requestMatchers(HttpMethod.DELETE, "/api/chat/user/unfollow").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/api/chat/user/countFollowers/*").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/api/chat/user/countFollowing/*").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/api/chat/user/allFollowing").hasRole("USER")
                                .requestMatchers(HttpMethod.GET,"/api/chat/user/status/*").hasRole("USER")
                                .requestMatchers(HttpMethod.GET,"/api/chat/user/userStatus/*").hasRole("USER")
                                .requestMatchers(HttpMethod.POST,"/api/chat/mediaUrls").hasRole("USER")
                                .requestMatchers(HttpMethod.GET,"/api/chat/mediaUrls/media/*").hasRole("USER")

                        .requestMatchers("/ws/**", "/topic/**", "/queue/**", "/user/**","/app/**", "/status/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/chat/messages/**").hasRole("USER")
                        ///////////////////////////////////////////////

                        .requestMatchers(HttpMethod.GET,"/api/userStatus/*").hasRole("USER")
                ).addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
