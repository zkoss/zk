/* Location.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec  8 15:23:58 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.util.resource;

/**
 * Represents a location information inside a document.
 *
 * @author tomyeh
 * @since 6.0.0
 */
public interface Location {
	/** Returns the path of the document, or null if not available.
	 */
	public String getPath();
	/** Return the line number in the document, or -1 if not available.
	 */
	public int getLineNumber();
	
	/** Return the column number in the document, or -1 if not available.
	 */
	public int getColumnNumber();

	/** Fomrats the given message by prefixing with the location information.
	 */
	public String format(String message);
}
