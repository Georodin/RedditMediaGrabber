package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.swing.Timer;

import controller.Controller;

public class RedditPull implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int pulls = 0;
	public void setController(Controller controller) {
		this.controller = controller;
	}

	public ArrayList<SubReddit> reddits;
	public int minutesInterval = 5;
	public boolean started = false;
	
	public LocalDateTime firstPull;
	public LocalDateTime lastPull;
	public LocalDateTime nextPull;
	transient Controller controller;
	public String xamppPath;
	
	String path;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public RedditPull(int pulls, ArrayList<SubReddit> reddits, Controller controller) {
		this.controller = controller;
		this.pulls = pulls;
		this.reddits = reddits;
	}
	
	public RedditPull(Controller controller) {
		
		this.controller = controller;
		reddits = new ArrayList<>();
	}
	
	public void pullAll() {
		
		if(SQLBridge.isDatabaseOnline()) {
			controller.status = "pulling new media in background";
			for (SubReddit sub : reddits) {

				controller.getMv().frameComponents.updateStats();
				MainRoutine.startRoutine(sub, controller);
			}
			pulls++;
			
			if(firstPull==null) {
				firstPull = LocalDateTime.now();
			}
			lastPull = LocalDateTime.now();
			nextPull = lastPull.plus(minutesInterval, ChronoUnit.MINUTES);
			
			controller.nextPullTimer = new Timer(minutesInterval*60000, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					pullAll();
				}
			});
			
			controller.nextPullTimer.setRepeats(false);
			controller.nextPullTimer.start();
		}else {
			controller.stopRoutine("ERROR: Could not connect to SQL Database...");
		}
	}
	
	public int getProgressPercent() {
		Duration duration;
		try {
			duration = Duration.between(LocalDateTime.now(), nextPull);
		} catch (Exception e) {
			return 0;
		}
		
		int percentage = clamp((float) duration.getSeconds()/(minutesInterval*60)*100, 0, 100);
		return 100-percentage;
	}
	
	static int clamp(float input, int min, int max) {
		int value = Math.round(input);
		return value<min ? min : value>max ? max : value;
	}

	public void updateTimeInterval() {
		if(lastPull!=null) {
			nextPull = lastPull.plus(minutesInterval, ChronoUnit.MINUTES);
			if(nextPull.isBefore(LocalDateTime.now())) {
				if(controller.nextPullTimer!=null) {
					controller.nextPullTimer.stop();
					
					pullAll();
				}
			}else {
				controller.nextPullTimer = new Timer(Math.toIntExact(Duration.between(nextPull, LocalDateTime.now()).toMillis()) , new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						pullAll();
					}
				});
			}
		}
	}
}