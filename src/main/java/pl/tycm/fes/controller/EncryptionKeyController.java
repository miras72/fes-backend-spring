package pl.tycm.fes.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import pl.tycm.fes.exception.EncryptionKeyNotFoundException;
import pl.tycm.fes.model.EncryptionKeyDTO;
import pl.tycm.fes.model.PrivateKeyDTO;
import pl.tycm.fes.model.PublicKeyDTO;
import pl.tycm.fes.service.controller.EncryptionKeyService;

@RestController
@RequestMapping("/api")
public class EncryptionKeyController {

	@Autowired
	private EncryptionKeyService encryptionKeyService;

	@GetMapping("/encryption-keys")
	@ResponseStatus(HttpStatus.OK)
	public List<EncryptionKeyDTO> getAllEncryptionKeyName() {

		return encryptionKeyService.getAllEncryptionKeyName();
	}

	@PostMapping("/encryption-keys")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<EncryptionKeyDTO> addEncryptionKey(@RequestParam("privateFile") MultipartFile privateFile,
			@RequestParam("publicFile") MultipartFile publicFile, UriComponentsBuilder ucb) throws IOException {

		EncryptionKeyDTO encryptionKeyDTO = encryptionKeyService.createEncryptionKey(privateFile, publicFile);

		HttpHeaders headers = new HttpHeaders();
		URI locationUri = ucb.path("/api/encryption-keys/").path(String.valueOf(encryptionKeyDTO.getId())).build()
				.toUri();
		headers.setLocation(locationUri);
		return new ResponseEntity<EncryptionKeyDTO>(encryptionKeyDTO, headers, HttpStatus.CREATED);
	}

	@DeleteMapping("/encryption-keys/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public void deleteEncryptionKey(@PathVariable long id)
			throws EncryptionKeyNotFoundException {

		encryptionKeyService.deleteEncryptionKey(id);
	}

	@GetMapping("/download/private-keys/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ByteArrayResource> downloadPrivateKey(@PathVariable long id)
			throws EncryptionKeyNotFoundException {

		PrivateKeyDTO privateKey = encryptionKeyService.getPrivateKey(id);
		ByteArrayResource resource = new ByteArrayResource(privateKey.getPrivateKeyBinaryFile());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "filename=" + privateKey.getPrivateKeyName());

		return new ResponseEntity<ByteArrayResource>(resource, headers, HttpStatus.OK);
	}

	@PutMapping("/upload/private-keys/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void uploadPrivateKey(@PathVariable long id,
			@RequestParam("privateFile") MultipartFile privateFile) throws IOException, EncryptionKeyNotFoundException {

		encryptionKeyService.updatePrivateKey(id, privateFile);
	}

	@GetMapping("/download/public-keys/{id}")
	public ResponseEntity<ByteArrayResource> downloadPublicKey(@PathVariable long id)
			throws EncryptionKeyNotFoundException {

		PublicKeyDTO privateKey = encryptionKeyService.getPublicKey(id);
		ByteArrayResource resource = new ByteArrayResource(privateKey.getPublicKeyBinaryFile());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "filename=" + privateKey.getPublicKeyName());

		return new ResponseEntity<ByteArrayResource>(resource, headers, HttpStatus.OK);
	}

	@PutMapping("/upload/public-keys/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void uploadPublicKey(@PathVariable long id,
			@RequestParam("publicFile") MultipartFile publicFile) throws IOException, EncryptionKeyNotFoundException {

		encryptionKeyService.updatePublicKey(id, publicFile);
	}
}
