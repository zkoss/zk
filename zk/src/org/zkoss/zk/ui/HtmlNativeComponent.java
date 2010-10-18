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
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.ext.DynamicTag;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.ext.render.DirectContent;
import org.zkoss.zk.ui.ext.render.PrologAllowed;
import org.zkoss.zk.ui.ext.render.Merger;
import org.zkoss.zk.ui.impl.NativeHelpers;

/**
 * A component used to represent XML elements that are associated
 * with the native namespace (http://www.zkoss.org/2005/zk/native).
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

	//---------
	//structure: _prefix <_tag> _prolog children _epilog </_tag> _postifx
	//---------

	private String _tag;
	private String _prolog = "", _epilog = "";
	/** The text before the tag name. */
	private String _prefix, _postfix;
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
	 * prolog and epilog.
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

	/** Returns whether this component is stub-only.
	 * By stub-only, we mean we don't need to maintain the states of
	 * the component at the server side.
	 * <p>Unlike other kind of components, this method always return "true",
	 * i.e., stub-only. In additions, its child component does not inherit
	 * this value even if the child's {@link Component#getStubonly} is "inherit"
	 * (and assumed to "false" if "inherit").
	 * @since 5.0.4
	 */
	public String getStubonly() {
		return "true";
	}
	/** Sets whether this component is stub-only.
	 * By stub-only, we mean we don't need to maintain the states of
	 * the component at the server side.
	 * <p>Default: "true".
	 * @exception UiException if stubonly is not "true".
	 * @since 5.0.4
	 */
	public void setStubonly(String stubonly) {
		if (!"true".equals(stubonly))
			throw new UiException("Not allowed: "+stubonly);
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
		if (exec != null && !HtmlPageRenders.isZkTagsGenerated(exec)
		&& exec.getAttribute(ATTR_TOP_NATIVE) == null) { //need to check topmost native only
			String tn;
			if (root || "html".equals(tn = _tag != null ? _tag.toLowerCase(): "")
			|| "body".equals(tn) || "head".equals(tn)) {
				exec.setAttribute(ATTR_TOP_NATIVE, Boolean.TRUE);
				oldout = out;
				out = new StringWriter();
			}
		}

		out.write(getPrologHalf());

		//children
		Component child = getFirstChild();
		if (child == null) {
			//need to invoke outStandalone to generate response if any (Bug 3009925)
			//however, it is not required if not root (since others will invoke)
			if (root)
				HtmlPageRenders.outStandalone(exec, null, out);
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

				boolean disableUnavailable = false;
				if (jhead < 0 && ((jhead = heade) < 0) //use </head> if no <zkhead>
				&& ((jhead = head) < 0) //use <head> if no </head> (though unlikely)
				&& ((jhead = junav) < 0) //use <body> if no <head>
				&& ((jhead = html) < 0)) { //use <html> if no <body>
					if (_tag != null) {
						final String tn = _tag.toLowerCase();
						if ("div".equals(tn) || "span".equals(tn)) {
							l_loop:
							for (int j = 0, len = sb.length(); j < len; ++j)
								switch (sb.charAt(j)) {
								case '>':
									disableUnavailable = true; //make output cleaner
									jhead = j + 1; //found
								case '=':  //it might have something depends on JS
								case '"':
									break l_loop;
								}
						}
					}
					if (jhead < 0)
						jhead = 0; //insert at head if not found
				}

				final String msg = HtmlPageRenders.outUnavailable(exec);
					//called if disableUnavailable (so it won't be generated later)
				if (msg != null && !disableUnavailable) {
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

		render(renderer, "prolog", getPrologHalf());
		render(renderer, "epilog", getEpilogHalf());
	}
	private String getPrologHalf() {
		final StringBuffer sb = new StringBuffer(128);
		final Helper helper = getHelper();
			//don't use _helper directly, since the derive might override it

		if (_prefix != null)
			sb.append(_prefix);

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

		if (_postfix != null)
			sb.append(_postfix);
		return sb.toString();
	}
	/** Return the full content, getPrologHalf() + getFullLast().
	 * It assumes it has no children.
	 */
	private final String getFullContent() {
		return getPrologHalf() + getEpilogHalf();
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
	private final Component mergeNext() {
		final AbstractComponent parent = (AbstractComponent)getParent();
		if (parent != null) {
			final AbstractComponent n = (AbstractComponent)getNextSibling();
			if (n instanceof HtmlNativeComponent) {
				final DesktopCtrl desktopCtrl = _page != null ?
					(DesktopCtrl)_page.getDesktop(): null;
			 	if (n.getFirstChild() == null) {
			 		//remove n
			 		final AbstractComponent n2 = (AbstractComponent)n.getNextSibling();
			 		parent.setNext(this, n2);
			 		parent.setPrev(n2, this);
			 		parent.incNChild(-1);

					final String s = ((HtmlNativeComponent)n).getFullContent();
			 		_postfix = _postfix != null ? _postfix + s: s;
					if (desktopCtrl != null)
						desktopCtrl.removeComponent(n, false); //ok but no need to recycle
					return this;
			 	}
			 	if (getFirstChild() == null) {
			 		//remove this
			 		final AbstractComponent p = (AbstractComponent)getPreviousSibling();
			 		parent.setNext(p, n);
			 		parent.setPrev(n, p);
			 		parent.incNChild(-1);

			 		final String s = getFullContent(),
			 			prefix = ((HtmlNativeComponent)n)._prefix;
			 		((HtmlNativeComponent)n)._prefix = prefix != null ? s + prefix: s;
					if (desktopCtrl != null)
						desktopCtrl.removeComponent(this, false); //ok but no need to recycle
					return n;
			 	}
			}
		}
		return null;
	}
	private final Component mergeChild() {
		Component child = getFirstChild();
		if (child != null) {
			HtmlNativeComponent childWithChild = null;
			for (; child != null; child = child.getNextSibling()) {
				if (!(child instanceof HtmlNativeComponent))
					return null;
				final Component cc = child.getFirstChild();
				if (cc != null) {
					if (childWithChild != null)
						return null; //only one child-with-child is allowed
					childWithChild = (HtmlNativeComponent)child;
				}
			}

			final DesktopCtrl desktopCtrl = _page != null ?
				(DesktopCtrl)_page.getDesktop(): null;
			boolean bEpilog = false;
			final StringBuffer prolog = new StringBuffer(_prolog),
				epilog = new StringBuffer();
			for (child = getFirstChild(); child != null;
			child = child.getNextSibling()) {
				final HtmlNativeComponent nc = (HtmlNativeComponent)child;
				if (bEpilog) { //after childWithChild
					epilog.append(nc.getFullContent());
				} else if (child != childWithChild) { //in front of childWithChild
					prolog.append(nc.getFullContent());
				} else { //childWithChild
					prolog.append(nc.getPrologHalf());
					epilog.append(nc.getEpilogHalf());
					bEpilog = true;
				}
				if (desktopCtrl != null)
					desktopCtrl.removeComponent(nc, false); //ok but no need to recycle
			}

			_prolog = prolog.toString();
			_epilog = epilog.append(_epilog).toString();

			if (childWithChild == null) {
				nChild(null, null, 0);
			} else {
				nChild(
					(AbstractComponent)(child = childWithChild.getFirstChild()),
					(AbstractComponent)childWithChild.getLastChild(),
					childWithChild.nChild());
			}
			return this;
		}
		return null;
	}

	//@Override
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}
	protected class ExtraCtrl implements DirectContent, PrologAllowed, Merger {
		//-- PrologAware --//
		public void setPrologContent(String prolog) {
			_prefix = prolog;
				//Notice: it is used as prefix (shown before the tag and children)
				//while _prolog is the text shown after the tag and before the children
		}
		//Merger//
		public Component mergeNextSibling() {
			return mergeNext();
		}
		public Component mergeChildren() {
			return mergeChild();
		}
	}
}
