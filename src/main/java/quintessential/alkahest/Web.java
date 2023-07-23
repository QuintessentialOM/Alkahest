package quintessential.alkahest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
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
	
	public static void download(String url, Path to){
		try(ReadableByteChannel channel = Channels.newChannel(new URL(url).openStream());
		    FileOutputStream output = new FileOutputStream(to.toFile())){
			
			output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
		}catch(MalformedURLException e){
			throw new IllegalStateException(e);
		}catch(IOException e){
			throw new UncheckedIOException(e);
		}
	}
}