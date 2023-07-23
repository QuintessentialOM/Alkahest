package quintessential.alkahest;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.iconset.IconSet;
import com.github.weisj.darklaf.theme.OneDarkTheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class UI{

	private static JFrame frame;
	private static JButton installedModsPage, getModsPage, quintessentialPage, settingsPage;
	
	private static final Icon installIcon = IconSet.iconLoader().getIcon("navigation/arrow/thick/arrowDown.svg");
	
	public static void createWindow(){
		LafManager.install(new OneDarkTheme());
		
		frame = new JFrame("Alkahest");
		
		JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT);
		pane.addTab("dummy", new JPanel());
		pane.addTab("Get Mods", getModsPanel());
		pane.addTab("Installed Mods", new JPanel());
		pane.addTab("Installs", getInstallsPanel());
		pane.addTab("Settings", new JPanel());
		var runButton = new JButton("Run Quintessential"){
			public void setEnabled(boolean b){}
		};
		runButton.setBackground(Color.GREEN.darker().darker().darker());
		runButton.addActionListener(e -> {
			if(Main.installs.size() > 0)
				Installs.startModded(Main.installs.get(0));
		});
		pane.setTabComponentAt(0, runButton);
		pane.setEnabledAt(0, false);
		pane.setSelectedIndex(1);
		frame.add(pane);
		
		frame.setSize(800, 500);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private static JComponent getModsPanel(){
		JPanel listPanel = createVPanel(0);
		Repos.fetchAllRepos(1).stream().map(UI::displayRepo).forEach(listPanel::add);
		listPanel.add(Box.createVerticalGlue());
		
		return new OverlayScrollPane(listPanel);
	}
	
	private static JComponent displayRepo(ModRepo repo){
		JPanel repoPanel = createHPanel(8);
		
		List<ModVersion> versions = Versions.fetchVersions(repo, 1); // TODO: limit to latest release
		if(versions.size() == 0){
			// if there's no releases, we can't do much
			repoPanel.add(new JLabel(repo.name()));
		}else{
			ModVersion latest = versions.get(0);
			latest.iconUrl().flatMap(UI::getIconComponent).ifPresent(repoPanel::add);
			
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
			repoPanel.add(Box.createHorizontalGlue());
			repoPanel.add(new JButton(new InstallModAction(latest)));
		}
		
		return repoPanel;
	}
	
	private static Optional<JComponent> getIconComponent(String url){
		try{
			URL u = new URL(url);
			Image image = ImageIO.read(u).getScaledInstance(64, 64, Image.SCALE_SMOOTH);
			var icon = new ImageIcon(image);
			icon.setDescription("Mod icon");
			return Optional.of(pad(new JLabel(icon), 6));
		}catch(IOException e){
			System.out.println("Invalid icon " + url);
			return Optional.empty();
		}
	}
	
	private static JComponent getInstallsPanel(){
		JPanel listPanel = createVPanel(0);
		Main.installs.stream().map(UI::displayInstall).forEach(listPanel::add);
		listPanel.add(Box.createVerticalGlue());
		return new OverlayScrollPane(listPanel);
	}
	
	private static JComponent displayInstall(OmInstall install){
		JPanel installPanel = createVPanel(8);
		
		var pathLabel = new JLabel(install.path().toAbsolutePath().toString());
		pathLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
		installPanel.add(pathLabel);
		installPanel.add(Box.createRigidArea(new Dimension(8, 8)));
		
		var details = createHPanel(0);
		if(install.hasMods())
			details.add(new JButton(new RunOmAction(false, install)));
		else
			details.add(new JButton("Install Quintessential", installIcon));
		details.add(new JButton(new RunOmAction(true, install)));
		details.add(new JButton(new OpenFolderAction(install.path())));
		installPanel.add(details);
		return installPanel;
	}
	
	private static JPanel createHPanel(int padding){
		JPanel hPanel = new JPanel();
		if(padding != 0)
			hPanel.setBorder(new EmptyBorder(padding, padding, padding, padding));
		hPanel.setLayout(new BoxLayout(hPanel, BoxLayout.X_AXIS));
		hPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return hPanel;
	}
	
	private static JPanel createVPanel(int padding){
		JPanel vPanel = new JPanel();
		if(padding != 0)
			vPanel.setBorder(new EmptyBorder(padding, padding, padding, padding));
		vPanel.setLayout(new BoxLayout(vPanel, BoxLayout.Y_AXIS));
		vPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return vPanel;
	}
	
	private static JComponent pad(JComponent component, int padding){
		return pad(component, padding, padding, padding, padding);
	}
	
	private static JComponent pad(JComponent component, int paddingTop, int paddingLeft, int paddingBottom, int paddingRight){
		JPanel container = createHPanel(0);
		container.add(component);
		container.setBorder(new EmptyBorder(paddingTop, paddingLeft, paddingBottom, paddingRight));
		return container;
	}
	
	private static class RunOmAction extends AbstractAction{
		
		private final boolean vanilla;
		private final OmInstall install;
		
		public RunOmAction(boolean vanilla, OmInstall install){
			super(vanilla ? "Start vanilla" : "Start with Quintessential", IconSet.iconLoader().getIcon("navigation/arrow/thick/arrowRight.svg"));
			this.vanilla = vanilla;
			this.install = install;
		}
		
		public void actionPerformed(ActionEvent e){
			if(vanilla)
				Installs.startVanilla(install);
			else
				Installs.startModded(install);
		}
	}
	
	private static class OpenFolderAction extends AbstractAction{
		
		private final Path path;
		
		public OpenFolderAction(Path path){
			super("Open folder", IconSet.iconLoader().getIcon("files/folder.svg"));
			this.path = path;
		}
		
		public void actionPerformed(ActionEvent e){
			try{
				Desktop.getDesktop().open(path.toFile());
			}catch(IOException ex){
				throw new UncheckedIOException(ex);
			}
		}
	}
	
	private static class InstallModAction extends AbstractAction{
		
		private final ModVersion version;
		
		public InstallModAction(ModVersion version){
			super("Install", installIcon);
			this.version = version;
			if(Main.installs.size() == 0 || version.assetUrl().isEmpty())
				setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent e){
			if(Main.installs.size() > 0)
				version.assetUrl().ifPresent(url ->
						Web.download(url, Main.installs.get(0).path().resolve("Mods").resolve(version.name() + ".zip")));
		}
	}
}