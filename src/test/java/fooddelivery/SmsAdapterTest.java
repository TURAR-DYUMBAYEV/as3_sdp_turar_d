package fooddelivery;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class SmsAdapterTest {
    @Test
    void convertsAndDelegatesSuccessfully() throws NotificationException {
        LegacyStub stub = new LegacyStub();
        new SmsAdapter(stub).send("+77011234567", "Order дайын");
        assertEquals(77011234567L, stub.phone);
        assertArrayEquals("Order дайын".getBytes(StandardCharsets.UTF_8), stub.message);
        assertEquals(OldSmsService.NORMAL_PRIORITY, stub.priority);
        assertEquals(1, stub.calls);
    }

    @ParameterizedTest
    @CsvSource({"-1,Invalid phone number.", "-2,Notification service is unavailable.",
            "99,Message delivery failed."})
    void translatesEveryStatus(int status, String expected) {
        LegacyStub stub = new LegacyStub();
        stub.status = status;
        NotificationException failure = assertThrows(NotificationException.class,
                () -> new SmsAdapter(stub).send("77011234567", "Ready"));
        assertEquals(expected, failure.getMessage());
        assertNull(failure.getCause());
    }

    @Test
    void translatesLegacyExceptionWithoutLeakingDetails() {
        LegacyStub stub = new LegacyStub();
        stub.failure = new IllegalArgumentException("Private legacy details");
        NotificationException failure = assertThrows(NotificationException.class,
                () -> new SmsAdapter(stub).send("77011234567", "Ready"));
        assertEquals("Message delivery failed.", failure.getMessage());
        assertNull(failure.getCause());
    }

    @Test
    void translatesActualServiceUnavailability() {
        NotificationException failure = assertThrows(NotificationException.class,
                () -> new SmsAdapter(new OldSmsService(false)).send("77011234567", "Ready"));
        assertEquals("Notification service is unavailable.", failure.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc", "0", "-12", "+", "00123", "7701 1234567", "99999999999999999999999"})
    void rejectsInvalidPhonesBeforeLegacyCall(String phone) {
        LegacyStub stub = new LegacyStub();
        NotificationException failure = assertThrows(NotificationException.class,
                () -> new SmsAdapter(stub).send(phone, "Ready"));
        assertEquals("Invalid phone number.", failure.getMessage());
        assertEquals(0, stub.calls);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsEmptyMessages(String message) {
        LegacyStub stub = new LegacyStub();
        assertThrows(NotificationException.class,
                () -> new SmsAdapter(stub).send("77011234567", message));
        assertEquals(0, stub.calls);
    }

    private static class LegacyStub extends OldSmsService {
        long phone;
        byte[] message;
        int priority;
        int calls;
        int status = SUCCESS;
        RuntimeException failure;

        @Override
        public int sendOldMessage(long phone, byte[] message, int priority) {
            this.phone = phone;
            this.message = message;
            this.priority = priority;
            calls++;
            if (failure != null) {
                throw failure;
            }
            return status;
        }
    }
}
