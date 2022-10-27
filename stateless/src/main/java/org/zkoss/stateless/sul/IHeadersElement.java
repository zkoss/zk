/* IHeadersElement.java

	Purpose:

	Description:

	History:
		12:33 PM 2021/10/26, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.stateless.action.data.ColSizeData;

/**
 * A skeletal implementation for headers, the parent of
 * a group of {@link IHeaderElement}.
 *
 * <h3>Support {@literal @}Action</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Action Type</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>onColSize</td>
 *          <td><strong>ActionData</strong>: {@link ColSizeData}
 *          <br>Denotes user has resized one of the columns.</td>
 *       </tr>
 *    </tbody>
 * </table>
 * @author jumperchen
 */
public interface IHeadersElement<I extends IHeadersElement> extends IXulElement<I> {

	/** Returns whether the width of the child column is sizable.
	 */
	default boolean isSizable() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code sizable}.
	 *
	 * <p> Sets whether the width of the child column is sizable.
	 * If true, a user can drag the border between two columns (e.g., {@link IColumn})
	 * to change the widths of adjacent columns.
	 * @param sizable Whether the width of the child column is sizable.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withSizable(boolean sizable);

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		IXulElement.super.renderProperties(renderer);

		render(renderer, "sizable", isSizable());
	}
}
