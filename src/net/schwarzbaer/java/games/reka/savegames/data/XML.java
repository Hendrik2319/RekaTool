package net.schwarzbaer.java.games.reka.savegames.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class XML
{
	static Document parse(byte[] bytes, String source, boolean suppressSAXException)
	{
		return parse(new InputSource(new ByteArrayInputStream(bytes)), source, suppressSAXException);
	}

	static Document parse(File file) {
		try (FileInputStream input = new FileInputStream(file))
		{
			return parse(new InputSource(input), "file \"%s\"".formatted(file.getAbsolutePath()), false);
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

	static Document parse(InputSource is, String source, boolean suppressSAXException) {
		if (is==null) return null;
		try
		{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			docBuilder.setErrorHandler(new ErrorHandler() {
				@Override public void warning   (SAXParseException exception) throws SAXException { throw exception; }
				@Override public void error     (SAXParseException exception) throws SAXException { throw exception; }
				@Override public void fatalError(SAXParseException exception) throws SAXException { throw exception; }
			});
			return docBuilder.parse(is);
		}
		catch (SAXException e)
		{
			if (!suppressSAXException)
				System.err.printf("%s while parsing XML of %s: %s%n", e.getClass().getName(), source, e.getMessage());
			//e.printStackTrace();
		}
		catch (IOException e)
		{
			System.err.printf("%s while parsing XML of %s: %s%n", e.getClass().getName(), source, e.getMessage());
			//e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			System.err.printf("%s while parsing XML of %s: %s%n", e.getClass().getName(), source, e.getMessage());
			//e.printStackTrace();
		}
		return null;
	}

}
