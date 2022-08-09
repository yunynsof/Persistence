package hn.com.tigo.josm.persistence.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hn.com.tigo.josm.common.interfaces.ConfigurationJosmRemote;
import hn.com.tigo.josm.common.interfaces.producer.InterfaceFactory;
import hn.com.tigo.josm.persistence.exception.PersistenceException;
import hn.com.tigo.josm.persistence.partitioning.FactoryConnection;

import java.util.List;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoadPartitionTest {
	
	/**
	 * This attribute will store an instance of log4j for PersistenceConstants
	 * class.
	 */
	private static final Logger LOGGER = Logger.getLogger(LoadPartitionTest.class);

	private static ConfigurationJosmRemote configurationJosmRemote;

	private static EJBContainer ejbContainer;
	
	public static final String VERSION = "";
	
	private static FactoryConnection serviceSession;

	public static final String COMMON_CONFIGURATION_REMOTE = "java:global/Common-Configuration"+ VERSION + "/ConfigurationJosm!hn.com.tigo.josm.common.interfaces.ConfigurationJosmRemote";

	public static final String SERVICE_SESSION = "java:global/Persistence/FactoryConnection";
	
	@BeforeClass
	public static void setUpBeforeClass() throws NamingException {
		InterfaceFactory.COMMON_CONFIGURATION_REMOTE = COMMON_CONFIGURATION_REMOTE;
		ejbContainer = EJBContainer.createEJBContainer();
		configurationJosmRemote = (ConfigurationJosmRemote) ejbContainer.getContext().lookup(COMMON_CONFIGURATION_REMOTE);
		assertTrue(configurationJosmRemote != null);
		serviceSession = (FactoryConnection) ejbContainer.getContext().lookup(SERVICE_SESSION);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (ejbContainer != null) {
			ejbContainer.close();
		}
	}
	
	@Test
	public void test() {

		final String[] datasources = { "DosPolicy", "GatewayLog", "JOSM",
				"MasterStatus", "Promotion", "PromotionEngine", "Scheduler",
				"SubscriberACLPolicy", "Subscription" };
		try {

			for (int i = 0; i < datasources.length; i++) {
				List<String> list;

				list = serviceSession.getDataSourceList(datasources[i]);
				LOGGER.info(datasources[i]);
				
				for (String string : list) {
					LOGGER.info(string);
				}

				assertTrue(list != null);
			}

		} catch (PersistenceException e) {
			LOGGER.error(e.getMessage());
			fail();
		}
	}

}
