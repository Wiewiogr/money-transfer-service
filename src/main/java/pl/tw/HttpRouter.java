package pl.tw;

import pl.tw.account.AccountController;

import static spark.Spark.before;
import static spark.Spark.post;

public class HttpRouter {

    private AccountController accountController;

    public HttpRouter(AccountController accountController) {
        this.accountController = accountController;
    }

    public void setUpRouting() {
        before((request, response) -> response.type("application/json"));

        post("/account", accountController::createAccount);
    }
}
