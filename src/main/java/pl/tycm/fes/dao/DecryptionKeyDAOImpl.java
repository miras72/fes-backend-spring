package pl.tycm.fes.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.tycm.fes.exception.DecryptionKeyNotFoundException;
import pl.tycm.fes.model.DecryptionKey;
import pl.tycm.fes.model.DecryptionKeyDTO;

@Repository
public class DecryptionKeyDAOImpl implements DecryptionKeyDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public DecryptionKey getDecryptionKey(long id) throws DecryptionKeyNotFoundException {
		DecryptionKey decryptionKey = entityManager.find(DecryptionKey.class, id);
		if (decryptionKey == null)
			throw new DecryptionKeyNotFoundException(id);
		return decryptionKey;
	}

	@Transactional
	@Override
	public void updateDecryptionKey(DecryptionKey decryptionKeyNew) throws DecryptionKeyNotFoundException {
		DecryptionKey decryptionKey = entityManager.find(DecryptionKey.class, decryptionKeyNew.getId());
		if (decryptionKey == null)
			throw new DecryptionKeyNotFoundException(decryptionKeyNew.getId());
		decryptionKey.setDecryptionKeyName(decryptionKeyNew.getDecryptionKeyName());
		decryptionKey.setDecryptionKeyBinaryFile(decryptionKeyNew.getDecryptionKeyBinaryFile());
		entityManager.flush();
	}

	@Transactional
	@Override
	public DecryptionKey createDecryptionKey(DecryptionKey decryptionKey) {
		entityManager.persist(decryptionKey);
		return decryptionKey;

	}

	@Transactional
	@Override
	public void deleteDecryptionKey(long id) throws DecryptionKeyNotFoundException {
		DecryptionKey decryptionKey = entityManager.find(DecryptionKey.class, id);
		if (decryptionKey == null)
			throw new DecryptionKeyNotFoundException(id);
		entityManager.remove(decryptionKey);
	}

	@Override
	public List<DecryptionKeyDTO> getAllDecryptionKeyName() {
		String hql = "Select New pl.tycm.fes.model.DecryptionKeyDTO (id, decryptionKeyName) From DecryptionKey Order By decryptionKeyName";
		return entityManager.createQuery(hql, DecryptionKeyDTO.class).getResultList();
	}
}
