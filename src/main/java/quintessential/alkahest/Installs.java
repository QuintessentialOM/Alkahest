package quintessential.alkahest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Installs{

	public static final String vanillaName = "Lightning.exe";
	public static final String moddedName = "ModdedLightning.exe";
	
	public static final List<String> pathHints = List.of(
			"C:/Program Files (x86)/Steam/steamapps/common/Opus Magnum"
	);
	
	public static List<OmInstall> findInstalls(){
		List<OmInstall> ret = new ArrayList<>();
		// TODO: allow specifying install locations manually & store in config
		
		for(String hint : pathHints){
			Path path = Path.of(hint);
			if(Files.isDirectory(path) && Files.isRegularFile(path.resolve(vanillaName)))
				ret.add(new OmInstall(hint, Files.isRegularFile(path.resolve(moddedName))));
		}
		// TODO: more hint paths for other locations (e.g. on linux)
		
		return ret;
	}
}