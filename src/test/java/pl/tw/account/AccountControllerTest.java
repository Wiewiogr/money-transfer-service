package pl.tw.account;

import org.testng.annotations.Test;
import pl.tw.http.ErrorMessage;
import pl.tw.http.IdResponse;
import spark.Request;
import spark.Response;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

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

        Response response = mock(Response.class);

        // When
        Object result = accountController.createAccount(request, response);

        // Then
        verify(response).status(200);
        assertThat(result).isInstanceOf(IdResponse.class);
        assertThat(((IdResponse)result).getId()).isEqualTo(uuid);
    }

    private final String unparsableBody = "{SDADSsdf}";

    @Test
    public void shouldReturnErrorMessageWhenErrorDuringParsingOccurs() {
        // Given
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountController accountController = new AccountController(accountRepository);

        Request request = mock(Request.class);
        when(request.body()).thenReturn(unparsableBody);

        Response response = mock(Response.class);

        // When
        Object result = accountController.createAccount(request, response);

        // Then
        verify(response).status(400);
        assertThat(result).isInstanceOf(ErrorMessage.class);
        assertThat(((ErrorMessage) result).getError()).isEqualTo("Error parsing request body.");
    }
}