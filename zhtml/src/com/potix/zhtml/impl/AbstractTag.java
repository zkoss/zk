/* AbstractTag.java

{{IS_NOTE
	$Id: AbstractTag.java,v 1.13 2006/05/26 06:41:12 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Oct  4 09:15:59     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zhtml.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.Writer;
import java.io.IOException;

import com.potix.lang.Objects;
import com.potix.xml.XMLs;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Components;
import com.potix.zk.ui.AbstractComponent;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.au.AuInit;
import com.potix.zk.ui.event.Events;
import com.potix.zk.ui.event.EventListener;
import com.potix.zk.ui.ext.DynamicPropertied;
import com.potix.zk.ui.ext.RawId;

/**
 * The raw component used to generate raw HTML elements.
 *
 * <p>Note: ZHTML components ignore the page listener since it handles
 * only ASAP event listeners.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.13 $ $Date: 2006/05/26 06:41:12 $
 */
public class AbstractTag extends AbstractComponent
implements DynamicPropertied, RawId {
	/** The tag name. */
	protected String _tagnm;
	private Map _attrs;

	protected AbstractTag(String tagname) {
		if (tagname == null || tagname.length() == 0)
			throw new IllegalArgumentException("A tag name is required");
		_tagnm = tagname;
	}
	protected AbstractTag() {
	}

	/** Returns the CSS class.
	 * Due to Java's limitation, we cannot use the name called getClas.
	 * <p>Default: null (the default value depends on element).
	 */
	public String getSclass() {
		return (String)getDynamicProperty("class");
	}
	/** Sets the CSS class.
	 */
	public void setSclass(String sclass) {
		setDynamicProperty("class", sclass);
	}
	/** Returns the CSS style.
	 * <p>Default: null.
	 */
	public String getStyle() {
		return (String)getDynamicProperty("style");
	}
	/** Sets the CSS style.
	 */
	public void setStyle(String style) {
		setDynamicProperty("style", style);
	}

	/** Returns the tag name.
	 */
	public String getTag() {
		return _tagnm;
	}

	//-- DynamicPropertys --//
	public boolean hasDynamicProperty(String name) {
		return name != null && !"use".equals(name) && !"if".equals(name)
			&& !"unless".equals(name);
	}
	/** Returns the dynamic property, or null if not found.
	 * Note: it must be a String object or null.
	 */
	public Object getDynamicProperty(String name) {
		return _attrs != null ? _attrs.get(name): null;
	}
	/** Sets the dynamic property.
	 * Note: it converts the value to a string object (by use of
	 * {@link Objects#toString}).
	 */
	public void setDynamicProperty(String name, Object value)
	throws WrongValueException {
		if (name == null)
			throw new WrongValueException("name is required");
		if (!hasDynamicProperty(name))
			throw new WrongValueException("Attribute not allowed: "+name+"\nSpecify the ZK namespace if you want to use special ZK attributes");

		final String sval = Objects.toString(value);
		if (sval == null) {
			if (_attrs != null) _attrs.remove(name);
		} else {
			if (_attrs == null)
				_attrs = new HashMap();
			_attrs.put(name, sval);
		}
		smartUpdate(name, sval);
	}

	/** Whether to hide the id attribute.
	 * <p>Default: false.
	 * <p>Some tags, such as {@link com.potix.zhtml.Html}, won't generate the id attribute.
	 * They will override this method to return true.
	 */
	protected boolean shallHideId() {
		return false;
	}

	//-- Component --//
	public boolean addEventListener(String evtnm, EventListener listener) {
		if (!listener.isAsap())
			throw new UiException("ZHTML accepts only ASAP listener");

		//Bug 1477271: Tom M Yeh: 
		//We can check ASAP only. Otherwise, if users add a page listener,
		//all ZHTML will generate zk_onChange. Too complicate!

		for (int j = 0;; ++j)
			if (j >= _evts.length)
				throw new UiException("Not supported event: "+evtnm);
			else if (_evts[j].equals(evtnm))
				break;

		boolean evtDeclared = isEventDeclared();
		final boolean ret = super.addEventListener(evtnm, listener);
		if (ret) {
			smartUpdate("zk_" + evtnm,
				Events.isListenerAvailable(this, evtnm, true) ? "true": null); //asap only
			if (!evtDeclared && isEventDeclared()) {
				smartUpdate("zk_type", "zhtml.main.Raw");
				response("init", new AuInit(this));
			}
		}
		return ret;
	}
	private boolean isEventDeclared() {
		for (int j = 0; j < _evts.length; ++j)
			if (Events.isListenerAvailable(this, _evts[j], true)) //asap only
				return true;
		return false;
	}

	public void redraw(Writer out) throws IOException {
		if (_tagnm == null)
			throw new UiException("The tag name is not initialized yet");

		out.write('<');
		out.write(_tagnm);

		boolean evtDeclared = false;
		for (int j = 0; j < _evts.length; ++j) {
			if (Events.isListenerAvailable(this, _evts[j], true)) { //asap only
				evtDeclared = true;
				out.write(" zk_");
				out.write(_evts[j]);
				out.write("=\"true\"");
			}
		}

		if (evtDeclared)
			out.write(" zk_type=\"zhtml.main.Raw\"");

		if (evtDeclared || !shallHideId() || !Components.isAutoId(getUuid())) {
			out.write(" id=\"");
			out.write(getUuid());
			out.write('"');
		}

		if (_attrs != null) {
			for (Iterator it = _attrs.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String key = (String)me.getKey();
				final String val = (String)me.getValue();
				out.write(' ');
				out.write(key);
				out.write("=\"");
				out.write(XMLs.encodeAttribute(val));
				out.write('"');
			}
		}

		if (isChildable()) {
			out.write('>');

			for (Iterator it = getChildren().iterator(); it.hasNext();)
				((Component)it.next()).redraw(out);

			out.write("</");
			out.write(_tagnm);
			out.write('>');
		} else {
			out.write("/>");
		}
	}
	public boolean isChildable() {
		return !_childless.contains(_tagnm);
	}

	public String toString() {
		return "["+_tagnm+' '+getId()+']';
	}

	/** A set of tags that don't have child. */
	private static final Set _childless = new HashSet(29);
	static {
		final String[] childless = {
			"area", "base", "basefont", "bgsound", "br",
			"col", "colgroup", "embed", "hr", "img", "input",
			"isindex", "keygen", "link", "meta", "object", "plaintext",
			"spacer", "wbr"
		};
		for (int j = childless.length; --j >= 0;)
			_childless.add(childless[j]);
	}
	/** Events that are supported. */
	private static final String _evts[] = {"onClick", "onChange"};
}
