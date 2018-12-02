package pl.tw.account.balance;

import pl.tw.eventbus.EventBus;
import pl.tw.transfer.DepositRequest;
import pl.tw.transfer.Transfer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AccountBalanceRepository {

    private final Map<UUID, BigDecimal> accountBalances = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    AccountBalanceRepository(EventBus<Transfer> transferRequestEventBus) {
        transferRequestEventBus.subscribe(this::onTransfer);
    }

    public BigDecimal getBalance(UUID from) {
        Lock readLock = this.lock.writeLock();
        BigDecimal balance;
        try {
            balance = accountBalances.get(from);
            readLock.lock();
        } finally {
            readLock.unlock();
        }
        return balance == null ? BigDecimal.ZERO : balance;
    }

    void onTransfer(Transfer transfer) {
        Lock writeLock = this.lock.writeLock();
        try {
            if (transfer.getFrom().equals(DepositRequest.DEPOSIT_UUID)) {
                handleDepositRequest(transfer);
            } else {
                handleTransferRequest(transfer);
            }

            writeLock.lock();
        } finally {
            writeLock.unlock();
        }
    }

    private void handleTransferRequest(Transfer transfer) {
        BigDecimal fromAmount = accountBalances.computeIfAbsent(transfer.getFrom(), (key) -> BigDecimal.ZERO);
        BigDecimal toAmount = accountBalances.computeIfAbsent(transfer.getTo(), (key) -> BigDecimal.ZERO);

        accountBalances.put(transfer.getFrom(), fromAmount.subtract(transfer.getAmount()));
        accountBalances.put(transfer.getTo(), toAmount.add(transfer.getAmount()));
    }

    private void handleDepositRequest(Transfer transfer) {
        BigDecimal toAmount = accountBalances.computeIfAbsent(transfer.getTo(), (key) -> BigDecimal.ZERO);
        accountBalances.put(transfer.getTo(), toAmount.add(transfer.getAmount()));
    }
}
