package camelinaction;

import java.util.*;

import org.apache.camel.Exchange;

class TradeRule{
	String statName;
	double criticalValue;
	String relationShipToCriticalValue;
	String tradeAction;
	TradeRule(String statName,double criticalValue,String relationShipToCriticalValue,String tradeAction){
		this.statName=statName;
		this.criticalValue=criticalValue;
		this.relationShipToCriticalValue=relationShipToCriticalValue;
		this.tradeAction=tradeAction;
	}
}

public class Stock extends PortfolioComponent{
	String stockName;
	int amount;
	HashMap<String,Double> interestedStat=new HashMap<>();
	ArrayList<TradeRule> rules=new ArrayList<>();
	State buyState;
	State sellState;
	State soldOutState;
	State waitState;
	
	State currentState;
	
	public Stock(String stockName,int amount){
		this.stockName=stockName;
		this.amount=amount;
		sellState=new SellState(this);
		buyState=new BuyState(this);
		soldOutState=new SoldOutState(this);
		currentState=new WaitState(this);
	}
	
	public void updateStat(Exchange e){
		if(!stockName.equals((String)e.getIn().getHeader("stockName"))){
			return;
		}else{
			Iterator it=interestedStat.entrySet().iterator();
			while(it.hasNext()){
				
				Map.Entry<String, Double> pair=(Map.Entry<String, Double>)it.next();
				String key=pair.getKey();
				double value=(double)e.getIn().getHeader(key);
				interestedStat.put(key,value);
			}
		}
		executeTradeRules();
	}
	
	
	public void addInterestedStat(String statName){
		if(!interestedStat.containsKey(statName)){
			interestedStat.put(statName, null);
			System.out.println("Successfully added "+statName+" to the stock:"+stockName);
		}else{
			System.out.println("Added a stat in stock:"+stockName+" which already exists");
		}
	}
	
	public Iterator createIterator(){
		return new NullIterator();
	}

	public void accept(ReportingEngine re) {
		re.visit(this);
	}
	
	public void setTradingRule(String statName, double criticalValue, String relationShipToCriticalValue, String tradeAction){
		TradeRule newRule=new TradeRule(statName,criticalValue,relationShipToCriticalValue,tradeAction);
		rules.add(newRule);
	}
	
	public void executeTradeRules(){
		Iterator it=rules.iterator();
		while(it.hasNext()){
			TradeRule rule=(TradeRule)it.next();
			if(rule.relationShipToCriticalValue.equals("LARGERTHAN")){
				if(this.interestedStat.get(rule.statName)>rule.criticalValue){
					System.out.println("Rule activated, now "+rule.tradeAction+" Stock "+this.stockName+
							" based on "+rule.statName+" "+rule.relationShipToCriticalValue+" "+rule.criticalValue);
					if(rule.tradeAction.equals("SELL")){
						this.currentState=sellState;
					}else if(rule.tradeAction.equals("BUY")){
						this.currentState=buyState;
					}else{
						System.out.println("Wrong tradeAction");
					}
				}
			}else if(rule.relationShipToCriticalValue.equals("SMALLERTHAN")){
				if(this.interestedStat.get(rule.statName)<rule.criticalValue){
					System.out.println("Rule activated, now "+rule.tradeAction+" Stock "+this.stockName+
							" based on "+rule.statName+" "+rule.relationShipToCriticalValue+" "+rule.criticalValue);
					if(rule.tradeAction.equals("SELL")){
						this.currentState=sellState;
					}else if(rule.tradeAction.equals("BUY")){
						this.currentState=buyState;
					}else{
						System.out.println("Wrong tradeAction");
					}
				}
			}else{
				System.out.println("Wrong relationShipToCriticalValue command");
			}
			
		}
		this.currentState.trade();
	}
}
