/** HtmlTreeBuilder.java.

	Purpose:
		
	Description:
		
	History:
		5:21:43 PM Sep 25, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zhtml.impl;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.input.ReaderInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.idom.Namespace;
import org.zkoss.idom.ProcessingInstruction;
import org.zkoss.util.Pair;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.TreeBuilder;
import org.zkoss.zsoup.Zsoup;
import org.zkoss.zsoup.nodes.Attribute;
import org.zkoss.zsoup.nodes.Comment;
import org.zkoss.zsoup.nodes.DataNode;
import org.zkoss.zsoup.nodes.Document;
import org.zkoss.zsoup.nodes.DocumentType;
import org.zkoss.zsoup.nodes.Element;
import org.zkoss.zsoup.nodes.Node;
import org.zkoss.zsoup.nodes.TextNode;
import org.zkoss.zsoup.nodes.XmlDeclaration;
import org.zkoss.zsoup.parser.Parser;
import org.zkoss.zsoup.parser.XHtmlTreeBuilder.ExceptionInfo;
import org.zkoss.zsoup.select.Elements;

/**
 * A HTML tree builder for parsing a content of a page into a idom tree.
 * <p> The default parsing engine is to use <tt>zsoup</tt> library.
 * @author jumperchen
 * @since 8.0.0
 */
public class HtmlTreeBuilder implements TreeBuilder {
	private static final Logger log = LoggerFactory.getLogger(HtmlTreeBuilder.class.getName());

	private final Map<String, Pair<Element, Namespace>> _nsMap = new HashMap<String, Pair<Element, Namespace>>(
			6);
	private final Map<Element, List<Namespace>> _elNSMap = new HashMap<Element, List<Namespace>>(
			6);
	
	private static class UiExceptionX extends UiException {
		private static final long serialVersionUID = 20140930153033L;
		private String _keyword;
		public UiExceptionX(String msg, String keyword) {
			super(msg);
			_keyword = keyword;
		}
		public String getKeyword() {
			return _keyword;
		}
	}
	
	private String getLineNumber(Reader file, String keyword) {
		return getLineNumber(new Scanner(file), keyword);
	}
	private String getLineNumber(Scanner scanner) {
		try {
			int row = 0;
			while (scanner.hasNextLine()) {
				scanner.nextLine();
				row++;
			}
			return "line: " + (row + 1) + ", column: 0";
		} finally {
			scanner.close();
		}
	}
	private String getLineNumber(Scanner scanner, String keyword) {
		try {
			int row = 0;
			while (scanner.hasNextLine()) {
				row++;
				String line = scanner.nextLine();
				int col = line.indexOf(keyword);
				if (col >= 0)
					return "line: " + row + ", column: " + (col+1) ;
			}
		} finally {
			scanner.close();
		}
		return null;
	}
	private void initNamespaceMap(String filePath, Elements elements) {
		// bind "xml" prefix to the XML uri
		_nsMap.put("xml", new Pair<Element, Namespace>(null,
				Namespace.getSpecial("xml")));
    	// bind "xmlns" prefix to the XMLNS uri
		_nsMap.put("xmlns", new Pair<Element, Namespace>(null,
				Namespace.getSpecial("xmlns")));
		
		for (Element ele : elements) {
			for (Attribute attr : ele.attributes()) {
				String key = attr.getOriginalKey();
				if (key.startsWith("xmlns")) {
					String prefix = "";
					if (key.startsWith("xmlns:")) {
						prefix = key.substring(6);
					} else if (attr.getValue().endsWith("zul")) {
						log.warn("The default namespace should not be ZUL, it may cause some potential errors! Please use ZUL file extension instead. [File at: " + filePath + "]");
					}
					if (!_nsMap.containsKey(prefix)) {
						Namespace ns = new Namespace(prefix, attr.getValue());
						_nsMap.put(prefix, new Pair<Element, Namespace>(ele, ns));
						List<Namespace> list = _elNSMap.get(ele);
						if (list == null)
							list = new LinkedList<Namespace>();
						list.add(ns);
						_elNSMap.put(ele, list);
					}
				}
			}
		}
		if (_nsMap.isEmpty())
			_nsMap.put("", new Pair<Element, Namespace>(null, new Namespace("",
					"")));
	}

	private Namespace getNamespace(Element el) {
		if (el != null) {
			String[] tags = el.tagName().split(":");
			if (tags.length > 1) {
				Pair<Element, Namespace> pair = _nsMap.get(tags[0]);
				if (pair == null) {
					throw new UiExceptionX("Unknown namespace: [" + el.tagName()
							+ "]", el.tagName());
				}
				return pair.getY();
			}
			Pair<Element, Namespace> p = _nsMap.get("");
			if (p == null)
				return Namespace.NO_NAMESPACE;
			else
				return p.getY();
		}
		return null;
	}
	private List<Namespace> getDeclareNamespace(Element el) {
		return _elNSMap.get(el);
	}

	private Namespace getNamespace(Attribute attr) {
		if (attr != null) {
			String[] tags = attr.getOriginalKey().split(":");
			if (tags.length > 1) {
				Pair<Element, Namespace> pair = _nsMap.get(tags[0]);
				if (pair == null) {
					throw new UiExceptionX("Unknown namespace: ["
							+ attr.getOriginalKey() + "]", attr.getOriginalKey());
				}
				return pair.getY();
			}
			return Namespace.NO_NAMESPACE; // attribute will return an empty namespace
		}
		return null;
	}

