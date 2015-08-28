/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*

I used a Content-Based Router here for the messages to be routed to different channels based on its stockName, and
I used an Invalid Message Channel to hold all the invalid messages(All the messages without the corresponding header).

Then they are processed by the StockAnalyzingProcessor, this processor basically first look at the stockName in its
HashMap for the stockName as the key. If there is no such stock, the processor will create a new StockStatData instance
for the new stock, then the processor will fill the StockStatData with the data in the message, and using that 
instance to calculate all of the related statistics. 

In order to calculate the statistics, certain value will also be stored in the corresponding StockStatData instance. 
Since all the statistics are basically related with certain price or Amount and all of their average or 
standard_deviation need to be calculated, I used class Stat to include a set of data related with certain value. 

Finally the calculation of average or standardDeviation are repeated a lot, so I used class StatCalculator to
calculate all the stat data in all these Stat class.


*/
package camelinaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;

class ExtractInfoToHeaderProcessor implements Processor{
	
	public void process(Exchange e) throws Exception{
		String s=e.getIn().getBody(String.class);
		String[] ma=s.split("\t");
		String stockName=ma[0].trim();
		Double bidPrice=Double.parseDouble(ma[1].trim());
		Double bidAmount=Double.parseDouble(ma[2].trim());
		Double askPrice=Double.parseDouble(ma[3].trim());
		Double askAmount=Double.parseDouble(ma[4].trim());
		e.getIn().setHeader("stockName",stockName);
		e.getIn().setHeader("bidPrice",bidPrice);
		e.getIn().setHeader("bidAmount",bidAmount);
		e.getIn().setHeader("askPrice",askPrice);
		e.getIn().setHeader("askAmount",askAmount);
		
	}
	
}

class StockAnalyzingProcessor implements Processor{
	
	HashMap<String, StockStatData> stockStatHashMap=new HashMap<>();
	
	public void process(Exchange e) throws Exception{
		String stockName=(String) e.getIn().getHeader("stockName");
		Double bidPrice=(Double) e.getIn().getHeader("bidPrice");
		Double bidAmount=(Double) e.getIn().getHeader("bidAmount");
		Double askPrice=(Double) e.getIn().getHeader("askPrice");
		Double askAmount=(Double) e.getIn().getHeader("askAmount");
		StockStatData stockStatData;
		if(stockStatHashMap.containsKey(stockName)){
			stockStatData=stockStatHashMap.get(stockName);
			stockStatData.updateStockStat(bidPrice, bidAmount, askPrice, askAmount);
		}else{
			stockStatData=new StockStatData(bidPrice,bidAmount,askPrice,askAmount);
			stockStatHashMap.put(stockName, stockStatData);
		}
		
		e.getIn().setHeader("bidPriceMax", stockStatData.bidPriceStat.maxValue);
		e.getIn().setHeader("bidPriceMin",stockStatData.bidPriceStat.minValue);
		e.getIn().setHeader("bidPriceAverage", stockStatData.bidPriceStat.average);
		e.getIn().setHeader("bidPriceVariance", stockStatData.bidPriceStat.variance);
		e.getIn().setHeader("bidPriceStd", stockStatData.bidPriceStat.standard_deviation);
		e.getIn().setHeader("bidPriceMovAverage", stockStatData.bidPriceStat.moving_average);
		
		e.getIn().setHeader("askPriceMax", stockStatData.askPriceStat.maxValue);
		e.getIn().setHeader("askPriceMin",stockStatData.askPriceStat.minValue);
		e.getIn().setHeader("askPriceAverage", stockStatData.askPriceStat.average);
		e.getIn().setHeader("askPriceVariance", stockStatData.askPriceStat.variance);
		e.getIn().setHeader("askPriceStd", stockStatData.askPriceStat.standard_deviation);
		e.getIn().setHeader("askPriceMovAverage", stockStatData.askPriceStat.moving_average);
		
		e.getIn().setHeader("bidAmountMax", stockStatData.bidAmountStat.maxValue);
		e.getIn().setHeader("bidAmountMin",stockStatData.bidAmountStat.minValue);
		e.getIn().setHeader("bidAmountAverage", stockStatData.bidAmountStat.average);
		e.getIn().setHeader("bidAmountVariance", stockStatData.bidAmountStat.variance);
		e.getIn().setHeader("bidAmountStd", stockStatData.bidAmountStat.standard_deviation);
		e.getIn().setHeader("bidAmountMovAverage", stockStatData.bidAmountStat.moving_average);
		
		e.getIn().setHeader("askAmountMax", stockStatData.askAmountStat.maxValue);
		e.getIn().setHeader("askAmountMin",stockStatData.askAmountStat.minValue);
		e.getIn().setHeader("askAmountAverage", stockStatData.askAmountStat.average);
		e.getIn().setHeader("askAmountVariance", stockStatData.askAmountStat.variance);
		e.getIn().setHeader("askAmountStd", stockStatData.askAmountStat.standard_deviation);
		e.getIn().setHeader("askAmountMovAverage", stockStatData.askAmountStat.moving_average);
	}
}


public class MessageRouter_StatCalculator {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // connect to ActiveMQ JMS broker listening on localhost on port 61616
        ConnectionFactory connectionFactory = 
        	new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms",
            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        
        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            public void configure() {
            	from("jms:queue:dataSource").process(new ExtractInfoToHeaderProcessor())
            	.to("jms:queue:dataSource_movedToHeader");
            	
            	StockAnalyzingProcessor sap=new StockAnalyzingProcessor();
            	
            	
            	from("jms:queue:dataSource_movedToHeader").process(sap).choice()
            		.when(header("stockName").contains("IBM")).to("jms:topic:IBM_Stats")
            		.when(header("stockName").contains("ORCL")).to("jms:topic:ORCL_Stats")
            		.when(header("stockName").contains("MSFT")).to("jms:topic:MSFT_Stats")
            		.otherwise().to("jms:queue:badMessage_Channel")
            		.end();
            }
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(3000000);
        // stop the CamelContext
        context.stop();
    }
}
