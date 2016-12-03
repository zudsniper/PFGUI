package cc.holstr.PFGUI.load;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

public class ResourceLoader {
	
	public static Image getImageFromResources(String path) {
		InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(path);
		try {
			return ImageIO.read(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static InputStream resource(String path) {
		return ResourceLoader.class.getClassLoader().getResourceAsStream(path);
	}
	
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
}
