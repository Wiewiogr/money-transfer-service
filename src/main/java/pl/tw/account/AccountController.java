package pl.tw.account;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import pl.tw.http.HttpResponse;
import spark.Request;

import java.sql.SQLException;
import java.util.UUID;

public class AccountController {

    private static Logger LOGGER = Logger.getLogger(AccountController.class);

    private final AccountRepository accountRepository;
    private final Gson gson = new Gson();

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public HttpResponse<UUID> createAccount(Request req) {
        CreateAccountRequest createAccountRequest;
        try {
            createAccountRequest = gson.fromJson(req.body(), CreateAccountRequest.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Error parsing request body : " + req.body(), e);
            return HttpResponse.error(400, "Error parsing request body.");
        }

        try {
            UUID accountId = accountRepository.createAccount(createAccountRequest);
            return HttpResponse.ok(accountId);
        } catch (SQLException e) {
            LOGGER.error("Error accessing data from repository.", e);
            return HttpResponse.error(500, "Internal server error, contact service owner.");
        }
    }

    public HttpResponse<Account> getAccount(Request req) {
        String accountIdParam = req.params("accountId");
        UUID accountId;
        try {
            accountId = UUID.fromString(accountIdParam);
        } catch (IllegalArgumentException e) {
            return HttpResponse.error(400, accountIdParam + " is not a valid UUID.");
        }

        try {
            Account account = accountRepository.getAccount(accountId);
            if (account != null) {
                return HttpResponse.ok(account);
            } else {
                return HttpResponse.error(404, "Account " + accountId + " does not exist.");
            }
        } catch (SQLException e) {
            LOGGER.error("Error accessing data from repository.", e);
            return HttpResponse.error(500, "Internal server error, contact service owner.");
        }
    }
}
