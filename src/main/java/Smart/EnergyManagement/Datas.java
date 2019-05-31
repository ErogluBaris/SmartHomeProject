package Smart.EnergyManagement;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Datas {
	
	public static ArrayList<Double> getPower() {
		return App.SumPower;
	}
	
	public static ArrayList<Double> getPower_before() {
		return App.SumPower_before;
	}
	public static ArrayList<String> getHours() {
		
		ArrayList<String> Hours = new ArrayList<String>();
		 
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		 LocalTime start = LocalTime.parse("00:00");//Starting hour
		 
		 for(int i=0;i<48;i++) {
			 start = start.plusMinutes(30);//we add 30 minutes in every step
			 Hours.add(start.minusMinutes(30).toString()+"-"+start.toString());
		 }
		 return Hours;
	}
}
