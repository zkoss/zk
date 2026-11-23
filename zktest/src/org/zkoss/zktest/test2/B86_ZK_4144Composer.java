/* B86_ZK_4144Composer.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec 21 16:15:07 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Textbox;


/**
 * @author rudyhuang
 */
public class B86_ZK_4144Composer extends GenericForwardComposer {
	private Textbox field1;
	private Textbox field2;
	private Textbox field3;
	private Textbox field4;
	private Groupbox gb;

	public void onClick$save() {
		List<WrongValueException> wve = new ArrayList<>();
		if ("".equals(field1.getValue())) {
			gb.setOpen(true);
			wve.add(new WrongValueException(field1, "Field 1 is mandatory"));
		}
		if ("".equals(field2.getValue())) {
			gb.setOpen(true);
			wve.add(new WrongValueException(field2, "Field 2 is mandatory"));
		}
		if ("".equals(field3.getValue())) {
			gb.setOpen(true);
			wve.add(new WrongValueException(field3, "Field 3 is mandatory"));
		}
		if ("".equals(field4.getValue())) {
			gb.setOpen(true);
			wve.add(new WrongValueException(field4, "Field 4 is mandatory"));
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}
}
