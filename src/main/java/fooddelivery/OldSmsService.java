package fooddelivery;

import java.nio.charset.StandardCharsets;

/** Standalone legacy simulation. Its numeric API does not know about Notifier. */
public class OldSmsService {
    public static final int SUCCESS = 0;
    public static final int INVALID_PHONE = -1;
    public static final int UNAVAILABLE = -2;
    public static final int NORMAL_PRIORITY = 1;

    private final boolean available;

    public OldSmsService() {
        this(true);
    }

    public OldSmsService(boolean available) {
        this.available = available;
    }

    public int sendOldMessage(long phoneNumber, byte[] message, int priority) {
        if (phoneNumber <= 0) {
            return INVALID_PHONE;
        }
        if (!available) {
            return UNAVAILABLE;
        }
        if (message == null || message.length == 0 || priority != NORMAL_PRIORITY) {
            throw new IllegalArgumentException("Invalid legacy payload or priority");
        }
        System.out.println("SMS to " + phoneNumber + ": " + new String(message, StandardCharsets.UTF_8));
        return SUCCESS;
    }
}
