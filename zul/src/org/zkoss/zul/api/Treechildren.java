/* Treechildren.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import java.util.Collection;
import org.zkoss.zk.ui.WrongValueException;

/**
 * A treechildren.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Treechildren extends org.zkoss.zul.impl.api.XulElement {

	public org.zkoss.zul.api.Tree getTreeApi();

	/**
	 * Returns the {@link Treerow} that is associated with this treechildren, or
	 * null if no such treerow. In other words, it is
	 * {@link Treeitem#getTreerowApi} of {@link #getParent}.
	 * 
	 * @see org.zkoss.zul.Treerow#getLinkedTreechildren
	 */
	public org.zkoss.zul.api.Treerow getLinkedTreerowApi();

	/**
	 * Returns a readonly list of all descending {@link Treeitem} (children's
	 * children and so on).
	 * 
	 * <p>
	 * Note: the performance of the size method of returned collection is no
	 * good.
	 */
	public Collection getItems();

	/**
	 * Returns the number of child {@link Treeitem} including all descendants.
	 * The same as {@link #getItems}.size().
	 * <p>
	 * Note: the performance is no good.
	 */
	public int getItemCount();

	/**
	 * Returns the number of visible descendant {@link Treeitem}. Descendants
	 * include direct children, gran children and so on.
	 * 
	 */
	public int getVisibleItemCount();

	/**
	 * @deprecated As of release 3.0.7, the page size is controlled by
	 *             {@link Tree#getPageSize} rather than this method. It always
	 *             return -1 since 3.0.7.
	 */
	public int getPageSize();

	/**
	 * @deprecated As of release 3.0.7, the page size is controlled by
	 *             {@link Tree#setPageSize} rather than this method. It always
	 *             does nothing since 3.0.7
	 */
	public void setPageSize(int size) throws WrongValueException;

	/**
	 * @deprecated As of release 3.0.7, the page size is controlled by
	 *             {@link Tree#getPageSize} rather than this method. It always
	 *             return 1 since 3.0.7.
	 */
	public int getPageCount();

	/**
	 * @deprecated As of release 3.0.7, the page size is controlled by
	 *             {@link Tree#getPageSize} rather than this method. It always
	 *             return 0 since 3.0.7.
	 */
	public int getActivePage();

	/**
	 * @deprecated As of release 3.0.7, the page size is controlled by
	 *             {@link Tree#setPageSize} rather than this method. It always
	 *             does nothing since 3.0.7
	 */
	public void setActivePage(int pg) throws WrongValueException;

	/**
	 * @deprecated As of release 3.0.7, the page size is controlled by
	 *             {@link Tree#setPageSize} rather than this method. It always
	 *             returns 0 since 3.0.7
	 */
	public int getVisibleBegin();

	/**
	 * @deprecated As of release 3.0.7, the page size is controlled by
	 *             {@link Tree#setPageSize} rather than this method. It always
	 *             returns Integer.MAX_VALUE since 3.0.7
	 */
	public int getVisibleEnd();

}
