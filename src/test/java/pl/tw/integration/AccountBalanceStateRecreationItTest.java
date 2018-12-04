package pl.tw.integration;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pl.tw.Application;
import pl.tw.account.Account;
import pl.tw.transfer.Transfer;
import pl.tw.util.DatabaseTestFixture;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.tw.integration.HttpUtils.get;
import static pl.tw.transfer.DepositRequest.DEPOSIT_UUID;

public class AccountBalanceStateRecreationItTest extends DatabaseTestFixture {

    private Application application;

    @Test
    public void shouldRecreateStateOnStartUp() throws SQLException, IOException {
        // Given
        Account first = new Account(UUID.randomUUID(), "A", "B");
        Account second = new Account(UUID.randomUUID(), "A", "B");
        Account third = new Account(UUID.randomUUID(), "A", "B");
        insertAccount(first);
        insertAccount(second);
        insertAccount(third);

        Transfer deposit = new Transfer(UUID.randomUUID(), DEPOSIT_UUID, first.getId(), new BigDecimal("200.0"), "Title", 0l);
        Transfer transfer1 = new Transfer(UUID.randomUUID(), first.getId(), second.getId(), new BigDecimal("100.0"), "Title", 0l);
        Transfer transfer2 = new Transfer(UUID.randomUUID(), second.getId(), third.getId(), new BigDecimal("25.0"), "Title", 0l);
        insertTransfer(deposit);
        insertTransfer(transfer1);
        insertTransfer(transfer2);

        // When
        application = new Application(dataSource);
        application.start(8080);

        // Then
        BigDecimal firstBalance = get("http://localhost:8080/account/" + first.getId().toString() + "/balance", BigDecimal.class);
        BigDecimal secondBalance = get("http://localhost:8080/account/" + second.getId().toString() + "/balance", BigDecimal.class);
        BigDecimal thirdBalance = get("http://localhost:8080/account/" + third.getId().toString() + "/balance", BigDecimal.class);

        assertThat(firstBalance).isEqualTo(new BigDecimal("100"));
        assertThat(secondBalance).isEqualTo(new BigDecimal("75"));
        assertThat(thirdBalance).isEqualTo(new BigDecimal("25"));
    }

    @AfterMethod(alwaysRun = true)
    public void stopApplication() {
        application.stop();
    }
}
