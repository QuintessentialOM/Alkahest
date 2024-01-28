package quintessential.alkahest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Web{
	
	public static Optional<String> makeGitHubQuery(String query){
		try{
			URL queryUrl = new URL(query);
			URLConnection con = queryUrl.openConnection();
			if(!(con instanceof HttpURLConnection hCon))
				throw new IllegalStateException();
			hCon.setRequestMethod("GET");
			hCon.setRequestProperty("Accept", "application/json");
			hCon.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
			
			hCon.connect();
			if(hCon.getResponseCode() != 200){
				System.err.println("Could not connect to server for \"" + query + "\" (response code " + hCon.getResponseCode() + ")");
				return Optional.empty();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(hCon.getInputStream()));
			String output = reader.lines().collect(Collectors.joining("\n"));
			hCon.disconnect();
			
			return Optional.of(output);
		}catch(IOException e){
			e.printStackTrace();
			return Optional.empty();
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