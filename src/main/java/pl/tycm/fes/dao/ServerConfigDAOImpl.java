package pl.tycm.fes.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.tycm.fes.exception.ServerConfigNotFoundException;
import pl.tycm.fes.model.ServerConfig;

@Repository
public class ServerConfigDAOImpl implements ServerConfigDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	@Override
	public ServerConfig getServerConfig() throws ServerConfigNotFoundException {
		ServerConfig serverConfig = entityManager.find(ServerConfig.class, 1L);
		if (serverConfig == null) {
			ServerConfig serverConfigNew = new ServerConfig();
			serverConfigNew.setWorkDirectory("/tmp");
			serverConfigNew.setArchDirectory("/tmp/arch");
			entityManager.persist(serverConfigNew);
			serverConfig = entityManager.find(ServerConfig.class, 1L);
		}
		return serverConfig;
	}

	@Transactional
	@Override
	public void updateServeConfig(ServerConfig serverConfigNew) {
		ServerConfig serverConfig = entityManager.find(ServerConfig.class, 1L);
		if (serverConfig != null) {
			serverConfig.setWorkDirectory(serverConfigNew.getWorkDirectory());
			serverConfig.setArchDirectory(serverConfigNew.getArchDirectory());
			entityManager.flush();
		} else {
			entityManager.persist(serverConfigNew);
		}
	}

}
