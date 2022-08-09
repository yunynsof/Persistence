package hn.com.tigo.josm.persistence.cache;

import hn.com.tigo.josm.common.configuration.dto.Partitioning;


import java.util.Hashtable;
import java.util.Map;

/**
 * PartitionDetail.
 *
 * @author Harold Castillo
 * @version 1.0
 * @since 09-05-2016 12:53:31 PM
 */
public class PartitionDetail {
	
	/** Attribute that determine partitioning object. */
	private Partitioning partitioning;
	
	/** Attribute that determine the information partition map. */
	private Map<Long, String> partitionMap;
	
	/** Attribute that has the count of the DS that will be use. */
	private long counter;
	
	/**
	 * Instantiates a new detail partitioning cache.
	 */
	public PartitionDetail() {
		partitionMap = new Hashtable<Long, String>();		
	}
	
	/**
	 * Gets the next data source.
	 *
	 * @return the next data
	 */
	public  String getNextDatasource() {

		String dataSource = null;
			
		if ((counter + 1) > partitionMap.size()) {
			counter = 0;
		} 
		
		final long position = counter % partitionMap.size();
		dataSource = partitionMap.get(position);
		counter++;
		
		return dataSource;
	}

	/**
	 * Gets the partitioning.
	 *
	 * @return the partitioning
	 */
	public Partitioning getPartitioning() {
		return partitioning;
	}

	/**
	 * Sets the partitioning.
	 *
	 * @param partitioning
	 *            the new partitioning
	 */
	public void setPartitioning(final Partitioning partitioning) {
		this.partitioning = partitioning;
	}

	/**
	 * @return the partitionMap
	 */
	public Map<Long, String> getPartitionMap() {
		return partitionMap;
	}

	

}
