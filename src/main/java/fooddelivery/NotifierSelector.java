package fooddelivery;

import java.util.ServiceLoader;

public class NotifierSelector {
    public Notifier select(String channel) {
        for (Notifier notifier : ServiceLoader.load(Notifier.class)) {
            if (notifier.channel().equalsIgnoreCase(channel)) {
                return notifier;
            }
        }
        throw new IllegalArgumentException("Unknown notification channel: " + channel);
    }
}
