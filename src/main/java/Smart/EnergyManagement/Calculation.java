package Smart.EnergyManagement;

import java.util.ArrayList;

public class Calculation {
	
	public static double peaktime_cost = 64.0199*0.01;//Because they are in kr
	public static double daytime_cost = 43.9950*0.01;
	public static double nighttime_cost = 27.9811*0.01;
	
	public static double hesap(int peaktime_s, int peaktime_e, ArrayList<Double> list) {
		double cost=0.0;
    
		for(int hour=0;hour<list.size();hour++) {
			if(hour>=peaktime_s && hour<=peaktime_e) {
				cost += list.get(hour)*0.001*peaktime_cost*0.5;//All of them 0.5 because ve arranged powers in half hour intervals and we prodeuct powers with 0.001 because powers are in watt
			}
			else if(hour>=0 && hour<peaktime_s) {
				cost += list.get(hour)*0.001*daytime_cost*0.5;
			}
			else if(hour>peaktime_e && hour<=list.size()) {
				cost += list.get(hour)*0.001*nighttime_cost*0.5;
			}
		}
		return cost;
		
	}
	
}
