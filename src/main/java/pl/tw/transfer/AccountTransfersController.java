package pl.tw.transfer;

import org.apache.log4j.Logger;
import pl.tw.account.AccountRepository;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AccountTransfersController {

    private static Logger LOGGER = Logger.getLogger(AccountTransfersController.class);

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public AccountTransfersController(TransferRepository transferRepository,
                                      AccountRepository accountRepository) {

        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    public HttpResponse<List<Transfer>> getTransfersForAccountInTimeRange(Request req) {
        String accountIdParam = req.params("accountId");
        UUID accountId;
        try {
            accountId = UUID.fromString(accountIdParam);
        } catch (IllegalArgumentException e) {
            return HttpResponse.error(400, accountIdParam + " is not a valid UUID.");
        }

        try {
            if (accountRepository.getAccount(accountId) == null) {
                return HttpResponse.error(404, "Account " + accountId + " does not exist.");
            }

            long from = Long.valueOf(req.params("from"));
            long to = Long.valueOf(req.params("to"));

            return HttpResponse.ok(transferRepository.getTransfersForAccountInTimeRange(accountId, from, to));
        } catch (SQLException e) {
            LOGGER.error("Error accessing data from repository.", e);
            return HttpResponse.error(500, "Internal server error, contact service owner.");
        }
    }
}
