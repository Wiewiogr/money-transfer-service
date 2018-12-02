package pl.tw.transfer;

import java.util.List;
import java.util.UUID;

public class TransferRepository {

    public UUID appendTransfer(TransferRequest transferRequest) {
        return UUID.randomUUID();
    }

    public boolean transferExist(UUID transferId) {
        return false;
    }

    public Transfer getTransfer(UUID transferId) {
        return null;
    }

    public List<Transfer> getTransfersForAccountInTimeRange(UUID accountId, long from, long to) {
        return null;
    }
}
