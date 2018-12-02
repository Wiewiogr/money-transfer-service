package pl.tw.eventbus;

import org.testng.annotations.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class EventBusTest {

    @Test
    public void shouldPublishToRegisteredSubscribers() {
        // Given
        EventBus<Integer> eventBus = new EventBus<>();
        TestConsumer testConsumer = new TestConsumer();

        // When
        eventBus.subscribe(testConsumer);
        eventBus.publish(10);

        // Then
        assertThat(testConsumer.getValue()).isEqualTo(10);
    }

    public static class TestConsumer implements Consumer<Integer> {

        private Integer value = null;

        @Override
        public void accept(Integer integer) {
            value = integer;
        }

        public Integer getValue() {
            return value;
        }
    }
}