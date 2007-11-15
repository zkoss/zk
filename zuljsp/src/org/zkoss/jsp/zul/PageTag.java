/* PageTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 20 11:32:56     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsp.zul;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.zkoss.jsp.zul.impl.RootTag;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;

/**
 * Defines a ZK page.
 * It is reponsible for doing the lifecycle for ZK components, such
 * as event processing and rendering,
 *
 * <p>All other ZK tags must be placed inside a {@link PageTag} tag.
 * Nested page tags are not allowed.
 *
 * @author tomyeh
 */
public class PageTag extends RootTag {
	private String _style;
	private String _id;

	/** Returns the style.
	 * Default: null (no style at all).
	 */
	public String getStyle() {
		return _style;
	}
	/** Sets the style.
	 */
	public void setStyle(String style) {
		_style = style != null && style.length() > 0 ? style: null;
	}
	/**
	 *  if not set before, ZK will generate one for this.
	 * @return this page id.
	 */
	public String getId() {
		return _id;
	}
	/**
	 * if not set before, ZK will generate one for this.
	 * @param id
	 */
	public void setId(String id) {
		_id = id;
	}
	
	/** Creates and returns the page.
	 */
	protected void init(Execution exec, Page page) {
		super.init(exec, page);
		// load page component definition
		Map compDefs = (Map)getJspContext().getAttribute(Const.CONTEXT_KEY);
		if(compDefs!=null)
		{
			for(Iterator it = compDefs.values().iterator();it.hasNext();)
				((ComponentDefinitionTag)it.next()).registComponentDefinition(page);
				
		}
		page.setId(_id);
		page.setStyle(_style);
	}


}
