package model;

public class DownloadTask {
	Entry entry;
	String downloadURL;
	
	public DownloadTask(Entry entry, String downloadURL) {
		super();
		this.entry = entry;
		this.downloadURL = downloadURL;
	}
	
	public Entry getEntry() {
		return entry;
	}
	public void setEntry(Entry entry) {
		this.entry = entry;
	}
	public String getDownloadURL() {
		return downloadURL;
	}
	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}
}
