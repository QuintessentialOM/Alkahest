package quintessential.alkahest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Repos{
	
	private static final String query = "https://api.github.com/search/repositories?q=topic:quintessential-mod&page=%s&sort=updated";
	
	public static List<ModRepo> fetchAllRepos(int page){
		Optional<String> resp = Web.makeGitHubQuery(query.formatted(page));
		if(resp.isEmpty())
			return List.of();
		JsonObject response = JsonParser.parseString(resp.get()).getAsJsonObject();
		int count = response.get("total_count").getAsInt();
		List<ModRepo> ret = new ArrayList<>(count);
		for(JsonElement repoElem : response.getAsJsonArray("items")){
			JsonObject repoObj = repoElem.getAsJsonObject();
			String name = repoObj.get("name").getAsString();
			String owner = repoObj.getAsJsonObject("owner").get("login").getAsString();
			
			ModRepo repo = new ModRepo(name, owner);
			ret.add(repo);
		}
		return ret;
	}
}