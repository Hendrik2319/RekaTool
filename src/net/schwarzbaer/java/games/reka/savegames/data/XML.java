package net.schwarzbaer.java.games.reka.savegames.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class XML
{
	static Document parse(byte[] bytes, String source)
	{
		return parse(new InputSource(new ByteArrayInputStream(bytes)), source);
	}

	static Document parse(File file) {
		try (FileInputStream input = new FileInputStream(file))
		{
			return parse(new InputSource(input), "file \"%s\"".formatted(file.getAbsolutePath()));
		}
		catch (FileNotFoundException e)
		{
			System.err.printf("File \"%s\" not found.%n", file.getAbsolutePath());
			// e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.printf("IOException while reading file \"%s\": %s%n", file.getAbsolutePath(), e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	static Document parse(InputSource is, String source) {
		if (is==null) return null;
		try
		{
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		}
		catch (SAXException | IOException | ParserConfigurationException e)
		{
			System.err.printf("%s while parsing XML of %s: %s%n", e.getClass().getName(), source, e.getMessage());
			//e.printStackTrace();
		}
		return null;
	}

}
