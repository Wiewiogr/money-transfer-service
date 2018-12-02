package pl.tw.account;

import org.testng.annotations.Test;
import pl.tw.http.HttpResponse;
import spark.Request;

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
    public void shouldReturnIdOfNewlyCreatedAccount() {
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
}