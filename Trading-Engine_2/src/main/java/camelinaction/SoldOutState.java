package camelinaction;

public class SoldOutState implements State{

	Stock s;
	public SoldOutState(Stock s){
		this.s=s;
	}
	
	public void trade() {
		System.out.println("Sold out on Stock--"+s.stockName);
	}

}
