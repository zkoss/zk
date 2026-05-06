/* B110_ZK_5686Test.java

	Purpose:

	Description:

	History:
		Wed May  6 14:19:02 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.idom.Attribute;
import org.zkoss.idom.Element;
import org.zkoss.idom.Namespace;

public class B110_ZK_5686Test {
	@Test
	public void testCloneRetainsInScopeAttributeNamespaces() {
		Element root = new Element("zk");
		root.addDeclaredNamespace(new Namespace("w", "client"));

		Element child = new Element("div");
		root.appendChild(child);
		child.setAttribute(new Attribute(new Namespace("w", "client"), "onClick", "console.log('test')"));

		Element clonedChild = Assertions.assertDoesNotThrow(() -> (Element) child.cloneNode(true));
		Assertions.assertEquals("console.log('test')", clonedChild.getAttributeValue("w:onClick"));
		Assertions.assertNotNull(clonedChild.getNamespace("w"));
		Assertions.assertEquals("client", clonedChild.getNamespace("w").getURI());

		Element clonedRoot = Assertions.assertDoesNotThrow(() -> (Element) root.cloneNode(true));
		Element clonedNestedChild = (Element) clonedRoot.getFirstChild();
		Assertions.assertEquals("console.log('test')", clonedNestedChild.getAttributeValue("w:onClick"));
		Assertions.assertNotNull(clonedNestedChild.getNamespace("w"));
		Assertions.assertEquals("client", clonedNestedChild.getNamespace("w").getURI());
	}

	@Test
	public void testCloneKeepsAttributeNamespaceAfterMove() {
		Element root = new Element("zk");
		root.addDeclaredNamespace(new Namespace("w", "client"));
		Element child = new Element("div");
		root.appendChild(child);
		child.setAttribute(new Attribute(new Namespace("w", "client"), "onClick", "console.log('test')"));

		Element other = new Element("zk");
		other.addDeclaredNamespace(new Namespace("w", "http://other"));
		child.detach();
		other.appendChild(child);

		Element cloned = Assertions.assertDoesNotThrow(() -> (Element) child.cloneNode(true));
		Assertions.assertEquals("client", cloned.getAttributeItem("w:onClick").getNamespace().getURI());
	}
}
