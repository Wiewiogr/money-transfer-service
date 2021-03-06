package pl.tw.transfer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import pl.tw.account.balance.AccountBalanceRepository;
import pl.tw.account.AccountController;
import pl.tw.account.AccountRepository;
import pl.tw.eventbus.EventBus;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.sql.SQLException;
import java.util.UUID;

public class WriteTransferController {

    private static Logger LOGGER = Logger.getLogger(AccountController.class);
    private static Gson gson = new Gson();

    private TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final AccountBalanceRepository accountBalanceRepository;
    private EventBus<Transfer> transferEventBus;

    public WriteTransferController(TransferRepository transferRepository,
                                   AccountRepository accountRepository,
                                   AccountBalanceRepository accountBalanceRepository,
                                   EventBus<Transfer> transferEventBus) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.accountBalanceRepository = accountBalanceRepository;
        this.transferEventBus = transferEventBus;
    }

    public HttpResponse<UUID> recordTransfer(Request req) {
        TransferRequest transferRequest;

        try {
            transferRequest = gson.fromJson(req.body(), TransferRequest.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Error parsing request body : " + req.body(), e);
            return HttpResponse.error(400, "Error parsing request body.");
        }

        try {
            if (accountRepository.getAccount(transferRequest.getFrom()) == null) {
                return HttpResponse.error(404, "User " + transferRequest.getFrom() + " not found.");
            }

            if (accountRepository.getAccount(transferRequest.getTo()) == null) {
                return HttpResponse.error(404, "User " + transferRequest.getTo() + " not found.");
            }

            if (accountBalanceRepository.getBalance(transferRequest.getFrom()).compareTo(transferRequest.getAmount()) < 0) {
                return HttpResponse.error(400, "User " + transferRequest.getFrom() + " do not have enough money");
            }

            Transfer transfer = transferRepository.appendTransfer(transferRequest);
            transferEventBus.publish(transfer);
            return HttpResponse.ok(transfer.getTransferId());
        } catch (SQLException e) {
            LOGGER.error("Error accessing data from repository.", e);
            return HttpResponse.error(500, "Internal server error, contact service owner.");
        }
    }

    public HttpResponse<UUID> recordDeposit(Request req) {
        DepositRequest depositRequest;
        try {
            depositRequest = gson.fromJson(req.body(), DepositRequest.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Error parsing request body : " + req.body(), e);
            return HttpResponse.error(400, "Error parsing request body.");
        }

        try {
            if (accountRepository.getAccount(depositRequest.getTo()) == null) {
                return HttpResponse.error(404, "User " + depositRequest.getTo() + " not found.");
            }

            Transfer transfer = transferRepository.appendTransfer(depositRequest.toTransferRequest());
            transferEventBus.publish(transfer);
            return HttpResponse.ok(transfer.getTransferId());
        } catch (SQLException e) {
            LOGGER.error("Error accessing data from repository.", e);
            return HttpResponse.error(500, "Internal server error, contact service owner.");
        }
    }
}
