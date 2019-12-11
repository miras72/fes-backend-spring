package pl.tycm.fes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import pl.tycm.fes.service.AppService;

@Configuration
@PropertySources({
	@PropertySource("file:./config.properties")
})
public class AppConfig {

	@Bean
	public AppService appBean() {
		return new AppService();
	}
}
