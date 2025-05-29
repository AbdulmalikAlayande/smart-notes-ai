package app.bola.smartnotesai.common.exception;

public class InvalidRequestException extends RuntimeException{
	
	public InvalidRequestException(Throwable exception) {
		super(exception);
	}
	
	public InvalidRequestException(String message) {
		super(message);
	}
}
