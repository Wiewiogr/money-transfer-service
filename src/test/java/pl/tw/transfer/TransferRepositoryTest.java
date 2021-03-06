package pl.tw.transfer;

import org.testng.annotations.Test;
import pl.tw.util.DatabaseTestFixture;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferRepositoryTest extends DatabaseTestFixture {

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

        // Then
        Transfer transfer;
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM money_transfer");
                resultSet.next();
                transfer = Transfer.fromResultSet(resultSet);
            }
        }
        assertThat(transfer).isEqualToComparingFieldByField(appendedTransfer);
    }

    @Test
    public void shouldReturnNullWhenGettingTransferThatDoesNotExist() throws SQLException {
        // Given
        TransferRepository repository = new TransferRepository(dataSource);

        // When
        Transfer result = repository.getTransfer(UUID.randomUUID());

        // Then
        assertThat(result).isNull();
    }

    @Test
    public void shouldGetTransfer() throws SQLException {
        // Given
        Transfer transfer = new Transfer(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE, "Title", 1L);
        insertTransfer(transfer);
        TransferRepository repository = new TransferRepository(dataSource);

        // When
        Transfer result = repository.getTransfer(transfer.getTransferId());

        // Then
        assertThat(result).isEqualToComparingFieldByField(transfer);
    }

    @Test
    public void shouldNotGetTransferWhenThereAreNoTransfers() throws SQLException {
        // Given
        TransferRepository repository = new TransferRepository(dataSource);

        // When
        List<Transfer> result = repository.getTransfersForAccountInTimeRange(UUID.randomUUID(), 0L, 1000L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldNotGetTransferWhenThereAreNoMatching() throws SQLException {
        // Given
        Transfer transfer = new Transfer(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE, "Title", 1L);
        insertTransfer(transfer);
        TransferRepository repository = new TransferRepository(dataSource);

        // When
        List<Transfer> result = repository.getTransfersForAccountInTimeRange(UUID.randomUUID(), 0L, 1000L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldGetMatchingTransferInTimeRangeWhenFromIdMatches() throws SQLException {
        // Given
        UUID from = UUID.randomUUID();
        Transfer transfer = new Transfer(UUID.randomUUID(), from, UUID.randomUUID(), BigDecimal.ONE, "Title", 100L);
        insertTransfer(transfer);
        TransferRepository repository = new TransferRepository(dataSource);

        // When
        List<Transfer> result = repository.getTransfersForAccountInTimeRange(from, 0L, 1000L);

        // Then
        assertThat(result)
                .usingFieldByFieldElementComparator()
                .containsExactly(transfer);
    }

    @Test
    public void shouldGetMatchingTransferInTimeRangeWhenToIdMatches() throws SQLException {
        // Given
        UUID to = UUID.randomUUID();
        Transfer transfer = new Transfer(UUID.randomUUID(), UUID.randomUUID(), to, BigDecimal.ONE, "Title", 200L);
        insertTransfer(transfer);
        TransferRepository repository = new TransferRepository(dataSource);

        // When
        List<Transfer> result = repository.getTransfersForAccountInTimeRange(to, 0L, 1000L);

        // Then
        assertThat(result)
                .usingFieldByFieldElementComparator()
                .containsExactly(transfer);
    }

    @Test
    public void shouldGetMatchingTransferOnlyWhenInTimeRange() throws SQLException {
        // Given
        UUID matchingId = UUID.randomUUID();
        Transfer transferInTimeRange = new Transfer(UUID.randomUUID(), UUID.randomUUID(), matchingId, BigDecimal.ONE, "Title", 200L);
        insertTransfer(transferInTimeRange);
        Transfer transferOutsideOfRange = new Transfer(UUID.randomUUID(), UUID.randomUUID(), matchingId, BigDecimal.ONE, "Title", 2000L);
        insertTransfer(transferOutsideOfRange);
        TransferRepository repository = new TransferRepository(dataSource);

        // When
        List<Transfer> result = repository.getTransfersForAccountInTimeRange(matchingId, 0L, 1000L);

        // Then
        assertThat(result)
                .usingFieldByFieldElementComparator()
                .containsExactly(transferInTimeRange);
    }

    @Test
    public void shouldGetOnlyMatchingTransfers() throws SQLException {
        // Given
        UUID matchingId = UUID.randomUUID();
        Transfer matchingTransfer = new Transfer(UUID.randomUUID(), UUID.randomUUID(), matchingId, BigDecimal.ONE, "Title", 200L);
        insertTransfer(matchingTransfer);
        Transfer notMatchingTransfer = new Transfer(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE, "Title", 100L);
        insertTransfer(notMatchingTransfer);
        TransferRepository repository = new TransferRepository(dataSource);

        // When
        List<Transfer> result = repository.getTransfersForAccountInTimeRange(matchingId, 0L, 1000L);

        // Then
        assertThat(result)
                .usingFieldByFieldElementComparator()
                .containsExactly(matchingTransfer);
    }

    @Test
    public void shouldGetAllTransfers() throws SQLException {
        // Given
        Transfer transfer1 = new Transfer(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE, "Title", 200L);
        insertTransfer(transfer1);
        Transfer transfer2 = new Transfer(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE, "Title", 100L);
        insertTransfer(transfer2);
        TransferRepository repository = new TransferRepository(dataSource);

        // When
        List<Transfer> result = repository.getAllTransfers();

        // Then
        assertThat(result)
                .usingFieldByFieldElementComparator()
                .containsExactly(transfer1, transfer2);
    }
}