package fooddelivery;

public class Main {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java -jar target/food-delivery-notifications-1.0.jar "
                    + "<ready|delivery> <channel> <recipient> <orderId>");
            return;
        }

        try {
            Notifier notifier = new NotifierSelector().select(args[1]);
            Notification notification;
            switch (args[0]) {
                case "ready":
                    notification = new OrderReadyNotification(notifier);
                    break;
                case "delivery":
                    notification = new DeliveryNotification(notifier);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown notification type: " + args[0]);
            }
            notification.send(args[2], args[3]);
        } catch (NotificationException | IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
