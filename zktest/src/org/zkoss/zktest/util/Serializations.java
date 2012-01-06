/* Serializations.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Jan 6, 2012 12:08:32 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author simonpai
 */
public class Serializations {
	
	/**
	 * Clone by serialization.
	 */
	public static Object clone(Object obj) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream boa = new ByteArrayOutputStream();
		new ObjectOutputStream(boa).writeObject(obj);
		byte[] bs = boa.toByteArray();
		return new ObjectInputStream(new ByteArrayInputStream(bs)).readObject();
	}
	
}
