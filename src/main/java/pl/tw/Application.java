package pl.tw;

import org.apache.log4j.BasicConfigurator;
import pl.tw.account.AccountController;
import pl.tw.account.AccountRepository;
import pl.tw.account.balance.AccountBalanceController;
import pl.tw.account.balance.AccountBalanceRepository;
import pl.tw.eventbus.EventBus;
import pl.tw.http.HttpRouter;
import pl.tw.sql.DatabaseConfiguration;
import pl.tw.transfer.*;
import spark.Spark;

import javax.sql.DataSource;
import java.sql.SQLException;

import static spark.Spark.port;

public class Application {

    private final HttpRouter router;

    public Application(DataSource dataSource) throws SQLException {
        BasicConfigurator.configure();
        EventBus<Transfer> eventBus = new EventBus<>();

        // Repositories
        AccountRepository accountRepository = new AccountRepository(dataSource);
        AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository(eventBus);
        TransferRepository transferRepository = new TransferRepository(dataSource);

        accountBalanceRepository.recreateState(transferRepository);

        // Controllers
        AccountController accountController = new AccountController(accountRepository);
        ReadTransferController readTransferController = new ReadTransferController(transferRepository);
        WriteTransferController writeTransferController =
                new WriteTransferController(transferRepository, accountRepository, accountBalanceRepository, eventBus);
        AccountBalanceController accountBalanceController =
                new AccountBalanceController(accountBalanceRepository, accountRepository);
        AccountTransfersController accountTransfersController =
                new AccountTransfersController(transferRepository, accountRepository);

        router = new HttpRouter(accountController, writeTransferController, readTransferController, accountTransfersController, accountBalanceController);
        port(8080);
    }

    public void start() {
        router.setUpRouting();
    }

    public void stop() {
        Spark.stop();
    }

    public static void main(String[] args) throws SQLException {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        DataSource dataSource = databaseConfiguration.getDataSource();

        Application application = new Application(dataSource);
        application.start();
    }
}
