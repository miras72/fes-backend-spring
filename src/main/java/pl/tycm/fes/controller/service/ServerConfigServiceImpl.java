package pl.tycm.fes.controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.tycm.fes.dao.ServerConfigDAO;
import pl.tycm.fes.exception.ServerConfigNotFoundException;
import pl.tycm.fes.model.ServerConfig;

@Service
public class ServerConfigServiceImpl implements ServerConfigService {

	@Autowired
	private ServerConfigDAO serverConfigDAO;

	@Override
	public ServerConfig getServerConfig() throws ServerConfigNotFoundException {
		return serverConfigDAO.getServerConfig();
	}

	@Override
	public void updateServeConfig(ServerConfig serverConfig) {
		serverConfigDAO.updateServeConfig(serverConfig);
	}
}
