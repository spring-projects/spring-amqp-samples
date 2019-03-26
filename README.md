This project hosts some samples that complement [Spring AMQP](https://github.com/SpringSource/spring-amqp), showing you how to get started with Spring and AMQP.

# Build Instructions #

Install the [RabbitMQ](https://www.rabbitmq.com) broker first (version
2.3.1 or better).  Then clone from GIT and then use Maven (2.1.*):

    $ git clone ...
    $ mvn install

SpringSource ToolSuite users (or Eclipse users with the latest
m2eclipse plugin) can import the projects as existing Maven projects.

The basic HelloWorld sample has two versions (with synchronous and
asynchronous consumers).  Both have two Java classes called `Producer`
and `Consumer` with main methods to launch.  Run the producer first
and ensure that the broker is already running.

The Stocks sample has a UI that can be launched as a Java main, and a
daemon server process with the same properties.  You can run them from
an IDE easily.  Run the `Server` and then the `Client` and you should
see a swing client pop up and stock tickers appearing.  To run from
the command line you can use the Maven exec plugin:

    $ mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=org.springframework.amqp.rabbit.stocks.Server &
    $ mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=org.springframework.amqp.rabbit.stocks.Client

In the example above we backgrounded the server process, or you could
run it in a different window to make things clearer in the console
logs.

## Web UI

The Stocks sample is also a web application.  You should be able to run it in your IDE, e.g. if you imported the project into STS (or Eclipse with Maven support) you can drag it onto a server, or use `Run As...->On Server`.  From the command line you can use

    $ mvn jetty:run

The web UI is designed like the swing client - it has a ticker table
that updates every second (if there is any new data), and an order
entry form. The `QuoteController` manages the interaction between the
browser and the application and between the application and the AMQP
broker.

## RabbitMQ Tutorials

Spring Boot versions of the [6 standard RabbitMQ tutorials](https://www.rabbitmq.com/getstarted.html) are provided in `rabbitmq-tutorials`.

## Spring Boot Applications

Several Spring Boot applications are also provided, with names `spring-rabbit-...`.
Explore their individual READMEs for more information.

# Contributing to Spring AMQP Samples

Here are some ways for you to get involved in the community:

* Get involved with the Spring community on the Spring Community Forums.  Please help out on the [forum](https://forum.spring.io/forumdisplay.php?f=74) by responding to questions and joining the debate.
* Create [JIRA](https://jira.springsource.org/browse/AMQP) tickets for bugs and new features and comment and vote on the ones that you are interested in.  
* Github is for social coding: if you want to write code, we encourage contributions through pull requests from [forks of this repository](https://help.github.com/forking/).  If you want to contribute code this way, please reference a JIRA ticket as well covering the specific issue you are addressing.
* Watch for upcoming articles on Spring by [subscribing](https://www.springsource.org/node/feed) to springframework.org

Refer to the spring-amqp [Contributor's Guidelines](https://github.com/spring-projects/spring-amqp/blob/master/CONTRIBUTING.adoc) for complete information about the necessary steps.

## Code Conventions and Housekeeping
None of these is essential for a pull request, but they will all help.  They can also be added after the original pull request but before a merge.

* Use the Spring Framework code format conventions (import `eclipse-code-formatter.xml` from the root of the project if you are using Eclipse).
* Make sure all new .java files to have a simple Javadoc class comment with at least an @author tag identifying you, and preferably at least a paragraph on what the class is for.
* Add the ASF license header comment to all new .java files (copy from existing files in the project)
* Add yourself as an @author to the .java files that you modify substantially (moew than cosmetic changes).
* Add some Javadocs and, if you change the namespace, some XSD doc elements (Spring AMQP is not stellar in this area yet, but it will have to come up to scratch one day, so you will be helping a lot).
* A few unit tests would help a lot as well - someone has to do it.
* If no-one else is using your branch, please rebase it against the current master (or other target branch in the main project).
