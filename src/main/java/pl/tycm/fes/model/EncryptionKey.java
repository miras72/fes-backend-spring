package pl.tycm.fes.model;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "ENCRYPTION_KEY_SPRING")
public class EncryptionKey {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encryptionKeySeq")
	@SequenceGenerator(name = "encryptionKeySeq", sequenceName = "ENCRYPTION_KEY_SPRING_SEQ", allocationSize = 1)
	private Long id;
	
	@NotBlank
	@Column(name = "PRIVATE_KEY_NAME")
	@Size(max = 25)
	private String privateKeyName;
	
	@NotNull
	@Column(name = "PRIVATE_KEY_BINARY_FILE")
	@Lob
	private byte[] privateKeyBinaryFile;
	
	@NotBlank
	@Column(name = "PUBLIC_KEY_NAME")
	@Size(max = 25)
	private String publicKeyName;
	
	@NotNull
	@Column(name = "PUBLIC_KEY_BINARY_FILE")
	@Lob
	private byte[] publicKeyBinaryFile;

	@Override
	public String toString() {
		return "EncryptionKey [id=" + id + ", privateKeyName=" + privateKeyName + ", privateKeyBinaryFile="
				+ Arrays.toString(privateKeyBinaryFile) + ", publicKeyName=" + publicKeyName + ", publicKeyBinaryFile="
				+ Arrays.toString(publicKeyBinaryFile) + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPrivateKeyName() {
		return privateKeyName;
	}

	public void setPrivateKeyName(String privateKeyName) {
		this.privateKeyName = privateKeyName;
	}

	public byte[] getPrivateKeyBinaryFile() {
		return privateKeyBinaryFile;
	}

	public void setPrivateKeyBinaryFile(byte[] privateKeyBinaryFile) {
		this.privateKeyBinaryFile = privateKeyBinaryFile;
	}

	public String getPublicKeyName() {
		return publicKeyName;
	}

	public void setPublicKeyName(String publicKeyName) {
		this.publicKeyName = publicKeyName;
	}

	public byte[] getPublicKeyBinaryFile() {
		return publicKeyBinaryFile;
	}

	public void setPublicKeyBinaryFile(byte[] publicKeyBinaryFile) {
		this.publicKeyBinaryFile = publicKeyBinaryFile;
	}
}
