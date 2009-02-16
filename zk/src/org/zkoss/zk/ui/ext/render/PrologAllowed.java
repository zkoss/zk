/* PrologAllowed.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 12 15:28:41     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.render;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * if it is able to generate the specified prolog before its real content.
 *
 * <p>It is used by ZK to minimize the output. Currently,
 * {@link org.zkoss.zk.ui.HtmlBasedComponent} supports it.
 * If a ZUML page has a string consisting of whitespaces, it will
 * ask the following component to generate them if {@link PrologAllowed}
 * is implemented.
 *
 * <p>For example, in the following codes, the whitespace between textbox
 * and datebox won't be converted to a label component.
 * Rather, it will ask datebox to generate by calling datebox's
 * {@link #setPrologContent}.
 *
 * <pre><code>&lt;textbox/&gt; &lt;datebox/&gt;</code></pre>
 *
 * @author tomyeh
 * @since 3.5.0
 */
public interface PrologAllowed {
	/** Sets the prolog content. It is the content generated
	 * before the child components, if any.
	 * <p>Default: none.
	 *
	 * <p>Note: the prolog is one-shot only. Once the component is
	 * generated, the prolog is gone.
	 */
	public void setPrologContent(String prolog);
}
