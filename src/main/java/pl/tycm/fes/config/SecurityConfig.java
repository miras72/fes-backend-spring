package pl.tycm.fes.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import pl.tycm.fes.service.CustomUserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${security.security-realm}")
	private String securityRealm;

	@Value("${ad.domain}")
	private String AD_DOMAIN;

	@Value("${ad.url}")
	private String AD_URL;

	@Override
	public void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
		
		authManagerBuilder.authenticationProvider(activeDirectoryLdapAuthenticationProvider()).userDetailsService(userDetailsService());
		
		/*authManagerBuilder.inMemoryAuthentication().withUser("test").password("123").roles("USER").and().withUser("admin")
				.password("123456").roles("ADMIN");*/
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return new ProviderManager(Arrays.asList(activeDirectoryLdapAuthenticationProvider()));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().httpBasic()
				.realmName(securityRealm);
	}
	
	@Bean
    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(AD_DOMAIN, AD_URL);
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);
        provider.setUserDetailsContextMapper(userDetailsContextMapper());

        return provider;
    }
	
	@Bean
    public UserDetailsContextMapper userDetailsContextMapper() {
        UserDetailsContextMapper contextMapper = new CustomUserDetailsServiceImpl();
        return contextMapper;
    }
}
