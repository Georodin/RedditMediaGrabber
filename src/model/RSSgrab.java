package model;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controller.Controller;


public class RSSgrab {
	
	static ArrayList<Entry> pullRss(SubReddit subReddit, Controller controller){
		
		String url = null;
		
		if(!subReddit.user) {
			if(subReddit.isGrabNew()) {
				url = "https://www.reddit.com/r/"+subReddit.getSubReddit()+"/new/.rss";
			}else {
				url = "https://www.reddit.com/r/"+subReddit.getSubReddit()+"/.rss";
			}
		}else {
			url = "https://www.reddit.com/user/"+subReddit.getSubReddit()+"/.rss";
		}
		
		List<Entry> output = new ArrayList<Entry>();
		List<DownloadTask> task = new ArrayList<DownloadTask>();
		
		try{
			ExecutorService executor = Executors.newSingleThreadExecutor();
			HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).connectTimeout(Duration.ofSeconds(5)).executor(executor).build();
		    HttpRequest request = HttpRequest.newBuilder()
		          .uri(URI.create(url))
		          .header("Content-Type", "text/plain; charset=UTF-8")
		          .build();

		    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = factory.newDocumentBuilder();
	        ////////System.out.println(response.body());
	        Document doc = db.parse(new ByteArrayInputStream(response.body().toString().getBytes("UTF-8")));
	        
	        NodeList nList = doc.getElementsByTagName("entry");


	        /*
	         	nodeStream.parallel().forEach(nNode -> {
	        	Element eElement = (Element) nNode;

	        	
	        	////////System.out.println(str+System.getProperty("line.separator")+System.getProperty("line.separator"));
	        	
	        	
	        	
	        	String user		= eElement.getElementsByTagName("name").item(0).getTextContent();
	        	String userUri	= eElement.getElementsByTagName("uri").item(0).getTextContent();
	        	String id		= eElement.getElementsByTagName("id").item(0).getTextContent();
	        	String link		= eElement.getElementsByTagName("link").item(0).getAttributes().getNamedItem("href").toString().substring(6).replaceFirst(".$","");
	        	
	    		link = link.substring(0, link.lastIndexOf('/'));
	    		link = link.substring(0, link.lastIndexOf('/')+1);
	        	
	        	String date		= eElement.getElementsByTagName("published").item(0).getTextContent();
	        	String title	= eElement.getElementsByTagName("title").item(0).getTextContent();
	        	
	        	String content	= eElement.getElementsByTagName("content").item(0).getTextContent();
	        	
	        	String contentURL = HTMLparse.getURLA(content);
	        	
	        	if(isSupportedMedia(contentURL)) {
	        		
	        		if(Downloader.downloadFile(contentURL, Controller.path, date)) {
		        		Entry entry = new Entry(user, userUri, id, link, date, title);
		        		entry.setMedia(null);
			        	output.add(entry);
	        		}

	        	}else {
	        		//////System.out.println("Not supported content type: "+contentURL);
	        	};
	        	////////System.out.println(content+System.getProperty("line.separator")+System.getProperty("line.separator"));
	        	

	        	
	        	////////System.out.println(new Entry(user, userUri, id, link, date, title)+System.getProperty("line.separator"));
	        });
	        */
	        
	        ArrayList<Entry> checkIfEntryExist = SQLBridge.getEntrysBySubreddit(subReddit.getSubReddit(), 100);
	        
	        mainloop:
	        for (int temp = 0; temp < nList.getLength(); temp++) {
	        	
	        	Node nNode = nList.item(temp);
	        	Element eElement = (Element) nNode;
	        	
	        	//Document document = eElement.getOwnerDocument();
	        	//DOMImplementationLS domImplLS = (DOMImplementationLS) document
	        	//    .getImplementation();
	        	//LSSerializer serializer = domImplLS.createLSSerializer();
	        	//String str = serializer.writeToString(eElement);
	        	
//	        	//////System.out.println(str+System.getProperty("line.separator")+System.getProperty("line.separator"));
	        	
	        	String user		= eElement.getElementsByTagName("name").item(0).getTextContent();
	        	String userUri	= eElement.getElementsByTagName("uri").item(0).getTextContent();
	        	String id		= eElement.getElementsByTagName("id").item(0).getTextContent();
	        	
	        	for(Entry entry : checkIfEntryExist) {
	        		if(id.equals(entry.id)) {
	        			continue mainloop;
	        		}
	        	}
	        	
	        	
	        	//FIX u_???
	        	String link		= eElement.getElementsByTagName("link").item(0).getAttributes().getNamedItem("href").toString().substring(6).replaceFirst(".$","");
	        	
	    		link = link.substring(0, link.lastIndexOf('/'));
	    		link = link.substring(0, link.lastIndexOf('/')+1);
	    		
	    		
	    		String date = "";
	    		
	    		if(eElement.getElementsByTagName("published").getLength()==0) {
	    			
	    			date		= eElement.getElementsByTagName("updated").item(0).getTextContent();
	    			//LogUtility.saveStringToFile("FAULT: "+str, "eLement");
	        	}else {
	        		date		= eElement.getElementsByTagName("published").item(0).getTextContent();
	        	}

	        	String formattedDate = date.replace('T', ' ').substring(0, date.length()-6);
	        	////////System.out.println("fd: "+formattedDate);
	        	
	        	String title	= eElement.getElementsByTagName("title").item(0).getTextContent();
	        	
	        	String content	= eElement.getElementsByTagName("content").item(0).getTextContent();
	        	
	        	String contentURL = HTMLparse.getURLA(content);
	        	
	        	if(isSupportedMedia(contentURL)) {
	        		Entry entry = new Entry(user, userUri, id, link, formattedDate, title);
	        		////////System.out.println(entry+" | "+contentURL);
	        		task.add(new DownloadTask(entry,contentURL));
	        		
	        		entry.setMedia(null);
		        	output.add(entry);
	        		/*if(Downloader.downloadFile(contentURL, Controller.path, date)) {
		        		Entry entry = new Entry(user, userUri, id, link, date, title);
		        		entry.setMedia(null);
			        	output.add(entry);
	        		}*/
	        	}else {
	        		LogUtility.newLineToLog("Warning: Not supported content type: "+contentURL);
	        	};
	        	
	        };
	        
	        task.parallelStream().forEach(downloadTask -> {
	        	downloadTask.getEntry().setMedia(Downloader.downloadFile(downloadTask.getDownloadURL(), controller.getRp().getPath(), downloadTask.getEntry().date, subReddit.subReddit));
	        });
	        
	        ArrayList<Entry> checkNullEntry = new ArrayList<Entry>(output);
	        
	        checkNullEntry.forEach(o -> {
	        	if(o.getMedia().size()==0) {
	        		output.remove(o);	
	        	}
	        });
	        
	        ////////System.out.println("Kill");
        	executor.shutdownNow();
        	client = null;
        	System.gc();
		}catch (Exception e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
		
		SQLBridge.writeEntrysToSubreddit(subReddit.getSubReddit(), (ArrayList<Entry>) output);

        return (ArrayList<Entry>) output;
	}
	
	static Boolean isSupportedMedia(String toCheck) {
		
		//type single image/video/gif
		if(toCheck.endsWith(".jpg")||toCheck.endsWith(".png")||toCheck.endsWith(".gif")||toCheck.endsWith(".mp4")||toCheck.endsWith(".gifv")) {
			return true;
		}
		//reddit gallery
		else if(toCheck.contains("www.reddit.com/gallery/")) {
			return true;
		}
		//unknown case
		else {
			return false;
		}
	}
}