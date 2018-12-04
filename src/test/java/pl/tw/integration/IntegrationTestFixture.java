package pl.tw.integration;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pl.tw.Application;
import pl.tw.util.DatabaseTestFixture;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class IntegrationTestFixture extends DatabaseTestFixture {

    public Application application;
    public int currentPort;

    private static final AtomicInteger TESTING_PORTS = new AtomicInteger(4000);

    @BeforeMethod
    public void setUpPortAndApplication() throws SQLException {
        currentPort = TESTING_PORTS.incrementAndGet();
        application = new Application(dataSource);
        application.start(currentPort);
    }

    @AfterMethod(alwaysRun = true)
    public void stopApplication() {
        application.stop();
    }
}
