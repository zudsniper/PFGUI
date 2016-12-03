package cc.holstr.PFGUI.load;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

public class Unpacker {
	private static Path storePath;
	private static Path filtersPath;
	public static File filters;
	public static void unpack() {
		storePath  = Paths.get(System.getProperty("user.home"),".store","PFGUI");
		filtersPath = Paths.get(storePath.toString(),"filters");
		filters = filtersPath.toFile();
		if(!filtersPath.toFile().exists()) {
			System.out.println("UNPACKER : Creating store directory...");
			try {
				Files.createDirectories(filtersPath);
				System.out.println("Success.");
			} catch (IOException e) {
				System.out.println("ERROR : Failed to create configuration file.");
			}
		}
		File photos = Paths.get(filtersPath.toString(),"photos.json").toFile();
		if(!photos.exists()) {
			System.out.println("UNPACKER : Creating default filter... (photos.json)");
			try {
				copyToFile(ResourceLoader.resource("photos.json"),photos);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static File copyToFile(InputStream s, File f) throws IOException {
		if(!f.exists()) {
			Path p = Paths.get(f.getAbsolutePath());
			Files.createDirectories(p.getParent());
			f.createNewFile();
		}
		FileWriter fw = new FileWriter(f);
		IOUtils.copy(s, fw, "UTF-8");
		fw.close();
		s.close();
		return f;
	}
}
