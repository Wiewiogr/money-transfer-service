package pl.tw.http;

import pl.tw.account.AccountController;
import pl.tw.transfer.ReadTransferController;
import pl.tw.transfer.WriteTransferController;
import spark.Response;

import static spark.Spark.before;
import static spark.Spark.post;

public class HttpRouter {

    private AccountController accountController;
    private WriteTransferController writeTransferController;
    private ReadTransferController readTransferController;
    private JsonTransformer transformer = new JsonTransformer();

    public HttpRouter(AccountController accountController,
                      WriteTransferController writeTransferController,
                      ReadTransferController readTransferController) {
        this.accountController = accountController;
        this.writeTransferController = writeTransferController;
        this.readTransferController = readTransferController;
    }

    public void setUpRouting() {
        before((request, response) -> response.type("application/json"));

        post("/account", (req, res) -> applyStatus(res, accountController.createAccount(req)), transformer);
        post("/account/:accountId", (req, res) -> applyStatus(res, accountController.getAccount(req)), transformer);

        post("/transfer", (req, res) -> applyStatus(res, writeTransferController.recordTransfer(req)), transformer);
        post("/transfer/:transferId", (req, res) -> applyStatus(res,readTransferController.getTransfer(req)), transformer);
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
