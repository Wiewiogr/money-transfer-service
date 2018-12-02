package pl.tw.transfer;

import java.math.BigDecimal;
import java.util.UUID;

public class DepositRequest {

    public static UUID DEPOSIT_UUID = new UUID(0, 0);

    private final UUID to;
    private final BigDecimal amount;
    private final String title;

    public DepositRequest(UUID to, BigDecimal amount, String title) {
        this.to = to;
        this.amount = amount;
        this.title = title;
    }

    public UUID getTo() {
        return to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTitle() {
        return title;
    }

    public TransferRequest toTransferRequest() {
        return new TransferRequest(DEPOSIT_UUID, to, amount, title);
    }
}
