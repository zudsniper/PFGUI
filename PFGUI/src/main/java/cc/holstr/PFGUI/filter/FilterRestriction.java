package cc.holstr.PFGUI.filter;

import java.util.HashSet;

public class FilterRestriction {
	private boolean generic;
	private HashSet<String> whitelist; 
	private HashSet<String> blacklist;
	
	public FilterRestriction(boolean generic, HashSet<String> whitelist, HashSet<String> blacklist) {
		super();
		this.generic = generic;
		this.whitelist = whitelist;
		this.blacklist = blacklist;
	}
	
	public boolean isGeneric() {
		return generic;
	}

	public void setGeneric(boolean generic) {
		this.generic = generic;
	}

	public HashSet<String> getWhitelist() {
		return whitelist;
	}
	public void setWhitelist(HashSet<String> whitelist) {
		this.whitelist = whitelist;
	}
	public HashSet<String> getBlacklist() {
		return blacklist;
	}
	public void setBlacklist(HashSet<String> blacklist) {
		this.blacklist = blacklist;
	}
}
