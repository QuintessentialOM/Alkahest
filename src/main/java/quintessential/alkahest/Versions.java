package quintessential.alkahest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Versions{
	
	private static final String query = "https://api.github.com/repos/%s/%s/releases?page=%s";
	
	public static List<ModVersion> fetchVersions(ModRepo repo, int page){
		JsonArray response = JsonParser.parseString(Web.makeGitHubQuery(query.formatted(repo.owner(), repo.name(), page))).getAsJsonArray();
		List<ModVersion> ret = new ArrayList<>(response.size());
		for(JsonElement versionElem : response){
			JsonObject versionObj = versionElem.getAsJsonObject();
			String tagName = versionObj.get("tag_name").getAsString();
			
			ModVersion version = fetchVersionInfo(repo, tagName);
			ret.add(version);
		}
		
		return ret;
	}
	
	private static ModVersion fetchVersionInfo(ModRepo repo, String tag){
		String prefix = repo.rawUrl() + "/" + tag + "/";
		String yaml = Web.makeGitHubQuery(prefix + "quintessential.yaml");
		QuintessentialYaml parsed = QuintessentialYaml.fromYaml(yaml);
		return new ModVersion(repo,
				tag,
				parsed.Name,
				parsed.Version,
				Optional.ofNullable(parsed.Title),
				Optional.ofNullable(parsed.Desc),
				// in theory, someone could use a PSD here, but that's unlikely and probably not worth supporting here
				Optional.ofNullable(parsed.Icon).map(path -> prefix + "Content/" + path + ".png")
		);
	}
}