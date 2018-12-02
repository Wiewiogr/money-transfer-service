package pl.tw.transfer;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class TransferRequest {

    private final UUID from;
    private final UUID to;
    private final BigDecimal amount;
    private final String title;

    public TransferRequest(UUID from, UUID to, BigDecimal amount, String title) {

        this.from = from;
        this.to = to;
        this.amount = amount;
        this.title = title;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransferRequest that = (TransferRequest) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(title, that.title);
    }
}
