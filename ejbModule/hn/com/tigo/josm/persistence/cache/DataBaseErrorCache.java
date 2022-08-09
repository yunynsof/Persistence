package hn.com.tigo.josm.persistence.cache;

/**
 * The class DataBaseErrorCache contains the <Usage of this class> for
 * Persistence project.
 *
 * @author Saul Villasenor
 * @version 1.0
 * @since Sep 8, 2016 7:17:58 PM
 */
public class DataBaseErrorCache {

	/** The error cache. */
	private DataBaseErrorsDetailCache errorCache;

	/** The database error cache. */
	private static DataBaseErrorCache databaseErrorCache = new DataBaseErrorCache();

	/**
	 * Instantiates a new data base error cache.
	 */
	private DataBaseErrorCache() {
	}

	/**
	 * Gets the single instance of DataBaseErrorCache.
	 *
	 * @return single instance of DataBaseErrorCache
	 */
	public static DataBaseErrorCache getInstance() {
		return databaseErrorCache;
	}

	/**
	 * Gets the error cache.
	 *
	 * @return the error cache
	 */
	public DataBaseErrorsDetailCache getErrorCache() {
		return errorCache;
	}

	/**
	 * Initialize errors.
	 *
	 * @param partitionName
	 *            the partition name
	 */
	public void initializeErrors(final String partitionName) {
		if (errorCache == null) {
			errorCache = new DataBaseErrorsDetailCache(partitionName);
		}
	}

}
