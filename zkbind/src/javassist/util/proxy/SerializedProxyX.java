/* SerializedProxyX.java

	Purpose:
		
	Description:
		Fix an issue for javassist - https://issues.jboss.org/browse/JASSIST-247
	History:
		10:43 AM 7/9/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package javassist.util.proxy;

import java.io.ObjectStreamException;

/**
 * A SerializedProxy helper class to fix javassist SerializedProxy issue on JDK8
 * @author jumperchen
 */
public class SerializedProxyX extends SerializedProxy {
	public SerializedProxyX(Class proxy, byte[] sig, MethodHandler h) {
		super(proxy, sig, h);
	}
	public Object readResolve() throws ObjectStreamException {
		return super.readResolve();
	}
}
