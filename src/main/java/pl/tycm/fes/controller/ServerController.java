package pl.tycm.fes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.tycm.fes.controller.service.ServerConfigService;
import pl.tycm.fes.exception.ServerConfigNotFoundException;
import pl.tycm.fes.model.ServerConfig;

@RestController
@RequestMapping("/api")
public class ServerController {

	private final ServerConfigService serverConfigService;

	public ServerController(ServerConfigService serverConfigService) {
		this.serverConfigService = serverConfigService;
	}

	@GetMapping("/server-config")
	@ResponseStatus(HttpStatus.OK)
	public ServerConfig getServerConfig() throws ServerConfigNotFoundException {

		return serverConfigService.getServerConfig();
	}

	@PutMapping("/server-config")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateServerConfig(@RequestBody ServerConfig serverConfig) {

		serverConfigService.updateServeConfig(serverConfig);
	}
}
