/* ImageableImageItem.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 21, 2007 5:27:07 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;

import org.zkoss.zkmob.Imageable;
import org.zkoss.zkmob.Itemable;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.ZkComponents;


/**
 * Wrapping ImageItem with Imageable so its image can be loaded 
 * via {@link org.zkoss.zkmob.ImageRequest}.
 * 
 * @author henrichen
 *
 */
public class ZkImageItem extends ImageItem implements Imageable, ZkComponent, Itemable {
	private String _id;
	private Zk _zk;
	private Form _form;
	
	public ZkImageItem(Zk zk, String id, String label, int layout, String altText,
	int appearanceMode) {
		super(label, null, layout, altText, appearanceMode);
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
		ZkComponents.setItemAttr(this, attr, val);

		if ("im".equals(attr)) {
			UiManager.loadImageOnThread(this, UiManager.prefixURL(_zk.getHostURL(), val));
		} else if ("tx".equals(attr)) {
			setAltText(val);
		}
	}

	//--Itemable--//
	public Form getForm() {
		return _form;
	}
	
	public void setForm(Form form) {
		_form = form;
	}
}
