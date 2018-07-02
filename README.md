# OneGuard Micro-Service Architecture Core Library

[![Build Status](https://travis-ci.org/OneGuardSolutions/msa-core.svg?branch=master)](https://travis-ci.org/OneGuardSolutions/msa-core)
[![GitHub license](https://img.shields.io/github/license/OneGuardSolutions/msa-core.svg)](https://github.com/OneGuardSolutions/msa-core/blob/master/LICENSE)
[![Coverage Status](https://coveralls.io/repos/github/OneGuardSolutions/msa-core/badge.svg?branch=master)](https://coveralls.io/github/OneGuardSolutions/msa-core?branch=master)
[![Maintainability](https://api.codeclimate.com/v1/badges/46939eb685cb545ba478/maintainability)](https://codeclimate.com/github/OneGuardSolutions/msa-core/maintainability)

Core library of OneGuard Micro-Service Architecture ecosystem for building 
micro-services that communicate with each other asynchronously in Java.

Read [OneGuard Micro-Service Architecture Inter-Service Communication Protocol Specification][1]
for information about inter-service communication protocol this architecture uses.

## Requirements

- JDK 1.8 or later
- Gradle 4+ or Maven 3.2+
- RabbitMQ server
- Basic knowledge of Spring Framework & Spring Boot

## Basic usage

### Set up RabbitMQ broker

Before we can build our micro-service, we need to set up the server
that will handle receiving and sending messages.

RabbitMQ server is freely available at
[http://www.rabbitmq.com/download.html](http://www.rabbitmq.com/download.html).

On Debian / Ubuntu you can install the server with apt:

```bash
sudo apt-get install rabbitmq-server
```

Default configuration will work for this example.

### Creating and registering custom message handler

First we need to add the Maven repository:

```xml
<repositories>
    <repository>
        <id>oneguard-msa</id>
        <url>https://packagecloud.io/oneguard/msa/maven2</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

Or if you're using Gradle:

```gradle
repositories {
  maven {
    url "https://packagecloud.io/oneguard/msa/maven2"
  }
}
```

We also need to add Maven dependency `solutions.oneguard:msa-core`:

```xml
<dependencies>
    <dependency>
        <groupId>solutions.oneguard</groupId>
        <artifactId>msa-core</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

Or if you're using Gradle:

```gradle
dependencies {
    implementation 'solutions.oneguard:msa-core:1.0-SNAPSHOT'
}
```

Minimal `pom.xml` file might look like [this](#minimal-pom-xml-file-example).

First we create a payload class (we use [Lombok](https://projectlombok.org/) library to simplify the example):

```java
/*
 * No arguments constructor is necessary for the parser to be able
 * to map incoming message payload on the instance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Greeting {
    private String greeting;
}
```

Next we can implement our message handler:

```java
@Component
public class GreetingHandler extends AbstractMessageHandler<Greeting> {
    public GreetingHandler() {
        super(Greeting.class);
    }

    @Override
    public void handleMessage(Message<Greeting> message) {
        // this function actually handles incoming messages
        System.out.printf("Received greeting: <%s>\n", message.getPayload().getGreeting());
    }
}
```

Now we can create main `Application` class in which wee bootstrap the Spring Application, and register our handler:

```java
@SpringBootApplication
@Configuration
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public MessageConsumerConfiguration messageConsumerConfiguration(GreetingHandler handler) {
        return new MessageConsumerConfiguration()
            .addHandler("greeting", handler);
    }
}
```

And lastly we configure service name in `src/main/resources/application.properties`:

```properties
oneguard.msa.service.name=greeting-service
```

## Sending messages

To send messages, use class [`solutions.oneguard.msa.core.messaging.MessageProducer`][2].

Issuer is automatically filled in by `MessageProducer` before the message is published.
The `occurredAt` property defaults to current `Date` if not specified.

```java
@Component
public class GreetingSender {
    @Autowired
    private MessageProducer producer;

    public void sendGreeting() {
        producer.sendToService("greeting-service", Message.<Greeting>builder()
            .type("greeting")
            .payload(new Greeting("Hello, service!"))
            .respondToInstance(true) // this will matter later, when we'll be sending responses
            .build()
        );
    }
}
```

## Responding to received messages

When responding to incoming messages, we have to set property `responseTo` to `id` of the received message.
The response should include unmodified `context` form original message. 
The incoming message also specifies (in property `respondToInstance`), if the response sould be sent back
to the specific issuing instance, or the service it belongs to.
This is all nicely taken care of, if you use function [`MessageProducer.sendResponse`][3].

Lets extend our message handler to respond to the greetings.

```java
@Component
public class GreetingHandler extends AbstractMessageHandler<Greeting> {
    private static final Logger log = LoggerFactory.getLogger(GreetingHandler.class);

    @Autowired
    private MessageProducer producer;

    public GreetingHandler() {
        super(Greeting.class);
    }

    @Override
    public void handleMessage(Message<Greeting> message) {
        log.info("Received greeting: <{}>", message.getPayload().getGreeting());

        producer.sendResponse(message, "greeting", new Greeting(
            "Hello, " + message.getIssuer().getName() + '!'
        ));
    }
}
```

### Making requests & handling responses

To send requests, use class [`solutions.oneguard.msa.core.messaging.RequestProducer`][4].

Handling responses is done using [`Mono`][5] from [Project Reactor](https://projectreactor.io/) -
a reactive library for building non-blocking applications in Java.

```java
@Component
public class RequestExample {
    @Autowired
    private RequestProducer producer;
    
    public void sendExampleRequest(String greeting) {
        producer.request(
                Greeting.class,
                "greeting-service",
                Message.<Greeting>builder()
                    .type("greeting.request")
                    .payload(new Greeting("Hello, service!"))
                    .build()
            )
            .timeout(Duration.of(1, ChronoUnit.SECONDS))
            .map(response -> "Greeting from service: " + response.getPayload().getGreeting())
            .doOnNext(System.out::println)
            .doOnError(() ->  System.out.println("Request timed out."))
            .subscribe();
    }
}
```

### Unit testing the handlers

TODO - write documentation

See [Example Echo service][6].

### Integration testing the application

TODO - write documentation

See [Example Echo service][6].

### Minimal `pom.xml` file example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>demo-service</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <java.version>8</java.version>
    </properties>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.3.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <dependencies>
        <dependency>
            <groupId>solutions.oneguard</groupId>
            <artifactId>msa-core</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

    <repositories>
        <repository>
            <id>oneguard-msa</id>
            <url>https://packagecloud.io/oneguard/msa/maven2</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
</project>
```

## See also

- [OneGuard Micro-Service Architecture Inter-Service Communication Protocol Specification](https://github.com/OneGuardSolutions/msa-interservice-communication-protocol-specification)
- [Example Echo service for OneGuard Micro-Service Architecture][6]

[1]: https://github.com/OneGuardSolutions/msa-interservice-communication-protocol-specification
[2]: https://oneguardsolutions.github.io/msa-core/solutions/oneguard/msa/core/messaging/MessageProducer.html
[3]: https://oneguardsolutions.github.io/msa-core/solutions/oneguard/msa/core/messaging/MessageProducer.html#sendResponse-solutions.oneguard.msa.core.model.Message-T-java.lang.String-
[4]: https://oneguardsolutions.github.io/msa-core/solutions/oneguard/msa/core/messaging/RequestProducer.html
[5]: https://projectreactor.io/docs/core/release/reference/#mono
[6]: https://github.com/OneGuardSolutions/msa-exampe-service-echo
