/* URIInfo.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 13 11:26:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.util;

/**
 * Represents an URI and how to use the URI.
 *
 * @author tomyeh
 * @since 3.6.3
 */
public class URIInfo {
	/** Represents send-redirect shall be used to access the URI ({@link #uri}).
	 * It implies the URI can be anything, such as HTML, JSP, and ZUL,
	 * as long as it is a complete Web page.
	 * @see #type
	 */
	public static final int SEND_REDIRECT = 0x0000;
	/** Represents popup shall be used to show the content of the URI ({@link #uri}).
	 * It implies the content of the URI might be a fragment of HTML tags
	 * (rather than ZUL).
	 * @see #type
	 */
	public static final int POPUP = 0x0001;

	/** The URI. */
	public final String uri;
	/** How to use the URI. It could be one of {@link #SEND_REDIRECT}
	 * or {@link #POPUP}.
	 */
	public final int type;

	/** Constructor.
	 * @param uri the URI. (Notice that URI is not encoded with servlet context yet)
	 * @param type how to use the URI. It could be one of {@link #SEND_REDIRECT}
	 * or {@link #POPUP}.
	 */
	public URIInfo(String uri, int type) {
		this.uri = uri;
		this.type = type;
	}
	/** Contructor.
	 * It is equivalent to URIInfo(uri, SEND_REDIRECT).
	 */
	public URIInfo(String uri) {
		this(uri, SEND_REDIRECT);
	}
}
