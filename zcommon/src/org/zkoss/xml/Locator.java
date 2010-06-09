/* Locator.java


	Purpose: 
	Description: 
	History:
	2001/10/26 16:11:27, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xml;

/**
 * The interface represents the source locator.
 *
 * <p>Unfortunately, Java XML has two interfaces doing the same thing:
 * org.w3c.sax.Locator and javax.xml.Transform.SourceLocator.
 * This class is, thus, to provide to bridge both.
 *
 * <p>Our applications, that provide source location info, shall
 * implements this interface, rather than any of above.
 *
 * @author tomyeh
 */
public interface Locator
extends org.xml.sax.Locator, javax.xml.transform.SourceLocator {
}
