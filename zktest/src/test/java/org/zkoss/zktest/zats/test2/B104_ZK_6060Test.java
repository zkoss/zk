/* B104_ZK_6060Test.java

        Purpose:
                
        Description:
                
        History:
                Sun Apr 26 08:40:03 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.junit.jupiter.api.Test;

public class B104_ZK_6060Test {

	@Test
	public void noDuplicateAttributesInZulXsd() throws Exception {
		URL xsd = getClass().getClassLoader().getResource("metainfo/xml/zul.xsd");
		assertNotNull(xsd, "metainfo/xml/zul.xsd not found on classpath");
		try (InputStream in = xsd.openStream()) {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			factory.newSchema(new StreamSource(in));
		}
	}
}
