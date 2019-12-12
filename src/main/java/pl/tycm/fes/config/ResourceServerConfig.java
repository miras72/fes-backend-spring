package pl.tycm.fes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Value("${security.jwt.resource-ids}")
	private String resourceIds;

	private final ResourceServerTokenServices tokenServices;

	public ResourceServerConfig(ResourceServerTokenServices tokenServices) {
		this.tokenServices = tokenServices;
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(resourceIds).tokenServices(tokenServices);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.requestMatchers().and().authorizeRequests()
				.antMatchers("/api/tasks-status", "/api/server-config", "/api/encryption-keys", "/api/decryption-keys",
						"/api/file-exchange-status", "/api/tasks/test/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/download/public-keys/*").permitAll()
				.antMatchers(HttpMethod.GET, "/api/tasks/*").permitAll()
				.antMatchers("/api/**").authenticated();
	}
}
