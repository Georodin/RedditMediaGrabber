package model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Downloader {
	
	public static ArrayList<String> downloadFile(String urlstring, String path, String date, String subreddit) {
		
		ArrayList<String> output = new ArrayList<String>();
		
		if(urlstring.contains("www.reddit.com/gallery/")) {
			return downloadGallery(urlstring, path, date, subreddit);
		}
		
		if(urlstring.contains(".gifv")) {
			return downloadGIFV(urlstring, path, date, subreddit);
		}
		
		try {
			URL url = new URL(urlstring);
			ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
			
			date = date.substring(0,10).replace("-", "");
			String outputPath = path+"/"+subreddit+"/"+date+"_"+url.getFile().split("/")[url.getFile().split("/").length-1];
			
			////System.out.println("urlPath: "+urlstring);
			////System.out.println("outputPath: "+outputPath);
			
			File f = new File(outputPath);
			if(!f.exists()) { 
				FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
				fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
				fileOutputStream.close();
			}
			String filename_only = date+"_"+url.getFile().substring(1);
			output.add(filename_only);
			
			ImageConverter.createPreview(outputPath);
			
			return output;
		} catch (Exception e) {
			
			LogUtility.newLineToLog("ERROR: Could not download - "+urlstring);
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
			return output;
		}

	}

	
	public static ArrayList<String> downloadGallery(String urlstring, String path, String date, String subreddit) {
		ArrayList<String> fileNames = new ArrayList<String>();
		boolean first = true;
		try {
			Document doc = Jsoup.connect(urlstring).get();
			//LogUtility.newLineToLog(doc);
			Elements images = doc.select("a");
			date = date.substring(0,10).replace("-", "");
			for (Element image : images) {
				if(image.attr("href").contains("&format=")) {
					URL url = new URL(image.attr("href"));
					ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
					
					
					String outputPath = path+"/"+subreddit+"/"+date+"_"+url.getFile().substring(1);
					outputPath = outputPath.substring(0, outputPath.indexOf('?'));
					
					String filename_only = outputPath.substring(outputPath.lastIndexOf('/')+1, outputPath.length());
					fileNames.add(filename_only);
					
					File f = new File(outputPath);
					if(!f.exists()) { 
						FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
						fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
						fileOutputStream.close();
					}
					
					if(first) {
						ImageConverter.createPreview(outputPath);
						first = false;
					}
				}
			}
		} catch (Exception e) {
//			//System.out.println("Download failed of: "+urlstring);
//			//System.out.println("download to: "+path);
//			//System.out.println("with date: "+date);
//			//System.out.println("affected subreddit: "+subreddit);
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
		
		return fileNames;
	}
	
	public static ArrayList<String> downloadGIFV(String urlstring, String path, String date, String subreddit) {
		ArrayList<String> fileNames = new ArrayList<String>();
		
		try {
			Document doc = Jsoup.connect(urlstring).get();
			//LogUtility.newLineToLog(doc);
			Elements metas = doc.select("meta");
			for (Element meta : metas) {
				if(meta.attr("itemprop").equals("contentURL")) {
					
					try {
						URL url = new URL(meta.attr("content"));
						ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
						
						date = date.substring(0,10).replace("-", "");
						String outputPath = path+"/"+subreddit+"/"+date+"_"+url.getFile().substring(1);
						
						String filename_only = outputPath.substring(outputPath.lastIndexOf('/')+1, outputPath.length());
						fileNames.add(filename_only);
						
						File f = new File(outputPath);
						if(!f.exists()) { 
							FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
							fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
							fileOutputStream.close();
						}
						
						ImageConverter.createPreview(outputPath);
					} catch (Exception e) {
						StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}

		return fileNames;
	}
}
