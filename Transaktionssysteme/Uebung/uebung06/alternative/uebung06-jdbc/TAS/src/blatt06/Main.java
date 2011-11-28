package blatt06;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Main
 */
public class Main {

	/* JDBC connection configuration */

	private static final String url = "jdbc:postgresql://alexandria.ifis.uni-passau.de:5432/db";
	// "jdbc:db2://alexandria.ifis.uni-passau.de:50000/db";
	private static final String user = "tas1112_team01";
	private static final String password = "d9oQDVxtdw";
	private static final String driver = "org.postgresql.Driver";
	// "com.ibm.db2.jcc.DB2Driver";

	/**
	 * isolation level for all transactions
	 */
	private static final int isolation = Connection.TRANSACTION_SERIALIZABLE;

	/* identifiers of tuples of table "data" */

	private static final char x = 'x';
	private static final char y = 'y';
	private static final char z = 'z';

	/**
	 * Resets the database. Creates a table "data" with two columns, "id" and
	 * "value". Inserts three tuples into the table "data", ('x', 7), ('y', 13)
	 * and ('z', 42).
	 */
	private static void resetDatabase() throws SQLException,
			ClassNotFoundException {
		if ((driver != null) && !(driver.trim().equals(""))) {
			Class.forName(driver);
		}
		Connection connection = DriverManager
				.getConnection(url, user, password);
		connection.setAutoCommit(true);
		connection
				.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			try {
				stmt.execute("DROP TABLE data");
			} catch (SQLException sqlException) {
			}
			stmt.close();
			stmt = connection.createStatement();
			stmt.execute("CREATE TABLE data (                  "
					+ "    id CHAR(1) PRIMARY KEY NOT NULL, "
					+ "    value INTEGER NOT NULL           "
					+ ")                                    ");
			stmt.close();
			stmt = connection.createStatement();
			stmt.execute("INSERT INTO data VALUES " + "('x', 7), "
					+ "('y', 13), " + "('z', 42)  ");
			stmt.close();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		connection.close();
	}

	/**
	 * Simulates a "lost update".
	 */
	private static void lostUpdate() throws Exception {
		Transaction T1 = new Transaction(1, url, user, password, driver,
				isolation);
		Transaction T2 = new Transaction(2, url, user, password, driver,
				isolation);

		System.out.println();
		System.out.println("Lost Update:");

		int vx1 = T1.read(x);
		int vx2 = T2.read(x);
		T1.write(x, vx1 + 1);
		T1.commit();
		T2.write(x, vx2 + 1);
		T2.commit();
	}

	/**
	 * Simulates an "inconsistent read".
	 */
	private static void inconsistentRead() throws Exception {
		Transaction T1 = new Transaction(1, url, user, password, driver,
				isolation);
		Transaction T2 = new Transaction(2, url, user, password, driver,
				isolation);

		System.out.println();
		System.out.println("Inconsistent Read:");

		int vx1 = T1.read(x);
		T1.write(x, vx1 + 1);
		int vx2 = T2.read(x);
		int vy1 = T1.read(y);
		T1.write(y, vy1 + 1);
		T1.commit();
		int vy2 = T2.read(y);
		T2.commit();
	}

	/**
	 * Simulates a "dirty read".
	 */
	private static void dirtyRead() throws Exception {
		Transaction T1 = new Transaction(1, url, user, password, driver,
				isolation);
		Transaction T2 = new Transaction(2, url, user, password, driver,
				isolation);

		System.out.println();
		System.out.println("Dirty Read:");

		int vx1 = T1.read(x);
		T1.write(x, vx1 + 1);
		int vx2 = T2.read(x);
		T1.abort();
		T2.write(x, vx2 + 1);
		T2.commit();
	}

	/**
	 * Main
	 */
	public static void main(String[] args) throws Exception {
		resetDatabase();
		dirtyRead();

		resetDatabase();
		inconsistentRead();

		resetDatabase();
		lostUpdate();
	}
}
