/** AuLoadCSS.java.

	Purpose:
		
	Description:
		
	History:
		3:19:40 PM Apr 16, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.au.out;

import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Executions;

/**
 * Loads a CSS file to client.
 * @author jumperchen
 * @since 8.0.0
 */
public class AuLoadCSS extends AuResponse {

	/**
	 * Loads a CSS file.
	 * @param href the URL of the CSS file
	 */
	public AuLoadCSS(String href) {
		this(href, null, null);
	}

	/**
	 * Loads a CSS file.
	 * @param href the URL of the CSS file, it will be encoded with
	 *            {@link Encodes#encodeURL(jakarta.servlet.ServletContext, jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse, String)}.
	 * @param id the identifier. Ignored if not specified
	 * @param media the media attribute. Ignored if not specified.
	 */
	public AuLoadCSS(String href, String id, String media) {
		super("loadCSS", new String[] { Executions.encodeURL(href), id, media });
	}

}
