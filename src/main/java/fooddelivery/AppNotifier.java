package fooddelivery;

public class AppNotifier implements Notifier {
    @Override
    public String channel() {
        return "app";
    }

    @Override
    public void send(String recipient, String message) throws NotificationException {
        if (recipient == null || recipient.isBlank() || message == null || message.isBlank()) {
            throw new NotificationException("Recipient and message must not be empty.");
        }
        System.out.println("APP to " + recipient + ": " + message);
    }
}
