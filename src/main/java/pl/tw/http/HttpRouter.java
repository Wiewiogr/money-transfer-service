package pl.tw.http;

import pl.tw.account.AccountController;
import pl.tw.transfer.WriteTransferController;
import spark.Response;

import static spark.Spark.before;
import static spark.Spark.post;

public class HttpRouter {

    private AccountController accountController;
    private WriteTransferController writeTransferController;
    private JsonTransformer transformer = new JsonTransformer();

    public HttpRouter(AccountController accountController,
                      WriteTransferController writeTransferController) {
        this.accountController = accountController;
        this.writeTransferController = writeTransferController;
    }

    public void setUpRouting() {
        before((request, response) -> response.type("application/json"));

        post("/account", (req, res) -> applyStatus(res, accountController.createAccount(req)), transformer);
        post("/transfer", (req, res) -> applyStatus(res, writeTransferController.recordTransfer(req)), transformer);
    }

    private <T> Object applyStatus(Response response, HttpResponse<T> httpResponse) {
        response.status(httpResponse.getStatus());
        if (httpResponse.isError()) {
            return new ErrorMessage(httpResponse.getError());
        } else {
            return httpResponse.getObject();
        }
    }
}
