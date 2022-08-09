package hn.com.tigo.josm.persistence.cache;

import hn.com.tigo.josm.common.cache.Cache;
import hn.com.tigo.josm.common.configuration.dto.ConstraintDetails;
import hn.com.tigo.josm.common.configuration.dto.ConstraintDetailsErrors;
import hn.com.tigo.josm.common.configuration.dto.DataBaseErrorMappingMap;
import hn.com.tigo.josm.common.configuration.dto.DatabaseErrors;
import hn.com.tigo.josm.common.configuration.dto.ResponseJOSM;
import hn.com.tigo.josm.common.interfaces.ConfigurationJosmRemote;
import hn.com.tigo.josm.common.locator.ServiceLocatorException;
import hn.com.tigo.josm.persistence.exception.PersistenceError;
import hn.com.tigo.josm.persistence.exception.PersistenceException;
import hn.com.tigo.josm.persistence.util.PersistenceConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

/**
 * The class DataBaseErrorsDetailCache contains the <Usage of this class> for
 * Persistence project.
 *
 * @author --
 * @version 1.0.0
 * @since Aug 24, 2017
 */
public class DataBaseErrorsDetailCache extends Cache<DataBaseErrorsDetail, PersistenceException> {

	/**
	 * This attribute will store an instance of log4j for PartitionDetailCache
	 * class.
	 */
	private static final Logger LOGGER = Logger.getLogger(DataBaseErrorsDetailCache.class);

	/**
	 * Instantiates a new data base errors detail cache.
	 *
	 * @param partitionName
	 *            the partition name
	 */
	public DataBaseErrorsDetailCache(final String partitionName) {
		super.path = String.format(PersistenceConstants.ERROR_PATH, partitionName);
		super.expiration = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hn.com.tigo.josm.common.cache.Cache#loadData()
	 */
	@Override
	protected DataBaseErrorsDetail loadData() throws PersistenceException {

		DataBaseErrorsDetail errorDetail = null;

		try {
			errorDetail = new DataBaseErrorsDetail();
			final ConfigurationJosmRemote configurationJosmRemote = super.getConfiguration();
			final ResponseJOSM response = configurationJosmRemote.getConfiguration(super.path);
			final DataBaseErrorMappingMap errorMapConfig = response.getConfigurations().getDatabaseErrorMapType();
			errorDetail.setDatabaseErrorMap(this.createErrorCodesMap(errorMapConfig));
			super.expiration = errorMapConfig.getTimeout();
		} catch (JAXBException | IOException | ServiceLocatorException e) {
			LOGGER.warn(e.getMessage(), e);
			throw new PersistenceException(String.format(PersistenceError.CONFIG_CACHE.getMessage(), e.getMessage()));
		}

		return errorDetail;
	}

	/**
	 * Creates the error codes map.
	 *
	 * @param errorMap
	 *            the error map
	 * @return the map
	 */
	private Map<String, Map<String, ConstraintDetailsErrors>> createErrorCodesMap(
			final DataBaseErrorMappingMap errorMap) {
		final Map<String, Map<String, ConstraintDetailsErrors>> errorCodeMap = new HashMap<String, Map<String, ConstraintDetailsErrors>>();

		for (DatabaseErrors error : errorMap.getDatabaseErrorNode()) {
			final Map<String, ConstraintDetailsErrors> internalMap = new HashMap<String, ConstraintDetailsErrors>();
			for (ConstraintDetails contraint : error.getConstraints()) {
				internalMap.put(contraint.getConstraintName(), contraint.getErrorMessage());
			}

			errorCodeMap.put(error.getErrorCode(), internalMap);
		}

		return errorCodeMap;
	}

}
