package net.schwarzbaer.java.games.reka.savegames;

import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.schwarzbaer.java.games.reka.savegames.data.SaveGame;
import net.schwarzbaer.java.games.reka.savegames.views.MainPanel;
import net.schwarzbaer.java.lib.gui.StandardMainWindow;
import net.schwarzbaer.java.lib.system.FileSystemHelper;
import net.schwarzbaer.java.lib.system.FileSystemHelper.FileObj;
import net.schwarzbaer.java.lib.system.FileSystemHelper.FileSelectException;

public class RekaSaveGameTool
{
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(() -> {
			try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
			
			new RekaSaveGameTool().initialize();
			
		});
	}

	private final StandardMainWindow mainWindow;
	private final MainPanel contentPane;

	RekaSaveGameTool()
	{
		mainWindow = new StandardMainWindow("Reka SaveGame Tool");
		mainWindow.startGUI(contentPane = new MainPanel());
	}

	private void initialize()
	{
		List<SaveGame> saveGames = getSaveGames(mainWindow);
		SaveGame.scan(saveGames);
		contentPane.set(saveGames);
	}

	private static List<SaveGame> getSaveGames(Component parent)
	{
		try
		{
			FileObj subFolder = FileSystemHelper
					.getUserFolder()
					.getSubFolder("AppData\\Roaming\\RekaGame\\Saves");
			subFolder.showPath();
			
			List<FileObj> playerFolders = subFolder.getFolders();
			
			FileObj playerFolder;
			if (playerFolders.size() > 1)
			{
				String msg   = "Please select a player:";
				String title = "Select a player";
				Object[] values = PlayerFolderItem.toArray(playerFolders);
				Object initValue = null;
				Object result = JOptionPane.showInputDialog(parent, msg, title, JOptionPane.QUESTION_MESSAGE, null, values, initValue);
				
				if (result instanceof PlayerFolderItem item)
					playerFolder = item.folder;
				else
					throw new FileSelectException("There was no player selected.");
			}
			else if (playerFolders.size() < 1)
				throw new FileSelectException(
						"It appears that no one has ever played the game Reka under the current user. "
						+ "There is no subfolder in folder \"%s\"",
						subFolder.file().getAbsolutePath()
				);
			else
				playerFolder = playerFolders.get(0);
			
			List<SaveGame> saveGames = new Vector<>();
			if (playerFolder!=null)
				playerFolder.forEachFolder(
						folder1 -> folder1.forEachFolder(
								saveGameFolder -> saveGames.add(
										new SaveGame(
												saveGameFolder.file(),
												"%s / %s".formatted(
														folder1.file().getName(),
														saveGameFolder.file().getName()
												)
										)
								)
						)
				);
			
			return saveGames;
		}
		catch (FileSelectException ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}
	
	private record PlayerFolderItem(FileObj folder)
	{
		@Override public String toString()
		{
			return folder.file().getName();
		}
		
		static PlayerFolderItem[] toArray(List<FileObj> folders)
		{
			return folders
					.stream()
					.map(PlayerFolderItem::new)
					.toArray(PlayerFolderItem[]::new);
		}
	}
}
