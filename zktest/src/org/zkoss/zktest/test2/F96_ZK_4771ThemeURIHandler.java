/* F96_ZK_4771ThemeURIHandler.java

		Purpose:
		
		Description:
		
		History:
				Fri Mar 05 11:49:59 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.web.fn.ThemeFns;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ThemeURIHandler;
import org.zkoss.zk.ui.util.ThemeURIModifier;

public class F96_ZK_4771ThemeURIHandler implements ThemeURIHandler {
	@Override
	public void modifyThemeURIs(Execution exec, ThemeURIModifier modifier) {
		if ("atlantic".equals(ThemeFns.getCurrentTheme()))
			modifier.add(1, "/test2/css/F96-ZK-4771-Style2.css");
		else
			modifier.add("/test2/css/F96-ZK-4771-Style.css");
	}
}
