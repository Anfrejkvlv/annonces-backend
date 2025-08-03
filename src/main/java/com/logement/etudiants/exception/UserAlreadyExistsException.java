package com.logement.etudiants.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String msg){ super(msg);}
}
