/* Native.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 16 11:35:01     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

/**
 * Implemented with {@link org.zkoss.zk.ui.Component} to represent
 * a native component.
 * The native component is used to implement the feature of the
 * Native namespace (http://www.zkoss.org/2005/zk/native).
 * 
 * @author tomyeh
 * @since 2.5.0
 */
public interface Native extends NonFellow {
	/** Returns the prolog content. It is the content generated
	 * before the child components, if any.
	 * <p>Default: empty ("").
	 */
	public String getProlog();
	/** Sets the prolog content. It is the content generated
	 * before the child components, if any.
	 */
	public void setProlog(String prolog);
	/** Returns the epilog content. It is the content generated
	 * before the child components, if any.
	 * <p>Default: empty ("").
	 */
	public String getEpilog();
	/** Sets the epilog content. It is the content generated
	 * before the child components, if any.
	 */
	public void setEpilog(String epilog);

	/** Returns the first half of the output.
	 * For example, getFirstHalf("tr") returns "&lt;tr&gt;", and
	 * getFirstHalf("br") returns "&lt;br/&gt;".
	 *
	 * <p>The first half also includes the prolog ({@link #getProlog}).
	 * Notice that it doesn't include the output of all child components.
	 */
	public String getFirstHalf();
	/** Returns the second half of the output.
	 * For example, getSecondHalf("tr") returns "&lt;/tr&gt;", and
	 * getSecondHalf("br") returns "".
	 *
	 * <p>The first half also includes the epilog ({@link #getEpilog}).
	 */
	public String getSecondHalf();
}
