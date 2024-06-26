package taskflower.taskflower.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import taskflower.taskflower.security.filter.TokenFilter;
import taskflower.taskflower.security.service.CustomOAuth2UserService;
import taskflower.taskflower.security.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import taskflower.taskflower.security.handler.OAuth2AuthenticationFailureHandler;
import taskflower.taskflower.security.handler.OAuth2AuthenticationSuccessHandler;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, proxyTargetClass = true, jsr250Enabled = true)
public class SecurityConfig {
    private final TokenFilter tokenFilter;
    private final CustomOAuth2UserService customOauth2UserService;
    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOauth2AuthorizationRequestRepository;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;
    @Value("${app.api-base-url}")
    private String apiBaseUrl;

    @Autowired
    public SecurityConfig(TokenFilter tokenFilter, CustomOAuth2UserService customOauth2UserService, OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler, OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler, HttpCookieOAuth2AuthorizationRequestRepository httpCookieOauth2AuthorizationRequestRepository) {
        this.tokenFilter = tokenFilter;
        this.customOauth2UserService = customOauth2UserService;
        this.oauth2AuthenticationSuccessHandler = oauth2AuthenticationSuccessHandler;
        this.oauth2AuthenticationFailureHandler = oauth2AuthenticationFailureHandler;
        this.httpCookieOauth2AuthorizationRequestRepository = httpCookieOauth2AuthorizationRequestRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                http 기본 인증 disable
                .httpBasic(HttpBasicConfigurer::disable)
//                security 제공 기본 로그인 페이지 disable
                .formLogin(FormLoginConfigurer::disable)
//                Post 요청 및 resource 허용 설정
                .csrf(CsrfConfigurer::disable)
//                JWT 사용 -> Session 필요 없음
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
//                Cors 문제 해결
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource()));

        http
//                url 권한 매칭 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(antMatcher(apiBaseUrl + "/auth/**")).permitAll()
                        .requestMatchers(antMatcher("/oauth2/**")).permitAll()
                        .requestMatchers(antMatcher(apiBaseUrl + "/user/**")).hasRole("USER")
                        .requestMatchers(antMatcher(apiBaseUrl + "/google/**")).hasRole("USER")
                        .requestMatchers(antMatcher(apiBaseUrl + "/task/**")).permitAll()
                        .anyRequest().authenticated());

        http
//                oauth2 login
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint(endPoint -> endPoint
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(httpCookieOauth2AuthorizationRequestRepository))
                        .redirectionEndpoint(endPoint -> endPoint
                                .baseUri("/oauth2/code/**"))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOauth2UserService))
                        .successHandler(oauth2AuthenticationSuccessHandler)
                        .failureHandler(oauth2AuthenticationFailureHandler)
                )
//                jwt token filter 추가
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name()
        ));
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.AUTHORIZATION
        ));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
