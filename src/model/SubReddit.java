package model;

import java.io.Serializable;

import controller.Controller;

public class SubReddit implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String subReddit;
	Boolean grabNew;
	boolean user;
	transient Controller controller;

	public SubReddit(String subReddit, Boolean grabNew, boolean user, Controller controller) {
		super();
		this.subReddit = subReddit;
		this.grabNew = grabNew;
		this.user = user;
		
		//System.out.println("null?"+controller);
		//System.out.println("null?"+subReddit);
		
//		Path path = Paths.get(controller.getRp().getDownloadPath()+"/"+subReddit);
//		
//		if(!Files.exists(path)) {
//			try {
//				Files.createDirectories(path);
//			} catch (IOException e) {
//				//System.out.println("serious error, could not create directory at: "+path);
//				StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
//			}
//		}
	}
	
	@Override
	public String toString() {
		return "SubReddit [subReddit=" + subReddit + ", grabNew=" + grabNew + "]";
	}

	public String getSubReddit() {
		return subReddit;
	}

	public void setSubReddit(String subReddit) {
		this.subReddit = subReddit;
	}

	public Boolean isGrabNew() {
		return grabNew;
	}

	public void setGrabNew(Boolean grabNew) {
		this.grabNew = grabNew;
	}
	
}
