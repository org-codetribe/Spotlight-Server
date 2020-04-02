package com.yappyapps.spotlight.security;

import com.yappyapps.spotlight.filter.RestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.yappyapps.spotlight.filter.CORSFilter;
import com.yappyapps.spotlight.filter.JwtAuthorizationTokenFilter;

/**
 * The SpotlightUserWebSecurityConfiguration class is used to configure the JWT
 * security for application
 * 
 * <h1>@Configuration</h1> indicates that this class creates one or more Beans
 * which will be used by other classes.
 * 
 * <h1>@EnableWebSecurity</h1> indicates that it contains the configuration for
 * Spring Security.
 * 
 * @author Naveen Goswami
 * @version 1.0
 * @since 2018-07-14
 */
@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(100)
public class SpotlightUserWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	/**
	 * serialVersionUID
	 */
	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	/**
	 * authenticationPath
	 */
	@Value("${jwt.route.authentication.path}")
	private String authenticationPath;

	/**
	 * CORSFilter dependency will be automatically injected.
	 * <h1>@Autowired</h1> will enable auto injecting the beans from Spring Context.
	 */
	//@Autowired
	//private RestConfig filterBean;

	@Autowired
	private JwtAuthorizationTokenFilter authenticationTokenFilter;

	@Autowired
    private CustomAuthenticationProvider authProvider;
	
	/**
	 * This method is used to configure the authenticationManagerBuilder with SpotlightUserService.
	 * 
	 * @param auth:
	 *            AuthenticationManagerBuilder
	 * 
	 * @throws Exception
	 *             Exception
	 * 
	 *             <h1>@Autowired</h1> will enable auto injecting the beans from
	 *             Spring Context.
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoderBean());
		auth.authenticationProvider(authProvider);
	}

	/**
	 * PasswordEncoder dependency will be automatically injected.
	 * 
	 * @return PasswordEncoder: BCryptPasswordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoderBean() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * AuthenticationManager dependency will be automatically injected.
	 * 
	 * @return AuthenticationManager: authenticationManager
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * This method is used to configure the HttpSecurity.
	 * 
	 * @param httpSecurity:
	 *            HttpSecurity
	 * 
	 * @throws Exception
	 *             Exception
	 * 
	 */
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
				.addFilterAfter(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
				//.addFilterBefore(filterBean, UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers(HttpMethod.POST, "/api/1.0/login/viewer", "/api/1.0/login/user").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/1.0/logout/viewer", "/api/1.0/logout/user").permitAll()
				.antMatchers(HttpMethod.GET, "/api/1.0/search/**").permitAll()
				.antMatchers(HttpMethod.PUT, "/api/1.0/resetUserPassword").permitAll()
				.antMatchers(HttpMethod.PUT, "/api/1.0/resetViewerPassword").permitAll()
				.antMatchers(HttpMethod.POST, "/api/1.0/register/viewer").permitAll()
				.antMatchers(HttpMethod.PATCH, "/api/1.0/register/viewer").permitAll()
				.antMatchers(HttpMethod.GET, "/api/1.0/event/public/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/1.0/broadcaster/public/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/1.0/viewer/favorite/**")
				.hasAnyRole("VIEWER")
				.antMatchers(HttpMethod.GET, "/api/1.0/viewer/**/favorite/**")
				.hasAnyRole("VIEWER")
				.antMatchers(HttpMethod.GET, "/api/1.0/viewer/**/purchased/**")
				.hasAnyRole("VIEWER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/viewer/chataccess/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "VIEWER_OWNER", "VIEWER_CONTRIBUTOR", "BROADCASTER")
				.antMatchers(HttpMethod.GET, "/api/1.0/auditlog/**")
				.hasAnyRole("SUPERADMIN")
				.antMatchers(HttpMethod.PUT, "/api/1.0/user/spotlight/password")
				.hasAnyRole("SUPERADMIN", "ADMIN", "BROADCASTER", "SALES", "MANAGEMENTCOMPANY")
				.antMatchers(HttpMethod.PUT, "/api/1.0/viewer/password")
				.hasAnyRole("VIEWER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/user/spotlight/profile")
				.hasAnyRole("SUPERADMIN", "ADMIN", "SALES", "MANAGEMENTCOMPANY")
				.antMatchers(HttpMethod.POST, "/api/1.0/broadcaster/profile")
				.hasAnyRole("BROADCASTER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/viewer/profile")
				.hasAnyRole("VIEWER")
				.antMatchers(HttpMethod.GET, "/api/1.0/user/spotlight/profile/**")
				.hasAnyRole("SUPERADMIN", "ADMIN", "SALES", "MANAGEMENTCOMPANY")
				.antMatchers(HttpMethod.GET, "/api/1.0/broadcaster/profile/**")
				.hasAnyRole("BROADCASTER")
				.antMatchers(HttpMethod.GET, "/api/1.0/viewer/profile/**")
				.hasAnyRole("VIEWER")
				.antMatchers(HttpMethod.GET, "/api/1.0/user/**").hasAnyRole("SUPERADMIN")
				.antMatchers(HttpMethod.POST, "/api/1.0/user/**").hasAnyRole("SUPERADMIN")
				.antMatchers(HttpMethod.PUT, "/api/1.0/user/**").hasAnyRole("SUPERADMIN")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/user/**").hasAnyRole("SUPERADMIN")
				.antMatchers(HttpMethod.GET, "/api/1.0/payment/**").hasAnyRole("SUPERADMIN", "ADMIN", "BROADCASTER", "SALES", "MANAGEMENTCOMPANY", "VIEWER")
				.antMatchers(HttpMethod.POST, "/api/1.0/payment/**").hasAnyRole("SUPERADMIN", "ADMIN", "BROADCASTER", "SALES", "MANAGEMENTCOMPANY", "VIEWER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/payment/**").hasAnyRole("SUPERADMIN", "ADMIN", "BROADCASTER", "SALES", "MANAGEMENTCOMPANY", "VIEWER")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/payment/**").hasAnyRole("SUPERADMIN", "ADMIN", "BROADCASTER", "SALES", "MANAGEMENTCOMPANY", "VIEWER")
				.antMatchers(HttpMethod.GET, "/api/1.0/admin/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "ADMIN_OWNER", "ADMIN_READER",
						"ADMIN_CONTRIBUTOR")
				.antMatchers(HttpMethod.POST, "/api/1.0/admin/**").hasAnyRole("SUPERADMIN", "OWNER", "ADMIN_OWNER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/admin/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "ADMIN_OWNER", "ADMIN_CONTRIBUTOR")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/admin/**").hasAnyRole("SUPERADMIN", "OWNER", "ADMIN_OWNER")
				.antMatchers(HttpMethod.GET, "/api/1.0/sales/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "SALES_OWNER", "SALES_READER",
						"SALES_CONTRIBUTOR")
				.antMatchers(HttpMethod.POST, "/api/1.0/sales/**").hasAnyRole("SUPERADMIN", "OWNER", "SALES_OWNER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/sales/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "SALES_OWNER", "SALES_CONTRIBUTOR")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/sales/**").hasAnyRole("SUPERADMIN", "OWNER", "SALES_OWNER")
				.antMatchers(HttpMethod.GET, "/api/1.0/managementcompany/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "MANAGEMENTCOMPANY_OWNER",
						"MANAGEMENTCOMPANY_READER", "MANAGEMENTCOMPANY_CONTRIBUTOR")
				.antMatchers(HttpMethod.POST, "/api/1.0/managementcompany/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "MANAGEMENTCOMPANY_OWNER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/managementcompany/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "MANAGEMENTCOMPANY_OWNER",
						"MANAGEMENTCOMPANY_CONTRIBUTOR")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/managementcompany/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "MANAGEMENTCOMPANY_OWNER")
				.antMatchers(HttpMethod.POST, "/api/1.0/broadcaster/update")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "BROADCASTER_OWNER", "BROADCASTER_CONTRIBUTOR")
				.antMatchers(HttpMethod.GET, "/api/1.0/broadcaster/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "BROADCASTER_OWNER", "BROADCASTER_READER",
						"BROADCASTER_CONTRIBUTOR")
				.antMatchers(HttpMethod.POST, "/api/1.0/broadcaster/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "BROADCASTER_OWNER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/broadcaster/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "BROADCASTER_OWNER", "BROADCASTER_CONTRIBUTOR")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/broadcaster/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "BROADCASTER_OWNER").antMatchers(HttpMethod.GET, "/api/1.0/role/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "ACCESSROLE_OWNER", "ACCESSROLE_READER",
						"ACCESSROLE_CONTRIBUTOR")
				.antMatchers(HttpMethod.POST, "/api/1.0/role/**").hasAnyRole("SUPERADMIN", "OWNER", "ACCESSROLE_OWNER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/role/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "ACCESSROLE_OWNER", "ACCESSROLE_CONTRIBUTOR")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/role/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "ACCESSROLE_OWNER").antMatchers(HttpMethod.GET, "/api/1.0/genre/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "GENRE_OWNER", "GENRE_READER",
						"GENRE_CONTRIBUTOR", "BROADCASTER")
				.antMatchers(HttpMethod.POST, "/api/1.0/genre/**").hasAnyRole("SUPERADMIN", "OWNER", "GENRE_OWNER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/genre/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "GENRE_OWNER", "GENRE_CONTRIBUTOR")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/genre/**").hasAnyRole("SUPERADMIN", "OWNER", "GENRE_OWNER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/event/review").hasAnyRole("VIEWER")
				.antMatchers(HttpMethod.GET, "/api/1.0/event/reviews/id/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "EVENT_OWNER", "EVENT_READER",
						"EVENT_CONTRIBUTOR", "BROADCASTER", "VIEWER")
				.antMatchers(HttpMethod.GET, "/api/1.0/event/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "EVENT_OWNER", "EVENT_READER",
						"EVENT_CONTRIBUTOR", "BROADCASTER")
				.antMatchers(HttpMethod.POST, "/api/1.0/event/**").hasAnyRole("SUPERADMIN", "OWNER", "EVENT_OWNER", "BROADCASTER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/event/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "EVENT_OWNER", "EVENT_CONTRIBUTOR", "BROADCASTER")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/event/**").hasAnyRole("SUPERADMIN", "OWNER", "EVENT_OWNER", "BROADCASTER")
				.antMatchers(HttpMethod.GET, "/api/1.0/eventtype/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "EVENTTYPE_OWNER", "EVENTTYPE_READER",
						"EVENTTYPE_CONTRIBUTOR", "BROADCASTER")
				.antMatchers(HttpMethod.POST, "/api/1.0/eventtype/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "EVENTTYPE_OWNER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/eventtype/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "EVENTTYPE_OWNER", "EVENTTYPE_CONTRIBUTOR")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/eventtype/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "EVENTTYPE_OWNER")
				.antMatchers(HttpMethod.GET, "/api/1.0/livestreamconfig/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "LIVESTREAMCONFIG_OWNER",
						"LIVESTREAMCONFIG_READER", "LIVESTREAMCONFIG_CONTRIBUTOR", "BROADCASTER")
				.antMatchers(HttpMethod.POST, "/api/1.0/livestreamconfig/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "LIVESTREAMCONFIG_OWNER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/livestreamconfig/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "LIVESTREAMCONFIG_OWNER",
						"LIVESTREAMCONFIG_CONTRIBUTOR")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/livestreamconfig/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "LIVESTREAMCONFIG_OWNER")
				.antMatchers(HttpMethod.GET, "/api/1.0/pricingrule/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "PRICINGRULE_OWNER", "PRICINGRULE_READER",
						"PRICINGRULE_CONTRIBUTOR", "BROADCASTER")
				.antMatchers(HttpMethod.POST, "/api/1.0/pricingrule/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "PRICINGRULE_OWNER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/pricingrule/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "PRICINGRULE_OWNER", "PRICINGRULE_CONTRIBUTOR")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/pricingrule/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "PRICINGRULE_OWNER")
				.antMatchers(HttpMethod.GET, "/api/1.0/coupon/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "COUPON_OWNER", "COUPON_READER",
						"COUPON_CONTRIBUTOR", "BROADCASTER")
				.antMatchers(HttpMethod.POST, "/api/1.0/coupon/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "COUPON_OWNER", "BROADCASTER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/coupon/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "COUPON_OWNER", "COUPON_CONTRIBUTOR", "BROADCASTER")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/coupon/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "COUPON_OWNER", "BROADCASTER")
				.antMatchers(HttpMethod.GET, "/api/1.0/viewer/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "READER", "CONTRIBUTOR", "VIEWER_OWNER", "VIEWER_READER",
						"VIEWER_CONTRIBUTOR")
				.antMatchers(HttpMethod.POST, "/api/1.0/viewer/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "VIEWER_OWNER")
				.antMatchers(HttpMethod.PUT, "/api/1.0/viewer/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "VIEWER_OWNER", "VIEWER_CONTRIBUTOR")
				.antMatchers(HttpMethod.DELETE, "/api/1.0/viewer/**")
				.hasAnyRole("SUPERADMIN", "OWNER", "VIEWER_OWNER")
				.antMatchers(HttpMethod.GET, "/api/1.0/analytics/**").hasAnyRole("SUPERADMIN", "OWNER", "CONTRIBUTOR", "ADMIN", "BROADCASTER")
				.anyRequest().authenticated();
	}

	/**
	 * This method is used to configure the WebSecurity.
	 * 
	 * @param webSecurity:
	 *            WebSecurity
	 * 
	 * @throws Exception
	 *             Exception
	 * 
	 */
	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		// AuthenticationTokenFilter will ignore the below paths
		webSecurity.ignoring().antMatchers(HttpMethod.OPTIONS, "/**")

				// allow anonymous resource requests
				.and().ignoring().antMatchers(HttpMethod.GET, "/", "/*.html", "/favicon.ico", "/**/*.html", "/**/*.css",
						"/**/*.js", "/swagger-ui.html");

	}
	
    private static final String[] AUTH_WHITELIST = {

            // -- swagger ui
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**"
    };
}