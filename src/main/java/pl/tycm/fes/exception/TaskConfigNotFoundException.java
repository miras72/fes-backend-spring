package pl.tycm.fes.exception;

public class TaskConfigNotFoundException extends Exception {

	private static final long serialVersionUID = 2975252478080498757L;

	public TaskConfigNotFoundException(long id) {
		super("TaskConfigNotFoundException with id=" + id);
	}

	public TaskConfigNotFoundException() {
		super("TaskConfigNotFoundException");
	}
}
