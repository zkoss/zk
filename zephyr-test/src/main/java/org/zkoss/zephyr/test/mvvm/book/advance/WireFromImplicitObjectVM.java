/* WireFromImplicitObjectVM.java

		Purpose:
		
		Description:
		
		History:
				Wed May 05 17:12:04 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.mvvm.book.advance;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.select.annotation.WireVariable;

public class WireFromImplicitObjectVM {
	@WireVariable
	private Page page;

	@WireVariable
	private Desktop desktop;

	@WireVariable
	private Session sess;

	@WireVariable
	private WebApp wapp;

	public Page getPage() {
		return page;
	}

	public Desktop getDesktop() {
		return desktop;
	}

	public Session getSess() {
		return sess;
	}

	public WebApp getWapp() {
		return wapp;
	}
}
