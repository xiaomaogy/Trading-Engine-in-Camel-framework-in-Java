package camelinaction;
import java.util.*;

import org.apache.camel.Exchange;


public class Portfolio extends PortfolioComponent{
	ArrayList<PortfolioComponent> components=new ArrayList();
	String name;
	
	public Portfolio(String name){
		this.name=name;
	}
	
	public void add(PortfolioComponent component){
		components.add(component);
	}
	
	public void remove(int index){
		components.remove(index);
	}
	
	public PortfolioComponent getChild(int index){
		return (PortfolioComponent)components.get(index);
	}
	
	public Iterator createIterator(){
		return (Iterator) components.iterator();
	}
	
	
	public void updateStat(Exchange e){
		Iterator iterator=createIterator();
		while(iterator.hasNext()){
			PortfolioComponent pc=(PortfolioComponent)iterator.next();
			pc.updateStat(e);
		}
	}
	
	
	public int countPortfolio(){
		Iterator iterator=createIterator();
		int count=0;
		while(iterator.hasNext()){
			if(iterator.next() instanceof Portfolio){
				count++;
			}
		}
		return count;
	}
	
	public int countTotalStock(){
		Iterator iterator=createIterator();
		int count=0;
		while(iterator.hasNext()){
			if(iterator.next() instanceof Stock){
				count++;
			}
		}
		return count;
	}

	public void accept(ReportingEngine re) {
		re.visit(this);
	}
}

