/* Taglib.java

{{IS_NOTE
	$Id: Taglib.java,v 1.3 2006/02/27 03:41:53 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 09:03:10     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.el;

/**
 * Represents a taglib.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/02/27 03:41:53 $
 */
public class Taglib {
	private final String _prefix, _uri;
	public Taglib(String prefix, String uri) {
		if (prefix == null || uri == null)
			throw new NullPointerException();
		_prefix = prefix;
		_uri = uri;
	}
	/** Returns the prefix. */
	public String getPrefix() {
		return _prefix;
	}
	/** Returns the uri. */
	public String getURI() {
		return _uri;
	}

	//-- Object --//
	public String toString() {
		return "[prefix: "+_prefix+" uri="+_uri+']';
	}
	public int hashCode() {
		return _prefix.hashCode() + _uri.hashCode();
	}
	public boolean equals(Object o) {
		return o instanceof Taglib
			&& ((Taglib)o)._prefix.equals(_prefix)
			&& ((Taglib)o)._uri.equals(_uri);
	}
}
