/* ZscriptTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 27 17:09:09     2007, Created by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsp.zul;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;

import org.zkoss.jsp.zul.impl.AbstractTag;
import org.zkoss.jsp.zul.impl.BranchTag;
import org.zkoss.jsp.zul.impl.RootTag;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.ZScript;

/**
 * A Jsp Tag class to handle the zscript element.
 * This tag should be declared under {@link PageTag} or any Component tags.
 * 
 * @author Ian Tsai 
 */
public class ZScriptTag extends AbstractTag {
	private static final Log log = Log.lookup(ZScriptTag.class);

	private String _lang = null;
	private Component _parent;
	private RootTag _roottag;
	private boolean _deferred;

	/**
	 * 
	 * add body content to parent's zscript info.
	 */
	public void doTag() throws JspException, IOException {
		if(!super.isEffective())return;
		
		StringWriter out = new StringWriter();
		getJspBody().invoke(out);
		final ZScript zscript = ZScript.parseContent(out.toString());
		if (zscript.getLanguage() == null)
			zscript.setLanguage(_lang != null ? _lang: _roottag.getZScriptLanguage());
		_roottag.processZScript(_parent, zscript);
	}

	
	//SimpleTagSupport//
	/** Sets the parent tag.
	 * Deriving class rarely need to invoke this method.
	 */
	public void setParent(JspTag parent) {
		super.setParent(parent);
		final AbstractTag pt =
		(AbstractTag)findAncestorWithClass(this, AbstractTag.class);
		if (pt instanceof RootTag) { //root component tag
			_roottag = (RootTag)pt;
		} else if (pt instanceof BranchTag) {
			BranchTag ptag = (BranchTag)pt;
			_roottag = ptag.getRootTag();
			_parent = ptag.getComponent();
		} else {
			throw new IllegalStateException("Must be nested inside the page tag: "+this);
		}
	}
	/**
	 * Returns whether to defer the execution of this zscript.
	 * <p>Default: false.
	 */
	public boolean isDeferred() {
		return _deferred;
	}
	/**
	 * Sets whether to defer the execution of this zscript.
	 * @param deferred whether to defer the execution.
	 */
	public void setDeferred(boolean deferred) {
		this._deferred  = deferred;
	}

	/** Returns the name of the scripting language in this ZScript tag.
	 *
	 * <p>Default: null (use what is defined in {@link PageTag#getZScriptLanguage}).
	 * @return the name of the scripting language in this ZScript tag. 
	 */
	public String getLanguage() {
		return _lang;
	}
	/**
	 * Sets the name of the scripting language in this ZScript tag.
	 *
	 * <p>Default: Java.
	 *
	 * @param lang the name of the scripting language, such as
	 * Java, Ruby and Groovy.
	 */
	public void setLanguage(String lang) {
		this._lang = lang;
	}

}
