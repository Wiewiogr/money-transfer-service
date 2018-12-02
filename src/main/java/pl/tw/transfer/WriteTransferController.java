package pl.tw.transfer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import pl.tw.account.AccountBalanceRepository;
import pl.tw.account.AccountController;
import pl.tw.account.AccountRepository;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.util.UUID;

public class WriteTransferController {

    private static Logger LOGGER = Logger.getLogger(AccountController.class);
    private static Gson gson = new Gson();

    private TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final AccountBalanceRepository accountBalanceRepository;

    public WriteTransferController(TransferRepository transferRepository,
                                   AccountRepository accountRepository,
                                   AccountBalanceRepository accountBalanceRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.accountBalanceRepository = accountBalanceRepository;
    }

    public HttpResponse<UUID> recordTransfer(Request req) {
        TransferRequest transferRequest;

        try {
            transferRequest = gson.fromJson(req.body(), TransferRequest.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Error parsing request body : " + req.body(), e);
            return HttpResponse.error(400, "Error parsing request body.");
        }

        if (!accountRepository.accountExists(transferRequest.getFrom())) {
            return HttpResponse.error(404, "User " + transferRequest.getFrom() + " not found.");
        }

        if (!accountRepository.accountExists(transferRequest.getTo())) {
            return HttpResponse.error(404, "User " + transferRequest.getTo() + " not found.");
        }

        if (accountBalanceRepository.getBalance(transferRequest.getFrom()).compareTo(transferRequest.getAmount()) < 0) {
            return HttpResponse.error(400, "User " + transferRequest.getFrom() + " do not have enough money");
        }

        UUID id = transferRepository.appendTransfer(transferRequest);
        return HttpResponse.ok(id);
    }
}
