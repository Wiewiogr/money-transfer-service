package pl.tw.integration;

import org.testng.annotations.Test;
import pl.tw.account.CreateAccountRequest;
import pl.tw.transfer.DepositRequest;
import pl.tw.transfer.TransferRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static pl.tw.integration.HttpUtils.get;
import static pl.tw.integration.HttpUtils.post;

public class ConcurrentAccessTest extends IntegrationTestFixture {

    @Test
    public void shouldTransferMoneyFromOneAccountToAnotherWithManyThreadsInvolved() throws Exception {
        // Given
        CreateAccountRequest createAccountRequest1 = new CreateAccountRequest("John", "Doe");
        CreateAccountRequest createAccountRequest2 = new CreateAccountRequest("Jane", "Smith");
        UUID first = post("http://localhost:" + currentPort + "/account", createAccountRequest1);
        UUID second = post("http://localhost:" + currentPort + "/account", createAccountRequest2);

        int amountOfMoneyToMove = 10000;
        DepositRequest depositRequest = new DepositRequest(first, new BigDecimal(amountOfMoneyToMove), "Deposit");
        post("http://localhost:" + currentPort + "/transfer/deposit", depositRequest);

        // When
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        for (int i = 0; i < amountOfMoneyToMove; i++) {
            executorService.submit(() -> {
                TransferRequest transferRequest = new TransferRequest(first, second, BigDecimal.ONE, "Title");
                try {
                    post("http://localhost:" + currentPort + "/transfer", transferRequest);
                } catch (IOException e) {
                    fail("Error while making transfer", e);
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);

        // Then
        BigDecimal firstBalance = get("http://localhost:" + currentPort + "/account/" + first.toString() + "/balance", BigDecimal.class);
        BigDecimal secondBalance = get("http://localhost:" + currentPort + "/account/" + second.toString() + "/balance", BigDecimal.class);

        assertThat(firstBalance).isEqualTo(BigDecimal.ZERO);
        assertThat(secondBalance).isEqualTo(new BigDecimal(amountOfMoneyToMove));
    }
}
