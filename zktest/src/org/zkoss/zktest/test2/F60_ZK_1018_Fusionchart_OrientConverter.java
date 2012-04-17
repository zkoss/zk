package org.zkoss.zktest.test2;

import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Radiogroup;

public class F60_ZK_1018_Fusionchart_OrientConverter implements TypeConverter {
	public Object coerceToBean(java.lang.Object val,
			org.zkoss.zk.ui.Component comp) {
		return ((Radiogroup) comp).getSelectedItem().getLabel();
	}

	public Object coerceToUi(java.lang.Object val,
			org.zkoss.zk.ui.Component comp) {
		
		((Radiogroup) comp).setSelectedIndex("vertical".equals(val) ? 0 : 1);
		return "";
	}
}