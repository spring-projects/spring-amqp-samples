RabbitMQ Tutorials
------------------

This project implements each of the [6 RabbitMQ Tutorials][1] using Spring AMQP.

Each is a pair of spring boot applications.

For tutorials 1-5, run the `ReceiverApplication` followed by the `SenderApplication`.

For tutorial 6, run the `ServerApplication` followed by the `ClientApplication`.

You can run these within an IDE or use the Spring Boot maven plugin which launches the `Main` class which decides which app to run based on the `runner` system property.

    $ mvn spring-boot:run -Drunner=tut1.Receiver &
    $ mvn spring-boot:run -Drunner=tut1.Sender &

    ...

    $ mvn spring-boot:run -Drunner=tut6.Server &
    $ mvn spring-boot:run -Drunner=tut6.Client &


[1]: https://www.rabbitmq.com/getstarted.html
