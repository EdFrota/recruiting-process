package com.efrota.recruitmentprocess.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom {@link RuntimeException} to handle not found content
 * <code>HttpStatus.BAD_REQUEST</code>.
 * 
 * @author edmundofrota
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@SuppressWarnings("serial")
public class ServiceValidationException extends RuntimeException {

	public ServiceValidationException(String message) {
		super(message);
	}
}
