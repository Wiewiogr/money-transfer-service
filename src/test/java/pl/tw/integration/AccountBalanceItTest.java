package pl.tw.integration;

import org.apache.http.client.HttpResponseException;
import org.testng.annotations.Test;
import pl.tw.account.CreateAccountRequest;
import pl.tw.transfer.DepositRequest;
import pl.tw.transfer.TransferRequest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.tw.integration.HttpUtils.get;
import static pl.tw.integration.HttpUtils.post;

public class AccountBalanceItTest extends IntegrationTestFixture {

    @Test
    public void shouldAddDepositedAmountToAccount() throws Exception {
        // Given
        CreateAccountRequest createAccountRequest = new CreateAccountRequest("John", "Doe");
        UUID accountId = post("http://localhost:" + currentPort + "/account", createAccountRequest);

        // When
        DepositRequest depositRequest = new DepositRequest(accountId, new BigDecimal("1000"), "Deposit");
        post("http://localhost:" + currentPort + "/transfer/deposit", depositRequest);

        // Then
        BigDecimal balance = get("http://localhost:" + currentPort + "/account/" + accountId.toString() + "/balance", BigDecimal.class);
        assertThat(balance).isEqualTo(new BigDecimal("1000"));
    }

    @Test
    public void shouldTransferMoneyBetweenAccounts() throws Exception {
        // Given
        CreateAccountRequest createAccountRequest1 = new CreateAccountRequest("John", "Doe");
        CreateAccountRequest createAccountRequest2 = new CreateAccountRequest("Jane", "Smith");
        UUID first = post("http://localhost:" + currentPort + "/account", createAccountRequest1);
        UUID second = post("http://localhost:" + currentPort + "/account", createAccountRequest2);

        DepositRequest depositRequest = new DepositRequest(first, new BigDecimal("1000"), "Deposit");
        post("http://localhost:" + currentPort + "/transfer/deposit", depositRequest);

        // When
        TransferRequest transferRequest = new TransferRequest(first, second, new BigDecimal("300"), "Title");
        post("http://localhost:" + currentPort + "/transfer", transferRequest);

        // Then
        BigDecimal firstBalance = get("http://localhost:" + currentPort + "/account/" + first.toString() + "/balance", BigDecimal.class);
        BigDecimal secondBalance = get("http://localhost:" + currentPort + "/account/" + second.toString() + "/balance", BigDecimal.class);

        assertThat(firstBalance).isEqualTo(new BigDecimal("700"));
        assertThat(secondBalance).isEqualTo(new BigDecimal("300"));
    }

    @Test
    public void shouldAllowTransferOnlyWhenThereIsEnoughMoney() throws Exception {
        // Given
        CreateAccountRequest createAccountRequest1 = new CreateAccountRequest("John", "Doe");
        CreateAccountRequest createAccountRequest2 = new CreateAccountRequest("Jane", "Smith");
        UUID first = post("http://localhost:" + currentPort + "/account", createAccountRequest1);
        UUID second = post("http://localhost:" + currentPort + "/account", createAccountRequest2);

        DepositRequest depositRequest = new DepositRequest(first, new BigDecimal("1000"), "Deposit");
        post("http://localhost:" + currentPort + "/transfer/deposit", depositRequest);

        // When
        TransferRequest transferRequest = new TransferRequest(first, second, new BigDecimal("3000"), "Title");

        HttpResponseException responseException = null;
        try {
            post("http://localhost:" + currentPort + "/transfer", transferRequest);
        } catch (HttpResponseException e) {
            responseException = e;
        }

        // Then
        assertThat(responseException).isNotNull();
        assertThat(responseException).hasMessage("Bad Request");
        assertThat(responseException.getStatusCode()).isEqualTo(400);
    }
}
