package camelinaction;
import java.util.*;

class StatCalculator{
	static double calculateAverage(ArrayList<Double> array){
		double sum=0;
		if(!array.isEmpty()){
			for(Object item:array){
				sum+=(Double)item;
			}
		}
		return sum/array.size();
	}
	
	static double calculateVariance(ArrayList<Double> array, double average){
		double sumOfVariance=0;
		if(!array.isEmpty()){
			for(Object item:array){
				double diff=(double)item-average;
				sumOfVariance+=Math.pow(diff,2);
			}
		}
		
		return sumOfVariance/array.size();
	}
	
	static double calcualteAverageMovement(ArrayList<Double> array){
		double sumOfMovement=0;
		if(array.isEmpty() || array.size()==1){
			return sumOfMovement;
		}else{
			double prev=0;
			double curr=0;
			for(int i=0;i<array.size()-1;i++){
				prev=array.get(i);
				curr=array.get(i+1);
				sumOfMovement+=Math.abs(prev-curr);
			}
		}
		return sumOfMovement/array.size();
	}
}

class Stat{
	double currentValue;
	double maxValue=Double.MIN_VALUE;
	double minValue=Double.MAX_VALUE;
	double average;
	double variance;
	double standard_deviation;
	double moving_average;
	ArrayList<Double> valueArray=new ArrayList<>();
	void updateStat(double value){
		currentValue=value;
		maxValue=Math.max(maxValue, value);
		minValue=Math.min(minValue, value);
		valueArray.add(value);
		
		average=StatCalculator.calculateAverage(valueArray);
		variance=StatCalculator.calculateVariance(valueArray, average);
		standard_deviation=Math.sqrt(variance);
		moving_average=StatCalculator.calcualteAverageMovement(valueArray);
	}
}


public class StockStatData {
	Stat bidPriceStat;
	Stat askPriceStat;
	Stat bidAmountStat;
	Stat askAmountStat;
	
	public StockStatData(double bidPrice, double bidAmount, double askPrice, double askAmount){
		bidPriceStat=new Stat();
		askPriceStat=new Stat();
		bidAmountStat=new Stat();
		askAmountStat=new Stat();
		updateStockStat(bidPrice,bidAmount,askPrice,askAmount);
	}
	
	void updateStockStat(double bidPrice, double bidAmount, double askPrice, double askAmount){
		bidPriceStat.updateStat(bidPrice);
		askPriceStat.updateStat(askPrice);
		bidAmountStat.updateStat(bidAmount);
		askAmountStat.updateStat(askAmount);
	}
}
