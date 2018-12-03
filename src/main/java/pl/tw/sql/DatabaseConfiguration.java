package pl.tw.sql;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseConfiguration {

    public static final String USER = "app";
    public static final String PASSWORD = "app";
    public static final String DRIVER_CLASS_NAME = "org.hsqldb.jdbc.JDBCDriver";
    public static final String DATABASE_URL = "jdbc:hsqldb:mem:app;";

    public DataSource getDataSource() throws SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(DATABASE_URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);
        dataSource.setDefaultAutoCommit(true);

        executeSqlFile(dataSource, "sql/init_transfers_table.sql");
        executeSqlFile(dataSource, "sql/init_account_table.sql");

        return dataSource;
    }

    private void executeSqlFile(BasicDataSource dataSource, String pathToFile) throws SQLException {
        String sql = readResource(pathToFile);

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }

    private String readResource(String name) {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(name);
        return new Scanner(resourceAsStream, "UTF-8")
                .useDelimiter("\\A")
                .next();
    }
}
