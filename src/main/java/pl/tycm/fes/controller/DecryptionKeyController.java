package pl.tycm.fes.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import pl.tycm.fes.controller.service.DecryptionKeyService;
import pl.tycm.fes.exception.DecryptionKeyNotFoundException;
import pl.tycm.fes.model.DecryptionKey;
import pl.tycm.fes.model.DecryptionKeyDTO;

@RestController
@RequestMapping("/api")
public class DecryptionKeyController {

	private final DecryptionKeyService decryptionKeyService;
	
	public DecryptionKeyController(DecryptionKeyService decryptionKeyService) {
		this.decryptionKeyService = decryptionKeyService;
	}

	@GetMapping("/decryption-keys")
	@ResponseStatus(HttpStatus.OK)
	public List<DecryptionKeyDTO> getAllDecryptionKeyName() {

		return decryptionKeyService.getAllDecryptionKeyName();
	}

	@PostMapping("/decryption-keys")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<DecryptionKeyDTO> addEncryptionKey(
			@RequestParam("decryptionFile") MultipartFile decryptionFile, UriComponentsBuilder ucb) throws IOException {

		DecryptionKeyDTO decryptionKeyDTO = decryptionKeyService.createDecryptionKey(decryptionFile);

		HttpHeaders headers = new HttpHeaders();
		URI locationUri = ucb.path("/api/decryption-keys/").path(String.valueOf(decryptionKeyDTO.getId())).build()
				.toUri();
		headers.setLocation(locationUri);
		return new ResponseEntity<DecryptionKeyDTO>(decryptionKeyDTO, headers, HttpStatus.CREATED);
	}

	@DeleteMapping("/decryption-keys/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public void deleteDecryptionKey(@PathVariable long id) throws DecryptionKeyNotFoundException {

		decryptionKeyService.deleteDecryptionKey(id);
	}

	@GetMapping("/download/decryption-keys/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ByteArrayResource> downloadDecryptionKey(@PathVariable long id)
			throws DecryptionKeyNotFoundException {

		DecryptionKey decryptionKey = decryptionKeyService.getDecryptionKey(id);
		ByteArrayResource resource = new ByteArrayResource(decryptionKey.getDecryptionKeyBinaryFile());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "filename=" + decryptionKey.getDecryptionKeyName());

		return new ResponseEntity<ByteArrayResource>(resource, headers, HttpStatus.OK);
	}

	@PutMapping("/upload/decryption-keys/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void uploadDecryptionKey(@PathVariable long id, @RequestParam("decryptionFile") MultipartFile decryptionFile)
			throws IOException, DecryptionKeyNotFoundException {

		decryptionKeyService.updateDecryptionKey(id, decryptionFile);
	}
}
