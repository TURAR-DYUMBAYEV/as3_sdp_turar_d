# Food Delivery Notification System Design Rationale

## Problem and chosen complexity module

A food-delivery system sends order-ready and delivery updates through app, email,
or SMS channels. Notification wording and delivery technology change for different
reasons. The design separates these decisions while allowing an older SMS service
to participate. The selected complexity module is **Dynamic Implementor Selection**.
The system does not implement a two-way adapter.

## Bridge and Adapter together

`Notification` is the Bridge Abstraction. It holds a `Notifier` reference supplied
through its constructor. `OrderReadyNotification` and `DeliveryNotification` are
the two Refined Abstractions: each creates its own message and delegates delivery.
`Notifier` is the Implementor interface. Its three production implementations are
`AppNotifier`, `EmailNotifier`, and `SmsAdapter`. Both notification types work with
all three channels, avoiding separate subclasses for every type/channel pair.

Bridge alone cannot call the incompatible SMS API through `Notifier`. Adapter
alone would make SMS usable, but would not separate the notification hierarchy
from the delivery hierarchy. Together, the adapter occupies one Implementor role
inside the Bridge, so this is one connected design.

## Genuine legacy incompatibility and failure translation

`OldSmsService` is a standalone simulated legacy class. It does not implement
`Notifier` and was not changed to fit it. Its native operation accepts a numeric
phone number, a byte array, and a priority value. It reports success with `0`, an
invalid number with `-1`, and unavailability with `-2`; invalid native arguments
can also cause a runtime exception. The modern operation accepts two strings and
reports delivery failure with the checked `NotificationException`.

`SmsAdapter` converts a supported phone string to `long`, encodes the message as
UTF-8, and supplies the legacy normal-priority constant. It translates every
documented error code, unexpected status codes, conversion failures, and legacy
runtime exceptions into meaningful modern exceptions. It does not expose legacy
codes, exception messages, or causes. Notification classes know only the modern
interface and its common exception contract. Fatal JVM errors are not delivery
failures and are not intercepted.

## Runtime selection and Open/Closed Principle

The client reads the channel from command-line input. `NotifierSelector` uses
Java's `ServiceLoader` to discover implementations listed in
`META-INF/services/fooddelivery.Notifier`, then matches the requested channel.
It contains no channel switch or references to concrete notifier classes.
The adapter has a public no-argument constructor for discovery and a second
constructor accepting the legacy service for testing.

A new notification variant can extend `Notification` without changing existing
notification or delivery classes. A new channel can implement `Notifier` and be
registered in the provider text file without changing existing Java classes.
Client wiring is separate: exposing an additional notification command in the
current CLI would require adding its case in `Main`. The Bridge hierarchies remain
closed to modification; the small CLI deliberately lists the two required types.

## Validation and limitation

JUnit 5 tests use handwritten recording and failing stubs to verify both refined
abstractions, SMS argument conversion, status translation, and exception isolation.
Additional tests verify service discovery and all six notification/channel pairs.
Maven compiles, tests, and packages an executable JAR in one build command.

The main limitation is that delivery is simulated through console output; the
system does not prove a customer actually received a notification. The legacy
numeric phone representation also supports only positive numbers that fit in a
Java `long`; the adapter rejects unsupported formats instead of silently changing
them. This keeps the assignment small enough to explain fully during defense.
