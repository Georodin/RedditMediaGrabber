package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LogUtility {
	public static void newLineToLog(Object message) {
	    String converted = (String) message.toString();
		
		try {
		   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		   LocalDateTime now = LocalDateTime.now();  
			
			FileWriter fw = new FileWriter(System.getProperty("user.dir")+"/log.txt", true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    bw.write(dtf.format(now) + " " + converted);
		    bw.newLine();
		    bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}

	}
	
	public static void newLineToErrorLog(Object message) {
		String converted = (String) message.toString();
		
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();  
			
			FileWriter fw = new FileWriter(System.getProperty("user.dir")+"/errorLog.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(dtf.format(now) + " " + converted);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
		
	}
	
	public static void saveStringToFile(Object message, String filename) {
	    String converted = (String) message.toString();
		
		try {
			FileWriter fw = new FileWriter(System.getProperty("user.dir")+"/"+filename+".txt", true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    bw.write(converted);
		    bw.newLine();
		    bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}

	}
	
	public static void openLogFile() {
		try {
			java.awt.Desktop.getDesktop().edit(new File(System.getProperty("user.dir")+"/log.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
	}
}
