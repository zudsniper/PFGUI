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
	private String filterName; 
	private HashMap<String, FilterRestriction> mimetypes;
	private Set<String> exts;
	
	public Filter(String filterName, HashMap<String, FilterRestriction> mimetypes, Set<String> exts) {
		super();
		this.setFilterName(filterName);
		this.mimetypes = mimetypes;
		this.exts = exts;
	}

	private boolean checkByMime(File f) {
		String ext; 
	 if(f.getName().lastIndexOf(".")==0) {
		 ext = "";
	 } else {
	 ext = f.getName().substring(f.getName().lastIndexOf("."));
	 }
	 String mimetype= new MimetypesFileTypeMap().getContentType(f);
	 String type = mimetype.split("/")[0];
	 System.out.println("ext: " + ext);
	 System.out.println("mime: " + mimetype);
	 System.out.println("general type: " + type);
	 boolean mime = false;
	 String checkMime = mimetype;
	 if(mimetypes.containsKey("@"+type)) {
		 checkMime = "@"+type;
	 }
	 if(mimetypes.containsKey(checkMime)) {
		 Set<String> blacklist = mimetypes.get(checkMime).getBlacklist();
		 if(blacklist!=null) {
			if(!blacklist.contains(ext) || blacklist.size()==0) {
				Set<String> whitelist = mimetypes.get(checkMime).getWhitelist();
				if(whitelist!=null) {
						mime = whitelist.contains(ext) || whitelist.size()==0;
				}
			}  
		 }
	 }
	 return mime;
	 
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
			Filter built = model.toFilter(f.getName());
			System.out.println(built);
			return built;
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
	
	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public String toString() {
		String output = "FILTER: \n";
		if(mimetypes!=null) {
			output+= "mimes : ";
			for(String mime : mimetypes.keySet()) {
				output+= mime + ", ";
			}
		}
		if(exts!=null) {
			output+="\nexts : ";
			for(String ext : exts) {
				output+= ext + ", ";
			}
		}
		return output;
	}

}