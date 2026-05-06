package org.zkoss.zktest.zats.test2;

import org.zkoss.idom.Attribute;
import org.zkoss.idom.Element;
import org.zkoss.idom.Namespace;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class B104_ZK_5686Test {
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
}
