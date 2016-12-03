package cc.holstr.PFGUI.filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Filter {
	public HashMap<String, FilterRestriction> mimetypes;
	public Set<String> exts;
	
	public Filter(HashMap<String, FilterRestriction> mimetypes, Set<String> exts) {
		super();
		this.mimetypes = mimetypes;
		this.exts = exts;
	}

	private boolean checkByMime(File f) {
	 String ext = f.getName().substring(f.getName().lastIndexOf("."));
	 String mimetype= new MimetypesFileTypeMap().getContentType(f);
	 String type = mimetype.split("/")[0];
	 boolean mime = false;
	 if(mimetypes.containsKey(mimetype) || mimetypes.containsKey("@"+type)) {
		 Set<String> blacklist = mimetypes.get(mimetype).getBlacklist();
		 if(blacklist!=null) {
			if(!blacklist.contains(ext) || blacklist.size()==0) {
				Set<String> whitelist = mimetypes.get(mimetypes).getWhitelist();
				if(whitelist!=null) {
						return whitelist.contains(ext) || whitelist.size()==0;
				}
			}  
		 }
	 }
	 return (mime);
	 
	}

	private boolean checkByExt(File f) {
	String ext = f.getName().substring(f.getName().lastIndexOf("."));
	return exts.contains(ext);
	}

	public boolean check(File f) {
		return checkByMime(f) || checkByExt(f);
	}
	
	public static Filter build(File f) {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeNulls();
		builder.setLenient();
		Gson gson = builder.create();
		
		try {
			FilterModel model = gson.fromJson(new FileReader(f), FilterModel.class);
			System.out.println("[photoFinder] Filter for " + f.getName() +" built.");
			return model.toFilter();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.err.println("[photoFinder] filter builder returning null on file " + f.getAbsolutePath());
		return null; 
	}
}