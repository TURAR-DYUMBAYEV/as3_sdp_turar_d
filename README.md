# Food Delivery Notification System

Assignment 3 combines Bridge and Adapter in one small Java console program.
Two notification types can use any of three channels. Delivery is simulated by
console output; no real email, SMS, or app messages are sent.

## Build and test

Requirements: JDK 17 or newer and Maven 3.9 or newer. Open `pom.xml` as a Maven
project in IntelliJ so it imports the source folders and JUnit dependencies.

```shell
mvn clean verify
mvn test
```

The first command compiles, runs tests, and creates the executable JAR.
Dependencies are downloaded on the first build. There are 25 JUnit 5 test cases.

If Maven is not on PATH, this machine has IntelliJ's bundled Maven. The verified
PowerShell commands use a project-local dependency cache:

```powershell
& 'C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.5\plugins\maven\lib\maven3\bin\mvn.cmd' '-Dmaven.repo.local=C:\Users\dumba\IdeaProjects\as3_sdp_turar_d\.maven-cache' --no-transfer-progress clean verify
& 'C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.5\plugins\maven\lib\maven3\bin\mvn.cmd' '-Dmaven.repo.local=C:\Users\dumba\IdeaProjects\as3_sdp_turar_d\.maven-cache' test
```

## Run

Arguments are notification type, channel, recipient, and order ID:

```shell
java -jar target/food-delivery-notifications-1.0.jar ready app customer42 42
java -jar target/food-delivery-notifications-1.0.jar ready email student@example.com 42
java -jar target/food-delivery-notifications-1.0.jar delivery sms +77011234567 42
```

The last example prints:

```text
SMS to 77011234567: Order 42 is on its way.
```

Types are `ready` and `delivery`. Channels are `app`, `email`, and `sms` (channel
matching ignores case). App recipients are user identifiers, email recipients are
email addresses, and SMS recipients are positive decimal phone numbers with an
optional leading `+`. SMS numbers cannot begin with zero, contain separators, or
exceed the positive `long` range. Email addresses are not validated by this demo.

## Classes and patterns

| Class/interface | Responsibility |
| --- | --- |
| `Notification` | Bridge abstraction holding a `Notifier`; declares notification sending. |
| `OrderReadyNotification` | Creates the order-ready message and delegates once. |
| `DeliveryNotification` | Creates the delivery message and delegates once. |
| `Notifier` | Modern channel contract: channel name and send operation. |
| `AppNotifier` | Simulates in-app delivery. |
| `EmailNotifier` | Simulates email delivery. |
| `SmsAdapter` | Converts modern inputs and all legacy delivery failures. |
| `OldSmsService` | Independent legacy API using numbers, bytes, priority, and status codes. |
| `NotificationException` | Checked exception for modern delivery failures. |
| `NotifierSelector` | Discovers providers with ServiceLoader and selects by input. |
| `Main` | Reads command-line arguments, wires objects, and displays errors. |

`Notification` holds an interface reference rather than creating a channel.
This is the Bridge. `SmsAdapter` implements that interface and wraps the legacy
class. This is the Adapter inside the Bridge.

The native legacy service has its own availability constructor for simulating an
outage. It is not altered by the adapter. The SMS adapter catches runtime delivery
exceptions, not fatal JVM errors. Native app/email validation also reports
`NotificationException`.

## Runtime selection and extension

`src/main/resources/META-INF/services/fooddelivery.Notifier` lists exactly three
production providers. ServiceLoader instantiates them through public no-argument
constructors. The selector compares their channel names with the user's input.
There is no hard-coded channel choice for a notification type.

To add a channel later, implement `Notifier` with a distinct channel name and add
its fully qualified name to the provider file. No existing Java class changes.
To add a notification variant, extend `Notification` and delegate to the inherited
notifier. Existing Bridge classes stay unchanged. Exposing that extra type in this
CLI would additionally require a new case in `Main`; client wiring is separate
from extension of the two Bridge hierarchies. This submission keeps exactly the
two requested notification types and three channels.

## Tests

- `NotificationTest`: recording stub verifies recipient, exact message, call count,
  and common failure propagation for both notification types.
- `SmsAdapterTest`: legacy stub verifies conversion and priority; tests all status
  categories, a native exception, actual unavailability, and invalid modern inputs.
- `NotifierSelectorTest`: verifies all three providers, unknown input, all six
  type/channel combinations, and native channels' failure contract.

Stubs are nested inside tests and are not additional production variants.

## Assignment documents and submission

- [UML class diagram](docs/class-diagram.svg)
- [Editable PlantUML source](docs/class-diagram.puml)
- [Design rationale](docs/design-rationale.md) (short Markdown document, under two pages of ordinary formatting)

The UML shows held references with hollow diamonds because injected objects can
be shared; it does not claim exclusive lifetime ownership. This still implements
object composition for Bridge and object adaptation for Adapter.

Domain eligibility was confirmed by the student. Submission also requires a Git
repository link and the individual oral defense (50% of the assignment grade).
Publish this source and the `docs` folder to your chosen repository; omit build
output, local dependencies, and IDE metadata covered by `.gitignore`.
