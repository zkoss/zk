/* ZKClientTestCase.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 4, 2009 9:50:12 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest;

/**
 * The ZK Client side API.
 * @author jumperchen
 *
 */
public class ZKClientTestCase extends ZKTestCase {
	
	/**
	 * Returns the Widget object of the UUID.
	 * @param number the number of the widget ID.
	 * @see #uuid(int)
	 * @see #getWidget(String)
	 */
	protected Widget widget(int number) {
		return widget(uuid(number));
	}
	
	/**
	 * Returns the Widget object of the UUID.
	 * @param uuid the widget ID.
	 */
	protected Widget widget(String uuid) {
		return new Widget(uuid);
	}
	
	/**
	 * Returns the Jquery object of the UUID
	 * <p> Default: appends "#" sign
	 * @param uuid the UUID of the element without "#" sign
	 * @see #jq(String, boolean)
	 */
	protected Jquery jq(String uuid) {
		return jq(uuid, true);
	}
	
	/**
	 * Returns the Jquery object of the ZKClientObject.
	 * @param el the ZKClientObject
	 */
	protected Jquery jq(ClientWidget el) {
		return new Jquery(el);
	}
	/**
	 * Returns the Jquery object of the UUID
	 * @param uuid the UUID of the element
	 * @param sharp if true, appends "#" sign automatically.
	 * @return
	 */
	protected Jquery jq(String uuid, boolean sharp) {
		return new Jquery((sharp ? "#" : "") + uuid);
	}
	
	/**
	 * Returns the ZK object of the ZKClientObject.
	 * @param el the ZKClientObject
	 */
	protected ZK zk(ClientWidget el) {
		return new ZK(el);
	}
	
	/**
	 * Returns the ZK object of the UUID
	 * @param uuid the UUID of the element
	 */
	protected ZK zk(String uuid) {
		return new ZK(uuid);
	}
	
}
