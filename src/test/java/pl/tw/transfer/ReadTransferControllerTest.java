package pl.tw.transfer;

import org.testng.annotations.Test;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReadTransferControllerTest {

    @Test
    public void shouldReturnTransfer() throws SQLException {
        // Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        ReadTransferController accountController = new ReadTransferController(transferRepository);

        UUID uuid = UUID.randomUUID();

        Request request = mock(Request.class);
        Transfer transfer = new Transfer(uuid, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ONE, "Title", 0L);
        when(request.params("transferId")).thenReturn(uuid.toString());
        when(transferRepository.getTransfer(uuid)).thenReturn(transfer);

        // When
        HttpResponse<Transfer> result = accountController.getTransfer(request);

        // Then
        assertThat(result.isError()).isFalse();
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getObject()).isEqualTo(transfer);
    }

    @Test
    public void shouldReturnErrorWhenIdIsNotUUID() {
        TransferRepository transferRepository = mock(TransferRepository.class);
        ReadTransferController accountController = new ReadTransferController(transferRepository);


        Request request = mock(Request.class);
        String notUUID = "NOTUUIDSTRING";
        when(request.params("transferId")).thenReturn(notUUID);

        // When
        HttpResponse<Transfer> result = accountController.getTransfer(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getError()).isEqualTo(notUUID + " is not a valid UUID.");
    }

    @Test
    public void shouldReturnErrorWhenTransferDoesNotExists() throws SQLException {
        // Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        ReadTransferController accountController = new ReadTransferController(transferRepository);

        UUID uuid = UUID.randomUUID();

        Request request = mock(Request.class);
        when(request.params("transferId")).thenReturn(uuid.toString());
        when(transferRepository.getTransfer(uuid)).thenReturn(null);

        // When
        HttpResponse<Transfer> result = accountController.getTransfer(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getError()).isEqualTo("Transfer " + uuid + " does not exist.");
    }

    @Test
    public void shouldReturnInternalServerErrorWhenSqlErrorOccurs() throws SQLException {
        // Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        ReadTransferController accountController = new ReadTransferController(transferRepository);

        UUID uuid = UUID.randomUUID();

        Request request = mock(Request.class);
        when(request.params("transferId")).thenReturn(uuid.toString());
        when(transferRepository.getTransfer(uuid)).thenThrow(SQLException.class);

        // When
        HttpResponse<Transfer> result = accountController.getTransfer(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getError()).isEqualTo("Internal server error, contact service owner.");
    }
}