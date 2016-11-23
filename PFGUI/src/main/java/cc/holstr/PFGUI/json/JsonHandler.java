package cc.holstr.PFGUI.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.apache.commons.io.FileUtils;

public class JsonHandler {
	
	private JsonObjectBuilder main;
	private JsonArrayBuilder inf; 
	
	public JsonHandler() {
		inf = Json.createArrayBuilder();
	}
	
	public void add(File f) {
		inf.add(Json.createObjectBuilder()
				.add("file", f.getAbsolutePath())
				);
	}
	
	public String readFromFile(File f) {
		String lastPath = null;
		try {
			JsonReader jr = Json.createReader(new FileInputStream(f));
			JsonArray sheetsArray = jr.readObject().getJsonArray("filedirs");
			for (JsonObject obj : sheetsArray.getValuesAs(JsonObject.class)) { 
				add(new File(obj.getString("file")));
				lastPath = obj.getString("file");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return lastPath;
	}
	
	public void writeToFile(File f) {
		main = Json.createObjectBuilder()
				.add("filedirs",inf);
		try {
			FileUtils.writeStringToFile(f, main.build().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
