package camelinaction;

public class WaitState implements State{

	Stock s;
	
	public WaitState(Stock s){
		this.s=s;
	}
	
	public void trade() {
		System.out.println("Stock--"+s.stockName+" is waiting");
		
	}

}
