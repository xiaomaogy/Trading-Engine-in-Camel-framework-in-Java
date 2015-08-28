package camelinaction;

import java.util.Iterator;
import java.util.Map;

public class ReportingEngine implements Visitor{
	private static ReportingEngine uniqueInstance;
	
	private ReportingEngine(){};
	
	public static ReportingEngine getInstance(){
		if(uniqueInstance==null){
			uniqueInstance=new ReportingEngine();
		}
		return uniqueInstance;
	}
	
	public void visit(Portfolio p) {
		System.out.println("\n-----------------Reporting Engine 2----------------");
		System.out.println("-------------------------------------------------");
		Iterator iterator=p.createIterator();
		while(iterator.hasNext()){
			PortfolioComponent pc=(PortfolioComponent)iterator.next();
			pc.accept(this);
		}
		
	}

	public void visit(Stock s) {
		System.out.println("\nStock:"+s.stockName+"---"+s.amount+" shares");
		Iterator it=s.interestedStat.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, Double> pair=(Map.Entry<String, Double>)it.next();
			String key=pair.getKey();
			Double value=pair.getValue();
			System.out.println("--"+key+"--"+value+"--");
		}
		System.out.println("--------------------");
		
	}
}
