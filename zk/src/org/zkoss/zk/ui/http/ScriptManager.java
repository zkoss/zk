package org.zkoss.zk.ui.http;

import javax.servlet.ServletRequest;

public interface ScriptManager {
	public boolean isScriptIgnored(ServletRequest request, String jspath);
}
