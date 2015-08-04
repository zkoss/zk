/** DataHandlerInfo.java.

	Purpose:
		
	Description:
		
	History:
		2:17:29 PM Apr 24, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.util;

import java.util.List;
import java.util.Map;

/**
 * A data handler info for {@link Configuration} to use.
 * @author jumperchen
 * @since 8.0.0
 */
public class DataHandlerInfo {
	final private String name;
	final private String script;
	final private String scriptUri;
	final private List<String> depends;
	final private List<Map<String, String>> links;
	final private boolean override;
	
	/**
	 * Construct the data handler info
	 * @param name the data attribute name
	 * @param script the script of the data attribute handler (Optional)
	 * @param scriptUri the script uri of the data attribute handler (Optional)
	 * @param depends a list of Javascript files where the script depends on. (Optional)
	 * @param override true if the data handler is to override to another one. (Optional)
	 */
	public DataHandlerInfo(String name, String script, String scriptUri, List<String> depends, boolean override, List<Map<String, String>> links) {
		this.name = name;
		this.script = script;
		this.scriptUri = scriptUri;
		this.depends = depends;
		this.override = override;
		this.links = links;
	}
	
	/**
	 * Returns the attribute name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Returns the script of the data handler.
	 */
	public String getScript() {
		return script;
	}
	/**
	 * Returns the script uri of the data handler.
	 */
	public String getScriptUri() {
		return scriptUri;
	}
	/**
	 * Returns a list of Javascript files where the script depends on
	 */
	public List<String> getDepends() {
		return depends;
	}
	/**
	 * Returns whether the handler is to override another one.
	 */
	public boolean isOverride() {
		return override;
	}
	/**
	 * Returns a list of link
	 */
	public List<Map<String, String>> getLinks() {
		return links;
	}
}
