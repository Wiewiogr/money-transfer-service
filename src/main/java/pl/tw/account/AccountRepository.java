package pl.tw.account;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountRepository {

    private static final String CREATE_ACCOUNT_SQL = "" +
            "INSERT INTO account(\n" +
            "    id,\n" +
            "    name,\n" +
            "    surname\n" +
            ") VALUES(?, ?, ?)";
    private static final String GET_ACCOUNT_SQL = "SELECT * FROM account WHERE id=?";

    private DataSource dataSource;

    public AccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UUID createAccount(CreateAccountRequest createAccountRequest) throws SQLException {
        UUID accountId = UUID.randomUUID();

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_ACCOUNT_SQL)) {
                statement.setString(1, accountId.toString());
                statement.setString(2, createAccountRequest.getName());
                statement.setString(3, createAccountRequest.getSurname());
                statement.execute();
            }
        }
        return accountId;
    }

    public Account getAccount(UUID accountId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(GET_ACCOUNT_SQL)) {
                statement.setString(1, accountId.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return Account.fromResultSet(resultSet);
                } else {
                    return null;
                }
            }
        }
    }
}
