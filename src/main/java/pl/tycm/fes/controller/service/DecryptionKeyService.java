package pl.tycm.fes.controller.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import pl.tycm.fes.exception.DecryptionKeyNotFoundException;
import pl.tycm.fes.model.DecryptionKey;
import pl.tycm.fes.model.DecryptionKeyDTO;

public interface DecryptionKeyService {

	public DecryptionKey getDecryptionKey(long id) throws DecryptionKeyNotFoundException;

	public void updateDecryptionKey(long id, MultipartFile decryptionFile) throws IOException, DecryptionKeyNotFoundException;

	public DecryptionKeyDTO createDecryptionKey(MultipartFile decryptionFile) throws IOException;

	public void deleteDecryptionKey(long id) throws DecryptionKeyNotFoundException;

	public List<DecryptionKeyDTO> getAllDecryptionKeyName();
}
