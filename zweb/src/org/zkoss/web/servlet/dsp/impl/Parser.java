/* Parser.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 12:12:41     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.util.resource.Locator;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.XelException;
import org.zkoss.xel.util.SimpleMapper;
import org.zkoss.xel.taglib.Taglibs;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.Element;
import org.zkoss.idom.util.IDOMs;

import org.zkoss.web.mesg.MWeb;
import org.zkoss.web.servlet.dsp.*;
import org.zkoss.web.servlet.dsp.action.Page;
import org.zkoss.web.servlet.dsp.action.Action;

/**
 * Used to parse a DSP page into a meta format called
 * {@link Interpretation}.
 *
 * @author tomyeh
 */
public class Parser {
	/** Parses the content into a meta format
	 *
	 * @param content the content to parse; never null.
	 * @param ctype the content type. Optional. It is used only if
	 * no page action at all. If it is not specified and not page
	 * action, "text/html" is assumed.
	 * @param xelc the context information used to parse XEL expressions
	 * in the content.
	 * @param loc used to locate the resource such as taglib.
	 * It could null only if DSP contains no such resource.
	 */
	public Interpretation parse(String content, String ctype,
	XelContext xelc, Locator loc)
	throws DspException, IOException, XelException {
		final Context ctx = new Context(content, xelc, loc);
		final RootNode root = new RootNode();
		parse0(ctx, root, 0, content.length());

		root.setFunctionMapper(ctx.getFunctionMapper());
		if (!ctx.pageDefined) {
			//We always create a page definition
			final ActionNode action = new ActionNode(Page.class, 0);
			root.addChild(0, action);
			final Map attrs = new HashMap(2);

			if (ctype == null) ctype = "text/html";
			else if (ctype.startsWith(";")) ctype = "text/html" + ctype;

			attrs.put("optionalContentType", ctype);
			applyAttrs("page", action, attrs, ctx);
		}

		return root;
	}

