package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HTMLparse {
	
	public static void main(String[] args) {
//		getImgurLinks("https://imgur.com/a/GH3wY3U");
	}
	
	public static String getImgurLinks(String url) {
		try {
			Document doc = Jsoup.parse(getURLSource(url));
			
			//JSONparse.writeToFile(doc.toString(), "faulty.txt");
			for(Element elm : doc.getElementsByTag("meta")) {
				
				if(elm.attr("name").equals("twitter:image")) {
					return elm.attr("content");
				}
			}
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw); e.printStackTrace(pw); LogUtility.newLineToErrorLog(sw);
		}
		
		
		return null;
	}
	
	public static String getURLA(String toParse) {
		Document doc = Jsoup.parse(toParse);
		
		String output = "";
		
		//JSONparse.writeToFile(doc.toString(), "faulty.txt");
		for(Element elm : doc.getElementsByTag("a")) {
			
			if(elm.text().equals("[link]")) {
				output = elm.attr("href");
				break;
			}
			
		}
		return output;
	}
	
    public static String getURLSource(String url) throws IOException
    {
        URL urlObject = new URL(url);
        URLConnection urlConnection = urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

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
}
