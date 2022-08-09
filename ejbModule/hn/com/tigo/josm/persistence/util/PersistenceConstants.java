/**
 * PersistenceConstants.java
 * Persistence
 * Copyright (c) Tigo Honduras.
 */
package hn.com.tigo.josm.persistence.util;

/**
 * PersistenceConstants.
 * 
 * Class that define the constants of the persistence project.
 *
 * @author Harold Castillo
 * @version 1.0
 * @since 13/05/2015 11:06:46
 */
public final class PersistenceConstants {

	/** Attribute that determine a the path of the configuration folder. */
	public static final String CONFIGURATION_PATH = "hn.com.tigo.persistence.config.%s";

	/** Constant that determine the message for sentences executed. */
	public static final String EXECUTE_SQL = "The sentence has been executed: %s";

	/** Constant that determine the partition path configuration. */
	public static final String PARTITIONS_PATH = "hn.com.tigo.persistence.partitions.%s";
	
	/** Constant that determine the partition path configuration. */
	public static final String ERROR_PATH = "hn.com.tigo.persistence.config.%sError";

	/** Constant that determine the expiration time cache. */
	public static final int EXPIRATION_TIME_CACHE = 86400000;

	/**
	 * Private constructor to prevent instantiation.
	 */
	private PersistenceConstants() {

	}

}
