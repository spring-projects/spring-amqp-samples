<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Spring AMQP Log4J Demo</title>
<link rel="stylesheet" href="static/css/main.css" type="text/css"></link>
<link rel="stylesheet" href="static/css/colors.css" type="text/css"></link>
<link rel="stylesheet" href="static/css/local.css" type="text/css"></link>
<script type="text/javascript" src="static/js/jquery.min.js"></script>
<script type="text/javascript" src="static/js/jquery.mustache.js"></script>
<script type="text/javascript">
	var running = false;
	var errorrunning = false;
	var timer;
	var errortimer;
	var debug = false;
	var lastlog = 0;
	var lasterrorlog = 0;
	var template = "{{#logs}}<tr>\
		<td>{{timeString}}</td>\
		<td>{{logger}}</td>\
		<td>{{level}}</td>\
		<td>{{message}}</td>\
		<td>{{applicationId}}</td>\
	</tr>{{/logs}}";
	var errortemplate = "{{#errorlogs}}<tr>\
		<td>{{timeString}}</td>\
		<td>{{logger}}</td>\
		<td>{{level}}</td>\
		<td>{{message}}</td>\
		<td>{{applicationId}}</td>\
	</tr>{{/errorlogs}}";
	var confirmation = "{{#response}}Log Confirmation: <ul>\
		<li>Logger: {{component}}</li>\
		<li>Log type: {{type}}</li>\
		<li>Message: {{message}}</li>\
	</ul>{{/response}}";
	function load() {
		errorlogsload();
		logsload();
	}
	function logsload() {
		if (running) {
			$('#status').text("Waiting...");
			$.ajax({
				url : "logs?timestamp=" + lastlog,
				success : function(message) {
					$('#status').text("Updating");
					if (debug) {
						$('#debug').text(JSON.stringify(message));
					}
					if (message && message.length) {
						lastlog = message[0].timestamp;
						$('#lastlog').prepend($.mustache(template, {
							logs : message
						}));
					}
					timer = poll();
				},
				error : function() {
					$('#status').text("Failed");
					timer = poll();
				},
				cache : false
			});
		} else {
			$('#status').text("Stopped");
		}
	}
	function start() {
		if (!running) {
			running = true;
			if (timer != null) {
				clearTimeout(timer);
			}
			timer = poll();
		}
	}
	function clear() {
		$('#lastlog').html('');
	}
	function stop() {
		$('#status').text("Stopped");
		if (running && timer != null) {
			clearTimeout(timer);
		}
		running = false;
	}
	function poll() {
		if (timer != null) {
			clearTimeout(timer);
		}
		return setTimeout(logsload, 2000);
	}
	// Functions for errorlogs table
	function errorlogsload() {
		if (errorrunning) {
			$('#errorstatus').text("Waiting...");
			$.ajax({
				url : "errorLogs?timestamp=" + lasterrorlog,
				success : function(message) {
					$('#errorstatus').text("Updating");
					if (debug) {
						$('#debug').text(JSON.stringify(message));
					}
					if (message && message.length) {
						lasterrorlog = message[0].timestamp;
						$('#lasterrorlog').prepend($.mustache(errortemplate, {
							errorlogs : message
						}));
					}
					errortimer = errorpoll();
				},
				error : function() {
					$('#errorstatus').text("Failed");
					errortimer = errorpoll();
				},
				cache : false
			});
		} else {
			$('#errorstatus').text("Stopped");
		}
	}
	function errorstart() {
		if (!errorrunning) {
			errorrunning = true;
			if (errortimer != null) {
				clearTimeout(errortimer);
			}
			errortimer = errorpoll();
		}
	}
	function errorclear() {
		$('#lasterrorlog').html('');
	}
	function errorstop() {
		$('#errorstatus').text("Stopped");
		if (errorrunning && errortimer != null) {
			clearTimeout(errortimer);
		}
		errorrunning = false;
	}
	function errorpoll() {
		if (errortimer != null) {
			clearTimeout(errortimer);
		}
		return setTimeout(errorlogsload, 2000);
	}
	
	$(function() {
		$.ajaxSetup({cache:false});
		$('#start').click(start);
		$('#stop').click(stop);
		$('#clear').click(clear);
		$('#errorstart').click(errorstart);
		$('#errorstop').click(errorstop);
		$('#errorclear').click(errorclear);
		start();
		errorstart();
		$('#logAForm')
				.submit(
						function() {
							$
									.post(
											$('#logAForm').attr("action"),
											$('#logAForm').serialize(),
											function(response) {
												if (response && response.message) {
													$('#messages').html($.mustache(confirmation, {
														response : response
													}));
												} else {
													$('#messages').text("The log request for logger 'A' was invalid. Please provide a log level and not empty log message.");
												}
											});
							return false;
						});
		$('#logBForm')
				.submit(
						function() {
							$
									.post(
											$('#logBForm').attr("action"),
											$('#logBForm').serialize(),
											function(response) {
												if (response && response.message) {
													$('#messages').html($.mustache(confirmation, {
														response : response
													}));
												} else {
													$('#messages').text("The log request for logger 'B' was invalid. Please provide a log level and not empty log message.");
												}
											});
							return false;
						});
	});
