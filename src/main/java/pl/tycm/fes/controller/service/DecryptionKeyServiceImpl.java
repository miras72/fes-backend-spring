package pl.tycm.fes.controller.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pl.tycm.fes.dao.DecryptionKeyDAO;
import pl.tycm.fes.exception.DecryptionKeyNotFoundException;
import pl.tycm.fes.model.DecryptionKey;
import pl.tycm.fes.model.DecryptionKeyDTO;

@Service
public class DecryptionKeyServiceImpl implements DecryptionKeyService {

	@Autowired
	private DecryptionKeyDAO decryptionKeyDAO;

	@Override
	public DecryptionKey getDecryptionKey(long id) throws DecryptionKeyNotFoundException {
		return decryptionKeyDAO.getDecryptionKey(id);
	}

	@Override
	public void updateDecryptionKey(long id, MultipartFile decryptionFile)
			throws IOException, DecryptionKeyNotFoundException {
		DecryptionKey decryptionKey = new DecryptionKey();
		decryptionKey.setId(id);
		decryptionKey.setDecryptionKeyName(decryptionFile.getOriginalFilename());
		decryptionKey.setDecryptionKeyBinaryFile(decryptionFile.getBytes());
		decryptionKeyDAO.updateDecryptionKey(decryptionKey);
	}

	@Override
	public DecryptionKeyDTO createDecryptionKey(MultipartFile decryptionFile) throws IOException {

		DecryptionKey decryptionKey = new DecryptionKey();

		decryptionKey.setDecryptionKeyName(decryptionFile.getOriginalFilename());
		decryptionKey.setDecryptionKeyBinaryFile(decryptionFile.getBytes());

		decryptionKeyDAO.createDecryptionKey(decryptionKey);

		DecryptionKeyDTO decryptionKeyDTO = new DecryptionKeyDTO(decryptionKey.getId(),
				decryptionKey.getDecryptionKeyName());

		return decryptionKeyDTO;
	}

	@Override
	public void deleteDecryptionKey(long id) throws DecryptionKeyNotFoundException {
		decryptionKeyDAO.deleteDecryptionKey(id);
	}

	@Override
	public List<DecryptionKeyDTO> getAllDecryptionKeyName() {
		return decryptionKeyDAO.getAllDecryptionKeyName();
	}

}
