package pl.tw.account;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountBalanceRepository {

    public BigDecimal getBalance(UUID from) {
        return BigDecimal.ONE;
    }
}
