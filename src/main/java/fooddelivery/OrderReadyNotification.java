package fooddelivery;

public class OrderReadyNotification extends Notification {
    public OrderReadyNotification(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String recipient, String orderId) throws NotificationException {
        notifier.send(recipient, "Order " + orderId + " is ready for pickup.");
    }
}
