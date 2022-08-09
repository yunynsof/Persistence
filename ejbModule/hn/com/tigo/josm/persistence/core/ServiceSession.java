/**
 * ServiceSession.java
 * Persistence
 * Copyright (c) Tigo Honduras.
 */
package hn.com.tigo.josm.persistence.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import hn.com.tigo.josm.common.locator.ServiceLocator;
import hn.com.tigo.josm.common.locator.ServiceLocatorException;
import hn.com.tigo.josm.persistence.cache.PartitionDetailCache;
import hn.com.tigo.josm.persistence.exception.PersistenceException;
import hn.com.tigo.josm.persistence.interfaces.ServiceSessionRemote;
import hn.com.tigo.josm.persistence.partitioning.FactoryConnection;
import hn.com.tigo.josm.persistence.util.PersistenceConfigurationUtil;

/**
 * ServiceSession.
 * 
 * Class that allows to create database connections.
 * 
 * @author Harold Castillo
 * @version 1.1
 * @since 27/03/2015 15:45:56
 */
@Stateless
@LocalBean
public class ServiceSession implements ServiceSessionRemote {

	/**
	 * Attribute that determine the factory connection.
	 */
	@EJB
	private FactoryConnection factoryConnection;

	/**
	 * Instantiates a new service session.
	 */
	public ServiceSession() {

	}

	/**
	 * Gets the session with data source associated directly without Round-Robin
	 * algorithm; useful when you have only one database instance.
	 * 
	 * @param dataSource
	 *            the data source name
	 * @param configFileName
	 *            the configuration file name
	 * @return the {@link SessionBase} instance
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public SessionBase getSessionDataSource(final String dataSource, final String configFileName)
			throws PersistenceException {

		SessionBase sessionBase;

		final SessionContainer sessionContainer = SessionContainer.getInstance();

		if (!sessionContainer.getDataSourceMap().containsKey(dataSource)) {
			final DataSource ds = this.createDataSource(dataSource, configFileName);
			sessionBase = new SessionBase(ds);
			sessionContainer.getDataSourceMap().put(dataSource, ds);
		} else {
			sessionBase = new SessionBase(sessionContainer.getDataSourceMap().get(dataSource));
		}

		return sessionBase;
	}

	/**
	 * Gets the session by a partition id.
	 *
	 * @param id
	 *            allows to partitioning between the different instances.
	 * @param partitionName
	 *            the partition name
	 * @return the {@link SessionBase} instance
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public SessionBase getSessionBase(final long id, final String partitionName) throws PersistenceException {

		final String dataSource = factoryConnection.getDataSource(id, partitionName);
		final SessionBase sessionBase = getSessionDataSource(dataSource, partitionName);

		return sessionBase;
	}

	/**
	 * Gets the session with multiple partition of the data base. It also loads
	 * the data source using round robin method.
	 * 
	 * @param partitionName
	 *            the partition name
	 * @return the {@link SessionBase} instance
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public SessionBase getSessionBase(final String partitionName) throws PersistenceException {

		final String dataSource = factoryConnection.getDataSource(partitionName);
		final SessionBase sessionBase = getSessionDataSource(dataSource, partitionName);

		return sessionBase;
	}

	/**
	 * Method responsible to gets the SessionBase list. It loads the map in the
	 * cache for the error codes.
	 * 
	 * @param partitionName
	 *            the partition name
	 * @return the {@link SessionBase} list instances
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public List<SessionBase> getSessionBaseList(final String partitionName) throws PersistenceException {

		final List<SessionBase> sessionList = new ArrayList<>();
		final List<String> dataSources = factoryConnection.getDataSourceList(partitionName);

		for (String dataSource : dataSources) {
			sessionList.add(this.getSessionDataSource(dataSource, partitionName));
		}

		return sessionList;
	}

	/**
	 * Method that allow creates the connection.
	 *
	 * @param dataSourceName
	 *            the data source name
	 * @param configFileName
	 *            the configuration file name
	 * @return the data source
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	private DataSource createDataSource(final String dataSourceName, final String configFileName)
			throws PersistenceException {

		DataSource dataSource = null;

		try {
			final ServiceLocator serviceLocator = ServiceLocator.getInstance();
			dataSource = serviceLocator.getService(dataSourceName,
					PersistenceConfigurationUtil.getInitialContext(configFileName));
		} catch (ServiceLocatorException e) {
			throw new PersistenceException(e.getMessage(), e);
		}

		return dataSource;
	}

	/**
	 * Reset session base configuration.
	 *
	 * @param partitionName
	 *            the partition name
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public void resetSessionBaseConfiguration(final String partitionName) throws PersistenceException {
		final Map<String, PartitionDetailCache> partionMap = factoryConnection.getPartitionCache()
				.getPartitionDetailCacheMap();
		if (partionMap.containsKey(partitionName)) {
			factoryConnection.getPartitionCache().getPartitionDetailCacheMap().remove(partitionName);
		}
		
		final SessionContainer sessionContainer = SessionContainer.getInstance();
		List<String> dataSources;
		try {
			dataSources = factoryConnection.getDataSourceList(partitionName);

			for (String dataSource : dataSources) {
				sessionContainer.getDataSourceMap().remove(dataSource);
			}

		} catch (PersistenceException e) {
			throw new PersistenceException(e.getMessage(), e);
		}

	}

}
