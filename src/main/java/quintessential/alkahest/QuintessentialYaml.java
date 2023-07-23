package quintessential.alkahest;

import org.yaml.snakeyaml.Yaml;

import java.util.List;

public final class QuintessentialYaml{
	
	private static class QuintessentialDep{
		public String Name, Version;
	}
	
	public String Name, Title, Desc, Version, Icon, DLL;
	public List<QuintessentialDep> Dependencies, OptionalDependencies;
	
	public static QuintessentialYaml fromYaml(String yaml){
		return new Yaml().loadAs(yaml, QuintessentialYaml.class);
	}
}