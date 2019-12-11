package pl.tycm.fes.model;

import java.util.Arrays;

public class PrivateKeyDTO {

	private Long id;
	private String privateKeyName;
	private byte[] privateKeyBinaryFile;
	
	public PrivateKeyDTO(Long id, String privateKeyName, byte[] privateKeyBinaryFile) {
		this.id = id;
		this.privateKeyName = privateKeyName;
		this.privateKeyBinaryFile = privateKeyBinaryFile;
	}

	@Override
	public String toString() {
		return "PrivateKeyDTO [id=" + id + ", privateKeyName=" + privateKeyName + ", privateKeyBinaryFile="
				+ Arrays.toString(privateKeyBinaryFile) + "]";
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
}
