/* WcsInfo.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec  2 12:12:32 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.http;

import java.util.List;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

/**
 * The WCS info.
 *
 * @author tomyeh
 */
/*package*/ class WcsInfo {
	/*package*/ final LanguageDefinition langdef;
	/** A list of URI or static method. */
	/*package*/ final Object[] items;
	/*package*/ WcsInfo(String lang, List<Object> items) {
		this.langdef = LanguageDefinition.lookup(lang);
		this.items = (Object[])items.toArray(new Object[items.size()]);
	}
}
