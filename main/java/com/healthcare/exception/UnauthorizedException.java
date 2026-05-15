package com.healthcare.exception;

public class UnauthorizedException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3995878812985611978L;

	public UnauthorizedException(String message) {
        super(message);
    }
}
