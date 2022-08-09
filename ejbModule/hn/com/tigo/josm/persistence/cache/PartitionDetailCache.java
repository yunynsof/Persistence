package hn.com.tigo.josm.persistence.cache;

import hn.com.tigo.josm.common.cache.Cache;
import hn.com.tigo.josm.common.configuration.dto.Partition;
import hn.com.tigo.josm.common.configuration.dto.ResponseJOSM;
import hn.com.tigo.josm.common.interfaces.ConfigurationJosmRemote;
import hn.com.tigo.josm.common.locator.ServiceLocatorException;
import hn.com.tigo.josm.persistence.exception.PersistenceError;
import hn.com.tigo.josm.persistence.exception.PersistenceException;
import hn.com.tigo.josm.persistence.util.PersistenceConstants;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

/**
 * PartitionDetailCache.
 *
 * @author Harold Castillo
 * @version 1.0
 * @since 09-05-2016 12:53:14 PM
 */
public class PartitionDetailCache extends
		Cache<PartitionDetail, PersistenceException> {

	/**
	 * This attribute will store an instance of log4j for PartitionDetailCache
	 * class.
	 */
	private static final Logger LOGGER = Logger.getLogger(PartitionDetailCache.class);

	/**
	 * Instantiates a new partition detail cache.
	 *
	 * @param partitionName
	 *            the partition name
	 */
	public PartitionDetailCache(final String partitionName) {
		super.path = String.format(PersistenceConstants.PARTITIONS_PATH, partitionName);
		super.expiration = PersistenceConstants.EXPIRATION_TIME_CACHE;
	}

	/* (non-Javadoc)
	 * @see hn.com.tigo.josm.common.cache.Cache#loadData()
	 */
	@Override
	protected PartitionDetail loadData() throws PersistenceException {

		PartitionDetail partitionDetail = null;

		try {

			final ConfigurationJosmRemote configurationJosmRemote = getConfiguration();
			final ResponseJOSM response = configurationJosmRemote.getConfiguration(super.path);
			partitionDetail = new PartitionDetail();
			partitionDetail.setPartitioning(response.getConfigurations().getPartitioningType());
			final List<Partition> partitionList = partitionDetail.getPartitioning().getComplexPartitioning().getPartition();
			for (Partition partition : partitionList) {
				partitionDetail.getPartitionMap().put(partition.getPosition(),
						partition.getName());
			}

		} catch (JAXBException | IOException | ServiceLocatorException e) {
			LOGGER.info(e.getMessage(), e);
			throw new PersistenceException(String.format(
					PersistenceError.CONFIG_CACHE.getMessage(), e.getMessage()));
		}

		return partitionDetail;
	}

}
