package net.schwarzbaer.java.games.reka.savegames.views;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.w3c.dom.Document;

import net.schwarzbaer.java.games.reka.savegames.Tools;
import net.schwarzbaer.java.games.reka.savegames.data.Base64Image;
import net.schwarzbaer.java.games.reka.savegames.data.SaveGame.DataFile;
import net.schwarzbaer.java.lib.gui.HexViewPanel;
import net.schwarzbaer.java.lib.gui.ImageView;
import net.schwarzbaer.java.lib.gui.NewXMLTreeView;
import net.schwarzbaer.java.lib.gui.ValueListOutput;

public class FilePanel extends JTabbedPane
{
	private static final long serialVersionUID = 529171409091722488L;
	
	public interface SimpleValueOutputter
	{
		boolean canShowInstantly();
		void writeTo(ValueListOutput out);

		public static void writeTo(ValueListOutput out, int indentLevel, String label, String[] strs)
		{
			if (strs==null)
				out.add(indentLevel, label, "%s", "NULL");
			
			else
			{
				out.add(indentLevel, label, strs.length);
				for (int i=0; i<strs.length; i++)
					out.add(indentLevel+1, "[%d]".formatted(i+1), strs[i]);
			}
			
		}
	}
	
	private static TypeTabs<?>[] TYPE_TABS = {
			new TypeTabs<>(
					DataFile.class,
					(tc, file) -> {
						if (file.bytes!=null)
						{
							tc.addTab("Hex", Tools.addOptionalPanel("Hex View", () -> createHexViewPanel(file.bytes)));
							
							if (file.showTextViewImmediately())
								tc.addTab("Text", createTextViewPanel(file.bytes, StandardCharsets.UTF_8, false));
							else
								tc.addTab("Text", Tools.addOptionalPanel("Text View", () -> createTextViewPanel(file.bytes, StandardCharsets.UTF_8, false)));
						}
						
						if (file.document!=null)
							tc.addTab("XML", createXMLTreeViewPanel(file.document));
					}
			),
			new TypeTabs<>(
					SimpleValueOutputter.class,
					(tc, outputter) -> {
						Supplier<JComponent> createPanel = () -> {
							ValueListOutput out = new ValueListOutput();
							outputter.writeTo(out);
							return createTextViewPanel(out.generateOutput(), false);
						};
						if (outputter.canShowInstantly())
							tc.addTab("Values", createPanel.get());
						else
							tc.addTab("Values", Tools.addOptionalPanel("Text View", createPanel));
					}
			),
			new TypeTabs<>(
					Base64Image.class,
					(tc, image) -> {
						if (image.rawBase64Str!=null)
							tc.addTab("Raw Base64", Tools.addOptionalPanel("Text View", () -> createTextViewPanel(image.rawBase64Str, true)));
						if (image.decodedBytes!=null)
							tc.addTab("Decoded Bytes", Tools.addOptionalPanel("Hex View", () -> createHexViewPanel(image.decodedBytes)));
						if (image.image!=null)
							tc.addTab("Image", new ImageView(image.image, 400,400, null, true));
					}
			),
	};
	
	private final DataFile file;

	FilePanel(DataFile file)
	{
		this.file = file;
		
		TabCreator tc = this::addTab;
		for (TypeTabs<?> typeTabs : TYPE_TABS)
			typeTabs.createTabsIfIsType(tc, this.file);
		
		int i = getTabCount();
		if (i>0) setSelectedIndex(i-1);
	}

	private static HexViewPanel createHexViewPanel(byte[] bytes)
	{
		HexViewPanel hexViewPanel = new HexViewPanel(48, false);
		hexViewPanel.setData(bytes);
		return hexViewPanel;
	}

	private static JScrollPane createTextViewPanel(byte[] bytes, Charset charset, boolean lineWrap)
	{
		return createTextViewPanel( new String(bytes, charset), lineWrap );
	}

	private static JScrollPane createTextViewPanel(String text, boolean lineWrap)
	{
		JTextArea textArea = new JTextArea(text);
		textArea.setLineWrap(lineWrap);
		JScrollPane textPanel = new JScrollPane( textArea );
		return textPanel;
	}

	private static JScrollPane createXMLTreeViewPanel(Document document)
	{
		NewXMLTreeView xmlTreeView = new NewXMLTreeView();
		xmlTreeView.setDocument(document);
		JScrollPane xmlPanel = new JScrollPane( xmlTreeView );
		return xmlPanel;
	}
	
	private interface TabCreator
	{
		void addTab(String title, JComponent tabComp);
	}
	
	private record TypeTabs<T>(Class<T> clazz, BiConsumer<TabCreator, T> createTabs)
	{
		void createTabsIfIsType(TabCreator tabCreator, DataFile dataFile)
		{
			if (clazz.isInstance(dataFile))
				createTabs.accept(tabCreator, clazz.cast(dataFile));
		}
	}
}