package quintessential.alkahest;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Versions{
	
	private static final String query = "https://api.github.com/repos/%s/%s/releases?page=%s";
	
	public static List<ModVersion> fetchVersions(ModRepo repo, int page){
		try{
			Optional<String> resp = Web.makeGitHubQuery(query.formatted(repo.owner(), repo.name(), page));
			if(resp.isEmpty())
				return List.of();
			JsonArray response = JsonParser.parseString(resp.get()).getAsJsonArray();
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
				
				fetchVersionInfo(repo, tagName, assetUrl).ifPresent(ret::add);
			}
			
			return ret;
		}catch(JsonSyntaxException e){
			e.printStackTrace();
			return List.of();
		}
	}
	
	private static Optional<ModVersion> fetchVersionInfo(ModRepo repo, String tag, String assetUrl){
		return Web.makeGitHubQuery(repo.rawUrl() + "/" + tag + "/" + "quintessential.yaml")
				.map(QuintessentialYaml::fromYaml)
				.map(parsed -> new ModVersion(repo,
						tag,
						parsed.Name,
						parsed.Version,
						Optional.ofNullable(parsed.Title),
						Optional.ofNullable(parsed.Desc),
						// in theory, someone could use a PSD here, but that's unlikely and probably not worth supporting here
						Optional.ofNullable(parsed.Icon).map(path -> repo.rawUrl() + "/" + tag + "/" + "Content/" + path + (path.endsWith(".png") ? "" : ".png")),
						Optional.ofNullable(assetUrl)
				));
	}
}