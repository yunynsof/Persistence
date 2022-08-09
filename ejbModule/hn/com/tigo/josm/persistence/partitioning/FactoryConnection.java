/**
 * FactoryConnection.java
 * Persistence
 * Copyright (c) Tigo Honduras.
 */
package hn.com.tigo.josm.persistence.partitioning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import hn.com.tigo.josm.common.configuration.dto.Partitioning;
import hn.com.tigo.josm.persistence.cache.DataBaseErrorCache;
import hn.com.tigo.josm.persistence.cache.PartitionCache;
import hn.com.tigo.josm.persistence.cache.PartitionDetail;
import hn.com.tigo.josm.persistence.cache.PartitionDetailCache;
import hn.com.tigo.josm.persistence.exception.PersistenceException;

/**
 * Class that allow to manage the database connections.
 * 
 * @author Harold Castillo
 * @version 1.0
 * @since 4/05/2015 8:56:32
 */
@Stateless
@LocalBean
public class FactoryConnection {

	/**
	 * Attribute that determine the partitioning cache.
	 */
	private final PartitionCache partitionCache = PartitionCache.getInstance();

	/**
	 * Attribute that determine the error mapping cache.
	 */
	private final DataBaseErrorCache errorCache = DataBaseErrorCache.getInstance();

	/**
	 * Instantiates a new factory connection.
	 */
	public FactoryConnection() {

	}

	/**
	 * Method responsible to gets the data source.
	 *
	 * @param id
	 *            the id
	 * @param partitionName
	 *            the partition name
	 * @return the data source
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public String getDataSource(final long id, final String partitionName) throws PersistenceException {

		final PartitionDetail detailPartitioningCache = getPartitionDetail(partitionName);
		final Partitioning partitioning = detailPartitioningCache.getPartitioning();
		final long residue = id % partitioning.getMod();

		errorCache.initializeErrors(partitionName);

		return detailPartitioningCache.getPartitionMap().get(residue);
	}

	/**
	 * Gets the data source using round robin method.
	 *
	 * @param partitionName
	 *            the partitioning name
	 * @return the data source
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public String getDataSource(final String partitionName) throws PersistenceException {

		final PartitionDetail partitionDetail = getPartitionDetail(partitionName);
		final String dataSource = partitionDetail.getNextDatasource();

		errorCache.initializeErrors(partitionName);

		return dataSource;
	}

	/**
	 * Method responsible to gets the partitioning dataSource list.
	 *
	 * @param partitioningName
	 *            the partitioning name
	 * @return the data source list
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public List<String> getDataSourceList(final String partitioningName) throws PersistenceException {

		final List<String> dataSourceList = new ArrayList<>();

		final Map<Long, String> detailPartitioningCache = getPartitionDetail(partitioningName).getPartitionMap();
		long i = 0L;
		while (detailPartitioningCache.containsKey(i)) {

			final String dataSource = detailPartitioningCache.get(i);
			dataSourceList.add(dataSource);
			i++;

		}

		errorCache.initializeErrors(partitioningName);

		return dataSourceList;
	}

	/**
	 * Method responsible to gets the partitioning detail.
	 *
	 * @param partitionName
	 *            the partition name
	 * @return the partition detail
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	private PartitionDetail getPartitionDetail(final String partitionName) throws PersistenceException {

		if (!partitionCache.getPartitionDetailCacheMap().containsKey(partitionName)) {
			final PartitionDetailCache partitionDetailCache = new PartitionDetailCache(partitionName);
			partitionCache.getPartitionDetailCacheMap().put(partitionName, partitionDetailCache);
		}

		return partitionCache.getPartitionDetailCacheMap().get(partitionName).retrieve();
	}

	/**
	 * Gets the partition cache.
	 *
	 * @return the partition cache
	 */
	public PartitionCache getPartitionCache() {
		return partitionCache;
	}

}
