/* F110_ZK_6167VM.java

		Purpose:
				
		Description:
				
		History:
				Sun Sep 27 19:54:57 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.StringReader;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class F110_ZK_6167VM {
	// ZK-6167: the runtime classes are sun.util.calendar.ZoneInfo and a xerces DocumentImpl, both in packages the JDK does not export
	private final TimeZone tz = TimeZone.getTimeZone("Asia/Taipei");
	private final Document xdoc;

	public F110_ZK_6167VM() throws Exception {
		xdoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new InputSource(new StringReader("<r/>")));
	}

	public TimeZone getTz() {
		return tz;
	}

	public Document getXdoc() {
		return xdoc;
	}
}
