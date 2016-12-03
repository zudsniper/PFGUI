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
			JCheckBoxMenuItem filterItem = new JCheckBoxMenuItem(f.getName());
			JPanel filter = new JPanel(new BorderLayout());
			filter.add(new JLabel(f.getName()),BorderLayout.WEST);
			filter.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            filter.addMouseListener(new MouseListener() {
            	Color bgBefore;
            	
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount()==2) {
						Window.enabledFilter = Filter.build(f);
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
					bgBefore = filter.getBackground();
					filter.setBackground(new Color(100,149,237));
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					filter.setBackground(bgBefore);
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
            });
            filterItem.addActionListener((ActionEvent e) -> {
            	Window.enabledFilter = Filter.build(f);
            });
            
            if(f.getName().contains("photos.json")) {
            	Window.enabledFilter = Filter.build(f);
            }
            this.filters.add(filter, gbc, 0);
            this.filterMenu.add(filterItem);
			}
		}
	}
}
