package pl.tw.transfer;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public Transfer(UUID transferId, TransferRequest transferRequest, long timestamp) {
        this.transferId = transferId;
        this.from = transferRequest.getFrom();
        this.to = transferRequest.getTo();
        this.amount = transferRequest.getAmount();
        this.title = transferRequest.getTitle();
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

    public static Transfer fromResultSet(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString(1));
        UUID from = UUID.fromString(resultSet.getString(2));
        UUID to = UUID.fromString(resultSet.getString(3));
        BigDecimal amount = resultSet.getBigDecimal(4);
        String title = resultSet.getString(5);
        long timestamp = resultSet.getTimestamp(6).getTime();
        return new Transfer(id, from, to, amount, title, timestamp);
    }
}
