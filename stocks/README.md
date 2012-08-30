
### Note

There is a bug that affects Tomcat 7.0.29 (see [Bug 53623](https://issues.apache.org/bugzilla/show_bug.cgi?id=53623). To use Tomcat to run this sample, you will need Tomcat 7.0.30 or a nightly 7.0.x snapshot (not yet available at the time of writing).

### Overview
This branch contains a version of the Spring AMQP stocks sample modified to take advantage of Spring MVC 3.2, Servlet-based async support. The change shows how an existing application with client-side polling can improve its latency while also optimizing the number of requests required to deliver updates. The changes can be viewed in following [commit 1a241f](https://github.com/SpringSource/spring-amqp-samples/commit/1a241f8cd68835fa9e6af4e987bccf7b9e6b8bf1) and although additional changes have been made afterwards, the referenced commit provides a good summary.









