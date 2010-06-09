/* DynamicTag.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct  3 22:03:28     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

import org.zkoss.zk.ui.WrongValueException;

/**
 * Represents a component is used to represent a broad range of tags.
 *
 * <p>For example, org.zkoss.zhtml.Raw is used to generate any HTML tags
 * that doesn't have the ZK counterpart. Rason: there are too many
 * HTML extended tags available and developers might choose to use them.
 *
 * <p>How to use:
 * <ol>
 * <li>First, extends a class from {@link org.zkoss.zk.ui.AbstractComponent}
 * and implements this interface</li>
 * <li>Declares the class in lang.xml by enclosing it with &lt;dynamic-tag&gt;.
 * Then, any tag that ZK doesn't understand, will use the class.</li>
 * </ol>
 *
 * @author tomyeh
 */
public interface DynamicTag extends DynamicPropertied {
	/** Sets the tag name.
	 */
	public void setTag(String tagname) throws WrongValueException;
	/** Returns whether the specified tag is allowed.
	 */
	public boolean hasTag(String tagname);
}
