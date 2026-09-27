package fooddelivery;

public class DeliveryNotification extends Notification {
    public DeliveryNotification(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String recipient, String orderId) throws NotificationException {
        notifier.send(recipient, "Order " + orderId + " is on its way.");
    }
}
