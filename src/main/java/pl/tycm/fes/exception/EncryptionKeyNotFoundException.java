package pl.tycm.fes.exception;

public class EncryptionKeyNotFoundException extends Exception {

	private static final long serialVersionUID = 1949142647174428694L;
	
	public EncryptionKeyNotFoundException(long id) {
		super("EncryptionKeyNotFoundException with id=" + id);
	}
	
	public EncryptionKeyNotFoundException() {
		super("EncryptionKeyNotFoundException");
	}
}
