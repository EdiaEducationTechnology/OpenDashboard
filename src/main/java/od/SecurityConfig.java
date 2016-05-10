/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 *
 */
package od;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lti.oauth.OAuthFilter;
import od.lti.LTIAuthenticationProvider;
import od.lti.LTIUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

/**
 * @author ggilbert
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
  
  @Configuration
  @Order(1)
  public static class LTIWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
    @Autowired private OAuthFilter oAuthFilter;
    @Autowired private LTIUserDetailsService userDetailsService;
    @Autowired private LTIAuthenticationProvider authenticationProvider;

    @Bean
    public FilterRegistrationBean oAuthFilterBean() {
      FilterRegistrationBean registrationBean = new FilterRegistrationBean();
      registrationBean.setFilter(oAuthFilter);
      List<String> urls = new ArrayList<>(1);
      urls.add("/lti");
      registrationBean.setUrlPatterns(urls);
      registrationBean.setOrder(2);
      return registrationBean;
    }

    protected void configure(HttpSecurity http) throws Exception {
      http
        .antMatcher("/lti")
          .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/lti").permitAll()
      .and()
        .headers().frameOptions().disable() 
        .csrf().disable();
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {    
      auth
        .authenticationProvider(authenticationProvider)
        .userDetailsService(userDetailsService);
    }
    
    @Bean(name="LTIAuthenticationManager")
    public AuthenticationManager authManager() throws Exception {
      return super.authenticationManagerBean();
    }
  }
  
  @Configuration
  @ConditionalOnProperty(name="features.saml",havingValue="false")
  public static class HttpBasicConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Value(value = "${password.admin:admin}")
    String adminPassword;

    @Value(value = "${password.student:admin}")
    String studentPassword;

    @Value(value = "${password.instructor:admin}")
    String instructorPassword;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
          .antMatchers("/assets/**", "/favicon.ico", "/cards/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
      .httpBasic()
        .authenticationEntryPoint(new NoWWWAuthenticate401ResponseEntryPoint("opendashboard"))
      .and()
      .authorizeRequests()
        .antMatchers("/features/**", "/", "/login").permitAll()
        .anyRequest().authenticated()
      .and()
        .logout()
        .invalidateHttpSession(true)
        .deleteCookies("ODSESSIONID", "X-OD-TENANT")
      .and().csrf().csrfTokenRepository(csrfTokenRepository())
      /**
       * 
       * TODO revisit after updating to Spring Security 4.1 
       * Currently the SessionManagementFilter is added here instead of the CsrfFilter 
       * Two session tokens are generated, one token is created before login and one token is created after.
       * The Csrf doesn't update with the second token.
       * Logout does not work as a side effect.
       * @link https://github.com/dsyer/spring-security-angular/issues/15
       * 
       * */
      .and().addFilterAfter(csrfHeaderFilter(), SessionManagementFilter.class);
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth
        .inMemoryAuthentication()
          .withUser("student").password(studentPassword).roles("STUDENT")
          .and()
          .withUser("instructor").password(instructorPassword).roles("INSTRUCTOR")
          .and()
          .withUser("admin").password(adminPassword).roles("INSTRUCTOR","ADMIN")
          //Test Admin set up for functional testing purposes
          .and()
          .withUser("test_admin").password(adminPassword).roles("INSTRUCTOR","ADMIN");
    }
    
    @Primary
    @Bean
    public AuthenticationManager authManager() throws Exception {
      return super.authenticationManagerBean();
    }
    
    private Filter csrfHeaderFilter() {
      return new OncePerRequestFilter() {      
        @Override
        protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
          CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
          
          if (csrf != null) {
            Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
            String token = csrf.getToken();
            if (cookie == null || token != null
                && !token.equals(cookie.getValue())) {
              cookie = new Cookie("XSRF-TOKEN", token);
              cookie.setPath("/");
              response.addCookie(cookie);
            }
          }
          filterChain.doFilter(request, response);
        }
      };
    }

    private CsrfTokenRepository csrfTokenRepository() {
      HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
      repository.setHeaderName("X-XSRF-TOKEN");
      return repository;
    }
    
    static class NoWWWAuthenticate401ResponseEntryPoint extends BasicAuthenticationEntryPoint {
      
      public NoWWWAuthenticate401ResponseEntryPoint(String realm) {
        setRealmName(realm);
      }
      
      @Override
      public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
          throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendRedirect("/login");
      }
    }

  }
    
  @Autowired
  private ExceptionFilter exceptionFilter;

  /*
   * Order of precedence
   * 
   * 1. exceptionFilter (handles exceptions thrown in downstream filters)
   * 2. oAuthFilter
   * 3. mongoFilterBean
   */
  
  @Bean
  public FilterRegistrationBean exceptionFilterBean() {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    registrationBean.setFilter(exceptionFilter);
    List<String> urls = new ArrayList<>(1);
    urls.add("/");
    urls.add("/api/*");
    urls.add("/cm/*");
    registrationBean.setUrlPatterns(urls);
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return registrationBean;
  }

	@Bean
	public SessionTrackingConfigListener sessionTrackingConfigListener() {
		return new SessionTrackingConfigListener();
	}

	public static class SessionTrackingConfigListener implements ServletContextInitializer {

		@Override
		public void onStartup(ServletContext servletContext) throws ServletException {
			SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
			sessionCookieConfig.setName("ODSESSIONID");
		}

	}

}
