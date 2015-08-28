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
/*
Design Pattern:

In the Trading Engine I used Visitor Pattern for the ReportingEngine to get into the TradingEngine 
and fetch the information and print out the a report for every five messages processed.

I also used Singleton Pattern for the Reporting Engine, because there should only be one instance of Reporting Engine.

I used Composite Pattern to manage the stocks and the portfolio which contains stocks, in order to go through
each stock in every portfolio, I used Iterator Pattern.

I used State pattern to manage different states of the stocks, which include sellState,buyState,waitState and SoldOutState


EIP:

I used Pub-Sub channel for the trading engine here, so that the message can be sent to multiple subscriber.

*/
class TradingEngineProcessor implements Processor{
	Portfolio rootPortfolio=new Portfolio("rootPortfolio");
	ReportingEngine re;
	int processCount=0;
	
	public TradingEngineProcessor(){
		Stock ibm=new Stock("IBM",200);
		ibm.addInterestedStat("bidPrice");
		ibm.addInterestedStat("bidPriceStd");
		ibm.addInterestedStat("askPrice");
		ibm.addInterestedStat("askPriceStd");
		ibm.setTradingRule("bidPrice",170.0, "LARGERTHAN", "SELL");
		
		Stock msft=new Stock("MSFT",100);
		msft.addInterestedStat("bidPrice");
		msft.addInterestedStat("bidPriceStd");
		msft.addInterestedStat("askPrice");
		msft.addInterestedStat("askPriceStd");
		msft.setTradingRule("bidPriceStd", 4.0, "SMALLERTHAN", "BUY");
		
		rootPortfolio.add(ibm);
		rootPortfolio.add(msft);
	}
	
	public void process(Exchange e) throws Exception{
		rootPortfolio.updateStat(e);
		processCount++;
		if(processCount%5==0){
			generateReport();
		}
	}
	
	public void generateReport(){
		re=ReportingEngine.getInstance();
		rootPortfolio.accept(re);
	}
}

public class TradingEngine {

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
            	TradingEngineProcessor tep=new TradingEngineProcessor();
            	from("jms:topic:IBM_Stats").process(tep).to("jms:queue:trading_engine_trash");
            	from("jms:topic:ORCL_Stats").process(tep).to("jms:queue:tranding_engine_trash");
            	from("jms:topic:MSFT_Stats").process(tep).to("jms:queue:tranding_engine_trash");
            }
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(4000000);
        // stop the CamelContext
        context.stop();
    }
}
