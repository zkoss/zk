/** DataHandlerInfo.java.

	Purpose:
		
	Description:
		
	History:
		2:17:29 PM Apr 24, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.util;

import org.zkoss.util.Pair;
import java.util.List;
import java.util.Map;

/**
 * A data handler info for {@link Configuration} to use.
 * @author jumperchen
 * @since 8.0.0
 */
public class DataHandlerInfo {
	final private String name;
	final private List<Pair<String, String>> scripts;
	final private List<Map<String, String>> links;
	final private boolean override;
	
	/**
	 * Construct the data handler info
	 * @param name the data attribute name
	 * @param scripts a list of Javascript files or the scripts of the data attribute handler (one or many)
	 * @param override true if the data handler is to override to another one. (Optional)
	 */
	public DataHandlerInfo(String name, List<Pair<String, String>> scripts, boolean override, List<Map<String, String>> links) {
		this.name = name;
		this.scripts = scripts;
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
	 * Returns a list of Javascript files or the scripts of the data attribute handler.
	 */
	public List<Pair<String, String>> getScripts() {
		return scripts;
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
