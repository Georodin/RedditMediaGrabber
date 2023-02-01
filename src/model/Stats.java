package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import controller.Controller;

public class Stats {
	
	static Controller controller;
	
	public Stats(Controller controller) {
		Stats.controller = controller;
	}
	
	public String getStats() {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");;
		String startedFormated = "never";
		String lastFormated = "never";
		if(controller.getRp().firstPull!=null) {
			startedFormated = formatter.format(controller.getRp().firstPull);
		}
		if(controller.getRp().lastPull!=null) {
			lastFormated = formatter.format(controller.getRp().lastPull);
		}
		
		//System.out.println(controller.status);
		
		String output = "<html>"
						+ "currently: "+controller.status+"<br>"
						+ "started: "+startedFormated+"<br>"
						+ "last: "+lastFormated+"<br>"
						+ "size: "+getSize()+"<br>"
						+ "files: "+getFileCount()+"<br>"
						+ "pulls: "+controller.getRp().pulls+"<br>"
						+ "</html>";
		return output;
	}
	
	public static ArrayList<String> getStatArray() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");;
		String startedFormated = "never";
		String lastFormated = "never";
		if(controller.getRp().firstPull!=null) {
			startedFormated = formatter.format(controller.getRp().firstPull);
		}
		if(controller.getRp().lastPull!=null) {
			lastFormated = formatter.format(controller.getRp().lastPull);
		}
		
		ArrayList<String> output = new ArrayList<>();
		output.add(startedFormated);
		output.add(lastFormated);
		output.add(getSize());
		output.add(getFileCount()+"");
		output.add(controller.getRp().pulls+"");
		output.add(controller.getRp().path+"");
		
		return output;
	}
	
	static long getFileCount() { 
	    try {
			return Files.walk(Paths.get(controller.getRp().getPath()))
			            .parallel()
			            .filter(p -> !p.toFile().isDirectory())
			            .count();
		} catch (IOException e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
			return 0;
		}
	  }
	
	static String getSize() {
	    AtomicLong size = new AtomicLong(0);
	    Path folder = Paths.get(controller.getRp().getPath());

	    try {
			Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
			      throws IOException {
			        size.addAndGet(attrs.size());
			        return FileVisitResult.CONTINUE;
			    }
			});
		} catch (IOException e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
			return "error";
		}
	    
	    return humanReadableByteCountSI(size.longValue());
	}
	
	public static String humanReadableByteCountSI(long bytes) {
	    if (-1000 < bytes && bytes < 1000) {
	        return bytes + " B";
	    }
	    CharacterIterator ci = new StringCharacterIterator("kMGTPE");
	    while (bytes <= -999_950 || bytes >= 999_950) {
	        bytes /= 1000;
	        ci.next();
	    }
	    return String.format("%.1f %cB", bytes / 1000.0, ci.current());
	}
}
