package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import controller.Controller;

public class MainRoutine {
	
	public static Controller controller;
	
	public static ObservableList<Thread> dowloaders = new ObservableList<>();
	
	public static void startRoutine(SubReddit subReddit, Controller controller) {
		
		MainRoutine.controller = controller;
		
		Runnable runnable = () -> { 

			Path path = Paths.get(controller.getRp().getPath()+File.separator+subReddit.subReddit);
			Path path_previews = Paths.get(controller.getRp().getPath()+File.separator+subReddit.subReddit+File.separator+"previews");
			
			
			//System.out.println(path);
			//System.out.println(path_previews);
			
			if(!Files.exists(path)) {
				try {
					Files.createDirectories(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(!Files.exists(path_previews)) {
				try {
					Files.createDirectories(path_previews);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			RSSgrab.pullRss(new SubReddit(subReddit.subReddit, subReddit.grabNew, subReddit.user, null), controller);
			
			controller.getMv().frameComponents.updateStats();
			controller.getMv().refreshView();
			dowloaders.remove(Thread.currentThread());
		};

		Thread thread = new Thread(runnable);
		dowloaders.add(thread);
		thread.start();
	}
	
	public static void checkDownloader() {
		if(dowloaders.size()==0) {
			controller.status = "waiting for next pull";
			controller.getMv().frameComponents.updateStats();
			controller.getMv().refreshView();
			SQLBridge.updateMetaInfoTable();
		}
	}
}
