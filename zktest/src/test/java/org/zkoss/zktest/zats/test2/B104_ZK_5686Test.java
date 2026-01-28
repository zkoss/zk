package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Level;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import org.zkoss.idom.Attribute;
import org.zkoss.idom.Element;
import org.zkoss.idom.Namespace;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class B104_ZK_5686Test extends WebDriverTestCase {
    @Test
    public void test() {
        Element parent = new Element("zk");
        Namespace clientNs = new Namespace("w", "client");
        parent.addDeclaredNamespace(clientNs);

        Element child = new Element("div");
        parent.getChildren().add(child);

        Attribute attr = new Attribute(clientNs, "onClick", "console.log('test')");
        child.getAttributeItems().add(attr);

        Assertions.assertEquals("w:onClick", attr.getTagName());
        Assertions.assertEquals(clientNs, attr.getNamespace());

        Element clonedChild = (Element) child.clone();

        Assertions.assertNotNull(clonedChild);
        Assertions.assertEquals(1, clonedChild.getAttributeItems().size());

        Attribute clonedAttr = clonedChild.getAttributeItems().get(0);
        Assertions.assertEquals("w:onClick", clonedAttr.getTagName());
        Assertions.assertEquals("console.log('test')", clonedAttr.getValue());
        Assertions.assertEquals("w", clonedAttr.getNamespace().getPrefix());
        Assertions.assertEquals("client", clonedAttr.getNamespace().getURI());
    }
}
