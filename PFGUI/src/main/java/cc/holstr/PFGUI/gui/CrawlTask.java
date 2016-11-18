package cc.holstr.PFGUI.gui;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.apache.commons.lang.time.StopWatch;
import org.imgscalr.Scalr;

import cc.holstr.PFGUI.json.JsonHandler;

public class CrawlTask extends SwingWorker<Long, ProgressObj>{

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
	
	private boolean fastmode;
	
	private long time;
	
	public CrawlTask(JProgressBar jpb, JLabel currentDir, JLabel found, JLabel progress, JLabel timeToComplete, JLabel image, int imageSize, boolean fastmode, String outputDir, String searchDir) {
		this.timeToComplete = timeToComplete;
		this.fastmode = fastmode;
		this.image = image;
		this.imageSize = imageSize;
		this.progress = progress;
		this.currentDir = currentDir;
		this.found = found;
		this.jpb = jpb;
		out = outputDir; 
		search = searchDir;
		json = new JsonHandler();
		currentDir.setFont(new Font(currentDir.getFont().getFontName(),Font.PLAIN, 10));
	}
	
	@Override
	protected Long doInBackground() throws Exception {
		if(fastmode) {
			System.out.println("[photoFinder] Fast mode on.");
		} else {
			System.out.println("[photoFinder] Fast mode off.");
		}
		
		StopWatch time = new StopWatch();
		time.start();
		File output = new File(out);
		File jsonOut = null; 
		if(!output.isDirectory()) {
			System.out.println("[photoFinder] Output folder not found!");
		} else {
			jsonOut = Paths.get(output.getAbsolutePath(), "local.json").toFile();
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
			time.stop();
			this.time = time.getTime();
			System.out.println("\n[photoFinder] finished. ");
			return time.getTime();
		} else {
			System.out.println("[photoFinder] Search folder not found!");
		}
		return null;
	}
	
	@Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        timeToComplete.setText("Completed in " + (time/1000.0) + " seconds.");
    }
	
	synchronized private void countAllFiles(String dirPath) {
		currentDir.setFont(new Font(currentDir.getFont().getFontName(),Font.PLAIN, 20));
		currentDir.setText("Calculating requirements...");
	    File f = new File(dirPath);
	    File[] files = f.listFiles();

	    if (files != null)
	    for (int i = 0; i < files.length; i++) {
	        allInDir++;
	        File file = files[i];

	        if (file.isDirectory()) {   
	             countAllFiles(file.getAbsolutePath()); 
	        }
	    }
	}
	
	public void crawlAndCopy(File dir, File outputDir) {
		File[] directoryListing = dir.listFiles();
		if(directoryListing != null) {
			for(File child : directoryListing) {
				if(child.isDirectory() && !child.getAbsolutePath().equals(outputDir.getAbsolutePath())) {
					crawlAndCopy(child,outputDir);
				} else {
					if(checkIfImage(child)) {
						File temp = new File(outputDir.getAbsolutePath(), child.getAbsolutePath());
						if(temp.exists()) {
							json.add(temp);
						} else {
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
					progress.setText(filesChecked+"/"+allInDir);
					jpb.setValue(100*(filesChecked/allInDir));
					
				}
				currentDir.setText(child.getAbsolutePath());
				publish(new ProgressObj(child.getAbsolutePath(),numOfMoved,filesChecked));
			}
		}
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
			if(f.getName().contains(".png") || f.getName().contains(".PNG")) {
        		return true; 
        	} 
		}
        return ret;
	}
	
	public void copyTo(File f,File outputDir,String copiedFileName) {
		CopyOption[] options = new CopyOption[]{
				  StandardCopyOption.REPLACE_EXISTING,
				  StandardCopyOption.COPY_ATTRIBUTES
				}; 
		json.add(f);
		Path FROM = Paths.get(f.getAbsolutePath());
		Path TO  = Paths.get(outputDir.getAbsolutePath() + System.getProperty("file.separator") + copiedFileName );
		try {
			Files.copy(FROM, TO, options);
		} catch (IOException e) {
			System.out.println("[photoFinder] couldn't copy file at " + f.getPath() + " to " + outputDir.getPath());
			e.printStackTrace();
		}
	}
	
	public String getExt(File f){
		String path = f.getAbsolutePath();
		return path.substring(path.lastIndexOf("."));
	}
	
}
