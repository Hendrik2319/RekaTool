package net.schwarzbaer.java.games.reka.savegames.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.nio.charset.StandardCharsets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.schwarzbaer.java.games.reka.savegames.data.SaveGame.DataFile;
import net.schwarzbaer.java.lib.gui.HexViewPanel;
import net.schwarzbaer.java.lib.gui.NewXMLTreeView;

class FilePanel extends JPanel
{
	private static final long serialVersionUID = 529171409091722488L;
	private final DataFile file;
	private final JLabel tabHeaderComp;

	FilePanel(DataFile file)
	{
		super(new BorderLayout());
		this.file = file;
		tabHeaderComp = new JLabel(this.file.file.getName());
		tabHeaderComp.setOpaque(false);
		tabHeaderComp.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
		
		JPanel initialPanel = new JPanel();
		
		JButton showBtn = new JButton("Show Data");
		showBtn.addActionListener(ev -> {
			SwingUtilities.invokeLater(() -> {
				remove(initialPanel);
				add(createTabbedPane(), BorderLayout.CENTER);
				tabHeaderComp.setOpaque(true);
				tabHeaderComp.setBackground(Color.GREEN);
			});
		});
		
		initialPanel.add(showBtn);
		add(initialPanel, BorderLayout.CENTER);
	}
	
	Component getTabHeaderComp()
	{
		return tabHeaderComp;
	}

	private JTabbedPane createTabbedPane()
	{
		JTabbedPane panel = new JTabbedPane();
		
		if (file.bytes!=null)
		{
			HexViewPanel hexViewPanel = new HexViewPanel(48, false);
			hexViewPanel.setData(file.bytes);
			panel.addTab("Hex", hexViewPanel);
			
			String text = new String(file.bytes, StandardCharsets.UTF_8);
			JTextArea textArea = new JTextArea(text);
			JScrollPane textPanel = new JScrollPane( textArea );
			panel.addTab("Text", textPanel);
		}
		
		if (file.document!=null)
		{
			
			NewXMLTreeView xmlTreeView = new NewXMLTreeView();
			xmlTreeView.setDocument(file.document);
			JScrollPane xmlPanel = new JScrollPane( xmlTreeView );
			panel.addTab("XML", xmlPanel);
		}
		
		return panel;
	}
}