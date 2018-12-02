package pl.tw.account;

import org.testng.annotations.Test;
import pl.tw.eventbus.EventBus;
import pl.tw.transfer.DepositRequest;
import pl.tw.transfer.TransferRequest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AccountBalanceRepositoryTest {

    @Test
    public void shouldReturnZeroWhenAskingForAccountWhichTransfersWereNotRecorderYet() {
        // Given
        EventBus<TransferRequest> eventBus = mock(EventBus.class);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);

        // When
        BigDecimal balance = accountBalanceRepository.getBalance(UUID.randomUUID());

        // Then
        assertThat(balance).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void shouldTransferMoneyBetweenAccountsWhenTheyDidNotExist() {
        // Given
        EventBus<TransferRequest> eventBus = mock(EventBus.class);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();

        TransferRequest transferRequest = new TransferRequest(from, to, new BigDecimal("100.0"), "Title");

        // When
        accountBalanceRepository.onTransferEvent(transferRequest);

        BigDecimal fromBalance = accountBalanceRepository.getBalance(from);
        BigDecimal toBalance = accountBalanceRepository.getBalance(to);

        // Then
        assertThat(fromBalance).isEqualTo(new BigDecimal("-100.0"));
        assertThat(toBalance).isEqualTo(new BigDecimal("100.0"));
    }

    @Test
    public void shouldTransferMoneyBetweenAccountsWhenSomeOfThemExist() {
        // Given
        EventBus<TransferRequest> eventBus = mock(EventBus.class);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID third = UUID.randomUUID();

        TransferRequest transferRequest1 = new TransferRequest(first, second, new BigDecimal("100.0"), "Title");
        TransferRequest transferRequest2 = new TransferRequest(second, third, new BigDecimal("25.0"), "Title");

        // When
        accountBalanceRepository.onTransferEvent(transferRequest1);
        accountBalanceRepository.onTransferEvent(transferRequest2);

        BigDecimal firstBalance = accountBalanceRepository.getBalance(first);
        BigDecimal secondBalance = accountBalanceRepository.getBalance(second);
        BigDecimal thirdBalance = accountBalanceRepository.getBalance(third);

        // Then
        assertThat(firstBalance).isEqualTo(new BigDecimal("-100.0"));
        assertThat(secondBalance).isEqualTo(new BigDecimal("75.0"));
        assertThat(thirdBalance).isEqualTo(new BigDecimal("25.0"));
    }

    @Test
    public void shouldDepositMoneyOnAccountThatDoesNotExist() {
        // Given
        EventBus<TransferRequest> eventBus = mock(EventBus.class);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);
        UUID to = UUID.randomUUID();

        DepositRequest depositRequest = new DepositRequest(to, new BigDecimal("100.0"), "Title");

        // When
        accountBalanceRepository.onTransferEvent(depositRequest.toTransferRequest());

        BigDecimal balance = accountBalanceRepository.getBalance(to);

        // Then
        assertThat(balance).isEqualTo(new BigDecimal("100.0"));
    }

    @Test
    public void shouldDepositMoneyOnAccountThatAlreadyExist() {
        // Given
        EventBus<TransferRequest> eventBus = mock(EventBus.class);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);
        UUID to = UUID.randomUUID();

        DepositRequest depositRequest = new DepositRequest(to, new BigDecimal("100.0"), "Title");

        // When
        accountBalanceRepository.onTransferEvent(depositRequest.toTransferRequest());
        accountBalanceRepository.onTransferEvent(depositRequest.toTransferRequest());

        BigDecimal balance = accountBalanceRepository.getBalance(to);

        // Then
        assertThat(balance).isEqualTo(new BigDecimal("200.0"));
    }
}