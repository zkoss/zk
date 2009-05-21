/* Fisheyebar.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 8, 2008 11:59:29 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkex.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.XulElement;

/** 
 * A fisheye bar is a bar of {@link Fisheye} that is a menu similar to the fish 
 * eye menu on the Mac OS.
 * 
 * <p>Default {@link #getZclass}: z-fisheyebar.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class Fisheyebar extends XulElement implements org.zkoss.zkex.zul.api.Fisheyebar {
	private int _itemwd = 50, _itemhgh = 50,
	_itemmaxwd = 200, _itemmaxhgh = 200, _itemPadding = 10;
	private String _orient = "horizontal", _attachEdge = "center",
		_labelEdge = "bottom";
	
	public Fisheyebar() {
	}
	
	/** Returns the item width of {@link Fisheye}.
	 */
	public int getItemWidth() {
		return _itemwd;
	}
	/** Sets the item width of {@link Fisheye}.
	 */
	public void setItemWidth(int itemwd) throws WrongValueException {
		if (itemwd <= 0)
			throw new WrongValueException("Positive is required: "+itemwd);
	
		if (_itemwd != itemwd) {
			_itemwd = itemwd;
			smartUpdate("itemWidth", itemwd);
		}
	}
	/** Returns the item height of {@link Fisheye}.
	 */
	public int getItemHeight() {
		return _itemhgh;
	}
	/** Sets the item height of {@link Fisheye}.
	 */
	public void setItemHeight(int itemhgh) throws WrongValueException {
		if (itemhgh <= 0)
			throw new WrongValueException("Positive is required: "+itemhgh);
	
		if (_itemhgh != itemhgh) {
			_itemhgh = itemhgh;
			smartUpdate("itemHeight", itemhgh);
		}
	}
	
	/** Returns the item maximal width of {@link Fisheye}.
	 */
	public int getItemMaxWidth() {
		return _itemmaxwd;
	}
	/** Sets the item maximal width of {@link Fisheye}.
	 */
	public void setItemMaxWidth(int itemmaxwd) throws WrongValueException {
		if (itemmaxwd <= 0)
			throw new WrongValueException("Positive is required: "+itemmaxwd);
	
		if (_itemmaxwd != itemmaxwd) {
			_itemmaxwd = itemmaxwd;
			smartUpdate("itemMaxWidth", itemmaxwd);
		}
	}
	/** Returns the item maximal height of {@link Fisheye}.
	 */
	public int getItemMaxHeight() {
		return _itemmaxhgh;
	}
	/** Sets the item maximal height of {@link Fisheye}.
	 */
	public void setItemMaxHeight(int itemmaxhgh) throws WrongValueException {
		if (itemmaxhgh <= 0)
			throw new WrongValueException("Positive is required: "+itemmaxhgh);
	
		if (_itemmaxhgh != itemmaxhgh) {
			_itemmaxhgh = itemmaxhgh;
			smartUpdate("itemMaxHeight", itemmaxhgh);
		}
	}
	
	/** Returns the item padding of {@link Fisheye}.
	 */
	public int getItemPadding() {
		return _itemPadding;
	}
	/** Sets the item padding of {@link Fisheye}.
	 */
	public void setItemPadding(int itemPadding) throws WrongValueException {
		if (itemPadding <= 0)
			throw new WrongValueException("Positive is required: "+itemPadding);
	
		if (_itemPadding != itemPadding) {
			_itemPadding = itemPadding;
			smartUpdate("itemPadding", itemPadding);
		}
	}
	
	/** Returns the orientation of {@link Fisheye}.
	 */
	public String getOrient() {
		return _orient;
	}
	/** Sets the orientation of {@link Fisheye}.
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException(orient);
	
		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			smartUpdate("orient", orient);
		}
	}
	/** Returns the attach edge.
	 * <p>Default: center
	 */
	public String getAttachEdge() {
		return _attachEdge;
	}
	/** Returns the attach edge.
	 */
	public void setAttachEdge(String attachEdge) throws WrongValueException {
		if (attachEdge == null || attachEdge.length() == 0)
			throw new WrongValueException("Empty attachEdge not allowed");
		if (!_attachEdge.equals(attachEdge)) {
			_attachEdge = attachEdge;
			smartUpdate("attachEdge", attachEdge);
		}
	}
	/** Returns the label edge.
	 * <p>Default: bottom
	 */
	public String getLabelEdge() {
		return _labelEdge;
	}
	/** Returns the label edge.
	 */
	public void setLabelEdge(String labelEdge) throws WrongValueException {
		if (labelEdge == null || labelEdge.length() == 0)
			throw new WrongValueException("Empty labelEdge not allowed");
		if (!_labelEdge.equals(labelEdge)) {
			_labelEdge = labelEdge;
			smartUpdate("labelEdge", labelEdge);
		}
	}

	public String getZclass() {
		return _zclass == null ? "z-fisheyebar" : _zclass;
	}
	//-- ComponentCtrl --//
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Fisheye))
			throw new UiException("Unsupported child for fisheyebar: "+child);
		super.beforeChildAdded(child, refChild);
	}
	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws IOException {
		super.renderProperties(renderer);
		if(_itemwd != 50)
			render(renderer, "itemWidth", new Integer(_itemwd));
		if(_itemhgh != 50)
			render(renderer, "itemHeight", new Integer(_itemhgh));
		if(_itemmaxwd != 200)
			render(renderer, "itemMaxWidth", new Integer(_itemmaxwd));
		if(_itemmaxhgh != 200)
			render(renderer, "itemMaxHeight", new Integer(_itemmaxhgh));
		if(_itemPadding != 10)
			render(renderer, "itemPadding", new Integer(_itemPadding));
		if(!_orient.equals("horizontal"))
			render(renderer, "orient", _orient);
		if(!_attachEdge.equals("center"))
			render(renderer, "attachEdge", _attachEdge);
		if(!_labelEdge.equals("bottom"))
			render(renderer, "labelEdge", _labelEdge);
		
	}
}
