package cc.holstr.PFGUI.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.imgscalr.Scalr;

import cc.holstr.PFGUI.load.ResourceLoader;
import cc.holstr.PFGUI.work.CrawlTask;
import cc.holstr.util.ZFileUtils;

public class Window extends JFrame{
	
	final static String PRERUN = "prerun";
	final static String RUNNING = "running";
	
	public final static boolean debug = true;
	
	private JPanel mainLayout;
	private JPanel textLayout;
	private JPanel running;
	private JPanel prerun; 
	private JPanel imagePanel;
	private JPanel main; 
	
	private JMenuBar bar; 
	private JMenu file; 
	private JMenu mode;
	private JMenuItem outputs;
	private JMenuItem fastMode;
	private JMenuItem resumeMode;
	private JMenuItem clearJson;
	private JMenuItem quit;
	
	private final JTextField search;
	private final JTextField output;
	
	private JLabel searchLabel;
	private JLabel outputLabel;
	private JLabel image;
	private JLabel imgDir; 
	private JLabel count; 
	private JLabel progressLabel;
	private JLabel timeToComplete;
	
	private JProgressBar progress;
	
	private JButton searchBrowse;
	private JButton outputBrowse;
	private JButton run; 
	
	private CardLayout cl;
	
	private JFileChooser fc;
	
	private File outputFile; 
	private File searchFile;
	
	private String view;
	
	private CrawlTask crawlTask;
	
	private boolean fastmode = false;
	private boolean resumemode = false;
	
	public Window() {
		output = new JTextField(40);
		search = new JTextField(40);
		build();
	}
	
