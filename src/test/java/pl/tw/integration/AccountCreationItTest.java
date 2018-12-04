package pl.tw.integration;

import org.testng.annotations.Test;
import pl.tw.account.Account;
import pl.tw.account.CreateAccountRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.tw.integration.HttpUtils.get;
import static pl.tw.integration.HttpUtils.post;

public class AccountCreationItTest extends IntegrationTestFixture {

    @Test
    public void shouldCreateAccountAndThenGetIt() throws Exception {
        // Given
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("John", "Doe");

        // When
        UUID id = post("http://localhost:" + currentPort + "/account", createAccountRequest);
        Account result = get("http://localhost:" + currentPort + "/account/" + id.toString(), Account.class);

        // Then
        Account expected = new Account(id, createAccountRequest.getName(), createAccountRequest.getSurname());
        assertThat(result).isEqualToComparingFieldByField(expected);
    }
}
