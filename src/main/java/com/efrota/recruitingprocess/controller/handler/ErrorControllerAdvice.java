package com.efrota.recruitingprocess.controller.handler;

import javax.persistence.PersistenceException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Responsible for handle controller exceptions for response type
 * <code>HttpStatus.INTERNAL_SERVER_ERROR</code>.
 * 
 * List of exceptions handled by: {@link PersistenceException}
 * {@link NullPointerException} {@link DataIntegrityViolationException}
 * 
 * @author edmundofrota
 *
 */
@ControllerAdvice
public class ErrorControllerAdvice {

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ PersistenceException.class, NullPointerException.class, DataIntegrityViolationException.class })
	public void handle() {
		// empty
	}

}
