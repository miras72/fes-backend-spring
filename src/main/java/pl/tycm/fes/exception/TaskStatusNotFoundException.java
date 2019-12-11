package pl.tycm.fes.exception;

public class TaskStatusNotFoundException extends Exception {

	private static final long serialVersionUID = 7714515117937865190L;

	public TaskStatusNotFoundException(long id) {
		super("TaskStatusKeyNotFoundException with id=" + id);
	}
	
	public TaskStatusNotFoundException() {
		super("TaskStatusKeyNotFoundException");
	}
}
