package pl.tycm.fes.service.controller;

import pl.tycm.fes.exception.ServerConfigNotFoundException;
import pl.tycm.fes.model.ServerConfig;

public interface ServerConfigService {
	
	public ServerConfig getServerConfig() throws ServerConfigNotFoundException;

	public void updateServeConfig(ServerConfig serverConfig);
}
