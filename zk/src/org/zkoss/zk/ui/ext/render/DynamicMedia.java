/* DynamicMedia.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 22 08:59:10     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.render;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.UiException;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl},
 * if a component might have viewable parts other than HTML (WML, XAML
 * or any client language). A typical example is an image and an audio.
 *
 * <h3>How it woks:</h3>
 *
 * <ol>
 * <li>DynamicMedia componet first invoke {@link org.zkoss.zk.ui.Desktop#getDynamicMediaURI}
 * to retrieve a URI and generate proper HTML (or any client language).<li>
 * <li>Then, client will send a request to the URI</li>
 * <li> {@link org.zkoss.zk.au.http.DHtmlUpdateServlet} interprets it
 * and call {@link #getMedia} to retrieve the media and return it the client</li>
 * </ol>
 *
 * @author tomyeh
 */
public interface DynamicMedia {
	/** Retrieve the view in {@link Media} format.
	 *
	 * <p>Unlike other methods, you cannot post event, create, remove,
	 * invalidate or do any smart updates in this method.
	 * In other words, READ ONLY.
	 *
	 * @param pathInfo the extra info passed to 
	 * {@link org.zkoss.zk.au.http.DHtmlUpdateServlet}.
	 * It is what you passed to
	 * {@link org.zkoss.zk.ui.Desktop#getDynamicMediaURI}.
	 * It is never null. It must start with "/" or be empty.
	 */
	public Media getMedia(String pathInfo);
}
