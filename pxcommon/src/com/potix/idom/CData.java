/* CData.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommon/src/com/potix/idom/CData.java,v 1.6 2006/05/11 07:16:22 tomyeh Exp $
	Purpose: 
	Description: 
	History:
	2001/10/22 20:52:22, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.idom;

import org.w3c.dom.CDATASection;
import com.potix.idom.impl.*;

/**
 * The iDOM CDATA.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.6 $ $Date: 2006/05/11 07:16:22 $
 * @see Text
 */
public class CData extends AbstractTextual implements CDATASection {
	/** Constructor.
	 */
	public CData(String text) {
		super(text);
	}
	/** Constructor.
	 */
	public CData() {
	}

	//-- AbstractTextual --//
	protected void checkText(String text) {
		Verifier.checkCData(text, getLocator());
	}

	//-- Item --//
	public final String getName() {
		return "#cdata-section";
	}

	//-- Node --//
	public final short getNodeType() {
		return CDATA_SECTION_NODE;
	}
}
