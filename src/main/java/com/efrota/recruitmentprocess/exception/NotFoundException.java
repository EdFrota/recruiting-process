package com.efrota.recruitmentprocess.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom {@link RuntimeException} to handle not found content
 * <code>HttpStatus.NOT_FOUND</code>.
 * 
 * @author edmundofrota
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@SuppressWarnings("serial")
public class NotFoundException extends RuntimeException {

	public NotFoundException(String message) {
		super(message);
	}
}
