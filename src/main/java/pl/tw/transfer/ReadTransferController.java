package pl.tw.transfer;

import org.apache.log4j.Logger;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.sql.SQLException;
import java.util.UUID;

public class ReadTransferController {

    private static Logger LOGGER = Logger.getLogger(ReadTransferController.class);

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

        try {
            Transfer transfer = transferRepository.getTransfer(transferId);
            if (transfer != null) {
                return HttpResponse.ok(transfer);
            } else {
                return HttpResponse.error(404, "Transfer " + transferId + " does not exist.");
            }
        } catch (SQLException e) {
            LOGGER.error("Error accessing data from repository.", e);
            return HttpResponse.error(500, "Internal server error, contact service owner.");
        }
    }
}
