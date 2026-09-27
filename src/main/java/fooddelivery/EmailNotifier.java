package fooddelivery;

public class EmailNotifier implements Notifier {
    @Override
    public String channel() {
        return "email";
    }

    @Override
    public void send(String recipient, String message) throws NotificationException {
        if (recipient == null || recipient.isBlank() || message == null || message.isBlank()) {
            throw new NotificationException("Recipient and message must not be empty.");
        }
        System.out.println("EMAIL to " + recipient + ": " + message);
    }
}
