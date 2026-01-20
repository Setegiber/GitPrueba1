package es.mjusticia.pagojus.frontconsola;

/*-
 * #%L
 * pagojus-frontconsola
 * %%
 * Copyright (C) 2023 - 2024 Ministerio de la Presidencia, Justicia y Relaciones con las Cortes
 * %%
 * Licencia con arreglo a la EUPL, Versión 1.2 o –en cuanto
 *  sean aprobadas por la Comisión Europea– versiones
 *  posteriores de la EUPL (la «Licencia»)
 *  Solo podrá usarse esta obra si se respeta la Licencia.
 *  Puede obtenerse una copia de la Licencia en:
 * 
 *  https://joinup.ec.europa.eu/software/page/eupl
 * 
 *  Salvo cuando lo exija la legislación aplicable o se acuerde
 *  por escrito, el programa distribuido con arreglo a la
 *  Licencia se distribuye «TAL CUAL»,
 *  SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas
 *  ni implícitas.
 *  Véase la Licencia en el idioma concreto que rige
 *  los permisos y limitaciones que establece la Licencia
 * #L%
 */

import es.mjusticia.milano.config.EnableContext;
import es.mjusticia.milano.config.EnableRestClient;
import es.mjusticia.milano.config.EnableSecurityAuthJwtClient;
import es.mjusticia.milano.config.EnableSecurityAuthUnica;
import es.mjusticia.milano.config.EnableWebfacadeApplicationInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
// Carga de componentes del arquetipo
@EnableContext
@EnableWebfacadeApplicationInit
// Carga de componentes aplicativos
@ComponentScan("es.mjusticia.pagojus.frontconsola")
@EnableSecurityAuthUnica
@EnableRestClient
@EnableSecurityAuthJwtClient
public class ApplicationConfiguration {
        private static final Logger log = LoggerFactory.getLogger(ApplicationConfiguration.class);

        // PATHS
        private static final String ADMIN_PAGOS = "/adminPagos/**";
        private static final String STATIC = "/static/**";
        private static final String ERROR = "/error/**";
        private static final String LOGOUT = "/auth/**";

        // Textos de trazas
        private static final String INIT_LOG = "Inicio configuración filto de autorización del path {}";
        private static final String END_LOG = "Filtro de autorización del path {} configurado";

        @Bean
        public SecurityFilterChain pagoPrevioConfiguration(HttpSecurity http, AccessDeniedHandler accessDeniedHandler,
                        AuthenticationEntryPoint entryPoint, HandlerMappingIntrospector introspector) throws Exception {
                log.debug(INIT_LOG, ADMIN_PAGOS);
                var mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

                http.securityMatcher(ADMIN_PAGOS)
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(mvcMatcherBuilder.pattern(ADMIN_PAGOS))
                                                .authenticated())
                                .exceptionHandling(exception -> exception.authenticationEntryPoint(entryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .headers(generateHeaders());

                var filterChain = http.build();

                log.debug(END_LOG, ADMIN_PAGOS);
                return filterChain;
        }

        @Bean
        public SecurityFilterChain logoutConfiguration(HttpSecurity http, AccessDeniedHandler accessDeniedHandler,
                        AuthenticationEntryPoint entryPoint, HandlerMappingIntrospector introspector) throws Exception {
                log.debug(INIT_LOG, LOGOUT);
                var mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

                http.securityMatcher(LOGOUT)
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(mvcMatcherBuilder.pattern(LOGOUT)).permitAll())
                                .exceptionHandling(exception -> exception.authenticationEntryPoint(entryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .headers(generateHeaders());

                var filterChain = http.build();

                log.debug(END_LOG, LOGOUT);
                return filterChain;
        }

        @Bean
        public SecurityFilterChain staticConfiguration(HttpSecurity http, AccessDeniedHandler accessDeniedHandler,
                        AuthenticationEntryPoint entryPoint, HandlerMappingIntrospector introspector) throws Exception {
                log.debug(INIT_LOG, STATIC);
                var mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

                http.securityMatcher(STATIC)
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(mvcMatcherBuilder.pattern(STATIC)).permitAll())
                                .exceptionHandling(exception -> exception.authenticationEntryPoint(entryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .headers(generateHeaders());

                var filterChain = http.build();

                log.debug(END_LOG, STATIC);
                return filterChain;
        }

        @Bean
        public SecurityFilterChain errorConfiguration(HttpSecurity http, AccessDeniedHandler accessDeniedHandler,
                        AuthenticationEntryPoint entryPoint, HandlerMappingIntrospector introspector) throws Exception {
                log.debug(INIT_LOG, ERROR);
                var mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

                http.securityMatcher(ERROR)
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(mvcMatcherBuilder.pattern(ERROR)).permitAll())
                                .exceptionHandling(exception -> exception.authenticationEntryPoint(entryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .headers(generateHeaders());

                var filterChain = http.build();

                log.debug(END_LOG, ERROR);
                return filterChain;
        }

        private Customizer<HeadersConfigurer<HttpSecurity>> generateHeaders() {
                return header -> header.contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'"));
        }
}