	/** Recursively parse the content into a tree of {@link Node}.
	 */
	private static void parse0(Context ctx, Node parent, int from, int to)
	throws DspException, IOException, XelException {
		boolean esc = false;
		final StringBuffer sb = new StringBuffer(512);
		for (int j = from; j < to; ++j) {
			char cc = ctx.content.charAt(j);
			//We only recognize <%, <\%, ${, $\{ and <xx:yy>
			switch (cc) {
			case '<':
				if (j + 1 < to) {
					char c2 = ctx.content.charAt(j + 1);
					if (c2 == '\\') {
						if (j + 2 < to && ctx.content.charAt(j + 2) == '%')
							++j; //skip '\\'
					} else if (c2 == '%') {
						addText(parent, sb);
						j = parseControl(ctx, parent, j, to);
						continue;
					} else {
						final int oldLines = ctx.nLines;
						int k = skipWhitespaces(ctx, j + 1, to);
						int l = nextSeparator(ctx, k, to);
						if (l >= to || l == k
						|| ctx.content.charAt(l) != ':') {
							ctx.nLines = oldLines;
							break; //bypass what we don't recognize
						}
						final String prefix = ctx.content.substring(k, l);
						if (!ctx.hasPrefix(prefix)) {
							ctx.nLines = oldLines;
							break; //bypass what we don't recognize
						}

						addText(parent, sb);
						j = parseAction(ctx, parent, prefix, l, to);
						continue;
					}
				}
				break;
			case '$':
				if (j + 1 < to) {
					char c2 = ctx.content.charAt(j + 1);
					if (c2 == '\\') {
						if (j + 2 < to && ctx.content.charAt(j + 2) == '{')
							++j; //skip '\\'
					} else if (c2 == '{') {
						addText(parent, sb);
						j = parseEL(ctx, parent, j, to);
						continue;
					}
				}
				break;
			case '\n':
				++ctx.nLines;
			}
			sb.append(cc);
		}
		addText(parent, sb);
	}
	/** Parses a control (e.g., &lt;% page %&gt;) starting at from,
	 * and returns the postion of '&gt' (in %&gt;).
	 */
	private static int parseControl(Context ctx, Node parent,
	int from, int to)
	throws DspException, IOException, XelException {
		int j = from + 2;
		if (j + 1 >= to)
			throw new DspException(MWeb.DSP_ACTION_NOT_TERMINATED,
				new Object[] {null, new Integer(ctx.nLines)});

		//0. comment
		char cc = ctx.content.charAt(j);
		if (cc == '-' && ctx.content.charAt(j + 1)  == '-') { //comment
			for (int end = to - 4;; ++j) {
				if (j > end)
					throw new DspException(MWeb.DSP_COMMENT_NOT_TERMINATED,
						new Integer(ctx.nLines));
				if (ctx.content.charAt(j) == '\n')
					++ctx.nLines;
				else if (startsWith(ctx.content, j, to, "--%>"))
					return j + 3;
			}
		}
		if (cc != '@')
			throw new DspException(MWeb.DSP_EXPECT_CHARACTER,
				new Object[] {new Character('@'), new Integer(ctx.nLines)});

		//1: which control
		j = skipWhitespaces(ctx, j + 1, to);
		int k = nextSeparator(ctx, j, to);
		if (k >= to)
			throw new DspException(MWeb.DSP_ACTION_NOT_TERMINATED,
				new Object[] {null, new Integer(ctx.nLines)});
		final ActionNode action;
		final String ctlnm = ctx.content.substring(j, k);
		if ("taglib".equals(ctlnm)) {
			action = null;
		} else if ("page".equals(ctlnm)) {
			ctx.pageDefined = true;
			trim(parent); //Bug 1798123: avoid getOut being called before Page
			parent.addChild(action = new ActionNode(Page.class, ctx.nLines));
		} else {
			throw new DspException(MWeb.DSP_UNKNOWN_ACTION,
				new Object[] {ctlnm, new Integer(ctx.nLines)});
		}

		//2: parse attributes
		final Map attrs = new HashMap();
		k = parseAttrs(ctx, attrs, ctlnm, k, to);
		cc = ctx.content.charAt(k);
		if (cc != '%')
			throw new DspException(MWeb.DSP_EXPECT_CHARACTER,
				new Object[] {new Character('%'), new Integer(ctx.nLines)});

		if (action == null) { //taglib
			final String uri = (String)attrs.get("uri"),
				prefix = (String)attrs.get("prefix");
			if (prefix == null || uri == null)
				throw new DspException(MWeb.DSP_TAGLIB_ATTRIBUTE_REQUIRED, new Integer(ctx.nLines));
			ctx.loadTaglib(prefix, uri);
		} else {
			applyAttrs(ctlnm, action, attrs, ctx);
		}

		if (++k >= to || ctx.content.charAt(k) != '>')
			throw new DspException(MWeb.DSP_ACTION_NOT_TERMINATED,
				new Object[] {ctlnm, new Integer(ctx.nLines)});
		return k;
	}
	/** Trimmed {@link TextNode} that contains nothing but spaces.
	 */
	private static void trim(Node node) {
		for (Iterator it = node.getChildren().iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof TextNode) {
				final String s = ((TextNode)o).getText();
				if (s == null || s.trim().length() == 0)
					it.remove();
			}
		}
	}

	/** Parses an action (e.g., &lt;c:forEach...&gt;...&lt;/c:forEach&gt;).
	 * @param from the position of ':'
	 * @return the postion of the last '&gt'.
	 */
	private static int parseAction(Context ctx, Node parent,
	String prefix, int from, int to)
	throws DspException, IOException, XelException {
		//1: which action
		int j = skipWhitespaces(ctx, from + 1, to);
		int k = nextSeparator(ctx, j, to);
		if (k >= to)
			throw new DspException(MWeb.DSP_ACTION_NOT_TERMINATED,
				new Object[] {prefix+':', new Integer(ctx.nLines)});
		if (k == j)
			throw new DspException(MWeb.DSP_ACTION_REQUIRED, new Integer(ctx.nLines));

		final String actnm = ctx.content.substring(j, k);
		final Class actcls = ctx.getActionClass(prefix, actnm);
		if (actcls == null)
			throw new DspException(MWeb.DSP_UNKNOWN_ACTION,
				new Object[] {prefix+':'+actnm, new Integer(ctx.nLines)});
		final ActionNode action = new ActionNode(actcls, ctx.nLines);
		parent.addChild(action);

		//2: action's attributes
		final Map attrs = new HashMap();
		j = parseAttrs(ctx, attrs, actnm, k, to);
		char cc = ctx.content.charAt(j);
		boolean ended = cc == '/';
		if (!ended && cc != '>')
			throw new DspException(MWeb.DSP_UNEXPECT_CHARACTER,
				new Object[] {new Character(cc), new Integer(ctx.nLines)});

		applyAttrs(actnm, action, attrs, ctx);

		if (ended) {
			if (j + 1 >= to || ctx.content.charAt(j + 1) != '>')
				throw new DspException(MWeb.DSP_ACTION_NOT_TERMINATED,
					new Object[] {prefix+':'+actnm, new Integer(action.getLineNumber())});
			return j + 1;
		}

		//3: nested content
		final int nestedFrom = ++j, nestedTo;
		for (int depth = 0;; ++j) {
			if (j >= to)
				throw new DspException(MWeb.DSP_ACTION_NOT_TERMINATED,
					new Object[] {actnm, new Integer(action.getLineNumber())});

			cc = ctx.content.charAt(j);
			if (j + 1 < to) {
				if (cc == '<') {
					final int oldLines = ctx.nLines;
					k = j + 1;
					ended = ctx.content.charAt(k) == '/';
					k = skipWhitespaces(ctx, ended ? k + 1: k, to);
					int l = nextSeparator(ctx, k, to);
					if (l >= to || ctx.content.charAt(l) != ':'
					|| !prefix.equals(ctx.content.substring(k, l))) {
						ctx.nLines = oldLines;
						continue; //bypass
					}

					k = skipWhitespaces(ctx, l + 1, to);
					l = nextSeparator(ctx, k, to);
					if (l >= to || !actnm.equals(ctx.content.substring(k, l))) {
						ctx.nLines = oldLines;
						continue; //bypass
					}
					l = skipWhitespaces(ctx, l, to);
					if (l >= to || (ended && ctx.content.charAt(l) != '>')) {
						ctx.nLines = oldLines;
						continue; //bypass
					}

					if (ended) {
						if (--depth < 0) {
							nestedTo = j;
							j = l;
							break; //done
						}
					} else {
						++depth;
					}
					j = l;
					continue;
				} else if (cc == '$' && ctx.content.charAt(j + 1) == '{') {
					j = endOfEL(ctx, j, to);
					continue;
				}
			}
			if (cc == '\n') ++ctx.nLines;
		}

		parse0(ctx, action, nestedFrom, nestedTo); //recursive
		return j;
	}
	private static boolean startsWith(String content, int from, int to, String s) {
		for (int j = 0, len = s.length(); ; ++from, ++j) {
			if (j >= len) return true;
			if (from >= to || content.charAt(from) != s.charAt(j)) return false;
		}
	}
	private static int skipWhitespaces(Context ctx, int from, int to) {
		for (; from < to; ++from) {
			final char cc = ctx.content.charAt(from);
			if (cc == '\n') ++ctx.nLines;
			else if (!Character.isWhitespace(cc))
				break;
		}
		return from;
	}
	private static int nextSeparator(Context ctx, int from, int to) {
		for (; from < to; ++from) {
			final char cc = ctx.content.charAt(from);
			if ((cc < '0' || cc > '9') && (cc < 'a' || cc > 'z')
			&& (cc < 'A' || cc > 'Z') && cc != '_')
				break;
		}
		return from;
	}
	/** Parses the attributes.
	 */
	private static int parseAttrs(Context ctx, Map attrs, String actnm,
	int from, int to)
	throws DspException {
		for (int j, k = from;;) {
			j = skipWhitespaces(ctx, k, to);
			k = nextSeparator(ctx, j, to);
			if (k >= to)
				throw new DspException(MWeb.DSP_ACTION_NOT_TERMINATED,
					new Object[] {actnm, new Integer(ctx.nLines)});
			if (j == k) return j;

			final String attrnm = ctx.content.substring(j, k);
			k = skipWhitespaces(ctx, k, to);
			j = skipWhitespaces(ctx, k + 1, to);
			if (j >= to || ctx.content.charAt(k) != '=')
				throw new DspException(MWeb.DSP_ATTRIBUTE_VALUE_REQUIRED,
					new Object[] {actnm, attrnm, new Integer(ctx.nLines)});

			final char quot = ctx.content.charAt(j);
			if (quot != '"' && quot != '\'')
				throw new DspException(MWeb.DSP_ATTRIBUTE_VALUE_QUOTE_REQUIRED,
					new Object[] {actnm, attrnm, new Integer(ctx.nLines)});

			final StringBuffer sbval = new StringBuffer();
			for (k = ++j;; ++k) {
				if (k >= to)
					throw new DspException(MWeb.DSP_ATTRIBUTE_VALUE_QUOTE_REQUIRED,
						new Object[] {actnm, attrnm, new Integer(ctx.nLines)});
				final char cc = ctx.content.charAt(k);
				if (cc == '\n')
					throw new DspException(MWeb.DSP_ATTRIBUTE_VALUE_QUOTE_REQUIRED,
						new Object[] {actnm, attrnm, new Integer(ctx.nLines)});

				if (cc == quot) {
					++k;
					break; //found
				}

				sbval.append(cc);
				if (cc == '\\' && ++k < to)
					sbval.setCharAt(sbval.length() - 1, ctx.content.charAt(k));
			}

			attrs.put(attrnm, sbval.toString());
		}
	}
	/** Applies attributes.
	 */
	private static final
	void applyAttrs(String actnm, ActionNode action, Map attrs,
	ParseContext ctx)
	throws DspException, XelException {
		for (Iterator it = attrs.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String attrnm = (String)me.getKey();
			final String attrval = (String)me.getValue();
			try {
				action.addAttribute(attrnm, attrval, ctx);
			} catch (NoSuchMethodException ex) {
				throw new DspException(MWeb.DSP_ATTRIBUTE_NOT_FOUND,
					new Object[] {actnm, attrnm, new Integer(action.getLineNumber())});
			} catch (ClassCastException ex) {
				throw new DspException(MWeb.DSP_ATTRIBUTE_INVALID_VALUE,
					new Object[] {actnm, attrnm, attrval, new Integer(action.getLineNumber())}, ex);
			}
		}
	}

	/** Parses an EL expression starting at from.
	 * @return the position of }.
	 */
	private static int parseEL(Context ctx, Node parent, int from, int to)
	throws DspException, XelException {
		int j = endOfEL(ctx, from, to); //point to }
		parent.addChild(
			new XelNode(ctx.content.substring(from, j + 1), ctx));
		return j;
	}
	/** Returns the position of '}'. */
	private static int endOfEL(Context ctx, int from, int to)
	throws DspException {
		for (int j = from + 2;; ++j) {
			if (j >= to)
				throw new DspException(MWeb.EL_NOT_TERMINATED, new Integer(ctx.nLines));

			final char cc = ctx.content.charAt(j);
			if (cc == '}') {
				return j;
			} else if (cc == '\'' || cc == '"') {
				while (++j < to) {
					final char c2 = ctx.content.charAt(j);
					if (c2 == cc) break;
					if (cc == '\n')
						throw new DspException("Illegal EL expression: non-terminaled "+cc+" at line "+ctx.nLines+" character "+j);
					if (c2 == '\\' && ++j < to && ctx.content.charAt(j) == '\n')
						++ctx.nLines;
				}
			} else if (cc == '\n') {
				++ctx.nLines;
			}
		}
	}

	/** Adds a text node. */
	private static void addText(Node parent, StringBuffer sb) {
		if (sb.length() > 0) {
			parent.addChild(new TextNode(sb.toString()));
			sb.setLength(0);
		}
	}

	/** Context used for parsing. */
	private static class Context implements ParseContext {
		private final String content;
		/** (String prefix, Map(String name, Class class)). */
		private final Map _actions = new HashMap();
		private final Locator _locator;
		private final ExpressionFactory _xelf;
		private final SimpleMapper _mapper;
		private final VariableResolver _resolver;
		private int nLines;
		/** Whether the page action is defined. */
		private boolean pageDefined;

		//ParseContext//
		public ExpressionFactory getExpressionFactory() {
			return _xelf;
		}
		public VariableResolver getVariableResolver() {
			return _resolver;
		}
		public FunctionMapper getFunctionMapper() {
			return _mapper;
		}

		//Internal//
		private Context(String content, XelContext xelc, Locator loc) {
			this.content = content;
			_resolver = xelc != null ? xelc.getVariableResolver(): null;
			_mapper = new SimpleMapper(xelc != null ? xelc.getFunctionMapper(): null);
			_xelf = Expressions.newExpressionFactory();
			_locator = loc;
			this.nLines = 1;
		}
		private boolean hasPrefix(String prefix) {
			return _actions.containsKey(prefix);
		}
		private Class getActionClass(String prefix, String actnm) {
			final Map acts = (Map)_actions.get(prefix);
			return acts != null ? (Class)acts.get(actnm): null;
		}
		private void loadTaglib(String prefix, String uri)
		throws DspException, IOException {
			if (_locator == null)
				throw new DspException("Unable to load "+uri+" because locator is not specified");

			URL url = uri.indexOf("://") > 0 ? null: _locator.getResource(uri);
			if (url == null) {
				url = Taglibs.getDefaultURL(uri);
				if (url == null)
					throw new FileNotFoundException(uri);
			}

			try {
				loadTaglib0(prefix, url);
			} catch (IOException ex) {
				throw ex;
			} catch (Exception ex) {
				throw DspException.Aide.wrap(ex);
			}	
		}
		private void loadTaglib0(String prefix, URL url)
		throws Exception {
			final Element root = new SAXBuilder(true, false, true)
				.build(url).getRootElement();
			_mapper.load(prefix, root);

			final Map acts = new HashMap();
			for (Iterator it = root.getElements("tag").iterator();
			it.hasNext();) {
				final Element e = (Element)it.next();
				final String name = IDOMs.getRequiredElementValue(e, "name");
				final String clsName = IDOMs.getRequiredElementValue(e, "tag-class");
				final Class cls = Classes.forNameByThread(clsName);
				if (!Action.class.isAssignableFrom(cls))
					throw new DspException(cls+" doesn't implement "+Action.class);
				acts.put(name, cls);
			}
			if (!acts.isEmpty())
				_actions.put(prefix, acts);
		}
	}
}
