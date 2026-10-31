package net.schwarzbaer.java.games.reka.savegames.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.schwarzbaer.java.games.reka.savegames.data.SaveGame;
import net.schwarzbaer.java.games.reka.savegames.data.SaveGame.DataFile;

public class MainPanel extends JPanel
{
	private static final long serialVersionUID = -5968043582436409731L;
	
	private final JTabbedPane saveGameList;

	public MainPanel()
	{
		super(new BorderLayout());
		add(saveGameList = new JTabbedPane(), BorderLayout.CENTER);
		saveGameList.setPreferredSize(new Dimension(1200,950));
	}

	public void set(List<SaveGame> saveGames)
	{
		saveGameList.removeAll();
		if (saveGames==null) return;
		
		for (SaveGame saveGame : saveGames)
		{
			DataFile[] dataFiles = saveGame.getDataFiles();
			Arrays.sort(dataFiles, Comparator.comparing(df -> df.file.getName()));
			
			JTabbedPane fileList = new JTabbedPane();
			for (DataFile file : dataFiles)
			{
				FilePanel filePanel = new FilePanel(file);
				int index = fileList.getTabCount();
				fileList.addTab(file.file.getName(), filePanel);
				fileList.setTabComponentAt(index, filePanel.getTabHeaderComp());
			}
			
			saveGameList.addTab(saveGame.name, fileList);
		}
	}
}