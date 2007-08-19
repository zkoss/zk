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
package org.zkoss.zkmob.ui;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;

import org.zkoss.zkmob.Imageable;
import org.zkoss.zkmob.Itemable;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ZkComponent;


/**
 * Wrapping ImageItem with Imageable so its image can be loaded 
 * via {@link org.zkoss.zkmob.ImageRequest}.
 * 
 * @author henrichen
 *
 */
public class ZkImageItem extends ImageItem implements Imageable, ZkComponent, Itemable {
	private String _id;
	private String _image;
	private ZkDesktop _zk;
	private ZkForm _form;
	
	public ZkImageItem(ZkDesktop zk, String id, String label, String image, int layout, String altText,
	int appearanceMode) {
		super(label, null, layout, altText, appearanceMode);
		_id = id;
		_zk = zk;
		_image = image;
	}
	
	//--Imageable--//
	public void loadImage(Image image) {
		setImage(image);
	}
	
	public String getImageSrc() {
		return _image;
	}
	
	//--ZkComponent--//
	public String getId() {
		return _id;
	}
	
	public ZkDesktop getZkDesktop() {
		return _zk;
	}
	
	public ZkComponent getParent() {
		return (ZkComponent) getForm();
	}
	
	public void setParent(ZkComponent parent) {
		if (_form != parent) { //yes, !=, not !equals
			if (_form != null) {
				_form.removeItem(this);
			}
			_form = (ZkForm) parent;
			ZkDesktop newzk = null;
			if (_form != null) {
				_form.appendChild(this);
				newzk = _form.getZkDesktop();
			}
			if (_zk != newzk) {
				_zk = newzk;
			}
		}
	}

	public void setAttr(String attr, String val) {
		UiManager.setItemAttr(this, attr, val);

		if ("im".equals(attr)) {
			if (val != null) {
				UiManager.loadImageOnThread(this, _zk.getHostURL(), _zk.getPathURL(), val);
			} else {
				setImage(null); //clean to empty
			}
			_image = val;
		} else if ("tx".equals(attr)) {
			setAltText(val);
		}
	}

	//--Itemable--//
	public Form getForm() {
		return _form;
	}
}
