package quintessential.alkahest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Installs{

	public static final String vanillaName;
	public static final String moddedName;

	static {
		String suffix;
		if(System.getProperty("os.name").contains("Windows"))
			suffix = ".exe";
		else if(System.getProperty("os.arch").contains("64"))
			suffix = ".bin.x86_64";
		else
			suffix = ".bin.x86";
		vanillaName = "Lightning" + suffix;
		moddedName = "ModdedLightning" + suffix;
	}

	public static final List<String> pathHints = List.of(
			"C:/Program Files (x86)/Steam/steamapps/common/Opus Magnum",
			System.getProperty("user.home") + "/.local/share/Steam/steamapps/common/Opus Magnum"
	);
	
	public static List<OmInstall> findInstalls(){
		List<OmInstall> ret = new ArrayList<>();
		// TODO: allow specifying install locations manually & store in config
		
		for(String hint : pathHints){
			Path path = Path.of(hint);
			if(Files.isDirectory(path) && Files.isRegularFile(path.resolve(vanillaName)))
				ret.add(new OmInstall(path, Files.isRegularFile(path.resolve(moddedName))));
		}
		// TODO: more hint paths for other locations (e.g. on linux)
		
		return ret;
	}
	
	public static void startVanilla(OmInstall install){
		startAt(install, vanillaName);
	}
	
	public static void startModded(OmInstall install){
		startAt(install, moddedName);
	}
	
	private static void startAt(OmInstall install, String filename){
		Path exePath = install.path().resolve(filename);
		if(Files.isRegularFile(exePath)){
			var builder = new ProcessBuilder(exePath.toAbsolutePath().toString())
					.directory(install.path().toFile())
					.inheritIO();
			try{
				builder.start();
			}catch(IOException e){
				throw new UncheckedIOException(e);
			}
		}
	}
}