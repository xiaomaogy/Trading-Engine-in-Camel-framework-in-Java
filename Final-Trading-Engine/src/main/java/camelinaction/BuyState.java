package camelinaction;

public class BuyState implements State{

	Stock s;
	
	public BuyState(Stock s){
		this.s=s;
	}
	
	public void trade() {
		int buyAmount=s.amount/10;
		s.amount+=buyAmount;
		if(s.amount>500){
			System.out.println("exceed maximum amount 500 shares, now stop buying");
			s.currentState=s.waitState;
		}
		System.out.println("Bought Stock--"+s.stockName+" for "+buyAmount+" shares.");
	}

}
