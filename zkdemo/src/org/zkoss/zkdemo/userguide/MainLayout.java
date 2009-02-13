/* MainLayout.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Oct 1, 2007 4:02:36 PM , Created by jumperchen
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
import org.zkoss.zkex.zul.Borderlayout;

/**
 * The main window of userguide.
 * @author jumperchen
 * @since 3.0.0
 */
public class MainLayout extends Borderlayout {
	public MainLayout() {
		// We have to decide the key of Google Maps since we have a demo using
		// it.
		// This key is used by zkdemo/userguide/index.zul to generate a proper
		// script
		final Execution exec = Executions.getCurrent();
		final String sn = exec.getServerName();
		final int sp = exec.getServerPort();

		// To add more keys: http://www.google.com/apis/maps/signup.html

		String gkey = null;
		if (sn.indexOf("www.potix.com") >= 0) { // http://www.potix.com/
			gkey = "ABQIAAAAmGxmYR57XDAbAumS9tV5fxRYCo_4ZGj_-54kHesWSk0nMkbs4xTpq0zo9O75_ZqvsSLGY2YkC7jjNg";
		} else if (sn.indexOf("www.zkoss.org") >= 0) { // http://www.zkoss.org/
			gkey = "ABQIAAAAmGxmYR57XDAbAumS9tV5fxQXyylOlR69a1vFTcUcpV6DXdesOBSMEHfkewcSzwEwBT7UzVx8ep8vjA";
		} else if (sn.indexOf("zkoss.org") >= 0) { // http://www.zkoss.org/
			gkey = "ABQIAAAAakIm31AXAvNGFHV8i1Tx8RSF4KLGEmvBsS1z1zAsQZvbQceuNRQBsm65qGaXpTWjZsc2bl-hm2Vyfw";
		} else if (sn.indexOf("localhost") >= 0) { // localhost
			if (sp == 80) // http://localhost/
				gkey = "ABQIAAAAmGxmYR57XDAbAumS9tV5fxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxRUITTZ-rzsyEVih16Hn3ApyUpSkA";
			else if (sp == 8080) // http://localhost:8080
				gkey = "ABQIAAAAmGxmYR57XDAbAumS9tV5fxTwM0brOpm-All5BF6PoaKBxRWWERSynObNOWSyMNmLGAMZAO1WkDUubA";
			else if (sp == 7799)
				gkey = "ABQIAAAAmGxmYR57XDAbAumS9tV5fxTT6-Op-9nAQgn7qnDG0QjE8aldaBRU1BQK2ADNWCt1BR2yg4ghOM6YIA";
		}

		if (gkey != null)
			exec.getDesktop().getSession().setAttribute("gmapsKey", gkey);
	}
}
