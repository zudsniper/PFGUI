package cc.holstr.util;

import java.io.File;
import java.io.IOException;

public class ZFileUtils {

	public static boolean isDirectoryValid(String path) {
		return (path.contains(System.getProperty("file.separator")));
	}
	
	public static boolean isDirectoryValid(File file) {
	    try {
	       file.getCanonicalPath();
	       return true;
	    }
	    catch (IOException e) {
	       return false;
	    }
	}
}
