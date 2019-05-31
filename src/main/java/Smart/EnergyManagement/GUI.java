package Smart.EnergyManagement;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.poi.ss.usermodel.Sheet;

public class GUI {
	
	public static double thresholdChoose() {
		Scanner scan1 = new Scanner(System.in);
		System.out.println("Please enter the threshold value for this calculation");
		System.out.println("WARNİNG!!! : To operate shifting method properly, please enter this value between 2000-4000");
		double threshold = scan1.nextDouble();
		return threshold;
	}
	public static String seasonChoose() {
			
		String result = new String();
		Scanner scan2 = new Scanner(System.in);
		System.out.println("Welcome to the Smart Home Energy Management Project....");
        System.out.println("Please choose the number of options below: ( 1 or 2)");
        System.out.println("1. A Winter Day"+"\n"+"2. A Summer Day");
        
        int answer = scan2.nextInt();
        
    	if(answer==1) {
    	result = "Kis_Gunu";
    	
    	}
    	else if(answer==2) {
    	result = "Yaz_Gunu";
    	
    	}
    	
    	return result;
	
	}
	   
	}

	