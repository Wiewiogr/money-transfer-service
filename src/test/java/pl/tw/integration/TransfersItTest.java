package pl.tw.integration;

import org.testng.annotations.Test;
import pl.tw.account.CreateAccountRequest;
import pl.tw.transfer.DepositRequest;
import pl.tw.transfer.Transfer;
import pl.tw.transfer.TransferRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.tw.integration.HttpUtils.get;
import static pl.tw.integration.HttpUtils.post;

public class TransfersItTest extends IntegrationTestFixture {

    public static final Comparator<Transfer> TRANSFER_COMPARATOR = Comparator
            .comparing(Transfer::getTo)
            .thenComparing(Transfer::getFrom)
            .thenComparing(Transfer::getAmount)
            .thenComparing(Transfer::getTitle);

    @Test
    public void shouldListAccountsTransfers() throws IOException {
        // Given
        CreateAccountRequest createAccountRequest1 = new CreateAccountRequest("John", "Doe");
        CreateAccountRequest createAccountRequest2 = new CreateAccountRequest("Jane", "Smith");
        UUID first = post("http://localhost:" + currentPort + "/account", createAccountRequest1);
        UUID second = post("http://localhost:" + currentPort + "/account", createAccountRequest2);

        DepositRequest depositRequest = new DepositRequest(first, new BigDecimal("1000"), "Deposit");
        post("http://localhost:" + currentPort + "/transfer/deposit", depositRequest);

        TransferRequest transferRequest = new TransferRequest(first, second, new BigDecimal("300"), "Title");
        post("http://localhost:" + currentPort + "/transfer", transferRequest);

        // When
        long now = System.currentTimeMillis();
        String from = String.valueOf(now - 10_000);
        String to = String.valueOf(now + 10_000);

        List<Transfer> list = Arrays.asList(get("http://localhost:" + currentPort + "/account/" + first.toString() + "/transfer/" + from + "/" + to, Transfer[].class));

        // Then
        Transfer transfer1 = new Transfer(UUID.randomUUID(), depositRequest.toTransferRequest(), 0);
        Transfer transfer2 = new Transfer(UUID.randomUUID(), transferRequest, 0);

        assertThat(list.size()).isEqualTo(2);
        assertThat(list).usingElementComparator(TRANSFER_COMPARATOR).contains(transfer1);
        assertThat(list).usingElementComparator(TRANSFER_COMPARATOR).contains(transfer2);
    }
}
