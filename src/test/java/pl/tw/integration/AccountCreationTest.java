package pl.tw.integration;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pl.tw.Application;
import pl.tw.account.Account;
import pl.tw.account.CreateAccountRequest;
import pl.tw.util.DatabaseTestFixture;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.tw.integration.HttpUtils.get;
import static pl.tw.integration.HttpUtils.post;

public class AccountCreationTest extends DatabaseTestFixture {

    private Application application;

    @Test
    public void shouldCreateAccountAndThenGetIt() throws Exception {
        // Given
        application = new Application(dataSource);
        application.start();
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("John", "Doe");

        // When
        UUID id = post("http://localhost:8080/account", createAccountRequest, UUID.class);
        Account result = get("http://localhost:8080/account/" + id.toString(), Account.class);

        // Then
        Account expected = new Account(id, createAccountRequest.getName(), createAccountRequest.getSurname());
        assertThat(result).isEqualToComparingFieldByField(expected);

        application.stop();
    }

    @AfterMethod(alwaysRun = true)
    public void stopApplication() {
        application.stop();
    }
}
