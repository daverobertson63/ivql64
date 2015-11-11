package com.roche.veeva;


/**
 * This is a simple Exception class defintion
 *
 * @author Dave
 * @version 
 */
public class VeevaException extends RuntimeException {
    /** Serialization ID */
    private static final long serialVersionUID = 0;

    /**
     * Constructs a VeevaException with an explanatory message.
     *
     * @param message
     *            Detail about the reason for the exception.
     */
    public VeevaException(final String message) {
        super(message);
    }

    /**
     * Constructs a VeevaException with an explanatory message and cause.
     * 
     * @param message
     *            Detail about the reason for the exception.
     * @param cause
     *            The cause.
     */
    public VeevaException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new VeevaException with the specified cause.
     * 
     * @param cause
     *            The cause.
     */
    public VeevaException(final Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
