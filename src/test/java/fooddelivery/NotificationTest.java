package fooddelivery;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {
    @Test
    void orderReadyDelegatesExactlyOnce() throws NotificationException {
        RecordingNotifier stub = new RecordingNotifier();
        new OrderReadyNotification(stub).send("customer", "42");
        assertEquals("customer", stub.recipient);
        assertEquals("Order 42 is ready for pickup.", stub.message);
        assertEquals(1, stub.calls);
    }

    @Test
    void deliveryDelegatesExactlyOnce() throws NotificationException {
        RecordingNotifier stub = new RecordingNotifier();
        new DeliveryNotification(stub).send("customer", "43");
        assertEquals("customer", stub.recipient);
        assertEquals("Order 43 is on its way.", stub.message);
        assertEquals(1, stub.calls);
    }

    @Test
    void bothNotificationsPreserveModernFailure() {
        RecordingNotifier stub = new RecordingNotifier();
        stub.failure = new NotificationException("Unavailable");
        assertSame(stub.failure, assertThrows(NotificationException.class,
                () -> new OrderReadyNotification(stub).send("customer", "42")));
        assertSame(stub.failure, assertThrows(NotificationException.class,
                () -> new DeliveryNotification(stub).send("customer", "42")));
    }

    private static class RecordingNotifier implements Notifier {
        String recipient;
        String message;
        int calls;
        NotificationException failure;

        public String channel() { return "stub"; }

        public void send(String recipient, String message) throws NotificationException {
            this.recipient = recipient;
            this.message = message;
            calls++;
            if (failure != null) {
                throw failure;
            }
        }
    }
}
