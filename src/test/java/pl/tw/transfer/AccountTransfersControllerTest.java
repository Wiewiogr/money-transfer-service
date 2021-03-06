package pl.tw.transfer;

import org.testng.annotations.Test;
import org.testng.collections.Lists;
import pl.tw.account.Account;
import pl.tw.account.AccountRepository;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountTransfersControllerTest {

    @Test
    public void shouldReturnTransfer() throws SQLException {
        // Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountTransfersController accountController = new AccountTransfersController(transferRepository, accountRepository);

        UUID uuid = UUID.randomUUID();

        Request request = mock(Request.class);
        List<Transfer> transfer = Lists.newArrayList();
        when(request.params("accountId")).thenReturn(uuid.toString());
        when(request.params("from")).thenReturn("10");
        when(request.params("to")).thenReturn("100");
        when(accountRepository.getAccount(uuid)).thenReturn(mock(Account.class));
        when(transferRepository.getTransfersForAccountInTimeRange(uuid, 10L, 100L)).thenReturn(transfer);

        // When
        HttpResponse<List<Transfer>> result = accountController.getTransfersForAccountInTimeRange(request);

        // Then
        assertThat(result.isError()).isFalse();
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getObject()).isEqualTo(transfer);
    }

    @Test
    public void shouldReturnErrorWhenIdIsNotUUID() {
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountTransfersController accountController = new AccountTransfersController(transferRepository, accountRepository);

        Request request = mock(Request.class);
        String notUUID = "NOTUUIDSTRING";
        when(request.params("accountId")).thenReturn(notUUID);

        // When
        HttpResponse<List<Transfer>> result = accountController.getTransfersForAccountInTimeRange(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getError()).isEqualTo(notUUID + " is not a valid UUID.");
    }

    @Test
    public void shouldReturnErrorWhenTransferDoesNotExists() throws SQLException {
        // Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountTransfersController accountController = new AccountTransfersController(transferRepository, accountRepository);

        UUID uuid = UUID.randomUUID();

        Request request = mock(Request.class);
        when(request.params("accountId")).thenReturn(uuid.toString());
        when(accountRepository.getAccount(uuid)).thenReturn(null);

        // When
        HttpResponse<List<Transfer>> result = accountController.getTransfersForAccountInTimeRange(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getError()).isEqualTo("Account " + uuid + " does not exist.");
    }

    @Test
    public void shouldReturnInternalErrorWhenThereIsSqlErrorInTransferRepository() throws SQLException {
        // Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountTransfersController accountController = new AccountTransfersController(transferRepository, accountRepository);

        UUID uuid = UUID.randomUUID();
        Request request = mock(Request.class);
        when(request.params("accountId")).thenReturn(uuid.toString());
        when(request.params("from")).thenReturn("10");
        when(request.params("to")).thenReturn("100");
        when(accountRepository.getAccount(uuid)).thenReturn(mock(Account.class));
        when(transferRepository.getTransfersForAccountInTimeRange(uuid, 10L, 100L))
                .thenThrow(SQLException.class);

        // When
        HttpResponse<List<Transfer>> result = accountController.getTransfersForAccountInTimeRange(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getError()).isEqualTo("Internal server error, contact service owner.");
    }

    @Test
    public void shouldReturnInternalErrorWhenThereIsSqlErrorInAccountRepository() throws SQLException {
        // Given
        TransferRepository transferRepository = mock(TransferRepository.class);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountTransfersController accountController = new AccountTransfersController(transferRepository, accountRepository);

        UUID uuid = UUID.randomUUID();
        Request request = mock(Request.class);
        when(request.params("accountId")).thenReturn(uuid.toString());
        when(request.params("from")).thenReturn("10");
        when(request.params("to")).thenReturn("100");
        when(accountRepository.getAccount(uuid)).thenThrow(SQLException.class);

        // When
        HttpResponse<List<Transfer>> result = accountController.getTransfersForAccountInTimeRange(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getError()).isEqualTo("Internal server error, contact service owner.");
    }
}