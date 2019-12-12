package pl.tycm.fes.controller.service;

import org.springframework.stereotype.Service;

import pl.tycm.fes.dao.ServerConfigDAO;
import pl.tycm.fes.exception.ServerConfigNotFoundException;
import pl.tycm.fes.model.ServerConfig;

@Service
public class ServerConfigServiceImpl implements ServerConfigService {

	private final ServerConfigDAO serverConfigDAO;

	public ServerConfigServiceImpl(ServerConfigDAO serverConfigDAO) {
		this.serverConfigDAO = serverConfigDAO;
	}

	@Override
	public ServerConfig getServerConfig() throws ServerConfigNotFoundException {
		return serverConfigDAO.getServerConfig();
	}

	@Override
	public void updateServeConfig(ServerConfig serverConfig) {
		serverConfigDAO.updateServeConfig(serverConfig);
	}
}
