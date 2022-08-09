/**
 * PersistenceError.java
 * Persistence
 * Copyright (c) Tigo Honduras.
 */
package hn.com.tigo.josm.persistence.exception;

/**
 * PersistenceError.
 * 
 * Enum error for the persistence.
 *
 * @author Harold Castillo
 * @version 1.0
 * @since 25/03/2015 11:11:33 AM
 */
public enum PersistenceError {
	
	/** Attributes that determine the data of the enum. */
	SESSION(1,"Failed to create session."),
	
	/** Attribute that determine NOCOMMIT. */
	NOCOMMIT(2,"Could not commit transaction."),
	
	/** Attribute that determine NOROLLBACK. */
	NOROLLBACK(3,"Could not rollback the transaction."),
	
	/** Attribute that determine INIT. */
	INIT(4,"Could not create the instance for the base data type specified."),
	
	/** Attribute that determine SQL. */
	SQL(5,"Error accessing the database when the SQL statement is executed."),
	
	/** Attribute that determine NOTCLOSED. */
	NOTCLOSED(6,"Error closing connection"),
	
	/** Attribute that determine NAMING. */
	NAMING(7,"Unresolved JNDI name."),
	
	/** Attribute that determine INITENTITYBASE. */
	INITENTITYBASE(8,"Failed to initialized the entity."),
	
	/** Attribute that determine the the configuration mistake cache. */
	CONFIG_CACHE(9,"Failed to load the configuration, %s."),
	
	/** Attribute that determine the the configuration mistake cache. */
	ERROR_CACHE(10,"Failed to load the ErrorMapping, %s.");

	/** Attribute that determine the error code. */
	private final int errorCode;

	/** Attribute that determine the message. */
	private final String message;

	/**
	 * Instantiates a new persistence error.
	 *
	 * @param errorCode
	 *            the error code
	 * @param message
	 *            the message
	 */
	private PersistenceError(final int errorCode, final String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return String.format("Persistence-error-%d: %s", errorCode, message);
	}

}