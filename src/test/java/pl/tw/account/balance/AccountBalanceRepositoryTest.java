package pl.tw.account.balance;

import org.assertj.core.util.Lists;
import org.testng.annotations.Test;
import pl.tw.eventbus.EventBus;
import pl.tw.transfer.DepositRequest;
import pl.tw.transfer.Transfer;
import pl.tw.transfer.TransferRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.tw.transfer.DepositRequest.DEPOSIT_UUID;

public class AccountBalanceRepositoryTest {

    @Test
    public void shouldReturnZeroWhenAskingForAccountWhichTransfersWereNotRecorderYet() {
        // Given
        EventBus<Transfer> eventBus = mock(EventBus.class);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);

        // When
        BigDecimal balance = accountBalanceRepository.getBalance(UUID.randomUUID());

        // Then
        assertThat(balance).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void shouldTransferMoneyBetweenAccountsWhenTheyDidNotExist() {
        // Given
        EventBus<Transfer> eventBus = mock(EventBus.class);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();

        Transfer transferRequest = new Transfer(UUID.randomUUID(), from, to, new BigDecimal("100.0"), "Title", 0L);

        // When
        accountBalanceRepository.onTransfer(transferRequest);

        BigDecimal fromBalance = accountBalanceRepository.getBalance(from);
        BigDecimal toBalance = accountBalanceRepository.getBalance(to);

        // Then
        assertThat(fromBalance).isEqualTo(new BigDecimal("-100.0"));
        assertThat(toBalance).isEqualTo(new BigDecimal("100.0"));
    }

    @Test
    public void shouldTransferMoneyBetweenAccountsWhenSomeOfThemExist() {
        // Given
        EventBus<Transfer> eventBus = mock(EventBus.class);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID third = UUID.randomUUID();

        Transfer transferRequest1 = new Transfer(UUID.randomUUID(), first, second, new BigDecimal("100.0"), "Title", 0l);
        Transfer transferRequest2 = new Transfer(UUID.randomUUID(), second, third, new BigDecimal("25.0"), "Title", 0l);

        // When
        accountBalanceRepository.onTransfer(transferRequest1);
        accountBalanceRepository.onTransfer(transferRequest2);

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
        EventBus<Transfer> eventBus = mock(EventBus.class);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);
        UUID to = UUID.randomUUID();

        DepositRequest depositRequest = new DepositRequest(to, new BigDecimal("100.0"), "Title");
        Transfer transfer = new Transfer(UUID.randomUUID(), depositRequest.toTransferRequest(), 0L);

        // When
        accountBalanceRepository.onTransfer(transfer);

        BigDecimal balance = accountBalanceRepository.getBalance(to);

        // Then
        assertThat(balance).isEqualTo(new BigDecimal("100.0"));
    }

    @Test
    public void shouldDepositMoneyOnAccountThatAlreadyExist() {
        // Given
        EventBus<Transfer> eventBus = mock(EventBus.class);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);
        UUID to = UUID.randomUUID();

        DepositRequest depositRequest = new DepositRequest(to, new BigDecimal("100.0"), "Title");
        Transfer transfer = new Transfer(UUID.randomUUID(), depositRequest.toTransferRequest(), 0L);

        // When
        accountBalanceRepository.onTransfer(transfer);
        accountBalanceRepository.onTransfer(transfer);

        BigDecimal balance = accountBalanceRepository.getBalance(to);

        // Then
        assertThat(balance).isEqualTo(new BigDecimal("200.0"));
    }

    @Test
    public void shouldRecreateState() throws SQLException {
        // Given
        EventBus<Transfer> eventBus = mock(EventBus.class);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID third = UUID.randomUUID();

        Transfer deposit = new Transfer(UUID.randomUUID(), DEPOSIT_UUID, first, new BigDecimal("200.0"), "Title", 0l);
        Transfer transfer1 = new Transfer(UUID.randomUUID(), first, second, new BigDecimal("100.0"), "Title", 0l);
        Transfer transfer2 = new Transfer(UUID.randomUUID(), second, third, new BigDecimal("25.0"), "Title", 0l);

        TransferRepository transferRepository = mock(TransferRepository.class);
        when(transferRepository.getAllTransfers()).thenReturn(Lists.newArrayList(deposit, transfer1, transfer2));

        // When
        accountBalanceRepository.recreateState(transferRepository);

        // Then
        BigDecimal firstBalance = accountBalanceRepository.getBalance(first);
        BigDecimal secondBalance = accountBalanceRepository.getBalance(second);
        BigDecimal thirdBalance = accountBalanceRepository.getBalance(third);

        assertThat(firstBalance).isEqualTo(new BigDecimal("100.0"));
        assertThat(secondBalance).isEqualTo(new BigDecimal("75.0"));
        assertThat(thirdBalance).isEqualTo(new BigDecimal("25.0"));
    }
}