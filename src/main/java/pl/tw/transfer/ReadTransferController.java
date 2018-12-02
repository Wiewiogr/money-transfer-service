package pl.tw.transfer;

import pl.tw.http.HttpResponse;
import spark.Request;

import java.util.UUID;

public class ReadTransferController {

    private final TransferRepository transferRepository;

    public ReadTransferController(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public HttpResponse<Transfer> getTransfer(Request req) {
        String accountIdParam = req.params("transferId");
        UUID transferId;
        try {
            transferId = UUID.fromString(accountIdParam);
        } catch (IllegalArgumentException e) {
            return HttpResponse.error(400, accountIdParam + " is not a valid UUID.");
        }

        if (!transferRepository.transferExist(transferId)) {
            return HttpResponse.error(404, "Transfer " + transferId + " does not exist.");
        }

        return HttpResponse.ok(transferRepository.getTransfer(transferId));
    }
}
