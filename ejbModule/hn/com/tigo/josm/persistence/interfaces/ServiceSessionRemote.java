package hn.com.tigo.josm.persistence.interfaces;

import javax.ejb.Remote;

import hn.com.tigo.josm.persistence.exception.PersistenceException;

/**
 * The interface ServiceSessionRemote contains the method that resets the
 * configuration for Common project.
 *
 * @author Gary Gonzalez Zepeda <mailto:ggzepeda@stefanini.com/>
 * @version 1.0.0
 * @since Apr 3, 2017
 */
@Remote
public interface ServiceSessionRemote {

	/**
	 * Reset session base configuration.
	 *
	 * @param partitionName
	 *            the partition name
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	void resetSessionBaseConfiguration(final String partitionName) throws PersistenceException;
	
}
