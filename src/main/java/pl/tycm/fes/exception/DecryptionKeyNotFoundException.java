package pl.tycm.fes.exception;

public class DecryptionKeyNotFoundException extends Exception {

	private static final long serialVersionUID = 7714515117937865190L;

	public DecryptionKeyNotFoundException(long id) {
		super("DecryptionKeyNotFoundException with id=" + id);
	}
	
	public DecryptionKeyNotFoundException() {
		super("DecryptionKeyNotFoundException");
	}
}
