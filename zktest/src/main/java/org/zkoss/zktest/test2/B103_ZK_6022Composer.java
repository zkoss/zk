/* B103_ZK_6022Composer.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 05 10:30:35 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.IOException;
import java.util.Locale;

import org.zkoss.util.Locales;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

public class B103_ZK_6022Composer extends SelectorComposer {
	@Listen("onClick=#btn")
	public void changeLocale() throws IOException {
		Locale locale = Locales.getLocale("zh_TW");
		Executions.getCurrent().getSession().setAttribute(Attributes.PREFERRED_LOCALE, locale);
		Clients.reloadMessages(locale);
		Locales.setThreadLocal(locale);
	}
}
