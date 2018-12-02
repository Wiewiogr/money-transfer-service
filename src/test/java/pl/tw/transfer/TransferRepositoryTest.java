package pl.tw.transfer;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.tw.sql.DatabaseConfiguration;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferRepositoryTest {

    DataSource dataSource;
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
        }
    }

    @Test
    public void shouldAppendTransfer() throws SQLException {
        // Given
        TransferRepository repository = new TransferRepository(dataSource);
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        String title = "Title";

        TransferRequest transferRequest = new TransferRequest(from, to, amount, title);

        // When
        Transfer appendedTransfer = repository.appendTransfer(transferRequest);
        Transfer result = repository.getTransfer(appendedTransfer.getTransferId());

        // Then
        assertThat(result).isEqualToComparingFieldByField(appendedTransfer);
    }
}