package cc.holstr.PFGUI.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterModel {
	private List<Mime> mimes;
	private List<String> exts;

public class Mime {
	protected String mime; 
	protected List<String> extRestrictions;
	}

public Filter toFilter(String filterName) {
	HashMap<String, FilterRestriction> mimetypes = new HashMap<String, FilterRestriction>();
	HashSet<String>	exts = new HashSet<String>();
	
	for(Mime m : mimes) {
		boolean generic = false;
		if(m.mime.contains("@")) {
			generic = true;
		} 
		HashSet<String> whitelist = new HashSet<String>();
		HashSet<String>	blacklist = new HashSet<String>();
		if(m.extRestrictions!=null) {
			for(String restriction : m.extRestrictions) {
				if(restriction.contains("!")) {
					blacklist.add(restriction.substring(1));
				} else {
					whitelist.add(restriction);
				}
			}
		} 
		mimetypes.put(m.mime, new FilterRestriction(generic,whitelist,blacklist));
		
			
		
	}

	for(String ext : this.exts) {
		exts.add(ext);
	}
	
	return new Filter(filterName, mimetypes,exts);
}
}