/* NonRootContextJQueryRenderPatch.java

	Purpose:
		
	Description:
		
	History:
		Thu Feb 2 17:24:04 TST 2012, Created by jimmyshiau

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zkplus.liferay;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.portlet.PortletSession;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.PageRenderPatch;
import org.zkoss.zk.ui.sys.RequestInfo;
/**
 * Used to patch the rendering result of a ZK portlet for Liferay.
 * When using ZK portlets with Liferay under non root context, we have
 * to add a JSESSIONID under root(/) path.
 *
 * <p>To use it, you have to specify a library proeprty called
 * ""org.zkoss.zk.portlet.PageRenderPatch.class" with this class's name
 * ("org.zkoss.zkplus.liferay.NonRootContextJQueryRenderPatch").
 *
 * @author jimmy
 * @since 5.0.11
 */
public class NonRootContextJQueryRenderPatch extends JQueryRenderPatch {

	public Writer beforeRender(RequestInfo reqInfo) {
		return new StringWriter();
	}

	public void patchRender(RequestInfo reqInfo, Page page, Writer result,
			Writer out) throws IOException {
		if (getDelay() >= 0)
			super.patchRender(reqInfo, page, result, out);
		PortletSession p = (PortletSession)reqInfo.getSession().getNativeSession();
		out.write("<script>zk.afterMount(function () {"+
			"document.cookie = 'JSESSIONID=" + p.getId() +
			"; path=/'" +
			"});</script>");
	}

}
