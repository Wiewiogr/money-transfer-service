package pl.tw.transfer;

import java.util.UUID;

public class TransferRepository {

    public UUID recordTransfer(TransferRequest transferRequest) {
        return UUID.randomUUID();
    }
}
