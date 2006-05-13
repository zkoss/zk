/* Comment.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/m3/pxcommon/src/com/potix/idom/Comment.java,v 1.4 2006/05/11 07:16:22 tomyeh Exp $
	Purpose: 
	Description: 
	History:
	2001/10/22 20:55:39, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.idom;

import com.potix.idom.impl.*;

/**
 * The iDOM Comment.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/11 07:16:22 $
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
