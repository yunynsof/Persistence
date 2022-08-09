/**
 * PersistanceSqlMessageBuilder.java
 * Persistence
 * Copyright (c) Tigo Honduras.
 */
package hn.com.tigo.josm.persistence.util;

import hn.com.tigo.josm.common.configuration.dto.ConstraintDetailsErrors;
import hn.com.tigo.josm.persistence.cache.DataBaseErrorCache;
import hn.com.tigo.josm.persistence.exception.PersistenceError;
import hn.com.tigo.josm.persistence.exception.PersistenceException;

import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * @author Saul Villasenor
 * @version 1.0
 * @since Sep 8, 2016 7:42:21 PM
 */
public final class PersistenceSqlMessageBuilder {

	/**
	 * This attribute will store an instance of log4j for
	 * PersistenceSqlMessageBuilder class.
	 */
	private static final Logger LOGGER = Logger.getLogger(PersistenceSqlMessageBuilder.class);

	/** Attribute that defines an empty string. */
	private static final String EMPTY_STRING = "";

	/** Attribute that defines a single space string. */
	private static final String SPACE = " ";

	/** Attribute that determines the number zero. */
	public static final int ZERO = 0;

	/** Attribute that determines the number one. */
	public static final int ONE = 1;

	/** Attribute that determines a colon string. */
	public static final String COLON = ":";

	/** Attribute that determine a Constant of DIGIT_PATTERN. */
	public static final String DIGIT_PATTERN = "[^0-9]";

	/** Attribute that determine a Constant of EMPTY. */
	public static final String EMPTY = "";

	/**
	 * Attribute that determine a constant of the default constraint of the
	 * error in the error map
	 */
	public static final String DEFAULT_CONSTRAINT = "Default";

	public static final String ERROR_LOADING_MAP = "Error when retrieving the error map";

	/** Attribute that determine _databaseErrorCache. */
	private static final DataBaseErrorCache DATA_BASE_ERROR_CACHE = DataBaseErrorCache.getInstance();

	/**
	 * A private constructor to prevent instantiation.
	 */
	private PersistenceSqlMessageBuilder() {

	}

	/**
	 * create a message loaded from map if found.
	 * 
	 * @param throwable
	 *            error that sent us here.
	 * @param query
	 *            sequel that generated the error.
	 * @return a human readable message or the original message if a human
	 *         readable one fails.
	 * @param vendor
	 *            the vendor of database
	 */
	public static PersistenceException createPersistenceException(final SQLException throwable, final String query) {

		Map<String, Map<String, ConstraintDetailsErrors>> errorCodes = null;
		try {
			if (DATA_BASE_ERROR_CACHE.getErrorCache() == null) {
				DATA_BASE_ERROR_CACHE.initializeErrors("default");
			}
			errorCodes = DATA_BASE_ERROR_CACHE.getErrorCache().retrieve().getDatabaseErrorMap();
		} catch (PersistenceException e) {
			LOGGER.warn(ERROR_LOADING_MAP);
		}

		String code = null;
		String message = null;

		if (throwable.getMessage().contains(COLON)) {
			final String[] messageArray = throwable.getMessage().split(COLON);
			code = messageArray[ZERO];
			message = messageArray[ONE];
			if (errorCodes != null && errorCodes.containsKey(code)) {

				final String errorCode = code.replaceAll(DIGIT_PATTERN, EMPTY);
				String finalErrorCode = PersistenceError.SQL.getErrorCode() + errorCode;

				final Map<String, ConstraintDetailsErrors> constraints = errorCodes.get(code);

				String constraintFromMessage = getConstraint(message);

				if (constraints.containsKey(constraintFromMessage)) {
					message = constraints.get(constraintFromMessage).getConstraintErrorMessage();
					finalErrorCode = finalErrorCode
							.concat(constraints.get(constraintFromMessage).getConstraintErrorCode());
				} else {
					if (constraints.containsKey(DEFAULT_CONSTRAINT)) {
						message = constraints.get(DEFAULT_CONSTRAINT).getConstraintErrorMessage();
						finalErrorCode = finalErrorCode
								.concat(constraints.get(DEFAULT_CONSTRAINT).getConstraintErrorCode());
					}

				}

				return new PersistenceException(Integer.parseInt(finalErrorCode), message, throwable);
			}
		}

		return new PersistenceException(PersistenceError.SQL.getErrorCode(), throwable.getMessage(), throwable);

	}

	/**
	 * Gets the constraint from the sql error message
	 * 
	 * @param message
	 *            the sql error message
	 * @return the constraint name
	 */
	private static String getConstraint(final String message) {
		final int indexStart = message.indexOf('(');
		final int indexEnd = message.indexOf(')') + 1;
		String messageWithSchema = EMPTY_STRING;
		String messageWithoutSchema = EMPTY_STRING;
		if (indexStart > -1 && indexStart < indexEnd) {
			messageWithSchema = SPACE.concat(message.substring(indexStart, indexEnd));
		}

		if (messageWithSchema.compareTo(EMPTY_STRING) != 0) {

			final int dotIndex = messageWithSchema.indexOf('.');

			if (dotIndex > -1) {
				messageWithoutSchema = SPACE.concat(messageWithSchema.substring(dotIndex + 1));
				messageWithoutSchema = SPACE
						.concat(messageWithoutSchema.substring(1, messageWithoutSchema.length() - 1));
			}

		}
		return messageWithoutSchema.trim();
	}

}
