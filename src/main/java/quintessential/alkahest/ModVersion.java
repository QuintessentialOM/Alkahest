package quintessential.alkahest;

import java.util.Optional;

public record ModVersion(
		ModRepo repo, String tag, String name, String version,
		Optional<String> title, Optional<String> desc, Optional<String> iconUrl, Optional<String> assetUrl
){}