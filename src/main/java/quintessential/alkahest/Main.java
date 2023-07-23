package quintessential.alkahest;

import java.util.List;

public final class Main{
	
	public static List<OmInstall> installs = null;
	
	public static void main(String[] args){
		installs = Installs.findInstalls();
		UI.createWindow();
	}
}