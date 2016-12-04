package cc.holstr.PFGUI.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cc.holstr.PFGUI.load.Unpacker;

public class CustomFilterWindow extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6826320750899934705L;
	
	private JPanel buttons;
	private JPanel filters;
	
	private JButton openFiltersDir;
	private JButton reloadFilters;
	
	private JMenu filterMenu;
	private JMenuItem reloadFiltersMenu;
	
	private List<GUIFilterModel> filtermodels; 
	
	public CustomFilterWindow(JMenu filterMenu) {
		this.filtermodels = new ArrayList<GUIFilterModel>();
		this.filterMenu = filterMenu;
		build();
	}

	public void build() {
		reloadFiltersMenu = new JMenuItem("Reload Filters");
		setLayout(new BorderLayout());
		buttons = new JPanel();
		openFiltersDir = new JButton("Open Filters Directory");
		reloadFilters = new JButton("Reload");
		filters = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        filters.add(new JPanel(), gbc);
		
		add(new JScrollPane(filters),BorderLayout.CENTER);
		add(buttons,BorderLayout.SOUTH);
		//buttons.add(reloadFilters);
		buttons.add(openFiltersDir);
		
		filterMenu.add(reloadFiltersMenu);
		
		reloadFiltersMenu.addActionListener((ActionEvent e) -> {
			reloadFilters();
		});
		
		reloadFilters.addActionListener((ActionEvent e) -> {
			reloadFilters();
		});
		
		openFiltersDir.addActionListener((ActionEvent e) -> {
			try {
				Desktop.getDesktop().open(Unpacker.filters);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "Unable to open filters folder.");
			}
		});
		
		loadFilters();
		setMinimumSize(new Dimension(300,250));
	}
	
	public void reloadFilters() {
		for(GUIFilterModel model : filtermodels) {
			filters.remove(model.getViewPanel());
			filterMenu.remove(model.getMenuItem());
		}
		filtermodels.clear();
//		revalidate();
//		repaint();
		loadFilters();
	}
	
	public void loadFilters() {
		File[] filters = Unpacker.filters.listFiles();
		for(File f : filters) {
			if(f.getName().substring(f.getName().lastIndexOf(".")).equals(".json")) {
			GUIFilterModel model = new GUIFilterModel(f,this);
            if(f.getName().contains("photos.json")) {
            	model.enable();
            }
            this.filters.add(model.getViewPanel(), model.getGBC(), 0);
            this.filterMenu.add(model.getMenuItem());
            filtermodels.add(model);
			}
		}
	}
	
	public void becameEnabled(GUIFilterModel model) {
		for(GUIFilterModel e : filtermodels) {
			if(!e.getName().equals(model.getName())) {
				e.disable();
			}
		}
	}
}
