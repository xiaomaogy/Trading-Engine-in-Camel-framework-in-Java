package camelinaction;
import java.util.*;

import org.apache.camel.Exchange;


public abstract class PortfolioComponent {
	public abstract void accept(ReportingEngine re);
	public void add(PortfolioComponent component){
		throw new UnsupportedOperationException();
	}
	
	public void remove(int index){
		throw new UnsupportedOperationException();
	}
	
	public PortfolioComponent getchild(int index){
		throw new UnsupportedOperationException();
	}
	
	public abstract void updateStat(Exchange e);
	
	public abstract Iterator createIterator();
	
}
