/* ZKTestThemeWebAppInit.java

        Purpose:

        Description:

        History:
                Fri Oct 16 11:23:12 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.http;

import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

public class ZKTestThemeWebAppInit implements WebAppInit {
	public void init(WebApp wapp) throws Exception {
		// zk.xml's library-property shadows a -Dorg.zkoss.theme.preferred, so override it here
		String theme = System.getProperty("zktest.theme");
		if (!Strings.isBlank(theme))
			Library.setProperty("org.zkoss.theme.preferred", theme);
	}
}
