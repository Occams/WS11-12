package blatt06;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A {@link Transaction}.
 */
public class Transaction {

	/**
	 * transaction number
	 */
	private int number;

	/**
	 * JDBC database connection
	 */
	private Connection connection;

	/**
	 * Creates a new {@link Transaction}.
	 * 
	 * @param number
	 *            number of the transaction
	 * @param url
	 *            JDBC URL of database connection
	 * @param user
	 *            user of database connection
	 * @param password
	 *            password for user of database connection
	 * @param driver
	 *            driver class for the JDBC driver for database connection
	 * @param isolation
	 *            transaction isolation level
	 * @throws ClassNotFoundException
	 *             will be thrown if the class that contains the JDBC driver is
	 *             not found.
	 * @throws SQLException
	 *             will be thrown if a connection to the database cannot be
	 *             established.
	 */
	public Transaction(int number, String url, String user, String password,
			String driver, int isolation) throws ClassNotFoundException,
			SQLException {
		this.number = number;
		if ((driver != null) && !(driver.trim().equals(""))) {
			Class.forName(driver);
		}
		connection = DriverManager.getConnection(url, user, password);
		connection.setAutoCommit(false);
		connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
	}

	/**
	 * Prints the given information to standard output.
	 * 
	 * @param command
	 *            a command
	 * @param parameter
	 *            current parameter of the given command
	 * @param result
	 *            result of the given command with the given parameter
	 */
	private void print(String command, Object parameter, Object result) {
		StringBuilder out = new StringBuilder();
		out.append(command);
		out.append(number);
		out.append("(");
		if (parameter != null) {
			out.append(parameter);
		}
		out.append(")");
		if (result != null) {
			out.append(" = ");
			out.append(result);
		}
		System.out.println(out.toString());
	}

	/**
	 * Returns the value of the tuple with the given id in the table "data".
	 * 
	 * @param id
	 *            an id
	 * @return the value
	 * @throws SQLException
	 *             will be thrown if the selection of the value of the tuple
	 *             with the given id fails.
	 */
	public int read(char id) throws SQLException {
		PreparedStatement stmt = connection
				.prepareStatement("SELECT value FROM data WHERE id = ?");
		stmt.setString(1, String.valueOf(id));
		ResultSet result = stmt.executeQuery();
		connection.setAutoCommit(false);
		result.next();
		int read = result.getInt("value");
		print("r", id, read);
		return result.getInt("value");
	}

	/**
	 * Sets the value of the tuple with the given id in the table "data" to the
	 * given value.
	 * 
	 * @param id
	 *            an id
	 * @param value
	 *            a value
	 * @throws SQLException
	 *             will be thrown if the update of the value of the tuple with
	 *             the given id fails.
	 */
	public void write(char id, int value) throws SQLException {
		PreparedStatement stmt = connection
				.prepareStatement("UPDATE data SET value = ? WHERE id = ?");
		stmt.setInt(1, value);
		stmt.setString(2, String.valueOf(id));
		stmt.executeUpdate();
		connection.setAutoCommit(false);
		print("w", id + "," + value, null);
	}

	/**
	 * Commits this transaction.
	 * 
	 * @throws SQLException
	 *             will be thrown if the commit of this transaction fails.
	 */
	public void commit() throws SQLException {
		connection.commit();
		print("c", null, null);
	}

	/**
	 * Aborts this transaction.
	 * 
	 * @throws SQLException
	 *             will be thrown if the abort of this transaction fails.
	 */
	public void abort() throws SQLException {
		connection.rollback();
		print("a", null, null);
	}
}
