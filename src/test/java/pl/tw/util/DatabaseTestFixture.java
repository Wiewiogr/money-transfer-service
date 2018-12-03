package pl.tw.util;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pl.tw.sql.DatabaseConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTestFixture {

    public DataSource dataSource;
    DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

    @BeforeMethod
    public void setUp() throws SQLException {
        dataSource = databaseConfiguration.getDataSource();
    }

    @AfterMethod
    public void clean() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DROP TABLE money_transfer");
            }
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DROP TABLE account");
            }
        }
    }

}
