package pl.tycm.fes.controller.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import pl.tycm.fes.exception.EncryptionKeyNotFoundException;
import pl.tycm.fes.model.EncryptionKeyDTO;
import pl.tycm.fes.model.PrivateKeyDTO;
import pl.tycm.fes.model.PublicKeyDTO;

public interface EncryptionKeyService {

	public List<EncryptionKeyDTO> getAllEncryptionKeyName();
	
	public EncryptionKeyDTO createEncryptionKey(MultipartFile privateFile, MultipartFile publicFile) throws IOException;
	
	public void deleteEncryptionKey(long id) throws EncryptionKeyNotFoundException;
	
	public PrivateKeyDTO getPrivateKey(long id) throws EncryptionKeyNotFoundException;
	
	public void updatePrivateKey(long id, MultipartFile privateFile) throws EncryptionKeyNotFoundException, IOException;
	
	public PublicKeyDTO getPublicKey(long id) throws EncryptionKeyNotFoundException;
	
	public void updatePublicKey(long id, MultipartFile publicFile) throws EncryptionKeyNotFoundException, IOException;
}
