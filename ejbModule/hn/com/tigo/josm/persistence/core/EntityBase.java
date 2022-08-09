/**
 * EntityBase.java
 * Persistence
 * Copyright (c) Tigo Honduras.
 */
package hn.com.tigo.josm.persistence.core;

import hn.com.tigo.josm.persistence.exception.PersistenceError;
import hn.com.tigo.josm.persistence.exception.PersistenceException;
import hn.com.tigo.josm.persistence.util.PersistenceConstants;
import hn.com.tigo.josm.persistence.util.PersistenceSqlMessageBuilder;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * EntityBase.
 * 
 * Abstract class that define the methods for execute statements sql in all
 * entities created and inherited.
 *
 * @author Harold Castillo
 * @version 1.0
 * @param <E>
 *            the entity type
 * @since 8/05/2015 07:10:31 PM
 */
public abstract class EntityBase<E extends Entity> implements Entity {

	/** This attribute will store an instance of log4j for EntityBase class. */
	private static final Logger LOGGER = Logger.getLogger(EntityBase.class);

	/** Attribute that determine the select statement. */
	protected PreparedStatement _selectStatement;

	/** Attribute that determine the update statement. */
	protected PreparedStatement _updateStatement;

	/** Attribute that determine the result set. */
	protected ResultSet _resultSet;

	/** Attribute that determine the connection. */
	protected Connection _connection;

	/** Attribute that determine the callable statement. */
	protected CallableStatement _callableStatement;

	/**
	 * Instantiates a new entity base.
	 */
	public EntityBase() {

	}

	/**
	 * Instantiates a new entity base.
	 *
	 * @param sessionBase
	 *            the session base
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public EntityBase(final SessionBase sessionBase) throws PersistenceException {
		_connection = sessionBase.getConnection();
	}

	/**
	 * Execute update cql.
	 *
	 * @param cql
	 *            the cql
	 * @throws PersistenceException
	 */
	public void executeUpdateCql(final String cql, final Object... params) throws PersistenceException {
		try {
			_updateStatement = _connection.prepareStatement(cql);
			for (int i = 0; i < params.length; i++) {
				params[i] = prepareParameter(params[i]);
				_updateStatement.setObject(i + 1, params[i]);
			}
			_updateStatement.execute();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw PersistenceSqlMessageBuilder.createPersistenceException(e, cql);
		} finally {
			try {
				if (_updateStatement != null) {
					_updateStatement.close();
				}
			} catch (SQLException e) {
				throw new PersistenceException(PersistenceError.SQL.getErrorCode(), PersistenceError.SQL.toString(), e);
			}
		}
	}

	/**
	 * Prepare a parameter of date to timestamp.
	 *
	 * @param param
	 *            the param
	 * @return the object
	 */
	protected Object prepareParameter(final Object param) {
		Object parameter = param;

		if (param instanceof java.util.Date) {
			parameter = new java.sql.Timestamp(((java.util.Date) param).getTime());
		}

		return parameter;
	}

