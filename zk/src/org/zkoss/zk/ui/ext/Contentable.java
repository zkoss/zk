/* Contentable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 13 10:02:51     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

/**
 * Represents a component that allows developers to specify the content
 * directly within the element in a ZUML page.
 *
 * <p>For example, let us assume <code>ab</code> is a component that
 * implements {@link Contentable}, then you can specify its content
 * with the following statement.
 *
 * <pre><code>&lt;ab&gt;
 * Here is ab's content.
 * It is not a label child.
 *&lt;/ab&gt;</code></pre>
 *
 * <p>In other words, it is the same as
 *
 * <pre><code>&lt;ab&gt;
 * &lt;attribute name="content"&gt;
 * Here is ab's content.
 * It is not a label child.
 * &lt;/attribute&gt;
 *&lt;/ab&gt;</code></pre>
 *
 * <p>Note: By default, the text within an element will be created as a Label
 * component and will become a child of the component instantiated by
 * the element.
 *
 * <p>For example, since window does <i>not</i> implements {@link Contentable},
 * so
 * <pre><code>&lt;window&gt;
 * This is a label child.
 *&lt;/window&gt;</code></pre>
 *
 * is equivalent to
 *
 * <pre><code>&lt;window&gt;
 * &lt;label value="This is a label child."/&gt;
 *&lt;/window&gt;</code></pre>
 *
 * @author tomyeh
 * @since 2.5.0
 */
public interface Contentable {
	/** Sets the content of the component.
	 * @since 2.5.0
	 */
	public void setContent(String content);
}
