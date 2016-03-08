/* ProxyFactoryX.java

	Purpose:
		
	Description:
		Fix an issue for javassist - https://issues.jboss.org/browse/JASSIST-247
	History:
		10:51 AM 7/9/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package javassist.util.proxy;

/**
 * A ProxyFactory helper class to fix javassist SerializedProxy issue on JDK8
 * @author jumperchen
 */
public class ProxyFactoryX extends ProxyFactory {
	public static byte[] getFilterSignature(Class clazz) {
		return ProxyFactory.getFilterSignature(clazz);
	}
}
