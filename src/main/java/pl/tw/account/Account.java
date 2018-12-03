package pl.tw.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Account {

    private final UUID id;
    private final String name;
    private final String surname;

    public Account(UUID id, String name, String surname) {

        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public static Account fromResultSet(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString(1));
        String name = resultSet.getString(2);
        String surname = resultSet.getString(3);
        return new Account(id, name, surname);
    }
}
