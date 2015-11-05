#RabbitMQ Tutorial Sample Application

This project implements each of the [6 RabbitMQ Tutorials][1] using Spring AMQP.

It is a CLI app that uses Spring Profiles to control its behavior.  Each tutorial is a trio of classes:
sender, receiver, and configuration.

##Usage

The following tutorials are available:

tut1,{sender|receiver}   Hello World
tut2,{sender|receiver}   Work Queues
tut3,{sender|receiver}   Publish/Subscribe
tut4,{sender|receiver}   Routing
tut5,{sender|receiver}   Topics
tut6,{client|server}     RPC

(The order of the profiles is not relevant.)

The app uses Spring Profiles to control what tutorial it's running, and if it's a
Sender or Receiver.  Run the app however you like to run boot apps.  I frequently do
something like this:

```
mvn clean package
java -jar rabbitmq-tutorials.jar --spring.profiles.active=tut1,sender
or
java -jar rabbitmq-tutorials.jar --spring.profiles.active=tut2,receiver
```

For tutorials 1-5, run the Receiver followed by the Sender.

For tutorial 6, run the Server followed by the Client.

##Properties

The `remote` profile causes Spring to load the properties in application-remote.yml that I use for testing.  (You always test
with a non-local server don't you?? :-) )  Don't include it for default (localhost) settings.


Remote Hosts:
-------------

To use to a remote RabbitMQ installation set the following properties as in this sample:

spring.rabbitmq.host=your-rabbit-server
spring.rabbitmq.username=tutorial-user
spring.rabbitmq.password=tutorial-password

To use this at runtime create a file called application-remote.yml (or properties) and set the properties in there.  Then use the 
remote profile.  See the Spring Boot and Spring AMQP documentation for more information on setting application 
properties and AMQP properties specifically.

[1]: https://www.rabbitmq.com/getstarted.html
