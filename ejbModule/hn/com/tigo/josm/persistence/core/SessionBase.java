/**
 * SessionBase.java
 * Persistence
 * Copyright (c) Tigo Honduras.
 */
package hn.com.tigo.josm.persistence.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import hn.com.tigo.josm.persistence.exception.PersistenceError;
import hn.com.tigo.josm.persistence.exception.PersistenceException;

/**
 * SessionBase.
 * 
 * Class that allows to encapsulate the connection data and convert the class
 * how a session.
 *
 * @author Harold Castillo
 * @version 1.0
 * @since 25/03/2015 10:57:07 AM
 */
public class SessionBase {

	/** Attribute that determine connection object. */
	private Connection _connection;

	/** Attribute that determine the session data source. */
	private DataSource _dataSource;

	/** Attribute that determine the vendor of data base. */
	private String _vendorDataBase;

	/**
	 * Instantiates a new session base.
	 *
	 * @param dataSource
	 *            the data source
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public SessionBase(final DataSource dataSource) throws PersistenceException {
		try {
			_dataSource = dataSource;
			_connection = _dataSource.getConnection();
			final DatabaseMetaData metadata = _connection.getMetaData();
			_vendorDataBase = metadata.getDatabaseProductName();
		} catch (SQLException e) {
			throw new PersistenceException(PersistenceError.SESSION.getErrorCode(), e.getMessage(), e);
		}
	}

	/**
	 * Method responsible to gets the connection.
	 *
	 * @return the connection
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public Connection getConnection() throws PersistenceException {
		try {
			if (_connection.isClosed()) {
				_connection = this.getDataSource().getConnection();
			}
		} catch (SQLException e) {
			throw new PersistenceException(PersistenceError.SESSION.getErrorCode(), e.getMessage(), e);
		}

		return _connection;
	}

	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	public DataSource getDataSource() {
		return _dataSource;
	}

	/**
	 * Method that allow to close the datasource connection.
	 *
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public void close() throws PersistenceException {
		try {
			if (_connection != null && !_connection.isClosed()) {
				_connection.close();
			}
		} catch (SQLException e) {
			throw new PersistenceException(PersistenceError.NOTCLOSED.getErrorCode(), e.getMessage(), e);
		}
	}

	/**
	 * Sets the auto commit.
	 *
	 * @param commit
	 *            the new auto commit
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public void setAutoCommit(final boolean commit) throws PersistenceException {
		try {
			_connection.setAutoCommit(commit);
		} catch (SQLException e) {
			throw new PersistenceException(PersistenceError.NOCOMMIT.getErrorCode(), e.getMessage(), e);
		}
	}

	/**
	 * Commit.
	 *
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public void commit() throws PersistenceException {
		try {
			_connection.commit();
		} catch (SQLException e) {
			throw new PersistenceException(PersistenceError.NOCOMMIT.getErrorCode(), e.getMessage(), e);
		}
	}

	/**
	 * Rollback.
	 *
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public void rollback() throws PersistenceException {
		try {
			_connection.rollback();
		} catch (SQLException e) {
			throw new PersistenceException(PersistenceError.NOROLLBACK.getErrorCode(), e.getMessage(), e);
		}
	}

	/**
	 * Gets the vendor data base.
	 *
	 * @return the vendor data base
	 */
	public String getVendorDataBase() {
		return _vendorDataBase;
	}

}