package hn.com.tigo.josm.persistence.cache;

import java.util.Hashtable;
import java.util.Map;

/**
 * PartitionCache.
 *
 * @author Harold Castillo
 * @version 1.0
 * @since 09-05-2016 09:56:02 AM
 */
public class PartitionCache {

	private Map<String, PartitionDetailCache> partitionDetailCacheMap;

	private static PartitionCache partitionCache = new PartitionCache();

	private PartitionCache() {
		partitionDetailCacheMap = new Hashtable<>();
	}

	public static PartitionCache getInstance() {
		return partitionCache;
	}

	public Map<String, PartitionDetailCache> getPartitionDetailCacheMap() {
		return partitionDetailCacheMap;
	}

}
