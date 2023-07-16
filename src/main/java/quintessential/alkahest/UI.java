package quintessential.alkahest;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.theme.OneDarkTheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public final class UI{

	private static JFrame frame;
	private static JButton installedModsPage, getModsPage, quintessentialPage, settingsPage;
	
	public static void createWindow(){
		LafManager.install(new OneDarkTheme());
		
		frame = new JFrame("Alkahest");
		
		JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT);
		pane.addTab("dummy", new JPanel());
		pane.addTab("Get Mods", getModsPanel());
		pane.addTab("Installed Mods", new JPanel());
		pane.addTab("Quintessential", new JPanel());
		pane.addTab("Settings", new JPanel());
		var runButton = new JButton("Run Quintessential"){
			public void setEnabled(boolean b){}
		};
		runButton.setBackground(Color.GREEN.darker().darker().darker());
		pane.setTabComponentAt(0, runButton);
		pane.setEnabledAt(0, false);
		pane.setSelectedIndex(1);
		frame.add(pane);
		
		frame.setSize(600, 400);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private static JComponent getModsPanel(){
		var listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		Repos.fetchAllRepos(1).stream().map(UI::displayRepo).forEach(listPanel::add);
		listPanel.add(Box.createVerticalGlue());
		
		return new OverlayScrollPane(listPanel);
	}
	
	private static JComponent displayRepo(ModRepo repo){
		JPanel repoPanel = new JPanel();
		repoPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		repoPanel.setLayout(new BoxLayout(repoPanel, BoxLayout.X_AXIS));
		repoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		List<ModVersion> versions = Versions.fetchVersions(repo, 1); // TODO: limit to latest release
		if(versions.size() == 0){
			// if there's no releases, we can't do much
			repoPanel.add(new JLabel(repo.name()));
		}else{
			ModVersion latest = versions.get(0);
			latest.iconUrl().ifPresent(icon -> repoPanel.add(getIconComponent(icon)));
			
			var infoPanel = new JPanel();
			infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
			
			var title = new JLabel(latest.title().orElse(latest.name()));
			title.setFont(title.getFont().deriveFont(18f));
			infoPanel.add(title);
			var version = new JLabel(latest.version() + " - by " + repo.owner());
			version.setFont(version.getFont().deriveFont(12f));
			infoPanel.add(version);
			repoPanel.add(infoPanel);
			
			repoPanel.add(Box.createRigidArea(new Dimension(8, 8)));
			repoPanel.add(new JButton("Install"));
		}
		
		return repoPanel;
	}
	
	private static JComponent getIconComponent(String url){
		try{
			URL u = new URL(url);
			Image image = ImageIO.read(u).getScaledInstance(64, 64, Image.SCALE_SMOOTH);
			var icon = new ImageIcon(image);
			icon.setDescription("Mod icon");
			return new JLabel(icon);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
}