package cc.holstr.PFGUI.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

import cc.holstr.PFGUI.load.Unpacker;
import cc.holstr.PFGUI.filter.Filter;
import cc.holstr.PFGUI.gui.Window;

public class CustomFilterWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6826320750899934705L;
	
	private JPanel buttons;
	private JPanel filters;
	
	private JButton openFiltersDir;
	
	private JMenu filterMenu;
	
	public CustomFilterWindow(JMenu filterMenu) {
		this.filterMenu = filterMenu;
		build();
	}

	public void build() {
		setLayout(new BorderLayout());
		buttons = new JPanel();
		openFiltersDir = new JButton("Open Filters Directory");
		filters = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        filters.add(new JPanel(), gbc);
		
		add(new JScrollPane(filters),BorderLayout.CENTER);
		add(buttons,BorderLayout.SOUTH);
		buttons.add(openFiltersDir);
		
		openFiltersDir.addActionListener((ActionEvent e) -> {
			try {
				Desktop.getDesktop().open(Unpacker.filters);
			} catch (IOException e1) {
				System.err.println("Couldn't open filters folder.");
			}
		});
		
		loadFilters();
		setMinimumSize(new Dimension(300,250));
	}
	
	public void loadFilters() {
		File[] filters = Unpacker.filters.listFiles();
		for(File f : filters) {
			if(f.getName().substring(f.getName().lastIndexOf(".")).equals(".json")) {
			GUIFilterModel model = new GUIFilterModel(f);
            if(f.getName().contains("photos.json")) {
            	model.enable();
            }
            this.filters.add(model.getViewPanel(), model.getGBC(), 0);
            this.filterMenu.add(model.getMenuItem());
			}
		}
	}
}
