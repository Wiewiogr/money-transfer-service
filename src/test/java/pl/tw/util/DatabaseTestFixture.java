package pl.tw.util;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pl.tw.account.Account;
import pl.tw.sql.DatabaseConfiguration;
import pl.tw.transfer.Transfer;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;

public class DatabaseTestFixture {

    public DataSource dataSource;
    DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

    @BeforeMethod
    public void setUp() throws SQLException {
        dataSource = databaseConfiguration.getDataSource();
    }

    @AfterMethod(alwaysRun = true)
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


    public void insertAccount(Account account) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("" +
                    "INSERT INTO account(\n" +
                    "    id,\n" +
                    "    name,\n" +
                    "    surname\n" +
                    ") VALUES(?, ?, ?)")) {
                statement.setString(1, account.getId().toString());
                statement.setString(2, account.getName());
                statement.setString(3, account.getSurname());
                statement.execute();
            }
        }
    }

    public void insertTransfer(Transfer transfer) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("" +
                    "INSERT INTO money_transfer (\n" +
                    "  id,\n" +
                    "  from_account,\n" +
                    "  to_account,\n" +
                    "  amount,\n" +
                    "  title,\n" +
                    "  time\n" +
                    ") VALUES (?, ?, ?, ?, ?, ?)")) {
                statement.setString(1, transfer.getTransferId().toString());
                statement.setString(2, transfer.getFrom().toString());
                statement.setString(3, transfer.getTo().toString());
                statement.setBigDecimal(4, transfer.getAmount());
                statement.setString(5, transfer.getTitle());
                statement.setTimestamp(6, Timestamp.from(Instant.ofEpochMilli(transfer.getTimestamp())));
                statement.execute();
            }
        }
    }
}
