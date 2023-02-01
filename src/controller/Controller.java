package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.swing.Timer;

import model.*;
import view.*;

public class Controller {
	
	public String status;
	MainView mv;
	RedditPull rp;
	Timer progressTimer;
	public Timer nextPullTimer;
	
	public int selectedIndex = -1;
	
	int[] intervals = {1,3,5,10,15,30,60};
	
	public Controller() {
		SQLBridge.controller = this;
		status = "not started";
		rp = loadProfile();
		if(rp==null) {
			rp = new RedditPull(this);
		}else {
			rp.setController(this);
		}
		
		if(rp.getPath()==null) {
			////System.out.println("pt: "+System.getProperty("user.dir"));
			try {
				Path path = Paths.get(".."+File.separator+"default_media");
				Files.createDirectories(path);
				rp.setPath(new File(".."+File.separator+"default_media").getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		mv = new MainView(this);
		
		progressTimer = new Timer(1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mv.frameComponents.updateProgressBar(rp.getProgressPercent());
				
			}
		});
		progressTimer.setRepeats(true);
		
		
		if(rp.started) {
			rp.pullAll();
			progressTimer.start();
		}
	}
	
	public int getTimeIndex() {
		int index = 0;
		for (int i : intervals) {
			if(i==getRp().minutesInterval) {
				return index;
			}
			index++;
		}
		return 0;
	}
	
	public static void main(String[] args) {
		new Controller().checkCurrentButtonState();
	}

	
	public void changePath(String downloadPath) {
		rp.setPath(downloadPath);
	}
	
	public void changeXamppPath(String xamppPath) {
		rp.xamppPath = xamppPath;
	}
	
	public void changeInterval(int interval) {
		rp.minutesInterval = intervals[interval];
		rp.updateTimeInterval();
	}

	public void removeSelected() {
		if(selectedIndex!=-1) {
			new DeleteWindow(rp ,rp.reddits.get(selectedIndex), this, selectedIndex);
		}
	}

	public void tryAddSubreddit(String reddit, boolean grabNew, boolean isUser) {
		Object[] returnValue = JSONparse.doesSubredditExist(reddit, isUser);
		if((Boolean)returnValue[0]) {
			
			Optional<?> existAlready = rp.reddits.stream().filter(s -> s.getSubReddit().equals(reddit)).findAny();

			if(existAlready.isPresent()) {
				returnValue[1] = reddit+" already in list...";
			}else {
				selectedIndex = -1;
				rp.reddits.add(new SubReddit(reddit, grabNew, isUser, this));
				mv.frameComponents.subViewList();
				FrameComponents.markSelected(selectedIndex);

				saveProfile();
			}
		}
		checkCurrentButtonState();
		Windows.displayWindowMsg((String)returnValue[1]);
	}
	
	void checkCurrentButtonState() {
		if(rp.reddits.size()==0) {
			mv.frameComponents.startStop.setText("Add a Subreddit to start!");
		}else {
			if(rp.started) {
				mv.frameComponents.startStop.setText("Stop");
			}else {
				mv.frameComponents.startStop.setText("Start");
			}
		}
	}
	
	public static RedditPull loadProfile() {
		RedditPull object = null;
		  
        // Deserialization
        try
        {   
        	String userDirectory = new File("").getAbsolutePath()+"\\reddit.profile";
            // Reading the object from a file
            FileInputStream file = new FileInputStream(userDirectory);
            ObjectInputStream in = new ObjectInputStream(file);
              
            // Method for deserialization of object
            object = (RedditPull)in.readObject();
              
            in.close();
            file.close();
           
        }
          
        catch(IOException ex)
        {
            //System.out.println("IOException is caught");
        }
          
        catch(ClassNotFoundException ex)
        {
            //System.out.println("ClassNotFoundException is caught");
        }
		return object;
	}
	
	public void saveProfile() {
        // Serialization 
        try
        {   
        	String userDirectory = new File("").getAbsolutePath()+"\\reddit.profile";
            //Saving of object in a file
        	////System.out.println("save to: "+userDirectory);
            FileOutputStream file = new FileOutputStream(userDirectory);
            ObjectOutputStream out = new ObjectOutputStream(file);
              
            // Method for serialization of object
            out.writeObject(rp);
              
            out.close();
            file.close();
              
            ////System.out.println("Object has been serialized");
        }catch (Exception e) {
        	e.printStackTrace();
		}
    
	}

	public MainView getMv() {
		return mv;
	}

	public void setMv(MainView mv) {
		this.mv = mv;
	}

	public RedditPull getRp() {
		return rp;
	}

	public void setRp(RedditPull rp) {
		this.rp = rp;
	}

	public void stopProgressbar() {
		
	}
	
	public void stopRoutine() {
		stopRoutine(null);
	}
	
	public void stopRoutine(String overwrite) {
		rp.started = false;
		progressTimer.stop();
		mv.frameComponents.updateProgressBar(0);
		if(nextPullTimer!=null) {
			nextPullTimer.stop();
		}
		
		if(overwrite==null) {
			status = "stopped";
		}else {
			status = overwrite;
		}
		
		mv.frameComponents.updateStats();
	}

	public void toggleStartStop() {
		
		if(rp.reddits.size()!=0) {
			if(rp.started) {
				rp.started = false;
				progressTimer.stop();
				mv.frameComponents.updateProgressBar(0);
				nextPullTimer.stop();
				status = "stopped";
				
				mv.frameComponents.updateStats();
				
			}else {
				mv.frameComponents.updateStats();
				rp.started = true;
				progressTimer.start();
				rp.pullAll();

			}
			checkCurrentButtonState();
		}
	}
}
