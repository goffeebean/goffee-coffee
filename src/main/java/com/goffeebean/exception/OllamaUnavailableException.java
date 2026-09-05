package com.goffeebean.exception;

/** Thrown when the local Ollama server can't be reached or errors out. */
public class OllamaUnavailableException extends RuntimeException {

    public OllamaUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
