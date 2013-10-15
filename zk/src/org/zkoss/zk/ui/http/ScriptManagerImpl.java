package org.zkoss.zk.ui.http;

import javax.servlet.ServletRequest;

public class ScriptManagerImpl implements ScriptManager {
	public boolean isScriptIgnored(ServletRequest request, String jspath) {
		return false;
	}
}
