package cc.holstr.PFGUI.gui;

import java.util.ArrayList;

public class FilterEnabler extends ArrayList<GUIFilterModel>{
	
	public FilterEnabler() {
		super();
		
	}
	
	protected void becameEnabled(GUIFilterModel enabled) {
		for(GUIFilterModel model : this) {
			if(!model.getName().equals(enabled.getName())) {
				model.disable();
			} 
		}
	}
	
}
