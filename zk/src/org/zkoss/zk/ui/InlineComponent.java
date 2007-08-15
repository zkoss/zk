/* InlineComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 19 18:05:01     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.ext.DynamicTag;
import org.zkoss.zk.device.Device;

/**
 * A comonent used to represent the inline namespace
 * (http://www.zkoss.org/2005/zk/inline).
 * It contains the content that shall be sent directly to client.
 * It has three parts: prolog, children and epilog.
 * The prolog ({@link #getProlog}) and epilog ({@link #getEpilog})
 * are both {@link String}.
 *
 * <p>When this component is renderred ({@link #redraw}), it generates
 * the prolog first, the children and then the epilog.
 *
 * @author tomyeh
 * @since 2.5.0
 */
public class InlineComponent extends AbstractComponent implements DynamicTag {
	private String _tagnm;
	private String _prolog = "", _epilog = "";
	private Map _props;

	/** Contructs a {@link InlineComponent} component.
	 * 
	 */
	public InlineComponent() {
	}
	/** Constructs a {@link InlineComponent} component with the specified tag name.
	 *
	 * @param tagname the tag name. If null or empty, plain text is assumed.
	 */
	public InlineComponent(String tagname) {
		setTag(tagname);
	}

	/** Contructs a {@link InlineComponent} component with the specified
	 * prolog ad epilog.
	 */
	public InlineComponent(String tagname, String prolog, String epilog) {
		this(tagname);

		_prolog = prolog != null ? prolog: "";
		_epilog = epilog != null ? epilog: "";
	}

	/** Returns the prolog content. It is the content generated
	 * before the child components, if any.
	 * <p>Default: empty ("").
	 */
	public String getProlog() {
		return _prolog;
	}
	/** Sets the prolog content. It is the content generated
	 * before the child components, if any.
	 */
	public void setProlog(String prolog) {
		_prolog = prolog != null ? prolog: "";
	}
	/** Returns the epilog content. It is the content generated
	 * before the child components, if any.
	 * <p>Default: empty ("").
	 */
	public String getEpilog() {
		return _epilog;
	}
	/** Sets the epilog content. It is the content generated
	 * before the child components, if any.
	 */
	public void setEpilog(String epilog) {
		_epilog = epilog != null ? epilog: "";
	}

	/** Returns the tag name, or null if plain text.
	 */
	public String getTag() {
		return _tagnm;
	}

	//-- Component --//
	public void redraw(Writer out) throws IOException {
		final Device device = Executions.getCurrent().getDesktop().getDevice();
		out.write(device.getRawTagBegin(_tagnm, _props));

		out.write(_prolog); //no encoding

		for (Iterator it = getChildren().iterator(); it.hasNext();)
			((Component)it.next()).redraw(out);

		out.write(_epilog); //no encodding

		out.write(device.getRawTagEnd(_tagnm));
	}

	//DynamicTag//
	/** Sets the tag name.
	 *
	 * @param tagname the tag name. If null or empty, plain text is assumed.
	 */
	public void setTag(String tagname) throws WrongValueException {
		_tagnm = tagname != null && tagname.length() > 0 ? tagname: null;
	}
	public boolean hasTag(String tagname) {
		return true; //accept anything
	}
	public boolean hasDynamicProperty(String name) {
		return name != null && !"use".equals(name) && !"if".equals(name)
			&& !"unless".equals(name) && !"forEach".equals(name);
	}
	public Object getDynamicProperty(String name) {
		return _props != null ? _props.get(name): null;
	}
	public void setDynamicProperty(String name, Object value)
	throws WrongValueException {
		if (name == null)
			throw new WrongValueException("name required");

		if (value == null) {
			if (_props != null) _props.remove(name);
		} else {
			if (_props == null)
				_props = new LinkedHashMap();
			_props.put(name, value);
		}
	}
}
