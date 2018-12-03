package pl.tw.account.balance;

import pl.tw.account.AccountRepository;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.math.BigDecimal;
import java.sql.SQLException;
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

        try {
            if (accountRepository.getAccount(accountId) == null) {
                return HttpResponse.error(404, "Account " + accountId + " does not exist.");
            }
        } catch (SQLException e) {
            return HttpResponse.error(500, "Internal server error, contact service owner.");
        }

        return HttpResponse.ok(accountBalanceRepository.getBalance(accountId));
    }
}
