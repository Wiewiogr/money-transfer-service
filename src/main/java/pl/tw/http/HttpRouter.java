package pl.tw.http;

import pl.tw.account.AccountController;
import pl.tw.transfer.WriteTransferController;

import static spark.Spark.before;
import static spark.Spark.post;

public class HttpRouter {

    private AccountController accountController;
    private WriteTransferController writeTransferController;

    public HttpRouter(AccountController accountController,
                      WriteTransferController writeTransferController) {
        this.accountController = accountController;
        this.writeTransferController = writeTransferController;
    }

    public void setUpRouting() {
        before((request, response) -> response.type("application/json"));

        post("/account", accountController::createAccount, new JsonTransformer());

        post("/transfer", writeTransferController::recordTransfer, new JsonTransformer());
    }
}
