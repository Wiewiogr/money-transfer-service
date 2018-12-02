package pl.tw.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import pl.tw.HttpUtils;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.UUID;

public class AccountController {

    private static Logger LOGGER = Logger.getLogger(AccountController.class);

    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String createAccount(Request req, Response res) {
        CreateAccountRequest createAccountRequest;
        try {
            createAccountRequest = objectMapper.readValue(req.body(), CreateAccountRequest.class);
        } catch (IOException e) {
            LOGGER.error("Error parsing request body : " + res.body(), e);

            res.status(400);
            return HttpUtils.errorResponse("Error parsing request body.");
        }

        UUID accountId = accountRepository.createAccount(createAccountRequest);

        res.status(200);
        return HttpUtils.idResponse(accountId.toString());
    }
}
