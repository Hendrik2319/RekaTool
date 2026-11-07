package net.schwarzbaer.java.games.reka.savegames.data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.schwarzbaer.java.lib.system.XMLHelper;
import net.schwarzbaer.java.lib.system.XMLHelper.KnownValues;

public class Base64Image extends SaveGame.DataFile
{
	private static KnownValues KV = SaveGame.KVF
			.createFor(Base64Image.class, " <DOC>")
			.addNodes( KnownValues.NodeContainerType.Nodes, "Image" );
	
	private static KnownValues KV_Image = SaveGame.KVF
			.createFor(Base64Image.class, " <Image>")
			.addNodes( KnownValues.NodeContainerType.Text, "Data" );
	/*
		   Base64Image <DOC>  --->  nodes: [1]Image
		   Base64Image <DOC> <Image>  --->  nodes: [1]Data
		   Base64Image <DOC> <Image> <Data>  --->  has text
	 */
	
	public final String rawBase64Str;
	public final byte[] decodedBytes;
	public final BufferedImage image;
	protected Base64Image(File file)
	{
		super(file);
		// SaveGame.structureScanner.scan(document, "Base64Image");
		rawBase64Str = getRawStr(document);
		decodedBytes = decodeBytes(this.file, rawBase64Str);
		image        = decodeImage(this.file, decodedBytes);
	}

	private static String getRawStr(Document document)
	{
		if (document == null)
			return null;
		
		KV.scan(document);
		
		Node imageNode = XMLHelper.getChild(document, "Image");
		if (imageNode == null)
			return null;
		
		KV_Image.scan(imageNode);
		
		Node dataNode = XMLHelper.getChild(imageNode, "Data");
		if (dataNode == null)
			return null;
		
		return XMLHelper.getTextContent(dataNode);
	}

	private static byte[] decodeBytes(File file, String rawBase64Str)
	{
		if (rawBase64Str!=null)
			try
			{
				return Base64.getDecoder().decode(rawBase64Str);
			}
			catch (Exception ex)
			{
				System.err.printf("%s while decoding Base64 of Base64Image \"%s\": %s", ex.getClass().getSimpleName(), file.getAbsolutePath(), ex.getMessage());
				// ex.printStackTrace();
			}
		return null;
	}

	private static BufferedImage decodeImage(File file, byte[] decodedBytes)
	{
		if (decodedBytes!=null)
			try
			{
				return ImageIO.read(new ByteArrayInputStream(decodedBytes));
			}
			catch (IOException ex)
			{
				System.err.printf("%s while decoding image from decoded Base64 bytes of Base64Image \"%s\": %s", ex.getClass().getSimpleName(), file.getAbsolutePath(), ex.getMessage());
				// ex.printStackTrace();
			}
		return null;
	}
}
