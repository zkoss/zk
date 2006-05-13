/* ExecutionCtrl.java

{{IS_NOTE
	$Id: ExecutionCtrl.java,v 1.9 2006/04/17 07:10:25 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Jun  6 14:36:47     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.event.Event;
import com.potix.zk.ui.metainfo.PageDefinition;

/**
 * Additional interface to {@link com.potix.zk.ui.Execution}
 * for implementation.
 *
 * <p>Application developers shall never access any of this methods.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.9 $ $Date: 2006/04/17 07:10:25 $
 */
public interface ExecutionCtrl {
	/** Returns the current page.
	 * Though an execution might process many pages, it processes update
	 * requests one-by-one and each update request is associated
	 * with a page.
	 *
	 * <p>Design decision: we put it here because user need not to know
	 * about the conccept of the current page.
	 *
	 * @see com.potix.zk.ui.Desktop#getPage
	 */
	public Page getCurrentPage();
	/** Sets the current page.
	 * Though an execution might process many pages, it processes update requests
	 * one-by-one and each update request is associated with a page.
	 */
	public void setCurrentPage(Page page);

	/** Returns the current page definition.
	 *
	 * <p>Note: it might not be the same as getCurrentPage().getDefinition(),
	 * because developer might use createComponents to load different
	 * page definitions and create components into the same page.
	 *
	 * @param fallback whether to retrieve {@link #getCurrentPage}'s
	 * {@link Page#getDefinition} if {@link #setCurrentPageDefinition} is set
	 * with null.
	 * In other words, specify false ONLY IF you want to store the old value
	 * and restore it later by {@link #setCurrentPageDefinition}.
	 */
	public PageDefinition getCurrentPageDefinition(boolean fallback);
	/** Sets the current page definition.
	 * @param pgdef the page definition. If null, it means it is the same
	 * as getCurrentPage().getPageDefinition().
	 */
	public void setCurrentPageDefinition(PageDefinition pgdef);

	/** Returns the next event queued by
	 * {@link com.potix.zk.ui.Execution#postEvent}, or null if no event queued.
	 */
	public Event getNextEvent();

	/** Returns whether this execution is activated.
	 */
	public boolean isActivated();
	/** Called when this execution is about to activate.
	 * It is called before any execution.
	 * <p>It is used as callback notification.
	 */
	public void onActivate();
	/** Called when this execution is about to de-activate.
	 * It is called before ending, no matter exception being throw or not.
	 * <p>It is used as callback notification.
	 */
	public void onDeactivate();

	/** Sets the {@link Visualizer} for this execution.
	 * It could be anything that {@link UiEngine} requires.
	 */
	public void setVisualizer(Visualizer ei);
	/** Returns the {@link Visualizer} for this execution
	 * (set by {@link #setVisualizer}.
	 */
	public Visualizer getVisualizer();

	/** Sets the header of response.
	 */
	public void setHeader(String name, String value);

	/** Returns the value of an attribute in the client request
	 * (e.g., HTTP request) that creates this execution.
	 *
	 * <p>Notice that a servlet might include serveral ZK pages,
	 * while an independent execution is created for adding a new page.
	 * It means, each client request might create several executions.
	 * Thus, the attribute set by this method might last for several
	 * executions (until the request ends).
	 */
	public Object getRequestAttribute(String name);
	/** Sets the value of an attribute to the client request
	 * (e.g., HTTP request) that creates this execution.
	 */
	public void setRequestAttribute(String name, Object value);
}
