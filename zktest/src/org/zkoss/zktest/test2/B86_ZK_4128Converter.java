/* B86_ZK_4128Converter.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 11 16:08:39 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.converter.sys.ListboxModelConverter;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;

/**
 * @author rudyhuang
 */
public class B86_ZK_4128Converter extends ListboxModelConverter {
	public B86_ZK_4128Converter() {
		System.out.println(B86_ZK_4128Converter.class);
	}

	@Override
	protected ListModel<?> getComponentModel(Listbox comp) {
		comp.setAttribute("B86_ZK_4128Converter", true);
		return super.getComponentModel(comp);
	}
}
