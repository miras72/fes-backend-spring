package pl.tycm.fes.model;

public class DecryptionKeyDTO {

	private Long id;
	private String decryptionKeyName;
	
	public DecryptionKeyDTO(Long id, String decryptionKeyName) {
		this.id = id;
		this.decryptionKeyName = decryptionKeyName;
	}

	@Override
	public String toString() {
		return "DecryptionKeyDTO [id=" + id + ", decryptionKeyName=" + decryptionKeyName + "]";
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
}
