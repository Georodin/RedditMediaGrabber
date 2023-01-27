package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	
	public void copyDirectory(String destinationDirectoryLocation) {
		 try {
			Path path = Paths.get(destinationDirectoryLocation);
			
			if(!Files.exists(path)) {
				try {
					Files.createDirectories(path);
				} catch (IOException e) {
					LogUtility.newLineToLog("ERROR: Failed to deploy the xampp viewer files at: "+destinationDirectoryLocation);
					e.printStackTrace();
				}
			}
		    Files.walk(Paths.get(getClass().getResource("/deploy").toURI()))
		      .forEach(source -> {
		    	  
		          Path destination = Paths.get(destinationDirectoryLocation, source.getFileName().toString());
		          
		          File f = new File(source.toString());
		          if(!f.isDirectory()) { 
			          try {
			              Files.copy(source, destination);
			          } catch (IOException e) {
			              e.printStackTrace();
			          }
		          }
		      });
		    
		    
		 }catch (Exception e) {
			e.printStackTrace();
			LogUtility.newLineToLog("ERROR: Failed to deploy the xampp viewer files at: "+destinationDirectoryLocation);
		}
	}
	
	public static void updateXAMPPpath(Controller controller) {
		
		System.out.println("Hello?");
		
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
			    			"Alias \"/redditgrabber/cl\" \"" + controller.getRp().getPath()+"\""+System.getProperty("line.separator")+
			    			"<Directory \""+controller.getRp().getPath()+File.separator+"\">" + System.getProperty("line.separator")+
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
		    			"Alias \"/redditgrabber/cl\" \"" + controller.getRp().getPath()+"\""+System.getProperty("line.separator")+
		    			"<Directory \""+controller.getRp().getPath()+File.separator+"\">" + System.getProperty("line.separator")+
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
			System.out.println(successful);
		} catch (IOException e) {
			e.printStackTrace();
			LogUtility.newLineToLog("ERROR: Failed to deploy XAMPP httpd.conf changes.");
		}
	}
}