	private org.zkoss.idom.Document convertToIDOM(Document doc) {
		initNamespaceMap(doc.baseUri(), doc.select("[^xmlns]"));
		org.zkoss.idom.Document root = new org.zkoss.idom.Document(null, null);
		for (Node n : doc.childNodes()) {
			if (n instanceof XmlDeclaration) {
				root.appendChild(convert((XmlDeclaration) n));
			} else if (n instanceof DocumentType) {
				root.appendChild(convert((DocumentType) n));
			} else {
				convertChildNodes(root, root, n);
			}
		}
		return root;
	}

	private ProcessingInstruction convert(XmlDeclaration xd) {
		String data = xd.getWholeDeclaration();
		
		// like <?taglib?>, we need to ignore the last char '?' here
		if (data.endsWith("?"))
			data = data.substring(0, data.length() - 1);
		String target = "";
		int index = data.indexOf(' ');
		if (index > 0) {
			target = data.substring(0, index);
			data = data.substring(index);
		}
		return new ProcessingInstruction(target, data);
	}

	private org.zkoss.idom.DocType convert(DocumentType dtype) {
		return new org.zkoss.idom.DocType(dtype.attr("name"),
				dtype.attr("publicId"), dtype.attr("systemId"));
	}

	private org.zkoss.idom.Text convert(TextNode text) {
		return new org.zkoss.idom.Text(text.getWholeText());
	}

	private org.zkoss.idom.Comment convert(Comment comment) {
		return new org.zkoss.idom.Comment(comment.getData());
	}

	private org.zkoss.idom.Text convert(DataNode dataNode) {
		return new org.zkoss.idom.Text(dataNode.getWholeData());
	}

	private static String getLocalName(String name) {
		String[] ns = name.split(":");
		if (ns.length > 1)
			return ns[1];
		return name;
	}
	private org.zkoss.idom.Attribute convert(Attribute attr) {
		try {
			return new org.zkoss.idom.Attribute(getNamespace(attr), getLocalName(attr.getOriginalKey()), attr.getValue());
		} catch (Exception e) {
			throw new UiExceptionX(e.getMessage(), attr.getOriginalKey());
		}
	}
	private org.zkoss.idom.Element convert(Element element) {
		try {
			return new org.zkoss.idom.Element(getNamespace(element), getLocalName(element.tagName()));
		} catch (Exception e) {
			throw new UiExceptionX(e.getMessage(), element.tagName());
		}
	}
	// merge attribute
	private void mergeAttr(org.w3c.dom.Node node, Node n) {
		if (node instanceof org.zkoss.idom.Element) {
			org.zkoss.idom.Element ie = (org.zkoss.idom.Element) node;
			for (Attribute attr : n.attributes())
				ie.setAttribute(convert(attr));
		}
	}

	private void convertChildNodes(org.w3c.dom.Node root, org.w3c.dom.Node parent, Node n) {
		org.w3c.dom.Node node = null;
		if (n instanceof Element) {
			node = convert((Element) n);
			List<Namespace> list = getDeclareNamespace((Element)n);
			if (list != null) {
				for (Namespace ns : list) {
					((org.zkoss.idom.Element)node).addDeclaredNamespace(ns);
				}
			}
		} else if (n instanceof Comment) {
			node = convert((Comment) n);
		} else if (n instanceof TextNode) {
			node = convert((TextNode) n);
		} else if (n instanceof DataNode) {
			node = convert((DataNode) n);
		} else if (n instanceof XmlDeclaration) {
			root.appendChild(convert((XmlDeclaration) n));
		}
		if (node != null) {
			parent.appendChild(node);
			mergeAttr(node, n); // merge after appended to parent.
		}
		for (Node subNode : n.childNodes()) {
			convertChildNodes(root, node, subNode);
		}
	}

	public org.zkoss.idom.Document parse(File file) throws Exception {
		return parse(file.toURI().toURL());
	}

	public org.zkoss.idom.Document parse(URL url) throws Exception {
		InputStream inStream = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Parsing file: [" + url.toString() + "]");
			inStream = url.openStream();
			return convertToIDOM(Zsoup.parse(inStream, "UTF-8", url.getFile(), Parser.xhtmlParser()));
		} catch (UiExceptionX ue) {
			throw ue;
		} catch (ExceptionInfo e) {
			Document currentDocument = e.getCurrentDocument();
			if (currentDocument != null) {
				currentDocument.outputSettings(currentDocument.outputSettings().prettyPrint(false));
				throw new UiException(" at [file:" + url.getFile() + ", " + getLineNumber(new Scanner(currentDocument.toString())) + "]", e);
			} else
				throw new UiException(" at [file:" + url.getFile() + "]", e);
		} finally {
			if (inStream != null)
				inStream.close();
		}
	}

	public org.zkoss.idom.Document parse(Reader reader) throws Exception {
		ReaderInputStream inputStream = null;
		try {

			if (log.isDebugEnabled())
				log.debug("Parsing reader: [" + reader + "]");
			
			inputStream = new ReaderInputStream(reader);
			return convertToIDOM(Zsoup.parse(inputStream,
				"UTF-8", null, Parser.xhtmlParser()));
		} catch (UiExceptionX ue) {
			String lineNumber = getLineNumber(reader, ue.getKeyword());
			if (lineNumber != null)
				throw new UiException(ue.getMessage() + lineNumber);
			else
				throw ue;
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}

}
