package cc.holstr.PFGUI.work;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.ImagingOpException;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.lang.time.StopWatch;
import org.imgscalr.Scalr;

import cc.holstr.PFGUI.gui.Window;
import cc.holstr.PFGUI.json.JsonHandler;
import cc.holstr.util.ZFileUtils;

public class CrawlTask extends SwingWorker<Void, Integer>{
 
	private JProgressBar jpb;
	
	private JLabel currentDir; 
	private JLabel found;
	private JLabel progress; 
	private JLabel timeToComplete;
	private JLabel image; 
	private int imageSize;
	
	private String out; 
	private String search;
	
	private int allInDir;
	private int numOfMoved; 
	private int filesChecked;
	private JsonHandler json; 
	
	private File jsonOut;
	
	private boolean fastmode;
	private boolean resumeMode;
	
	private StopWatch time;
	
	private long timeStopped;
	
	private PropertyChangeListener props;
	
	public CrawlTask(JProgressBar jpb, JLabel currentDir, JLabel found, JLabel progress, JLabel timeToComplete,
		JLabel image, int imageSize, boolean fastmode, boolean resumeMode, String outputDir, String searchDir) {
		this.timeToComplete = timeToComplete;
		this.fastmode = fastmode;
		this.resumeMode = resumeMode;
		this.image = image;
		this.imageSize = imageSize;
		this.progress = progress;
		this.currentDir = currentDir;
		this.found = found;
		this.jpb = jpb;
		out = outputDir; 
		search = searchDir;
		json = new JsonHandler();
		time = new StopWatch();
		currentDir.setFont(new Font(currentDir.getFont().getFontName(),Font.PLAIN, 10));
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		if(fastmode) {
			System.out.println("[photoFinder] Fast mode on.");
		} else {
			System.out.println("[photoFinder] Fast mode off.");
		}
		time.reset();
		time.start();
		File output = new File(out);
		jsonOut = null; 
		if(!output.isDirectory()) {
			System.out.println("[photoFinder] Output folder not found!");
		} else {
			if(resumeMode) {
				jsonOut = Paths.get(output.getAbsolutePath(), "local.json").toFile();
				if(jsonOut.exists()) {
					File temp = new File(json.readFromFile(jsonOut));
					output = ZFileUtils.getFirstExistingParent(new File(temp.getParent()));
					System.out.println("[photoFinder(RESUME_MODE)] first existing path is "+output);
				} 
			}
		}
		File searchFile = new File(search);
		if(searchFile.isDirectory()) {
			numOfMoved = 0; 
			filesChecked = 0; 
			allInDir = 0;
			if(!fastmode) {
				countAllFiles(search);
			}
			currentDir.setFont(new Font(currentDir.getFont().getFontName(),Font.PLAIN, 10));
			crawlAndCopy(searchFile,output);
			json.writeToFile(jsonOut);
			System.out.println("\n[photoFinder] finished. ");
			return null;
		} else {
			System.out.println("[photoFinder] Search folder not found!");
		}
		return null;
	}
	
	@Override
    public void done() {
		jpb.setValue(100);
		time.stop();
        Toolkit.getDefaultToolkit().beep();
        timeToComplete.setText("Completed in " + (time.getTime()/1000.0) + " seconds.");
    }
	
	private void countAllFiles(String dirPath) {
		jpb.setIndeterminate(true);
		currentDir.setFont(new Font(currentDir.getFont().getFontName(),Font.PLAIN, 20));
		currentDir.setText("Calculating requirements...");
	    File f = new File(dirPath);
	    File[] files = f.listFiles();

	    if (files != null)
	    for (int i = 0; i < files.length; i++) {
	    	jpb.setString(allInDir+" files counted...");
	        File file = files[i];

	        if (file.isDirectory()) {   
	             countAllFiles(file.getAbsolutePath()); 
	        } else if(file.isFile()) {
	        	allInDir++;
	        }
	    }
	    jpb.setString("");
	    jpb.setIndeterminate(false);
	}
	
	public void crawlAndCopy(File dir, File outputDir) {
		File[] directoryListing = dir.listFiles();
		if(directoryListing != null) {
			for(File child : directoryListing) {
				System.err.println("[DEBUG] Iterating");
				if(child.isDirectory() && !child.getAbsolutePath().equals(outputDir.getAbsolutePath())) {
				System.err.println("[DEBUG] Child is directory that isn't output");
					crawlAndCopy(child,outputDir);
				} else {
					if(isCancelled()) {
						System.out.println("[DEBUG] Thread cancelled!");
						json.writeToFile(jsonOut);
						return;
					}
					if(checkIfImage(child)) {
						System.err.println("[DEBUG] Child is an image");
						File temp = new File(outputDir.getAbsolutePath(), child.getAbsolutePath());
						if(!temp.exists()) {
							copyTo(child,outputDir,child.getName());
						}
						numOfMoved++;
						found.setText(numOfMoved+" found.");
						if(!fastmode) {
							try {
								image.setIcon(new ImageIcon(Scalr.resize(ImageIO.read(child),imageSize)));
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (ImagingOpException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					filesChecked++;
					if(!fastmode) {
					
					progress.setText(filesChecked+"/"+allInDir);
					int percent = (int)(((double)filesChecked)/allInDir*100); 
					System.out.println( percent + "%");
					publish(percent);
					} else {
						progress.setText(filesChecked+"/?");
					}
					
				}
				currentDir.setText(child.getAbsolutePath());
			}
		}
	}
	
	@Override
	protected void process(List<Integer> prog) {
		int progress = prog.get(prog.size()-1);
		
		jpb.setValue(progress);
	}
	
	public boolean checkIfImage(File f) {
		boolean ret = false;
		//stolen off stackOverflow's Ismael
        String mimetype= new MimetypesFileTypeMap().getContentType(f);
       // System.out.println("\n\n"+mimetype);
        String type = mimetype.split("/")[0];
        if(type.equals("image")){
        		return true;
        } else if(mimetype.equals("application/octet-stream")) {
        	String temp = f.getName().substring(f.getName().lastIndexOf("."));
			if(temp.equalsIgnoreCase(".png")) {
        		return true; 
        	} 
		}
        return ret;
	}
	
	public void copyTo(File f,File outputDir,String copiedFileName) {
		if(Window.debug) System.out.println("[photoFinder] Copying " + f.getAbsolutePath() + " to " + outputDir.getAbsolutePath());
		final CopyOption[] options = new CopyOption[]{
				  StandardCopyOption.REPLACE_EXISTING,
				  StandardCopyOption.COPY_ATTRIBUTES
				}; 
		json.add(f);
		final Path FROM = Paths.get(f.getAbsolutePath());
		final Path TO  = Paths.get(outputDir.getAbsolutePath() + System.getProperty("file.separator") + copiedFileName );
		if(((f.length()/1024)/1024)>=8.0) {
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			        try {
						Files.copy(FROM, TO, options);
						if(Window.debug) System.out.println("[photoFinder] Large photo (>8MB) copied.");
					} catch (IOException e) {
						System.out.println("[photoFinder] couldn't copy file at " + FROM + " to " + TO);
						e.printStackTrace();
					}
			    }
			});
		} else {
			try {
				Files.copy(FROM, TO, options);
			} catch (IOException e) {
				
				System.out.println("[photoFinder] couldn't copy file at " + f.getPath() + " to " + outputDir.getPath());
				e.printStackTrace();
			}
		}
	}
	
	public String getExt(File f){
		String path = f.getAbsolutePath();
		return path.substring(path.lastIndexOf("."));
	}
	
}
