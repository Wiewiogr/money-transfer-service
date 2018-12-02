package pl.tw.transfer;

import pl.tw.account.AccountRepository;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountTransfersController {

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

        if (!accountRepository.accountExists(accountId)) {
            return HttpResponse.error(404, "Account " + accountId + " does not exist.");
        }

        Optional<Long> from = parseParam(req.queryParams("from"));
        Optional<Long> to = parseParam(req.queryParams("to"));

        return HttpResponse.ok(transferRepository.getTransfersForAccountInTimeRange(accountId, from, to));
    }

    private Optional<Long> parseParam(String param) {
        return param == null ? Optional.empty() : Optional.of(Long.valueOf(param));
    }
}
