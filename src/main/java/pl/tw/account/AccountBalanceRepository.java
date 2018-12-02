package pl.tw.account;

import pl.tw.eventbus.EventBus;
import pl.tw.transfer.TransferRequest;

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

    AccountBalanceRepository(EventBus<TransferRequest> transferRequestEventBus) {
        transferRequestEventBus.subscribe(this::onTransferEvent);
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

    void onTransferEvent(TransferRequest transferRequest) {
        Lock writeLock = this.lock.writeLock();
        try {
            BigDecimal fromAmount = accountBalances.computeIfAbsent(transferRequest.getFrom(), (key) -> BigDecimal.ZERO);
            BigDecimal toAmount = accountBalances.computeIfAbsent(transferRequest.getTo(), (key) -> BigDecimal.ZERO);

            accountBalances.put(transferRequest.getFrom(), fromAmount.subtract(transferRequest.getAmount()));
            accountBalances.put(transferRequest.getTo(), toAmount.add(transferRequest.getAmount()));

            writeLock.lock();
        } finally {
            writeLock.unlock();
        }
    }
}
