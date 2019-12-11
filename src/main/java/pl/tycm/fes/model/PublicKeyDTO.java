package pl.tycm.fes.model;

import java.util.Arrays;

public class PublicKeyDTO {

	private Long id;
	private String publicKeyName;
	private byte[] publicKeyBinaryFile;
	
	public PublicKeyDTO(Long id, String publicKeyName, byte[] publicKeyBinaryFile) {
		this.id = id;
		this.publicKeyName = publicKeyName;
		this.publicKeyBinaryFile = publicKeyBinaryFile;
	}

	@Override
	public String toString() {
		return "PublicKeyDTO [id=" + id + ", publicKeyName=" + publicKeyName + ", publicKeyBinaryFile="
				+ Arrays.toString(publicKeyBinaryFile) + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
