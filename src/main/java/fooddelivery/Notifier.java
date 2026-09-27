package fooddelivery;

public interface Notifier {
    String channel();

    // Successful delivery returns normally; delivery failures use this exception.
    // The recipient must identify a destination supported by the chosen channel.
    void send(String recipient, String message) throws NotificationException;
}
