package pl.tw.account;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import pl.tw.http.HttpUtils;
import spark.Request;
import spark.Response;

import java.util.UUID;

public class AccountController {

    private static Logger LOGGER = Logger.getLogger(AccountController.class);

    private final AccountRepository accountRepository;
    private final Gson gson = new Gson();

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String createAccount(Request req, Response res) {
        CreateAccountRequest createAccountRequest;
        try {
            createAccountRequest = gson.fromJson(req.body(), CreateAccountRequest.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Error parsing request body : " + res.body(), e);

            res.status(400);
            return HttpUtils.errorResponse("Error parsing request body.");
        }

        UUID accountId = accountRepository.createAccount(createAccountRequest);

        res.status(200);
        return HttpUtils.idResponse(accountId.toString());
    }
}
