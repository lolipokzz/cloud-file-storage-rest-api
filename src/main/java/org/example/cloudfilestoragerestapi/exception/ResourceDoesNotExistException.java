package org.example.cloudfilestoragerestapi.exception;

public class ResourceDoesNotExistException extends RuntimeException {
    public ResourceDoesNotExistException(String message) {
        super(message);
    }
}
