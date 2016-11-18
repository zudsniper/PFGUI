package cc.holstr.PFGUI.gui;

public class ProgressObj {
	private String currentDir;
	private int numFound;
	private int numTotal;
	
	public ProgressObj(String currentDir, int numFound, int numTotal) {
		super();
		this.currentDir = currentDir;
		this.numFound = numFound;
		this.numTotal = numTotal;
	}
	
	public String getCurrentDir() {
		return currentDir;
	}
	public void setCurrentDir(String currentDir) {
		this.currentDir = currentDir;
	}
	public int getNumFound() {
		return numFound;
	}
	public void setNumFound(int numFound) {
		this.numFound = numFound;
	}
	public int getNumTotal() {
		return numTotal;
	}
	public void setNumTotal(int numTotal) {
		this.numTotal = numTotal;
	}
	
	
	
}
