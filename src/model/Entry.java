package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Entry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String user;
	String userUri;
	String id;
	String uri;
	String date;
	String title;
	String source;
	
	private List<String> media = Collections.synchronizedList(new ArrayList<String>());
	
	public Entry(String user, String userUri, String id, String uri, String date, String title) {
		this.user = user;
		this.userUri = userUri;
		this.id = id;
		this.uri = uri;
		this.date = date;
		this.title = title;
	}

	@Override
	public String toString() {
		if(media==null) {
			return "Entry [user=" + user + ", userUri=" + userUri + ", id=" + id + ", uri=" + uri + ", date=" + date
					+ ", title=" + title + ", media=[null]";
		}else {
			return "Entry [user=" + user + ", userUri=" + userUri + ", id=" + id + ", uri=" + uri + ", date=" + date
					+ ", title=" + title + ", media="+getMedia().stream().map(s -> s+"; ").reduce("", String::concat)+"]";
		}
	}

	public List<String> getMedia() {
		return media;
	}

	public void setMedia(ArrayList<String> media) {
		this.media = media;
	}
	
	public String toSQL() {
		
		String formattedTitle = title.replace("\\", "\\\\").replace("'", "\\'");
		
		
		String output = "(NULL, '"+user+"', '"+userUri+"', '"+id+"', '"+uri+"', '"+date+"', '"+formattedTitle+"', '"+getMedia().stream().map(s -> s+"; ").reduce("", String::concat)+"')";

		return output;
	}
}