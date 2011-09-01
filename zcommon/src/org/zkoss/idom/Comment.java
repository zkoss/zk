/* Comment.java


	Purpose: 
	Description: 
	History:
	2001/10/22 20:55:39, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import org.zkoss.idom.impl.*;

/**
 * The iDOM Comment.
 *
 * @author tomyeh
 */
public class Comment extends AbstractTextual implements org.w3c.dom.Comment {
	/** Constructor.
	 */
	public Comment(String text) {
		super(text);
	}
	/** Constructor.
	 */
	public Comment() {
	}

	//-- AbstractTextual --//
	/**
	 * Returns false to denote it is not part of parent's text,
	 */
	public final boolean isPartOfParentText() {
		return false;
	}
	protected void checkText(String text) {
		Verifier.checkCommentData(text, getLocator());
	}

	//-- Item --//
	public final String getName() {
		return "#comment";
	}

	//-- Node --//
	public final short getNodeType() {
		return COMMENT_NODE;
	}
}
