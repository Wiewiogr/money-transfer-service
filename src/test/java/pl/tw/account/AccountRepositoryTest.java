package pl.tw.account;

import org.testng.annotations.Test;
import pl.tw.util.DatabaseTestFixture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountRepositoryTest extends DatabaseTestFixture {

    @Test
    public void shouldReturnNullWhenGettingAccountThatDoesNotExist() throws SQLException {
        // Given
        AccountRepository accountRepository = new AccountRepository(dataSource);

        // When
        Account account = accountRepository.getAccount(UUID.randomUUID());

        // Then
        assertThat(account).isNull();
    }

    @Test
    public void shouldReturnAccountIfItExist() throws SQLException {
        // Given
        AccountRepository accountRepository = new AccountRepository(dataSource);
        Account account = new Account(UUID.randomUUID(), "John", "Doe");
        insertAccount(account);

        // When
        Account result = accountRepository.getAccount(account.getId());

        // Then
        assertThat(result).isEqualToComparingFieldByField(account);
    }

    @Test
    public void shouldCreateAccount() throws SQLException {
        // Given
        AccountRepository accountRepository = new AccountRepository(dataSource);

        // When
        UUID result = accountRepository.createAccount(new CreateAccountRequest("John", "Doe"));

        // Then
        Account actual;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM account WHERE id=?")) {
                statement.setString(1, result.toString());
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                actual = Account.fromResultSet(resultSet);
            }
        }
        Account expected = new Account(result, "John", "Doe");
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }
}