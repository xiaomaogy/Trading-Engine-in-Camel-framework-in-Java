package camelinaction;

public class SellState implements State{

	Stock s;
	
	public SellState(Stock s){
		this.s=s;
	}
	public void trade() {
		int sellAmount=0;
		if(s.amount<10 && s.amount>0){
			sellAmount=2;
			
		}else if(s.amount>=10){
			sellAmount=s.amount/10;
		}
		s.amount-=sellAmount;
		System.out.println("Sold Stock--"+s.stockName+" for "+sellAmount+" shares.");
		if(s.amount<=0){
			s.currentState=s.soldOutState;
		}
	}


}
