package pl.tw.http;

import pl.tw.account.AccountBalanceController;
import pl.tw.account.AccountController;
import pl.tw.transfer.AccountTransfersController;
import pl.tw.transfer.ReadTransferController;
import pl.tw.transfer.WriteTransferController;
import spark.Response;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.post;

public class HttpRouter {

    private AccountController accountController;
    private WriteTransferController writeTransferController;
    private ReadTransferController readTransferController;
    private AccountTransfersController accountTransfersController;
    private AccountBalanceController accountBalanceController;

    private JsonTransformer transformer = new JsonTransformer();

    public HttpRouter(AccountController accountController,
                      WriteTransferController writeTransferController,
                      ReadTransferController readTransferController,
                      AccountTransfersController accountTransfersController,
                      AccountBalanceController accountBalanceController) {
        this.accountController = accountController;
        this.writeTransferController = writeTransferController;
        this.readTransferController = readTransferController;
        this.accountTransfersController = accountTransfersController;
        this.accountBalanceController = accountBalanceController;
    }

    public void setUpRouting() {
        before((request, response) -> response.type("application/json"));

        post("/account",
                (req, res) -> applyStatus(res, accountController.createAccount(req)), transformer);
        get("/account/:accountId",
                (req, res) -> applyStatus(res, accountController.getAccount(req)), transformer);

        get("/account/:accountId/balance",
                (req, res) -> applyStatus(res, accountBalanceController.getBalance(req)), transformer);

        get("/account/:accountId/:from/:to",
                (req, res) -> applyStatus(res, accountTransfersController.getTransfersForAccountInTimeRange(req)), transformer);

        post("/transfer",
                (req, res) -> applyStatus(res, writeTransferController.recordTransfer(req)), transformer);

        get("/transfer/:transferId",
                (req, res) -> applyStatus(res, readTransferController.getTransfer(req)), transformer);
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
