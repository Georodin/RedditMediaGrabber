package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.JFileChooser;


import controller.Controller;

public class XamppUtility {
	
	public static String checkXamppLocation() {
		File[] roots = File.listRoots();
		for(int i = 0; i < roots.length ; i++) {
			Path path = Paths.get(roots[i]+"xampp");
			if(Files.exists(path)) {
				return path.toString();
			}
		}
		return new java.io.File(".").toString();
	}
	
	public void dialogXamppPath(Controller controller) {
		JFileChooser fc = new JFileChooser(); 

	    fc.setCurrentDirectory(new File(XamppUtility.checkXamppLocation()));
	    fc.setDialogTitle("Select XAMPP root path and deploy viewer");
	    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //
	    // disable the "All files" option.
	    //
	    fc.setAcceptAllFileFilterUsed(false);
	    //    
	    if (fc.showDialog(controller.getMv().frame, "Deploy") == JFileChooser.APPROVE_OPTION) { 
	      controller.changeXamppPath(fc.getSelectedFile().toString()); 
	      updateXAMPPpath(controller);
	      copyDirectory(fc.getSelectedFile().toString()+File.separator+"htdocs"+File.separator+"redditgrabber"+File.separator);
	      controller.saveProfile();
	    }
	}
	
	
	public void copyFile(String destination, String file) {
		try {
			Files.copy(getClass().getResourceAsStream("/deploy/"+file), Paths.get(destination+file), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LogUtility.newLineToLog("ERROR: Could not deploy "+file+" to "+destination+".");
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
	}
	
	//REPLACE WITH https://stackoverflow.com/questions/10308221/how-to-copy-file-inside-jar-to-outside-the-jar
	public void copyDirectory(String destinationDirectoryLocation) {
		
		Path path = Paths.get(destinationDirectoryLocation);
		try {
			Files.createDirectory(path);
		} catch (IOException e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
		
		ArrayList<String> files = new ArrayList<>();
		
		files.add("ajax.php");
		files.add("gallery.svg");
		files.add("index.php");
		files.add("jquery-3.6.3.min.js");
		files.add("moon.svg");
		files.add("Reddit_logo.svg");
		files.add("style.css");
		
		files.forEach(file -> {
			copyFile(destinationDirectoryLocation, file);
		});
		
		
		/*System.out.println("Trying to save to: "+destinationDirectoryLocation);
		 try {
			Path path = Paths.get(destinationDirectoryLocation);
			System.out.println("REACH 00");
			if(!Files.exists(path)) {
				try {
					new File(destinationDirectoryLocation.substring(destinationDirectoryLocation.length()-1)).mkdirs();
				} catch (Exception e) {
					LogUtility.newLineToLog("ERROR: Failed to deploy the xampp viewer files at: "+destinationDirectoryLocation.substring(destinationDirectoryLocation.length()-1));
					StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
				}
			}
			System.out.println("REACH 01");
			System.out.println(getClass().getResource("/deploy").toURI());
		    Files.walk(Paths.get(getClass().getResource("/deploy").toURI()))
		      .forEach(source -> {
		    	  System.out.println("REACH 02222");
		          Path destination = Paths.get(destinationDirectoryLocation, source.getFileName().toString());
		          
		          File f = new File(source.toString());
		          if(!f.isDirectory()) { 
			          try {
			              Files.copy(source, destination);
			          } catch (IOException e) {
			              StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
			          }
		          }
		      });
		    
		    
		 }catch (Exception e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
			LogUtility.newLineToLog("ERROR: Failed to deploy the xampp viewer files at: "+destinationDirectoryLocation);
		}*/
	}
	
	public static void updateXAMPPpath(Controller controller) {
		
		File inputFile = new File(controller.getRp().xamppPath+File.separator+"apache"+File.separator+"conf"+File.separator+"httpd.conf");
		File tempFile = new File(controller.getRp().xamppPath+File.separator+"apache"+File.separator+"conf"+File.separator+"tmp.conf");
		
		
		try{
			tempFile.createNewFile();
			
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String currentLine;
			
			boolean flag_skip = false;
			boolean flag_insert = false;
			boolean flag_found = false;
	
			while((currentLine = reader.readLine()) != null) {
			    // trim newline when comparing with
			    String trimmedLine = currentLine.trim();
			    if(trimmedLine.equals("# redditgrabber content path START")) {
			    	flag_skip = true;
			    	flag_found = true;
			    }
			    
			    if(!flag_skip) {
			    	writer.write(currentLine + System.getProperty("line.separator"));
			    }
			    
			    if(trimmedLine.equals("# redditgrabber content path END")) {
			    	flag_insert = true;
			    	flag_skip = false;
			    }

			    
			    if(flag_insert) {
			    	
			    	String output = ""+
			    			"# redditgrabber content path START" + System.getProperty("line.separator")+
			    			"Alias \"/redditgrabber/cl\" \"" + controller.getRp().getPath().replace('\\', '/') +"/\""+System.getProperty("line.separator")+
			    			//not sure if this is even needed
			    			"<Directory \""+controller.getRp().getPath().replace('\\', '/') +"\">" + System.getProperty("line.separator")+
			    	        "	Options Indexes MultiViews" + System.getProperty("line.separator")+
			    	        "	AllowOverride all" + System.getProperty("line.separator")+
			    	    	"	Require all granted" + System.getProperty("line.separator")+
			    	    	"</Directory>" + System.getProperty("line.separator")+
			    	    	"# redditgrabber content path END" + System.getProperty("line.separator");
			    	
			    	writer.write(output);
			    }
			    
			}
			if(!flag_found) {
		    	String output = ""+
		    			"# redditgrabber content path START" + System.getProperty("line.separator")+
		    			"Alias \"/redditgrabber/cl\" \"" + controller.getRp().getPath().replace('\\', '/') +"/\""+System.getProperty("line.separator")+
		    			//not sure if this is even needed
		    			"<Directory \""+controller.getRp().getPath().replace('\\', '/') +"\">" + System.getProperty("line.separator")+
		    	        "	Options Indexes MultiViews" + System.getProperty("line.separator")+
		    	        "	AllowOverride all" + System.getProperty("line.separator")+
		    	    	"	Require all granted" + System.getProperty("line.separator")+
		    	    	"</Directory>" + System.getProperty("line.separator")+
		    	    	"# redditgrabber content path END" + System.getProperty("line.separator");
		    	
		    	writer.write(output);
			}
			writer.close(); 
			reader.close();
			
			
			
			inputFile.delete();
			boolean successful = tempFile.renameTo(inputFile);
		} catch (IOException e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
			LogUtility.newLineToLog("ERROR: Failed to deploy XAMPP httpd.conf changes.");
		}
	}
}