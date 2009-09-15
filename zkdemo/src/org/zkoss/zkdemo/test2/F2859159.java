/* F2859159.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 15 18:33:28     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.ext.*;
import org.zkoss.zul.*;

/**
 * Test case
 * @author tomyeh
 * @since 5.0.0
 */
public class F2859159 {
	final Component _inf;
	ScopeListener _sessListener, _appListener;
	public F2859159(Component inf) {
		_inf = inf;
		inf.getDesktop().addListener(new org.zkoss.zk.ui.util.DesktopCleanup() {
			public void cleanup(Desktop desktop) {
				if (_sessListener != null)
					desktop.getSession().removeScopeListener(_sessListener);
				if (_appListener != null)
					desktop.getWebApp().removeScopeListener(_appListener);
			}
		});
	}
	public void init(Session sess) {
		sess.addScopeListener(_sessListener = new ScopeListener() {
			public void attributeAdded(Scope scope, String name, Object value) {
				new Label("sess added: "+name+"="+value).setParent(_inf);
			}
			public void attributeReplaced(Scope scope, String name, Object value) {
				new Label("sess replaced: "+name+"="+value).setParent(_inf);
			}
			public void attributeRemoved(Scope scope, String name) {
				new Label("sess removed: "+name).setParent(_inf);
			}
			public void parentChanged(Scope scope, Scope newparent) {
			}
		});
	}
	public void init(WebApp app) {
		app.addScopeListener(_appListener = new ScopeListener() {
			public void attributeAdded(Scope scope, String name, Object value) {
				new Label("app added: "+name+"="+value).setParent(_inf);
			}
			public void attributeReplaced(Scope scope, String name, Object value) {
				new Label("app replaced: "+name+"="+value).setParent(_inf);
			}
			public void attributeRemoved(Scope scope, String name) {
				new Label("app removed: "+name).setParent(_inf);
			}
			public void parentChanged(Scope scope, Scope newparent) {
			}
		});
	}
	public void init(Execution exec) {
		exec.addScopeListener(new ScopeListener() {
			public void attributeAdded(Scope scope, String name, Object value) {
				new Label("req added: "+name+"="+value).setParent(_inf);
			}
			public void attributeReplaced(Scope scope, String name, Object value) {
				new Label("req replaced: "+name+"="+value).setParent(_inf);
			}
			public void attributeRemoved(Scope scope, String name) {
				new Label("req removed: "+name).setParent(_inf);
			}
			public void parentChanged(Scope scope, Scope newparent) {
			}
		});
	}
}
