/* F110_ZK_6056Model.java

        Purpose:
                
        Description:
                
        History:
                Thu Oct 15 13:00:20 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.Locale;

import org.zkoss.zul.SimpleListModel;

public class F110_ZK_6056Model extends SimpleListModel<Locale> {
	public F110_ZK_6056Model() {
		super(Arrays.asList(Locale.getAvailableLocales()));
	}

	@Override
	protected int getMaxNumberInSubModel(int nRows) {
		return 50;
	}
}
