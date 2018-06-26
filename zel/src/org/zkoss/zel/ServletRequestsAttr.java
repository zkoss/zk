/* ServletRequests.java

        Purpose:
                
        Description:
                
        History:
                Tue Jun 26 14:22:47 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zel;


public class ServletRequestsAttr {
	protected static final ThreadLocal<String> urls = new ThreadLocal<String>();

	public static final String getCurrentURL() {
		return urls.get();
	}

	public static final void setCurrentURL(String url) {
		urls.set(url);
	}
}
