package net.schwarzbaer.java.games.reka.savegames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Tools
{
	public static JPanel addOptionalPanel(String viewTitle, Supplier<JComponent> createPanel)
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JButton showBtn = new JButton("Show %s".formatted(viewTitle));
		showBtn.addActionListener(ev -> {
			SwingUtilities.invokeLater(() -> {
				panel.remove(showBtn);
				panel.setLayout(new BorderLayout());
				panel.add(createPanel.get(), BorderLayout.CENTER);
			});
		});
		
		panel.add(showBtn);
		return panel;
	}
}
