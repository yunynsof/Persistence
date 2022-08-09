package hn.com.tigo.josm.persistence.partitioning;

import hn.com.tigo.josm.persistence.configuration.dto.ConstraintDetailsErrors;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The DatabaseErrorCodesCache is a class responsible to store the error codes
 * generated for database.
 * 
 * @author Andres Felipe Hinestroza <mailto:afhinestroza@stefanini.com />
 * @version 1.0
 * @since 6/04/2016 05:17:48 PM 2016
 */
public final class DatabaseErrorCodesCache {
	
	/** Attribute that determine databaseErrorCodesCache. */
	private static DatabaseErrorCodesCache databaseErrorCodesCache =  new DatabaseErrorCodesCache() ;

	/** Attribute that determine databaseErrorMap. */
	private Map<String, Map<String, ConstraintDetailsErrors>> databaseErrorMap;
			
	
	/** Attribute that determine date init cache. */
	private Date dateInitCache;
	
	/**
	 * Instantiates a new database error codes cache.
	 */
	private DatabaseErrorCodesCache(){
		databaseErrorMap = new HashMap<String, Map<String,ConstraintDetailsErrors>>();
	}
	
	

	/**
	 * Gets the single instance of DatabaseErrorCodesCache.
	 *
	 * @return single instance of DatabaseErrorCodesCache
	 */
	public static DatabaseErrorCodesCache getInstance(){
		if(databaseErrorCodesCache == null){
			databaseErrorCodesCache = new DatabaseErrorCodesCache();
		}
		return databaseErrorCodesCache;
	}

	/**
	 * Gets the database error map.
	 *
	 * @return the database error map
	 */
	public Map<String, Map<String, ConstraintDetailsErrors>> getDatabaseErrorMap() {
		return databaseErrorMap;
	}
	
	/**
	 * Sets the data base error map from (probably) the configuration file.
	 * 
	 * @param databaseErrorMap the databaseErrorMap to set
	 */
	public void setDatabaseErrorMap(
			final Map<String, Map<String, ConstraintDetailsErrors>> databaseErrorMap) {
		this.databaseErrorMap = databaseErrorMap;
	}

	/**
	 * Gets the date init cache.
	 *
	 * @return the date init cache
	 */
	public Date getDateInitCache() {
		return dateInitCache;
	}

	/**
	 * Sets the date init cache.
	 *
	 * @param dateInitCache the new date init cache
	 */
	public void setDateInitCache(final Date dateInitCache) {
		this.dateInitCache = dateInitCache;
	}
	
	
	
}
