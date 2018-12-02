package pl.tw.account;

import pl.tw.http.HttpResponse;
import spark.Request;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountBalanceController {

    private AccountBalanceRepository accountBalanceRepository;
    private AccountRepository accountRepository;

    public AccountBalanceController(AccountBalanceRepository accountBalanceRepository,
                                    AccountRepository accountRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
        this.accountRepository = accountRepository;
    }

    public HttpResponse<BigDecimal> getBalance(Request req) {
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

        return HttpResponse.ok(accountBalanceRepository.getBalance(accountId));
    }
}
