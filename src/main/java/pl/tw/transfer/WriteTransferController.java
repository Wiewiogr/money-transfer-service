package pl.tw.transfer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import pl.tw.account.AccountBalanceRepository;
import pl.tw.account.AccountController;
import pl.tw.account.AccountRepository;
import pl.tw.http.ErrorMessage;
import pl.tw.http.IdResponse;
import spark.Request;
import spark.Response;

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

    public Object recordTransfer(Request req, Response res) {
        TransferRequest transferRequest;

        try {
            transferRequest = gson.fromJson(req.body(), TransferRequest.class);
        } catch (JsonSyntaxException e) {
            return handleParsingError(res, e);
        }

        if (!accountRepository.accountExists(transferRequest.getFrom())) {
            return handleUserNotFoundError(res, transferRequest.getFrom());
        }

        if (!accountRepository.accountExists(transferRequest.getTo())) {
            return handleUserNotFoundError(res, transferRequest.getTo());
        }

        if (accountBalanceRepository.getBalance(transferRequest.getFrom()).compareTo(transferRequest.getAmount()) < 0) {
            return handleNotEnoughMoneyError(res, transferRequest.getFrom());
        }

        UUID id = transferRepository.recordTransfer(transferRequest);
        return handleSuccess(res, id);
    }

    private IdResponse handleSuccess(Response res, UUID id) {
        res.status(200);
        return new IdResponse(id);
    }

    private ErrorMessage handleNotEnoughMoneyError(Response res, UUID id) {
        res.status(400);
        return new ErrorMessage("User " + id + " do not have enough money");
    }

    private ErrorMessage handleUserNotFoundError(Response res, UUID id) {
        res.status(404);
        return new ErrorMessage("User " + id + " not found.");
    }

    private ErrorMessage handleParsingError(Response res, Exception e) {
        LOGGER.error("Error parsing request body : " + res.body(), e);

        res.status(400);
        return new ErrorMessage("Error parsing request body.");
    }
}
