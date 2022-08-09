/**
 * PersitenceTest.java
 * Persistence
 * Copyright (c) Tigo Honduras.
 */
package hn.com.tigo.josm.persistence.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hn.com.tigo.josm.common.interfaces.producer.InterfaceFactory;
import hn.com.tigo.josm.common.util.EnumTest;
import hn.com.tigo.josm.persistence.core.ServiceSession;
import hn.com.tigo.josm.persistence.core.SessionBase;
import hn.com.tigo.josm.persistence.exception.PersistenceException;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * PersitenceTest.
 *
 * @author Harold Castillo
 * @version 1.0
 * @since 23/03/2015 17:10:26
 */
public class PersitenceTest {

	private static final String INITIAL_CONTEXT = "org.apache.openejb.client.LocalInitialContextFactory";
	
	private static final String COMMON_CONFIGURATION_REMOTE = "java:global/Common-Configuration/ConfigurationJosm!hn.com.tigo.josm.common.interfaces.ConfigurationJosmRemote";
	
	private static final String SERVICE_SESSION = "java:global/Persistence/ServiceSession";

	/**
	 * This attribute will store an instance of log4j for PersistenceConstants
	 * class.
	 */
	private static final Logger LOGGER = Logger.getLogger(PersitenceTest.class);

	/** Attribute that determine the ejb container. */
	private static EJBContainer ejbContainer;

	/** Attribute that determine the ServiceSession instance. */
	private static ServiceSession serviceSession;
	
	/**
	 * Instantiates a new persitence test.
	 */
	public PersitenceTest(){
		
	}

	/**
	 * @throws NamingException 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws NamingException {
		
		final Properties prop = new Properties();
		prop.put(EnumTest.P_MS_JPA.getKey(), EnumTest.P_MS_JPA.getValue());
		prop.put(EnumTest.P_MS_JPA_JDBC_DRIVER.getKey(), EnumTest.P_MS_JPA_JDBC_DRIVER.getValue());
		prop.put(EnumTest.P_MS_JPA_JDBC_URL.getKey(), EnumTest.P_MS_JPA_JDBC_URL.getValue());
		prop.put(EnumTest.P_MS_JPA_USER_NAME.getKey(), EnumTest.P_MS_JPA_USER_NAME.getValue());
		prop.put(EnumTest.P_MS_JPA_PASSWORD.getKey(), EnumTest.P_MS_JPA_PASSWORD.getValue());
		prop.put(EnumTest.P_MS_JPA_JTA_MANAGED.getKey(), EnumTest.P_MS_JPA_JTA_MANAGED.getValue());
		prop.put(EnumTest.P_OPENEJB_EMBEDDED_REMOTABLE.getKey(),EnumTest.P_OPENEJB_EMBEDDED_REMOTABLE.getValue());
		prop.put("java.naming.factory.initial", INITIAL_CONTEXT);
		
		InterfaceFactory.COMMON_CONFIGURATION_REMOTE = COMMON_CONFIGURATION_REMOTE;

		ejbContainer = EJBContainer.createEJBContainer(prop);

		serviceSession = (ServiceSession) ejbContainer.getContext().lookup(SERVICE_SESSION);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (ejbContainer != null) {
			ejbContainer.close();
		}
	}

	@Test
	public void getSessionBase() {

		SessionBase sessionBase = null;

		try {
			List<SessionBase> sessionList = serviceSession.getSessionBaseList("MasterStatus");
			assertTrue(sessionList.size() > 0);
			sessionBase = serviceSession.getSessionBase("MasterStatus");
			assertTrue(sessionBase.getDataSource().getConnection().getMetaData().getUserName().equals("MASTER_STATUS"));
			sessionBase = serviceSession.getSessionBase(99569702, "MasterStatus");
			assertTrue(sessionBase.getVendorDataBase().equals("Oracle"));
			sessionBase = serviceSession.getSessionBase("MasterStatus");
		} catch (PersistenceException | SQLException e) {
			LOGGER.error(e.getMessage(), e);
			fail();
		}

	}

}
