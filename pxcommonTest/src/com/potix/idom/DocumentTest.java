/* DocumentTest.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommonTest/src/com/potix/idom/DocumentTest.java,v 1.5 2006/02/27 03:42:08 tomyeh Exp $
	Purpose: Test MultiValues
	Description:
	History:
	 2001/4/12, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.idom;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

import com.potix.util.logging.Log;

import com.potix.idom.input.SAXBuilder;
import com.potix.idom.util.IDOMs;
import com.potix.xml.XPath;

import junit.framework.*;

public class DocumentTest extends TestCase {
	public static final Log log = Log.lookup(DocumentTest.class);

	private static final String FILE1 = "/metainfo/com/potix/idom/t1.xml";
	private static final String FILE2 = "/metainfo/com/potix/idom/t2.xml";
	private static final String FILEBIG5 = "/metainfo/com/potix/idom/tBig5.xml";
	private static final String WEBXML = "/metainfo/com/potix/idom/web.xml";

	public DocumentTest(String name) throws Exception {
		super(name);
		log.setLevel(Log.ALL);
		/** default: xerces (because ant uses it)
		System.setProperty("javax.xml.parsers.SAXParserFactory",
			//"org.apache.crimson.jaxp.SAXParserFactoryImpl");
			"org.apache.xerces.jaxp.SAXParserFactoryImpl");
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
			//"org.apache.crimson.jaxp.DocumentBuilderFactoryImpl");
			"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		*/
	}
	public static Test suite() {
		return new TestSuite(DocumentTest.class);
	}
	protected void setUp() {
		//TY: no need to specify these two properties
		//System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
		//	"org.apache.crimson.jaxp.DocumentBuilderFactoryImpl");
		//System.setProperty("javax.xml.parsers.SAXParserFactory",
		//	"org.apache.crimson.jaxp.SAXParserFactoryImpl");
	}
	protected void tearDown() {
		Log.lookup("com.potix.idom").setLevel(null);
	}

	private static void dump(Node node) {
		dump("", node);
	}
	private static void dump(String prefix, Node node) {
		System.out.println(prefix /*+ "Node: " + node.getClass().getName()*/
			+" " + node.getNodeName() + '=' + node.getNodeValue());

		if (node instanceof org.w3c.dom.Element) {
			org.w3c.dom.Element e = (org.w3c.dom.Element)node;
			NamedNodeMap map = e.getAttributes();

			System.out.println(prefix + "Element: " + e.getLocalName()
				+ " attribute #=" + map.getLength());
			for (int j = 0; j < map.getLength(); j++) {
				Node attr = map.item(j);
				System.out.println(prefix + "[" + j +"] "
					+ "name=" + attr.getNodeName()
					+ " value=" + attr.getNodeValue());
			}
		}
		org.w3c.dom.NodeList list = node.getChildNodes();
		for (int j=0, sz=list.getLength(); j<sz; ++j) {
			dump(prefix + j + ".", list.item(j));
		}
	}
	private void checkDOM(org.w3c.dom.Document doc, boolean dumpping)
	throws Exception {
		if (dumpping)
			dump(doc);

		assertTrue(0 == doc.getElementsByTagName("book").getLength());
		assertTrue(1 == doc.getElementsByTagName("t1:book").getLength());

		org.w3c.dom.DocumentType dt = doc.getDoctype();
		assertTrue(dt == doc.getFirstChild());
		assertEquals("#document", dt.getParentNode().getNodeName());
		assertEquals("t1:book", dt.getName());
		assertTrue(null == dt.getPreviousSibling());
		assertEquals("pi-cmd", dt.getNextSibling().getNodeName());
		assertEquals("#document", dt.getNextSibling().getParentNode().getNodeName());

		org.w3c.dom.Element root = doc.getDocumentElement();
		if (dumpping)
			dump(root);
		assertEquals("t1:book", root.getNodeName());
		assertEquals("http://potix.com/client/t1", root.getNamespaceURI());
		assertEquals("t1", root.getPrefix());
		assertTrue(7 == root.getChildNodes().getLength()); //3 text, 1 element, 1 pi
		assertTrue(root.hasAttribute("xmlns:t1"));
		assertTrue(root.hasAttribute("nsless"));
		assertTrue(!root.hasAttribute("ns"));
		assertTrue(root.hasAttributeNS(root.getNamespaceURI(), "ns"));
		assertTrue(root.hasAttribute("t1:ns"));

		assertTrue(!root.hasAttributeNS(root.getNamespaceURI(), "nsless"));
			//a bug of crimson 1.1.3: the above statement causes NullPointerException

		org.w3c.dom.Attr attr = root.getAttributeNode("t1:ns");
		assertEquals("t1:ns", attr.getName());

 		//-- XPath --//
		assertEquals(root, XPath.selectOne(root, "."));

		assertEquals("author", XPath.selectOne(doc, ".//author").getNodeName());
		assertTrue(null == XPath.selectOne(doc, ".//title"));

		Node title = XPath.selectOne(doc, ".//t1:title");
		assertEquals("t1:title", title.getNodeName());
	}
	public void testDOM() throws Exception {
		DocumentBuilderFactory fty = DocumentBuilderFactory.newInstance();
		fty.setNamespaceAware(true);
		DocumentBuilder parser = fty.newDocumentBuilder();
		org.w3c.dom.Document doc = parser.parse( //it doesn't support URL
			DocumentTest.class.getResourceAsStream(FILE1));
		checkDOM(doc, false);
	}
	public void testIDOM() throws Exception {
		//Log.lookup("com.potix.idom").setLevel(Log.FINER);
		Document doc = new SAXBuilder(true, false).build( //we support URL
			DocumentTest.class.getResource(FILE1));
		//dump(doc);
		assertEquals(new Integer(3), new Integer(doc.getChildren().size()));
		assertEquals(new Integer(7), new Integer(doc.getRootElement().getChildren().size()));
		assertEquals("t1:title", doc.getRootElement().getElement("t1:title").getName());
		checkDOM(doc, false);
		Log.lookup("com.potix.idom").setLevel(null);
	}
	public static void compareAttrs(NamedNodeMap map1, NamedNodeMap map2) {
		int j = 0, skip = 0;
		for (; j < map1.getLength(); j++) {
			Node n1 = map1.item(j);

			//Xerces generates xml:base for included elements, so ignore them
			if (n1.getNodeName().equals("xml:base")) {
				log.info("Skip: xml:base from " + n1.getClass());
				++skip;
				continue;
			}

			Node n2 = map2.getNamedItem(n1.getNodeName());
			assertNotNull(n2);
			assertEquals(n1.getNodeName(), n2.getNodeName());
			assertEquals(n1.getNodeValue(), n2.getNodeValue());
		}
		assertEquals(new Integer(map1.getLength()-skip),
			new Integer(map2.getLength()));
	}	
	public static void compare(Node node1, Node node2) {
		//log.info(node2.getClass());
		assertEquals(new Integer(node1.getNodeType()),
			new Integer(node2.getNodeType()));
		assertEquals(node1.getNodeName(), node2.getNodeName());
		assertEquals(node1.getNodeValue(), node2.getNodeValue());

		if (node1.getNodeType() == Node.ELEMENT_NODE) {
			org.w3c.dom.NamedNodeMap map1 = node1.getAttributes();
			org.w3c.dom.NamedNodeMap map2 = node2.getAttributes();
			compareAttrs(map1, map2);
		}
	
		org.w3c.dom.NodeList list1 = node1.getChildNodes();
		org.w3c.dom.NodeList list2 = node2.getChildNodes();
		assertEquals(new Integer(list1.getLength()),
			new Integer(list2.getLength()));
		for (int j=0, sz=list1.getLength(); j<sz; ++j)
			compare(list1.item(j), list2.item(j));
	}
	
	private void test(String flnm, boolean special, boolean dumpit)
	throws Exception {
		DocumentBuilderFactory fty = DocumentBuilderFactory.newInstance();
		fty.setNamespaceAware(true);
		if (special) {
			fty.setIgnoringComments(true);
			fty.setCoalescing(true);
			fty.setExpandEntityReferences(false);
		}

		DocumentBuilder parser = fty.newDocumentBuilder();
		org.w3c.dom.Document doc1 = parser.parse(
			DocumentTest.class.getResourceAsStream(flnm));
		if (dumpit) {
			System.out.println("DOM-----------------");
			dump(doc1);
		}

		SAXBuilder builder = new SAXBuilder(true, false);
		if (special) {
			builder.setIgnoringComments(true);
			builder.setCoalescing(true);
			builder.setExpandEntityReferences(false);
		}

		Document doc2 = builder.build( //we support URL
			DocumentTest.class.getResource(flnm));
		if (dumpit) {
			System.out.println("IDOM-----------------");
			dump(doc2);
			//IDOMs.dumpTree(doc2);
		}

		compare(doc1, doc2);
	}
	public void testT2A() throws Exception {
		//Log.lookup("com.potix.idom").setLevel(Log.FINER);
		log.debug("test " + FILE2 + "...");
		test(FILE2, false, false);
		Log.lookup("com.potix.idom").setLevel(null);
	}
	public void testT2B() throws Exception {
		//Log.lookup("com.potix.idom").setLevel(Log.FINER);
		test(FILE2, true, false);
		Log.lookup("com.potix.idom").setLevel(null);
	}
	private void addContent(Element e, String[] cs) {
		for (int j = 0; j < cs.length; ++j)
			e.setContent(cs[j], cs[j]);
	}
	public void testContent() throws Exception {
		Element e = new Element("a");
		String[] cs = {"b1", "b2", "b3/x", "b1/x", "b3", "b1/y"};
		addContent(e, cs);
		//IDOMs.dumpTree(e);

		assertTrue(3 == e.getChildren().size());
		assertTrue(2 == e.getElement("b3").getChildren().size());//1 element + 1 text
		for (int j = 0; j < cs.length; ++j)
			assertEquals(cs[j], e.getContent(cs[j]));

		/*
		Arrays.sort(cs); //because we want to compare one by one
		//getContents is obsoleted (will be moved to IDOMs).
		Collection c = IDOMs.getContents(e);
		assertEquals(new Integer(1 + cs.length), new Integer(c.size()));
		Iterator iter = c.iterator();
		assertEquals("", ((Map.Entry)iter.next()).getKey());
		for (int j = 0; iter.hasNext(); ++j)
			assertEquals(cs[j], ((Map.Entry)iter.next()).getKey());
		*/

		//remove
		Document doc = new Document(e);
		for (int j = 0; j < cs.length; ++j)
			e.removeContent(cs[j]);
		assertEquals(new Integer(0), new Integer(e.getChildren().size()));
		assertEquals(doc, e.getParent()); //not detached?
	}
	public void testContent2() throws Exception {
		Element e = new Element("a");
		
		String[] cs = {"b1", "b2", "b3", "b2", "b1", "b2"};
		String[] avs = {"1", "2", "3", "21", "11", "23"};
		for (int j = 0; j < cs.length; j++) {
			Element ce = new Element(cs[j]);
			Attribute a = new Attribute("id", avs[j]);
			ce.setAttributeNode(a);
			e.insertBefore(ce, null);	//
		}
		//IDOMs.dumpTree(e);
		
		org.w3c.dom.NodeList clist = e.getChildNodes();	//extra text node
		e.insertBefore(new Text("textnode1"), clist.item(5));
		e.insertBefore(new Text("textnode2"), clist.item(5));	
		//IDOMs.dumpTree(e);
		
		Element ce = new Element("b2");	//
		Attribute a = new Attribute("id", "22");
		ce.setAttributeNode(a);
		e.insertBefore(ce, clist.item(5));	//before 23
		//IDOMs.dumpTree(e);
		
		clist = e.getElementsByTagName("b2");
		String[] ms = {"2", "21", "22", "23"};
		for (int j = 0; j < clist.getLength(); j++) {
			Element n = (Element)clist.item(j);
			n.getAttributeNode("id").getValue();
			assertEquals(n.getAttributeNode("id").getValue(), ms[j]);
		}
	}
	public void checkModified(Item v, boolean expected) throws Exception {
		if (v.isModified() != expected) {
			System.out.println("Failed: " + v.getName() + " " + v.getClass());
			assertTrue(false);
		}

		if (v instanceof Group) {
			Iterator iter = ((Group)v).getChildren().iterator();
			while (iter.hasNext())
				checkModified((Item)iter.next(), expected);
		}
	}
	public void testClone() throws Exception {
		Element e1 = new Element("a");
		addContent(e1, new String[] {"b1", "b2", "b3/x", "b1/x", "b3", "b1/y"});
		Element e2 = (Element)e1.clone();
		//IDOMs.dumpTree(e2);
		checkModified(e2, false);

		//e1.setAttribute("multiple", " true ");
		//assertEquals("true", e1.getAttributeValue("multiple")); //normalized is required
			//we don't normalize it anymore
		e2 = (Element)e1.clone();
		assertEquals(e1.getAttributeValue("multiple"), e2.getAttributeValue("multiple"));
	}
	public void testBig5() throws Exception {
		Document doc = new SAXBuilder(true, false).build(
			DocumentTest.class.getResource(FILEBIG5));
		//assertEquals("\u9673\u5fd7\u6052", doc.getRootElement().getElementValue("name", true));
		//The above line proves Java is not capable to convert some Big5
		//extension characters to Unicode
	}
	public void testDocType() throws Exception {
		DocumentBuilderFactory fty = DocumentBuilderFactory.newInstance();
		fty.setNamespaceAware(true);
		DocumentBuilder parser = fty.newDocumentBuilder();
		org.w3c.dom.Document doc = parser.parse( //it doesn't support URL
			DocumentTest.class.getResourceAsStream(WEBXML));
		org.w3c.dom.DocumentType dt = doc.getDoctype();
//		System.out.println("----"+dt.getPublicId()+" "+dt.getSystemId());
	}
}
