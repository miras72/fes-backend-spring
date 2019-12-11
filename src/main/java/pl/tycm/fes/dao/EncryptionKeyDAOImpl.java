package pl.tycm.fes.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.tycm.fes.exception.EncryptionKeyNotFoundException;
import pl.tycm.fes.model.EncryptionKey;
import pl.tycm.fes.model.EncryptionKeyDTO;
import pl.tycm.fes.model.PrivateKeyDTO;
import pl.tycm.fes.model.PublicKeyDTO;

@Repository
public class EncryptionKeyDAOImpl implements EncryptionKeyDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<EncryptionKeyDTO> getAllEncryptionKeyName() {
		String hql = "Select New pl.tycm.fes.model.EncryptionKeyDTO (id, privateKeyName, publicKeyName) From EncryptionKey Order By privateKeyName";
		return entityManager.createQuery(hql, EncryptionKeyDTO.class).getResultList();
	}

	@Transactional
	@Override
	public EncryptionKey createEncryptionKey(EncryptionKey encryptionKey) {
		entityManager.persist(encryptionKey);
		return encryptionKey;
	}

	@Transactional
	@Override
	public void deleteEncryptionKey(long id) throws EncryptionKeyNotFoundException {
		EncryptionKey encryptionKey = entityManager.find(EncryptionKey.class, id);
		if (encryptionKey == null)
			throw new EncryptionKeyNotFoundException(id);
		entityManager.remove(encryptionKey);
	}

	@Override
	public PrivateKeyDTO getPrivateKey(long id) throws EncryptionKeyNotFoundException {
		EncryptionKey encryptionKey = entityManager.find(EncryptionKey.class, id);
		if (encryptionKey == null)
			throw new EncryptionKeyNotFoundException(id);
		String hql = "Select New pl.tycm.fes.model.PrivateKeyDTO (id, privateKeyName, privateKeyBinaryFile) From EncryptionKey Where id=:id";
		return entityManager.createQuery(hql, PrivateKeyDTO.class).setParameter("id", id).getSingleResult();
	}

	@Transactional
	@Override
	public void updatePrivateKey(PrivateKeyDTO privateKeyDTO) throws EncryptionKeyNotFoundException {
		EncryptionKey encryptionKey = entityManager.find(EncryptionKey.class, privateKeyDTO.getId());
		if (encryptionKey == null)
			throw new EncryptionKeyNotFoundException(privateKeyDTO.getId());
		encryptionKey.setPrivateKeyName(privateKeyDTO.getPrivateKeyName());
		encryptionKey.setPrivateKeyBinaryFile(privateKeyDTO.getPrivateKeyBinaryFile());
		entityManager.flush();
	}

	@Override
	public PublicKeyDTO getPublicKey(long id) throws EncryptionKeyNotFoundException {
		EncryptionKey encryptionKey = entityManager.find(EncryptionKey.class, id);
		if (encryptionKey == null)
			throw new EncryptionKeyNotFoundException(id);
		String hql = "Select New pl.tycm.fes.model.PublicKeyDTO (id, publicKeyName, publicKeyBinaryFile) From EncryptionKey Where id=:id";
		return entityManager.createQuery(hql, PublicKeyDTO.class).setParameter("id", id).getSingleResult();
	}

	@Transactional
	@Override
	public void updatePublicKey(PublicKeyDTO publicKeyDTO) throws EncryptionKeyNotFoundException {
		EncryptionKey encryptionKey = entityManager.find(EncryptionKey.class, publicKeyDTO.getId());
		if (encryptionKey == null)
			throw new EncryptionKeyNotFoundException(publicKeyDTO.getId());
		encryptionKey.setPublicKeyName(publicKeyDTO.getPublicKeyName());
		encryptionKey.setPublicKeyBinaryFile(publicKeyDTO.getPublicKeyBinaryFile());
		entityManager.flush();
	}
}
