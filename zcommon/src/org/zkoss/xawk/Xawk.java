/* Xawk.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  2 20:19:20  2002, Created by tomyeh
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xawk;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import bsh.Interpreter;
import bsh.EvalError;
import bsh.TargetError;

import org.zkoss.util.logging.Log;
import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.math.Calcs;

/**
 * An Awk-like even-driven XML parser.
 *
 * @author tomyeh
 * @see Context
 */
public class Xawk {
	private static final Log log = Log.lookup(Xawk.class);

	/** The interpreter. */
	private Interpreter _ip = null;
	/** A list of {@link Xawk.Rule}. */
	protected final List _rules = new LinkedList();
	/** The scripts.*/
	protected String _init, _cleanup, _condition, _begin, _end;

	public Xawk() {
	}

	//-- Configuring --//
	/** Configures the xawk with a set of patterns and rules in a iDOM tree.
	 *
	 * @param root the root element that containing patterns and rules
	 */
	public final void config(Element root) throws XawkException {
		for (Iterator it = root.getElements("rule").iterator(); it.hasNext();) {
			Element e = (Element)it.next();

			List patterns = new LinkedList();
			for (Iterator i2 = e.getElements("pattern").iterator(); i2.hasNext();)
				patterns.add(((Element)i2.next()).getText(true));

			addRule(patterns, e.getElementValue("condition", true),
				e.getElementValue("begin", true), e.getElementValue("end", true));
		}

		_init = root.getElementValue("init", true);
		_cleanup = root.getElementValue("cleanup", true);
		_condition = root.getElementValue("condition", true);
		if (_condition != null && _condition.length() == 0)
			_condition = null; //note: "" is evaluated to true in bsh

		_begin = root.getElementValue("begin", true);
		_end = root.getElementValue("end", true);
	}
	/** Configures the xawk with a set of patterns and rules in a stream.
	 *
	 * @param strm the input stream that holds the XML of patterns and rules
	 */
	public final void config(InputStream strm)
	throws XawkException, SAXException, IOException {
		try {
			config(new SAXBuilder(true, false, true)
				.build(strm).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}
	/** Configures the xawk with a set of patterns and rules in a file.
	 *
	 * @param file the file that holds the XML of patterns and rules
	 */
	public final void config(File file)
	throws XawkException, SAXException, IOException {
		try {
			config(new SAXBuilder(true, false, true)
				.build(file).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}
	/** Configures the xawk with a set of patterns and rules in an uri.
	 *
	 * @param uri the uri that holds the XML of patterns and rules
	 */
	public final void config(String uri)
	throws XawkException, SAXException, IOException {
		try {
			config(new SAXBuilder(true, false, true)
				.build(uri).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}
	/** Configures the xawk with a set of patterns and rules in an url.
	 *
	 * @param url the url that holds the XML of patterns and rules
	 */
	public final void config(URL url)
	throws XawkException, SAXException, IOException {
		try {
			config(new SAXBuilder(true, false, true)
				.build(url).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}
	/** Configures the xawk with a set of patterns and rules in an input source.
	 *
	 * @param src the input source that holds the XML of patterns and rules
	 */
	public final void config(InputSource src)
	throws XawkException, SAXException, IOException {
		try {
			config(new SAXBuilder(true, false, true)
				.build(src).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}
	/** Configures the xawk with a set of patterns and rules in a reader.
	 *
	 * @param reader the reader that holds the XML of patterns and rules
	 */
	public final void config(Reader reader)
	throws XawkException, SAXException, IOException {
		try {
			config(new SAXBuilder(true, false, true)
				.build(reader).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}

	/** Adds a processing rule. A rule is defined by a collection of 
	 * of regular expression, a condition, a begin and an end.
	 * If any regex matches, the condition is tested. If the condition returns
	 * true, the begin script is executed before processing any child elements,
	 * and the end script is executed after processing all child elements.
	 *
	 * @param patterns a collection of regular expressions (String) that
	 * decides which element matches this rule. Null is OK.
	 * @param condition the BeanShell script to test after any pattern matches
	 * @param begin the BeanShell to execute before processing any child elements
	 * @param end the BeanShell to execute after processing all child elements
	 */
	public final void
	addRule(Collection patterns, String condition, String begin, String end) {
		if ((begin != null && begin.length() != 0)
		|| (end != null && end.length() != 0))
			_rules.add(new Rule(patterns, condition, begin, end));
	}
	/** Resets rules and global scripts.
	 *
	 * @see #addRule
	 */
	public final void resetRules() {
		_rules.clear();
		_init = _cleanup = _condition = _begin = _end = null;
	}

	/** Binds a value to a variable that can be accessed by the interpreter.
	 */
	public final void set(String name, Object val) throws XawkException {
		try {
			interpreter().set(name, val);
		}catch(TargetError ex) {
			Throwable t = ex.getTarget();
			throw new XawkException(t != null ? t: ex);
		}catch(EvalError ex) {
			throw new XawkException(ex);
		}
	}
	/** Returns the value bound to a variable of the interpreter.
	 */
	public final Object get(String name) throws XawkException {
		try {
			return interpreter().get(name);
		}catch(TargetError ex) {
			Throwable t = ex.getTarget();
			throw new XawkException(t != null ? t: ex);
		}catch(EvalError ex) {
			throw new XawkException(ex);
		}
	}
	public final void resetInterpreter() {
		_ip = null;
	}
	public final Interpreter interpreter() {
		if (_ip == null) {
			_ip = new Interpreter();
			_ip.setClassLoader(Thread.currentThread().getContextClassLoader());
		}
		return _ip;
	}
	/** Returns the context of the element being processed.
	 */
	public final Context context() {
		try {
			return _ip != null ? (Context)_ip.get("the"): null;
		}catch(EvalError ex) {
			return null; //not available
		}
	}

	//-- Parsing --//
	/** Parses the XML based on the patterns and rules
	 * ({@link #config(Element)}) and binding ({@link #set}).
	 *
	 * @param strm the source stream containing the XML to parse
	 * @param validate whether to validate the input
	 * @return the value of a variable called result, or null if not such
	 * variable
	 */
	public final Object parse(InputStream strm, boolean validate)
	throws XawkException, SAXException, IOException {
		try {
			return parse(new SAXBuilder(true, validate, true)
				.build(strm).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}
	/** Parses the XML based on the patterns and rules
	 * ({@link #config(Element)}) and binding ({@link #set}).
	 *
	 * @param file the source file containing the XML to parse
	 * @param validate whether to validate the input
	 * @return the value of a variable called result, or null if not such
	 * variable
	 */
	public final Object parse(File file, boolean validate)
	throws XawkException, SAXException, IOException {
		try {
			return parse(new SAXBuilder(true, validate, true)
				.build(file).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}
	/** Parses the XML based on the patterns and rules
	 * ({@link #config(Element)}) and binding ({@link #set}).
	 *
	 * @param uri the source URI containing the XML to parse
	 * @param validate whether to validate the input
	 * @return the value of a variable called result, or null if not such
	 * variable
	 */
	public final Object parse(String uri, boolean validate)
	throws XawkException, SAXException, IOException {
		try {
			return parse(new SAXBuilder(true, validate, true)
				.build(uri).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}
	/** Parses the XML based on the patterns and rules
	 * ({@link #config(Element)}) and binding ({@link #set}).
	 *
	 * @param url the source URL containing the XML to parse
	 * @param validate whether to validate the input
	 * @return the value of a variable called result, or null if not such
	 * variable
	 */
	public final Object parse(URL url, boolean validate)
	throws XawkException, SAXException, IOException {
		try {
			return parse(new SAXBuilder(true, validate, true)
				.build(url).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}
	/** Parses the XML based on the patterns and rules
	 * ({@link #config(Element)}) and binding ({@link #set}).
	 *
	 * @param src the input source containing the XML to parse
	 * @param validate whether to validate the input
	 * @return the value of a variable called result, or null if not such
	 * variable
	 */
	public final Object parse(InputSource src, boolean validate)
	throws XawkException, SAXException, IOException {
		try {
			return parse(new SAXBuilder(true, validate, true)
				.build(src).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}
	/** Parses the XML based on the patterns and rules
	 * ({@link #config(Element)}) and binding ({@link #set}).
	 *
	 * @param reader the source reader containing the XML to parse
	 * @param validate whether to validate the input
	 * @return the value of a variable called result, or null if not such
	 * variable
	 */
	public final Object parse(Reader reader, boolean validate)
	throws XawkException, SAXException, IOException {
		try {
			return parse(new SAXBuilder(true, validate, true)
				.build(reader).getRootElement());
		}catch(ParserConfigurationException ex) {
			throw new XawkException(ex);
		}
	}
	/** Parses the XML based on the patterns and rules
	 * ({@link #config(Element)}) and binding ({@link #set}).
	 *
	 * @param root the source iDOM tree to parse.
	 * @return the value of a variable called result, or null if not such
	 * variable
	 */
	public final Object parse(Element root) throws XawkException {
		try {
			Context ctx = new Context(root, interpreter());

			//eval <init>
			if (log.debugable()) //no D.ON to help debugging
				log.debug("init: " + _init);
			if (_init != null) {
				interpreter().set("the", ctx);
				interpreter().eval(_init);
			}

			if (root != null)
				parse(ctx);

			//eval <cleanup>
			if (log.debugable()) //no D.ON to help debugging
				log.debug("cleanup: " + _cleanup);
			if (_cleanup != null) {
				interpreter().set("the", ctx);
				interpreter().eval(_cleanup);
			}

			return interpreter().get("result");
		}catch(TargetError ex) {
			Throwable t = ex.getTarget();
			throw new XawkException(t != null ? t: ex);
		}catch(EvalError ex) {
			throw new XawkException(ex);
		}
	}

	/** Does the parsing recursively. */
	private final void parse(Context ctx)
	throws XawkException {
		final int[] ruleModes = new int[_rules.size()]; //Rule.ENDxxx
		int j = 0;
		for (final Iterator it = _rules.iterator(); it.hasNext();)
			ruleModes[j++] = ((Rule)it.next()).eval(ctx, Rule.BEGIN);

		for (final Iterator it = ctx.element.getChildren().iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof Element)
				parse(new Context((Element)o, ctx));
		}

		j = 0;
		for (final Iterator it = _rules.iterator(); it.hasNext();)
			((Rule)it.next()).eval(ctx, ruleModes[j++]);
	}

	/** The rule information. */
	protected class Rule {
		private final Pattern[] _patterns;
		private final String _condition, _begin, _end;

		/** Tests and executes the begin script if matches.
		 * The only mode for executing the begin script.
		 */
		protected static final int BEGIN = 0;
		/** Tests and executes the end script if matches. */
		protected static final int END_TEST_FIRST = 1;
		/** Always executes the end script. */
		protected static final int END_ALWAYS = 2;
		/** Never executes the end script. */
		protected static final int END_NEVER = 3;

		protected
		Rule(Collection patterns, String condition, String begin, String end) {
			if (patterns == null || patterns.size() == 0) {
				_patterns = null;
			} else {
				_patterns = new Pattern[patterns.size()];
				int j = 0;
				for (Iterator it = patterns.iterator(); it.hasNext();)
					_patterns[j++] = Pattern.compile((String)it.next());
			}
			_condition =
				condition != null && condition.length() != 0 ? condition: null;
			_begin = begin != null && begin.length() != 0 ? begin: null;
			_end = end != null && end.length() != 0 ? end: null;
		}

		/** Evaluates this rule if matches.
		 *
		 * @param mode one of BEGINxxx and ENDxxx
		 * @return one of END_xxx.
		 * The returned value is used only if mode = BEGIN
		 */
		protected final int eval(Context ctx, int mode)
		throws XawkException {
			String script = mode == BEGIN ? _begin: _end;
			if (script == null || mode == END_NEVER)
				return END_TEST_FIRST; //no script

			//eval patterns
			boolean doit = _patterns == null || mode == END_ALWAYS;
			if (!doit)
				for (int j = 0; j < _patterns.length; ++j)
					if (_patterns[j].matcher(ctx.path).matches()) {
						doit = true;
						break; //done
					}
			if (!doit)
				return END_NEVER; //no pattern matches

			try {
				ctx.interpreter.set("the", ctx);

				//eval the global condition
				if (mode != END_ALWAYS && Xawk.this._condition != null
				&& !Calcs.booleanValueOf(ctx.interpreter.eval(Xawk.this._condition)))
					return END_NEVER; //global condition fails

				//eval the rule condition
				if (mode != END_ALWAYS && _condition != null
				&& !Calcs.booleanValueOf(ctx.interpreter.eval(_condition)))
					return END_NEVER; //rule condition fails

				if (log.debugable()) //no D.ON to help debugging
					log.debug("path="+ctx.path+", text="+ctx.text+", script={"+script+'}');

				//do golbal begin
				if (mode == BEGIN && Xawk.this._begin != null)
					ctx.interpreter.eval(Xawk.this._begin);

				//do rule begin/end
				ctx.interpreter.eval(script);

				//do global end
				if (mode != BEGIN && Xawk.this._end != null)
					ctx.interpreter.eval(Xawk.this._end);

				return END_ALWAYS;
			}catch(TargetError ex) {
				Throwable t = ex.getTarget();
				throw new XawkException(t != null ? t: ex);
			}catch(EvalError ex) {
				throw new XawkException(ex);
			}
		}
	}
}
