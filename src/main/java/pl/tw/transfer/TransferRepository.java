package pl.tw.transfer;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransferRepository {

    private DataSource dataSource;
    public static final String GET_TRANSFER_SQL = "SELECT * FROM money_transfer WHERE id=?";
    public static final String GET_TRANSFERS_IN_TIME_RANGE_SQL = "SELECT * FROM money_transfer WHERE from_account=? OR to_account=? AND time BETWEEN ? AND ?";
    public static final String GET_ALL_TRANSFERS_SQL = "SELECT * FROM money_transfer";
    public static final String APPEND_TRANSFER_SQL = "" +
            "INSERT INTO money_transfer (\n" +
            "  id,\n" +
            "  from_account,\n" +
            "  to_account,\n" +
            "  amount,\n" +
            "  title,\n" +
            "  time\n" +
            ") VALUES (?, ?, ?, ?, ?, ?)";

    public TransferRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Transfer appendTransfer(TransferRequest transferRequest) throws SQLException {
        UUID transferId = UUID.randomUUID();
        Instant now = Instant.now();

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(APPEND_TRANSFER_SQL)) {
                statement.setString(1, transferId.toString());
                statement.setString(2, transferRequest.getFrom().toString());
                statement.setString(3, transferRequest.getTo().toString());
                statement.setBigDecimal(4, transferRequest.getAmount());
                statement.setString(5, transferRequest.getTitle());
                statement.setTimestamp(6, Timestamp.from(now));
                statement.execute();
            }
        }

        return new Transfer(transferId, transferRequest, now.toEpochMilli());
    }

    public Transfer getTransfer(UUID transferId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(GET_TRANSFER_SQL)) {
                statement.setString(1, transferId.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return Transfer.fromResultSet(resultSet);
                } else {
                    return null;
                }
            }
        }
    }

    public List<Transfer> getTransfersForAccountInTimeRange(UUID accountId, long from, long to) throws SQLException {
        Instant fromInstant = Instant.ofEpochMilli(from);
        Instant toInstant = Instant.ofEpochMilli(to);

        List<Transfer> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(GET_TRANSFERS_IN_TIME_RANGE_SQL)) {
                statement.setString(1, accountId.toString());
                statement.setString(2, accountId.toString());
                statement.setTimestamp(3, Timestamp.from(fromInstant));
                statement.setTimestamp(4, Timestamp.from(toInstant));
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(Transfer.fromResultSet(resultSet));
                }
            }
        }
        return result;
    }

    public List<Transfer> getAllTransfers() throws SQLException {
        List<Transfer> result = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(GET_ALL_TRANSFERS_SQL)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(Transfer.fromResultSet(resultSet));
                }
            }
        }
        return result;
    }
}
