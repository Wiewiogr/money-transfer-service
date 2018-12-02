package pl.tw.account;

import java.util.Objects;

public class CreateAccountRequest {

    private final String name;
    private final String surname;

    public CreateAccountRequest(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateAccountRequest that = (CreateAccountRequest) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(surname, that.surname);
    }
}
