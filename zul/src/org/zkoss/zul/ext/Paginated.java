/* Paginated.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 24 09:45:47     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.ext;

/**
 * Indicates a component that can be controller by an external paging
 * controller (i.e., an instance of {@link Paginal}).
 *
 * <ul>
 * <li>{@link Paginated}: a multi-page component whose pagination
 * is controlled by an external page controlle ({@link Paginal}).</li>
 * <li>{@link Paginal}: the paging controller used to control
 * the pagination of {@link Paginated}.</li>
 * <li>{@link Pageable}: a multi-page component that handles pagination
 * by itself.</li>
 * </ul>
 *
 * @author tomyeh
 * @since 3.0.7
 * @see Paginal
 */
public interface Paginated {
	/** Returns the paging position if the component contains a paging
	 * component.
	 * <p>Possible values: top, bottom and both.
	 * @since 3.0.7
	 */
	public String getPagingPosition();
}
