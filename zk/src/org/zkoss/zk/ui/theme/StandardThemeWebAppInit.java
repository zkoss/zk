package org.zkoss.zk.ui.theme;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

public class StandardThemeWebAppInit implements WebAppInit {

	@Override
    public void init(WebApp webapp) throws Exception {
		Themes.register(Themes.BREEZE_NAME, Themes.BREEZE_DISPLAY, Themes.BREEZE_PRIORITY);
    }
}
