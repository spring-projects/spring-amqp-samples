#RabbitMQ Tutorial Sample Application

This project implements each of the [6 RabbitMQ Tutorials][1] using Spring AMQP.

It is a CLI app that uses Spring Profiles to control its behavior.  Each tutorial is a trio of classes:
sender, receiver, and configuration.

##Usage

The following tutorials are available:

- tut1,{sender|receiver}   Hello World
- tut2,{sender|receiver}   Work Queues
- tut3,{sender|receiver}   Publish/Subscribe
- tut4,{sender|receiver}   Routing
- tut5,{sender|receiver}   Topics
- tut6,{client|server}     RPC

The app uses Spring Profiles to control what tutorial it's running, and if it's a
Sender or Receiver.  Run the app however you like to run boot apps.  I frequently build
the app with maven, and then run it like:

```
java -jar rabbitmq-tutorials.jar --spring.profiles.active=tut2,receiver,remote
```

For tutorials 1-5, run the Receiver followed by the Sender.

For tutorial 6, run the Server followed by the Client.

##Properties

By default, Spring AMQP uses localhost to connect to RabbitMQ.  In the sample, the `remote` profile 
causes Spring to load the properties in `application-remote.yml` that are used for testing with a 
non-local server.  Set your own properties in the one in the project, or provide your own on the
command line when you run it.

To use to a remote RabbitMQ installation set the following properties:

```
spring.rabbitmq.host=<your-rabbit-server>
spring.rabbitmq.username=<tutorial-user>
spring.rabbitmq.password=<tutorial-password>
```

To use this at runtime create a file called `application-remote.yml` (or properties) and set the properties in there.  Then set the 
remote profile as in the example above.  See the Spring Boot and Spring AMQP documentation for more information on setting application 
properties and AMQP properties specifically.

[1]: https://www.rabbitmq.com/getstarted.html
