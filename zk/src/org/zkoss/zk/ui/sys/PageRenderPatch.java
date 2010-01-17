/* PageRenderPatch.java

	Purpose:
		
	Description:
		
	History:
		Sat Jan 16 17:53:15 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.sys;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.Page;

/**
 * A patch that is used to process the rendering result of a page.
 * Currently, it is used only by {@link org.zkoss.zk.ui.http.DHtmlLayoutPortlet}
 * to allow the deployer to plug-in a patch for particular portal container.
 * For example, {@link org.zkoss.zkplus.liferay.JQueryRenderPatch} is used
 * to patch that Liferay failed to load zk.wpd under IE.
 *
 * <p>To specify a patch, use a library property called
 * "org.zkoss.zk.portlet.PageRenderPatch.class" (refer to {@link Attributes#PORTLET_RENDER_PATCH_CLASS}.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public interface PageRenderPatch {
	/** Called before rendering to test if the patch shall be applied.
	 * @param reqInfo the request information
	 * @return null if no need of patch, or a writer if the patch is required.
	 * The writer will be used to hold the rendering result of the porlet.
	 * Thus, you can process it later in {@link #patchRender} (so the writer
	 * is usually an instance of StringWriter).
	 */
	public Writer beforeRender(RequestInfo reqInfo);
	/** Called after rendering to patch the result.
	 * If {@link #beforeRender} returns null, this method won't be called
	 * since nothing to patch.
	 * <p>If {@link #beforeRender} returns a writer, the writer will become
	 * the result argument.
	 * @param reqInfo the request information
	 * @param result the result returned by {@link #beforeRender}, and
	 * the rendering result of a page will be written to it.
	 * @param out the real output sent to the client. It is the writer
	 * to write the patched result to.
	 */
	public void patchRender(RequestInfo reqInfo, Page page, Writer result, Writer out)
	throws IOException;
}
