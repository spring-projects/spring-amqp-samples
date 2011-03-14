/*
 * Copyright 2002-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.amqp.rabbit.stocks.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.stocks.domain.Quote;
import org.springframework.amqp.rabbit.stocks.domain.TradeRequest;
import org.springframework.amqp.rabbit.stocks.domain.TradeResponse;
import org.springframework.amqp.rabbit.stocks.gateway.StockServiceGateway;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dave Syer
 * 
 */
@Controller
public class QuoteController {

	private static Log logger = LogFactory.getLog(QuoteController.class);

	private StockServiceGateway stockServiceGateway;

	private ConcurrentMap<String, TradeResponse> responses = new ConcurrentHashMap<String, TradeResponse>();

	private Queue<Quote> quotes = new PriorityBlockingQueue<Quote>(100, new QuoteComparator());

	private long timeout = 30000; // 30 seconds of data

	public void setStockServiceGateway(StockServiceGateway stockServiceGateway) {
		this.stockServiceGateway = stockServiceGateway;
	}

	public void handleTrade(TradeResponse response) {
		logger.info("Client received: " + response);
		String key = response.getRequestId();
		responses.putIfAbsent(key, response);
		Collection<TradeResponse> queue = new ArrayList<TradeResponse>(responses.values());
		long timestamp = System.currentTimeMillis() - timeout;
		for (Iterator<TradeResponse> iterator = queue.iterator(); iterator.hasNext();) {
			TradeResponse tradeResponse = iterator.next();
			if (tradeResponse.getTimestamp() < timestamp) {
				responses.remove(tradeResponse.getRequestId());
			}
		}
	}

	public void handleQuote(Quote message) {
		logger.info("Client received: " + message);
		long timestamp = System.currentTimeMillis() - timeout;
		for (Iterator<Quote> iterator = quotes.iterator(); iterator.hasNext();) {
			Quote quote = iterator.next();
			if (quote.getTimestamp() < timestamp) {
				iterator.remove();
			}
		}
		quotes.add(message);
	}

	@RequestMapping("/quotes")
	@ResponseBody
	public List<Quote> quotes(@RequestParam(required = false) Long timestamp) {
		if (timestamp == null) {
			timestamp = 0L;
		}
		ArrayList<Quote> list = new ArrayList<Quote>();
		for (Quote quote : quotes) {
			if (quote.getTimestamp() > timestamp) {
				list.add(quote);
			}
		}
		Collections.reverse(list);
		return list;
	}

	@RequestMapping(value = "/trade", method = RequestMethod.POST)
	@ResponseBody
	public TradeRequest trade(@ModelAttribute TradeRequest tradeRequest) {
		String ticker = tradeRequest.getTicker();
		Long quantity = tradeRequest.getQuantity();
		if (quantity == null || quantity <= 0 || !StringUtils.hasText(ticker)) {
			// error
			return tradeRequest;
		} else {
			// Fake rest of request while UI is basic
			tradeRequest.setAccountName("ACCT-123");
			tradeRequest.setBuyRequest(true);
			tradeRequest.setOrderType("MARKET");
			tradeRequest.setRequestId("REQ-1");
			tradeRequest.setUserName("Joe Trader");
			tradeRequest.setUserName("Joe");
			stockServiceGateway.send(tradeRequest);
		}
		return tradeRequest;
	}

	@RequestMapping(value = "/trade", method = RequestMethod.GET)
	@ResponseBody
	public TradeResponse response(@RequestParam String requestId) {
		TradeResponse result = responses.get(requestId);
		return result;
	}

	private static class QuoteComparator implements Comparator<Quote> {

		public int compare(Quote o1, Quote o2) {
			return new Long(o1.getTimestamp() - o2.getTimestamp()).intValue();
		}

	}

}
