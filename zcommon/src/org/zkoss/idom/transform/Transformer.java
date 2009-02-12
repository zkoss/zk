/* Transformer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 13 17:31:01  2002, Created by andrewho
		May 19 2003, Modified by tomyeh to make API more complete
		and let developer know about Source and Result
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom.transform;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXResult;

import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.DocType;
import org.zkoss.idom.input.SAXHandler;

import org.zkoss.util.logging.Log;

/**
 * Transforms an iDOM Document.
 *
 * @author <a href="mailto:andrewho@potix.com">andrewho@potix.com</a>
 * @author tomyeh
 */
public class Transformer {
	private static final Log log = Log.lookup(Transformer.class);

	/** The transformer. */
	private final javax.xml.transform.Transformer _tfmr;
	/** Whether to output doc-type. */
	private boolean _outDocType = true;

	/**
	 * Transfomer constructor without stylesheet.
	 */
	public Transformer()
	throws TransformerConfigurationException {
		final TransformerFactory tf = TransformerFactory.newInstance();
		_tfmr = tf.newTransformer();
	}
	/** Constructs a transformer with a stylesheet in form of Source.
	 *
	 * <p>Examples:<br>
	 * <dl>
	 * <dt>File file</dt>
	 * <dd>new Transformer(new javax.xml.transform.stream.StreamSource(file));</dd>
	 * <dt>Reader reader</dt>
	 * <dd>new Transformer(new javax.xml.transform.stream.StreamSource(reader));</dd>
	 * <dt>URL url</dt>
	 * <dd>new Transformer(new javax.xml.transform.stream.StreamSource(url.openStream()));</dd>
	 * <dt>iDOM or DOM dom</dt>
	 * <dd>new Transformer(new javax.xml.transform.dom.DOMSource(dom));</dd>
	 * </dl>
	 *
	 * <p>See javax.xml.transform.stream.StreamSource
	 * and javax.xml.transform.dom.DOMSource
	 */
	public Transformer(Source source)
	throws TransformerConfigurationException {
		final TransformerFactory tf = TransformerFactory.newInstance();
		_tfmr = source != null ? tf.newTransformer(source): tf.newTransformer();
	}

	/** Sets whether to output the doc type.
	 * Default: true.
	 *
	 * <p>Useful only if {@link Document} is used in transform(), e.g.,
	 * {@link #transform(Document, Result)}.
	 * If not, you have to set OutputKeys.DOCTYPE_SYSTEM and
	 * OutputKeys.DOCTYPE_PUBLIC explicitly (thru {@link #getTransformer}).
	 */
	public final void enableOutputDocType(boolean enable) {
		_outDocType = enable;
	}
	private final Document processDocType(final Document doc) {
		if (!_outDocType)
			return doc;

		final DocType dt = doc.getDocType();
		if (dt == null)
			return doc;

		final String sysid = dt.getSystemId();
		if (sysid != null && sysid.length() > 0)
			_tfmr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, sysid);
		final String pubid = dt.getPublicId();
		if (pubid != null && pubid.length() > 0)
			_tfmr.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, pubid);
		return doc;
	}
	/** Returns the JAXP transformer encapsulated by this object.
	 * Then, you can use it to set properties, listener and so on.
	 * <p>Notice: OutputKeys.DOCTYPE_SYSTEM and OutputKeys.DOCTYPE_PUBLIC
	 * are set automatically if outDocType is true when constructing
	 * this object and {@link Document} is used to transform.
	 */
	public final javax.xml.transform.Transformer getTransformer() {
		return _tfmr;
	}

	/**
	 * Transforms from a source to a result.
	 *
	 * and javax.xml.transform.dom.DOMSource
	 * @param source the source
	 * @param result the result
	 * @see #transform(Document, Result)
	 */
	public final void transform(Source source, Result result)
	throws TransformerException {
		_tfmr.transform(source, result);
	}
	/**
	 * Transforms from an iDOM document to a result.
	 *
	 * <p>Examples:<br>
	 * <dl>
	 * <dt>File file</dt>
	 * <dd>transformer.transform(doc, new javax.xml.transform.stream.StreamResult(file));</dd>
	 * <dt>Writer writer</dt>
	 * <dd>transformer.transform(doc, new javax.xml.transform.stream.StreamResult(writer));</dd>
	 * <dt>URL url</dt>
	 * <dd>No simple way yet.</dd>
	 * <dt>iDOM or DOM dom</dt>
	 * <dd>{@link #transform(Source)} and {@link #transform(Document)}.</dd>
	 * <dt>String systemId</dt>
	 * <dd>transformer.transform(doc, new javax.xml.transform.stream.StreamResult(systemId));</dd>
	 * </dl>
	 *
	 * <p>See javax.xml.transform.stream.StreamResult
	 * and javax.xml.transform.dom.DOMResult
	 *
	 * @param doc the source document
	 * @param result the result
	 * @see #transform(Source, Result)
	 * @see #transform(Document)
	 */
	public final void transform(Document doc, Result result)
	throws TransformerException {
		_tfmr.transform(new DOMSource(processDocType(doc)), result);
	}
	/**
	 * Transforms from an iDOM element to a result.
	 *
	 * @param elm the source element
	 * @param result the result
	 */
	public final void transform(Element elm, Result result)
	throws TransformerException {
		_tfmr.transform(new DOMSource(elm), result);
	}
	/**
	 * Trasforms a source and returns the transformed result as
	 * an iDOM Document.
	 *
	 * @param source the source
	 * @return the transformed result in an iDOM document
	 */
	public final Document transform(Source source)
	throws TransformerException {
		final SAXHandler hdl = new SAXHandler();
		_tfmr.transform(source, new SAXResult(hdl));
		return hdl.getDocument();
	}
	/**
	 * Trasforms an iDOM document and returns the transformed result as
	 * another iDOM Document.
	 *
	 * @param doc the source document
	 * @return the transformed result in an iDOM document
	 */
	public final Document transform(Document doc)
	throws TransformerException {
		return transform(new DOMSource(processDocType(doc)));
	}
	/**
	 * Trasforms an iDOM element and returns the transformed result as
	 * another iDOM Document.
	 *
	 * @param elm the source element
	 * @return the transformed result in an iDOM document
	 */
	public final Document transform(Element elm)
	throws TransformerException {
		return transform(new DOMSource(elm));
	}
	/** Get a copy of the output properties for the transformation.
	 */
	public final Properties getOutputProperties() {
		return _tfmr.getOutputProperties();
	}
	/** Get an output property that is in effect for the transformation.
	 */
	public final String getOutputProperty(String name) {
		return _tfmr.getOutputProperty(name);
	}

	/** Set an output property that will be in effect for the transformation.
	 */
	public final void setOutputProperty(String name, String value) {
		_tfmr.setOutputProperty(name, value);
	}
	/** Set the output properties for the transformation.
	 */
	public final void setOutputProperties(Properties props) {
		_tfmr.setOutputProperties(props);
	}

	/** Get the error event handler in effect for the transformation. 
	 */
	public final ErrorListener getErrorListener() {
		return _tfmr.getErrorListener();
	}
	/** Set the error event listener in effect for the transformation.
	 */
	public final void setErrorListener(ErrorListener listener) {
		_tfmr.setErrorListener(listener);
	}
}
