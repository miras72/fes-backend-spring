package pl.tycm.fes.dao;

import pl.tycm.fes.exception.ServerConfigNotFoundException;
import pl.tycm.fes.model.ServerConfig;

public interface ServerConfigDAO {

	public ServerConfig getServerConfig() throws ServerConfigNotFoundException;

	public void updateServeConfig(ServerConfig serverConfig);
}
