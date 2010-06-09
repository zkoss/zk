/* SAXBuilder.java


	Purpose: 
	Description: 
	History:
	2001/10/25 13:21:14, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom.input;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;
import org.zkoss.idom.Document;

/**
 * The builder based on SAX parsers.
 *
 * <p>A new instance of {@link SAXHandler} is created and configured
 * each time one of the build methods is called.
 *
 * @author tomyeh
 * @see SAXHandler
 */
public class SAXBuilder {
	private static final Log log = Log.lookup(SAXBuilder.class);

	/** The parser. */
	private final SAXParser _parser;
	/** The iDOM factory. */
	private IDOMFactory _factory;
	/** Whether to ignore ignorable whitespace */
	private boolean _ignoreWhitespaces = false;
	/** Whether expansion of entities should occur */
	private boolean _expandEntities = true;
	/** Whether to convert CData to Text and coalesce them. */
	private boolean _coalescing = false;
	/** Whether to ignore comments. */
	private boolean _ignoreComments = false;
	/** The error handler. */
	private ErrorHandler _errHandler = null;
	/** The entity resolver. */
	private EntityResolver _resolver = null;

	/**
	 * Constructor which reuses a parser.
	 */
	public SAXBuilder(SAXParser parser) {
		if (parser == null)
			throw new NullPointerException("parser");
		_parser = parser;
	}
	/**
	 * Constructor that creates the parser on-the-fly.
	 *
	 * @param nsaware whether the parser is namespace aware
	 * @param validate whether the parser shall validate the document
	 *
	 * @exception ParserConfigurationException if a parser cannot be created
	 * which satisfies the requested configuration.
	 *
	 * @see #SAXBuilder(boolean, boolean, boolean)
	 */
	public SAXBuilder(boolean nsaware, boolean validate)
	throws ParserConfigurationException, SAXException {
		SAXParserFactory fty = SAXParserFactory.newInstance();

		// SAX2 namespace-prefixes should be true for either builder
		setSafeFeature(fty, "http://xml.org/sax/features/namespace-prefixes", true);

		// Set SAX2 namespaces feature appropriately
		setSafeFeature(fty, "http://xml.org/sax/features/namespaces", nsaware);
		fty.setNamespaceAware(nsaware);

		setSafeFeature(fty, "http://xml.org/sax/features/validation", validate);
		setSafeFeature(fty, "http://apache.org/xml/features/validation/schema", validate);
		fty.setValidating(validate);

		_parser = fty.newSAXParser();
	}
	private static
	void setSafeFeature(SAXParserFactory fty, String feature, boolean value) {
		try {
			fty.setFeature(feature, value);
		} catch (Throwable ex) {
			//IGNORE IT (crimson doesn't support ...validation/schema)
			if (feature.startsWith("http://xml.org"))
				log.warning("Ignored: "+fty+" doesn't support "+feature+". Cause: "+Exceptions.getMessage(ex));
		}
	}
	/**
	 * Constructor that creates the parser on-the-fly, that accepts
	 * an additional option, smartIgnore.
	 *
	 * <p>When parsing XML for input purpose only, it is better to use this
	 * constructor with smartIgnore true, and then comments will be ignored
	 * CDATA will be coalesced with TEXT. A smaller DOM tree is formed.
	 *
	 * @param nsaware whether the parser is namespace aware
	 * @param validate whether the parser shall validate the document
	 * @param smartIgnore whether to ignore comments and ignorable-whitesace
	 * (if validate is true), and to coalesce
	 *
	 * @exception ParserConfigurationException if a parser cannot be created
	 * which satisfies the requested configuration.
	 */
	public SAXBuilder(boolean nsaware, boolean validate, boolean smartIgnore)
	throws ParserConfigurationException, SAXException {
		this(nsaware, validate);
		if (smartIgnore) {
			setIgnoringComments(true);
			setCoalescing(true);
			if (validate)
				setIgnoringElementContentWhitespace(true);
		}
	}

	/**
	 * Tests whether to ignore whitespaces in element content.
	 */
	public final boolean isIgnoringElementContentWhitespace() {
		return _ignoreWhitespaces;
	}
	/**
	 * Sets whether the parser should elminate whitespace in 
	 * element content. They are known as "ignorable whitespace". 
	 * Only whitespace which is contained within element content that has
	 * an element only content model will be eliminated (see XML Rec 2.10).
	 *
	 * <p>For this setting to take effect requires that validation be turned on.
	 *
	 * <p>Default: false.
	 *
	 * @param ignore Whether to ignore whitespaces in element content.
	 */
	public final void setIgnoringElementContentWhitespace(boolean ignore) {
		_ignoreWhitespaces = ignore;
	}

	/**
	 * Tests whether to expand entity reference nodes.
	 */
	public final boolean isExpandEntityReferences() {
		return _expandEntities;
	}
	/**
	 * Sets whether to expand entities during parsing.
	 * A true means to expand entities as normal content.  A false means to
	 * leave entities unexpanded as <code>EntityReference</code> objects.
	 *
	 * <p>Default: true.
	 *
	 * @param expand whether entity expansion should occur.
	 */
	public final void setExpandEntityReferences(boolean expand) {
		_expandEntities = expand;
	}

	/**
	 * Indicates whether or not the factory is configured to produce parsers
	 * which converts CDATA to Text and appends it to the adjacent (if any)
	 * Text node.
	 *
	 * <p>Default: false.
	 *
	 * @return true if the factory is configured to produce parsers which
	 * converts CDATA nodes to Text nodes
	 * and appends it to the adjacent (if any) Text node; false otherwise.
	 */
	public final boolean isCoalescing() {
		return _coalescing;
	}
	/**
	 * Specifies that the parser produced by this code will convert
	 * CDATA to Text and append it to the adjacent (if any) text.
	 *
	 * <p>Default: false.
	 */
	public final void setCoalescing(boolean coalescing) {
		_coalescing = coalescing;
	}

