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
        String transferIdParam = req.params("transferId");
        UUID transferId;
        try {
            transferId = UUID.fromString(transferIdParam);
        } catch (IllegalArgumentException e) {
            return HttpResponse.error(400, transferIdParam + " is not a valid UUID.");
        }

        if (!transferRepository.transferExist(transferId)) {
            return HttpResponse.error(404, "Transfer " + transferId + " does not exist.");
        }

        return HttpResponse.ok(transferRepository.getTransfer(transferId));
    }
}
