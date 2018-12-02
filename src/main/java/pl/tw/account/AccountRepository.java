package pl.tw.account;

import java.util.UUID;

public class AccountRepository {

    public UUID createAccount(CreateAccountRequest createAccountRequest) {
        return UUID.randomUUID();
    }

    public boolean accountExists(UUID from) {
        return true;
    }
}
