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
	 * @return the inline macro will return mulitple Components.
	 */
	Component[] getComponents();
	/**
	 * 
	 * @param child Adds a child tag.
	 */
	void addChildTag(ComponentTag child);
	/**
	 * test if this tag's contain multiple inline prepared components. 
	 *
	 */
	boolean isInline();
}
