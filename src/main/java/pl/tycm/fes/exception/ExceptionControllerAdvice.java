package pl.tycm.fes.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import pl.tycm.fes.model.StatusResponse;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.transaction.TransactionSystemException;

@ControllerAdvice
public class ExceptionControllerAdvice {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<StatusResponse> exceptionHandler(Exception ex) {
		StatusResponse status = new StatusResponse();
		status.setStatusCode(HttpStatus.BAD_REQUEST.value());
		status.setMessage(ex.getMessage());
		logger.error("StackTrace: ", ex);
		return new ResponseEntity<StatusResponse>(status, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<StatusResponse> transactionSystemException(Exception ex) {
		StatusResponse status = new StatusResponse();
		status.setStatusCode(HttpStatus.BAD_REQUEST.value());
		status.setMessage("Nieprawidłowe parametry.");
		logger.error("StackTrace: ", ex);
		return new ResponseEntity<StatusResponse>(status, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<StatusResponse> constraintViolationException(ConstraintViolationException ex) {
		StatusResponse status = new StatusResponse();
		status.setStatusCode(HttpStatus.BAD_REQUEST.value());
		String errors = "";
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors += (violation.getPropertyPath() + ": " + violation.getMessage() + ", ");
		}
		status.setMessage(errors);
		logger.error("StackTrace: ", ex);
		return new ResponseEntity<StatusResponse>(status, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<StatusResponse> noHandlerFoundException(Exception ex) {
		StatusResponse status = new StatusResponse();
		status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		status.setMessage("Błąd: " + HttpStatus.INTERNAL_SERVER_ERROR + " (Internal Server Error)");
		logger.error("StackTrace: ", ex);
		return new ResponseEntity<StatusResponse>(status, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(TaskConfigNotFoundException.class)
	public ResponseEntity<StatusResponse> taskConfigNotFoundException(TaskConfigNotFoundException ex) {
		StatusResponse status = new StatusResponse();
		status.setStatusCode(HttpStatus.BAD_REQUEST.value());
		status.setMessage("Brak konfiguracji dla tego zadania.");
		logger.error("StackTrace: ", ex);
		return new ResponseEntity<StatusResponse>(status, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ServerConfigNotFoundException.class)
	public ResponseEntity<StatusResponse> serverConfigNotFoundException(ServerConfigNotFoundException ex) {
		StatusResponse status = new StatusResponse();
		status.setStatusCode(HttpStatus.BAD_REQUEST.value());
		status.setMessage("Brak konfiguracji Serwera.");
		logger.error("StackTrace: ", ex);
		return new ResponseEntity<StatusResponse>(status, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(EncryptionKeyNotFoundException.class)
	public ResponseEntity<StatusResponse> encryptionKeyNotFoundException(EncryptionKeyNotFoundException ex) {
		StatusResponse status = new StatusResponse();
		status.setStatusCode(HttpStatus.BAD_REQUEST.value());
		status.setMessage("Podany klucz enkrypcji nie istnieje.");
		logger.error("StackTrace: ", ex);
		return new ResponseEntity<StatusResponse>(status, HttpStatus.BAD_REQUEST);
	}
}
