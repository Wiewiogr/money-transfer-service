package pl.tw.eventbus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus<T> {

    private final List<Consumer<T>> listeners;

    public EventBus() {
        listeners = new ArrayList<>();
    }

    public void subscribe(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void publish(T event) {
        listeners.forEach(listener -> listener.accept(event));
    }
}
