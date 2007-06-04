/* ImageableAlert.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 21, 2007 7:21:31 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Image;

import org.zkoss.zkmob.Imageable;
import org.zkoss.zkmob.ZkComponent;

/**
 * Wrapping Alert component with Imageable so its image can be loaded 
 * via {@link org.zkoss.zkmob.ImageRequest}.
 * 
 * @author henrichen
 */
public class ZkAlert extends Alert implements Imageable, ZkComponent {
	private String _id;
	private Zk _zk;
	
	public ZkAlert(Zk zk, String id, String title, String alertText, AlertType alertType) {
		super(title, alertText, null, alertType);
		_id = id;
		_zk = zk;
	}

	//--Imageable--//
	public void loadImage(Image image) {
		setImage(image);
	}
	
	//--ZkComponent--//
	public String getId() {
		return _id;
	}
	
	public Zk getZk() {
		return _zk;
	}

	public void setAttr(String attr, String val) {
		//TODO:
	}
	
}
