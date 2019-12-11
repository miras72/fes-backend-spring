package pl.tycm.fes.dao;

import java.util.List;

import pl.tycm.fes.exception.DecryptionKeyNotFoundException;
import pl.tycm.fes.model.DecryptionKey;
import pl.tycm.fes.model.DecryptionKeyDTO;

public interface DecryptionKeyDAO {

	public DecryptionKey getDecryptionKey(long id) throws DecryptionKeyNotFoundException;

	public void updateDecryptionKey(DecryptionKey decryptionKey) throws DecryptionKeyNotFoundException;

	public DecryptionKey createDecryptionKey(DecryptionKey decryptionKey);

	public void deleteDecryptionKey(long id) throws DecryptionKeyNotFoundException;

	public List<DecryptionKeyDTO> getAllDecryptionKeyName();
}
