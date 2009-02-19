/* ThemeImage.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 2, 2007 9:08:20 AM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Image;

/**
 * @author Dennis.Chen / Tom Yeh
 */
public class ThemeImage extends Image {
	
	
	public void onCreate(){
		final String src = getSrc();
		String fs = FontSizeThemeProvider.getFontSizeCookie(
			Executions.getCurrent());
		if("lg".equals(fs)){
			updateSrc("large");
		}else if("sm".equals(fs)){
			updateSrc("small");
		}else{
			updateSrc("normal");
		}
	}
	private void updateSrc(String fontsz) {
		String src = getSrc();
		int j = src.indexOf(fontsz);
		if (j >= 0)
			setSrc(new StringBuffer(src).insert(j + fontsz.length(), "-sel").toString());
	}
	
	public void onClick() {
		String src = getSrc();
		Execution exec = Executions.getCurrent();
		FontSizeThemeProvider.setFondSizeCookie(exec,
			src.indexOf("large") >= 0 ? "lg":
			src.indexOf("small") >= 0 ? "sm": "");
		exec.sendRedirect("");
	}
}
