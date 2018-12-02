package pl.tw.transfer;

import java.math.BigDecimal;
import java.util.UUID;

public class Transfer {

    private final UUID transferId;
    private final UUID from;
    private final UUID to;
    private final BigDecimal amount;
    private final String title;
    private long timestamp;

    public Transfer(UUID transferId, UUID from, UUID to, BigDecimal amount, String title, long timestamp) {
        this.transferId = transferId;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.title = title;
        this.timestamp = timestamp;
    }

    public UUID getTransferId() {
        return transferId;
    }

    public UUID getFrom() {
        return from;
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

    public long getTimestamp() {
        return timestamp;
    }
}
