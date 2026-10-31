package net.schwarzbaer.java.games.reka.savegames.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

public class SaveGame
{
	public final File folder;
	public final String name;

	public SaveGame(File folder, String name)
	{
		this.folder = folder;
		this.name = name;
	}

	public static void scan(List<SaveGame> saveGames)
	{
		Map<String,Integer> fileOcc = new HashMap<>();
		if (saveGames!=null)
		{
			System.out.println();
			System.out.printf("%d SaveGames%n", saveGames.size());
			for (SaveGame saveGame : saveGames)
			{
				System.out.printf("%-30s -> \"%s\"%n", "\"%s\"".formatted(saveGame.name), saveGame.folder.getAbsolutePath());

				File[] files = saveGame.listFiles();
				for (File file : files)
					fileOcc.compute(file.getName(), (name,n) -> n==null ? 1 : n+1);
			}
		}
		
		if (!fileOcc.isEmpty())
		{
			System.out.println();
			List<String> fileNames = fileOcc.keySet().stream().sorted().toList();
			System.out.printf("%d Files in each SaveGame%n", fileNames.size());
			for (String fileName : fileNames)
				System.out.printf("%-20s : %d%n", fileName, fileOcc.get(fileName));
		}
	}

	public File[] listFiles()
	{
		return folder.listFiles(file -> file!=null && file.isFile());
	}

	public DataFile[] getDataFiles()
	{
		File[] files = listFiles();
		return Arrays
				.stream(files)
				.map(DataFile::new)
				.toArray(DataFile[]::new);
	}
	
	public static class DataFile
	{
		public final File file;
		public final byte[] bytes;
		public final Document document;

		private DataFile(File file)
		{
			this.file = file;
			bytes = readBytes(this.file);
			document = XML.parse(bytes, "file \"%s\"".formatted(this.file.getAbsolutePath()));
		}

		private static byte[] readBytes(File file)
		{
			try
			{
				return Files.readAllBytes(file.toPath());
			}
			catch (IOException e)
			{
				System.err.printf("IOException while reading file \"%s\": %s%n", file.getAbsolutePath(), e.getMessage());
				// e.printStackTrace();
			}
			return null;
		}
	}
}