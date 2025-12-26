package Test;

import DataBase.DataBaseManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DataBaseManagerTest {

    private DataBaseManager dataBaseManager;
    private Connection connection;

    //Run before every test
    @Before
    public void setUp() {
        try { //DB'll read config files and try to connect
            dataBaseManager = new DataBaseManager();
            connection = dataBaseManager.getConnection();
        } catch (Exception e) {
            fail("Error: " + e.getMessage());
        }
    }

    //Run before every test
    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    //Checks if connection is successful or not
    @Test
    public void testConnectionIsValid() throws SQLException {
        assertNotNull("Connection can not be null", connection);
        assertFalse("Connection must be active", connection.isClosed());
        assertTrue("Connection must be valid", connection.isValid(2));
    }

    //Checks if tables are properly formed
    @Test
    public void testTablesExist() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();

        //Tables to check
        String[] expectedTables = {
                "users",
                "watchlists",
                "user_groups",
                "group_members",
                "group_watchlists",
                "list_items"
        };

        for (String tableName : expectedTables) {
            boolean tableExists = false;

            try (ResultSet resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
                if (resultSet.next()) {
                    tableExists = true;
                }
            }

            assertTrue("Table not found: " + tableName, tableExists);
        }
    }

    // getConnection must always be active
    @Test
    public void testGetConnectionReturnsActiveConnection() {
        Connection conn1 = dataBaseManager.getConnection();
        assertNotNull(conn1);

        //If called again should return null
        Connection conn2 = dataBaseManager.getConnection();
        assertNotNull(conn2);

        //Are two the same reference?
        assertSame("Should return same reference", conn1, conn2);
    }
}