package pl.tw.account;

import org.testng.annotations.Test;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountControllerTest {

    String correctBody = "" +
            "{\n" +
            "    \"name\": \"John\",\n" +
            "    \"surname\": \"Doe\"\n" +
            "}";

    @Test
    public void shouldReturnIdOfNewlyCreatedAccount() throws SQLException {
        // Given
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountController accountController = new AccountController(accountRepository);

        UUID uuid = UUID.randomUUID();
        Request request = mock(Request.class);
        when(request.body()).thenReturn(correctBody);
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("John", "Doe");
        when(accountRepository.createAccount(createAccountRequest)).thenReturn(uuid);

        // When
        HttpResponse<UUID> result = accountController.createAccount(request);

        // Then

        assertThat(result.isError()).isFalse();
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getObject()).isEqualTo(uuid);
    }

    @Test
    public void shouldReturnInternalErrorWhenSqlErrorOccursWhileCreatingAccount() throws SQLException {
        // Given
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountController accountController = new AccountController(accountRepository);

        Request request = mock(Request.class);
        when(request.body()).thenReturn(correctBody);
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("John", "Doe");
        when(accountRepository.createAccount(createAccountRequest)).thenThrow(SQLException.class);

        // When
        HttpResponse<UUID> result = accountController.createAccount(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getError()).isEqualTo("Internal server error, contact service owner.");
    }

    private final String unparsableBody = "{SDADSsdf}";

    @Test
    public void shouldReturnErrorMessageWhenErrorDuringParsingOccurs() {
        // Given
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountController accountController = new AccountController(accountRepository);

        Request request = mock(Request.class);
        when(request.body()).thenReturn(unparsableBody);

        // When
        HttpResponse<UUID> result = accountController.createAccount(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getError()).isEqualTo("Error parsing request body.");
    }

    @Test
    public void shouldReturnAccount() throws SQLException {
        // Given
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountController accountController = new AccountController(accountRepository);

        UUID uuid = UUID.randomUUID();
        Request request = mock(Request.class);
        Account account = new Account(uuid, "Name", "Surname");
        when(request.params("accountId")).thenReturn(uuid.toString());
        when(accountRepository.getAccount(uuid)).thenReturn(account);

        // When
        HttpResponse<Account> result = accountController.getAccount(request);

        // Then
        assertThat(result.isError()).isFalse();
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getObject()).isEqualTo(account);
    }

    @Test
    public void shouldReturnErrorWhenIdIsNotUUID() {
        // Given
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountController accountController = new AccountController(accountRepository);

        Request request = mock(Request.class);
        String notUUID = "NOTUUIDSTRING";
        when(request.params("accountId")).thenReturn(notUUID);

        // When
        HttpResponse<Account> result = accountController.getAccount(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getError()).isEqualTo(notUUID + " is not a valid UUID.");
    }

    @Test
    public void shouldReturnErrorWhenAccountDoesNotExists() throws SQLException {
        // Given
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountController accountController = new AccountController(accountRepository);

        UUID uuid = UUID.randomUUID();
        Request request = mock(Request.class);
        when(request.params("accountId")).thenReturn(uuid.toString());
        when(accountRepository.getAccount(uuid)).thenReturn(null);

        // When
        HttpResponse<Account> result = accountController.getAccount(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getError()).isEqualTo("Account " + uuid + " does not exist.");
    }

    @Test
    public void shouldReturnInternalErrorWhenSqlExceptionOccursWhileGetAccount() throws SQLException {
        // Given
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountController accountController = new AccountController(accountRepository);

        UUID uuid = UUID.randomUUID();
        Request request = mock(Request.class);
        when(request.params("accountId")).thenReturn(uuid.toString());
        when(accountRepository.getAccount(uuid)).thenThrow(SQLException.class);

        // When
        HttpResponse<Account> result = accountController.getAccount(request);

        // Then
        assertThat(result.isError()).isTrue();
        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getError()).isEqualTo("Internal server error, contact service owner.");
    }
}