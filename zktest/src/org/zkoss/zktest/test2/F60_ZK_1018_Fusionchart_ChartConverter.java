package org.zkoss.zktest.test2;

import java.util.Iterator;

import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

public class F60_ZK_1018_Fusionchart_ChartConverter implements TypeConverter {
	public Object coerceToBean(java.lang.Object val,
			org.zkoss.zk.ui.Component comp) {
		return ((Listbox) comp).getSelectedItem().getValue();
	}

	public Object coerceToUi(java.lang.Object val,
			org.zkoss.zk.ui.Component comp) {
		
		Listbox lb = (Listbox) comp;
		
		int index = 0;
		for (Iterator it = lb.getItems().iterator(); it.hasNext(); index++) {
			Listitem item = (Listitem) it.next();
			if (val.equals(item.getValue())) {
				item.setSelected(true);
				break;
			}
		}
		
		return "";
	}
}