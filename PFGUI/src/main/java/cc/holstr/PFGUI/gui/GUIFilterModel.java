package cc.holstr.PFGUI.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import cc.holstr.PFGUI.filter.Filter;

public class GUIFilterModel {
	private final Color highlightColor = new Color(100,149,237);
	private final Color enabledColor = new Color(154,205,50);
	private Color bgBefore; 
	
	private JPanel viewPanel;
	
	private JCheckBoxMenuItem menuItem;
	
	private GridBagConstraints gbc; 
	
	private String name; 
	private boolean enabled;
	private File file; 
	
	public GUIFilterModel(File file) {
		this.name = file.getName();
		this.file = file;
		this.enabled = false;
		makeMenuModel();
	}
	
	public GUIFilterModel(File file, String name) {
		this.name = name; 
		this.enabled = false;
		this.file = file;
		makeMenuModel();
	}
	
	private void makeMenuModel() {
		menuItem = new JCheckBoxMenuItem(name);
		viewPanel = new JPanel(new BorderLayout());
		viewPanel.add(new JLabel(name),BorderLayout.WEST);
		viewPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bgBefore = viewPanel.getBackground();
        viewPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) {
					if(enabled) {
						disable();
					} else {
						enable();
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if(!enabled) {
					viewPanel.setBackground(highlightColor);
				}
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if(!enabled) {
					viewPanel.setBackground(bgBefore);
				}
				
			}
        });
        menuItem.addActionListener((ActionEvent e) -> {
        	if(menuItem.isSelected()) {
        		disable();
        	} else {
        		enable();
        	}
        });
	}
	
	public void enable() {
		enabled = true;
		Window.enabledFilter = Filter.build(file);
		viewPanel.setBackground(enabledColor);
		menuItem.setSelected(true);
	}
	
	public void disable() {
		enabled = false;
		viewPanel.setBackground(bgBefore);
		menuItem.setSelected(false);
	}

	public JPanel getViewPanel() {
		return viewPanel;
	}

	public JCheckBoxMenuItem getMenuItem() {
		return menuItem;
	}

	public GridBagConstraints getGBC() {
		return gbc;
	}
}
