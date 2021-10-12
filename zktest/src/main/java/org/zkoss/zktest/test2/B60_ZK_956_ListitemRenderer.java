package org.zkoss.zktest.test2;

import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Popup;

public class B60_ZK_956_ListitemRenderer implements ListitemRenderer {

	public void render(Listitem item, Object data,int ind) throws Exception {
		render(item,data);
	}
	
	public void render(Listitem item, Object data) throws Exception {
		Listcell lstcell = new Listcell();
		
		Popup popTooltip = new Popup();
		Combobox combobox= new Combobox();
		Hbox hbox = new Hbox();
		combobox.setTooltip(popTooltip);
		hbox.appendChild(combobox);
		hbox.appendChild(popTooltip);

		lstcell.appendChild(hbox);
		item.appendChild(lstcell);
		
	}

}
