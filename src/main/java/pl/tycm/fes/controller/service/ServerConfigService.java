package pl.tycm.fes.controller.service;

import pl.tycm.fes.exception.ServerConfigNotFoundException;
import pl.tycm.fes.model.ServerConfig;

public interface ServerConfigService {
	
	public ServerConfig getServerConfig() throws ServerConfigNotFoundException;

	public void updateServeConfig(ServerConfig serverConfig);
}
