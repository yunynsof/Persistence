package hn.com.tigo.josm.persistence.util;

import hn.com.tigo.josm.common.configuration.dto.Configuration;
import hn.com.tigo.josm.common.configuration.dto.ResponseJOSM;
import hn.com.tigo.josm.common.interfaces.ConfigurationJosmRemote;
import hn.com.tigo.josm.common.interfaces.producer.InterfaceFactory;
import hn.com.tigo.josm.common.locator.ServiceLocator;
import hn.com.tigo.josm.common.locator.ServiceLocatorException;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

/**
 * The PersistenceConfigurationUtil is a class responsible to get the configuration from XML File.
 *
 * @author Andres Felipe Hinestroza <mailto:afhinestroza@stefanini.com />
 * @version 1.0
 * @since 6/04/2016 12:21:28 PM 2016
 */
public final class PersistenceConfigurationUtil {

	/**
	 * This attribute will store an instance of log4j for PersistenceConstants
	 * class.
	 */
	private static final Logger LOGGER = Logger.getLogger(PersistenceConstants.class);
	
	/**
	 * Instantiates a new persistence configuration util.
	 */
	private PersistenceConfigurationUtil(){
		
	}
	
	/**
	 * Gets the configuration.
	 *
	 * @return the configuration
	 */
	private static Configuration getConfiguration(final String configFile) {
		
		Configuration configuration = null;

		try {
			final ServiceLocator serviceLocator = ServiceLocator.getInstance();
			final ConfigurationJosmRemote configurationJosmRemote = serviceLocator
					.getService(InterfaceFactory.COMMON_CONFIGURATION_REMOTE);
			final String pathFile = String.format(
					PersistenceConstants.CONFIGURATION_PATH, configFile);
			final ResponseJOSM responseDTO = configurationJosmRemote.getConfiguration(pathFile);
			configuration = responseDTO.getConfigurations().getConfigurationType();
		} catch (ServiceLocatorException | JAXBException | IOException e) {
			LOGGER.warn("Failed to obtain the configurations of the persistence project, file name " + configFile);
		}

		return configuration;
	}

	/**
	 * Gets the initial context.
	 *
	 * @return the initial context
	 */
	public static Properties getInitialContext(final String configFile) {

		Properties parameters = null;

		final Configuration configuration = getConfiguration(configFile);
		if(configuration != null) {
			parameters = new Properties();
			parameters.put(Context.INITIAL_CONTEXT_FACTORY, configuration.getInitialContextFactory());
			parameters.put(Context.PROVIDER_URL, configuration.getProviderUrl());
		}

		return parameters;
	}

}
