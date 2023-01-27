package model;

import java.io.File;
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
}