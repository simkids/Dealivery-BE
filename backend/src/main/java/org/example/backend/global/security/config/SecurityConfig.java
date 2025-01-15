package org.example.backend.global.security.config;


import lombok.RequiredArgsConstructor;
import org.example.backend.global.security.custom.service.OAuth2Service;
import org.example.backend.global.security.filter.JwtFilter;
import org.example.backend.global.security.filter.LoginFilter;
import org.example.backend.global.security.handler.AccessDeniedHandler;
import org.example.backend.global.security.handler.CustomLogoutSuccessHandler;
import org.example.backend.global.security.handler.LoginFailureHandler;
import org.example.backend.global.security.handler.OAuth2AuthenticationSuccessHandler;
import org.example.backend.global.security.jwt.JwtUtil;
import org.example.backend.global.security.jwt.repository.CompanyRefreshTokenRepository;
import org.example.backend.global.security.jwt.repository.UserRefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final CompanyRefreshTokenRepository companyRefreshTokenRepository;
    private final AccessDeniedHandler accessDeniedHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final OAuth2Service oAuth2Service;


    @Value("${domain}")
    private String domain;
  
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:8081"); // 허용할 출처
        config.addAllowedOrigin("http://localhost:3000"); // 허용할 출처
        config.addAllowedOrigin(domain); // 허용할 출처
        config.addAllowedOrigin(domain + "/api"); // 허용할 출처
        config.addAllowedOriginPattern("*"); // 허용할 출처
        config.addAllowedMethod("*"); // 허용할 메서드 (GET, POST, PUT 등)
        config.addAllowedHeader("*"); // 허용할 헤더
        config.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((auth) -> auth.disable());
        http.httpBasic((auth) -> auth.disable());
        http.sessionManagement((auth) -> auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin((auth) -> auth.disable());
        http.authorizeHttpRequests((auth) ->
                auth
                        .requestMatchers("/user/**").permitAll() // 모든 사람 접속 가능
                        .requestMatchers("/v2/api-dosc", "/swagger-resources/**", "/swagger-ui.html","/webjars/**","/swagger-ui/**" ).permitAll()
                        .anyRequest().permitAll()

        );
        //로그아웃 처리
        http.logout(logout -> {
            logout.logoutUrl("/logout");
            logout.logoutSuccessHandler(customLogoutSuccessHandler);
            logout.deleteCookies("JSESSIONID","AToken","RToken","type");
            logout.invalidateHttpSession(true);
            logout.permitAll();
        });

        http.oauth2Login(
            oauth2Login -> oauth2Login.authorizationEndpoint(
                authorizationEndpointConfig -> authorizationEndpointConfig
                    .baseUri("/oauth/oauth2/authorization")
                )
                .redirectionEndpoint(
                    redirectionEndpointConfig -> redirectionEndpointConfig
                        .baseUri(("/oauth/login/oauth2/code/*"))
                )
            );

        //필터생성 및 설정추가
        LoginFilter loginFilter = new LoginFilter(jwtUtil, authenticationManager(authenticationConfiguration)
                ,companyRefreshTokenRepository,userRefreshTokenRepository);
        loginFilter.setFilterProcessesUrl("/login");
        loginFilter.setAuthenticationFailureHandler(loginFailureHandler);

        //예외처리 핸들러 추가
        http.exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedHandler(accessDeniedHandler));

        //OAuth관련 설정 추가
        http.oauth2Login((config) -> {
            config.successHandler(oAuth2AuthenticationSuccessHandler);
            config.userInfoEndpoint((endPoint) -> endPoint.userService(oAuth2Service));
        });

        http.addFilterBefore(new JwtFilter(jwtUtil, companyRefreshTokenRepository, userRefreshTokenRepository), LoginFilter.class);
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
