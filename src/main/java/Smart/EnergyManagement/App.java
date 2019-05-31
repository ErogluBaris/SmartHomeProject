package Smart.EnergyManagement;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.LegendPosition;

import sun.awt.SunHints.Value;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale.Category;
import java.util.Scanner;

public class App 
{
	
	public static double MinBulma(double a, double b, double c, double d) {//to obtain which hour has less power after peaktime we use this method
		return Math.min(a, Math.min(b, Math.min(c, d)));
	}
	
	static String title = new String();
	static ArrayList<Double> SumPower = new ArrayList<Double>();//Sum of power values
    static ArrayList<ArrayList<Double> > Values = new ArrayList<ArrayList<Double> > ();//we create 2 dimensional arraylist to save datas
    static ArrayList<Double> PVPanel = new ArrayList<Double>();
	static ArrayList<Double> SumPower_before = new ArrayList<Double>();//The power list after load shifting
	
	
    public static void main( String[] args ) throws IOException, InvalidFormatException
    {   
    	
    	
        String  SAMPLE_XLSX_FILE_PATH = "C:\\PowerTable.xlsx";//This has to be the path of the excel file in your PC
        int devices=19;
        
        for(int i=1;i<=devices;i++) {//with this loop we create rows of the 2d list
        	Values.add(new ArrayList<Double>());
        }
        
        //DATA READ FROM EXCEL
       
       
        Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));//Creating a workbook
        
       
        
        Sheet sheet =workbook.getSheet(GUI.seasonChoose());//The function inside it has the options if it is winter or a summer day
        
       
		Iterator<Row> rowIterator = sheet.rowIterator();//traveling in rows
        
        while(rowIterator.hasNext()) {
        	
        	Row row = rowIterator.next();
        	
        	if(row.getRowNum() == devices+2) {//+2 because there are 2 rows which are not device values	
        		
        		Iterator<Cell> cellIterator = row.cellIterator();//travelling in cells in specified row
        		
        		while(cellIterator.hasNext()) {
        		
        			Cell cell = cellIterator.next();
        			
        			switch(cell.getCellTypeEnum()) {
        				case NUMERIC:
        					SumPower_before.add(cell.getNumericCellValue());//Toplam değerlerini listeye kaydetmiş olduk
        					break;
        				default:
        					System.out.println("");
        			}
        		}
        	}
        	
        	else if( row.getRowNum()>=2 && row.getRowNum()<devices+2) {
        		
        		Iterator<Cell> cellIterator = row.cellIterator();
        		
        		while(cellIterator.hasNext()) {
        			Cell cell = cellIterator.next();
        			
        			switch(cell.getCellTypeEnum()) {//We save all device datas
        				
        				case NUMERIC:
        					Values.get(row.getRowNum()-2).add(cell.getColumnIndex()-1, cell.getNumericCellValue());//-1 because there is a column for device names so we have to consider this
        					break;
        				default:
        					System.out.print("");
        			}
        		}
        		
        	}
        	else if(row.getRowNum() == devices+4) {
        		Iterator<Cell> cellIterator = row.cellIterator();
        		
        		while(cellIterator.hasNext()) {
        			Cell cell = cellIterator.next();
        			
        			switch(cell.getCellTypeEnum() ) {//Power production of PV Panel
        				case NUMERIC:
        					PVPanel.add(cell.getNumericCellValue());
        					break;
        				default:
        					System.out.print("");
        					break;
        			}
        		}
        	}
        	else if(row.getRowNum() == 0) {//To obtain title for the graphs(to understand winter or summer)
        		Iterator<Cell> cellIterator = row.cellIterator();
        		while(cellIterator.hasNext()) {
        			Cell cell = cellIterator.next();
        			switch(cell.getCellTypeEnum() ) {
    				case STRING:
    					title = cell.getStringCellValue();
    					break;
    				default:
    					System.out.print("");
    					break;
        			}
        		}
        	}
        	
        }
        workbook.close();
        
        for(int i=0;i<SumPower_before.size();i++) {
        	SumPower.add(i, SumPower_before.get(i));
        }
          
        //BATTERY AND PV PANEL
        
        double PVpanel_installed = 2000.0;//Installed Power of PV Panel
         double battery_capacity = 3000.0;//( Wh ) Approximately half of the max power
        double charge = battery_capacity*0.2;//charge and discharge
        double discharge = battery_capacity*0.3;
        double SOC = 3000*0.1;//starting value of battery is %10 
        double min_SOC = 3000*0.3;
        double max_SOC = 3000*0.8;
        double normal_SOC = 3000*0.5;//We didn't use that one because when battery is totally discharged it will never charge again because of the night(Solar panels won't produce electricity)
        
        
        double shifting_value = GUI.thresholdChoose();
        
        int peaktime_startingindex = (SumPower.size())/2+(17-12)*2;//Middle of the total size is 12 than we go to 17.00
        int peaktime_endingindex = peaktime_startingindex + (22-17)*2-1;
        
        
        for(int i=0;i<PVPanel.size();i++) {//we obtain the power produced by Pv Panels
        	PVPanel.set(i, PVpanel_installed*PVPanel.get(i)/100);
        }
        
        for(int i=0;i<peaktime_endingindex;i++) {//it charged to the max value
        	if(SOC < max_SOC) {
        		if( PVPanel.get(i) >= charge) {
        			SOC = SOC + charge*0.5;
        		}
        		else if( PVPanel.get(i) < charge ) {
        			SOC = SOC + PVPanel.get(i)*0.5;
        		}
        	}
        	
        }
        
       //SHIFTING
         
        for(int i=peaktime_startingindex;i<=peaktime_endingindex;i++) {
        	int j=18;
        	if(SOC > min_SOC) {
        		SumPower.set(i, SumPower.get(i)-discharge);
        		SOC = SOC - discharge*0.5;
        	}
        	while(SumPower.get(i)>shifting_value) {//if it is below threshold value
        		SumPower.set(i, SumPower.get(i)-Values.get(j).get(i));//update the SumPower list
        		
        			if(Values.get(j).get(i)!=0.0 && j>11 && j<19) {//low priority
        					
        				double min_deger = MinBulma(SumPower.get(peaktime_endingindex+1),SumPower.get(peaktime_endingindex+2),SumPower.get(peaktime_endingindex+3),SumPower.get(peaktime_endingindex+4));
        					if(SumPower.get(peaktime_endingindex+1) == min_deger) {
        						Values.get(j).set(peaktime_endingindex+1, Values.get(j).get(i));
            					Values.get(j).set(i, 0.0);
            					
        					}
        					else if(SumPower.get(peaktime_endingindex+2) == min_deger) {
        						Values.get(j).set(peaktime_endingindex+2, Values.get(j).get(i));
            					Values.get(j).set(i, 0.0);
            					
        					}
        					else if(SumPower.get(peaktime_endingindex+3) == min_deger) {
        						Values.get(j).set(peaktime_endingindex+3, Values.get(j).get(i));
            					Values.get(j).set(i, 0.0);
            					
        					}
        					else if(SumPower.get(peaktime_endingindex+4) == min_deger) {
        						Values.get(j).set(peaktime_endingindex+4, Values.get(j).get(i));
            					Values.get(j).set(i, 0.0);
            					
        					}
        				}
        			
        			else if((Values.get(j).get(i)!=0.0 && j<12 && j>5)) {
        				//We find the min value
        					double min_deger = MinBulma(SumPower.get(peaktime_endingindex+1),SumPower.get(peaktime_endingindex+2),SumPower.get(peaktime_endingindex+3),SumPower.get(peaktime_endingindex+4));
        					if(SumPower.get(peaktime_endingindex+1) == min_deger) {
        						Values.get(j).set(peaktime_endingindex+1, Values.get(j).get(i));
            					Values.get(j).set(i, 0.0);
            					
        					}
        					else if(SumPower.get(peaktime_endingindex+2) == min_deger) {
        						Values.get(j).set(peaktime_endingindex+2, Values.get(j).get(i));
            					Values.get(j).set(i, 0.0);
            					
        					}
        					else if(SumPower.get(peaktime_endingindex+3) == min_deger) {
        						Values.get(j).set(peaktime_endingindex+3, Values.get(j).get(i));
            					Values.get(j).set(i, 0.0);
            					
        					}
        					else if(SumPower.get(peaktime_endingindex+4) == min_deger) {
        						Values.get(j).set(peaktime_endingindex+4, Values.get(j).get(i));
            					Values.get(j).set(i, 0.0);
            					
        					}
        				}
        			
        			else {
        				System.out.print("");
        			}
        		for(int i1=peaktime_endingindex;i1<SumPower.size();i1++) {//update SumPower values after peaktime
        			double toplam=0;
        			for(int j1=0;j1<Values.size();j1++) {
        				toplam+=Values.get(j1).get(i1);
        			}
        			SumPower.set(i1, toplam);
        		}
        		j--;
        	}
        	if(SOC<max_SOC) {//charge in peak hours
        		if( PVPanel.get(i) >= charge) {
        			SOC = SOC + charge*0.5;
        		}
        		else if( PVPanel.get(i) < charge ) {
        			SOC = SOC + PVPanel.get(i)*0.5;
        		}
        	}
        }
        //GRAPH DRAWING
        
        Graph.draw();
   
        //CALCULATIONS
        
        	System.out.println(title);
        	System.out.println("Choosed threshold value : "+ shifting_value+" kW");
        	System.out.println("Daily Cost when Enery Management Algorithm is working : "+Calculation.hesap(peaktime_startingindex, peaktime_endingindex, SumPower)+" TL");
        	System.out.println("Daily Cost when Energy Management Algorithm is not working : "+Calculation.hesap(peaktime_startingindex, peaktime_endingindex, SumPower_before)+" TL");
        	System.out.println("Monthly Cost when Enery Management Algorithm is working : "+Calculation.hesap(peaktime_startingindex, peaktime_endingindex, SumPower)*30+" TL");
        	System.out.println("Monthly Cost when Enery Management Algorithm is not1 working : "+Calculation.hesap(peaktime_startingindex, peaktime_endingindex, SumPower_before)*30+" TL");
        	double gain = (Calculation.hesap(peaktime_startingindex, peaktime_endingindex, SumPower_before)-Calculation.hesap(peaktime_startingindex, peaktime_endingindex, SumPower))/Calculation.hesap(peaktime_startingindex, peaktime_endingindex, SumPower_before);
        	System.out.println("Gain percentage is : "+gain*100);
        	
        }
        
}
