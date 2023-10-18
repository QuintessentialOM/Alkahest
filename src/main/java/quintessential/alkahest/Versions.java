package quintessential.alkahest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Versions{
	
	private static final String query = "https://api.github.com/repos/%s/%s/releases?page=%s";
	
	public static List<ModVersion> fetchVersions(ModRepo repo, int page){
		try{
			JsonArray response = JsonParser.parseString(Web.makeGitHubQuery(query.formatted(repo.owner(), repo.name(), page))).getAsJsonArray();
			List<ModVersion> ret = new ArrayList<>(response.size());
			for(JsonElement versionElem : response){
				JsonObject versionObj = versionElem.getAsJsonObject();
				String tagName = versionObj.get("tag_name").getAsString();
				String assetUrl = null;
				if(versionObj.has("assets")){
					var assets = versionObj.getAsJsonArray("assets");
					if(assets.size() == 1)
						assetUrl = assets.get(0).getAsJsonObject().get("browser_download_url").getAsString();
				}
				
				ModVersion version = fetchVersionInfo(repo, tagName, assetUrl);
				ret.add(version);
			}
			
			return ret;
		}catch(RuntimeException e){
			System.err.println(e);
			return List.of();
		}
	}
	
	private static ModVersion fetchVersionInfo(ModRepo repo, String tag, String assetUrl){
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
				Optional.ofNullable(parsed.Icon).map(path -> prefix + "Content/" + path + (path.endsWith(".png") ? "" : ".png")),
				Optional.ofNullable(assetUrl)
		);
	}
}