package net.schwarzbaer.java.games.reka.savegames.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Document;

import net.schwarzbaer.java.lib.system.XMLHelper;

public class SaveGame implements Iterable<SaveGame.DataFile>
{
	public static final XMLHelper.StructureScanner structureScanner = new XMLHelper.StructureScanner();
	
	public  final File folder;
	public  final String name;
	private final List<DataFile> dataFiles;
	@SuppressWarnings("unused")
	private Base64Image fogMap;
	@SuppressWarnings("unused")
	private Base64Image thumbnail;

	public SaveGame(File folder, String name)
	{
		this.folder = folder;
		this.name = name;
		fogMap    = null;
		thumbnail = null;
		dataFiles = new Vector<>();
		reloadDataFiles();
	}

	@Override
	public Iterator<DataFile> iterator()
	{
		return dataFiles.iterator();
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
				for (DataFile file : saveGame)
				{
					fileOcc.compute(file.file.getName(), (name,n) -> n==null ? 1 : n+1);
					if (file.document!=null && file.getClass().equals(DataFile.class))
						structureScanner.scan(file.document, "File[%s]".formatted(file.file.getName()));
				}
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

	public void reloadDataFiles()
	{
		dataFiles.clear();
		fogMap    = null;
		thumbnail = null;
		
		File[] files = folder.listFiles(file -> file!=null && file.isFile());
		Arrays.sort(files, Comparator.comparing(File::getName));
		
		for (File file : files)
		{
			DataFile dataFile;
			switch (file.getName())
			{
			/*
				Character.xml        : 12
				Collection.xml       : 12
				CurrentMap_c.xml     : 12
				FogMap.xml           : 12
				Guidance.xml         : 12
				House.xml            : 12
				Inventory.xml        : 12
				Meta.xml             : 12
				PoiHistory.xml       : 10
				Progress.xml         : 12
				SaveLog.txt          : 12
				Stats.xml            : 12
				Thumbnail.xml        : 12
			 */
			case "FogMap.xml"   : dataFile = fogMap    = new Base64Image(file); break;
			case "Thumbnail.xml": dataFile = thumbnail = new Base64Image(file); break;
			
			default:
				dataFile = new DataFile(file);
				break;
			}
			dataFiles.add(dataFile);
		}
	}
	
	public static class DataFile
	{
		public final String name;
		public final File file;
		public final byte[] bytes;
		public final Document document;

		protected DataFile(File file)
		{
			this.file = file;
			name = this.file.getName();
			
			bytes = readBytes(this.file);
			
			boolean suppressSAXException = name.equals("CurrentMap_c.xml") || name.equals("SaveLog.txt");
			String source = "file \"%s\"".formatted(this.file.getAbsolutePath());
			document = bytes==null ? null : XMLHelper.parse(bytes, source, suppressSAXException);
		}

		public boolean showTextViewImmediately()
		{
			return !name.equals("CurrentMap_c.xml");
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