	/**
	 * Indicates whether or not the factory is configured to produce parsers
	 * which ignores comments.
	 *
	 * <p>Default: false.
	 *
	 * @return true if the factory is configured to produce parsers
	 * which ignores comments; false otherwise.
	 */
	public final boolean isIgnoringComments() {
		return _ignoreComments;
	}
	/**
	 * Specifies that the parser produced by this code will ignore comments.
	 *
	 * <p>Default: false.
	 */
	public final void setIgnoringComments(boolean ignoreComments) {
		_ignoreComments = ignoreComments;
	}

	/**
	 * Specifies the org.xml.sax.ErrorHandler to be used to report errors
	 * present in the XML document to be parsed.
	 * <p>Default: null -- to use the default imple-mentation and behavior.
	 */
	public final void setErrorHandler(ErrorHandler eh) {
		_errHandler = eh;
	}
	/**
	 * Gets the org.xml.sax.ErrorHandler.
	 *
	 * @return the error handler; null to use the default implementation
	 */
	public final ErrorHandler getErrorHandler() {
		return _errHandler;
	}

	/**
	 * Specifies the org.xml.sax.EntityResolver to be used to resolve
	 * entities present in the XML docu-ment to be parsed.
	 * <p>Default: null -- to use the default implementation and behavior.
	 */
	public final void setEntityResolver(org.xml.sax.EntityResolver er) {
		_resolver = er;
	}
	/**
	 * Gets the org.xml.sax.EntityResolver.
	 *
	 * @return the enity resolverr; null to use the default implementation
	 */
	public final EntityResolver getEntityResolver() {
		return _resolver;
	}

	/**
	 * Tests whether or not this parser is configured to understand namespaces.
	 */
	public final boolean isNamespaceAware() {
		return _parser.isNamespaceAware();
	}
	/**
	 * Tests whether or not this parser is configured to validate XML documents.
	 */
	public final boolean isValidating() {
		return _parser.isValidating();
	}

	/**
	 * Gets the iDOM factory. Null for DefaultIDOMFactory.THE.
	 */
	public final IDOMFactory getIDOMFactory() {
		return _factory;
	}
	/**
	 * Sets the iDOM factory. Null for DefaultIDOMFactory.THE.
	 */
	public final void setIDOMFactory(IDOMFactory factory) {
		_factory = factory;
	}

	/**
	 * Gets the sax parser.
	 */
	public final SAXParser getParser() {
		return _parser;
	}

	/**
	 * Build an iDOM tree from a file.
	 */
	public final Document build(File src)
	throws SAXException, IOException {
		SAXHandler handler = newHandler();
		_parser.parse(src, handler);
		return handler.getDocument();
	}
	/**
	 * Build an iDOM tree from a input stream.
	 */
	public final Document build(InputStream src)
	throws SAXException, IOException {
		SAXHandler handler = newHandler();
		_parser.parse(src, handler);
		return handler.getDocument();
	}
	/**
	 * Build an iDOM tree from a input source.
	 */
	public final Document build(InputSource src)
	throws SAXException, IOException {
		SAXHandler handler = newHandler();
		_parser.parse(src, handler);
		return handler.getDocument();
	}
	/**
	 * Build an iDOM tree from a URI string.
	 */
	public final Document build(String uri)
	throws SAXException, IOException {
		SAXHandler handler = newHandler();
		_parser.parse(uri, handler);
		return handler.getDocument();
	}
	/**
	 * Build an iDOM tree from a URL.
	 */
	public final Document build(URL url)
	throws SAXException, IOException {
		SAXHandler handler = newHandler();
		_parser.parse(url.toExternalForm(), handler);
		return handler.getDocument();
	}
	/**
	 * Build an iDOM tree from a Reader.
	 */
	public final Document build(Reader src)
	throws SAXException, IOException {
		SAXHandler handler = newHandler();
		_parser.parse(new InputSource(src), handler);
		return handler.getDocument();
	}
	/**
	 * Creates a Sax Handler.
	 * Deriving class might override to provide a subclass of SAXHandler.
	 */
	protected SAXHandler newHandler() throws SAXException {
		SAXHandler handler = new SAXHandler(_factory);

		//configure handler
		handler.setIgnoringElementContentWhitespace(_ignoreWhitespaces);
		handler.setExpandEntityReferences(_expandEntities);
		handler.setCoalescing(_coalescing);
		handler.setIgnoringComments(_ignoreComments);
		handler.setErrorHandler(_errHandler);
		handler.setEntityResolver(_resolver);

		//configure parser
		setSafeProperty(
			"http://xml.org/sax/properties/lexical-handler",
			"http://xml.org/sax/handlers/LexicalHandler",
			handler);

		if (!isExpandEntityReferences()) { //not expanding?
			//then, we need declaration-handler
			setSafeProperty(
				"http://xml.org/sax/properties/declaration-handler", null,
				handler);
		}
		return handler;
	}
	private void setSafeProperty(String name, String auxnm, Object value) {
		Throwable ex;
		try {
			_parser.setProperty(name, value);
			return;
		} catch (Throwable t) {
			ex = t;
		}
		if (auxnm != null) {
			try {
				_parser.setProperty(auxnm, value);
				return;
			} catch (Throwable t) {
			}
		}

		if (name.startsWith("http://xml.org"))
			log.warning("Ignored: "+_parser+" doesn't support "+name+". Cause: "+Exceptions.getMessage(ex));
	}
}