	public void build() {
		mainLayout = new JPanel(new BorderLayout());
		prerun = new JPanel();
		textLayout = new JPanel(new BorderLayout());
		running = new JPanel(new BorderLayout());
		imagePanel = new JPanel();
		main = new JPanel(new CardLayout());
		
		bar = new JMenuBar();
		
		file = new JMenu("File");
		mode = new JMenu("Modes");
		
		outputs = new JMenuItem("View Outputs...");
		quit = new JMenuItem("Force Quit");
		clearJson = new JMenuItem("Reset JSON");
		
		fastMode = new JMenuItem("Fast Mode ON");
		fastMode.setForeground(Color.RED);
		
		resumeMode = new JMenuItem("[WIP] Resume Mode ON");
		resumeMode.setForeground(Color.RED);
		
		run = new JButton();
		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});
		
		run.setPreferredSize(new Dimension(Integer.MAX_VALUE,20));
		
		cl = (CardLayout)(main.getLayout());
		
		fc = new JFileChooser();
		
		//menu layout
		bar.add(file);
		bar.add(mode);
		
		file.add(outputs);
		file.add(clearJson);
		file.add(quit);
		
		mode.add(resumeMode);
		mode.add(fastMode);
		
		outputs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openPreviousOutputs();
			}
		});
		
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		
		clearJson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearJson();
			}
		});
		
		fastMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(fastMode.getText().contains("OFF")) {
					fastMode.setText("Fast Mode ON");
					fastMode.setForeground(Color.RED);
					fastmode = false;
				} else {
					fastMode.setText("Fast Mode OFF");
					fastMode.setForeground(Color.GREEN);
					fastmode = true;
				}
				
			}
		});
		
		resumeMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(resumeMode.getText().contains("OFF")) {
					resumeMode.setText("[WIP] Resume Mode ON");
					resumeMode.setForeground(Color.RED);
					resumemode = false;
				} else {
					resumeMode.setText("[WIP] Resume Mode OFF");
					resumeMode.setForeground(Color.GREEN);
					resumemode = true;
				}
				
			}
		});
		
		//prerun
		JPanel searchPanel = new JPanel();
		JPanel outputPanel = new JPanel();
		
		search.setDropTarget(new DropTarget() {
	        public synchronized void drop(DropTargetDropEvent evt) {
	            try {
	                evt.acceptDrop(DnDConstants.ACTION_COPY);
	                List<File> droppedFiles = (List<File>) evt
	                        .getTransferable().getTransferData(
	                                DataFlavor.javaFileListFlavor);
	                for (File file : droppedFiles) {
	                    search.setText(file.getAbsolutePath());
	                }
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        }
	    });
		
		output.setDropTarget(new DropTarget() {
	        public synchronized void drop(DropTargetDropEvent evt) {
	            try {
	                evt.acceptDrop(DnDConstants.ACTION_COPY);
	                List<File> droppedFiles = (List<File>) evt
	                        .getTransferable().getTransferData(
	                                DataFlavor.javaFileListFlavor);
	                for (File file : droppedFiles) {
	                    output.setText(file.getAbsolutePath());
	                }
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        }
	    });
		
		outputLabel = new JLabel("Output: ");
		searchLabel = new JLabel("Search: ");
		
		outputBrowse = new JButton("Browse...");
		outputBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(output!=null) {
					outputFile = makeFileChooser(output.getText());
				} else {
					outputFile = makeFileChooser("");
				}
				if(outputFile==null) {
					output.setText("");
				} else {
					output.setText(outputFile.getAbsolutePath());
				}
			}
		});
		searchBrowse = new JButton("Browse...");
		searchBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(search!=null) {
					searchFile = makeFileChooser(search.getText());
				} else {
					searchFile = makeFileChooser("");
				}
				if(searchFile==null) {
					search.setText("");
				} else {
					search.setText(searchFile.getAbsolutePath());
				}
			}
		});
		
		outputPanel.add(outputLabel);
		outputPanel.add(output);
		outputPanel.add(outputBrowse);
		
		searchPanel.add(searchLabel);
		searchPanel.add(search);
		searchPanel.add(searchBrowse);
		
		prerun.add(outputPanel);
		prerun.add(searchPanel);
		
		//running 
		progress = new JProgressBar(0,100);
		progressLabel = new JLabel("0/?");
		
		image = new JLabel();
		imgDir = new JLabel("No directory found, run app.");
		count = new JLabel("Found: 0 images");
		timeToComplete = new JLabel("");
		
		progress.setValue(0);
		progress.setStringPainted(true);
		
		imgDir.setFont(new Font(imgDir.getFont().getFontName(),Font.PLAIN, 20));
		
		JPanel textPanel = new JPanel(new GridLayout(5, 1));
		
		textPanel.add(imgDir);
		textPanel.add(count);
		textPanel.add(timeToComplete);
		
		JPanel progressPanel = new JPanel(new BorderLayout());
		progressPanel.add(progress, BorderLayout.CENTER);
		progressPanel.add(progressLabel, BorderLayout.EAST);
		//progressPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		progress.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
		
		imagePanel.add(image);
		
		textLayout.add(textPanel,BorderLayout.WEST);
		running.add(imagePanel, BorderLayout.EAST);
		running.add(textLayout, BorderLayout.CENTER);
		running.add(new JPanel(new GridLayout(0,1)).add(progressPanel), BorderLayout.SOUTH);
		
		running.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		
		//main layout
		preRunView();
		
		main.add(prerun, PRERUN);
		main.add(running, RUNNING);
		
		//TODO
		cl.show(main, PRERUN);
		
		add(mainLayout);
		mainLayout.add(bar, BorderLayout.NORTH);
		mainLayout.add(main, BorderLayout.CENTER);
		mainLayout.add(run,BorderLayout.SOUTH);
		
		//jframe 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(675,225));
		setResizable(false);
		setVisible(true);
		setTitle("PhotoFinder GUI");
	}
	
	public void run() {
		if(view.equals("prerun")) {
			if(!ZFileUtils.isDirectoryValid(search.getText()) && !ZFileUtils.isDirectoryValid(output.getText())) {
				JOptionPane.showMessageDialog(this, "Both directories are not valid.");
			} else if(!ZFileUtils.isDirectoryValid(search.getText())) {
				JOptionPane.showMessageDialog(this, "Search directory " + search.getText() + " is not valid.");
			} else if(!ZFileUtils.isDirectoryValid(output.getText())) {
				JOptionPane.showMessageDialog(this, "Output directory " + output.getText() + " is not valid.");
			} else if(output.getText().equals(search.getText())) {
				JOptionPane.showMessageDialog(this, "Directories cannot be the same.");
			} else {
				runningView();
			}
		} else if(view.equals("running")) {
			preRunView();
		}
	}
	
	public void preRunView() {
		view = "prerun";
		run.setText("RUN");
	    cl.show(main, PRERUN);
	    if(crawlTask!=null) {
	    	crawlTask.cancel(true);
	    }
	}
	
	public void runningView() {
		view = "running";
		run.setText("STOP");
		imgDir.setText("No directory found, run app.");
		count.setText("Found: 0");
		timeToComplete.setText("");
		progress.setValue(0);
		image.setIcon(new ImageIcon(Scalr.resize(ResourceLoader.toBufferedImage(ResourceLoader.getImageFromResources("blank.png")),main.getHeight())));
	    cl.show(main, RUNNING);
	    crawlTask = new CrawlTask(
	    		progress, imgDir, count, progressLabel, timeToComplete,
	    		image, main.getHeight(), fastmode, resumemode,
	    		output.getText(), search.getText());
	   crawlTask.execute();
	}
	
	public void openPreviousOutputs() {
		JOptionPane.showMessageDialog(this, "This option is not currently functional.");
	}
	
	public void quit() {
		System.exit(0);
	}
	
	public void clearJson() {
		if(ZFileUtils.isDirectoryValid(output.getText())) {
			File temp = new File(output.getText()+System.getProperty("file.separator")+"local.json"); 
			if(temp.delete()) {
				JOptionPane.showMessageDialog(this, "Reset JSON output file.");
			} else if(!temp.exists()){
				JOptionPane.showMessageDialog(this, "JSON output file is already clear.");
			} else {
				JOptionPane.showMessageDialog(this, "Failed to reset JSON output file.");
			}
		} else {
			JOptionPane.showMessageDialog(this, "Invalid output directory, couldn't reset.");
		}
	}
	
	public File makeFileChooser(String startingDir) {
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File startFile = new File(startingDir);
		fc.setSelectedFile(startFile);
		File file = null;
		int returnVal = fc.showOpenDialog(this);
		if(returnVal==JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
		} else if(returnVal==JFileChooser.CANCEL_OPTION) {
			file = startFile;
		}
		return file;
	}
}
