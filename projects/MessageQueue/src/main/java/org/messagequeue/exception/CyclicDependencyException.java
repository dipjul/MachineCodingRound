package org.messagequeue.exception;

public class CyclicDependencyException extends Exception {
    public CyclicDependencyException(String message) {
        super(message);
    }
}