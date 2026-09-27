package fooddelivery;

import java.nio.charset.StandardCharsets;

public class SmsAdapter implements Notifier {
    private final OldSmsService service;

    // ServiceLoader needs a public no-argument constructor.
    public SmsAdapter() {
        this(new OldSmsService());
    }

    public SmsAdapter(OldSmsService service) {
        this.service = service;
    }

    @Override
    public String channel() {
        return "sms";
    }

    @Override
    public void send(String recipient, String message) throws NotificationException {
        if (recipient == null || !recipient.matches("\\+?[1-9][0-9]*")) {
            throw new NotificationException("Invalid phone number.");
        }
        if (message == null || message.isBlank()) {
            throw new NotificationException("Message must not be empty.");
        }

        long phoneNumber;
        try {
            phoneNumber = Long.parseLong(recipient);
        } catch (NumberFormatException exception) {
            throw new NotificationException("Invalid phone number.");
        }

        int result;
        try {
            result = service.sendOldMessage(phoneNumber, message.getBytes(StandardCharsets.UTF_8),
                    OldSmsService.NORMAL_PRIORITY);
        } catch (RuntimeException exception) {
            // Do not expose the legacy exception or its message to callers.
            throw new NotificationException("Message delivery failed.");
        }

        switch (result) {
            case OldSmsService.SUCCESS:
                return;
            case OldSmsService.INVALID_PHONE:
                throw new NotificationException("Invalid phone number.");
            case OldSmsService.UNAVAILABLE:
                throw new NotificationException("Notification service is unavailable.");
            default:
                throw new NotificationException("Message delivery failed.");
        }
    }
}
