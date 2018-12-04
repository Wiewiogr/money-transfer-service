package pl.tw.http;

import pl.tw.account.AccountController;
import pl.tw.account.balance.AccountBalanceController;
import pl.tw.transfer.AccountTransfersController;
import pl.tw.transfer.ReadTransferController;
import pl.tw.transfer.WriteTransferController;
import spark.Response;
import spark.Service;

import static spark.Service.ignite;

public class HttpRouter {

    private AccountController accountController;
    private WriteTransferController writeTransferController;
    private ReadTransferController readTransferController;
    private AccountTransfersController accountTransfersController;
    private AccountBalanceController accountBalanceController;

    private JsonTransformer transformer = new JsonTransformer();
    private Service http;

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

    public void setUpRouting(int port) {
        http = ignite().port(port);

        http.before((request, response) -> response.type("application/json"));

        http.post("/account",
                (req, res) -> applyStatus(res, accountController.createAccount(req)), transformer);
        http.get("/account/:accountId",
                (req, res) -> applyStatus(res, accountController.getAccount(req)), transformer);

        http.get("/account/:accountId/balance",
                (req, res) -> applyStatus(res, accountBalanceController.getBalance(req)), transformer);

        http.get("/account/:accountId/:from/:to",
                (req, res) -> applyStatus(res, accountTransfersController.getTransfersForAccountInTimeRange(req)), transformer);

        http.post("/transfer",
                (req, res) -> applyStatus(res, writeTransferController.recordTransfer(req)), transformer);
        http.post("/transfer/deposit",
                (req, res) -> applyStatus(res, writeTransferController.recordDeposit(req)), transformer);

        http.get("/transfer/:transferId",
                (req, res) -> applyStatus(res, readTransferController.getTransfer(req)), transformer);

        http.awaitInitialization();
    }

    public void stop() {
        http.stop();
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
