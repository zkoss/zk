/* HtmlNativeComponent.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 19 18:05:01     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;

import org.zkoss.xml.HTMLs;
import org.zkoss.xml.XMLs;
import org.zkoss.idom.Namespace;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.ext.DynamicTag;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.ext.render.DirectContent;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.ui.impl.NativeHelpers;

/**
 * A comonent used to represent XML elements that are associated
 * with the inline namespace (http://www.zkoss.org/2005/zk/inline).
 *
 * <p>It contains the content that shall be sent directly to client.
 * It has three parts: prolog, children and epilog.
 * The prolog ({@link #getPrologContent}) and epilog ({@link #getEpilogContent})
 * are both {@link String}.
 *
 * <p>When this component is renderred ({@link #redraw}), it generates
 * the prolog first, the children and then the epilog.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class HtmlNativeComponent extends AbstractComponent
implements DynamicTag, Native {
	private static final Helper _helper = new HtmlHelper();
	private static final String ATTR_RENDER_CONTEXT = "org.zkoss.zk.native.renderContext";

	private String _tag;
	private String _prolog = "", _epilog = "";
	private Map _props;
	/** Declared namespaces ({@link Namespace}). */
	private List _dns;

	/** Contructs a {@link HtmlNativeComponent} component.
	 * 
	 */
	public HtmlNativeComponent() {
	}
	/** Constructs a {@link HtmlNativeComponent} component with the specified tag name.
	 *
	 * @param tag the tag name. If null or empty, plain text is assumed.
	 */
	public HtmlNativeComponent(String tag) {
		setTag(tag);
	}

	/** Contructs a {@link HtmlNativeComponent} component with the specified
	 * prolog ad epilog.
	 */
	public HtmlNativeComponent(String tag, String prolog, String epilog) {
		this(tag);

		_prolog = prolog != null ? prolog: "";
		_epilog = epilog != null ? epilog: "";
	}

	/** Returns the widget class, "zk.Native".
	 * @since 5.0.0
	 */
	public String getWidgetClass() {
		return "zk.Native";
	}
	/** Returns the tag name, or null if plain text.
	 */
	public String getTag() {
		return _tag;
	}

	//Native//
	public List getDeclaredNamespaces() {
		return _dns != null ? _dns: Collections.EMPTY_LIST;
	}
	public void addDeclaredNamespace(Namespace ns) {
		if (ns == null)
			throw new IllegalArgumentException();

		if (_dns == null)
			_dns = new LinkedList();
		_dns.add(ns);
	}
	public String getPrologContent() {
		return _prolog;
	}
	public void setPrologContent(String prolog) {
		_prolog = prolog != null ? prolog: "";
	}
	public String getEpilogContent() {
		return _epilog;
	}
	public void setEpilogContent(String epilog) {
		_epilog = epilog != null ? epilog: "";
	}

	public Helper getHelper() {
		return _helper;
	}

	//-- Component --//
	public void setId(String id) {
		super.setId(id);
		setDynamicProperty("id", id);
	}
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("Use client-dependent attribute, such as display:none");
	}

	public void redraw(java.io.Writer out) throws java.io.IOException {
		//Note: _tag == null can NOT be handled specially
		final Execution exec = Executions.getCurrent();
		final boolean root = getParent() == null && (getPage().isComplete()
		|| (exec != null && "complete".equals(exec.getAttribute(Attributes.PAGE_REDRAW_CONTROL))));
		if (exec == null || exec.isAsyncUpdate(null)
		|| (!root && !HtmlPageRenders.isDirectContent(exec))) {
			super.redraw(out); //renderProperties (assume in zscript)
			return;
		}

		final NativeRenderContext rc = getNativeRenderContext(exec);
		out.write(getPrologHalf(rc));

		//children
		if (root) HtmlPageRenders.setDirectContent(exec, true);
		for (Component child = getFirstChild(); child != null;) {
			Component next = child.getNextSibling();
			if (child instanceof Native
			|| ((ComponentCtrl)child).getExtraCtrl() instanceof DirectContent) {
				((ComponentCtrl)child).redraw(out);
			} else {
				HtmlPageRenders.setDirectContent(exec, false);
				HtmlPageRenders.outStandalone(exec, child, out);
				HtmlPageRenders.setDirectContent(exec, true);
			}
			child = next;
		}

		out.write(getEpilogHalf(rc));
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		final NativeRenderContext rc = getNativeRenderContext(Executions.getCurrent());
		render(renderer, "prolog", getPrologHalf(rc));
		render(renderer, "epilog", getEpilogHalf(rc));
	}
	private NativeRenderContext getNativeRenderContext(Execution exec) {
		if (exec == null)
			return new NativeRenderContext();
		NativeRenderContext rc = (NativeRenderContext)exec.getAttribute(ATTR_RENDER_CONTEXT);
		if (rc == null)
			exec.setAttribute(ATTR_RENDER_CONTEXT, rc = new NativeRenderContext());
		return rc;
	}
	private String getPrologHalf(NativeRenderContext rc) {
		final StringBuffer sb = new StringBuffer(128);
		final Helper helper = getHelper();
			//don't use _helper directly, since the derive might override it

		//first half
		helper.getFirstHalf(sb, _tag, _props, _dns);

		//prolog
		sb.append(_prolog); //no encoding

		final Execution exec = Executions.getCurrent();
		if (exec != null) {
			replaceZkhead(exec, sb, rc);

			final String tn = _tag != null ? _tag.toLowerCase(): "";
			if (!rc.zktagGened
			&& ("html".equals(tn) || "body".equals(tn) || "head".equals(tn))) { //<head> might be part of _prolog
				final int j = indexOfHead(sb);
				if (j >= 0) {
					rc.zktagGened = true;
					final String zktags =
						HtmlPageRenders.outHeaderZkTags(exec, getPage());
					if (zktags != null)
						sb.insert(j, zktags);
				}
			}
		}

		return sb.toString();
	}
	private String getEpilogHalf(NativeRenderContext rc) {
		final StringBuffer sb = new StringBuffer(128);
		final Helper helper = getHelper();

		//epilog
		sb.append(_epilog);

		//second half
		helper.getSecondHalf(sb, _tag);

		final Execution exec = Executions.getCurrent();
		if (exec != null) {
			replaceZkhead(exec, sb, rc);

			final String tn = _tag != null ? _tag.toLowerCase(): "";
			if ("html".equals(tn) || "body".equals(tn)) {
				final int j = sb.lastIndexOf("</" + _tag);
				if (j >= 0) {
					if (!rc.zktagGened) {
						final String zktags =
							HtmlPageRenders.outHeaderZkTags(exec, getPage());
						if (zktags != null)
							sb.insert(j, zktags);
					}

					final String msg = HtmlPageRenders.outUnavailable(exec);
					if (msg != null) sb.insert(j, msg);
				}
			}
		}
		return sb.toString();
	}
	private void
	replaceZkhead(Execution exec, StringBuffer sb, NativeRenderContext rc) {
		if (!rc.zkheadFound) {
			final int j = sb.indexOf("<zkhead/>");
			if (j >= 0) {
				rc.zkheadFound = true;
					//Note: we allow only one zkhead (for better performance)

				if (!rc.zktagGened) {
					rc.zktagGened = true;
					final String zktags = HtmlPageRenders.outHeaderZkTags(exec, getPage());
					if (zktags != null) {
						sb.replace(j, j + 9, zktags);
						return;
					}
				}
				sb.delete(j, j + 9);
			}
		}
		return;
	}
	/** Search <head> case-insensitive. */
	private static int indexOfHead(StringBuffer sb) {
		for (int j = 0, len = sb.length(); (j = sb.indexOf("<", j)) >= 0;) {
			if (j + 6 > len) break; //not found

			char cc = sb.charAt(++j);
			if (cc != 'h' && cc != 'H') continue;
			cc = sb.charAt(++j);
			if (cc != 'e' && cc != 'E') continue;
			cc = sb.charAt(++j);
			if (cc != 'a' && cc != 'A') continue;
			cc = sb.charAt(++j);
			if (cc != 'd' && cc != 'D') continue;
			j = sb.indexOf(">", j + 1);
			return j >= 0 ? j + 1: -1;
		}
		return -1;
	}

	//DynamicTag//
	/** Sets the tag name.
	 *
	 * @param tag the tag name. If null or empty, plain text is assumed.
	 */
	public void setTag(String tag) throws WrongValueException {
		_tag = tag != null && tag.length() > 0 ? tag: null;
	}
	public boolean hasTag(String tag) {
		return true; //accept anything
	}
	public boolean hasDynamicProperty(String name) {
		return ComponentsCtrl.isReservedAttribute(name);
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

	/** The HTML helper.
	 */
	public static class HtmlHelper implements Helper {
		public Component newNative(String text) {
			final HtmlNativeComponent nc = new HtmlNativeComponent();
			if (text != null)
				nc.setPrologContent(text);
			return nc;
		}
		public void getFirstHalf(StringBuffer sb, String tag,
		Map props, Collection namespaces) {
			if (tag != null)
				sb.append('<').append(tag);

			NativeHelpers.getAttributes(sb, props, namespaces);

			if (tag != null) {
				final String tn = tag.toLowerCase();
				if ("zkhead".equals(tn) || HTMLs.isOrphanTag(tn))
					sb.append('/');
				sb.append('>');

				if (!_noLFs.contains(tn) && !_begNoLFs.contains(tn))
					sb.append('\n'); //make it more readable
			}
		}
		public void getSecondHalf(StringBuffer sb, String tag) {
			if (tag != null) {
				final String tn = tag.toLowerCase();
				if ("zkhead".equals(tn) || HTMLs.isOrphanTag(tn))
					return;

				sb.append("</").append(tag).append('>');

				if (!_noLFs.contains(tn))
					sb.append('\n'); //make it more readable
			}
		}
		public void appendText(StringBuffer sb, String text) {
			sb.append(text); //don't encode (bug 2689443)
		}
	}
	/** A set of tags that we shall append linefeed to it.
	 */
	private static final Set _noLFs, _begNoLFs;
	static {
		final String[] noLFs = {
			"a", "abbr", "acronym", "address",
			"b", "basefont", "bdo", "big", "blink",
			"cite", "code",
			"del", "dfn", "dir",
			"em",
			"font",
			"i", "img", "input", "ins", "kbd", "q",
			"s", "samp", "small", "strike", "strong", "style", "sub", "sup",
			"u"
		};
		_noLFs = new HashSet((noLFs.length << 2) / 5);
		for (int j = noLFs.length; --j >= 0;)
			_noLFs.add(noLFs[j]);

		final String[] begNoLFs = {
			"caption", "dd", "div", "dt", "legend", "li",
			"p", "pre",
			"span", "td", "tfoot", "th", "title"
			
		};
		_begNoLFs = new HashSet((begNoLFs.length << 2) / 5);
		for (int j = begNoLFs.length; --j >= 0;)
			_begNoLFs.add(begNoLFs[j]);
	}
	private static class NativeRenderContext {
		boolean zktagGened;
		boolean zkheadFound;
	};

	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	protected class ExtraCtrl implements DirectContent {
	}
}
