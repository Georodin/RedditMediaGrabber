package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

public class JSONparse {
	
	public static Object[] doesSubredditExist(String url, boolean isUser) {
		Object[] output = new Object[2];
		
		String jsonString = null;
		
		if(url.equals("")) {
			output[0] = false;
			output[1] = "no input...";
			return output;
		}
		
		try {
			if(isUser) {
				jsonString = getURLSource("https://www.reddit.com/user/"+url+"/.json");
			}else {
				jsonString = getURLSource("https://www.reddit.com/r/"+url+"/.json");
			}
		} catch (IOException e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
			output[0] = false;
			output[1] = "error: webrequest(malformed subreddit?)";
			return output;
		}
		
		if(jsonString.contains("\"after\": null")) {
			output[0] = false;
			output[1] = url+" does not exist...";
			return output;
		}
		
		output[0] = true;
		output[1] = url+" has been added too the list";
		return output;
	} 
	
	static ArrayList<String> checkType(Entry entry) {
		ArrayList<String> outputList = new ArrayList<String>();
		
		
		Boolean isGallery = false;
		String jsonString = null;
		
		try {

			try {
				jsonString = getURLSource(entry.uri+".json");
			} catch (IOException e) {
				StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
				return null;
			}
			
			jsonString = jsonString.substring(1).replaceFirst(".$","");
			
			JSONObject obj = new JSONObject(jsonString);
			JSONObject data = new JSONObject(obj.getJSONObject("data").getJSONArray("children").get(0).toString());
			
			Iterator<String> i = data.getJSONObject("data").keys();
			while(i.hasNext()) {
				String key = i.next();
				if(key.equals("is_gallery")&&data.getJSONObject("data").getBoolean(key)==true) {
					Iterator<String> ii = data.getJSONObject("data").getJSONObject("media_metadata").keys();
					while(ii.hasNext()) {
						String images = ii.next();
						outputList.add("https://i.redd.it/"+images+".jpg");
					}
					
					isGallery = true;
				}
			}
			
			if(!isGallery) {
				outputList.add(data.getJSONObject("data").getString("url_overridden_by_dest"));
			}

			return outputList;
		} catch (Exception e) {
//			//System.out.println("error at uri: "+entry.uri);
//			writeToFile(entry.uri, "uri");
//			writeToFile(jsonString, "error");
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
			return null;
		}
	}
	
    public static String getURLSource(String url) throws IOException
    {
    	String normalize = Normalizer.normalize(url, Normalizer.Form.NFD);
        URL urlObject = new URL(normalize);
        URLConnection urlConnection = urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        
        System.out.println();
        return toString(urlConnection.getInputStream());
    }
    
    private static String toString(InputStream inputStream) throws IOException
    {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")))
        {
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }

            return stringBuilder.toString();
        }
    }
    
	public static void writeToFile(String content, String name) {
	    BufferedWriter bwr;
		try {
			bwr = new BufferedWriter(new FileWriter(new File(name)));
		    bwr.write(content);
		    bwr.flush();
		    bwr.close();
		} catch (IOException e) {
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
	}

	public static ArrayList<String> checkType(String string) {
		Entry entry = new Entry(null, null, null, string, null, null);
		return checkType(entry);
	}
}