	/**
	 * Execute update statement.
	 *
	 * @param sql
	 *            the sql
	 * @param autoGeneratedKeys
	 *            the auto generated keys
	 * @param idField
	 *            the id field
	 * @param params
	 *            the params
	 * @return the object
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	protected Object executeUpdateStatement(final String sql, final int autoGeneratedKeys, final String[] idField,
			final Object... params) throws PersistenceException {
		try {
			final int rows;

			final int[] keys = new int[] { 1 };
			if (idField != null) {
				if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
					_updateStatement = _connection.prepareStatement(sql, idField);
				} else {
					_updateStatement = _connection.prepareStatement(sql, idField);
				}
			} else {
				if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
					_updateStatement = _connection.prepareStatement(sql, keys);
				} else {
					_updateStatement = _connection.prepareStatement(sql, autoGeneratedKeys);
				}
			}

			for (int i = 0; i < params.length; i++) {
				params[i] = prepareParameter(params[i]);
				_updateStatement.setObject(i + 1, params[i]);
			}
			rows = _updateStatement.executeUpdate();
			return (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) ? _updateStatement.getGeneratedKeys() : rows;
		} catch (SQLException e) {
			throw PersistenceSqlMessageBuilder.createPersistenceException(e, sql);
		} finally {
			try {
				if (autoGeneratedKeys != Statement.RETURN_GENERATED_KEYS) {
					if (_updateStatement != null) {
						_updateStatement.close();
					}
				}
			} catch (SQLException e) {
				throw new PersistenceException(PersistenceError.SQL.getErrorCode(), PersistenceError.SQL.toString(), e);
			}
			LOGGER.debug(String.format(PersistenceConstants.EXECUTE_SQL, sql));
		}
	}

	/**
	 * Execute batch statement.
	 *
	 * @param sql
	 *            the sql
	 * @param autoGeneratedKeys
	 *            the auto generated keys
	 * @param idField
	 *            the id field
	 * @param params
	 *            the params
	 * @return the object
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public Object executeBatchStatement(final String sql, final boolean autoGeneratedKeys, final String[] idField,
			final List<Object[]> params) throws PersistenceException {

		int[] rows = null;

		try {

			if (idField != null) {
				_updateStatement = _connection.prepareStatement(sql, idField);
			} else {

				if (autoGeneratedKeys) {
					_updateStatement = _connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				} else {
					final int[] keys = new int[] { 1 };
					_updateStatement = _connection.prepareStatement(sql, keys);
				}

			}

			for (Object[] object : params) {
				for (int i = 0; i < object.length; i++) {
					object[i] = prepareParameter(object[i]);
					_updateStatement.setObject(i + 1, object[i]);
				}
				_updateStatement.addBatch();
			}

			rows = _updateStatement.executeBatch();

		} catch (SQLException e) {
			// throw new PersistenceException(e.getErrorCode(), e.getMessage(),
			// e);
			throw PersistenceSqlMessageBuilder.createPersistenceException(e, sql);
		} finally {

			closeConnection();
		}

		return rows;
	}

	/**
	 * Method responsible to execute batch statement returning generated
	 * identifiers.
	 *
	 * @param sql
	 *            the sql
	 * @param idField
	 *            the id field
	 * @param params
	 *            the params
	 * @return the list
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public List<Integer> executeBatchStatementReturn(final String sql, final String idField,
			final List<Object[]> params) throws PersistenceException {

		final List<Integer> generatedIdList = new ArrayList<Integer>();

		try {

			_updateStatement = _connection.prepareStatement(sql, new String[] { idField });

			for (Object[] object : params) {
				for (int i = 0; i < object.length; i++) {
					object[i] = prepareParameter(object[i]);
					_updateStatement.setObject(i + 1, object[i]);
				}
				_updateStatement.addBatch();
			}

			_updateStatement.executeBatch();
			final ResultSet rs = _updateStatement.getGeneratedKeys();

			while (rs.next()) {
				generatedIdList.add(rs.getInt(1));
			}

		} catch (SQLException e) {
			throw PersistenceSqlMessageBuilder.createPersistenceException(e, sql);
		} finally {
			closeConnection();
		}

		return generatedIdList;
	}

	/**
	 * Close connection.
	 *
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	private void closeConnection() throws PersistenceException {
		try {
			if (_updateStatement != null) {
				_updateStatement.close();
			}

			if (_connection != null) {
				_connection.close();
			}
		} catch (Exception e) {
			throw new PersistenceException(PersistenceError.NOTCLOSED.getErrorCode(), e.getMessage(), e);
		}
	}

	/**
	 * Execute select statement.
	 *
	 * @param sql
	 *            the sql
	 * @param params
	 *            the params
	 * @return the array list
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public List<E> executeSelectStatement(final String sql, final Object... params) throws PersistenceException {
		try {
			_selectStatement = _connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				params[i] = prepareParameter(params[i]);
				_selectStatement.setObject(i + 1, params[i]);
			}
			_resultSet = _selectStatement.executeQuery();
			return fillList(_resultSet);
		} catch (SQLException e) {
			// throw new PersistenceException(e.getErrorCode(), e.getMessage(),
			// e);
			throw PersistenceSqlMessageBuilder.createPersistenceException(e, sql);
		} finally {
			try {
				if (_resultSet != null) {
					_resultSet.close();
				}
				if (_selectStatement != null) {
					_selectStatement.close();
				}
			} catch (SQLException e) {
				throw new PersistenceException(PersistenceError.SQL.getErrorCode(), PersistenceError.SQL.toString(), e);
			}
			LOGGER.debug(String.format(PersistenceConstants.EXECUTE_SQL, sql));
		}
	}

	/**
	 * Execute update statement.
	 *
	 * @param sql
	 *            the sql
	 * @param params
	 *            the params
	 * @return the int
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public int executeUpdateStatement(final String sql, final Object... params) throws PersistenceException {
		final String respuesta = executeUpdateStatement(sql, Statement.NO_GENERATED_KEYS, null, params).toString();
		return Integer.parseInt(respuesta);
	}

	/**
	 * Execute insert statement.
	 *
	 * @param sql
	 *            the sql
	 * @param params
	 *            the params
	 * @return the array list
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public List<Object> executeInsertStatement(final String sql, final Object... params) throws PersistenceException {
		return executeInsertStatementLocal(sql, null, params);
	}

	/**
	 * Execute insert statement.
	 *
	 * @param sql
	 *            the sql
	 * @param idField
	 *            the id field
	 * @param params
	 *            the params
	 * @return the list
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public List<Object> executeInsertStatement(final String sql, final String[] idField, final Object... params)
			throws PersistenceException {
		return executeInsertStatementLocal(sql, idField, params);
	}

	/**
	 * Execute insert statement local.
	 *
	 * @param sql
	 *            the sql
	 * @param idField
	 *            the id field
	 * @param params
	 *            the params
	 * @return the array list
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	protected List<Object> executeInsertStatementLocal(final String sql, final String[] idField, final Object... params)
			throws PersistenceException {
		final ArrayList<Object> valores = new ArrayList<Object>();
		final ResultSet setResultados = (ResultSet) executeUpdateStatement(sql, Statement.RETURN_GENERATED_KEYS,
				idField, params);
		try {
			Object generatedValue;
			while (setResultados.next()) {
				generatedValue = setResultados.getObject(1);
				valores.add(generatedValue);
			}
			return valores;
		} catch (SQLException e) {
			// throw new PersistenceException(e.getErrorCode(), e.getMessage(),
			// e);
			throw PersistenceSqlMessageBuilder.createPersistenceException(e, sql);
		} finally {
			try {
				setResultados.close();
				_updateStatement.close();
			} catch (SQLException e) {
				throw new PersistenceException(PersistenceError.SQL, e);
			}
			LOGGER.debug(String.format(PersistenceConstants.EXECUTE_SQL, sql));
		}
	}

	/**
	 * Execute delete statement.
	 *
	 * @param sql
	 *            the sql
	 * @param params
	 *            the params
	 * @return the int
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public int executeDeleteStatement(final String sql, final Object... params) throws PersistenceException {
		final String respuesta = executeUpdateStatement(sql, Statement.NO_GENERATED_KEYS, null, params).toString();
		return Integer.parseInt(respuesta);
	}

	/**
	 * Fills a list of entities from a result set.
	 *
	 * @param resultSet
	 *            the result set
	 * @return the array list
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	protected abstract List<E> fillList(final ResultSet resultSet) throws PersistenceException;

	/**
	 * Commit.
	 *
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public void commit() throws PersistenceException {
		try {
			_connection.commit();
			LOGGER.debug("Committed");
		} catch (SQLException e) {
			throw new PersistenceException(PersistenceError.NOCOMMIT.getErrorCode(),
					PersistenceError.NOCOMMIT.toString(), e);
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
			LOGGER.debug("Rollback");
		} catch (SQLException e) {
			throw new PersistenceException(PersistenceError.NOROLLBACK.getErrorCode(),
					PersistenceError.NOROLLBACK.toString(), e);
		}
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection() {
		return _connection;
	}

	/**
	 * Gets the update statement.
	 *
	 * @return the update statement
	 */
	public PreparedStatement getUpdateStatement() {
		return _updateStatement;
	}

}