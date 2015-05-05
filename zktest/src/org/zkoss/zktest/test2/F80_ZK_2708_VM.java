package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Rowlayout;

public class F80_ZK_2708_VM {
	private int _ncols = 12;
	private String _spacing = "20/60";
		
	@Wire("#rowlayout1")
	Rowlayout rowlayout;
	
	private List<Component> detached = new ArrayList<Component>();
		
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);       
    }

	public int getNcols() {
		return _ncols;
	}

	@NotifyChange("ncols")
	public void setNcols(int ncols) {
		List<Component> children = rowlayout.getChildren();
		int size = children.size();
		if (ncols < _ncols) {
			while (size > ncols) {
				Component detaching = children.get(--size);
				detached.add(detaching);
				detaching.detach();
			}
		} else {
			int last = detached.size()-1;
			while (size++ < ncols) {
				Component attaching = detached.get(last--);
				attaching.setParent(rowlayout);
				detached.remove(attaching);
			}
			String js = "jq('.z-window-embedded-cnt').attr('contentEditable', '').css('min-height', '30px')";
			Clients.evalJavaScript(js);
		}
				
		_ncols = ncols;
	}

	public String getSpacing() {
		return _spacing;
	}

	@NotifyChange("spacing")
	public void setSpacing(String spacing) {
		_spacing = spacing;
	}
}
