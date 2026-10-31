package net.schwarzbaer.java.games.reka.savegames.views;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.schwarzbaer.java.games.reka.savegames.data.SaveGame;

public class MainPanel extends JPanel
{
	private static final long serialVersionUID = -5968043582436409731L;
	
	private final JTabbedPane saveGameList;

	public MainPanel()
	{
		super(new BorderLayout());
		add(saveGameList = new JTabbedPane(), BorderLayout.CENTER);
	}

	public void set(List<SaveGame> saveGames)
	{
		saveGameList.removeAll();
		if (saveGames==null) return;
		
		for (SaveGame saveGame : saveGames)
		{
			File[] files = saveGame.listFiles();
			Arrays.sort(files, Comparator.comparing(File::getName));
			
			JTabbedPane fileList = new JTabbedPane();
			for (File file : files)
				fileList.addTab(file.getName(), new FilePanel(file));
			
			saveGameList.addTab(saveGame.name, fileList);
		}
	}
}