/* Locator.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommon/src/com/potix/xml/Locator.java,v 1.2 2006/02/27 03:42:08 tomyeh Exp $
	Purpose: 
	Description: 
	History:
	2001/10/26 16:11:27, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.xml;

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
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:42:08 $
 */
public interface Locator
extends org.xml.sax.Locator, javax.xml.transform.SourceLocator {
}
