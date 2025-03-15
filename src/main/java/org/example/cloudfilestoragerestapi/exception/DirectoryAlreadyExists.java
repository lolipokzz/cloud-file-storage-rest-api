package org.example.cloudfilestoragerestapi.exception;

public class DirectoryAlreadyExists extends RuntimeException {
    public DirectoryAlreadyExists(String message) {
        super(message);
    }
}
