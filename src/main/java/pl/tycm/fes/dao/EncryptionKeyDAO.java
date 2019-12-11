package pl.tycm.fes.dao;

import java.util.List;

import pl.tycm.fes.exception.EncryptionKeyNotFoundException;
import pl.tycm.fes.model.EncryptionKey;
import pl.tycm.fes.model.EncryptionKeyDTO;
import pl.tycm.fes.model.PrivateKeyDTO;
import pl.tycm.fes.model.PublicKeyDTO;

public interface EncryptionKeyDAO {
	
	public List<EncryptionKeyDTO> getAllEncryptionKeyName();
	
	public EncryptionKey createEncryptionKey(EncryptionKey encryptionKey);
	
	public void deleteEncryptionKey(long id) throws EncryptionKeyNotFoundException;
	
	public PrivateKeyDTO getPrivateKey(long id) throws EncryptionKeyNotFoundException;
	
	public void updatePrivateKey(PrivateKeyDTO privateKeyDTO) throws EncryptionKeyNotFoundException;
	
	public PublicKeyDTO getPublicKey(long id) throws EncryptionKeyNotFoundException;
	
	public void updatePublicKey(PublicKeyDTO publicKeyDTO) throws EncryptionKeyNotFoundException;
}
