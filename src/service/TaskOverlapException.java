package service;

public class TaskOverlapException extends RuntimeException {

    public TaskOverlapException(String message) {
        super(message);
    }
    public TaskOverlapException(String message, Throwable cause) {
        super(message, cause);
    }
}