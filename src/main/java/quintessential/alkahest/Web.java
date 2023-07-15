package quintessential.alkahest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public final class Web{
	
	public static String makeGitHubQuery(String query){
		try{
			URL queryUrl = new URL(query);
			URLConnection con = queryUrl.openConnection();
			if(!(con instanceof HttpURLConnection hCon))
				throw new IllegalStateException();
			hCon.setRequestMethod("GET");
			hCon.setRequestProperty("Accept", "application/json");
			hCon.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
			
			hCon.connect();
			if(hCon.getResponseCode() != 200)
				throw new RuntimeException("Could not connect to server (response code " + hCon.getResponseCode() + ")");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(hCon.getInputStream()));
			String output = reader.lines().collect(Collectors.joining("\n"));
			hCon.disconnect();
			
			return output;
		}catch(MalformedURLException e){
			throw new IllegalStateException(e);
		}catch(IOException e){
			throw new UncheckedIOException(e);
		}
	}
}
