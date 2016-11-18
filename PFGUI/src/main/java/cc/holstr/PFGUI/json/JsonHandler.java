package cc.holstr.PFGUI.json;

import java.io.File;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.apache.commons.io.FileUtils;

public class JsonHandler {
	
	private JsonObjectBuilder main;
	private JsonArrayBuilder inf; 
	
	public JsonHandler() {
		inf = Json.createArrayBuilder();
	}
	
	public void add(File f) {
		inf.add(Json.createObjectBuilder()
				.add(f.getName(), f.getAbsolutePath())
				);
	}
	
	public void writeToFile(File f) {
		main = Json.createObjectBuilder()
				.add("filedirs",inf);
		try {
			FileUtils.writeStringToFile(f, main.build().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
