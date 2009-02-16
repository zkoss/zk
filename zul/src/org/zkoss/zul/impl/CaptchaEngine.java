/* SimpleChartEngine.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 1 10:30:48     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

public interface CaptchaEngine {
	/** 
	 * Per the given data, generate the distortion Captcha image into a byte array.
	 */
	public byte[] generateCaptcha(Object data);
}