</script>
</head>
<body>
	<div id="page">
		<div id="header">
			<div id="name-and-company">
				<div id='site-name'>
					<a href="" title="Site Name" rel="home"> Spring AMQP Log4J
						Demo</a>
				</div>
				<div id='company-name'>
					<a href="http://www.springsource.org/spring-amqp"
						title="Spring AMQP"> Spring AMQP Home</a>
				</div>
			</div>
			<!-- /name-and-company -->
		</div>
		<!-- /header -->
		<div id="container">
			<c:choose>
				<c:when test="messageA!=null">
					<c:set var="messageA" value="${messageA}" />
				</c:when>
				<c:otherwise>
					<c:set var="messageA" value="Manual log message" />
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="messageB!=null">
					<c:set var="messageB" value="${messageB}" />
				</c:when>
				<c:otherwise>
					<c:set var="messageB" value="Manual log message" />
				</c:otherwise>
			</c:choose>
		
			<div id="content" class="no-side-nav">
				This application is a the "log4j" sample from <a
					href="http://github.com/SpringSource/spring-amqp">Spring AMQP</a>.
				You can get the source code from the <a
					href="http://github.com/SpringSource/spring-amqp-samples">Spring
					AMQP Samples</a> project on Github.
					
				<br /> <br />
					
				<table class="bordered-table">
					<thead>
						<tr>
							<th>Logs from application 'log4j-sample-A'</th>
							<th>Logs from application 'log4j-sample-B'</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<form id="logAForm" method="post" action="logA">
									<ol>
										<li><label for="messageA">Log level</label>
											<select name="level">
												<option value="info">info</option>
												<option value="debug">debug</option>
												<option value="warn">warn</option>
												<option value="error">error</option>
											</select>
										</li>
										<li><label for="messageA">Log message</label><input id="messageA"
											type="text" name="message" value="${messageA}" />
										</li>
										<li><label for="messageA">Submit log message</label><input type="submit" name="logA" value="Submit" />
										</li>
									</ol>
								</form>
							</td>
							<td>
								<form id="logBForm" method="post" action="logB">
									<ol>
										<li><label for="messageB">Log level</label>
											<select name="level">
												<option value="info">info</option>
												<option value="debug">debug</option>
												<option value="warn">warn</option>
												<option value="error">error</option>
											</select>
										</li>
										<li><label for="messageB">Log message</label><input id="messageB"
											type="text" name="message" value="${messageB}" />
										</li>
										<li><label for="messageB">Submit log message</label><input type="submit" name="logB" value="Submit" />
										</li>
									</ol>
								</form>
							</td>
						</tr>
					</tbody>
				</table>
					
				<div id="messages">
					<form:errors path="*" cssClass="errors" />
				</div>
				<h1>Logs from separate logger queues</h1>
				<div id="status">Stopped</div>
				<br />
				<button id="start">Start</button>
				<button id="stop">Stop</button>
				<button id="clear">Clear</button>
				<br /> <br />
				<table id="logs" class="bordered-table">
					<thead>
						<tr>
							<th>Time</th>
							<th>Logger</th>
							<th>Level</th>
							<th>Message</th>
							<th>Application</th>
						</tr>
					</thead>
					<tbody id="lastlog">
					</tbody>
				</table>
				<h1>Logs from common logger error queue</h1>
				<div id="errorstatus">Stopped</div>
				<br />
				<button id="errorstart">Start</button>
				<button id="errorstop">Stop</button>
				<button id="errorclear">Clear</button>
				<br /> <br />
				<table id="errorlogs" class="bordered-table">
					<thead>
						<tr>
							<th>Time</th>
							<th>Logger</th>
							<th>Level</th>
							<th>Message</th>
							<th>Application</th>
						</tr>
					</thead>
					<tbody id="lasterrorlog">
					</tbody>
				</table>
				<div id="debug"></div>
			</div>
		</div>
	</div>
</body>
</html>