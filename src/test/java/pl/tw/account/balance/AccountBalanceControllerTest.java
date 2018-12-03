package pl.tw.account.balance;

import org.testng.annotations.Test;
import pl.tw.account.Account;
import pl.tw.account.AccountRepository;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountBalanceControllerTest {

    @Test
    public void shouldReturnAccount() throws SQLException {
        // Given
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountBalanceRepository accountBalanceRepository = mock(AccountBalanceRepository.class);
        AccountBalanceController accountBalanceController =
                new AccountBalanceController(accountBalanceRepository, accountRepository);

        UUID uuid = UUID.randomUUID();
        BigDecimal balance = BigDecimal.ONE;
        Request request = mock(Request.class);
        when(request.params("accountId")).thenReturn(uuid.toString());
        when(accountRepository.getAccount(uuid)).thenReturn(mock(Account.class));
        when(accountBalanceRepository.getBalance(uuid)).thenReturn(balance);

        // When
        HttpResponse<BigDecimal> result = accountBalanceController.getBalance(request);

        // Then
        assertThat(result.isError()).isFalse();
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getObject()).isEqualTo(balance);
    }

    @Test
    public void shouldReturnErrorWhenIdIsNotUUID() {
        // Given

        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountBalanceRepository accountBalanceRepository = mock(AccountBalanceRepository.class);
        AccountBalanceController accountBalanceController =
                new AccountBalanceController(accountBalanceRepository, accountRepository);

        Request request = mock(Request.class);
        String notUUID = "NOTUUIDSTRING";
        when(request.params("accountId")).thenReturn(notUUID);

        // When
        HttpResponse<BigDecimal> result = accountBalanceController.getBalance(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getError()).isEqualTo(notUUID + " is not a valid UUID.");
    }

    @Test
    public void shouldReturnErrorWhenAccountDoesNotExists() throws SQLException {
        // Given
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountBalanceRepository accountBalanceRepository = mock(AccountBalanceRepository.class);
        AccountBalanceController accountBalanceController =
                new AccountBalanceController(accountBalanceRepository, accountRepository);

        UUID uuid = UUID.randomUUID();
        Request request = mock(Request.class);
        when(request.params("accountId")).thenReturn(uuid.toString());
        when(accountRepository.getAccount(uuid)).thenReturn(null);

        // When
        HttpResponse<BigDecimal> result = accountBalanceController.getBalance(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getError()).isEqualTo("Account " + uuid + " does not exist.");
    }

    @Test
    public void shouldReturnInternalServerErrorWhenSqlOccursDuringGetAccount() throws SQLException {
        // Given
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountBalanceRepository accountBalanceRepository = mock(AccountBalanceRepository.class);
        AccountBalanceController accountBalanceController =
                new AccountBalanceController(accountBalanceRepository, accountRepository);

        UUID uuid = UUID.randomUUID();
        Request request = mock(Request.class);
        when(request.params("accountId")).thenReturn(uuid.toString());
        when(accountRepository.getAccount(uuid)).thenThrow(SQLException.class);

        // When
        HttpResponse<BigDecimal> result = accountBalanceController.getBalance(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getError()).isEqualTo("Internal server error, contact service owner.");
    }
}