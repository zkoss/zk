/**
 * 
 */
package org.zkoss.jsp.zul.impl;

import org.zkoss.zk.ui.Component;

/**
 * @author ian
 *
 */
public interface ComponentTag {
	
	/**
	 * 
	 * @return the page tag that this tag belongs to.
	 */
	RootTag getRootTag();
	/** 
	 * @return the parent tag.
	 */
	ComponentTag getParentTag();
	/**
	 * 
	 * @return the component associated with this tag.
	 */
	Component getComponent();
	/**
	 * 
	 * @param child Adds a child tag.
	 */
	void addChildTag(ComponentTag child);
}
