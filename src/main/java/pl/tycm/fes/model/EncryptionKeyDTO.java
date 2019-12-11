package pl.tycm.fes.model;

public class EncryptionKeyDTO {

	private Long id;
	private String privateKeyName;
	private String publicKeyName;

	public EncryptionKeyDTO(Long id, String privateKeyName, String publicKeyName) {
		this.id = id;
		this.privateKeyName = privateKeyName;
		this.publicKeyName = publicKeyName;
	}

	@Override
	public String toString() {
		return "EncryptionKeyDTO [id=" + id + ", privateKeyName=" + privateKeyName + ", publicKeyName=" + publicKeyName
				+ "]";
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

	public String getPublicKeyName() {
		return publicKeyName;
	}

	public void setPublicKeyName(String publicKeyName) {
		this.publicKeyName = publicKeyName;
	}
}
