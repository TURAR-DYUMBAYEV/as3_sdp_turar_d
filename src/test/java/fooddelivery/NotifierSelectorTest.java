package fooddelivery;

import java.util.ServiceLoader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotifierSelectorTest {
    @Test
    void discoversExactlyThreeProviders() {
        assertEquals(3, ServiceLoader.load(Notifier.class).stream().count());
        NotifierSelector selector = new NotifierSelector();
        assertInstanceOf(AppNotifier.class, selector.select("app"));
        assertInstanceOf(EmailNotifier.class, selector.select("email"));
        assertInstanceOf(SmsAdapter.class, selector.select("sms"));
        assertInstanceOf(SmsAdapter.class, selector.select("SMS"));
    }

    @Test
    void rejectsUnknownChannel() {
        assertThrows(IllegalArgumentException.class, () -> new NotifierSelector().select("fax"));
    }

    @Test
    void bothNotificationTypesWorkWithEveryRegisteredChannel() throws NotificationException {
        for (Notifier notifier : ServiceLoader.load(Notifier.class)) {
            String recipient = notifier.channel().equals("sms") ? "77011234567" : "student@example.com";
            new OrderReadyNotification(notifier).send(recipient, "42");
            new DeliveryNotification(notifier).send(recipient, "42");
        }
    }

    @Test
    void nativeChannelsUseCommonFailureContract() {
        assertThrows(NotificationException.class, () -> new AppNotifier().send("", "Ready"));
        assertThrows(NotificationException.class, () -> new EmailNotifier().send("student@example.com", null));
    }
}
