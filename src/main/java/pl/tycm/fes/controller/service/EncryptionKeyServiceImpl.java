package pl.tycm.fes.service.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pl.tycm.fes.dao.EncryptionKeyDAO;
import pl.tycm.fes.exception.EncryptionKeyNotFoundException;
import pl.tycm.fes.model.EncryptionKey;
import pl.tycm.fes.model.EncryptionKeyDTO;
import pl.tycm.fes.model.PrivateKeyDTO;
import pl.tycm.fes.model.PublicKeyDTO;

@Service
public class EncryptionKeyServiceImpl implements EncryptionKeyService {

	@Autowired
	private EncryptionKeyDAO encryptionKeyDAO;

	@Override
	public List<EncryptionKeyDTO> getAllEncryptionKeyName() {
		return encryptionKeyDAO.getAllEncryptionKeyName();
	}

	@Override
	public EncryptionKeyDTO createEncryptionKey(MultipartFile privateFile, MultipartFile publicFile) throws IOException {

		EncryptionKey encryptionKey = new EncryptionKey();

		encryptionKey.setPrivateKeyName(privateFile.getOriginalFilename());
		encryptionKey.setPrivateKeyBinaryFile(privateFile.getBytes());

		encryptionKey.setPublicKeyName(publicFile.getOriginalFilename());
		encryptionKey.setPublicKeyBinaryFile(publicFile.getBytes());
		
		encryptionKeyDAO.createEncryptionKey(encryptionKey);
		
		EncryptionKeyDTO encryptionKeyDTO = new EncryptionKeyDTO(encryptionKey.getId(), encryptionKey.getPrivateKeyName(), encryptionKey.getPublicKeyName());
		
		return encryptionKeyDTO;
	}

	@Override
	public void deleteEncryptionKey(long id) throws EncryptionKeyNotFoundException {
		encryptionKeyDAO.deleteEncryptionKey(id);
	}

	@Override
	public PrivateKeyDTO getPrivateKey(long id) throws EncryptionKeyNotFoundException {
		return encryptionKeyDAO.getPrivateKey(id);
	}

	@Override
	public void updatePrivateKey(long id, MultipartFile privateFile)
			throws EncryptionKeyNotFoundException, IOException {
		PrivateKeyDTO privateKeyDTO = new PrivateKeyDTO(id, privateFile.getOriginalFilename(), privateFile.getBytes());
		
		encryptionKeyDAO.updatePrivateKey(privateKeyDTO);
	}

	@Override
	public PublicKeyDTO getPublicKey(long id) throws EncryptionKeyNotFoundException {
		return encryptionKeyDAO.getPublicKey(id);
	}

	@Override
	public void updatePublicKey(long id, MultipartFile publicFile)
			throws EncryptionKeyNotFoundException, IOException {
		PublicKeyDTO publicKeyDTO = new PublicKeyDTO(id, publicFile.getOriginalFilename(), publicFile.getBytes());
		
		encryptionKeyDAO.updatePublicKey(publicKeyDTO);
	}
}
