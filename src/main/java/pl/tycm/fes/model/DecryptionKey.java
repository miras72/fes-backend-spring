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
@Table(name = "DECRYPTION_KEY_SPRING")
public class DecryptionKey {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "decryptionKeySeq")
	@SequenceGenerator(name = "decryptionKeySeq", sequenceName = "DECRYPTION_KEY_SPRING_SEQ", allocationSize = 1)
	private Long id;
	
	@NotBlank
	@Column(name = "DECRYPTION_KEY_NAME")
	@Size(max = 25)
	private String decryptionKeyName;
	
	@NotNull
	@Column(name = "DECRYPTION_KEY_BINARY_FILE")
	@Lob
	private byte[] decryptionKeyBinaryFile;

	@Override
	public String toString() {
		return "DecryptionKey [id=" + id + ", decryptionKeyName=" + decryptionKeyName + ", decryptionKeyBinaryFile="
				+ Arrays.toString(decryptionKeyBinaryFile) + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDecryptionKeyName() {
		return decryptionKeyName;
	}

	public void setDecryptionKeyName(String decryptionKeyName) {
		this.decryptionKeyName = decryptionKeyName;
	}

	public byte[] getDecryptionKeyBinaryFile() {
		return decryptionKeyBinaryFile;
	}

	public void setDecryptionKeyBinaryFile(byte[] decryptionKeyBinaryFile) {
		this.decryptionKeyBinaryFile = decryptionKeyBinaryFile;
	}
}
