/* ServletRequests.java

        Purpose:
                
        Description:
                
        History:
                Tue Jun 26 14:22:47 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zel;


public class ThreadLocalsManager {
	protected static final ThreadLocal<Object> pageDefs = new ThreadLocal<Object>();

	public static final Object getCurrentPageDef() {
		return pageDefs.get();
	}

	public static final void setCurrentPageDef(Object pageDef) {
		pageDefs.set(pageDef);
	}
}
