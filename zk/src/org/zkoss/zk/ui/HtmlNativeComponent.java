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
import java.io.StringWriter;
import java.io.Writer;

import org.zkoss.lang.Strings;
import org.zkoss.xml.HTMLs;
import org.zkoss.xml.XMLs;
import org.zkoss.idom.Namespace;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.ext.DynamicTag;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.ext.render.DirectContent;
import org.zkoss.zk.ui.ext.render.PrologAllowed;
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
	/** The text before the tag name. */
	private String _prefix;
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

	public void redraw(Writer out) throws java.io.IOException {
		//Note: _tag == null can NOT be handled specially
		final Execution exec = Executions.getCurrent();
		final boolean root = getParent() == null && (getPage().isComplete()
		|| (exec != null && "complete".equals(ExecutionsCtrl.getPageRedrawControl(exec))));
		if (exec == null || exec.isAsyncUpdate(null)
		|| (!root && !HtmlPageRenders.isDirectContent(exec))) {
			super.redraw(out); //renderProperties (assume in zscript)
			return;
		}

		Writer oldout = null;
		String tn = null;
		if (exec != null && !HtmlPageRenders.isZkTagsGenerated(exec)
		&& exec.getAttribute(ATTR_TOP_NATIVE) == null) { //need to check topmost native only
			tn = _tag != null ? _tag.toLowerCase(): "";
			if ("html".equals(tn) || "body".equals(tn) || "head".equals(tn)) {
				exec.setAttribute(ATTR_TOP_NATIVE, Boolean.TRUE);
				oldout = out;
				out = new StringWriter();
			}
		}

		out.write(getPrologHalf());

		//children
		Component child = getFirstChild();
		if (child == null) {
			HtmlPageRenders.outStandalone(exec, null, out);
				//need to generate response JavaScript if any (Bug 3009925)
		} else {
			if (root)
				HtmlPageRenders.setDirectContent(exec, true);
			do {
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
			} while (child != null);
		}

		out.write(getEpilogHalf());

		if (oldout != null) {
			exec.removeAttribute(ATTR_TOP_NATIVE);

			//order: <html><head><zkhead><body>
			//1. replace <zkhead/> if found
			//2. insert before </head> if found
			//3. insert after <body> if found
			//4. insert after <html> if found
			//5. insert at the end if none of above found
			final StringBuffer sb = ((StringWriter)out).getBuffer();
			if (!HtmlPageRenders.isZkTagsGenerated(exec)) {
				int jhead = -1, //anchor of header
					junav = -1, //anchor of unavailable
					head = -1, //index of <head>
					heade = -1, //index of </head>
					html = -1; //index of <html>
				boolean unavailDone = false;
				for (int j = 0, len = sb.length(); (j = sb.indexOf("<", j)) >= 0;) {
					++j;
					if (jhead < 0 && startsWith(sb, "zkhead", j)) {
						int l = Strings.indexOf(sb, '>', j) + 1;
						sb.delete(jhead = --j, l); //jhead found
						len = sb.length();
					} else if (head < 0 && startsWith(sb, "head", j)) {
						head = Strings.indexOf(sb, '>', j) + 1;
					} else if (html < 0 && startsWith(sb, "html", j)) {
						html = Strings.indexOf(sb, '>', j) + 1;
					} else if (junav < 0 && startsWith(sb, "body", j)) {
						junav = Strings.indexOf(sb, '>', j) + 1; //junav found
						break; //done
					} else if (sb.charAt(j) == '/' && startsWith(sb, "head", ++j)) {
						heade = j - 2;
					}
				}

				if (jhead < 0 && ((jhead = heade) < 0) //use </head> if no <zkhead>
				&& ((jhead = head) < 0) //use <head> if no </head> (though unlikely)
				&& ((jhead = junav) < 0) //use <body> if no <head>
				&& ((jhead = html) < 0)) //use <html> if no <body>
					jhead = 0; //insert at head if not found

				final String msg = HtmlPageRenders.outUnavailable(exec);
				if (msg != null) {
					if (junav < 0) {
						if (html >= 0)
							junav = sb.lastIndexOf("</html");
					}
					if (junav >= 0)
						sb.insert(junav < jhead ? jhead: junav, msg);
					else
						sb.append(msg);
				}

				final String zktags = HtmlPageRenders.outHeaderZkTags(exec, getPage());
				if (zktags != null)
					sb.insert(jhead, zktags);
			}

			oldout.write(sb.toString());
		}
	}
	/** Used to indicate the redrawing of the top native is found. */
	private static final String ATTR_TOP_NATIVE = "zkHtmlTopNative";
	private static boolean startsWith(StringBuffer sb, String tag, int start) {
		int end = start + tag.length();
		char cc;
		return sb.length() > end && tag.equalsIgnoreCase(sb.substring(start, end))
			&& ((cc=sb.charAt(end)) < 'a' || cc > 'z') && (cc < 'A' || cc > 'Z');
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		final String prolog = getPrologHalf();
		render(renderer, "prolog", _prefix != null ? _prefix + prolog: prolog);
		render(renderer, "epilog", getEpilogHalf());
	}
	private String getPrologHalf() {
		final StringBuffer sb = new StringBuffer(128);
		final Helper helper = getHelper();
			//don't use _helper directly, since the derive might override it

		//first half
		helper.getFirstHalf(sb, _tag, _props, _dns);

		//prolog
		sb.append(_prolog); //no encoding
		return sb.toString();
	}
	private String getEpilogHalf() {
		final StringBuffer sb = new StringBuffer(128);
		final Helper helper = getHelper();

		//epilog
		sb.append(_epilog);

		//second half
		helper.getSecondHalf(sb, _tag);
		return sb.toString();
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
			}
		}
		public void getSecondHalf(StringBuffer sb, String tag) {
			if (tag != null) {
				final String tn = tag.toLowerCase();
				if ("zkhead".equals(tn) || HTMLs.isOrphanTag(tn))
					return;

				sb.append("</").append(tag).append('>');
			}
		}
		public void appendText(StringBuffer sb, String text) {
			sb.append(text); //don't encode (bug 2689443)
		}
	}

	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	protected class ExtraCtrl implements DirectContent, PrologAllowed {
		//-- PrologAware --//
		public void setPrologContent(String prolog) {
			_prefix = prolog;
				//Notice: it is used as prefix (shown before the tag and children)
				//while _prolog is the text shown after the tag and before the children
		}
	}
}
