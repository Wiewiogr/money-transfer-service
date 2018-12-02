package pl.tw.http;

import java.util.UUID;

public class IdResponse {

    private UUID id;

    public IdResponse(UUID id) {

        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
