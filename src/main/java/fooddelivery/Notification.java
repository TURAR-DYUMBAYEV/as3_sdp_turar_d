package fooddelivery;

public abstract class Notification {
    protected final Notifier notifier;

    protected Notification(Notifier notifier) {
        this.notifier = notifier;
    }

    public abstract void send(String recipient, String orderId) throws NotificationException;
}
