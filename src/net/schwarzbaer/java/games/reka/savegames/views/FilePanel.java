package net.schwarzbaer.java.games.reka.savegames.views;

import java.nio.charset.StandardCharsets;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import net.schwarzbaer.java.games.reka.savegames.Tools;
import net.schwarzbaer.java.games.reka.savegames.data.SaveGame.DataFile;
import net.schwarzbaer.java.lib.gui.HexViewPanel;
import net.schwarzbaer.java.lib.gui.NewXMLTreeView;

class FilePanel extends JTabbedPane
{
	private static final long serialVersionUID = 529171409091722488L;
	private final DataFile file;

	FilePanel(DataFile file)
	{
		this.file = file;
		
		if (this.file.bytes!=null)
		{
			addTab("Hex", Tools.addOptionalPanel("Hex View", this::createHexViewPanel));
			
			if (this.file.showTextViewImmediately())
				addTab("Text", createTextViewPanel());
			else
				addTab("Text", Tools.addOptionalPanel("Text View", this::createTextViewPanel));
		}
		
		if (this.file.document!=null)
			addTab("XML", createXMLTreeViewPanel());
		
		int i = getTabCount();
		if (i>0) setSelectedIndex(i-1);
	}

	private HexViewPanel createHexViewPanel()
	{
		HexViewPanel hexViewPanel = new HexViewPanel(48, false);
		hexViewPanel.setData(file.bytes);
		return hexViewPanel;
	}

	private JScrollPane createTextViewPanel()
	{
		String text = new String(file.bytes, StandardCharsets.UTF_8);
		JTextArea textArea = new JTextArea(text);
		JScrollPane textPanel = new JScrollPane( textArea );
		return textPanel;
	}

	private JScrollPane createXMLTreeViewPanel()
	{
		NewXMLTreeView xmlTreeView = new NewXMLTreeView();
		xmlTreeView.setDocument(file.document);
		JScrollPane xmlPanel = new JScrollPane( xmlTreeView );
		return xmlPanel;
	}
}