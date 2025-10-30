/* B86_ZK_4312wpd.java

		Purpose:
		
		Description:
		
		History:
				Fri Sep 06 18:35:32 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class B86_ZK_4312wpd {
	public static String testFunction(String testParam, ServletRequest request) {
		HttpServletRequest req = (HttpServletRequest) request;
		return String.format("alert('testParam: %s - sessionId: %s')", testParam, req.getSession().getId());
	}
}
