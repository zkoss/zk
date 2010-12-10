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
	private void show(String msg) {
		final Execution exec = Executions.getCurrent();
		if (exec != null && exec.getDesktop() == _inf.getDesktop())
			new Label(msg).setParent(_inf);
	}
	public void init(Session sess) {
		sess.addScopeListener(_sessListener = new ScopeListener() {
			public void attributeAdded(Scope scope, String name, Object value) {
				show("sess added: "+name+"="+value);
			}
			public void attributeReplaced(Scope scope, String name, Object value) {
				show("sess replaced: "+name+"="+value);
			}
			public void attributeRemoved(Scope scope, String name) {
				show("sess removed: "+name);
			}
			public void parentChanged(Scope scope, Scope newparent) {
			}
			public void idSpaceChanged(Scope scope, IdSpace newIdSpace) {
			}
		});
	}
	public void init(WebApp app) {
		app.addScopeListener(_appListener = new ScopeListener() {
			public void attributeAdded(Scope scope, String name, Object value) {
				show("app added: "+name+"="+value);
			}
			public void attributeReplaced(Scope scope, String name, Object value) {
				show("app replaced: "+name+"="+value);
			}
			public void attributeRemoved(Scope scope, String name) {
				show("app removed: "+name);
			}
			public void parentChanged(Scope scope, Scope newparent) {
			}
			public void idSpaceChanged(Scope scope, IdSpace newIdSpace) {
			}
		});
	}
	public void init(Execution exec) {
		exec.addScopeListener(new ScopeListener() {
			public void attributeAdded(Scope scope, String name, Object value) {
				show("req added: "+name+"="+value);
			}
			public void attributeReplaced(Scope scope, String name, Object value) {
				show("req replaced: "+name+"="+value);
			}
			public void attributeRemoved(Scope scope, String name) {
				show("req removed: "+name);
			}
			public void parentChanged(Scope scope, Scope newparent) {
			}
			public void idSpaceChanged(Scope scope, IdSpace newIdSpace) {
			}
		});
	}
}
