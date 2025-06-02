package org.vrk.accounting.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableMethodSecurity  // активирует @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .authorizeHttpRequests(auth -> auth
                        // публичные эндпоинты
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()   // публично
                        .requestMatchers("/**").permitAll()
//                        .anyRequest().authenticated()
                        // всё остальное — только для аутентифицированных
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> auths = new ArrayList<>();

            // 1) Realm-роли
            Map<String,Object> realm = jwt.getClaim("realm_access");
            if (realm != null && realm.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realm.get("roles");
                roles.forEach(r -> auths.add(new SimpleGrantedAuthority("ROLE_" + r)));
            }

            // 2) Client-роли (если нужны)
            Map<String,Object> resource = jwt.getClaim("resource_access");
            if (resource != null && resource.containsKey("item-service")) {
                @SuppressWarnings("unchecked")
                Map<String,Object> client = (Map<String,Object>) resource.get("item-service");
                @SuppressWarnings("unchecked")
                List<String> clientRoles = (List<String>) client.get("roles");
                clientRoles.forEach(r -> auths.add(new SimpleGrantedAuthority("ROLE_" + r)));
            }

            return auths;
        });

        return converter;
    }

}
